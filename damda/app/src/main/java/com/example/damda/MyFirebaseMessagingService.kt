package com.example.damda

import android.app.Service
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

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
