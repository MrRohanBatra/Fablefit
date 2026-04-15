import os
from datetime import datetime, timezone, timedelta

from apscheduler.schedulers.asyncio import AsyncIOScheduler

# Imported inside functions to avoid circular imports at module load time
# (Beanie models need the app to be initialised first)

# ── Tuneable constants ─────────────────────────────────────────────────────────
ABANDONED_AFTER_DAYS      = int(os.getenv("ABANDONED_CART_DAYS", "3"))
ABANDONED_REMIND_COOLDOWN = int(os.getenv("ABANDONED_REMIND_COOLDOWN_DAYS", "7"))
PRICE_DROP_CHECK_HOURS    = int(os.getenv("PRICE_DROP_CHECK_HOURS", "6"))

scheduler = AsyncIOScheduler(timezone="UTC")


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

    carts = await Cart.find_all().to_list()
    notified = 0

    for cart in carts:
        # Skip empty carts
        if not cart.items:
            continue

        # Cart must not have been touched recently
        if cart.updatedAt > stale_cutoff:
            continue

        # Don't re-notify within the cooldown window
        if cart.abandoned_notified_at and cart.abandoned_notified_at > cool_cutoff:
            continue

        user = await User.find_one(User.uid == cart.uid)
        if not user or not user.fcm_token:
            continue

        # Personalise with the first item's product name
        first_item = cart.items[0]
        product = await Product.get(str(first_item.product))
        product_name = product.name if product else "your item"

        # Simple deterministic discount code per user
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

    items     = await WishlistItem.find_all().to_list()
    notified  = 0

    for item in items:
        product = await Product.get(item.product_id)
        if not product:
            continue

        # Only fire if price actually dropped
        if product.price >= item.price_at_add:
            continue

        drop_pct = int(((item.price_at_add - product.price) / item.price_at_add) * 100)

        user = await User.find_one(User.uid == item.uid)

        if user and user.fcm_token:
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
                notified += 1

        # Update snapshot so we don't fire again for the same drop level
        item.price_at_add = product.price
        await item.save()

    print(f"✅ [Scheduler] Price drop check done — notified {notified} user(s)")


# ── Lifecycle helpers called from main.py ─────────────────────────────────────

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
    scheduler.start()
    print(
        f"✅ Scheduler started — "
        f"abandoned cart check every 24 h, "
        f"price drop check every {PRICE_DROP_CHECK_HOURS} h"
    )


def stop_scheduler():
    if scheduler.running:
        scheduler.shutdown(wait=False)
        print("🛑 Scheduler stopped")
