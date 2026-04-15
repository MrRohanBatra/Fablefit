from typing import List, Optional, Any, Literal
from pydantic import Field, BaseModel
from datetime import datetime, timezone
from beanie import Document


# ----------------------------
# Tier logic (single source of truth)
# ----------------------------
TIER_THRESHOLDS = {
    "Bronze": 0,
    "Silver": 1000,
    "Gold": 5000,
}

TIER_DISCOUNTS = {
    "Bronze": 0.0,
    "Silver": 0.05,   # 5%
    "Gold": 0.15,     # 15%
}

def compute_tier(total_spent: float) -> str:
    if total_spent >= TIER_THRESHOLDS["Gold"]:
        return "Gold"
    elif total_spent >= TIER_THRESHOLDS["Silver"]:
        return "Silver"
    return "Bronze"


class User(Document):
    uid: str

    phone: Optional[str] = None
    address: List[Any] = Field(default_factory=list)
    vton_image: Optional[str] = None
    type: Literal["normal", "seller"] = "normal"

    # Loyalty fields
    total_spent: float = 0.0
    tier: Literal["Bronze", "Silver", "Gold"] = "Bronze"

    createdAt: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))
    updatedAt: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))

    class Settings:
        name = "users"


class UserResponse(BaseModel):
    message: str
    user: User


class UserUploadImageRepsonse(BaseModel):
    message: str
    file: str


class UserCreate(BaseModel):
    uid: str
    phone: Optional[str] = None
    address: List[Any] = []
    vton_image: Optional[str] = None
    type: Literal["normal", "seller"] = "normal"
    # total_spent and tier are intentionally excluded — they are computed, not user-supplied
