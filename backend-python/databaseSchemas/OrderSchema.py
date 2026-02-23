from typing import List, Optional, Literal
from pydantic import BaseModel, Field
from datetime import datetime, timedelta, timezone
from beanie import Document, PydanticObjectId

# 🔹 Order Item Model (Embedded)
class OrderItem(BaseModel):
    # Matches mongoose.Schema.Types.ObjectId
    product: PydanticObjectId 
    size: str
    color: Optional[str] = None
    quantity: int = Field(..., ge=1)
    price: float  # Snapshot price at the time of purchase

# 🔹 Order Model (Main Collection)
class Order(Document):
    # Firebase UID as a string
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

    # Default delivery date set to 8 days from now
    deliveryDate: datetime = Field(
        default_factory=lambda: datetime.now(timezone.utc) + timedelta(days=8)
    )

    createdAt: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))
    updatedAt: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))

    class Settings:
        name = "orders" # The MongoDB collection name