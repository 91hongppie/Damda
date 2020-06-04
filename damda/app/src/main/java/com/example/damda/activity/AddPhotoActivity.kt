package com.example.damda.activity

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.damda.GlobalApplication
import com.example.damda.ImageUpload
import com.example.damda.R
import kotlinx.android.synthetic.main.activity_add_photo.*
import java.io.*
import kotlin.collections.ArrayList


class AddPhotoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)
        setSupportActionBar(findViewById(R.id.toolbar))
        val actionBar = supportActionBar
        actionBar?.setDisplayShowCustomEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        upload_photo_button.isClickable = false
        val pickImage: Button = findViewById(R.id.select_photo_button)

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

            val imageUris = arrayListOf<Uri>()
            val images = arrayListOf<File>()
            val paths = arrayListOf<String>()
            val ids = arrayListOf<String>()
            val clipdata: ClipData? = data?.clipData

            if (clipdata != null) {
                for (i in 0 until clipdata.itemCount) {
                    val imageUri: Uri = clipdata.getItemAt(i).uri
                    val idPath = getFilePath(imageUri)
                    if (idPath != null) {
                        imageUris.add(imageUri)
                        ids.add(idPath[0])
                        paths.add(idPath!![1])
                        val image = File(idPath[1])
                        Log.d("File path", "result : ${idPath[1]}")
                        try {
                            images.add(image)
                        } catch (e: FileNotFoundException) {
                            e.printStackTrace()
                        }
                    }
                }
                Log.d("Image arraylist", "size: ${images.size}")
            } else {
                val imageUri: Uri? = data?.data
                val idPath = getFilePath(imageUri!!)
                if (idPath != null) {
                    imageUris.add(imageUri)
                    paths.add(idPath!![1])
                    val image = File(idPath[1])
                    try {
                        images.add(image)

                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                }
            }

            upload_photo_button.isClickable = true
            upload_photo_button.background = getDrawable(R.color.enableButton)

            setImageView(imageUris)
            select_photo_button.text = "다시 선택"

            upload_photo_button.setOnClickListener {
                if (ActivityCompat.checkSelfPermission(
                        this@AddPhotoActivity,
                        Manifest.permission.INTERNET
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this@AddPhotoActivity,
                        arrayOf(Manifest.permission.INTERNET),
                        100
                    )
                }

                val uploadIntent = Intent(this, ImageUpload::class.java)
                uploadIntent.putExtra("paths", paths)
                uploadIntent.putExtra("ids", ids)
//                startService(uploadIntent)
                ImageUpload().enqueueWork(this, uploadIntent)

                var intent = Intent(this, MainActivity::class.java)
                intent.putExtra("before", "AddPhoto")
                startActivity(intent)
                finishAffinity()
            }
        }
    }


    fun getFilePath(imageUri: Uri): ArrayList<String>? {
        var result = arrayListOf<String>()
        val imageProjection =
            arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA)
        val selection = "${MediaStore.Images.Media.DATA} NOT LIKE ?"
        val selectionArgs = arrayOf(
            "%damda%"
        )
        val cursor: Cursor? = contentResolver.query(imageUri, imageProjection, selection, selectionArgs, null)

        if (cursor == null) {
            return null
        } else {
            cursor.moveToFirst()
            val imageIdIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val imageDataIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
            result.add(cursor.getString(imageIdIndex))
            result.add(cursor.getString(imageDataIndex))
            cursor.close()
        }

        return result
    }

    fun setImageView(imageUris: ArrayList<Uri>) {
        addphoto_image.setImageURI(imageUris[0])
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
