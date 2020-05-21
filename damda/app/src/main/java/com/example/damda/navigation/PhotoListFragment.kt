package com.example.damda.navigation

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.Context.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.damda.GlobalApplication
import com.example.damda.navigation.adapter.PhotoAdapter
import com.example.damda.navigation.model.Photos
import com.example.damda.R
import com.example.damda.URLtoBitmapTask
import com.example.damda.navigation.model.Album
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_photo_list.view.*
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

class PhotoListFragment : Fragment() {
    private val STORAGE_PERMISSION_CODE: Int = 1000
    var result = emptyArray<Photos>()
    var album: Album? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_photo_list, container, false)
        album = arguments?.getParcelable<Album>("album")
        var url = URL("http://10.0.2.2:8000/api/albums/photo/${album?.id}/")
        val jwt = GlobalApplication.prefs.token
        val request = Request.Builder().url(url).addHeader("Authorization", "JWT $jwt")
            .build()
        val client = OkHttpClient()

        view.rv_photo?.adapter = PhotoAdapter(result)
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException){
                println("Failed to execute request!")
            }
            override fun onResponse(call: Call, response: Response) {
                val body = response.body()?.string()
                val gson = GsonBuilder().create()
                var photoList = emptyArray<Photos>()
                result = gson.fromJson(body, photoList::class.java)
                activity?.runOnUiThread(Runnable {
                    view.rv_photo?.adapter = PhotoAdapter(result)
                })
            }
        })
        view.albumTitle?.text = album?.title
        view.rv_photo?.layoutManager = GridLayoutManager(activity, 3)
        view.saveAlbum.setOnClickListener{
/*            var image_task: URLtoBitmapTask = URLtoBitmapTask()
            for(photo in result){
                image_task = URLtoBitmapTask().apply {
                    imgurl = URL("http://10.0.2.2:8000${photo.pic_name}")
                }
                var bitmap: Bitmap = image_task.execute().get()
                Log.e("ebtest",""+bitmap)
          }
*/
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (ContextCompat.checkSelfPermission(this.context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_DENIED){
                        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
                    }
                    else{
                        startDownloading()
                    }

                }
                else{
                    startDownloading()
                }
        }
        return view
    }
    private  fun startDownloading() {
        for(photo in result){
            val imgurl = "http://10.0.2.2:8000${photo.pic_name}"
            val request = DownloadManager.Request(Uri.parse(imgurl))
            val jwt = GlobalApplication.prefs.token
            request.addRequestHeader("Authorization", "JWT $jwt")
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            request.setTitle("Download")
            request.setDescription("the file is downloading...")
            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DCIM, "damda/${album?.title}/${System.currentTimeMillis()}")
            val manager = context?.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            STORAGE_PERMISSION_CODE -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startDownloading()
                }
                else{
                    Toast.makeText(this.context, "Permission denied", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


}