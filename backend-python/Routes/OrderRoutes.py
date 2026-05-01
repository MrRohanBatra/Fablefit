from fastapi import APIRouter, HTTPException
from datetime import datetime, timezone
from typing import List
from pydantic import BaseModel

from databaseSchemas.OrderSchema import Order, OrderItem
from databaseSchemas.CartSchema import Cart
from databaseSchemas.ProductSchema import Product
from helpers.NotificationService import send_push
from databaseSchemas.UserSchema import User

orderRouter = APIRouter(prefix="/orders", tags=["Orders"])


# ── Response models ────────────────────────────────────────────────────────────

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
    address: str
    items: List[OrderItemSimple]


class OrderListResponse(BaseModel):
    count: int
    orders: List[OrderSimpleResponse]


class OrderPlaceRequest(BaseModel):
    user_id: str
    address: str


# ── Helpers ────────────────────────────────────────────────────────────────────

async def _send_order_notification(user_id: str, order_id: str, status: str, title: str, body: str) -> bool:
    """
    Look up the user's FCM token and fire a push notification.
    Returns True if the push was accepted, False otherwise.
    """
    user = await User.find_one(User.uid == user_id)
    if not user or not user.fcm_token:
        print(f"⚠️  No FCM token for uid={user_id} — skipping '{status}' notification")
        return False

    return send_push(
        token=user.fcm_token,
        title=title,
        body=body,
        data={
            "type":     "order_status",
            "order_id": order_id,
            "status":   status,
        },
    )


# ── 1. Place order (COD) ───────────────────────────────────────────────────────

@orderRouter.post("/place", response_model=OrderPlaceResponse)
async def place_order(data: OrderPlaceRequest):
    user_id = data.user_id
    address = data.address

    cart = await Cart.find_one(Cart.uid == user_id)
    if not cart or len(cart.items) == 0:
        raise HTTPException(status_code=400, detail="Cart is empty")

    order_items = []
    total_price = 0

    for item in cart.items:
        product = await Product.get(item.product)
        if not product:
            continue

        order_items.append(
            OrderItem(
                product=item.product,
                size=item.size,
                color=item.color,
                quantity=item.quantity,
                price=product.price,
            )
        )
        total_price += product.price * item.quantity

    if not order_items:
        raise HTTPException(status_code=400, detail="No valid items")
    now=datetime.now(timezone.utc)
    order = Order(
        userId=user_id,
        items=order_items,
        totalPrice=total_price,
        address=address,
        paymentMethod="cod",
        status="placed",
        isPaid=False,
        # Mark "placed" as already notified so the scheduler never double-sends it.
        notified_statuses=["placed"],
        createdAt=now,
        updatedAt=now,
    )

    await order.insert()

    # ── Send "order placed" notification immediately ───────────────────────────
    order_id_str = str(order.id)
    sent = await _send_order_notification(
        user_id=user_id,
        order_id=order_id_str,
        status="placed",
        title="Order placed! 🛒",
        body="We've received your order and are getting it ready.",
    )
    if not sent:
        # Non-fatal — the scheduler will retry on its next run if needed,
        # but since "placed" is already in notified_statuses it won't re-send.
        print(f"⚠️  Could not send 'placed' notification for order={order_id_str}")

    # ── Clear the cart ─────────────────────────────────────────────────────────
    cart.items = []
    cart.totalPrice = 0
    await cart.save()

    return {"message": "Order placed", "order_id": order_id_str}


# ── 2. Track order ─────────────────────────────────────────────────────────────

@orderRouter.get("/track/{order_id}", response_model=OrderTrackResponse)
async def track_order(order_id: str):
    order = await Order.get(order_id)
    if not order:
        raise HTTPException(status_code=404, detail="Order not found")

    return {
        "order_id":      str(order.id),
        "status":        order.status,
        "delivery_date": order.deliveryDate,
        "total":         order.totalPrice,
    }


# ── 3. Cancel order ────────────────────────────────────────────────────────────

@orderRouter.put("/cancel/{order_id}", response_model=OrderCancelResponse)
async def cancel_order(order_id: str):
    order = await Order.get(order_id)
    if not order:
        raise HTTPException(status_code=404, detail="Order not found")

    if order.status in ["shipped", "out-for-delivery", "delivered"]:
        raise HTTPException(status_code=400, detail="Cannot cancel now")

    order.status    = "cancelled"
    order.updatedAt = datetime.now(timezone.utc)
    await order.save()

    # ── Send cancellation notification ─────────────────────────────────────────
    await _send_order_notification(
        user_id=order.userId,
        order_id=order_id,
        status="cancelled",
        title="Order cancelled ❌",
        body="Your order has been cancelled successfully.",
    )

    return {"message": "Order cancelled", "status": order.status}


# ── 4. Get user orders ─────────────────────────────────────────────────────────

@orderRouter.get("/{user_id}", response_model=OrderListResponse)
async def get_user_orders(user_id: str):
    orders = await Order.find(Order.userId == user_id).to_list()

    return {
        "count": len(orders),
        "orders": [
            {
                "order_id":      str(o.id),
                "status":        o.status,
                "total":         o.totalPrice,
                "delivery_date": o.deliveryDate,
                "address":       o.address,
                "items": [
                    {
                        "product":  str(i.product),
                        "quantity": i.quantity,
                        "price":    i.price,
                    }
                    for i in o.items
                ],
            }
            for o in orders
        ],
    }
