package com.example.damda.navigation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.damda.*
import com.example.damda.activity.MainActivity
import com.example.damda.navigation.model.Album
import com.example.damda.navigation.adapter.AlbumAdapter
import com.example.damda.retrofit.model.Albums
import com.example.damda.retrofit.service.AlbumsService
import kotlinx.android.synthetic.main.fragment_album_list.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class AlbumListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        val context = activity as MainActivity
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_album_list, container, false)

        var albums: Albums? = null
        var albumList = emptyArray<Album>()
        view.rv_album.adapter =
            AlbumAdapter(albumList) { album ->
                var bundle = Bundle()
                bundle.putParcelable("album", album)
                var fragment = PhotoListFragment()
                fragment.arguments = bundle
                context.replaceFragment(fragment)
            }
        var retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val jwt = GlobalApplication.prefs.token
        var albumsService: AlbumsService = retrofit.create(
            AlbumsService::class.java)
        albumsService.requestAlbums("JWT $jwt").enqueue(object: Callback<Albums>{
            override fun onFailure(call: Call<Albums>, t: Throwable) {
                Log.e("Albu ", ""+t)
                var dialog = AlertDialog.Builder(context)
                dialog.setTitle("에러")
                dialog.setMessage("호출실패했습니다.")
                dialog.show()
            }

            override fun onResponse(call: Call<Albums>, response: Response<Albums>) {
                albums = response.body()
                albumList = albums!!.data
                val albumAdapter =
                    AlbumAdapter(albumList) { album ->
                        var bundle = Bundle()
                        bundle.putParcelable("album", album)
                        var fragment = PhotoListFragment()
                        fragment.arguments = bundle
                        context.replaceFragment(fragment)
                    }
                view.rv_album.adapter = albumAdapter
            }
        })
        view.rv_album.layoutManager = GridLayoutManager(activity, 3)
        return view
    }
}