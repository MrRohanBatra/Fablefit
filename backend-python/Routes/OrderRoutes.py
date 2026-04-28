from fastapi import APIRouter, HTTPException
from datetime import datetime, timezone
from typing import List
from pydantic import BaseModel

from databaseSchemas.OrderSchema import Order, OrderItem
from databaseSchemas.CartSchema import Cart
from databaseSchemas.ProductSchema import Product

router = APIRouter(prefix="/orders", tags=["Orders"])


# -------------------------------
# 📦 RESPONSE MODELS (Simple)
# -------------------------------

class OrderPlaceResponse(BaseModel):
    message: str
    order_id: str


class OrderTrackResponse(BaseModel):
    order_id: str
    status: str
    delivery_date: datetime
    total: float


class OrderCancelResponse(BaseModel):
    message: str
    status: str


class OrderItemSimple(BaseModel):
    product: str
    quantity: int
    price: float


class OrderSimpleResponse(BaseModel):
    order_id: str
    status: str
    total: float
    delivery_date: datetime
    address:str
    items: List[OrderItemSimple]


class OrderListResponse(BaseModel):
    count: int
    orders: List[OrderSimpleResponse]

class OrderPlaceRequest(BaseModel):
    user_id: str
    address: str
    
# -------------------------------
# 🧾 1. PLACE ORDER (COD ONLY)
# -------------------------------
@router.post("/place", response_model=OrderPlaceResponse)
async def place_order(data:OrderPlaceRequest):
    user_id=data.user_id
    address=data.address
    cart = await Cart.find_one(Cart.uid == user_id)

    if not cart or len(cart.items) == 0:
        raise HTTPException(status_code=400, detail="Cart is empty")

    order_items = []
    total_price = 0

    for item in cart.items:
        product = await Product.get(item.product)

        if not product:
            continue

        price = product.price

        order_items.append(
            OrderItem(
                product=item.product,
                size=item.size,
                color=item.color,
                quantity=item.quantity,
                price=price
            )
        )

        total_price += price * item.quantity

    if len(order_items) == 0:
        raise HTTPException(status_code=400, detail="No valid items")

    order = Order(
        userId=user_id,
        items=order_items,
        totalPrice=total_price,
        address=address,
        paymentMethod="cod",
        status="placed",
        isPaid=False
    )

    await order.insert()

    # 🧹 Clear cart
    cart.items = []
    cart.totalPrice = 0
    await cart.save()

    return {
        "message": "Order placed",
        "order_id": str(order.id)
    }


# -------------------------------
# 🔍 2. TRACK ORDER
# -------------------------------
@router.get("/track/{order_id}", response_model=OrderTrackResponse)
async def track_order(order_id: str):
    order = await Order.get(order_id)

    if not order:
        raise HTTPException(status_code=404, detail="Order not found")

    return {
        "order_id": str(order.id),
        "status": order.status,
        "delivery_date": order.deliveryDate,
        "total": order.totalPrice
    }


# -------------------------------
# ❌ 3. CANCEL ORDER
# -------------------------------
@router.put("/cancel/{order_id}", response_model=OrderCancelResponse)
async def cancel_order(order_id: str):
    order = await Order.get(order_id)

    if not order:
        raise HTTPException(status_code=404, detail="Order not found")

    # ❗ Restrict cancellation
    if order.status in ["shipped", "out-for-delivery", "delivered"]:
        raise HTTPException(status_code=400, detail="Cannot cancel now")

    order.status = "cancelled"
    order.updatedAt = datetime.now(timezone.utc)

    await order.save()

    return {
        "message": "Order cancelled",
        "status": order.status
    }


# -------------------------------
# 📦 4. GET USER ORDERS
# -------------------------------
@router.get("/{user_id}", response_model=OrderListResponse)
async def get_user_orders(user_id: str):
    orders = await Order.find(Order.userId == user_id).to_list()

    return {
        "count": len(orders),
        "orders": [
            {
                "order_id": str(o.id),
                "status": o.status,
                "total": o.totalPrice,
                "delivery_date": o.deliveryDate,
                "address":o.address,
                "items": [
                    {
                        "product": str(i.product),
                        "quantity": i.quantity,
                        "price": i.price
                    }
                    for i in o.items
                ]
            }
            for o in orders
        ]
    }