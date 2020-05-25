package com.example.damda.navigation.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.damda.R
import com.example.damda.navigation.model.Photos
import com.jakewharton.rxbinding2.view.visibility
import com.jakewharton.rxbinding2.widget.checked
import com.jakewharton.rxbinding2.widget.checkedChanges
import kotlinx.android.synthetic.main.list_item_photo.view.*
import org.jetbrains.anko.find


class PhotoAdapter (val photoList: Array<Photos>, val itemClick: (Photos) -> Unit) : RecyclerView.Adapter<PhotoAdapter.CustomViewHolder>() {
    private var photoArray = ArrayList<Photos>()
    private var ck = 0
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
        var checkbox: CheckBox = itemView!!.findViewById<CheckBox>(R.id.cb_photo)
        fun bind(photo: Photos) {
//            checkbox.visibility = View.VISIBLE
            Glide.with(view.context).load("http://10.0.2.2:8000${photo.pic_name}")
                .apply(RequestOptions().override(600, 600))
                .apply(RequestOptions.centerCropTransform()).into(image)
            if (ck == 1) {
                view.findViewById<CheckBox>(R.id.cb_photo).visibility = View.VISIBLE
            } else {
                view.findViewById<CheckBox>(R.id.cb_photo).visibility = View.INVISIBLE
            }
            view.setOnLongClickListener {
                ck = 1
                notifyDataSetChanged()
                true
            }
            view.setOnClickListener {
                if (ck == 0) {
                    println(photoArray.size)
                    itemClick(photo)
                } else {
                    if (photoArray.contains(photo)) {
                        view.findViewById<CheckBox>(R.id.cb_photo).isChecked = false
                        photoArray.remove(photo)
                        println(photoArray.size)
                    } else {
                        view.findViewById<CheckBox>(R.id.cb_photo).isChecked = true
                        photoArray.add(photo)
                        println(photoArray.size)
                    }
                }
            }
            view.findViewById<CheckBox>(R.id.cb_photo).setOnClickListener {
                if (itemView.findViewById<CheckBox>(R.id.cb_photo).isChecked) {
                    itemView.findViewById<CheckBox>(R.id.cb_photo).isChecked = false
                    photoArray.remove(photo)
                } else {
                    itemView.findViewById<CheckBox>(R.id.cb_photo).isChecked = true
                    photoArray.add(photo)
                    println(photoArray.size)
                }
            }
        }
    }
fun updateCheckbox(n: Int) {
    ck = n
}
}

