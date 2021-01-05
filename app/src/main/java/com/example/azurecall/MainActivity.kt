package com.example.azurecall

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.azure.android.communication.common.CommunicationUser
import com.azure.android.communication.common.CommunicationUserCredential
import com.azure.communication.calling.*
import java.util.*

class MainActivity : AppCompatActivity() {

  private val UserToken =
    "eyJhbGciOiJSUzI1NiIsImtpZCI6IjEwMiIsIng1dCI6IjNNSnZRYzhrWVNLd1hqbEIySmx6NTRQVzNBYyIsInR5cCI6IkpXVCJ9.eyJza3lwZWlkIjoiYWNzOjE0ZjkxMDY3LWFmYWYtNDhjYS1iZTBlLTdmMWM5ODkwZjU1OV8wMDAwMDAwNi03YTg4LTAxYzAtYWMwMC0zNDNhMGQwMDAxOGYiLCJzY3AiOjE3OTIsImNzaSI6IjE2MDk4MzYyMTEiLCJpYXQiOjE2MDk4MzYyMTEsImV4cCI6MTYwOTkyMjYxMSwiYWNzU2NvcGUiOiJ2b2lwIiwicmVzb3VyY2VJZCI6IjE0ZjkxMDY3LWFmYWYtNDhjYS1iZTBlLTdmMWM5ODkwZjU1OSJ9.00Rqh27qzE2ARJ0wAZFgwkw9Byb5QMiPXqiFHMBAXHuz3o0rsIX6K-QBy2EXwIMmvPcv74garZmOynPuC9EPQ8uDWTp2QM2i93bx5eB88x_UBuEpTumV_mZf2cUkesCYtRL3TYgvLZwlx5m2txFvnvQu8EHAPWaiNDVKr2_9yvktarTeT-FZSEG8tx5zn0BKe4V-bd8ZCb3cJVvIVjGpppgQHJP9zbt5rxgz-KyoYIa3Nd1jPEY06Y9BE1BdO33OQVve2aJMqY44P0n7qtCk73t0AwXSEAibTIknDgUtpYRWcVi4Xkz6vqVP6drEn_JIMO3zRDi1L0UFkSSCOdM_hQ"
  private var callAgent: CallAgent? = null
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
  }

  /**
   * Request each required permission if the app doesn't already have it.
   */
  private fun getAllPermissions() {
    val requiredPermissions = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE)
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
   * Create the call agent for placing calls
   */
  private fun createAgent() {
    try {
      val credential = CommunicationUserCredential(UserToken)
      callAgent = CallClient().createCallAgent(applicationContext, credential).get()
    } catch (ex: Exception) {
      Toast.makeText(applicationContext, "Failed to create call agent.", Toast.LENGTH_SHORT).show()
    }
  }

  /**
   * Place a call to the callee id provided in `callee_id` text input.
   */
  private fun startCall() {
    if (UserToken.startsWith("<")) {
      Toast.makeText(this, "Please enter token in source code", Toast.LENGTH_SHORT).show()
      return
    }

    val calleeIdView = findViewById<EditText>(R.id.callee_id)
    val calleeId = calleeIdView.text.toString()
    if (calleeId.isEmpty()) {
      Toast.makeText(this, "Please enter callee", Toast.LENGTH_SHORT).show()
      return
    }

    val options = StartCallOptions()
    call = callAgent!!.call(
      applicationContext, arrayOf(CommunicationUser(calleeId)),
      options
    )
    call!!.addOnCallStateChangedListener { p: PropertyChangedEvent? ->
      setStatus(
        call!!.state.toString()
      )
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
   * Shows message in the status bar
   */
  private fun setStatus(status: String) {
    runOnUiThread { statusBar!!.text = status }
  }
}