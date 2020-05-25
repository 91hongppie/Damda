package com.example.damda.navigation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.damda.R
import com.example.damda.activity.MainActivity
import com.example.damda.activity.MainActivity.Companion.photoStatus
import com.example.damda.navigation.PhotoListFragment
import com.example.damda.navigation.model.Photos


class PhotoAdapter (val photoList: Array<Photos>, val itemClick: (Photos) -> Unit) : RecyclerView.Adapter<PhotoAdapter.CustomViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_photo, parent, false)
        return CustomViewHolder(view)
    }

    override fun getItemCount(): Int {
        return photoList.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(photoList[position])
    }

    inner class CustomViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.iv_image)
        var checkbox = view.findViewById<CheckBox>(R.id.cb_photo)

        fun bind(photo: Photos) {
            Glide.with(view.context).load("http://10.0.2.2:8000${photo.pic_name}")
                .apply(RequestOptions().override(600, 600))
                .apply(RequestOptions.centerCropTransform()).into(image)
            checkbox.isChecked = false
            if (photoStatus == 1) {
                checkbox.visibility = View.VISIBLE
            } else {
                checkbox.visibility = View.INVISIBLE
            }
            image.setOnLongClickListener {
                photoStatus = 1
                notifyDataSetChanged()
                true
            }
            image.setOnClickListener {
                if (photoStatus == 0) {
                    println(photoArray.size)
                    itemClick(photo)
                } else {
                    if (photoArray.contains(photo)) {
                        checkbox.isChecked = false
                        photoArray.remove(photo)
                        println(photoArray.size)
                    } else {
                        checkbox.isChecked = true
                        photoArray.add(photo)
                        println(photoArray.size)
                    }
                }
            }
            checkbox.setOnClickListener {
                if (checkbox.isChecked) {
                    checkbox.isChecked = false
                    photoArray.remove(photo)
                } else {
                    checkbox.isChecked = true
                    photoArray.add(photo)
                    println(photoArray.size)
                }
            }
        }
    }
    companion object {
        var photoArray = ArrayList<Photos>()
    }
}

