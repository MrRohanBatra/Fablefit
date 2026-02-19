from typing import List, Optional, Any
from pydantic import BaseModel, Field
from datetime import datetime, timezone


class User(BaseModel):
    uid: str  # Firebase UID

    phone: Optional[str] = None

    # Address as list of mixed objects (same as mongoose Mixed[])
    address: List[Any] = Field(default_factory=list)

    # Virtual try-on image URL/path
    vton_image: Optional[str] = None

    # normal | seller
    type: str = "normal"

    createdAt: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))
    updatedAt: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))
