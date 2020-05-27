package com.example.damda

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import okhttp3.*
import java.io.IOException

class MyFirebaseInstanceIdService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        var msgHandler : Handler? = null
        msgHandler = Handler()
        msgHandler.handleMessage(Message())

        var message = Message()
        message.obj = token

        msgHandler.sendMessage(message)

        sendRegistrationToServer(token)
        Log.d("New User", "Token: $token")
    }

    private fun sendRegistrationToServer(token: String) {
        val client = OkHttpClient()

        val builder = Request.Builder()
        val url = builder.url(getString(R.string.damda_server)+"/api/accounts/addtoken/")
        val formBody = FormBody.Builder()
        val body = formBody.add("token", token).build()
        val request = url
            .post(body)
            .build()

        val callback = Callback1()

        client.newCall(request).enqueue(callback)

    }

    inner class Callback1: Callback {
        override fun onFailure(call: Call, e: IOException) {

        }

        override fun onResponse(call: Call, response: Response) {

            val result = response.body()?.string()

            Log.d("response", "result: $result")
        }
    }
}
