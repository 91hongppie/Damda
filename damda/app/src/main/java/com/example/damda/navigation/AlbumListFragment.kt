package com.example.damda.navigation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.damda.*
import com.example.damda.GlobalApplication.Companion.prefs
import com.example.damda.activity.CropperActivity
import com.example.damda.activity.MainActivity
import com.example.damda.activity.MainActivity.Companion.navStatus
import com.example.damda.activity.MainActivity.Companion.photoStatus
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
    private val STORAGE_PERMISSION_CODE = 1000

    @SuppressLint("RestrictedApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        navStatus = 0
        val context = activity as MainActivity
        context.replaceNavbar()
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_album_list, container, false)

        var albumList = emptyArray<Album>()
        photoStatus = 0
        view.rv_album.adapter =
            AlbumAdapter(albumList, context, this) { album ->
                var bundle = Bundle()
                bundle.putParcelable("album", album)
                var fragment = PhotoListFragment()
                fragment.arguments = bundle
                context.replaceFragment(fragment)
            }
        var retrofit = Retrofit.Builder()
            .baseUrl(prefs.damdaServer)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val jwt = GlobalApplication.prefs.token
        val family_id = GlobalApplication.prefs.family_id.toString()
        var albumsService: AlbumsService = retrofit.create(
            AlbumsService::class.java)
        albumsService.requestAlbums("JWT $jwt", family_id, prefs.user_id!!).enqueue(object: Callback<Albums>{
            override fun onFailure(call: Call<Albums>, t: Throwable) {
                var dialog = AlertDialog.Builder(context)
                dialog.setTitle("에러")
                dialog.setMessage("호출실패했습니다.")
                dialog.show()
            }

            override fun onResponse(call: Call<Albums>, response: Response<Albums>) {
                val albums = response.body()
                albumList = albums!!.data
                val albumAdapter =
                    AlbumAdapter(albumList, context,this@AlbumListFragment) { album ->
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
        if(prefs.state == "3"){
            view.add_album_button.setOnClickListener {
                var intent = Intent(context, CropperActivity::class.java)
                startActivity(intent)
            }
        }
        else {
            view.add_album_button.visibility = View.GONE
        }
        return view
    }

    fun perm(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this.context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED){
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
                if (ContextCompat.checkSelfPermission(this.context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED){
                    return false
                }
                return true

            }
            else{
                return true
            }

        }
        else{
            return true
        }
    }

}