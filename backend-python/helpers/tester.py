import json
from typing import List
import sys
import os
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from pydantic import BaseModel,TypeAdapter
class ProductData(BaseModel):
    name: str
    description: str
    category: str
    price: int
    sizes: List[str]
    color: str
    stock: int
    companyName: str
    images: List[str]
    vton_category: str
    # Add the new column here
    embedding: List[float] = []
    
adaptor=TypeAdapter(List[ProductData])

data=adaptor.validate_python(json.load(open("products_with_embeddings.json","r",encoding="utf-8")))
print(data[0])