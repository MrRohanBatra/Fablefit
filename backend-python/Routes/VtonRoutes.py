import httpx
from typing import Optional
from pydantic import BaseModel
from fastapi import APIRouter, UploadFile, File, Form, HTTPException
from fastapi.responses import JSONResponse, StreamingResponse
from dotenv import load_dotenv
import os
load_dotenv()
router = APIRouter(prefix="/vton")

# --- Response Models ---

class TryonResponse(BaseModel):
    task_id: str
    message: str
    position_in_queue: int

class StatusResponse(BaseModel):
    task_id: str
    status: str
    result_path: Optional[str] = None
    error: Optional[str] = None

# --- Configuration ---
FLASK_URL = os.getenv("VTON_URL","http://localhost:8000")
client = httpx.AsyncClient(timeout=120.0) # Increased timeout for heavy VTON processing

@router.post("/tryon", response_model=TryonResponse)
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