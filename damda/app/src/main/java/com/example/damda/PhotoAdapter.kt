package com.example.damda

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.damda.navigation.model.Photos


class PhotoAdapter (val photoList: ArrayList<Photos>) : RecyclerView.Adapter<PhotoAdapter.CustomViewHolder>()
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
        return photoList.size
    }

    override fun onBindViewHolder(holder: PhotoAdapter.CustomViewHolder, position: Int) {
        holder.image.setImageResource(photoList.get(position).image)
    }

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<ImageView>(R.id.iv_image)
    }

}