package com.example.damda.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.damda.GlobalApplication
import com.example.damda.GlobalApplication.Companion.prefs
import com.example.damda.R
import com.example.damda.retrofit.model.Face
import com.example.damda.retrofit.service.AlbumsService
import com.theartofdev.edmodo.cropper.CropImage
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


class CropperActivity : AppCompatActivity() {
    val PIC_CROP_REQUEST = 1
    val PICK_IMAGE_REQUEST = 2
    var imagePreview: ImageView?=null
    var uri = ""
    val token = "JWT " + GlobalApplication.prefs.token.toString()
    val family_id = GlobalApplication.prefs.family_id.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cropper)
        setSupportActionBar(findViewById(R.id.toolbar))
        val actionBar = supportActionBar
        actionBar?.setDisplayShowCustomEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        var retrofit = Retrofit.Builder()
            .baseUrl(prefs.damdaServer)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        var albumsService: AlbumsService = retrofit.create(
            AlbumsService::class.java)
        imagePreview = findViewById<ImageView>(R.id.preview) as ImageView
        ImagePicker()
        preview.setOnClickListener {
            ImagePicker()
        }
        save_member.setOnClickListener {
            val directory = getApplicationContext().cacheDir
            val arr = uri.split("/")
            val file_name = arr[arr.size - 1]
            val file = File(directory, file_name)
            val requestFile: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), file)
            val body =
                MultipartBody.Part.createFormData("image", file.getName(), requestFile)
            albumsService.updateFace(token, family_id, member_name.text.toString(), body).enqueue(object:
                Callback<Face> {
                override fun onFailure(call: Call<Face>, t: Throwable) {
                    Log.e("LOGIN",t.message)
                    var dialog = AlertDialog.Builder(this@CropperActivity)
                    dialog.setTitle("에러")
                    dialog.setMessage("호출실패했습니다.")
                    dialog.show()
                }
                override fun onResponse(call: Call<Face>, response: Response<Face>) {
                    if (response.code() == 202) {
                        Toast.makeText(this@CropperActivity, response.body()?.message, Toast.LENGTH_LONG).show()
                    } else {
                    val builder = AlertDialog.Builder(this@CropperActivity)
                    builder.setTitle("앨범").setMessage("앨범 생성이 완료되었습니다.")
                    builder.setPositiveButton(
                        "확인"
                    ) { dialog, id ->
                        var intent = Intent(this@CropperActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    }

                    val alertDialog = builder.create()
                    alertDialog.show()}
                }
            })
        }
    }
    private fun ImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
//        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra("crop", "true")
        startActivityForResult(Intent.createChooser(intent, "사진 선택"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }
            CropImage.activity(data.data!!).start(this);
        }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                Log.v("uri", result.getUri().toString())
                uri = result.getUri().toString()
                imagePreview?.setImageURI(result.getUri())
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.getError()
                Toast.makeText(this@CropperActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        }

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
}
