import asyncio
import sys
import os
from collections import Counter

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from database import db

async def inspect():
    collection = db["products"]
    
    # Fetch all products (excluding embedding to keep it fast)
    cursor = collection.find({}, {"embedding": 0})
    products = await cursor.to_list(length=None)
    
    print(f"\n📦 Total products: {len(products)}\n")

    # --- Name distribution ---
    name_counts = Counter(p.get("name") for p in products)
    print("🏷️  Name distribution:")
    for name, count in name_counts.most_common():
        print(f"   {name}: {count}")

    # --- Color distribution ---
    color_counts = Counter(p.get("color", "unknown") for p in products)
    print("\n🎨 Color distribution:")
    for color, count in color_counts.most_common():
        print(f"   {color}: {count}")

    # --- Category distribution ---
    cat_counts = Counter(p.get("category") for p in products)
    print("\n📂 Category distribution:")
    for cat, count in cat_counts.most_common():
        print(f"   {cat}: {count}")

    # --- Company distribution ---
    company_counts = Counter(p.get("companyName") for p in products)
    print("\n🏢 Company distribution:")
    for company, count in company_counts.most_common():
        print(f"   {company}: {count}")

    # --- True duplicates: same name + color + companyName ---
    print("\n🔍 Checking for true duplicates (name + color + company)...")
    seen = Counter()
    for p in products:
        key = (p.get("name"), p.get("color"), p.get("companyName"))
        seen[key] += 1

    dupes = {k: v for k, v in seen.items() if v > 1}
    if dupes:
        print(f"   Found {len(dupes)} duplicate groups:")
        for (name, color, company), count in sorted(dupes.items(), key=lambda x: -x[1])[:20]:
            print(f"   [{count}x] {name} | {color} | {company}")
    else:
        print("   ✅ No exact duplicates found!")

    # --- Sample a few products to see full data ---
    print("\n👀 Sample of 3 products (full data):")
    for p in products[:3]:
        p.pop("_id", None)
        for k, v in p.items():
            if k == "images":
                print(f"   images: {len(v)} image(s)")
            else:
                print(f"   {k}: {v}")
        print("   ---")

asyncio.run(inspect())