package com.example.damda.navigation.adapter
import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.damda.GlobalApplication
import com.example.damda.R
import com.example.damda.activity.MainActivity
import com.example.damda.navigation.AlbumListFragment
import com.example.damda.navigation.PhotoDetailFragment
import com.example.damda.navigation.model.Album
import com.example.damda.navigation.model.Photos
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_photo_list.view.*
import okhttp3.*
import java.io.IOException
import java.net.URL
import java.util.prefs.NodeChangeListener

class AlbumAdapter(val albumList: Array<Album>, val fragment: AlbumListFragment, val itemClick: (Album) -> Unit) : RecyclerView.Adapter<AlbumAdapter.CustomViewHolder>()
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
        var image = itemView.findViewById<ImageView>(R.id.album_image)
        fun bind (album: Album) {
            name?.text = album.title
            Glide.with(itemView.context).load("http://10.0.2.2:8000/${album.image}").error(R.drawable.album).apply(RequestOptions().override(600, 600))
                .apply(RequestOptions.centerCropTransform()).into(image)

            image.setOnLongClickListener{
                val pop = PopupMenu(itemView.context, it)
                pop.inflate(R.menu.menu_album)
                pop.setOnMenuItemClickListener { item ->
                    when(item.itemId){
                        R.id.album_menu_item1->
                        {
                            if (fragment.perm()) {
                                var photoList = emptyArray<Photos>()
                                var url = URL("http://10.0.2.2:8000/api/albums/photo/${album.id}/")
                                val jwt = GlobalApplication.prefs.token
                                val request = Request.Builder().url(url)
                                    .addHeader("Authorization", "JWT $jwt")
                                    .build()
                                val client = OkHttpClient()
                                client.newCall(request).enqueue(object : Callback {
                                    override fun onFailure(call: Call, e: IOException) {
                                        println("Failed to execute request!")
                                    }

                                    override fun onResponse(call: Call, response: Response) {
                                        val body = response.body()?.string()
                                        val gson = GsonBuilder().create()
                                        photoList = gson.fromJson(body, Array<Photos>::class.java)
                                        for (photo in photoList) {
                                            val imgurl = "http://10.0.2.2:8000${photo.pic_name}"
                                            val downrequest =
                                                DownloadManager.Request(Uri.parse(imgurl))
                                            downrequest.addRequestHeader(
                                                "Authorization",
                                                "JWT $jwt"
                                            )
                                            downrequest.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                                            downrequest.setTitle("${photo.title}")
                                            downrequest.setDescription("앨범 다운로드 중")
                                            downrequest.allowScanningByMediaScanner()
                                            downrequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                            downrequest.setDestinationInExternalPublicDir(
                                                Environment.DIRECTORY_DCIM,
                                                "damda/${album?.title}/${photo.title}"
                                            )
                                            val manager =
                                                fragment.context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                                            manager.enqueue(downrequest)
                                        }
                                    }
                                })
                            }
                        }
                        R.id.album_menu_item3 -> {

                        }
                    }
                    true
                }
                pop.show()
                true
            }
            image.setOnClickListener { itemClick(album) }
            itemView.setOnClickListener { itemClick(album) }

        }

    }

}

