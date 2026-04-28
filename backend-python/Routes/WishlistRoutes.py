import sys
import os
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from fastapi import APIRouter, HTTPException
from databaseSchemas.WishlistSchema import WishlistItem, WishlistToggleRequest, WishlistToggleResponse
from databaseSchemas.ProductSchema import Product
from helpers.Utilities import Utils

wishlistRouter = APIRouter(prefix="/wishlist",tags=["Whishlist"])
Tools = Utils()


def serialize_item(item: WishlistItem) -> dict:
    """Serialize consistently so Android gets _id as a string."""
    return Tools.serializeDoc(item.model_dump(by_alias=True))


@wishlistRouter.post("/toggle", response_model=WishlistToggleResponse)
async def toggle_wishlist(payload: WishlistToggleRequest):
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


@wishlistRouter.get("/{uid}")
async def get_wishlist(uid: str):
    """Return all wishlist items for a user, serialized with _id as string."""
    items = await WishlistItem.find(WishlistItem.uid == uid).to_list()
    return [serialize_item(i) for i in items]


@wishlistRouter.get("/check/{uid}/{product_id}")
async def check_wishlisted(uid: str, product_id: str):
    exists = await WishlistItem.find_one(
        WishlistItem.uid == uid,
        WishlistItem.product_id == product_id,
    )
    return {"wishlisted": exists is not None}
