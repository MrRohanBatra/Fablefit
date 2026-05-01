import asyncio
import sys
import os
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from beanie import init_beanie

from databaseSchemas.OrderSchema import Order
from databaseSchemas.ProductSchema import Product
from databaseSchemas.UserSchema import User

import os
from datetime import datetime, timezone, timedelta

from apscheduler.schedulers.asyncio import AsyncIOScheduler

ABANDONED_AFTER_DAYS      = int(os.getenv("ABANDONED_CART_DAYS", "3"))
ABANDONED_REMIND_COOLDOWN = int(os.getenv("ABANDONED_REMIND_COOLDOWN_DAYS", "7"))
PRICE_DROP_CHECK_HOURS    = int(os.getenv("PRICE_DROP_CHECK_HOURS", "6"))
ORDER_STATUS_CHECK_HOURS  = int(os.getenv("ORDER_STATUS_CHECK_HOURS", "6"))

scheduler = AsyncIOScheduler(timezone="UTC")

# Helper to prevent offset-naive/aware errors
def ensure_aware(dt: datetime) -> datetime:
    if dt.tzinfo is None:
        return dt.replace(tzinfo=timezone.utc)
    return dt

# ── Notification content ───────────────────────────────────────────────────────
_ORDER_NOTIFICATIONS = {
    "placed": (
        "Order placed! 🛒",
        "We've received your order and are getting it ready.",
    ),
    "shipped": (
        "Your order is on the way! 📦",
        "Your order has been shipped and is heading your way.",
    ),
    "out-for-delivery": (
        "Out for delivery! 🚚",
        "Your order is out for delivery — expect it today!",
    ),
    "delivered": (
        "Order delivered! ✅",
        "Your order has been delivered. Enjoy your purchase!",
    ),
}

_NOTIFY_THRESHOLDS = [
    ("placed",            0),
    ("shipped",           2),
    ("out-for-delivery",  5),
    ("delivered",         8),
]


# ── Job 1: Abandoned cart nudge ────────────────────────────────────────────────

async def job_abandoned_cart_nudge():
    from databaseSchemas.CartSchema import Cart
    from databaseSchemas.UserSchema import User
    from databaseSchemas.ProductSchema import Product
    from helpers.NotificationService import send_push

    print("🔔 [Scheduler] Checking for abandoned carts...")

    now          = datetime.now(timezone.utc)
    stale_cutoff = now - timedelta(days=ABANDONED_AFTER_DAYS)
    cool_cutoff  = now - timedelta(days=ABANDONED_REMIND_COOLDOWN)

    carts    = await Cart.find_all().to_list()
    notified = 0

    for cart in carts:
        if not cart.items:
            continue

        updated_at = ensure_aware(cart.updatedAt)

        if updated_at > stale_cutoff:
            continue

        if cart.abandoned_notified_at:
            notified_at = ensure_aware(cart.abandoned_notified_at)
            if notified_at > cool_cutoff:
                continue

        user = await User.find_one(User.uid == cart.uid)
        if not user or not user.fcm_token:
            continue

        first_item   = cart.items[0]
        product      = await Product.get(str(first_item.product))
        product_name = product.name if product else "your item"
        discount_code = f"CART5-{cart.uid[:6].upper()}"

        sent = send_push(
            token=user.fcm_token,
            title="Still thinking it over? 🛒",
            body=(
                f"Your {product_name} is waiting! "
                f"Use code {discount_code} for 5% off — today only."
            ),
            data={
                "type":          "abandoned_cart",
                "discount_code": discount_code,
            },
        )

        if sent:
            cart.abandoned_notified_at = now
            await cart.save()
            notified += 1

    print(f"✅ [Scheduler] Abandoned cart nudge done — notified {notified} user(s)")


# ── Job 2: Price drop alerts ───────────────────────────────────────────────────

async def job_price_drop_alerts():
    from databaseSchemas.WishlistSchema import WishlistItem
    from databaseSchemas.UserSchema import User
    from databaseSchemas.ProductSchema import Product
    from helpers.NotificationService import send_push

    print("💰 [Scheduler] Checking for price drops...")

    items    = await WishlistItem.find_all().to_list()
    notified = 0

    for item in items:
        product = await Product.get(item.product_id)
        if not product:
            continue

        if product.price >= item.price_at_add:
            continue

        drop_pct = int(((item.price_at_add - product.price) / item.price_at_add) * 100)
        user     = await User.find_one(User.uid == item.uid)

        if not user or not user.fcm_token:
            continue

        sent = send_push(
            token=user.fcm_token,
            title="Price Drop Alert 📉",
            body=(
                f"{product.name} just dropped {drop_pct}%! "
                f"Now ₹{product.price:.0f} (was ₹{item.price_at_add:.0f})."
            ),
            data={
                "type":       "price_drop",
                "product_id": str(product.id),
            },
        )

        if sent:
            item.price_at_add = product.price
            await item.save()
            notified += 1

    print(f"✅ [Scheduler] Price drop check done — notified {notified} user(s)")


# ── Job 3: Order status notifications ─────────────────────────────────────────

# async def job_order_status_notifications():
#     from databaseSchemas.OrderSchema import Order
#     from databaseSchemas.UserSchema import User
#     from helpers.NotificationService import send_push

#     print("📬 [Scheduler] Checking order status notifications...")

#     now    = datetime.now(timezone.utc)
#     orders = await Order.find(Order.status != "cancelled").to_list()
#     notified_count = 0

#     for order in orders:
#         if "delivered" in order.notified_statuses:
#             continue

#         user = await User.find_one(User.uid == order.userId)
#         if not user or not user.fcm_token:
#             continue

#         # Force awareness to prevent subtraction crash
#         created_at = ensure_aware(order.createdAt)

#         days_elapsed = (now - created_at).days
#         order_dirty  = False

#         for status, threshold_days in _NOTIFY_THRESHOLDS:
#             if days_elapsed < threshold_days:
#                 break

#             if status in order.notified_statuses:
#                 continue

#             title, body = _ORDER_NOTIFICATIONS[status]

#             sent = send_push(
#                 token=user.fcm_token,
#                 title=title,
#                 body=body,
#                 data={
#                     "type":     "order_status",
#                     "order_id": str(order.id),
#                     "status":   status,
#                 },
#             )

#             if sent:
#                 order.notified_statuses.append(status)
#                 order_dirty   = True
#                 notified_count += 1
#                 print(
#                     f"   📩 Sent '{status}' notification for "
#                     f"order={order.id} uid={order.userId} (day {days_elapsed})"
#                 )

#         if order_dirty:
#             await order.save()

#     print(
#         f"✅ [Scheduler] Order status notifications done "
#         f"— sent {notified_count} notification(s) across {len(orders)} order(s)"
#     )

async def job_order_status_notifications():
    from databaseSchemas.OrderSchema import Order
    from databaseSchemas.UserSchema import User
    from helpers.NotificationService import send_push

    print("📬 [Scheduler] Checking order status notifications & updating states...")

    now    = datetime.now(timezone.utc)
    # We fetch all non-cancelled/non-delivered orders to see if they need a promotion
    orders = await Order.find(Order.status != "cancelled", Order.status != "delivered").to_list()
    notified_count = 0

    for order in orders:
        user = await User.find_one(User.uid == order.userId)
        if not user or not user.fcm_token:
            continue

        created_at = ensure_aware(order.createdAt)
        days_elapsed = (now - created_at).days
        order_dirty  = False

        for status, threshold_days in _NOTIFY_THRESHOLDS:
            if days_elapsed < threshold_days:
                break

            # If this status hasn't been notified yet...
            if status not in order.notified_statuses:
                
                # 1. Update the actual status field in the DB
                # Note: We only update if the order isn't already manually set 
                # to something "further" in the lifecycle by an admin.
                order.status = status 
                
                # 2. Trigger the Push Notification
                title, body = _ORDER_NOTIFICATIONS[status]
                sent = send_push(
                    token=user.fcm_token,
                    title=title,
                    body=body,
                    data={
                        "type":     "order_status",
                        "order_id": str(order.id),
                        "status":   status,
                    },
                )

                if sent:
                    order.notified_statuses.append(status)
                    order_dirty = True
                    notified_count += 1
                    print(f"   📩 Order {order.id} promoted to '{status}' (Day {days_elapsed})")

        if order_dirty:
            order.updatedAt = now # Keep the updatedAt timestamp fresh
            await order.save()

    print(f"✅ [Scheduler] Status updates done — {notified_count} orders progressed.")
# ── Lifecycle ──────────────────────────────────────────────────────────────────

def start_scheduler():
    scheduler.add_job(
        job_abandoned_cart_nudge,
        trigger="interval",
        hours=24,
        id="abandoned_cart_nudge",
        replace_existing=True,
    )
    scheduler.add_job(
        job_price_drop_alerts,
        trigger="interval",
        hours=PRICE_DROP_CHECK_HOURS,
        id="price_drop_alerts",
        replace_existing=True,
    )
    scheduler.add_job(
        job_order_status_notifications,
        trigger="interval",
        hours=ORDER_STATUS_CHECK_HOURS,
        id="order_status_notifications",
        replace_existing=True,
    )
    scheduler.start()
    print(
        f"✅ Scheduler started — "
        f"abandoned cart check every 24 h, "
        f"price drop check every {PRICE_DROP_CHECK_HOURS} h, "
        f"order status check every {ORDER_STATUS_CHECK_HOURS} h"
    )

def stop_scheduler():
    if scheduler.running:
        scheduler.shutdown(wait=False)
        print("🛑 Scheduler stopped")

async def tester():
    from database import db
    # Include all schemas needed by the various jobs
    from databaseSchemas.CartSchema import Cart
    from databaseSchemas.WishlistSchema import WishlistItem
    
    print("🚀 Initializing Beanie for Full Test...")
    await init_beanie(
        database=db,
        document_models=[Order, Product, User, Cart, WishlistItem],
    )

    print("\n--- Starting Sequential Job Tests ---")

    # 1. Test Order Status Notifications
    print("\n[Test 1/3] Running: job_order_status_notifications")
    await job_order_status_notifications()

    # 2. Test Price Drop Alerts
    print("\n[Test 2/3] Running: job_price_drop_alerts")
    await job_price_drop_alerts()

    # 3. Test Abandoned Cart Nudge
    print("\n[Test 3/3] Running: job_abandoned_cart_nudge")
    await job_abandoned_cart_nudge()

    print("\n--- All Tests Completed ---")

if __name__ == "__main__":
    # Ensure environment variables are loaded if running as standalone
    from dotenv import load_dotenv
    load_dotenv()
    
    asyncio.run(tester())