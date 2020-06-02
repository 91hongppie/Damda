package com.example.damda.navigation.adapter
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.database.DataSetObserver
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.res.TypedArrayUtils.getString
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.damda.GlobalApplication
import com.example.damda.GlobalApplication.Companion.prefs
import com.example.damda.R
import com.example.damda.activity.MainActivity
import com.example.damda.navigation.AlbumListFragment
import com.example.damda.navigation.model.Album
import com.example.damda.navigation.model.Photos
import com.example.damda.retrofit.model.DeleteAlbum
import com.example.damda.retrofit.service.AlbumsService
import com.google.gson.GsonBuilder
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.URL

class NullAlbumAdapter(val albumList: Array<Album>) : BaseAdapter()
{
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_album, null)
        val name = view.findViewById<TextView>(R.id.tv_album_title)
        var image = view.findViewById<ImageView>(R.id.album_image)
        name?.text = albumList[position].title
        if (albumList[position].image != "empty") {
            Glide.with(view.context)
                .load(prefs.damdaServer + "/${albumList[position].image}")
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

