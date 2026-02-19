# from beanie import Document
# from typing import List, Optional
# from bson.binary import Binary
# from pydantic import Field
# from datetime import datetime


# class Product(Document):
#     name: str = Field(..., description="Product name")
#     description: str = Field(..., description="Product description")

#     category: str

#     price: float

#     sizes: List[str] = Field(default_factory=list)

#     color: str = "unknown"

#     stock: int = 0

#     companyName: str

#     images: List[str] = Field(default_factory=list)

#     vton_category: Optional[str] = None

#     embedding: Binary

#     createdAt: datetime = Field(default_factory=datetime.utcnow)
#     updatedAt: datetime = Field(default_factory=datetime.utcnow)

#     class Settings:
#         name = "products"

#     # 🔥 REQUIRED FOR BSON Binary
#     model_config = {
#         "arbitrary_types_allowed": True
#     }

from typing import List, Optional
from pydantic import BaseModel, Field
from datetime import datetime, timezone

class Product(BaseModel):
    name: str
    description: str
    category: str 
    price: float
    sizes: List[str] = Field(default_factory=list)
    color: str = "unknown"
    stock: int = 0
    companyName: str
    images: List[str] = Field(default_factory=list)
    vton_category: Optional[str] = None

    embedding: List[float] = Field(default_factory=list)

    createdAt: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))
    updatedAt: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))