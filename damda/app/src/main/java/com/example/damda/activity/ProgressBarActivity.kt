package com.example.damda.activity

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.example.damda.R

class ProgressBarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress_bar)

        var notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
        var notibuilder = NotificationCompat.Builder(this)
        notibuilder.setContentTitle("이미지 업로드")
            .setContentText("업로드 중")
            .setSmallIcon(R.drawable.push_icon)

    }
}