import os
import traceback
import platform
import socket
from contextlib import asynccontextmanager

from beanie import init_beanie
from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import FileResponse, JSONResponse
from pymongo.errors import PyMongoError


from Routes.chatRoute import chat_router
from Routes.CartRoutes import cartRouter
from Routes.ProductRoutes import ProductRouter
from Routes.UiRouter import uiRouter
from Routes.UserRoutes import UserRouter
# from Routes.orderRoute import orderRouter
from Routes.VtonRoutes import vtonRouter
from Routes.WishlistRoutes import wishlistRouter          # ← NEW
from Routes.NotificationTesterRoutes import noti_test_router
from helpers.Scheduler import start_scheduler, stop_scheduler  # ← NEW
from Routes.OrderRoutes import orderRouter
from database import db
from databaseSchemas.CartSchema import Cart
from databaseSchemas.OrderSchema import Order
from databaseSchemas.ProductSchema import Product
from databaseSchemas.UserSchema import User
from databaseSchemas.WishlistSchema import WishlistItem  # ← NEW
from helpers.Utilities import Utils


@asynccontextmanager
async def lifespan(app: FastAPI):
    # 1. Initialise Beanie (includes new WishlistItem model)
    await init_beanie(
        database=db,  # type: ignore
        document_models=[Product, User, Cart, Order, WishlistItem],
    )
    print("✅ Beanie initialised")

    # 2. Start background scheduler
    start_scheduler()

    yield

    # 3. Graceful shutdown
    stop_scheduler()


app = FastAPI(lifespan=lifespan)
Tools = Utils()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["GET", "POST", "PUT", "DELETE"],
    allow_headers=["*"],
)


# ── Exception handlers ─────────────────────────────────────────────────────────

@app.exception_handler(PyMongoError)
async def mongo_exception_handler(request: Request, exc: PyMongoError):
    print("🔥 Mongo Error:", exc)
    return JSONResponse(status_code=500, content={"success": False, "message": "Database error"})


@app.exception_handler(Exception)
async def global_exception_handler(request: Request, exc: Exception):
    print("🔥 GLOBAL ERROR:", exc)
    traceback.print_exc()
    return JSONResponse(status_code=500, content={"success": False, "message": "Internal server error"})


# ── Routers ────────────────────────────────────────────────────────────────────

os.makedirs("images", exist_ok=True)

app.include_router(UserRouter,     prefix="/api")
app.include_router(ProductRouter,  prefix="/api")
app.include_router(cartRouter,     prefix="/api")
# app.include_router(orderRouter,    prefix="/api")
app.include_router(uiRouter,       prefix="/api")
app.include_router(vtonRouter,         prefix="/api")
app.include_router(wishlistRouter, prefix="/api")  # ← NEW
app.include_router(noti_test_router,prefix="/api")
app.include_router(orderRouter,prefix="/api")
if(os.getenv("ENABLE_AGENT")=="true"):
    app.include_router(chat_router,prefix="/api")

# ── Static image serving ───────────────────────────────────────────────────────

CACHE_TIME    = 31536000
DEFAULT_IMAGE = os.path.join(os.getcwd(), "default.png")


@app.get("/images/{path:path}")
async def get_image(path: str):
    image_path = os.path.join("images", path)
    if os.path.exists(image_path):
        return FileResponse(image_path)
    return FileResponse(DEFAULT_IMAGE)


@app.middleware("http")
async def add_cache_headers(request: Request, call_next):
    response = await call_next(request)
    if request.url.path.startswith("/images"):
        response.headers["Cache-Control"] = f"public, max-age={CACHE_TIME}, immutable"
    return response


@app.get("/")
async def root():
    return {
        "status":         "running",
        "os":             platform.system(),
        "os_version":     platform.version(),
        "platform":       platform.platform(),
        "architecture":   platform.machine(),
        "hostname":       socket.gethostname(),
        "python_version": platform.python_version(),
        "cpu_count":      os.cpu_count(),
    }
