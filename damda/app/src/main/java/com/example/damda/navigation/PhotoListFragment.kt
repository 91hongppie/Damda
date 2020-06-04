package com.example.damda.navigation

import android.Manifest
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.SharedElementCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import com.example.damda.GlobalApplication
import com.example.damda.GlobalApplication.Companion.prefs
import com.example.damda.R
import com.example.damda.URLtoBitmapTask
import com.example.damda.activity.AddPhotoActivity
import com.example.damda.activity.MainActivity
import com.example.damda.activity.MainActivity.Companion.currentPosition
import com.example.damda.activity.MainActivity.Companion.navStatus
import com.example.damda.activity.MainActivity.Companion.photoStatus
import com.example.damda.navigation.adapter.PhotoAdapter
import com.example.damda.navigation.model.Album
import com.example.damda.navigation.model.Photos
import com.google.gson.GsonBuilder
import com.jakewharton.rxbinding2.widget.checked
import kotlinx.android.synthetic.main.fragment_photo_list.*
import kotlinx.android.synthetic.main.fragment_photo_list.view.*
import kotlinx.android.synthetic.main.list_item_photo.*
import kotlinx.android.synthetic.main.list_item_photo.view.*
import okhttp3.*
import retrofit2.http.Url
import java.io.ByteArrayOutputStream
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
        val view = LayoutInflater.from(activity).inflate(R.layout.fragment_photo_list, container, false)
        if (navStatus == 0) {
            view.cl_navbar.visibility = View.GONE
            view.btn_cancel.visibility = View.INVISIBLE
            view.btn_correct.visibility = View.VISIBLE
            view.cb_image.visibility = View.INVISIBLE
        } else {
            view.cb_image.visibility = View.VISIBLE
            view.cl_navbar.visibility = View.VISIBLE
            view.btn_cancel.visibility = View.VISIBLE
            view.btn_correct.visibility = View.INVISIBLE
        }
        view.cb_image.setOnClickListener {
        if (view.cb_image.isChecked) {
            image_checked = 1
            view.cb_image.text = "전체 해제"
            view.rv_photo.adapter?.notifyDataSetChanged()
        } else {
            image_checked = 0
            view.rv_photo.adapter?.notifyDataSetChanged()
            view.cb_image.text = "전체 선택"
        }
        }
        album = arguments?.getParcelable<Album>("album")
        val family_id = GlobalApplication.prefs.family_id?.toInt()
        var url = URL(prefs.damdaServer+"/api/albums/photo/${family_id}/")
        view.albumTitle?.text = "전체 보기"
        if (album?.id != null) {
            url = URL(prefs.damdaServer+"/api/albums/photo/${family_id}/${album?.id}/")
            view.albumTitle?.text = album?.call
        }
        val jwt = GlobalApplication.prefs.token
        val request = Request.Builder().url(url).addHeader("Authorization", "JWT $jwt")
            .build()
        val client = OkHttpClient()
        photoArray = ArrayList<Photos>()
        deleteArray = ArrayList<Int>()
        view.rv_photo?.adapter =
            PhotoAdapter(photoList, context,this) { photo ->
                var bundle = Bundle()
                bundle.putSerializable("photoList", photoList)
                bundle.putInt("position", photoList.indexOf(photo))
                bundle.putParcelable("album", album)
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
                        PhotoAdapter(photoList, context, this@PhotoListFragment) { photo ->
                            var bundle = Bundle()
                            bundle.putSerializable("photoList", photoList)
                            bundle.putInt("position", photoList.indexOf(photo))
                            bundle.putParcelable("album", album)
                            var fragment = PhotoDetailFragment()
                            fragment.arguments = bundle
                            context.replaceFragment(fragment)
                        }
                    })
                }
            })
        view.rv_photo?.layoutManager = GridLayoutManager(activity, 3)
            view.btn_correct.setOnClickListener {
                if (photoList.isNotEmpty()) {
                    photoArray = ArrayList<Photos>()
                    deleteArray = ArrayList<Int>()
                    navStatus = 1
                    photoStatus = 1
                    context.replaceNavbar()
                    view.cb_image.isChecked = false
                    view.cb_image.text = "전체 선택"
                    view.cb_image.visibility = View.VISIBLE
                    view.cl_navbar.visibility = View.VISIBLE
                    view.btn_cancel.visibility = View.VISIBLE
                    view.btn_correct.visibility = View.INVISIBLE
                    view.rv_photo.adapter?.notifyDataSetChanged()
                }
            }
        view.btn_download.setOnClickListener {
            photoList = photoArray.toTypedArray()
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
            photoStatus = 0
            navStatus = 0
            image_checked = 0
            view.cb_image.isChecked = false
            view.cb_image.text = "전체 선택"
            view.cb_image.visibility = View.INVISIBLE
            view.cl_navbar.visibility = View.GONE
            view.btn_cancel.visibility = View.INVISIBLE
            view.btn_correct.visibility = View.VISIBLE
            context.replaceNavbar()
            photoArray = ArrayList<Photos>()
            deleteArray = ArrayList<Int>()
            view.rv_photo.adapter?.notifyDataSetChanged()

        }
        view.btn_share.setOnClickListener {
            photoList = photoArray.toTypedArray()
            photoStatus = 0
            navStatus = 0
            image_checked = 0
            photoArray = ArrayList<Photos>()
            deleteArray = ArrayList<Int>()
            view.rv_photo.adapter?.notifyDataSetChanged()
            var imageUris = ArrayList<Uri?>()
            for (photo in photoList) {
                var url = prefs.damdaServer+"/${photo.pic_name}"
                var image_task: URLtoBitmapTask = URLtoBitmapTask()
                image_task = URLtoBitmapTask().apply {
                    imgurl = URL(url)
                }
                var bitmap: Bitmap = image_task.execute().get()
                var uri: Uri? = getImageUri(context, bitmap, photo.title)
                imageUris.add(uri)
            }

            val share_intent = Intent().apply {
                    action = Intent.ACTION_SEND_MULTIPLE
                    putExtra(Intent.EXTRA_STREAM, imageUris)
                    type = "image/*"
                }
            val chooser = Intent.createChooser(share_intent, "친구에게 공유하기")
            view.cb_image.visibility = View.INVISIBLE
            view.cb_image.isChecked = false
            view.cb_image.text = "전체 선택"
            view.cl_navbar.visibility = View.GONE
            view.btn_cancel.visibility = View.INVISIBLE
            view.btn_correct.visibility = View.VISIBLE
            context.replaceNavbar()
            startActivity(chooser)
        }
        view.btn_cancel.setOnClickListener {
            photoStatus = 0
            navStatus = 0
            image_checked = 0
            view.cb_image.visibility = View.INVISIBLE
            view.cl_navbar.visibility = View.GONE
            view.btn_cancel.visibility = View.INVISIBLE
            view.btn_correct.visibility = View.VISIBLE
            context.replaceNavbar()
            photoArray = ArrayList<Photos>()
            deleteArray = ArrayList<Int>()
            view.rv_photo.adapter?.notifyDataSetChanged()
            view.cb_image.isChecked = false
            view.cb_image.text = "전체 선택"

        }
            val dialog = AlertDialog.Builder(activity)
            dialog.setMessage("삭제하시겠습니까?")
                .setPositiveButton("확인",
                    DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int ->
                        photoStatus = 0
                        navStatus = 0
                        image_checked = 0
                        view.cb_image.isChecked = false
                        view.cb_image.text = "전체 선택"
                        view.cb_image.visibility = View.INVISIBLE
                        view.cl_navbar.visibility = View.GONE
                        view.btn_cancel.visibility = View.INVISIBLE
                        view.btn_correct.visibility = View.VISIBLE
                        context.replaceNavbar()
                        val jwt = GlobalApplication.prefs.token
                        var payload: String = ""
                        for (photo in deleteArray) {
                            if (payload.length == 0) {
                                payload += "$photo"
                            } else {
                                payload += ", $photo"
                            }
                        }
                        val formBody = FormBody.Builder()
                            .add("photos", payload.toString())
                            .build()
                        val request = Request.Builder().url(url).addHeader("Authorization", "JWT $jwt").method("POST", formBody)
                            .build()
                        val client = OkHttpClient()
                        client.newCall(request).enqueue(object: Callback {
                            override fun onFailure(call: Call, e: IOException) {
                                println("Failed to execute request!")
                            }

                            override fun onResponse(call: Call, response: Response) {
                                val body = response.body()?.string()
                                val gson = GsonBuilder().create()
                                var list = emptyArray<Photos>()
                                photoList= gson.fromJson(body, list::class.java)
                                var bundle = Bundle()
                                bundle.putParcelable("album", album)
                                var fragment = PhotoListFragment()
                                fragment.arguments = bundle
                                fragmentManager!!.beginTransaction().remove(this@PhotoListFragment).commit()
                                fragmentManager!!.popBackStack()
                                fragmentManager!!.popBackStack()
                                context.replaceFragment(fragment)
                            }
                        })
                        photoArray = ArrayList<Photos>()
                        deleteArray = ArrayList<Int>()
                    })
                .setNegativeButton("취소",
                DialogInterface.OnClickListener { dialog, id ->
                    image_checked = 0
                    dialog.dismiss()
                })
            dialog.create()
        view.btn_photos_delete.setOnClickListener {
            dialog.show()
        }
        prepareTransitions()
        postponeEnterTransition()
        Handler().postDelayed(Runnable { view.rv_photo?.scrollToPosition(currentPosition) }, 100)
        return view
    }

    private  fun startDownloading() {
        for(photo in photoList){
            val imgurl = prefs.damdaServer+"/${photo.pic_name}"
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
    private fun getImageUri(context: Context, inImage: Bitmap, title: String?): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String = MediaStore.Images.Media.insertImage(
            context.contentResolver,
            inImage,
            title,
            null
        )
        return Uri.parse(path)
    }
    fun btnInvisible(fragment: Fragment) {
        fragment.fragmentManager?.beginTransaction()?.detach(fragment)?.attach(fragment)?.commit()
    }
    companion object {
        /**
         * Holds the current image position to be shared between the grid and the pager fragments. This
         * position updated when a grid item is clicked, or when paging the pager.
         *
         * In this demo app, the position always points to an image index at the [ ] class.
         */
        private const val KEY_CURRENT_POSITION = "com.google.samples.gridtopager.key.currentPosition"
        var photoArray = ArrayList<Photos>()
        var deleteArray = ArrayList<Int>()
        var image_checked = 0
    }

 }