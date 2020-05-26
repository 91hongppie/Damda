package com.example.damda.activity

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.system.Os
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.damda.R
import kotlinx.android.synthetic.main.activity_add_photo.*
import java.io.File
import java.io.InputStream
import java.net.URI
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.*

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

            val photoPickerIntent = Intent(Intent.ACTION_GET_CONTENT)
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
                val images : ArrayList<Uri> = arrayListOf()
                val clipdata : ClipData? = data?.clipData
                if (clipdata != null) {
                    for (i in 0 until clipdata.itemCount) {
                        val imageUri = clipdata.getItemAt(i).uri
                        images.add(imageUri)
//                        val image: String = imageUri.path!!
//                        val exif = ExifInterface(image)
//
//                        val attributes = arrayOf(
//                            ExifInterface.TAG_DATETIME,
//                            ExifInterface.TAG_DATETIME_DIGITIZED,
//                            ExifInterface.TAG_GPS_ALTITUDE,
//                            ExifInterface.TAG_GPS_ALTITUDE_REF,
//                            ExifInterface.TAG_GPS_DATESTAMP,
//                            ExifInterface.TAG_GPS_LATITUDE,
//                            ExifInterface.TAG_GPS_LATITUDE_REF,
//                            ExifInterface.TAG_GPS_LONGITUDE,
//                            ExifInterface.TAG_GPS_LONGITUDE_REF,
//                            ExifInterface.TAG_GPS_PROCESSING_METHOD,
//                            ExifInterface.TAG_GPS_TIMESTAMP,
//                            ExifInterface.TAG_MAKE,
//                            ExifInterface.TAG_MODEL,
//                            ExifInterface.TAG_ORIENTATION)
//
//                        for (i in attributes.indices) {
//                            val value = exif.getAttribute(attributes[i])
//                            if (value != null)
//                                Log.d("EXIF", "value: $value")
//                        }

                        Log.d("images", "list: $images")
                    }
                } else {
                    var imageUri : Uri? = data?.data
                    if (imageUri != null) {
                        Log.d("Image path", "URI: $imageUri")
                        Log.d("Image path", "absolute: ${imageUri.encodedPath}")
                        images.add(imageUri)

//                        val exif = ExifInterface(image)
//
//                        val attributes = arrayOf(
//                            ExifInterface.TAG_DATETIME,
//                            ExifInterface.TAG_DATETIME_DIGITIZED,
//                            ExifInterface.TAG_GPS_ALTITUDE,
//                            ExifInterface.TAG_GPS_ALTITUDE_REF,
//                            ExifInterface.TAG_GPS_DATESTAMP,
//                            ExifInterface.TAG_GPS_LATITUDE,
//                            ExifInterface.TAG_GPS_LATITUDE_REF,
//                            ExifInterface.TAG_GPS_LONGITUDE,
//                            ExifInterface.TAG_GPS_LONGITUDE_REF,
//                            ExifInterface.TAG_GPS_PROCESSING_METHOD,
//                            ExifInterface.TAG_GPS_TIMESTAMP,
//                            ExifInterface.TAG_MAKE,
//                            ExifInterface.TAG_MODEL,
//                            ExifInterface.TAG_ORIENTATION)
//
//                        for (i in attributes.indices) {
//                            val value = exif.getAttribute(attributes[i])
//                            if (value != null)
//                                Log.d("EXIF", "value: $value")
//                        }
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

    private fun getAbsolutePath(uri: Uri) : String {
        var path = filesDir.absolutePath
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = managedQuery(uri, projection, null, null, null)
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
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

    companion object {

        private const val TAG = "AddPhotoActivity"
    }
}
