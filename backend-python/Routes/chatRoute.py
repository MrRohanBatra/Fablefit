from typing import Optional

from fastapi import APIRouter, UploadFile, File, Form
from chatbot.agent import process_chat
from Routes.ProductRoutes import search_images # Re-using your CLIP search

chat_router = APIRouter(prefix="/chat",tags=["Rasberry"])

@chat_router.post("/")
async def chat_with_agent(
    user_id: str = Form(...),
    message: str = Form(""),
    image: Optional[UploadFile] = File(None)
):
    image_context = None
    
    # If user sent an image, perform CLIP search first to give agent context
    if image:
        image_context = await search_images(image)
    
    return await process_chat(user_id, message, image_results=image_context)