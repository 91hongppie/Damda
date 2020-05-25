package com.example.damda.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.damda.R
import kotlinx.android.synthetic.main.activity_add_photo.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddPhotoActivity : AppCompatActivity() {
    private var PICK_IMAGE_FROM_ALBUM = 10
    private var photoUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        addphoto_btn_upload.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this@AddPhotoActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@AddPhotoActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 100)
                return@setOnClickListener
            }

            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_FROM_ALBUM) {
            if (resultCode == Activity.RESULT_OK) {
                // This is path to the selected image
                var images : ArrayList<Uri> = arrayListOf()
                var clipdata = data?.clipData
                if (clipdata != null) {
                    for (i in 0 until clipdata.itemCount) {
                        var imageUri = clipdata.getItemAt(i).uri
                        images.add(imageUri)
                        Log.d("images", "list: $images")
                    }
                } else {
                    var imageUri : Uri? = data?.data
                    if (imageUri != null) {
                        images.add(imageUri)
                    }
                }

                class ThreadClass : Thread() {
                    override fun run() {
                        for (image in images) {
                            runOnUiThread { addphoto_image.setImageURI(image) }
                        }

                        try {
                            sleep(3000)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                    }
                }

                val tc = ThreadClass()
                tc.start()
            }else {
                // Exit the addPhotoActivity if you leave the album without selecting it
                finish()
            }
        }
    }
    fun contentUpload() {
        // Make file name

        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "damda_" + timestamp + "_.jpg"

//        var storageRef = storage?.reference?.child("images")?.child(imageFileName)
//
//        // File upload
//        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
//            Toast.makeText(this, getString(R.string.upload_success), Toast.LENGTH_LONG).show()
//
//        }
    }
}
