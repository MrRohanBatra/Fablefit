import os
from pprint import pprint
import sys
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from beanie import init_beanie
from databaseSchemas.ProductSchema import Product
import asyncio
from database import db

async def test():
    await init_beanie(
        database=db, 
        document_models=[Product]
    )
    print("✅ Beanie initialized successfully in test")
    
    result = await Product.find(Product.embedding == [0.0]*512).to_list()
    print(f"Found {len(result)} products with zero embeddings.")
        
if __name__ == "__main__":
    asyncio.run(test())