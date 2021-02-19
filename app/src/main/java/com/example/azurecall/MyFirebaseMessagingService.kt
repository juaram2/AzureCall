package com.example.azurecall

import android.util.Log
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.communication.calling.CallAgent
import com.azure.communication.calling.CallClient
import com.azure.communication.calling.IncomingCallPushNotification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {

    private var pushNotificationMessageDataFromFCM: Map<String, String>? = null
    private var callAgent: CallAgent? = null
    private val userToken =
//  my token
//    "eyJhbGciOiJSUzI1NiIsImtpZCI6IjEwMiIsIng1dCI6IjNNSnZRYzhrWVNLd1hqbEIySmx6NTRQVzNBYyIsInR5cCI6IkpXVCJ9.eyJza3lwZWlkIjoiYWNzOjkxODU5N2JlLTNmMjktNDY4MC1hOTFhLWI0ZjVlNjc1OGExN18wMDAwMDAwOC00OTI0LWUxMjUtOTljNi01OTNhMGQwMDBhMjQiLCJzY3AiOjE3OTIsImNzaSI6IjE2MTM2MTEyNDYiLCJpYXQiOjE2MTM2MTEyNDYsImV4cCI6MTYxMzY5NzY0NiwiYWNzU2NvcGUiOiJ2b2lwIiwicmVzb3VyY2VJZCI6IjkxODU5N2JlLTNmMjktNDY4MC1hOTFhLWI0ZjVlNjc1OGExNyJ9.atcaLBGgqtcgv78vHQYs3Z61ELQmgpM8EuQxtmwh_ZdQPb-waLCVtWzeJTYGzhlUxVN-KD-HiPpP4uqiC_CD-0-rx2Lfj-sOLepTwqXSoTfR4NDaqRvx2geYl2sY1n202b7Rs7y7TpStSsEjG91ysgkX75cD1NEFR8RTpyh-kPwxF34m9gc6oRUJy6geDp2H6kmbopaLoznbFg04oFJFD8suiOO5wMnFaa4GExnQxo1PtzyRroPyRc3PymrIzNs6bmQS3HWhy_gQIbX_D81si8tIY9ENtnvV--iQjGHXnFYc9ghOzIItXZt4D0Cw_9JbVUU2v8yGdqYRdt6-y222ng"
//  help token
        "eyJhbGciOiJSUzI1NiIsImtpZCI6IjEwMiIsIng1dCI6IjNNSnZRYzhrWVNLd1hqbEIySmx6NTRQVzNBYyIsInR5cCI6IkpXVCJ9.eyJza3lwZWlkIjoiYWNzOjE0ZjkxMDY3LWFmYWYtNDhjYS1iZTBlLTdmMWM5ODkwZjU1OV8wMDAwMDAwNi03YTg4LTAxYzAtYWMwMC0zNDNhMGQwMDAxOGYiLCJzY3AiOjE3OTIsImNzaSI6IjE2MDk4MzYyMTEiLCJpYXQiOjE2MDk4MzYyMTEsImV4cCI6MTYwOTkyMjYxMSwiYWNzU2NvcGUiOiJ2b2lwIiwicmVzb3VyY2VJZCI6IjE0ZjkxMDY3LWFmYWYtNDhjYS1iZTBlLTdmMWM5ODkwZjU1OSJ9.00Rqh27qzE2ARJ0wAZFgwkw9Byb5QMiPXqiFHMBAXHuz3o0rsIX6K-QBy2EXwIMmvPcv74garZmOynPuC9EPQ8uDWTp2QM2i93bx5eB88x_UBuEpTumV_mZf2cUkesCYtRL3TYgvLZwlx5m2txFvnvQu8EHAPWaiNDVKr2_9yvktarTeT-FZSEG8tx5zn0BKe4V-bd8ZCb3cJVvIVjGpppgQHJP9zbt5rxgz-KyoYIa3Nd1jPEY06Y9BE1BdO33OQVve2aJMqY44P0n7qtCk73t0AwXSEAibTIknDgUtpYRWcVi4Xkz6vqVP6drEn_JIMO3zRDi1L0UFkSSCOdM_hQ"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.d(
                "PushNotification", "Message Notification Body: " + remoteMessage.notification!!
                    .body
            )
        } else {
            val credential = CommunicationTokenCredential(userToken)
            callAgent = CallClient().createCallAgent(applicationContext, credential).get()
            pushNotificationMessageDataFromFCM = remoteMessage.data
            val incomingCallPushNotification = IncomingCallPushNotification.fromMap(pushNotificationMessageDataFromFCM)
            try {
                callAgent!!.handlePushNotification(incomingCallPushNotification).get()
                Log.d("PushNotification","$incomingCallPushNotification")
            } catch (e: java.lang.Exception) {
                println("Something went wrong while handling the Incoming Calls Push Notifications.")
            }
        }

//            UNREGISTER PUSH NOTIFICATION
//            try {
//                callAgent!!.unRegisterPushNotifications().get();
//            }
//            catch(e: Exception) {
//                println("Something went wrong while un-registering for all Incoming Calls Push Notifications.")
//            }
    }
}