package com.example.damda.activity

import android.app.AlertDialog
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.damda.GlobalApplication
import com.example.damda.R
import com.example.damda.retrofit.model.Message
import com.example.damda.retrofit.service.VideoService
import kotlinx.android.synthetic.main.activity_add_video.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class AddVideoActivity : AppCompatActivity() {
    val token = "JWT " + GlobalApplication.prefs.token.toString()
    val family_id = GlobalApplication.prefs.family_id.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_video)
        var uri: Uri? = intent.getParcelableExtra("uri")
        var retrofit = Retrofit.Builder()
            .baseUrl(GlobalApplication.prefs.damdaServer)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        var videoService: VideoService = retrofit.create(
            VideoService::class.java)
        add_video.setVideoPath(uri.toString())
        add_video.start()
        video_save.setOnClickListener {
            val directory = getApplicationContext().cacheDir
            val file = File(getRealPathFromMediaData(uri))
            val requestFile: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), file)
            val body =
                MultipartBody.Part.createFormData("image", file.getName(), requestFile)
            videoService.updateVideo(token, family_id, video_title.text.toString(), body).enqueue(object:
                Callback<Message> {
                override fun onFailure(call: Call<Message>, t: Throwable) {
                    Log.e("LOGIN",t.message)
                    var dialog = AlertDialog.Builder(this@AddVideoActivity)
                    dialog.setTitle("에러")
                    dialog.setMessage("호출실패했습니다.")
                    dialog.show()
                }
                override fun onResponse(call: Call<Message>, response: Response<Message>) {
                    if (response.code() == 202) {
                        Toast.makeText(this@AddVideoActivity, response.body()?.message, Toast.LENGTH_LONG).show()
                    } else {
                        val builder = AlertDialog.Builder(this@AddVideoActivity)
                        builder.setTitle("앨범").setMessage("앨범 생성이 완료되었습니다.")
                        builder.setPositiveButton(
                            "확인"
                        ) { dialog, id ->
                            var intent = Intent(this@AddVideoActivity, MainActivity::class.java)
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
    private fun getRealPathFromMediaData(data: Uri?): String {
        data ?: return ""

        var cursor: Cursor? = null
        try {
            cursor = contentResolver.query(
                data,
                arrayOf(MediaStore.Video.Media.DATA),
                null, null, null
            )

            val col = cursor!!.getColumnIndex(MediaStore.Video.Media.DATA)
            cursor.moveToFirst()

            return cursor.getString(col)
        } finally {
            cursor?.close()
        }
    }
}
