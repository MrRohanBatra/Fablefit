import httpx
from typing import Optional
from pydantic import BaseModel
from fastapi import APIRouter, UploadFile, File, Form, HTTPException,status
from fastapi.responses import JSONResponse, StreamingResponse
from dotenv import load_dotenv
import os
import sys

from databaseSchemas.ProductSchema import Product
from databaseSchemas.UserSchema import User
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

load_dotenv()
BASE_URL=os.getenv("BASE_URL","http://127.0.0.1:1607")
router = APIRouter(prefix="/vton")

def getNetworkUrl(partial_url:str)->str:
    if(partial_url.startswith("/")):
        return f"{BASE_URL}/{partial_url[1::]}"
    else:
        return f"{BASE_URL}/{partial_url}"    



# --- Response Models ---

class TryonResponse(BaseModel):
    task_id: str
    message: str
    position_in_queue: int


class TryonPathRequest(BaseModel):
    human_image_path: str
    garment_image_path: str
    description: str = ""
    denoise_steps: int = 20
    seed: int = 42
    category: str = "upper_body"

class ModernTryOn(BaseModel):
    product_id:str
    uid:str

class StatusResponse(BaseModel):
    task_id: str
    status: str
    result_path: Optional[str] = None
    error: Optional[str] = None

# --- Configuration ---
FLASK_URL = os.getenv("VTON_URL","http://localhost:8000")
client = httpx.AsyncClient(timeout=120.0) 

@router.post("/tryonold", response_model=TryonResponse)
async def forward_tryon(
    human_image: UploadFile = File(...),
    garment_image: UploadFile = File(...),
    description: str = Form(""),
    denoise_steps: int = Form(20),
    seed: int = Form(42),
    category: str = Form("upper_body")
):
    files = {
        "human_image": (human_image.filename, await human_image.read(), human_image.content_type),
        "garment_image": (garment_image.filename, await garment_image.read(), garment_image.content_type),
    }
    data = {
        "description": description,
        "denoise_steps": str(denoise_steps),
        "seed": str(seed),
        "category": category
    }

    try:
        response = await client.post(f"{FLASK_URL}/tryon", data=data, files=files)
        response.raise_for_status()
        return response.json()
    except httpx.HTTPStatusError as e:
        # Pass through the exact error from Flask if it fails validation
        return JSONResponse(status_code=e.response.status_code, content=e.response.json())
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Proxy Error: {str(e)}")


@router.post("/tryonbypath", response_model=TryonResponse)
async def forward_tryon_by_path(request: TryonPathRequest):
    """
    Forwards local file paths to the Flask VTON API.
    Reduces network overhead by reading from local storage.
    """
    # 1. Validate that files exist before trying to send them
    if not os.path.exists(request.human_image_path):
        raise HTTPException(status_code=404, detail=f"Human image not found at {request.human_image_path}")
    if not os.path.exists(request.garment_image_path):
        raise HTTPException(status_code=404, detail=f"Garment image not found at {request.garment_image_path}")

    try:
        # 2. Open files in binary mode
        # We use a 'with' block or manual close to ensure file handles are released
        with open(request.human_image_path, "rb") as h_file, \
                open(request.garment_image_path, "rb") as g_file:

            files = {
                "human_image": (os.path.basename(request.human_image_path), h_file, "image/png"),
                "garment_image": (os.path.basename(request.garment_image_path), g_file, "image/png"),
            }

            data = {
                "description": request.description,
                "denoise_steps": str(request.denoise_steps),
                "seed": str(request.seed),
                "category": request.category
            }

            # 3. Forward to Flask
            response = await client.post(f"{FLASK_URL}/tryon", data=data, files=files)
            response.raise_for_status()
            return response.json()

    except httpx.HTTPStatusError as e:
        return JSONResponse(status_code=e.response.status_code, content=e.response.json())
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Internal Server Error: {str(e)}")
    

@router.post("/tryon",response_model=TryonResponse)
async def tryon_function(request:ModernTryOn):
    garment=await Product.get(request.product_id)
    if garment is None:
        raise HTTPException(404,f"product {request.product_id} not found")
    human=await User.find_one({"uid":request.uid})
    if human is None:
        raise HTTPException(404,f"user {request.uid} not found")
    garment_image=garment.images[0]
    human_image=human.vton_image
    human_url=getNetworkUrl(human_image)
    garment_url=getNetworkUrl(garment_image)
    
    
    human_response=await client.get(human_url)
    if human_response.status_code!=200:
        raise HTTPException(404, "Human image not found")
    garment_response=await client.get(garment_url)
    if garment_response.status_code!=200:
        raise HTTPException(404, "Garment image not found")
    human_type = human_response.headers.get("content-type", "image/jpeg")
    garment_type = garment_response.headers.get("content-type", "image/jpeg")
    files = {
        "human_image": ("human", human_response.content, human_type),
        "garment_image": ("garment", garment_response.content, garment_type),
    }
    data={
        "category":garment.vton_category,
        "denoise_steps":20,
        "seed":42,
        "description":garment.name,
    }
    final_response=await client.post(f"{FLASK_URL}/tryon",data=data,files=files)
    final_response.raise_for_status()
    return final_response.json()

@router.get("/status/{task_id}", response_model=StatusResponse)
async def forward_status(task_id: str):
    try:
        response = await client.get(f"{FLASK_URL}/status/{task_id}")
        response.raise_for_status()
        return response.json()
    except httpx.HTTPStatusError as e:
        return JSONResponse(status_code=e.response.status_code, content=e.response.json())

@router.get("/result/{task_id}")
async def forward_result(task_id: str):
    # No response_model here because it returns a binary stream (image)
    req = client.build_request("GET", f"{FLASK_URL}/result/{task_id}")
    response = await client.send(req, stream=True)
    
    if response.status_code != 200:
        # Must await the error body before returning
        error_data = await response.aread()
        import json
        return JSONResponse(status_code=response.status_code, content=json.loads(error_data))

    return StreamingResponse(
        response.aiter_bytes(),
        media_type="image/png"
    )
class HealthCheckResponse(BaseModel):
    status: str
    flask_connected: bool
    message: str

# --- Routes ---

@router.get("/check", response_model=HealthCheckResponse)
async def check_vton_availability():
    """
    Checks if the Flask VTON service is up and reachable.
    """
    try:
        # Pinging the Flask root "/" route
        response = await client.get(f"{FLASK_URL}/", timeout=5.0)
        
        if response.status_code == 200:
            return {
                "status": "online",
                "flask_connected": True,
                "message": "VTON Service is ready to process jobs."
            }
        else:
            return JSONResponse(
                status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
                content={
                    "status": "error",
                    "flask_connected": False,
                    "message": f"Flask returned status code {response.status_code}"
                }
            )
    except httpx.RequestError:
        raise HTTPException(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE, 
            detail="VTON Flask server is unreachable. Is it running on port 8000?"
        )