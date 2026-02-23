import sys
import os
import asyncio
from tqdm import tqdm
from motor.motor_asyncio import AsyncIOMotorClient
from beanie import init_beanie
from dotenv import load_dotenv

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from databaseSchemas.ProductSchema import Product
from helpers.ClipService import ClipServiceModel

load_dotenv()


# -----------------------------
# INIT DATABASE
# -----------------------------
async def init_db():
    client = AsyncIOMotorClient(os.getenv("MONGODB_URI"))
    db = client["fablefit"] # type: ignore

    await init_beanie(
        database=db, # type: ignore
        document_models=[Product],
    )


# -----------------------------
# REINDEX JOB
# -----------------------------
async def main():
    print("🔌 Connecting to Mongo...")
    await init_db()

    clip = ClipServiceModel

    batch_size = clip.get_optimal_batch_size()
    print(f"🧠 Batch size: {batch_size}")

    total = await Product.count()
    print(f"📦 Products: {total}")

    cursor = Product.find_all()

    batch = []

    pbar = tqdm(total=total)

    async for product in cursor:
        batch.append(product)

        if len(batch) >= batch_size:
            await clip._process_batch(batch)
            pbar.update(len(batch))
            batch = []

    if batch:
        await clip._process_batch(batch)
        pbar.update(len(batch))

    pbar.close()

    print("🎉 Reindex finished")


if __name__ == "__main__":
    asyncio.run(main())