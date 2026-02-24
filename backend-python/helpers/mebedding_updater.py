import asyncio
from beanie import init_beanie
from tqdm import tqdm
from databaseSchemas.ProductSchema import Product
from helpers.ClipService import ClipService # Ensure this matches your actual import
from database import db

async def update_product_embeddings():
    await init_beanie(
        database=db,
        document_models=[Product],
    )
    print("✅ Beanie initialized in updater")
    
    clip_service = ClipService()
    
    # 1. Get the total count of documents to set up the progress bar
    total_products = await Product.count()
    
    print(f"Found {total_products} products to update.")
    
    # 2. Create the tqdm progress bar manually
    with tqdm(total=total_products, desc="Processing Products") as pbar:
        # 3. Iterate asynchronously through the Beanie cursor
        async for p in Product.find():
            
            # 4. Call the correctly named method
            embedding = clip_service.generate_embedding(p)
            
            p.embedding = embedding
            await p.save()
            
            # 5. Advance the progress bar
            pbar.update(1)

# To run it:
if __name__ == "__main__":
    asyncio.run(update_product_embeddings())
