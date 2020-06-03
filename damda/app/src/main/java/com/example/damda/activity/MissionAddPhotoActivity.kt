package com.example.damda.activity

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.damda.GlobalApplication
import com.example.damda.GlobalApplication.Companion.prefs
import com.example.damda.R
import com.example.damda.navigation.adapter.MissionAdapter
import com.example.damda.navigation.model.Mission
import com.example.damda.retrofit.service.MissionService
import kotlinx.android.synthetic.main.activity_add_photo.*
import okhttp3.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.net.URL
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*


class MissionAddPhotoActivity : AppCompatActivity() {
    private var mission_title: String? = null
    private var mission_id = 0
    private var period = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mission_title = intent.getStringExtra("mission_title")
        period = intent.getIntExtra("period", 0)
        mission_id = intent.getIntExtra("mission_id", 0)
        setContentView(R.layout.activity_add_photo)
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        upload_photo_button.isClickable = false
        val pickImage: Button = findViewById(R.id.select_photo_button)

        pickImage.setOnClickListener(object : OnClickListener {
            override fun onClick(view: View?) {
                if (ActivityCompat.checkSelfPermission(
                        this@MissionAddPhotoActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this@MissionAddPhotoActivity,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        100
                    )
                    return
                }
                val intent = Intent(Intent.ACTION_PICK)
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.type = "image/*"
                startActivityForResult(intent, 1)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {

            var imageUris = arrayListOf<Uri>()
            var images = arrayListOf<File>()
            var paths = arrayListOf<String>()
            val clipdata: ClipData? = data?.clipData

            if (clipdata != null) {
                for (i in 0 until clipdata.itemCount) {
                    var imageUri: Uri = clipdata.getItemAt(i).uri
                    var path = getFilePath(imageUri)
                    imageUris.add(imageUri)
                    paths.add(path!!)
                    val image = File(path)
                    Log.d("File path", "result : $path")
                    try {
                        images.add(image)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                }
                Log.d("Image arraylist", "size: ${images.size}")
            } else {
                var imageUri: Uri? = data?.data
                var path = getFilePath(imageUri!!)
                imageUris.add(imageUri)
                paths.add(path!!)
                val image = File(path)
                try {
                    images.add(image)

                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }

            upload_photo_button.isClickable = true
            upload_photo_button.background = getDrawable(R.color.enableButton)

            setImageView(imageUris)
            select_photo_button.text = "선택됨"
            select_photo_button.isClickable = false
            select_photo_button.background = getDrawable(R.color.disableButton)

            upload_photo_button.setOnClickListener {
                if (ActivityCompat.checkSelfPermission(
                        this@MissionAddPhotoActivity,
                        Manifest.permission.INTERNET
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this@MissionAddPhotoActivity,
                        arrayOf(Manifest.permission.INTERNET),
                        100
                    )
                }

                val url = URL(prefs.damdaServer+"/api/albums/addphoto/")

                uploadImage(url, images, paths)

            }
        }
    }

    fun getFilePath(imageUri: Uri): String? {
        var result : String?
        val cursor: Cursor? = contentResolver.query(imageUri, null, null, null, null)

        if (cursor == null) {
            result = imageUri.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }

        return result
    }

    fun setImageView(imageUris: ArrayList<Uri>) {
        addphoto_image.setImageURI(imageUris[0])
    }

    fun uploadImage(url : URL, images: ArrayList<File>, paths: ArrayList<String>) {
        try {
            val MEDIA_TYPE_IMAGE = MediaType.parse("image/*")

            val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id", "${prefs.user_id}")

            for (i in 0 until images.size) {
                val exif = ExifInterface(paths[i])
                var fileDatetime : String

                fileDatetime = if (exif.getAttribute(ExifInterface.TAG_DATETIME) != null) {
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

                Log.d("TIME", "$fileDatetime")
                Log.d("USER", "id: ${prefs.user_id}")
                requestBody.addFormDataPart("uploadImages", "damda_${prefs.user_id}_${fileDatetime}", RequestBody.create(MEDIA_TYPE_IMAGE, images[i]))

            }

            val body = requestBody.build()
            val jwt = GlobalApplication.prefs.token
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

            val result = response.body()?.string()
            Log.d("Sever response", "result: $result")
            println(mission_title)
            println(period)
            var retrofit = Retrofit.Builder()
                .baseUrl(GlobalApplication.prefs.damdaServer)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val jwt = GlobalApplication.prefs.token
            val user_id = GlobalApplication.prefs.user_id.toString()
            var missionService: MissionService = retrofit.create(
                MissionService::class.java)
            missionService.changeMission("JWT $jwt", user_id, period, mission_title!!, mission_id).enqueue(object:
                retrofit2.Callback<Mission> {
                override fun onFailure(call: Call<Mission>, t: Throwable) {
                    var dialog = AlertDialog.Builder(this@MissionAddPhotoActivity)
                    dialog.setTitle("에러")
                    dialog.setMessage("호출실패했습니다.")
                    dialog.show()
                }
                override fun onResponse(call: Call<Mission>, response: retrofit2.Response<Mission>) {
                    val missions = response.body()
                }
            })
            setResult(1, intent)
            finish()
        }
    }
}
