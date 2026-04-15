import sys
import os
from typing import List, Optional
from datetime import datetime, timezone

from fastapi import APIRouter, HTTPException
from databaseSchemas.CartSchema import Cart, CartItem, CartUpdate
from databaseSchemas.ProductSchema import Product
from databaseSchemas.UserSchema import User, TIER_DISCOUNTS
from helpers.Utilities import Utils

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

cartRouter = APIRouter(prefix="/cart")
Tools = Utils()


# ----------------------------
# Helpers
# ----------------------------

async def calculate_raw_total(items: List[CartItem]) -> float:
    """Sum of (price × quantity) for all items — no discount applied."""
    total = 0.0
    for item in items:
        prod = await Product.get(str(item.product))
        if prod:
            total += prod.price * item.quantity
    return total


async def get_user_tier(uid: str) -> str:
    user = await User.find_one(User.uid == uid)
    return user.tier if user else "Bronze"


def apply_discount(raw_total: float, tier: str) -> float:
    discount = TIER_DISCOUNTS.get(tier, 0.0)
    return round(raw_total * (1.0 - discount), 2)


def serialize_cart_with_tier(cart: Cart, tier: str) -> dict:
    """Serialize the cart document and inject tier + discountPct into the dict."""
    cart_dict = Tools.serializeDoc(cart.model_dump(by_alias=True))
    cart_dict["tier"] = tier
    cart_dict["discountPct"] = TIER_DISCOUNTS.get(tier, 0.0)
    return cart_dict


# ----------------------------
# Routes
# ----------------------------

@cartRouter.get("/getall")
async def get_all_carts():
    carts = await Cart.find_all().to_list()
    return [Tools.serializeDoc(c.model_dump(by_alias=True)) for c in carts]


@cartRouter.get("/{uid}")
async def get_cart(uid: str):
    """Get or create a user's cart, with tier discount reflected in totalPrice."""
    print(f"📦 [GET CART] uid={uid}")

    cart = await Cart.find_one(Cart.uid == uid)
    if not cart:
        print(f"🆕 Creating new empty cart for uid={uid}")
        cart = Cart(uid=uid, items=[], totalPrice=0)
        await cart.insert()

    tier = await get_user_tier(uid)

    # Recalculate on every fetch so the price is always fresh
    raw_total = await calculate_raw_total(cart.items)
    cart.totalPrice = apply_discount(raw_total, tier)
    await cart.save()

    print(f"✅ Cart fetched ({len(cart.items)} items), tier={tier}, total=₹{cart.totalPrice}")
    return serialize_cart_with_tier(cart, tier)


@cartRouter.post("/add")
async def add_to_cart(payload: CartUpdate):
    try:
        uid = payload.uid
        product_id = payload.product
        size = payload.size
        color = payload.color
        quantity = payload.quantity

        print(f"🟢 [ADD] uid={uid}, product={product_id}, qty={quantity}")

        product = await Product.get(str(product_id))
        if not product:
            raise HTTPException(status_code=404, detail="Product not found")

        cart = await Cart.find_one(Cart.uid == uid)
        if not cart:
            print(f"🆕 Creating new cart for {uid}")
            cart = Cart(uid=uid, items=[], totalPrice=0)
            await cart.insert()

        existing_item = next(
            (i for i in cart.items
             if str(i.product) == str(product_id) and i.size == size and i.color == color),
            None
        )

        if existing_item:
            existing_item.quantity += quantity
        else:
            cart.items.append(CartItem(product=product_id, size=size, color=color, quantity=quantity))  # type: ignore

        tier = await get_user_tier(uid)
        raw_total = await calculate_raw_total(cart.items)
        cart.totalPrice = apply_discount(raw_total, tier)
        cart.updatedAt = datetime.now(timezone.utc)
        await cart.save()

        print(f"✅ Cart updated: {len(cart.items)} items, tier={tier}, total=₹{cart.totalPrice}")
        return {"message": "Cart updated", "cart": serialize_cart_with_tier(cart, tier)}

    except Exception as e:
        print(f"❌ [ADD CART ERROR] {e}")
        raise HTTPException(status_code=500, detail=str(e))


@cartRouter.post("/remove")
async def remove_from_cart(payload: CartUpdate):
    uid = payload.uid
    product_id = payload.product
    size = payload.size
    color = payload.color

    print(f"🔴 [REMOVE] uid={uid}, product={product_id}")

    cart = await Cart.find_one(Cart.uid == uid)
    if not cart:
        raise HTTPException(status_code=404, detail="Cart not found")

    cart.items = [
        i for i in cart.items
        if not (str(i.product) == str(product_id) and i.size == size and i.color == color)
    ]

    tier = await get_user_tier(uid)
    raw_total = await calculate_raw_total(cart.items)
    cart.totalPrice = apply_discount(raw_total, tier)
    cart.updatedAt = datetime.now(timezone.utc)
    await cart.save()

    print(f"✅ Item removed, tier={tier}, total=₹{cart.totalPrice}")
    return {"message": "Item removed", "cart": serialize_cart_with_tier(cart, tier)}


@cartRouter.post("/update")
async def update_cart_quantity(payload: CartUpdate):
    uid = payload.uid
    product_id = payload.product
    size = payload.size
    color = payload.color
    quantity = payload.quantity

    print(f"🟠 [UPDATE] uid={uid}, product={product_id}, qty={quantity}")

    cart = await Cart.find_one(Cart.uid == uid)
    if not cart:
        raise HTTPException(status_code=404, detail="Cart not found")

    item = next(
        (i for i in cart.items
         if str(i.product) == str(product_id) and i.size == size and i.color == color),
        None
    )
    if not item:
        raise HTTPException(status_code=404, detail="Item not found in cart")

    item.quantity = quantity  # type: ignore

    tier = await get_user_tier(uid)
    raw_total = await calculate_raw_total(cart.items)
    cart.totalPrice = apply_discount(raw_total, tier)
    cart.updatedAt = datetime.now(timezone.utc)
    await cart.save()

    print(f"✅ Quantity updated, tier={tier}, total=₹{cart.totalPrice}")
    return {"message": "Quantity updated", "cart": serialize_cart_with_tier(cart, tier)}
