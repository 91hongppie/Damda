package com.example.damda
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.damda.navigation.model.Album

class AlbumAdapter (val albumList: ArrayList<Album>, val itemClick: (Album) -> Unit) : RecyclerView.Adapter<AlbumAdapter.CustomViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumAdapter.CustomViewHolder {
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
        val name = itemView.findViewById<TextView>(R.id.tv_album_name)
        val btn = itemView.findViewById<Button>(R.id.btn_album)
        fun bind (album: Album) {
            name?.text = album.name
            btn.setOnClickListener { itemClick(album) }
            itemView.setOnClickListener { itemClick(album) }
        }
    }
}