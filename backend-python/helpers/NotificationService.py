import os
import firebase_admin
from firebase_admin import credentials, messaging

_initialized = False


def _init() -> bool:
    """Lazy-initialise Firebase Admin SDK once. Returns True if ready."""
    global _initialized
    if _initialized:
        return True

    current_dir = os.path.dirname(os.path.abspath(__file__))
    cred_path = os.path.join(current_dir, "firebase-admin.json")
    print(f"using path: {cred_path}")
    if not cred_path or not os.path.exists(cred_path):
        print(
            "⚠️  NotificationService: FIREBASE_SERVICE_ACCOUNT_PATH not set or file missing. "
            "Push notifications are disabled."
        )
        return False

    cred = credentials.Certificate(cred_path)
    firebase_admin.initialize_app(cred)
    _initialized = True
    print("✅ Firebase Admin SDK initialised")
    return True


def send_push(
    token: str,
    title: str,
    body: str,
    data: dict | None = None,
) -> bool:
    """
    Send a single FCM notification to a device token.
    Returns True on success, False on any failure.
    data dict values must all be strings (FCM requirement).
    """
    if not _init():
        return False

    try:
        message = messaging.Message(
            notification=messaging.Notification(title=title, body=body),
            data={k: str(v) for k, v in (data or {}).items()},
            android=messaging.AndroidConfig(priority="high"),
            token=token,
        )
        response = messaging.send(message)
        print(f"📲 FCM sent: {response}")
        return True
    except Exception as exc:
        print(f"❌ FCM send error: {exc}")
        return False
