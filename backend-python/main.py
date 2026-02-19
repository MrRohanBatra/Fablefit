from fastapi import Depends, FastAPI
from database import db
from helpers.Utilities import Utils
from databaseSchemas.CartSchema import Cart
from databaseSchemas.OrderSchema import Order
from databaseSchemas.ProductSchema import Product
from Routes.UserRoutes import UserRouter
app = FastAPI()
Tools = Utils()


async def get_db():
    return db
app.include_router(UserRouter)

@app.get("/")
async def root():
    return {"status": "running"}