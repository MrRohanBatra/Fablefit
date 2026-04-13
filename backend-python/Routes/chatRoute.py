from fastapi import APIRouter
from pydantic import BaseModel
from chatbot.agent import process_chat

router = APIRouter()

class ChatRequest(BaseModel):
    message: str
    user_id: str

@router.post("/chat")
async def chat(req: ChatRequest):
    return await process_chat(req.user_id, req.message)