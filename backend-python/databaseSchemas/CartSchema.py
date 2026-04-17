from typing import List, Optional
from pydantic import BaseModel, Field
from datetime import datetime, timezone
from beanie import Document, Indexed


class CartItem(BaseModel):
    product: str
    size: str
    color: Optional[str] = None
    quantity: int = Field(default=1, ge=1)


class Cart(Document):
    uid: str

    items: List[CartItem] = Field(default_factory=list)
    totalPrice: float = 0.0

    # Tracks the last time we sent an abandoned-cart notification for this cart.
    # Prevents spamming the user if they ignore the nudge.
    abandoned_notified_at: Optional[datetime] = None

    createdAt: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))
    updatedAt: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))

    class Settings:
        name = "carts"


class CartUpdate(BaseModel):
    uid: str
    product: str
    size: str
    color: Optional[str] = None
    quantity: int
