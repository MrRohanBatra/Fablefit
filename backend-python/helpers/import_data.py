import json
import asyncio
import sys
import os
from dotenv import load_dotenv
from tqdm import tqdm

# Adjust path if needed
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

# Import the db instance directly from your new database.py
from database import db
from databaseSchemas.ProductSchema import Product

load_dotenv()

BATCH_SIZE = 500

async def import_products():
    # 1. No need for connect_db() since db is initialized globally in database.py
    
    print("🧹 Clearing products...")
    # 2. Use Motor syntax to delete all documents in the 'products' collection
    await db["products"].delete_many({})

    print("📂 Loading JSON data...")
    with open("products_with_embeddings.json", "r", encoding="utf-8") as f:
        products_data = json.load(f)

    total = len(products_data)
    print(f"📦 Found {total} products to import.")

    for i in tqdm(range(0, total, BATCH_SIZE)):
        batch = products_data[i:i + BATCH_SIZE]
        docs = []

        for p_data in batch:
            # 3. Validate the raw data using your Pydantic model
            # Notice we completely removed the to_mongo_vector/Binary conversion!
            validated_product = Product(**p_data)
            
            # 4. Convert the Pydantic model to a standard dictionary for Motor
            doc = validated_product.model_dump()
            
            docs.append(doc)

        # 5. Insert the batch using Motor
        if docs:
            await db["products"].insert_many(docs)

    print("🎉 Import finished")

if __name__ == "__main__":
    asyncio.run(import_products())