package com.ebgbs.damda.activity

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
import com.ebgbs.damda.GlobalApplication
import com.ebgbs.damda.GlobalApplication.Companion.prefs
import com.ebgbs.damda.LoadingDialog
import com.ebgbs.damda.R
import com.ebgbs.damda.navigation.model.Mission
import com.ebgbs.damda.retrofit.service.MissionService
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
    private var photo_id = 0
    private lateinit var loadingDialog: LoadingDialog
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
                var intent = Intent(this@MissionAddPhotoActivity, ImagePickerOneActivity::class.java)
                intent.putExtra("before", "mission")
                startActivityForResult(intent, 1)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == 1) {
            Log.v("asdf", data?.getStringExtra("path").toString())
            var imageUri : Uri? = null
            var image : File? = null
            var path : String? = null

            imageUri = data?.getParcelableExtra("uri") as Uri
            path = data?.getStringExtra("path")
            photo_id = data?.getIntExtra("id", 0)
            image = File(path)

            upload_photo_button.isClickable = true
            upload_photo_button.background = getDrawable(R.color.enableButton)

            setImageView(imageUri)
            select_photo_button.text = "다시 선택"

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
                loadingDialog = LoadingDialog(this)
                loadingDialog.show()
                val url = URL(prefs.damdaServer + "/api/albums/addphoto/")

                uploadImage(url, image, path)

            }
        }
    }

    fun getFilePath(imageUri: Uri): String? {
        var result: String?
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

    fun setImageView(imageUri: Uri) {
        addphoto_image.setImageURI(imageUri)
    }

    fun uploadImage(url: URL, image: File, path: String) {
        try {
            val MEDIA_TYPE_IMAGE = MediaType.parse("image/*")

            val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id", "${prefs.user_id}")

                val exif = ExifInterface(path)

                var fileDatetime: String = if (exif.getAttribute(ExifInterface.TAG_DATETIME) != null) {
                    val datetime = exif.getAttribute(ExifInterface.TAG_DATETIME)
                    val datetime_split = datetime.split(" ")
                    var date = datetime_split[0].split(":").joinToString("")
                    var time = datetime_split[1].split(":").joinToString("")
                    "${date}_${time}_${photo_id}"
                } else {
                    val datetime = Calendar.getInstance(TimeZone.getDefault(), Locale.KOREA).time
                    val today = SimpleDateFormat("yyyyMMdd_HHmmss").format(datetime)
                    today + "_$photo_id"
                }

                Log.d("TIME", "$fileDatetime")
                Log.d("USER", "id: ${prefs.user_id}")
                requestBody.addFormDataPart(
                    "uploadImages",
                    "damda_${fileDatetime}_${prefs.user_id}",
                    RequestBody.create(MEDIA_TYPE_IMAGE, image)
                )


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
            loadingDialog.dismiss()

        }

        override fun onResponse(call: okhttp3.Call, response: Response) {
            val status = response.code()
            Log.d("server response", "$status")
            loadingDialog.dismiss()

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
                MissionService::class.java
            )
            missionService.changeMission("JWT $jwt", user_id, period, mission_title!!, mission_id)
                .enqueue(object :
                    retrofit2.Callback<Mission> {
                    override fun onFailure(call: Call<Mission>, t: Throwable) {
                        var dialog = AlertDialog.Builder(this@MissionAddPhotoActivity)
                        dialog.setTitle("에러")
                        dialog.setMessage("호출실패했습니다.")
                        dialog.show()
                    }

                    override fun onResponse(
                        call: Call<Mission>,
                        response: retrofit2.Response<Mission>
                    ) {
                        val missions = response.body()
                    }
                })
            setResult(1, intent)
            finish()
        }
    }
}
