from typing import List, Optional
from pydantic import BaseModel, Field
from datetime import datetime, timezone
from beanie import Document, Indexed

# 🔹 Cart Item (Embedded)
class CartItem(BaseModel):
    product: str # You can also use PydanticObjectId here for stricter validation
    size: str
    color: Optional[str] = None
    quantity: int = Field(default=1, ge=1)

# 🔹 Cart (Main Collection)
class Cart(Document): # 🚀 CHANGE THIS FROM BaseModel TO Document
    uid: str # Adding index here makes lookups much faster

    items: List[CartItem] = Field(default_factory=list)
    totalPrice: float = 0.0

    createdAt: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))
    updatedAt: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))

    class Settings:
        name = "carts" # MongoDB collection name