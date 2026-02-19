from typing import List, Optional
from pydantic import BaseModel, Field
from datetime import datetime, timezone


# 🔹 Cart Item
class CartItem(BaseModel):
    product: str            # Mongo ObjectId stored as string
    size: str
    color: Optional[str] = None
    quantity: int = Field(default=1, ge=1)


# 🔹 Cart
class Cart(BaseModel):
    uid: str                # Firebase UID

    items: List[CartItem] = Field(default_factory=list)

    totalPrice: float = 0

    createdAt: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))
    updatedAt: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))
