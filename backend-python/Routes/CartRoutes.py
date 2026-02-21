import sys
import os
from typing import List, Optional
from datetime import datetime, timezone

from fastapi import APIRouter, HTTPException
from databaseSchemas.CartSchema import Cart, CartItem
from databaseSchemas.ProductSchema import Product
from helpers.Utilities import Utils

# Setup path for internal imports
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

cartRouter = APIRouter(prefix="/cart")
Tools = Utils()

async def calculate_total(items: List[CartItem]) -> float:
    """Helper to calculate total price by fetching current product prices"""
    total = 0.0
    for item in items:
        # PydanticObjectId handles the conversion from string automatically
        prod = await Product.get(str(item.product))
        if prod:
            total += prod.price * item.quantity
    return total

@cartRouter.get("/getall")
async def get_all_carts():
    """Get all carts (Matches Node.js getall)"""
    carts = await Cart.find_all().to_list()
    return [Tools.serializeDoc(c.model_dump(by_alias=True)) for c in carts]

@cartRouter.get("/{uid}")
async def get_cart(uid: str):
    """Get or create user's cart (Matches Node.js getCart)"""
    print(f"📦 [GET CART] uid={uid}")
    
    cart = await Cart.find_one(Cart.uid == uid)
    if not cart:
        print(f"🆕 Creating new empty cart for uid={uid}")
        cart = Cart(uid=uid, items=[], totalPrice=0)
        await cart.insert()
    
    print(f"✅ Cart fetched ({len(cart.items)} items)")
    return Tools.serializeDoc(cart.model_dump(by_alias=True))

@cartRouter.post("/add")
async def add_to_cart(payload: dict):
    """Add or update item in cart (Matches Node.js addToCart)"""
    try:
        uid = payload.get("uid")
        product_id = payload.get("productId")
        size = payload.get("size")
        color = payload.get("color")
        quantity = payload.get("quantity", 1)

        print(f"🟢 [ADD] uid={uid}, product={product_id}, qty={quantity}")

        # 1. Validate Product
        product = await Product.get(str(product_id))
        if not product:
            raise HTTPException(status_code=404, detail="Product not found")

        # 2. Get or Create Cart
        cart = await Cart.find_one(Cart.uid == uid)
        if not cart:
            print(f"🆕 Creating new cart for {uid}")
            cart = Cart(uid=uid, items=[], totalPrice=0)
            await cart.insert()

        # 3. Check for existing item with same Product + Size + Color
        existing_item = next(
            (i for i in cart.items if str(i.product) == str(product_id) 
             and i.size == size and i.color == color), 
            None
        )

        if existing_item:
            existing_item.quantity += quantity
        else:
            new_item = CartItem(product=product_id, size=size, color=color, quantity=quantity) # type: ignore
            cart.items.append(new_item)

        # 4. Recalculate and Save
        cart.totalPrice = await calculate_total(cart.items)
        cart.updatedAt = datetime.now(timezone.utc)
        await cart.save()

        print(f"✅ Cart updated: {len(cart.items)} items")
        return {"message": "Cart updated", "cart": Tools.serializeDoc(cart.model_dump(by_alias=True))}

    except Exception as e:
        print(f"❌ [ADD CART ERROR] {e}")
        raise HTTPException(status_code=500, detail=str(e))

@cartRouter.post("/remove")
async def remove_from_cart(payload: dict):
    """Remove an item from cart (Matches Node.js removeFromCart)"""
    uid = payload.get("uid")
    product_id = payload.get("productId")
    size = payload.get("size")
    color = payload.get("color")

    print(f"🔴 [REMOVE] uid={uid}, product={product_id}")

    cart = await Cart.find_one(Cart.uid == uid)
    if not cart:
        raise HTTPException(status_code=404, detail="Cart not found")

    # Filter out the matching item
    cart.items = [
        i for i in cart.items 
        if not (str(i.product) == str(product_id) and i.size == size and i.color == color)
    ]

    cart.totalPrice = await calculate_total(cart.items)
    cart.updatedAt = datetime.now(timezone.utc)
    await cart.save()

    print(f"✅ Item removed, total={cart.totalPrice}")
    return {"message": "Item removed", "cart": Tools.serializeDoc(cart.model_dump(by_alias=True))}

@cartRouter.post("/update")
async def update_cart_quantity(payload: dict):
    """Update item quantity (Matches Node.js updateCartQuantity)"""
    uid = payload.get("uid")
    product_id = payload.get("productId")
    size = payload.get("size")
    color = payload.get("color")
    quantity = payload.get("quantity")

    print(f"🟠 [UPDATE] uid={uid}, product={product_id}, qty={quantity}")

    cart = await Cart.find_one(Cart.uid == uid)
    if not cart:
        raise HTTPException(status_code=404, detail="Cart not found")

    item = next(
        (i for i in cart.items if str(i.product) == str(product_id) 
         and i.size == size and i.color == color), 
        None
    )

    if not item:
        raise HTTPException(status_code=404, detail="Item not found in cart")

    item.quantity = quantity # type: ignore
    cart.totalPrice = await calculate_total(cart.items)
    cart.updatedAt = datetime.now(timezone.utc)
    await cart.save()

    print("✅ Quantity updated")
    return {"message": "Quantity updated", "cart": Tools.serializeDoc(cart.model_dump(by_alias=True))}