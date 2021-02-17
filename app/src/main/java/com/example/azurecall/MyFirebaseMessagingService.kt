package com.example.azurecall

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {
    private var pushNotificationMessageDataFromFCM: Map<String, String>? = null
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.d(
                "PushNotification", "Message Notification Body: " + remoteMessage.notification!!
                    .body
            )
        } else {
            pushNotificationMessageDataFromFCM = remoteMessage.data
        }
    }
}