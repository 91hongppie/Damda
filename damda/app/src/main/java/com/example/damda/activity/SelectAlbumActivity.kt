package com.example.damda.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.damda.GlobalApplication
import com.example.damda.R
import com.example.damda.navigation.model.Album
import com.example.damda.retrofit.model.Albums
import com.example.damda.retrofit.model.Face
import com.example.damda.retrofit.model.Faces
import com.example.damda.retrofit.service.AlbumsService
import kotlinx.android.synthetic.main.activity_select_album.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SelectAlbumActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_album)
        setSupportActionBar(findViewById(R.id.toolbar))
        val actionBar = supportActionBar
        actionBar?.setDisplayShowCustomEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        album_list.layoutManager = LinearLayoutManager(this)
        var retrofit = Retrofit.Builder()
            .baseUrl(GlobalApplication.prefs.damdaServer)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val jwt = GlobalApplication.prefs.token
        val family_id = GlobalApplication.prefs.family_id.toString()
        var albumsService: AlbumsService = retrofit.create(
            AlbumsService::class.java
        )
        albumsService.nullAlbums("JWT $jwt", family_id, GlobalApplication.prefs.user_id!!)
            .enqueue(object : Callback<Albums> {
                override fun onFailure(call: Call<Albums>, t: Throwable) {
                    Log.v("face", t.toString())
                    var dialog = AlertDialog.Builder(this@SelectAlbumActivity)
                    dialog.setTitle("에러")
                    dialog.setMessage("호출실패했습니다.")
                    dialog.show()
                }

                override fun onResponse(call: Call<Albums>, response: Response<Albums>) {
                    val albums = response.body()
                    album_list.addItemDecoration(
                        DividerItemDecoration(
                            this@SelectAlbumActivity,
                            LinearLayoutManager.VERTICAL
                        )
                    )

                }
            })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

