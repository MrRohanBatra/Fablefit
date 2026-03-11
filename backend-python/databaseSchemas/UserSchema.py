from typing import List, Optional, Any, Literal
from pydantic import Field, BaseModel
from datetime import datetime, timezone
from beanie import Document

class User(Document):
    # 🔹 Standard string, indexing removed for now
    uid: str 

    phone: Optional[str] = None

    # Address as list of mixed objects
    address: List[Any] = Field(default_factory=list)

    vton_image: Optional[str] = None


    type: Literal["normal", "seller"] = "normal"

    createdAt: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))
    updatedAt: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))

    class Settings:
        name = "users"


class UserResponse(BaseModel):
    message:str
    user: User

class UserUploadImageRepsonse(BaseModel):
    message:str
    file:str


class UserCreate(BaseModel):
    uid: str
    phone: Optional[str] = None
    address: List[Any] = []
    vton_image: Optional[str] = None
    type: Literal["normal", "seller"] = "normal"