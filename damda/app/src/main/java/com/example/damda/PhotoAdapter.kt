package com.example.damda

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.damda.navigation.model.Photos
import kotlinx.android.synthetic.main.list_item_photo.view.*


class PhotoAdapter (val photoList: Array<Photos>) : RecyclerView.Adapter<PhotoAdapter.CustomViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_photo, parent, false)
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
        fun bind(photo: Photos){
            Glide.with(view.context).load("http://10.0.2.2:8000${photo.pic_name}").apply(RequestOptions().override(600, 600))
                .apply(RequestOptions.centerCropTransform()).into(image)
        }
    }
}
