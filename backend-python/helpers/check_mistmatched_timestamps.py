import asyncio
from datetime import timezone
import sys
import os
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from databaseSchemas.CartSchema import Cart
from databaseSchemas.ProductSchema import Product


from beanie import init_beanie
from databaseSchemas.OrderSchema import Order
from database import db


async def fix_order_datetimes(dry_run: bool = True):
    """
    dry_run=True  → only print mismatches
    dry_run=False → fix them in DB
    """

    await init_beanie(
        database=db,
        document_models=[Order],
    )

    print("🔍 Scanning orders for timezone issues...\n")

    total = await Order.count()
    print(f"📦 Total orders: {total}\n")

    naive_count = 0
    fixed_count = 0

    async for order in Order.find_all():
        created = order.createdAt

        # ❌ NAIVE datetime (problem)
        if created.tzinfo is None:
            naive_count += 1

            print(f"❌ NAIVE → order_id={order.id}")
            print(f"   createdAt: {created}\n")

            if not dry_run:
                # ✅ Fix: assume UTC
                order.createdAt = created.replace(tzinfo=timezone.utc)
                await order.save()
                fixed_count += 1

        # ✅ AWARE datetime (correct)
        else:
            # Optional debug (comment out if noisy)
            pass

    print("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
    print(f"❌ Naive timestamps found: {naive_count}")

    if dry_run:
        print("🧪 DRY RUN → no changes made")
    else:
        print(f"✅ Fixed timestamps: {fixed_count}")

    print("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")


if __name__ == "__main__":
    # 🔹 Step 1: run in dry mode first
    # asyncio.run(fix_order_datetimes(dry_run=True))

    # 🔹 Step 2: uncomment to actually fix
    asyncio.run(fix_order_datetimes(dry_run=False))