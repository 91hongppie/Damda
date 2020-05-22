package com.example.damda.navigation

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.Context.*
import android.content.pm.PackageManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.SharedElementCallback
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.damda.GlobalApplication
import com.example.damda.navigation.adapter.PhotoAdapter
import com.example.damda.navigation.model.Photos
import com.example.damda.R
import com.example.damda.activity.MainActivity
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
    var photoList = emptyArray<Photos>()
    var album: Album? = null
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val context = activity as MainActivity
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_photo_list, container, false)
        album = arguments?.getParcelable<Album>("album")
        var url = URL("http://10.0.2.2:8000/api/albums/photo/${album?.id}/")
        val jwt = GlobalApplication.prefs.token
        val request = Request.Builder().url(url).addHeader("Authorization", "JWT $jwt")
            .build()
        val client = OkHttpClient()
        view.rv_photo?.adapter =
            PhotoAdapter(photoList) { photo ->
                var bundle = Bundle()
                bundle.putSerializable("photoList", photoList)
                bundle.putInt("position", photoList.indexOf(photo))
                var fragment = PhotoDetailFragment()
                fragment.arguments = bundle
                context.replaceFragment(fragment)

            }
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute request!")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body()?.string()
                val gson = GsonBuilder().create()
                photoList= gson.fromJson(body, Array<Photos>::class.java)
                activity?.runOnUiThread(Runnable {
                    view.rv_photo?.adapter =
                        PhotoAdapter(photoList) { photo ->
                            var bundle = Bundle()
                            bundle.putSerializable("photoList", photoList)
                            bundle.putInt("position", photoList.indexOf(photo))
                            var fragment = PhotoDetailFragment()
                            fragment.arguments = bundle
                            context.replaceFragment(fragment)
                        }
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
        prepareTransitions()
        postponeEnterTransition()
        return view
    }
    private  fun startDownloading() {
        for(photo in photoList){
            val imgurl = "http://10.0.2.2:8000${photo.pic_name}"
            val request = DownloadManager.Request(Uri.parse(imgurl))
            val jwt = GlobalApplication.prefs.token
            request.addRequestHeader("Authorization", "JWT $jwt")
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            request.setTitle("${photo.title}")
            request.setDescription("앨범 다운로드 중")
            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DCIM, "damda/${album?.title}/${photo.title}")
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
                    Toast.makeText(this.context, "필수 권한이 거부되었습니다.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scrollToPosition()
    }

    private fun scrollToPosition() {
        view?.rv_photo!!.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(v: View,
                                        left: Int,
                                        top: Int,
                                        right: Int,
                                        bottom: Int,
                                        oldLeft: Int,
                                        oldTop: Int,
                                        oldRight: Int,
                                        oldBottom: Int) {
                view?.rv_photo!!.removeOnLayoutChangeListener(this)
                val layoutManager = view?.rv_photo!!.layoutManager
                val viewAtPosition = layoutManager!!.findViewByPosition(PhotoListFragment.currentPosition)
                // Scroll to position if the view for the current position is null (not currently part of
                // layout manager children), or it's not completely visible.
                if (viewAtPosition == null || layoutManager
                        .isViewPartiallyVisible(viewAtPosition, false, true)) {
                    view?.rv_photo!!.post { layoutManager.scrollToPosition(PhotoListFragment.currentPosition) }
                }
            }
        })
    }
    private fun prepareTransitions() {
//        exitTransition.TransitionInflater.from(context).inflateTransition(R.transition.grid_exit_transition)


        // A similar mapping is set at the ImagePagerFragment with a setEnterSharedElementCallback.
        setExitSharedElementCallback(
            object : SharedElementCallback() {
                override fun onMapSharedElements(names: List<String>, sharedElements: MutableMap<String, View>) {
                    // Locate the ViewHolder for the clicked position.
                    val selectedViewHolder = view?.rv_photo?.findViewHolderForAdapterPosition(
                        currentPosition)
                        ?: return

                    // Map the first shared element name to the child ImageView.
                    sharedElements[names[0]] = selectedViewHolder.itemView.findViewById(R.id.ivFullscreenImage)
                }
            })
    }
    companion object {
        /**
         * Holds the current image position to be shared between the grid and the pager fragments. This
         * position updated when a grid item is clicked, or when paging the pager.
         *
         * In this demo app, the position always points to an image index at the [ ] class.
         */
        var currentPosition = 0
        private const val KEY_CURRENT_POSITION = "com.google.samples.gridtopager.key.currentPosition"
    }
 }