import sys
import os
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from fastapi import APIRouter, HTTPException
from databaseSchemas.WishlistSchema import WishlistItem, WishlistToggleRequest, WishlistToggleResponse
from databaseSchemas.ProductSchema import Product

wishlistRouter = APIRouter(prefix="/wishlist")


@wishlistRouter.post("/toggle", response_model=WishlistToggleResponse)
async def toggle_wishlist(payload: WishlistToggleRequest):
    """
    Idempotent toggle — adds the item if absent, removes it if present.
    The Android client calls this on heart-button tap without needing to know
    the current state first.
    """
    existing = await WishlistItem.find_one(
        WishlistItem.uid == payload.uid,
        WishlistItem.product_id == payload.product_id,
    )

    if existing:
        await existing.delete()
        return WishlistToggleResponse(
            action="removed",
            message="Removed from wishlist",
            wishlisted=False,
        )

    # Validate product exists before adding
    product = await Product.get(payload.product_id)
    if not product:
        raise HTTPException(status_code=404, detail="Product not found")

    new_item = WishlistItem(
        uid=payload.uid,
        product_id=payload.product_id,
        price_at_add=payload.price_at_add,
    )
    await new_item.insert()

    return WishlistToggleResponse(
        action="added",
        message="Added to wishlist",
        wishlisted=True,
    )


@wishlistRouter.get("/{uid}", response_model=list[WishlistItem])
async def get_wishlist(uid: str):
    """Return all wishlist items for a user."""
    return await WishlistItem.find(WishlistItem.uid == uid).to_list()


@wishlistRouter.get("/check/{uid}/{product_id}")
async def check_wishlisted(uid: str, product_id: str):
    """Quick boolean check — avoids fetching the full wishlist just to show the heart state."""
    exists = await WishlistItem.find_one(
        WishlistItem.uid == uid,
        WishlistItem.product_id == product_id,
    )
    return {"wishlisted": exists is not None}
