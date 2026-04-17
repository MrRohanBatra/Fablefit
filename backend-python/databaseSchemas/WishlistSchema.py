from typing import Optional
from pydantic import BaseModel, Field
from datetime import datetime, timezone
from beanie import Document


class WishlistItem(Document):
    """One row per (user, product) pair. Price snapshot enables drop detection."""
    uid: str
    product_id: str

    # Price of the product at the moment the user wishlisted it.
    # The scheduler compares current product price against this value.
    price_at_add: float

    added_at: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))

    class Settings:
        name = "wishlist"


# ── Request / Response models ──────────────────────────────────────────────────

class WishlistToggleRequest(BaseModel):
    uid: str
    product_id: str
    price_at_add: float


class WishlistToggleResponse(BaseModel):
    action: str          # "added" | "removed"
    message: str
    wishlisted: bool
