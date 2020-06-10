package com.ebgbs.damda.navigation.adapter

import android.annotation.SuppressLint
import android.view.*
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ebgbs.damda.GlobalApplication.Companion.prefs
import com.ebgbs.damda.R
import com.ebgbs.damda.navigation.model.Album

class MoveAlbumAdapter(val albumList: Array<Album>) : BaseAdapter() {
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_album, null)
        val name = view.findViewById<TextView>(R.id.tv_album_title)
        var image = view.findViewById<ImageView>(R.id.album_image)
        name?.text = albumList[position].title
        if (albumList[position].image != "empty") {
            Glide.with(view.context)
                .load(prefs.damdaServer + "/api/${albumList[position].image}")
                .error(R.drawable.album).apply(RequestOptions().override(600, 600))
                .apply(RequestOptions.centerCropTransform()).into(image)
        }

        return view
    }

    override fun getItem(position: Int): Any {
        return albumList[position].id
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return albumList.size
    }

}
