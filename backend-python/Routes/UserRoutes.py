import os
import shutil
import sys
from datetime import datetime, timezone
from typing import List

from fastapi import APIRouter, File, Form, HTTPException, UploadFile
from openai import BaseModel
from databaseSchemas.UserSchema import User, UserResponse, UserUploadImageRepsonse, UserCreate, FcmTokenUpdate
from helpers.Utilities import Utils

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

UserRouter = APIRouter(prefix="/users",tags=["User"])
Tools = Utils()


@UserRouter.post("/add", response_model=UserResponse)
async def add_user(user_data: UserCreate):
    try:
        print(f"📥 Received user data: {user_data.uid}")
        user = await User.find_one(User.uid == user_data.uid)

        if user:
            print(f"🔄 Updating existing user: {user_data.uid}")
            if user_data.phone:       user.phone      = user_data.phone
            if user_data.name:        user.name       = user_data.name
            if user_data.type:        user.type       = user_data.type
            if user_data.vton_image:  user.vton_image = user_data.vton_image
            if user_data.address is not None:
                user.address = user_data.address
            user.updatedAt = datetime.now(timezone.utc)
            saved_user = await user.save()
            print("✅ User updated successfully")
            return {
                "message": "User updated",
                "user": Tools.serializeDoc(saved_user.model_dump(by_alias=True))
            }

        print(f"🆕 Creating new user: {user_data.uid}")
        new_user = User(**user_data.model_dump())
        await new_user.insert()
        return {
            "message": "User created",
            "user": Tools.serializeDoc(user_data.model_dump(by_alias=True))
        }

    except Exception as e:
        print(f"❌ Error in addUser: {e}")
        raise HTTPException(status_code=500, detail=str(e))


@UserRouter.post("/fcmtoken")
async def update_fcm_token(payload: FcmTokenUpdate):
    """
    Called by the Android app whenever FCM issues a new device token.
    Stored on the user document so the scheduler can find it for notifications.
    """
    user = await User.find_one(User.uid == payload.uid)
    if not user:
        raise HTTPException(status_code=404, detail="User not found")

    user.fcm_token = payload.fcm_token
    user.updatedAt = datetime.now(timezone.utc)
    await user.save()

    print(f"📲 FCM token updated for uid={payload.uid}")
    return {"message": "FCM token updated"}


@UserRouter.get("/{uid}", response_model=User)
async def get_user(uid: str):
    user = await User.find_one(User.uid == uid)
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    return Tools.serializeDoc(user.model_dump(by_alias=True))


@UserRouter.post("/updatetype/{uid}", response_model=UserResponse)
async def update_user_type(uid: str, payload: User):
    new_type = payload.get("type")
    if not new_type:
        raise HTTPException(status_code=400, detail="New user type is required")
    if new_type not in ["normal", "seller"]:
        raise HTTPException(status_code=400, detail="Invalid user type")

    user = await User.find_one(User.uid == uid)
    if not user:
        raise HTTPException(status_code=404, detail="User not found")

    user.type      = new_type  # type: ignore
    user.updatedAt = datetime.now(timezone.utc)
    await user.save()
    return {"message": "User type updated successfully", "user": Tools.serializeDoc(user.model_dump(by_alias=True))}


@UserRouter.get("/all/users", response_model=List[User])
async def all_users():
    users = await User.find_all().to_list()
    return [Tools.serializeDoc(u.model_dump(by_alias=True)) for u in users]
class UserAddressRequest(BaseModel):
    address:str
@UserRouter.put("address/{uid}",response_model=UserResponse)
async def updateAddress(uid:str,payload:UserAddressRequest):
    print(f"uid: {uid}")
    print(f"payload: {payload}")
    user=await User.find_one(User.uid==uid)
    if not user:
        raise HTTPException(404,"user not found")
    if not payload:
        raise HTTPException(404,"empty payload")
    print(f"user address updated to {payload.address}")
    user.address=[payload.address]
    await user.save()
    return {"message":"user address updated","uid":uid}
@UserRouter.post("/uploadimage", response_model=UserUploadImageRepsonse)
async def upload_image(uid: str = Form(...), image: UploadFile = File(...)):
    try:
        user = await User.find_one({"uid": uid})
        if not user:
            raise HTTPException(status_code=404, detail="User not found")

        upload_dir = "images/102"
        os.makedirs(upload_dir, exist_ok=True)

        ext       = os.path.splitext(image.filename)[1] if image.filename else ".png"
        timestamp = int(datetime.now().timestamp() * 1000)
        filename  = f"{uid}_{timestamp}{ext}"
        filepath  = os.path.join(upload_dir, filename)

        with open(filepath, "wb") as buffer:
            shutil.copyfileobj(image.file, buffer)

        user.vton_image = filepath
        await user.save()
        return {"message": "Image replaced successfully", "file": filepath}

    except Exception as e:
        print(f"Upload error: {e}")
        raise HTTPException(status_code=500, detail=str(e))
