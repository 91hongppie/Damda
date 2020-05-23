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

    fun sendRegistrationToServer(token: String) {
        var thread = NetworkThread()
        thread.start()
    }

    inner class NetworkThread: Thread() {
        override fun run() {

            var token : String? = null

            @SuppressLint("HandlerLeak")
            val handler: Handler = object : Handler() {
                override fun handleMessage(message: Message) {
                    token = message.obj.toString()
                }
            }

            var client = OkHttpClient()

            var builder = Request.Builder()
            var url = builder.url("http://10.0.2.2:8000/api/accounts/addtoken/")
            var formBody = FormBody.Builder()
            var body = formBody.add("token", token).build()
            var request = url
                .post(body)
                .build()

            var callback = Callback1()

            client.newCall(request).enqueue(callback)

        }
    }

    inner class Callback1: Callback {
        override fun onFailure(call: Call, e: IOException) {

        }

        override fun onResponse(call: Call, response: Response) {

            var result = response?.body()?.string()

            Log.d("response", "result: $result")
        }
    }
}
