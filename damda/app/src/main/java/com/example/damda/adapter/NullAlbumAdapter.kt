package com.example.damda.navigation.adapter
import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.damda.GlobalApplication.Companion.prefs
import com.example.damda.R
import com.example.damda.navigation.model.Album


class NullAlbumAdapter(val albumList: Array<Album>) : BaseAdapter()
{
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_nullalbum, null)
        val name = view.findViewById<TextView>(R.id.tv_null_album_title)
        var image = view.findViewById<ImageView>(R.id.null_album_image)
        val hint = view.findViewById<TextView>(R.id.text1)
        name?.text = albumList[position].title
        if (position == getCount()) {
            image.visibility = View.GONE
            name.visibility = View.GONE
            hint.visibility = View.VISIBLE
            hint.setHint("앨범을\n선택해주세요")
        }
        else if (albumList[position].image != "empty") {
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
        return albumList.size - 1
    }

}

