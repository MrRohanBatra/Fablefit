from typing import List, Optional
from pydantic import BaseModel, Field
from datetime import datetime, timezone
from beanie import Document
class Product(Document):
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
    
    class Settings:
        name = "products"