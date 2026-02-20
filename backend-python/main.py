import traceback

from fastapi import Depends, FastAPI, Request
from fastapi.responses import JSONResponse
from Routes.ProductRoutes import ProductRouter
from database import db,get_db
from helpers.Utilities import Utils
from databaseSchemas.CartSchema import Cart
from databaseSchemas.OrderSchema import Order
from databaseSchemas.ProductSchema import Product
from Routes.UserRoutes import UserRouter
from pymongo.errors import PyMongoError

app = FastAPI()
Tools = Utils()


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

app.include_router(UserRouter)
app.include_router(ProductRouter)
@app.get("/")
async def root():
    return {"status": "running"}