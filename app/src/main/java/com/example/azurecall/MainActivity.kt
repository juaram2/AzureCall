package com.example.azurecall

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.azure.android.communication.common.CommunicationUser
import com.azure.android.communication.common.CommunicationUserCredential
import com.azure.communication.calling.*

class MainActivity : AppCompatActivity() {

  private var callAgent: CallAgent? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    getAllPermissions()
    createAgent()

    // Bind call button to call `startCall`
    val callButton = findViewById<Button>(R.id.call_button)
    callButton.setOnClickListener { startCall() }
  }

  /**
   * Request each required permission if the app doesn't already have it.
   */
  private fun getAllPermissions() {
    val requiredPermissions = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE)
    val permissionsToAskFor: ArrayList<String> = ArrayList()
    for (permission in requiredPermissions) {
      if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
        permissionsToAskFor.add(permission)
      }
    }
    if (permissionsToAskFor.isNotEmpty()) {
      ActivityCompat.requestPermissions(this, permissionsToAskFor.toArray(arrayOfNulls(0)), 1)
    }
  }

  /**
   * Create the call agent for placing calls
   */
  private fun createAgent() {
    val userToken = "endpoint=https://chazurecommunicationservices.communication.azure.com/;accesskey=t3TQUTIs1hrxGEL4BPJU2UcGZ7oE0kDIKoYyqet3yUPlAUKojrw5fUacu/hNAsVggxdPhnBRaCC6b8iwhduZ0g=="

    try {
      val credential = CommunicationUserCredential(userToken)
      callAgent = CallClient().createCallAgent(applicationContext, credential).get()
    } catch (ex: Exception) {
      Toast.makeText(applicationContext, "Failed to create call agent.", Toast.LENGTH_SHORT).show()
    }
  }

  /**
   * Place a call to the callee id provided in `callee_id` text input.
   */
  private fun startCall() {
    val calleeIdView = findViewById<EditText>(R.id.callee_id)
    val calleeId = calleeIdView.text.toString()
    val startCallOptions = StartCallOptions()

    callAgent?.call(this, arrayOf(CommunicationUser(calleeId)), startCallOptions)
  }

}