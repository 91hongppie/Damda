package com.example.damda

import android.content.Context
import android.content.Intent
import android.media.ExifInterface
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.JobIntentService
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
        const val JOB_ID = 1001
    }
    val MEDIA_TYPE_IMAGE = MediaType.parse("image/*")

    val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
        .addFormDataPart("user_id", "${GlobalApplication.prefs.user_id}")
    val jwt = GlobalApplication.prefs.token

    fun enqueueWork(context: Context, work: Intent){
        enqueueWork(context, ImageUpload::class.java, JOB_ID, work)
    }
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG,"이미지 업로드 시작하자")
    }
    override fun onHandleWork(intent: Intent) {
        val url = URL(GlobalApplication.prefs.damdaServer+"/api/albums/addphoto/")
        var i = 0
        val paths = intent.getStringArrayListExtra("paths")!!
        for (path in paths) {
            val image = File(path)
            uploadImage(url, image, path, i)
            Thread.sleep(500)
        }
        onDestroy()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"끝끝끝")
    }
    fun uploadImage(url : URL, image: File, path: String, i: Int) {
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

}