from fastapi import APIRouter, Depends, UploadFile, File, Form
from database import db
from helpers.Utilities import Utils
from datetime import datetime, timezone
from databaseSchemas.UserSchema import User
import os
import shutil

UserRouter = APIRouter(prefix="/user")
Tools = Utils()


async def get_db():
    return db
@UserRouter.post("/add")
async def add_user(user: User, database=Depends(get_db)):

    users = database["users"]

    existing = await users.find_one({"uid": user.uid})

    user_dict = user.model_dump()

    if existing:
        await users.update_one(
            {"uid": user.uid},
            {
                "$set": {
                    **user_dict,
                    "updatedAt": datetime.now(timezone.utc)
                }
            }
        )

        updated = await users.find_one({"uid": user.uid})
        return {"message": "User updated", "user": Tools.serializeDoc(updated)}

    await users.insert_one(user_dict)

    return {"message": "User created", "user": user_dict}

@UserRouter.get("/{uid}")
async def get_user(uid: str, database=Depends(get_db)):

    users = database["users"]

    user = await users.find_one({"uid": uid})

    if not user:
        return {"message": "User not found"}

    return Tools.serializeDoc(user)

@UserRouter.post("/updatetype/{uid}")
async def update_user_type(uid: str, payload: dict, database=Depends(get_db)):

    users = database["users"]

    type = payload.get("type")

    if not type:
        return {"message": "New user type is required"}

    if type not in ["normal", "seller"]:
        return {"message": "Invalid user type"}

    user = await users.find_one({"uid": uid})

    if not user:
        return {"message": "User not found"}

    await users.update_one(
        {"uid": uid},
        {"$set": {"type": type}}
    )

    updated = await users.find_one({"uid": uid})

    return {
        "message": "User type updated successfully",
        "user": Tools.serializeDoc(updated)
    }

@UserRouter.get("/all/users")
async def all_users(database=Depends(get_db)):

    users = database["users"]

    cursor = users.find({})

    result = []

    async for u in cursor:
        result.append(Tools.serializeDoc(u))

    return result

@UserRouter.post("/uploadimage")
async def upload_image(
    uid: str = Form(...),
    image: UploadFile = File(...)
):

    upload_dir = "images"
    os.makedirs(upload_dir, exist_ok=True)

    ext = os.path.splitext(image.filename)[1]
    filename = f"{uid}_{int(datetime.now().timestamp())}{ext}"
    filepath = os.path.join(upload_dir, filename)

    with open(filepath, "wb") as buffer:
        shutil.copyfileobj(image.file, buffer)

    return {
        "message": "Image replaced successfully",
        "file": f"/images/{filename}"
    }
