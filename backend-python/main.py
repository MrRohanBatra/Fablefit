import os
import traceback
from contextlib import asynccontextmanager  # 🔹 1. Import this for the startup event

from fastapi import Depends, FastAPI, Request
from fastapi.responses import JSONResponse
from beanie import init_beanie              # 🔹 2. Import Beanie initialization
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
from Routes.ProductRoutes import ProductRouter
from database import db, get_db             # We can use your existing 'db' variable
from databaseSchemas.UserSchema import User
from helpers.Utilities import Utils
from databaseSchemas.CartSchema import Cart
from databaseSchemas.OrderSchema import Order
from databaseSchemas.ProductSchema import Product
from Routes.UserRoutes import UserRouter
from pymongo.errors import PyMongoError
from Routes.CartRoutes import cartRouter
from Routes.orderRoute import orderRouter
# 🔹 3. Create the startup event
@asynccontextmanager
async def lifespan(app: FastAPI):
    # ADD THIS PRINT STATEMENT

    await init_beanie(
        database=db, 
        document_models=[Product,User,Cart,Order]
    )
    print("✅ Beanie initialized successfully")
    yield
# 🔹 4. Pass the lifespan to the FastAPI app
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
app.mount("/images", StaticFiles(directory="images"), name="images")
app.include_router(UserRouter)
app.include_router(ProductRouter)
app.include_router(cartRouter)
app.include_router(orderRouter)
CACHE_TIME = 31536000

# Matches: app.use("/images", express.static(...))
app.mount(
    "/images", 
    StaticFiles(directory="images", html=False), 
    name="images"
)

# To add the "Heavy Caching" headers, we override the default response
@app.middleware("http")
async def add_cache_headers(request: Request, call_next):
    response = await call_next(request)
    
    # If the request is for an image, tell the browser to cache it heavily
    if request.url.path.startswith("/images") or request.url.path.startswith("/product_images"):
        response.headers["Cache-Control"] = f"public, max-age={CACHE_TIME}, immutable"
    
    return response
@app.get("/")
async def root():
    return {"status": "running"}