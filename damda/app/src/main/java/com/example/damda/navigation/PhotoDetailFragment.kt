package com.example.damda.navigation

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.damda.R
import com.example.damda.helper.ZoomOutPageTransformer
import com.example.damda.navigation.model.Photos
import com.jakewharton.rxbinding2.view.clicks
import kotlinx.android.synthetic.main.fragment_photo_detail.*
import kotlinx.android.synthetic.main.fragment_photo_detail.view.*
import kotlinx.android.synthetic.main.fragment_photo_detail.view.btn_photo
import kotlinx.android.synthetic.main.image_fullscreen.view.*
import java.io.ByteArrayOutputStream

class PhotoDetailFragment: Fragment() {

    private var photoList = emptyArray<Photos>()
    private var selectedPosition: Int = 0
    lateinit var tvGalleryTitle: TextView
    lateinit var viewPager: ViewPager

    lateinit var galleryPagerAdapter: GalleryPagerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_photo_detail, container, false)

        viewPager = view.findViewById(R.id.vp_photo)
        tvGalleryTitle = view.findViewById(R.id.tvGalleryTitle)


        galleryPagerAdapter = GalleryPagerAdapter()

        photoList = arguments?.getSerializable("photoList") as Array<Photos>
        selectedPosition = arguments!!.getInt("position")

        viewPager.adapter = galleryPagerAdapter
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener)
        viewPager.setPageTransformer(true, ZoomOutPageTransformer())

        setCurrentItem(selectedPosition)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var url = "https://newsimg.hankookilbo.com/2016/04/13/201604131460701467_1.jpg"

        view.btn_photo.setOnClickListener { view ->
            val share_intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, "https://newsimg.hankookilbo.com/2016/04/13/201604131460701467_1.jpg")
                type = "image/*"
            }
            val chooser = Intent.createChooser(share_intent, "친구에게 공유하기")
//            intent.putExtra(Intent.EXTRA_STREAM, "http://10.0.2.2:8000${photoList[selectedPosition].pic_name}")
            startActivity(chooser)
        }
    }


//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
//    }

    private fun setCurrentItem(position: Int) {
        viewPager.setCurrentItem(position, true)
    }

    // viewpager page change listener
    internal var viewPagerPageChangeListener: ViewPager.OnPageChangeListener =
        object : ViewPager.OnPageChangeListener {

            override fun onPageSelected(position: Int) {
                // set gallery title
                selectedPosition = position
                tvGalleryTitle.text = photoList.get(position).title
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
}