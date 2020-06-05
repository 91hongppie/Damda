package com.example.damda

import android.content.ContentValues.TAG
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.example.damda.GlobalApplication.Companion.prefs
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import okhttp3.*
import java.io.IOException

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Looper.prepare()

        var msgHandler: Handler? = null

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
        val jwt = GlobalApplication.prefs.token
        val builder = Request.Builder().addHeader("Authorization", "JWT $jwt")
        val url = builder.url(prefs.damdaServer + "/api/accounts/device/")
        val formBody = FormBody.Builder()
        val body = formBody.add("token", token).add("user_id", "${prefs.user_id}").build()
        val request = url
            .post(body)
            .build()

        val callback = Callback1()

        client.newCall(request).enqueue(callback)

        prefs.device_token = token

    }

    inner class Callback1 : Callback {
        override fun onFailure(call: Call, e: IOException) {

        }

        override fun onResponse(call: Call, response: Response) {

            val result = response.body()?.string()

            Log.d("response", "result: $result")
        }
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        Log.d(TAG, "From: " + p0.from);

        // Check if message contains a data payload.
        if (p0.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: " + p0.data);
        }

        // Check if message contains a notification payload.
        if (p0.notification != null) {
            Log.d(TAG, "Message Notification Body: " + p0.notification!!.body);
        }
    }


}
