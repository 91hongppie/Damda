package com.example.damda.navigation.adapter
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
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

class AlbumAdapter(val albumList: Array<Album>,val activity: MainActivity, val fragment: AlbumListFragment, val itemClick: (Album) -> Unit) : RecyclerView.Adapter<AlbumAdapter.CustomViewHolder>()
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
            name?.text = album.call
            if (album.image != "empty") {
                Glide.with(itemView.context)
                    .load(prefs.damdaServer + "/${album.image}")
                    .error(R.drawable.album).apply(RequestOptions().override(600, 600))
                    .apply(RequestOptions.centerCropTransform()).into(image)
            }
            image.setOnLongClickListener{
                val wrapper = ContextThemeWrapper(itemView.context, R.style.BasePopupMenu)
                val pop = PopupMenu(wrapper, it)
                pop.inflate(R.menu.menu_album)
                pop.setOnMenuItemClickListener { item ->
                    when(item.itemId){
                        R.id.album_menu_item1->
                        {
                            if (fragment.perm()) {
                                val jwt = GlobalApplication.prefs.token
                                val family_id = GlobalApplication.prefs.family_id?.toInt()
                                val url = URL(prefs.damdaServer+"/api/albums/photo/${family_id}/${album.id}/")
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
                                        val photoList = gson.fromJson(body, Array<Photos>::class.java)
                                        for (photo in photoList) {
                                            val imgurl = prefs.damdaServer+"${photo.pic_name}"
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
                                                "damda/${album.title}/${photo.title}"
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
                            var dialogBuilder = AlertDialog.Builder(itemView.context)
                            dialogBuilder.setTitle("앨범 삭제")
                            dialogBuilder.setMessage("앨범을 삭제하시겠습니까?")
                            var dialogListener = object:DialogInterface.OnClickListener{
                                override fun onClick(dialog: DialogInterface?, which: Int) {
                                    when(which){
                                        DialogInterface.BUTTON_NEUTRAL -> {
                                            var retrofit = Retrofit.Builder()
                                                .baseUrl(prefs.damdaServer)
                                                .addConverterFactory(GsonConverterFactory.create())
                                                .build()
                                            val jwt = GlobalApplication.prefs.token
                                            var albumsService: AlbumsService = retrofit.create(AlbumsService::class.java)
                                            albumsService.deleteAlbum("JWT $jwt", album.id).enqueue(object:
                                                retrofit2.Callback<DeleteAlbum> {
                                                override fun onFailure(call: retrofit2.Call<DeleteAlbum>, t: Throwable) {
                                                    var deletedialog = androidx.appcompat.app.AlertDialog.Builder(itemView.context)
                                                    deletedialog.setTitle("에러")
                                                    deletedialog.setMessage("호출실패했습니다.")
                                                    deletedialog.show()
                                                }

                                                override fun onResponse(call: retrofit2.Call<DeleteAlbum>, response: retrofit2.Response<DeleteAlbum>) {
                                                    var albumListFragment = AlbumListFragment()
                                                    activity.replaceFragment(albumListFragment)
                                                }
                                            })
                                        }
                                    }
                                }
                            }
                            dialogBuilder.setPositiveButton("취소", dialogListener)
                            dialogBuilder.setNeutralButton("삭제", dialogListener)
                            dialogBuilder.show()
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

