package com.example.damda.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.damda.GlobalApplication
import com.example.damda.R
import com.example.damda.adapter.MemberAdapter
import com.example.damda.navigation.model.Album
import com.example.damda.retrofit.model.Albums
import com.example.damda.retrofit.service.AlbumsService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_add_member.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AddMemberActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_member)
        setSupportActionBar(findViewById(R.id.toolbar))
        val actionBar = supportActionBar
        actionBar?.setDisplayShowCustomEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)
//        member_list.adapter = MemberAdapter()
        member_list.layoutManager = LinearLayoutManager(this)
        add_member_button.setOnClickListener {
            var intent = Intent(this@AddMemberActivity, CropperActivity::class.java)
                startActivity(intent)
        }
        var albums: Albums? = null
        var albumList = emptyArray<Album>()
        var retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val jwt = GlobalApplication.prefs.token
        val family_id = GlobalApplication.prefs.family_id.toString()
        var albumsService: AlbumsService = retrofit.create(
            AlbumsService::class.java)
        albumsService.requestAlbums("JWT $jwt", family_id).enqueue(object: Callback<Albums> {
            override fun onFailure(call: Call<Albums>, t: Throwable) {
                Log.e("Albu ", ""+t)
                var dialog = AlertDialog.Builder(this@AddMemberActivity)
                dialog.setTitle("에러")
                dialog.setMessage("호출실패했습니다.")
                dialog.show()
            }

            override fun onResponse(call: Call<Albums>, response: Response<Albums>) {
                albums = response.body()
                albumList = albums!!.data
                if (albumList.size > 0) {
                    member_info.visibility = View.GONE
                    member_list.adapter = MemberAdapter(albumList)
                }
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
