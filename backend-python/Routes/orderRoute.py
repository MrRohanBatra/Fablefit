import sys
import os
from datetime import datetime, timezone
from typing import List

from fastapi import APIRouter, HTTPException
from databaseSchemas.OrderSchema import Order
from databaseSchemas.CartSchema import Cart
from helpers.Utilities import Utils

# Setup path for internal imports
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

orderRouter = APIRouter(prefix="/orders")
Tools = Utils()

def compute_status(order: Order) -> str:
    """
    Logic: Determines status based on days passed since createdAt.
    Matches Node.js computeStatus exactly.
    """
    created_at = order.createdAt
    now = datetime.now(timezone.utc)

    # Convert to local dates (removing time) for day difference calculation
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
    """
    Place a new order. 
    Tip: You should also clear the user's cart here.
    """
    try:
        await order_data.insert()
        # Logic to clear cart: await Cart.find_one(Cart.uid == order_data.userId).delete()
        return {"message": "Order placed successfully", "orderId": str(order_data.id)}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@orderRouter.get("/getall")
async def get_all_orders():
    """Admin route to see all orders"""
    orders = await Order.find_all().to_list()
    return [Tools.serializeDoc(o.model_dump(by_alias=True)) for o in orders]

@orderRouter.get("/user/{uid}")
async def get_user_orders(uid: str):
    """
    Fetch all orders for a specific user.
    Matches Node.js GET /user/:uid logic.
    """
    try:
        print(f"\n📥 Fetching orders for UID: {uid}")

        # Fetch orders sorted by createdAt descending
        orders = await Order.find(Order.userId == uid).sort("-createdAt").to_list()

        print(f"📦 Orders found: {len(orders)}")

        updated_orders = []
        for order in orders:
            computed_status = compute_status(order)
            
            # Merge computed status into the document
            order_dict = order.model_dump(by_alias=True)
            order_dict["status"] = computed_status
            
            print(f"📝 FINAL STATUS returned: {computed_status}")
            updated_orders.append(Tools.serializeDoc(order_dict))

        return updated_orders

    except Exception as e:
        print(f"❌ Order Fetch Error: {e}")
        raise HTTPException(status_code=500, detail="Failed to load orders")