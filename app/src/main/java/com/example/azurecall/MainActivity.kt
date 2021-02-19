package com.example.azurecall

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationUserIdentifier
import com.azure.communication.calling.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.microsoft.windowsazure.messaging.notificationhubs.NotificationHub
import java.util.*


class MainActivity : AppCompatActivity() {

  private val userToken =
//  my token
//    "eyJhbGciOiJSUzI1NiIsImtpZCI6IjEwMiIsIng1dCI6IjNNSnZRYzhrWVNLd1hqbEIySmx6NTRQVzNBYyIsInR5cCI6IkpXVCJ9.eyJza3lwZWlkIjoiYWNzOjkxODU5N2JlLTNmMjktNDY4MC1hOTFhLWI0ZjVlNjc1OGExN18wMDAwMDAwOC00OTI0LWUxMjUtOTljNi01OTNhMGQwMDBhMjQiLCJzY3AiOjE3OTIsImNzaSI6IjE2MTM2MTEyNDYiLCJpYXQiOjE2MTM2MTEyNDYsImV4cCI6MTYxMzY5NzY0NiwiYWNzU2NvcGUiOiJ2b2lwIiwicmVzb3VyY2VJZCI6IjkxODU5N2JlLTNmMjktNDY4MC1hOTFhLWI0ZjVlNjc1OGExNyJ9.atcaLBGgqtcgv78vHQYs3Z61ELQmgpM8EuQxtmwh_ZdQPb-waLCVtWzeJTYGzhlUxVN-KD-HiPpP4uqiC_CD-0-rx2Lfj-sOLepTwqXSoTfR4NDaqRvx2geYl2sY1n202b7Rs7y7TpStSsEjG91ysgkX75cD1NEFR8RTpyh-kPwxF34m9gc6oRUJy6geDp2H6kmbopaLoznbFg04oFJFD8suiOO5wMnFaa4GExnQxo1PtzyRroPyRc3PymrIzNs6bmQS3HWhy_gQIbX_D81si8tIY9ENtnvV--iQjGHXnFYc9ghOzIItXZt4D0Cw_9JbVUU2v8yGdqYRdt6-y222ng"
//  help token
    "eyJhbGciOiJSUzI1NiIsImtpZCI6IjEwMiIsIng1dCI6IjNNSnZRYzhrWVNLd1hqbEIySmx6NTRQVzNBYyIsInR5cCI6IkpXVCJ9.eyJza3lwZWlkIjoiYWNzOjE0ZjkxMDY3LWFmYWYtNDhjYS1iZTBlLTdmMWM5ODkwZjU1OV8wMDAwMDAwNi03YTg4LTAxYzAtYWMwMC0zNDNhMGQwMDAxOGYiLCJzY3AiOjE3OTIsImNzaSI6IjE2MDk4MzYyMTEiLCJpYXQiOjE2MDk4MzYyMTEsImV4cCI6MTYwOTkyMjYxMSwiYWNzU2NvcGUiOiJ2b2lwIiwicmVzb3VyY2VJZCI6IjE0ZjkxMDY3LWFmYWYtNDhjYS1iZTBlLTdmMWM5ODkwZjU1OSJ9.00Rqh27qzE2ARJ0wAZFgwkw9Byb5QMiPXqiFHMBAXHuz3o0rsIX6K-QBy2EXwIMmvPcv74garZmOynPuC9EPQ8uDWTp2QM2i93bx5eB88x_UBuEpTumV_mZf2cUkesCYtRL3TYgvLZwlx5m2txFvnvQu8EHAPWaiNDVKr2_9yvktarTeT-FZSEG8tx5zn0BKe4V-bd8ZCb3cJVvIVjGpppgQHJP9zbt5rxgz-KyoYIa3Nd1jPEY06Y9BE1BdO33OQVve2aJMqY44P0n7qtCk73t0AwXSEAibTIknDgUtpYRWcVi4Xkz6vqVP6drEn_JIMO3zRDi1L0UFkSSCOdM_hQ"
  private var callAgent: CallAgent? = null
  private var callClient = CallClient()
  private var call: Call? = null

  var statusBar: TextView? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    getAllPermissions()
    createAgent()

    // Bind call button to call `startCall`
    val callButton: Button = findViewById<Button>(R.id.call_button)
    callButton.setOnClickListener { startCall() }

    statusBar = findViewById<TextView>(R.id.status_bar)

    var incomingCall: Call? = null
    callAgent!!.addOnCallsUpdatedListener { callsUpdatedEvent -> // Look for incoming call
      val calls = callsUpdatedEvent.addedCalls
      for (call in calls) {
        if (call.state == CallState.Incoming) {
          incomingCall = call
          break
        }
      }
      incomingCall?.addOnCallStateChangedListener { state ->
        Log.d("state", "$state")
        Log.d("Notification", "The call state has changed.");
      }
    }

    NotificationHub.setListener(CustomNotificationListener())
    NotificationHub.start(application, "chnotification-int-voip", "Endpoint=sb://chhubns.servicebus.windows.net/;SharedAccessKeyName=DefaultListenSharedAccessSignature;SharedAccessKey=Cjjoip6v+KpgR9QkUWqoZeCUYkxJCXTU5KDfgb07bWM=")
  }

  /**
   * Request each required permission if the app doesn't already have it.
   */
  private fun getAllPermissions() {
    val requiredPermissions = arrayOf(
      Manifest.permission.RECORD_AUDIO,
      Manifest.permission.CAMERA,
      Manifest.permission.WRITE_EXTERNAL_STORAGE,
      Manifest.permission.READ_PHONE_STATE
    )
    val permissionsToAskFor = ArrayList<String>()
    for (permission in requiredPermissions) {
      if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
        permissionsToAskFor.add(permission)
      }
    }
    if (permissionsToAskFor.isNotEmpty()) {
      ActivityCompat.requestPermissions(this, permissionsToAskFor.toTypedArray(), 1)
    }
  }

  /**
   * Ensure all permissions were granted, otherwise inform the user permissions are missing.
   */
  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String?>,
    grantResults: IntArray
  ) {
    var allPermissionsGranted = true
    for (result in grantResults) {
      allPermissionsGranted =
        allPermissionsGranted and (result == PackageManager.PERMISSION_GRANTED)
    }
    if (!allPermissionsGranted) {
      Toast.makeText(this, "All permissions are needed to make the call.", Toast.LENGTH_LONG).show()
      finish()
    }
  }

  /**
   * Create the call agent for placing calls
   */
  private fun createAgent() {
    try {
      val credential = CommunicationTokenCredential(userToken)
      val callAgentOption = CallAgentOptions()
      callAgentOption.displayName = "Test"
      callAgent = callClient.createCallAgent(applicationContext, credential, callAgentOption).get()

      FirebaseInstanceId.getInstance().instanceId
        .addOnCompleteListener(OnCompleteListener { task ->
          if (!task.isSuccessful) {
            Log.w("PushNotification", "getInstanceId failed", task.exception)
            return@OnCompleteListener
          }

          // Get new Instance ID token
          val deviceToken = task.result!!.token
          // Log
          Log.d("PushNotification", "$deviceToken : Device Registration token retrieved successfully")
        })
    } catch (ex: Exception) {
      Toast.makeText(applicationContext, "Failed to create call agent.", Toast.LENGTH_SHORT).show()
    }
  }

  /**
   * Place a call to the callee id provided in `callee_id` text input.
   */
  private fun startCall() {
    if (userToken.startsWith("<")) {
      Toast.makeText(this, "Please enter token in source code", Toast.LENGTH_SHORT).show()
      return
    }

    val calleeIdView = findViewById<EditText>(R.id.callee_id)
    val calleeId = calleeIdView.text.toString()
    if (calleeId.isEmpty()) {
      Toast.makeText(this, "Please enter callee", Toast.LENGTH_SHORT).show()
      return
    }

    val deviceManager = callClient.deviceManager.get()
    deviceManager.let {

    }
    var defaultMicrophone = deviceManager.microphoneList[0]
    var defaultSpeaker = deviceManager.speakerList[0]
    deviceManager.microphone = defaultMicrophone
    deviceManager.speaker = defaultSpeaker

    val desiredCamera: VideoDeviceInfo = deviceManager.cameraList[0]
    val currentVideoStream = LocalVideoStream(desiredCamera, applicationContext)
    val videoOptions = VideoOptions(currentVideoStream)

    // Render a local preview of video so the user knows that their video is being shared
    val previewRenderer = Renderer(currentVideoStream, applicationContext)
    val uiView: View = previewRenderer.createView(RenderingOptions(ScalingMode.Fit))

    var layout = findViewById<LinearLayout>(R.id.video_view)
    layout.addView(uiView)

    val acsUserId = CommunicationUserIdentifier(calleeId)
    val participants = arrayOf(acsUserId)

    val startCallOptions = StartCallOptions()
    startCallOptions.videoOptions = videoOptions
    call = callAgent!!.call(applicationContext, participants, startCallOptions)

    call!!.addOnCallStateChangedListener { p: PropertyChangedEvent? ->
      setStatus(
        call!!.state.toString()
      )
    }
  }

  /**
   * Shows message in the status bar
   */
  private fun setStatus(status: String) {
    runOnUiThread { statusBar!!.text = status }
  }

  fun getCall(callId: String): Call? {
    call.let {
      val currentCallId: String? = call!!.callId
      if (currentCallId == callId) {
        return call
      } else {
        return null
      }
    }
  }

  private fun acceptCall() {
    val appContext = this.applicationContext
    val incomingCall: Call = retrieveIncomingCall()!!
    val acceptCallOptions = AcceptCallOptions()
    val desiredCamera: VideoDeviceInfo = callClient.deviceManager?.get()?.cameraList!![0]
    acceptCallOptions.videoOptions = VideoOptions(LocalVideoStream(desiredCamera, appContext))
    incomingCall.accept(applicationContext, acceptCallOptions).get()
  }

  private fun retrieveIncomingCall(): Call? {
    var incomingCall: Call? = null
    callAgent!!.addOnCallsUpdatedListener { callsUpdatedEvent -> // Look for incoming call
      val calls = callsUpdatedEvent.addedCalls
      for (call in calls) {
        if (call.state === CallState.Incoming) {
          incomingCall = call
          break
        }
      }
    }
    return incomingCall
  }
}