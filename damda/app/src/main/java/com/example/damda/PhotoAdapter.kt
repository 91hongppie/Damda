package com.example.damda

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.damda.navigation.JsonObj
import com.example.damda.navigation.MyMy
import com.example.damda.navigation.model.Photos
import kotlinx.android.synthetic.main.list_item_photo.view.*


class PhotoAdapter (val photoList: JsonObj) : RecyclerView.Adapter<PhotoAdapter.CustomViewHolder>()
{


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_photo, parent, false)
        return CustomViewHolder(view).apply {
            itemView.setOnClickListener {
                Toast.makeText(parent.context, "클릭하면 나와용", Toast.LENGTH_SHORT).show()

            }

        }
    }

    override fun getItemCount(): Int {
        return photoList.result.size
    }

    override fun onBindViewHolder(holder: PhotoAdapter.CustomViewHolder, position: Int) {
        holder.bindItems(photoList.result[position])
    }

    class CustomViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bindItems(data: MyMy){
            println("tttttttttttttttttt ${data.pic_name}")
            Glide.with(view.context).load("http://10.0.2.2:8000${data.pic_name}").apply(RequestOptions().override(600, 600))
                .apply(RequestOptions.centerCropTransform()).into(view.iv_image)
        }
//        val image = itemView.findViewById<ImageView>(R.id.iv_image)

    }
}
