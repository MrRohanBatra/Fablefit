import io
import random
import shutil
from typing import Any
import uuid
from bson.errors import InvalidId
from fastapi import APIRouter, Depends, File, HTTPException, UploadFile
import sys, os
from PIL import Image
from fastapi.concurrency import run_in_threadpool

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from databaseSchemas.ProductSchema import Product
from helpers.Utilities import Utils
from helpers.ClipService import ClipServiceModel

BASE_IMAGE_DIR = "images"

Tools = Utils()

ProductRouter = APIRouter(prefix="/products")

@ProductRouter.post("/add")
async def add_product(product: Product):
    # 1. Generate embedding
    
    embedding = ClipServiceModel.generate_embedding(product)
    
    product.embedding = embedding.get("text_emb")
    product.image_embedding = embedding.get("image_emb")
    
    # 2. Save using Beanie
    await product.insert()
    
    # 3. Convert back to dict with '_id' to match old PyMongo output exactly
    saved_dict = product.model_dump(by_alias=True)
    
    return {
        "success": True,
        "message": "Product added successfully",
        "product": Tools.serializeDoc(saved_dict)
    }

@ProductRouter.get("/id/{id}")
async def get_product_by_id(id: str):
    print("Product id", id)
    try:
        product = await Product.get(id)
    except Exception: # Catches invalid hex strings exactly like the old cod
        raise HTTPException(status_code=400, detail="Invalid product id")
        
    if not product:
        raise HTTPException(status_code=404, detail="Product not found")
        
    # Convert back to dict so serializeDoc works perfectly
    product_dict = product.model_dump(by_alias=True)
    return Tools.serializeDoc(product_dict)


@ProductRouter.get("/search", response_model=list[Product])
async def vector_search_products(s: str, limit: int = 20):
    query = s.strip()
    if not query:
        raise HTTPException(status_code=400, detail="Missing search query")

    gender = Tools.detect_gender(query)
    expanded_query = Tools.expand_query(query)
    query_embedding = await run_in_threadpool(
        ClipServiceModel.generate_text_embedding,
        expanded_query
    )
    
    match_filter: dict[str, Any] = {"stock": {"$gt": 0}}

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

    # Use Beanie's aggregate method directly on the class
    # gate(pipeline).to_list()
    collection = Product.get_pymongo_collection()
    cursor = collection.aggregate(pipeline)
    results = await cursor.to_list() # type: ignore
    print(f'Semantic searched "{query}" → {len(results)} results')

    random.shuffle(results)
    return results

@ProductRouter.post("/search/images/", response_model=list[Product])
async def search_images(file: UploadFile = File(...)):
    image_file_bytes=await file.read()
    image_file=Image.open(io.BytesIO(image_file_bytes)).convert("RGB")
    filename_dir=os.path.join(BASE_IMAGE_DIR, "101") # search uploads at  101
    os.makedirs(filename_dir, exist_ok=True)
    filename=os.path.join(filename_dir,f"{uuid.uuid4()}.png")
    image_file.save(filename)
    embeddings=await run_in_threadpool(
        ClipServiceModel.generate_image_embedding,
        filename,
    )
    match_filter: dict[str, Any] = {"stock": {"$gt": 0}}
    pipeline = [
        {
            "$vectorSearch": {
                "index": "vector_index_images",
                "path": "image_embedding",
                "queryVector": embeddings,
                "numCandidates": 200,
                "limit": 20,
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
    collection = Product.get_pymongo_collection()
    cursor = collection.aggregate(pipeline)
    results = await cursor.to_list()  # type: ignore
    print(f'Semantic searched image → {len(results)} results')

    random.shuffle(results)
    return results


@ProductRouter.post("/upload")
async def upload_image(file: UploadFile = File(...)):
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