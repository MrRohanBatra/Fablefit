from fastapi import Depends, FastAPI
from database import db
app = FastAPI()

async def get_db():
    return db


@app.get("/")
async def root():
    return {"status": "running"}

from helpers.search import search_products

@app.get("/search")
async def search(s: str, db=Depends(get_db)):
    results = await search_products(db, s, limit=20)
    return results