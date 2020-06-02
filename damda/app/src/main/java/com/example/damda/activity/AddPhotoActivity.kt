package com.example.damda.activity

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.media.Image
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.fragment.app.FragmentManager
import com.example.damda.MySharedPreferences
import com.example.damda.GlobalApplication
import com.example.damda.GlobalApplication.Companion.prefs
import com.example.damda.ImageUpload
import com.example.damda.R
import com.example.damda.navigation.PhotoListFragment
import com.jakewharton.rxbinding2.view.clickable
import kotlinx.android.synthetic.main.activity_add_photo.*
import okhttp3.*
import org.jetbrains.anko.contentView
import java.io.*
import java.net.URL
import java.net.UnknownHostException
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.Exception
import kotlin.collections.ArrayList


class AddPhotoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)
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
                startService(uploadIntent)
//                ImageUpload().enqueueWork(this, uploadIntent)
                var intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
        }
    }

//    private class UploadImagesTask : AsyncTask<ArrayList<Uri>, Int, Long>() {
//
//        // Do the long-running work in here
//        override fun doInBackground(vararg urls: URL): Long? {
//            val count: Float = urls.size.toFloat()
//            var totalSize: Long = 0
//            urls.forEachIndexed { index, url ->
////                totalSize += Downloader.downloadFile(url)
//                publishProgress((index / count * 100).toInt())
//                // Escape early if cancel() is called
//                if (isCancelled) return totalSize
//            }
//            return totalSize
//        }
//
//        // This is called each time you call publishProgress()
//        override fun onProgressUpdate(vararg progress: Int?) {
//            setProgressPercent(progress.firstOrNull() ?: 0)
//        }
//
//        // This is called when doInBackground() is finished
//        override fun onPostExecute(result: Long?) {
//            showNotification("Downloaded $result bytes")
//        }
//    }


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


}
