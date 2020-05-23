package com.example.damda.activity

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.damda.R
import java.io.FileNotFoundException
import java.io.InputStream


class AddPhotoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        val pickImage: Button = findViewById(R.id.addphoto_btn_upload)

        pickImage.setOnClickListener(object : OnClickListener {
            override fun onClick(view: View?) {
                if (ActivityCompat.checkSelfPermission(
                        this@AddPhotoActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this@AddPhotoActivity,
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

            val imageView: ImageView = findViewById(R.id.addphoto_image)

            var bitmaps: Array<Bitmap> = arrayOf()
            val clipdata: ClipData? = data?.clipData

            if (clipdata != null) {
                for (i in 0 until clipdata.itemCount) {
                    var imageUri: Uri = clipdata.getItemAt(i).uri

                    try {
                        val iss: InputStream? = contentResolver.openInputStream(imageUri)
                        val bitmap: Bitmap = BitmapFactory.decodeStream(iss)
                        bitmaps.plus(bitmap)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                }
                Log.d("Image arraylist", "size: ${bitmaps.size}")
            } else {
                var imageUri: Uri? = data?.data
                try {
                    val iss: InputStream? = contentResolver.openInputStream(imageUri!!)
                    val bitmap: Bitmap = BitmapFactory.decodeStream(iss)
                    bitmaps.plus(bitmap)

                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }

            class ThreadClass : Thread() {
                override fun run() {
                    for (b in bitmaps) runOnUiThread { imageView.setImageBitmap(b) }

                    try {
                        sleep(3000)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }

            val tc = ThreadClass()
            tc.start()
        }
    }
}
