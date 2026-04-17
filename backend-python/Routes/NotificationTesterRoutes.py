
from fastapi import APIRouter, HTTPException,BackgroundTasks
from pydantic import BaseModel
from typing import Optional, Dict,List

from databaseSchemas.UserSchema import User
from helpers.NotificationService import send_push


noti_test_router = APIRouter(prefix="/notify", tags=["Notifications"])

# Define the request body schema
class NotificationRequest(BaseModel):
    token: str
    title: str
    body: str
    data: Optional[Dict[str, str]] = None  # FCM data values MUST be strings

@noti_test_router.post("/test")
async def test_notification(request: NotificationRequest):
    """
    Manually trigger a notification to a specific device token.
    """
    success = send_push(
        token=request.token,
        title=request.title,
        body=request.body,
        data=request.data
    )

    if success:
        return {"status": "success", "message": "Notification queued for delivery"}
    else:
        raise HTTPException(
            status_code=500, 
            detail="Failed to send notification. Check server logs for FCM errors."
        )
@noti_test_router.post("/all")
async def send_notification_to_all(backgroundTask:BackgroundTasks):
    users=await User.find(User.fcm_token!=None).to_list()
    if not users:
        return {"status": "info", "message": "No users found with registered tokens."}
    backgroundTask.add_task(notification_broadcaster,users)
    return {"status": "info", "message": f"Sending notification to {len(users)} users."}
async def notification_broadcaster(users:List[User]):
    success_count=0
    for user in users:
        send=send_push(
            token=user.fcm_token,
            title="Test Nofication",
            body="This was sent to test the fcm service"
        )
        if(send):
            success_count+=1
    print(f"✅ Broadcast complete: {success_count}/{len(users)} delivered.")
        