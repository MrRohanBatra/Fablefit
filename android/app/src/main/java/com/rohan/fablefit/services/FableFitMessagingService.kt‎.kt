package com.rohan.fablefit.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.rohan.fablefit.MainActivity
import com.rohan.fablefit.R
import com.rohan.fablefit.network.RetrofitInstance
import com.rohan.fablefit.ui.model.FcmTokenUpdate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FableFitMessagingService : FirebaseMessagingService() {

    companion object {
        private const val CHANNEL_ID   = "fablefit_alerts"
        private const val CHANNEL_NAME = "FableFit Alerts"
        private const val TAG          = "FableFitFCM"
    }

    /**
     * Called when FCM issues a new token (first launch or token rotation).
     * Upload it to the backend so the scheduler can reach this device.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            Log.w(TAG, "No logged-in user — token not uploaded yet")
            return
        }
        uploadToken(uid, token)
    }

    /**
     * Called when a message arrives while the app is in the FOREGROUND.
     * When the app is in the background/killed, FCM displays the notification
     * automatically using the notification payload — no code needed for that case.
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title ?: "FableFit"
        val body  = message.notification?.body  ?: return   // nothing to show
        val data  = message.data

        Log.d(TAG, "Message received: type=${data["type"]}")
        showNotification(title, body, data)
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun showNotification(title: String, body: String, data: Map<String, String>) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create channel (safe to call repeatedly — no-op if it exists)
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = "Cart reminders and price drop alerts" }
        manager.createNotificationChannel(channel)

        // Tap opens MainActivity
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Pass product_id through so MainActivity could deep-link if desired
            data["product_id"]?.let { putExtra("product_id", it) }
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    // ── Static-ish helpers ────────────────────────────────────────────────────

    fun uploadToken(uid: String, token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                RetrofitInstance.api.updateFcmToken(FcmTokenUpdate(uid = uid, fcmToken = token))
                Log.d(TAG, "FCM token uploaded for uid=$uid")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to upload FCM token: ${e.message}")
            }
        }
    }
}