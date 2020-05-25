package com.example.damda.navigation.adapter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.damda.R
import com.example.damda.navigation.model.Album

class AlbumAdapter(val albumList: Array<Album>, val itemClick: (Album) -> Unit) : RecyclerView.Adapter<AlbumAdapter.CustomViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_album, parent, false)
        return CustomViewHolder(view)
    }

    override fun getItemCount(): Int {
        return albumList.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(albumList[position])
    }

    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.tv_album_title)
        val image = itemView.findViewById<ImageView>(R.id.album_image)
        fun bind (album: Album) {
            name?.text = album.title
            Log.e("dhodhodhdohdohdoho", ""+album.title)
            if (album.image !== "empty") {
                Glide.with(itemView.context).load("http://10.0.2.2:8000/${album.image}").apply(RequestOptions().override(600, 600))
                    .apply(RequestOptions.centerCropTransform()).into(image)
            }
            itemView.setOnClickListener { itemClick(album) }
        }
    }
}