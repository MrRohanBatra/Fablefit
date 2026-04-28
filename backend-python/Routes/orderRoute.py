import sys
import os
from datetime import datetime, timezone
from typing import List

from fastapi import APIRouter, HTTPException
from databaseSchemas.OrderSchema import Order
from databaseSchemas.CartSchema import Cart
from databaseSchemas.UserSchema import User, compute_tier
from helpers.Utilities import Utils

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

orderRouter = APIRouter(prefix="/orders",tags=["Orders"])
Tools = Utils()


def compute_status(order: Order) -> str:
    created_at = order.createdAt
    now = datetime.now(timezone.utc)

    start = datetime(created_at.year, created_at.month, created_at.day)
    end = datetime(now.year, now.month, now.day)
    diff_days = (end - start).days + 1

    if diff_days <= 1:
        return "placed"
    elif diff_days <= 4:
        return "shipped"
    elif diff_days <= 7:
        return "out-for-delivery"
    else:
        return "delivered"


@orderRouter.post("/place")
async def place_order(order_data: Order):
    try:
        await order_data.insert()

        # --- Update user loyalty ---
        user = await User.find_one(User.uid == order_data.userId)
        new_tier = "Bronze"

        if user:
            previous_tier = user.tier
            user.total_spent += order_data.totalPrice
            user.tier = compute_tier(user.total_spent)
            user.updatedAt = datetime.now(timezone.utc)
            await user.save()
            new_tier = user.tier

            if user.tier != previous_tier:
                print(f"🎉 User {order_data.userId} upgraded from {previous_tier} → {user.tier}!")
            else:
                print(f"✅ User {order_data.userId} remains {user.tier} (total spent: ₹{user.total_spent:.2f})")
        else:
            print(f"⚠️  User {order_data.userId} not found in DB — skipping tier update")

        return {
            "message": "Order placed successfully",
            "orderId": str(order_data.id),
            "tier": new_tier,
        }

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@orderRouter.get("/getall")
async def get_all_orders():
    orders = await Order.find_all().to_list()
    return [Tools.serializeDoc(o.model_dump(by_alias=True)) for o in orders]


@orderRouter.get("/user/{uid}")
async def get_user_orders(uid: str):
    try:
        print(f"\n📥 Fetching orders for UID: {uid}")
        orders = await Order.find(Order.userId == uid).sort("-createdAt").to_list()
        print(f"📦 Orders found: {len(orders)}")

        updated_orders = []
        for order in orders:
            computed_status = compute_status(order)
            order_dict = order.model_dump(by_alias=True)
            order_dict["status"] = computed_status
            print(f"📝 FINAL STATUS returned: {computed_status}")
            updated_orders.append(Tools.serializeDoc(order_dict))

        return updated_orders

    except Exception as e:
        print(f"❌ Order Fetch Error: {e}")
        raise HTTPException(status_code=500, detail="Failed to load orders")
