import random
import shutil
from typing import Any
import uuid

from bson import ObjectId
from fastapi import APIRouter,Depends, File, HTTPException, UploadFile
import sys,os
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from databaseSchemas.ProductSchema import Product
from database import db,get_db
from helpers.Utilities import Utils
from helpers.ClipService import ClipServiceModel
Tools=Utils()

ProductRouter=APIRouter(prefix="/products")

@ProductRouter.post("/add")
async def add_product(product: Product, database=Depends(get_db)):
    products = database["products"]
    product_dict = product.model_dump()
    embedding = ClipServiceModel.generate_embedding(product)
    product_dict["embedding"] = embedding
    result = await products.insert_one(product_dict)
    saved = await products.find_one({"_id": result.inserted_id})
    return {
        "success": True,
        "message": "Product added successfully",
        "product": Tools.serializeDoc(saved)
    }

@ProductRouter.get("/id/{id}")
async def get_product_by_id(id: str, database=Depends(get_db)):
    products = database["products"]
    print("Product id", id)
    try:
        product = await products.find_one({"_id": ObjectId(id)})
    except:
        raise HTTPException(status_code=400, detail="Invalid product id")
    if not product:
        raise HTTPException(status_code=404, detail="Product not found")
    product = Tools.serializeDoc(product)
    return product


@ProductRouter.get("/search")
async def vector_search_products(s: str, limit: int = 20, database=Depends(get_db)):

    query = s.strip()
    if not query:
        raise HTTPException(status_code=400, detail="Missing search query")

    collection = database["products"]
    gender = Tools.detect_gender(query)

    expanded_query = Tools.expand_query(query)

    query_embedding = ClipServiceModel.generate_text_embedding(expanded_query)
    match_filter:dict[str, Any] = {"stock": {"$gt": 0}}

    if gender:
        match_filter["category"] = {"$regex": gender, "$options": "i"}

    pipeline = [
        {
            "$vectorSearch": {
                "index": "vector_index",
                "path": "embedding",
                "queryVector": query_embedding,
                "numCandidates": 200,
                "limit": limit,
            }
        },
        {"$match": match_filter},
        {
            "$project": {
                "_id": {"$toString": "$_id"},
                "name": 1,
                "description": 1,
                "category": 1,
                "price": 1,
                "color": 1,
                "sizes": 1,
                "stock": 1,
                "companyName": 1,
                "images": 1,
                "vton_category": 1,
                "score": {"$meta": "vectorSearchScore"}
            }
        }
    ]

    cursor = collection.aggregate(pipeline)
    results = await cursor.to_list(length=limit)

    print(f'Semantic searched "{query}" → {len(results)} results')

    random.shuffle(results)
    return results


@ProductRouter.post("/upload")
async def upload_image(file: UploadFile = File(...)):

    BASE_IMAGE_DIR = "images"
    PRODUCT_IMAGE_DIR = os.path.join(BASE_IMAGE_DIR, "100")

    # Create folder if not exists
    os.makedirs(PRODUCT_IMAGE_DIR, exist_ok=True)

    if not file.content_type.startswith("image/"): # type: ignore
        raise HTTPException(status_code=400, detail="Only image files allowed")

    extension = file.filename.split(".")[-1] # type: ignore
    filename = f"{uuid.uuid4()}.{extension}"

    file_path = os.path.join(PRODUCT_IMAGE_DIR, filename)

    with open(file_path, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)

    return {
        "success": True,
        "message": "Image uploaded successfully",
        "url": f"/images/100/{filename}"
    }