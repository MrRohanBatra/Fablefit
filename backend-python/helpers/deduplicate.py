import asyncio
import sys
import os

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from database import db

async def deduplicate():
    collection = db["products"]

    print("Fetching all products...")
    cursor = collection.find({}, {"_id": 1, "description": 1, "name": 1, "color": 1})
    products = await cursor.to_list(length=None)
    print(f"Total: {len(products)}")

    seen_descriptions = {}
    ids_to_delete = []

    for p in products:
        desc = p.get("description", "").strip()
        pid = p["_id"]

        if desc in seen_descriptions:
            ids_to_delete.append(pid)
        else:
            seen_descriptions[desc] = pid

    print(f"\n🗑️  Duplicates to remove: {len(ids_to_delete)}")
    print(f"✅ Unique products to keep: {len(seen_descriptions)}")

    confirm = input("\nProceed with deletion? (yes/no): ")
    if confirm.lower() != "yes":
        print("Aborted.")
        return

    # Delete in batches of 1000
    batch_size = 1000
    deleted = 0
    for i in range(0, len(ids_to_delete), batch_size):
        batch = ids_to_delete[i:i+batch_size]
        result = await collection.delete_many({"_id": {"$in": batch}})
        deleted += result.deleted_count
        print(f"Deleted {deleted}/{len(ids_to_delete)}...")

    print(f"\n✅ Done! Removed {deleted} duplicates. {len(seen_descriptions)} unique products remain.")

asyncio.run(deduplicate())