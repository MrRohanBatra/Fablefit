from typing import List, Optional, Literal
from pydantic import BaseModel, Field
from datetime import datetime, timedelta, timezone


# 🔹 Order Item Model
class OrderItem(BaseModel):
    product: str  # ObjectId as string
    size: str
    color: Optional[str] = None
    quantity: int = Field(..., ge=1)
    price: float  # snapshot price


# 🔹 Order Model
class Order(BaseModel):
    userId: str  # Firebase UID (string)

    items: List[OrderItem] = Field(default_factory=list)

    totalPrice: float

    address: str

    paymentMethod: Literal["cod", "online"] = "cod"

    status: Literal[
        "placed",
        "shipped",
        "delivered",
        "cancelled",
        "out-for-delivery"
    ] = "placed"

    isPaid: bool = False

    paidAt: Optional[datetime] = None

    deliveryDate: datetime = Field(
        default_factory=lambda: datetime.now(timezone.utc) + timedelta(days=8)
    )

    createdAt: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))
    updatedAt: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))
