package com.example.azurecall

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.RemoteMessage
import com.microsoft.windowsazure.messaging.notificationhubs.NotificationListener


class CustomNotificationListener : NotificationListener {
    override fun onPushNotificationReceived(context: Context?, message: RemoteMessage?) {
        /* The following notification properties are available. */
        val notification: RemoteMessage.Notification? = message!!.notification
        val title = notification?.title!!
        val body = notification?.body!!
        val data = message.data
        if (message != null) {
            Log.d("PushNotification", "Message Notification Title: $title")
            Log.d("PushNotification", "Message Notification Body: $message")
        }
        if (data != null) {
            for ((key, value) in data) {
                Log.d("PushNotification", "key, $key value $value")
            }
        }
    }
}