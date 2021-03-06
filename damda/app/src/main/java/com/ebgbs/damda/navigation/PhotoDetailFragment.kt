package com.ebgbs.damda.navigation

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.transition.TransitionInflater
import android.view.*
import android.widget.*
import androidx.annotation.DrawableRes
import androidx.core.app.SharedElementCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.ebgbs.damda.GlobalApplication
import com.ebgbs.damda.GlobalApplication.Companion.prefs
import com.ebgbs.damda.R
import com.ebgbs.damda.URLtoBitmapTask
import com.ebgbs.damda.activity.MainActivity
import com.ebgbs.damda.activity.MainActivity.Companion.currentPosition
import com.ebgbs.damda.helper.ZoomOutPageTransformer
import com.ebgbs.damda.navigation.model.Album
import com.ebgbs.damda.navigation.model.Photos
import com.ebgbs.damda.retrofit.model.PutAlbum
import com.ebgbs.damda.retrofit.service.AlbumsService
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_photo_detail.view.*
import kotlinx.android.synthetic.main.fragment_photo_detail.view.btn_option
import kotlinx.android.synthetic.main.image_fullscreen.view.*
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URL


class PhotoDetailFragment: Fragment() {
    private val STORAGE_PERMISSION_CODE: Int = 1000
    private var photoList = emptyArray<Photos>()
    private var selectedPosition: Int = 0
    private var album: Album? = null
    lateinit var tvGalleryTitle: TextView
    lateinit var btn_option: ImageView
    lateinit var viewPager: ViewPager
    lateinit var galleryPagerAdapter: GalleryPagerAdapter
    lateinit var dialog: AlertDialog.Builder

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val context = activity as MainActivity
        val view = inflater.inflate(R.layout.fragment_photo_detail, container, false)
        viewPager = view!!.vp_photo
        tvGalleryTitle = view.tvGalleryTitle
        btn_option = view.btn_option
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
        currentPosition = selectedPosition
        prepareSharedElementTransition()
        if (savedInstanceState == null) {
            postponeEnterTransition()
        }

        dialog = AlertDialog.Builder(activity)
        dialog.setMessage("삭제하시겠습니까?")
            .setPositiveButton("확인",
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int ->
                    val album = arguments?.getParcelable<Album>("album")
                    val family_id = GlobalApplication.prefs.family_id?.toInt()
                    var url = URL(prefs.damdaServer+"/api/albums/photo/${family_id}/")
                    if (album?.id != null) {
                        url = URL(prefs.damdaServer+"/api/albums/photo/${family_id}/${album.id}/")
                    }
                    val jwt = GlobalApplication.prefs.token
                    val payload = photoList[selectedPosition].id
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
                            fragmentManager!!.beginTransaction().remove(this@PhotoDetailFragment).commit()
                            fragmentManager!!.popBackStack()
                            fragmentManager!!.popBackStack()
                            context.replaceFragment(fragment)
                        }
                    })
                })
            .setNegativeButton("취소",
                DialogInterface.OnClickListener { dialog, id ->
                    dialog.dismiss()
                })

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = activity as MainActivity
        btn_option.setOnClickListener {
            val wrapper = ContextThemeWrapper(this.context, R.style.BasePopupMenu)
            val pop = PopupMenu(wrapper, it)
            pop.inflate(R.menu.menu_photo)
            if (album?.id == null){
                pop.menu.removeItem(R.id.image_change)
            }
            pop.setOnMenuItemClickListener { item ->
                when(item.itemId){
                    R.id.save -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            if (ContextCompat.checkSelfPermission(this.context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                PackageManager.PERMISSION_DENIED){
                                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
                            }
                            else{
                                startDownloading()
                            }

                        }
                        else {
                            startDownloading()
                        }
                    }
                    R.id.image_change -> {
                        var retrofit = Retrofit.Builder()
                            .baseUrl(prefs.damdaServer)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
                        val jwt = GlobalApplication.prefs.token
                        val photo = photoList[selectedPosition].pic_name!!
                        var albumsService: AlbumsService = retrofit.create(AlbumsService::class.java)
                        albumsService.changeAlbumImage("JWT $jwt", album!!.id, photo).enqueue(object: retrofit2.Callback<PutAlbum>{
                            override fun onFailure(call: retrofit2.Call<PutAlbum>, t: Throwable) {
                                Toast.makeText(context, "대표 이미지가 변경에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            }

                            override fun onResponse(call: retrofit2.Call<PutAlbum>, response: retrofit2.Response<PutAlbum>) {
                                Toast.makeText(context, "대표 이미지가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                    R.id.share -> {
                        val share_intent = Intent().apply {
                            var url = prefs.damdaServer+"/api/${photoList[selectedPosition].pic_name}"
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
//            intent.putExtra(Intent.EXTRA_STREAM, prefs.damdaServer+"${photoList[selectedPosition].pic_name}")
                        startActivity(chooser)
                    }
                    R.id.delete-> {
                        dialog.show()
                    }
                }
                true
            }
            pop.show()
        }
    }

    private fun setCurrentItem(position: Int) {
        viewPager.setCurrentItem(position, false)
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.menu_photo, menu)
//    }

    private  fun startDownloading() {
        val photo = photoList[selectedPosition]
        val imgurl = prefs.damdaServer+"/api/${photo.pic_name}"
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

            @SuppressLint("SetTextI18n")
            override fun onPageSelected(position: Int) {
                // set gallery title
                selectedPosition = position
                currentPosition = position
                tvGalleryTitle.text = photoList[position].title
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
                .load(prefs.damdaServer+"/api/${photo.pic_name}")
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
    private fun prepareSharedElementTransition() {
        val transition = TransitionInflater.from(context)
            .inflateTransition(R.transition.image_shared_element_transition)
        sharedElementEnterTransition = transition

        // A similar mapping is set at the GridFragment with a setExitSharedElementCallback.
        setEnterSharedElementCallback(
            object : SharedElementCallback() {
                override fun onMapSharedElements(names: List<String>, sharedElements: MutableMap<String, View>) {
                    // Locate the image view at the primary fragment (the ImageFragment that is currently
                    // visible). To locate the fragment, call instantiateItem with the selection position.
                    // At this stage, the method will simply return the fragment at the position and will
                    // not create a new one.
                    val currentFragment = viewPager!!.adapter?.instantiateItem(viewPager!!, MainActivity.currentPosition) as Fragment
                    val view = currentFragment.view ?: return

                    // Map the first shared element name to the child ImageView.
                    sharedElements[names[0]] = view.findViewById(R.id.image)
                }
            })
    }
}