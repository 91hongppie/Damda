package com.example.damda.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.damda.GlobalApplication
import com.example.damda.GlobalApplication.Companion.prefs
import com.example.damda.LoadingDialog
import com.example.damda.R
import com.example.damda.retrofit.model.Face
import com.example.damda.retrofit.service.AlbumsService
import com.jakewharton.rxbinding2.view.clickable
import kotlinx.android.synthetic.main.activity_cropper.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class CropperActivity : AppCompatActivity() {
    val STORAGE_PERMISSION = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    val FLAG_PERM_STORAGE = 99
    val FLAG_REQ_STORAGE = 102
    var imagePreview: ImageView? = null
    val token = "JWT " + prefs.token.toString()
    val family_id = prefs.family_id.toString()
    var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cropper)
        cropper = this
        setSupportActionBar(findViewById(R.id.toolbar))
        val actionBar = supportActionBar
        actionBar?.setDisplayShowCustomEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        if (checkPermission(STORAGE_PERMISSION, FLAG_PERM_STORAGE)) {
            setViews()
        }
        var retrofit = Retrofit.Builder()
            .baseUrl(prefs.damdaServer)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        var albumsService: AlbumsService = retrofit.create(
            AlbumsService::class.java
        )
        imagePreview = findViewById<ImageView>(R.id.preview) as ImageView
        preview.setOnClickListener {
            if (checkPermission(STORAGE_PERMISSION, FLAG_PERM_STORAGE)) {
                setViews()
            }
        }
        val spinner: Spinner = findViewById(R.id.family_name)
        if (intent.getStringExtra("before") == "addFamily") {
            family_name.visibility = View.GONE
        } else {
            my.visibility = View.GONE
            var nameArray = R.array.familyNameM
            if (prefs.gender == 2) {
                nameArray = R.array.familyNameF
            }
            ArrayAdapter.createFromResource(
                this,
                nameArray,
                R.layout.list_item_gender
            ).also { adapter ->
                adapter.setDropDownViewResource(R.layout.list_item_gender)
                spinner.adapter = adapter
            }

        }


        save_member.setOnClickListener {
            var loadingDialog = LoadingDialog(this)
            loadingDialog.show()
            var albumCall = "나"
            if (intent.getStringExtra("before") != "addFamily") {
                albumCall = spinner.selectedItem.toString()
            }
            save_member.clickable().accept(false)
            val file = File(getPath(uri!!))
            val requestFile: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), file)
            val exif = ExifInterface(getPath(uri!!))
            var fileDatetime : String = if (exif.getAttribute(ExifInterface.TAG_DATETIME) != null) {
                val datetime = exif.getAttribute(ExifInterface.TAG_DATETIME)
                val datetime_split = datetime.split(" ")
                var date = datetime_split[0].split(":").joinToString("")
                var time = datetime_split[1].split(":").joinToString("")
                "${date}_${time}_0"
            } else {
                val datetime = Calendar.getInstance(TimeZone.getDefault(), Locale.KOREA).time
                val today = SimpleDateFormat("yyyyMMdd_HHmmss").format(datetime)
                today + "_0"
            }

            var filename = "damda_${fileDatetime}_${GlobalApplication.prefs.user_id}.jpg"
            val body =
                MultipartBody.Part.createFormData("image", filename, requestFile)
            albumsService.updateFace(
                token,
                family_id,
                prefs.user_id!!,
                albumCall,
                prefs.user_id!!.toInt(),
                body
            ).enqueue(object :
                Callback<Face> {
                override fun onFailure(call: Call<Face>, t: Throwable) {
                    Log.e("LOGIN",t.message)
                    loadingDialog.dismiss()
                    var dialog = AlertDialog.Builder(this@CropperActivity)
                    dialog.setTitle("에러")
                    dialog.setMessage("호출실패했습니다.")
                    dialog.show()
                }

                override fun onResponse(call: Call<Face>, response: Response<Face>) {
                    loadingDialog.dismiss()
                    if (response.code() == 202) {
                        Toast.makeText(
                            this@CropperActivity,
                            response.body()?.message,
                            Toast.LENGTH_LONG
                        ).show()
                        save_member.clickable().accept(true)
                    } else {
                        val builder = AlertDialog.Builder(this@CropperActivity)
                        builder.setTitle("앨범").setMessage("앨범 생성이 완료되었습니다.")
                        builder.setPositiveButton(
                            "확인"
                        ) { dialog, id ->
                            if (intent.getStringExtra("before") == "addFamily") {
                                prefs.my_album = true
                            }
                            var intent = Intent(this@CropperActivity, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                            finish()
                        }

                        val alertDialog = builder.create()
                        alertDialog.show()
                    }
                }
            })
        }
    }

    fun setViews() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra("crop", "true")
        startActivityForResult(Intent.createChooser(intent, "사진 선택"), FLAG_REQ_STORAGE)
    }

    fun checkPermission(permissions: Array<out String>, flag: Int): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(this, permissions, flag)
                    return false
                }
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            FLAG_PERM_STORAGE -> {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "저장소 권환을 승인해야지만 앱을 사용할 수 있습니다.", Toast.LENGTH_LONG)
                            .show()
                        return
                    }
                }
                setViews()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                FLAG_REQ_STORAGE -> {
                    uri = data?.data
                    imagePreview?.setImageURI(uri)
                }
            }
        }
    }

    private fun getPath(uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = managedQuery(uri, projection, null, null, null)
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        var cropper = CropperActivity()
    }

}
