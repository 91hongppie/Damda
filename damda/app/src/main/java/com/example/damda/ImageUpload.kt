package com.example.damda

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.ExifInterface
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.damda.activity.MainActivity
import okhttp3.*
import java.io.File
import java.io.IOException
import java.net.URL
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*

class ImageUpload : JobIntentService() {
    companion object {
        const val TAG = "ImageUpload"
        const val JOB_ID = 1004
    }
    val MEDIA_TYPE_IMAGE = MediaType.parse("image/*")

    val jwt = GlobalApplication.prefs.token

    fun enqueueWork(context: Context, work: Intent){
        enqueueWork(context, ImageUpload::class.java, JOB_ID, work)
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG,"이미지 업로드 시작하자")
    }

    override fun onHandleWork(intent: Intent) {
        var url = URL(GlobalApplication.prefs.damdaServer + "/api/albums/addphoto/")
        if (intent.getStringExtra("before") == "main") {
            url = URL(GlobalApplication.prefs.damdaServer+"/api/albums/autoaddphoto/")
        }
        val paths = intent.getStringArrayListExtra("paths")!!

        for (i in 0 until paths.size) {
            val image = File(paths[i])
            uploadImage(url, image, paths[i], i, paths.size)
            Thread.sleep(500)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val url = URL(GlobalApplication.prefs.damdaServer+"/api/albums/uploadend/")

        val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("user_id", "${GlobalApplication.prefs.user_id}").build()

        val request = Request.Builder().addHeader("Authorization", "JWT $jwt")
            .url(url)
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        val callback = Callback2()

        client.newCall(request).enqueue(callback)

        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var builder = NotificationCompat.Builder(this, "UPLOAD")
            .setSmallIcon(R.drawable.push_icon)
            .setContentTitle("담다")
            .setContentText("업로드 완료")
            .setProgress(0, 0, false)

        notificationManager.notify(12100, builder.build())

        Log.d(TAG,"끝끝끝")
    }
    fun uploadImage(url : URL, image: File, path: String, i: Int, size: Int) {
        val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("user_id", "${GlobalApplication.prefs.user_id}")

        try {
            val exif = ExifInterface(path)
            var fileDatetime : String = if (exif.getAttribute(ExifInterface.TAG_DATETIME) != null) {
                val datetime = exif.getAttribute(ExifInterface.TAG_DATETIME)
                val datetime_split = datetime.split(" ")
                var date = datetime_split[0].split(":").joinToString("")
                var time = datetime_split[1].split(":").joinToString("")
                "${date}_${time}"
            } else {
                val datetime = Calendar.getInstance(TimeZone.getDefault(), Locale.KOREA).time
                val today = SimpleDateFormat("yyyyMMdd_HHmmss").format(datetime)
                today + "_$i"
            }
            requestBody.addFormDataPart("uploadImages", "damda_${GlobalApplication.prefs.user_id}_${fileDatetime}", RequestBody.create(MEDIA_TYPE_IMAGE, image))

            Log.d("사진 순서", ": $i")

            val body = requestBody.build()
            val request = Request.Builder().addHeader("Authorization", "JWT $jwt")
                .url(url)
                .post(body)
                .build()

            val client = OkHttpClient()
            val callback = Callback1()

            client.newCall(request).enqueue(callback)

        } catch (e: UnknownHostException) {
            Log.d("Error", "$e")
        } catch (e: Exception) {
            Log.d("Exception", "$e")
        }

        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var builder = NotificationCompat.Builder(this, "UPLOAD")
            .setSmallIcon(R.drawable.push_icon)
            .setContentTitle("담다")
            .setContentText("업로드 진행중")

        var present = i.toFloat()
        var max_size = size.toFloat()
        var progress =  present.div(max_size).times(100).toInt()
        Log.d("PROGRESS", "i: $i, progress: $progress == ${present / max_size * 100}, size: $size")
        builder.setProgress(100, progress, false)
        notificationManager.notify(12100, builder.build())
    }

    inner class Callback1 : Callback {
        override fun onFailure(call: okhttp3.Call, e: IOException) {
            Log.d("Sever response", "error: $e")

        }

        override fun onResponse(call: okhttp3.Call, response: Response) {
            val status = response.code()
            Log.d("server response", "$status")
        }
    }

    inner class Callback2 : Callback {
        override fun onFailure(call: okhttp3.Call, e: IOException) {
            Log.d("Sever response", "error: $e")

        }

        override fun onResponse(call: okhttp3.Call, response: Response) {
            val status = response.code()
            Log.d("server response", "푸시는 $response")
            Log.d("server response", "푸시도 보냈어요! = $status")
        }
    }

}