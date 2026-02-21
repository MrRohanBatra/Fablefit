import os
from motor.motor_asyncio import AsyncIOMotorClient, AsyncIOMotorDatabase
from dotenv import load_dotenv

load_dotenv()

MONGO_URI = os.getenv("MONGODB_URI")

# 1. Safety Check: Fails loud and early if the .env file is missing or misnamed
if not MONGO_URI:
    raise ValueError("MONGODB_URI is missing from environment variables.")

# 2. Create the Client
client = AsyncIOMotorClient(MONGO_URI)

# 3. Reference the database for Fablefit
db = client["fablefit"]

# 4. Tiny fix: The return type is AsyncIOMotorDatabase, not AsyncIOMotorClient
async def get_db() -> AsyncIOMotorDatabase:
    return db
    
print("Mongo async client created successfully")