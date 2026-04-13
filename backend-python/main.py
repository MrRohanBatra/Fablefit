import os
import traceback
from contextlib import asynccontextmanager  # 🔹 1. Import this for the startup event

from beanie import init_beanie  # 🔹 2. Import Beanie initialization
from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import FileResponse, JSONResponse
from pymongo.errors import PyMongoError


from Routes.CartRoutes import cartRouter
from Routes.ProductRoutes import ProductRouter
from Routes.UiRouter import uiRouter
from Routes.UserRoutes import UserRouter
from Routes.orderRoute import orderRouter
from Routes.VtonRoutes import router
from Routes.ChatRoute import chat_router
from database import db  
from databaseSchemas.CartSchema import Cart
from databaseSchemas.OrderSchema import Order
from databaseSchemas.ProductSchema import Product
from databaseSchemas.UserSchema import User
from helpers.Utilities import Utils
import platform
import socket
@asynccontextmanager
async def lifespan(app: FastAPI):
    # ADD THIS PRINT STATEMENT

    await init_beanie(
        database=db,  # type: ignore
        document_models=[Product,User,Cart,Order]
    )
    print("✅ Beanie initialized successfully")
    yield

app = FastAPI(lifespan=lifespan)
Tools = Utils()
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"], # Matches your origin: "*"
    allow_credentials=True,
    allow_methods=["GET", "POST", "PUT", "DELETE"],
    allow_headers=["*"],
)

@app.exception_handler(PyMongoError)
async def mongo_exception_handler(request: Request, exc: PyMongoError):
    print("🔥 Mongo Error:", exc)

    return JSONResponse(
        status_code=500,
        content={
            "success": False,
            "message": "Database error",
        },
    )

@app.exception_handler(Exception)
async def global_exception_handler(request: Request, exc: Exception):
    print("🔥 GLOBAL ERROR:", exc)
    traceback.print_exc()

    return JSONResponse(
        status_code=500,
        content={
            "success": False,
            "message": "Internal server error",
        },
    )
os.makedirs("images", exist_ok=True)
# app.mount("/images", StaticFiles(directory="images"), name="images")
app.include_router(UserRouter,prefix="/api")
app.include_router(ProductRouter,prefix="/api")
app.include_router(cartRouter,prefix="/api")
app.include_router(orderRouter,prefix="/api")
app.include_router(uiRouter,prefix="/api")
app.include_router(router,prefix="/api")
app.include_router(chat_router,prefix="/api")
CACHE_TIME = 31536000
# Matches: app.use("/images", express.static(...))
# app.mount(
#     "/images", 
#     StaticFiles(directory="images", html=False), 
#     name="images"
# )
DEFAULT_IMAGE = os.path.join(os.getcwd(),"default.png")

@app.get("/images/{path:path}")
async def get_image(path: str):
    image_path = os.path.join("images", path)

    if os.path.exists(image_path):
        print(f"Serving image: {image_path}")
        return FileResponse(image_path)

    print(f"Image not found: {image_path}, serving default image")
    return FileResponse(DEFAULT_IMAGE)

@app.middleware("http")
async def add_cache_headers(request: Request, call_next):
    response = await call_next(request)
    
    # If the request is for an image, tell the browser to cache it heavily
    if request.url.path.startswith("/images") or request.url.path.startswith("/product_images"):
        response.headers["Cache-Control"] = f"public, max-age={CACHE_TIME}, immutable"
    
    return response
@app.get("/")
async def root():
    return {
        "status": "running",
        "os": platform.system(),
        "os_version": platform.version(),
        "platform": platform.platform(),
        "architecture": platform.machine(),
        "hostname": socket.gethostname(),
        "python_version": platform.python_version(),
        "cpu_count": os.cpu_count()
    }