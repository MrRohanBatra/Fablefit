import os
from motor.motor_asyncio import AsyncIOMotorClient
from dotenv import load_dotenv

load_dotenv()

MONGO_URI = os.getenv("MONGODB_URI")

# 1. Safety Check: Fails loud and early if the .env file is missing or misnamed
if not MONGO_URI:
    raise ValueError("MONGODB_URI is missing from environment variables.")

client = AsyncIOMotorClient(MONGO_URI)

db = client["fablefit"]

print("Mongo async client created successfully")