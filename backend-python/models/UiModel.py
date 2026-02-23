from pydantic import BaseModel, Field
from typing import List, Optional
from enum import Enum

class SectionType(str, Enum):
    HORIZONTAL_LIST = "HORIZONTAL_LIST"
    GRID = "GRID"
    FEATURED = "FEATURED"

class ProductResponse(BaseModel):
    id: str = Field(alias="_id") # Maps MongoDB _id to JSON id
    name: str
    description: str
    category: str
    price: float
    sizes: List[str]
    color: str
    stock: int
    companyName: str
    images: List[str]
    vton_category: Optional[str] = None

    class Config:
        populate_by_name = True # Allows using 'id' or '_id'
        from_attributes = True

class HomeSectionResponse(BaseModel):
    id: str # Changed from _id to match your frontend data class
    title: str
    type: SectionType
    products: List[ProductResponse]