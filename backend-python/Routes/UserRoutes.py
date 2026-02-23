import os
import shutil
import sys
from datetime import datetime, timezone

from fastapi import APIRouter, File, Form, HTTPException, UploadFile
from databaseSchemas.UserSchema import User
from helpers.Utilities import Utils

# Setup path for internal imports
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

UserRouter = APIRouter(prefix="/users")
Tools = Utils()

@UserRouter.post("/add")
async def add_user(user_data: User):
    """Add or update user (Matches Node.js addUser logic)"""
    try:
        print(f"📥 Received user data: {user_data.uid}")

        # Check for existing user
        user = await User.find_one(User.uid == user_data.uid)

        if user:
            print(f"🔄 Updating existing user: {user_data.uid}")
            
            # Update fields if provided (mimicking the Node.js null-coalescing check)
            if user_data.phone: user.phone = user_data.phone
            if user_data.type: user.type = user_data.type
            if user_data.vton_image: 
                print(f"🖼️ Updating vton_image: {user_data.vton_image}")
                user.vton_image = user_data.vton_image
            
            if user_data.address is not None:
                print(f"🏠 Updating address: {user_data.address}")
                user.address = user_data.address
            
            user.updatedAt = datetime.now(timezone.utc)
            saved_user = await user.save()
            
            print("✅ User updated successfully")
            return {
                "message": "User updated", 
                "user": Tools.serializeDoc(saved_user.model_dump(by_alias=True))
            }

        # Create new user if not found
        print(f"🆕 Creating new user: {user_data.uid}")
        # Beanie's .insert() uses the defaults defined in your UserSchema
        await user_data.insert()
        
        return {
            "message": "User created", 
            "user": Tools.serializeDoc(user_data.model_dump(by_alias=True))
        }

    except Exception as e:
        print(f"❌ Error in addUser: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@UserRouter.get("/{uid}")
async def get_user(uid: str):
    """Get user by UID"""
    user = await User.find_one(User.uid == uid)
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    
    return Tools.serializeDoc(user.model_dump(by_alias=True))

@UserRouter.post("/updatetype/{uid}")
async def update_user_type(uid: str, payload: dict):
    """Update user type only"""
    new_type = payload.get("type")
    
    if not new_type:
        raise HTTPException(status_code=400, detail="New user type is required")
    
    if new_type not in ["normal", "seller"]:
        raise HTTPException(status_code=400, detail="Invalid user type")

    user = await User.find_one(User.uid == uid)
    if not user:
        raise HTTPException(status_code=404, detail="User not found")

    user.type = new_type # type: ignore
    user.updatedAt = datetime.now(timezone.utc)
    await user.save()

    return {
        "message": "User type updated successfully",
        "user": Tools.serializeDoc(user.model_dump(by_alias=True))
    }

@UserRouter.get("/all/users")
async def all_users():
    """Get all users"""
    users = await User.find_all().to_list()
    return [Tools.serializeDoc(u.model_dump(by_alias=True)) for u in users]

@UserRouter.post("/uploadimage")
async def upload_image(
    uid: str = Form(...),
    image: UploadFile = File(...)
):
    """Handle vton image upload and replacement"""
    try:
        upload_dir = "images"
        os.makedirs(upload_dir, exist_ok=True)

        ext = os.path.splitext(image.filename)[1] if image.filename else ".png"
        # Matches Node.js fileName: `${uid}_${Date.now()}${ext}`
        timestamp = int(datetime.now().timestamp() * 1000)
        filename = f"{uid}_{timestamp}{ext}"
        filepath = os.path.join(upload_dir, filename)

        # In Python, we just write the new file; 
        # for strict 'replacement' of the specific timestamped file, 
        # usually, we'd delete old ones based on a pattern, 
        # but here we follow your Node logic of saving the new one.
        with open(filepath, "wb") as buffer:
            shutil.copyfileobj(image.file, buffer)

        return {
            "message": "Image replaced successfully",
            "file": f"/images/{filename}"
        }
    except Exception as e:
        print(f"Upload error: {e}")
        raise HTTPException(status_code=500, detail=str(e))