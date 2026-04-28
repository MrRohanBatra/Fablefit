from typing import List, Optional, Literal
from pydantic import BaseModel, Field
from datetime import datetime, timedelta, timezone
from beanie import Document, PydanticObjectId


class OrderItem(BaseModel):
    product: PydanticObjectId
    size: str
    color: Optional[str] = None
    quantity: int = Field(..., ge=1)
    price: float


class Order(Document):
    userId: str

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

    # Tracks which status-change notifications have already been sent.
    # Values will be one of: "placed", "shipped", "out-for-delivery", "delivered"
    # Appended to as the scheduler (and the place-order route) fire each notification,
    # so no duplicate pushes are ever sent.
    notified_statuses: List[str] = Field(default_factory=list)

    createdAt: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))
    updatedAt: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))

    class Settings:
        name = "orders"
