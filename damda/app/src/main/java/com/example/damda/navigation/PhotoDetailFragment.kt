package com.example.damda.navigation

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.damda.GlobalApplication
import com.example.damda.R
import com.example.damda.URLtoBitmapTask
import com.example.damda.activity.MainActivity
import com.example.damda.helper.ZoomOutPageTransformer
import com.example.damda.navigation.model.Album
import com.example.damda.navigation.model.Photos
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_photo_detail.*
import kotlinx.android.synthetic.main.fragment_photo_detail.view.*
import kotlinx.android.synthetic.main.fragment_photo_detail.view.btn_share
import kotlinx.android.synthetic.main.image_fullscreen.view.*
import okhttp3.*
import org.jetbrains.anko.find
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URL


class PhotoDetailFragment: Fragment() {
    private val STORAGE_PERMISSION_CODE: Int = 1000
    private var photoList = emptyArray<Photos>()
    private var selectedPosition: Int = 0
    private var album: Album? = null
    lateinit var tvGalleryTitle: TextView
    lateinit var et_update: EditText
    lateinit var btn_share: Button
    lateinit var btn_delete: Button
    lateinit var btn_save: Button
    lateinit var viewPager: ViewPager
    lateinit var galleryPagerAdapter: GalleryPagerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_photo_detail, container, false)

        viewPager = view!!.findViewById(R.id.vp_photo)
        tvGalleryTitle = view.findViewById(R.id.tvGalleryTitle)
        et_update = view.findViewById(R.id.et_update)
        btn_share = view.findViewById(R.id.btn_share)
        btn_delete = view.findViewById(R.id.btn_delete)
        btn_save = view.findViewById(R.id.btn_save)
        album = arguments?.getParcelable<Album>("album")




        galleryPagerAdapter = GalleryPagerAdapter()
        @DrawableRes val imageRes = arguments!!.getInt(KEY_IMAGE_RES)

        photoList = arguments?.getSerializable("photoList") as Array<Photos>
        selectedPosition = arguments!!.getInt("position")
        tvGalleryTitle.text = photoList[selectedPosition].title
//        view.findViewById<View>(R.id.ivFullscreenImage).transitionName = imageRes.toString()

        viewPager.adapter = galleryPagerAdapter
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener)
        viewPager.setPageTransformer(true, ZoomOutPageTransformer())

        setCurrentItem(selectedPosition)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = activity as MainActivity

        btn_share.setOnClickListener {
            val share_intent = Intent().apply {
                var url = "http://10.0.2.2:8000${photoList[selectedPosition].pic_name}"
                var image_task: URLtoBitmapTask = URLtoBitmapTask()
                image_task = URLtoBitmapTask().apply {
                    imgurl = URL(url)
                }
                var bitmap: Bitmap = image_task.execute().get()
                var uri: Uri? = getImageUri(context, bitmap, photoList[selectedPosition].title)
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uri)
                type = "image/*"
            }
            val chooser = Intent.createChooser(share_intent, "친구에게 공유하기")
//            intent.putExtra(Intent.EXTRA_STREAM, "http://10.0.2.2:8000${photoList[selectedPosition].pic_name}")
            startActivity(chooser)
        }
        btn_delete.setOnClickListener {
            val url = URL("http://10.0.2.2:8000/api/albums/photo/delete/")
            val jwt = GlobalApplication.prefs.token
            val payload = photoList[selectedPosition].id
            val formBody = FormBody.Builder()
                .add("photos", payload.toString())
                .build()
            val request = Request.Builder().url(url).addHeader("Authorization", "JWT $jwt").method("POST", formBody)
                .build()
            val client = OkHttpClient()
            val album = arguments?.getParcelable<Album>("album")
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
                    fragmentManager!!.beginTransaction().remove(this@PhotoDetailFragment).commit()
                    fragmentManager!!.popBackStack()
                    fragmentManager!!.popBackStack()
                    context.replaceFragment(fragment)
                }
            })
        }
        btn_save.setOnClickListener {
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
    }

    private fun setCurrentItem(position: Int) {
        viewPager.setCurrentItem(position, false)
    }

    private  fun startDownloading() {
        val photo = photoList[selectedPosition]
        val imgurl = "http://10.0.2.2:8000${photo.pic_name}"
        val request = DownloadManager.Request(Uri.parse(imgurl))
        val jwt = GlobalApplication.prefs.token
        request.addRequestHeader("Authorization", "JWT $jwt")
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        request.setTitle("${photo.title}")
        request.setDescription("사진 다운로드 중")
        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DCIM, "damda/${album?.title}/${photo.title}")
        val manager = context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)
    }


    // viewpager page change listener
    internal var viewPagerPageChangeListener: ViewPager.OnPageChangeListener =
        object : ViewPager.OnPageChangeListener {

            override fun onPageSelected(position: Int) {
                // set gallery title
                selectedPosition = position
                tvGalleryTitle.text = photoList.get(selectedPosition).title
            }

            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {
            }

            override fun onPageScrollStateChanged(arg0: Int) {
            }
        }

    // gallery adapter
    inner class GalleryPagerAdapter : PagerAdapter() {

        override fun instantiateItem(container: ViewGroup, position: Int): Any {

            val layoutInflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = layoutInflater.inflate(R.layout.image_fullscreen, container, false)
            val photo = photoList.get(position)
            // load image
            Glide.with(context!!)
                .load("http://10.0.2.2:8000${photo.pic_name}")
                .into(view.ivFullscreenImage)

            container.addView(view)

            return view
        }

        override fun getCount(): Int {
            return photoList.size
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj as View
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container.removeView(obj as View)
        }
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
    companion object {
        private const val KEY_IMAGE_RES = "com.google.samples.gridtopager.key.imageRes"
        fun newInstance(@DrawableRes drawableRes: Int): PhotoDetailFragment {
            val fragment = PhotoDetailFragment()
            val argument = Bundle()
            argument.putInt(KEY_IMAGE_RES, drawableRes)
            fragment.arguments = argument
            return fragment
        }
    }
}