package com.example.damda.adapter

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.damda.GlobalApplication
import com.example.damda.GlobalApplication.Companion.prefs
import com.example.damda.R
import com.example.damda.activity.CropperActivity
import com.example.damda.activity.RequestListActivity
import com.example.damda.navigation.adapter.NullAlbumAdapter
import com.example.damda.navigation.model.Album
import com.example.damda.retrofit.model.*
import com.example.damda.retrofit.service.AlbumsService
import com.example.damda.retrofit.service.RequestService
import kotlinx.android.synthetic.main.list_item_request.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RequestAdapter(
    val requestList: Array<WaitUser>,
    requestListActivity: RequestListActivity
) : RecyclerView.Adapter<RequestAdapter.MainViewHolder>() {
    val ra = requestListActivity
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = MainViewHolder(parent)


    override fun getItemCount(): Int = requestList.size

    override fun onBindViewHolder(holer: MainViewHolder, position: Int) {
        requestList[position].let { item ->
            with(holer) {
                Log.v("asdf", item.toString())
                tvTitle.text = item.wait_user
                holer.bind(item.id)
            }
        }
    }

    inner class MainViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.list_item_request, parent, false)) {
        val parentContext = parent.context
        val tvTitle = itemView.search_fullname
        val button1 = itemView.findViewById<Button>(R.id.button1)
        val button2 = itemView.findViewById<Button>(R.id.button2)
        val spinner = itemView.findViewById<Spinner>(R.id.album_spinner)
        val album = itemView.findViewById<TextView>(R.id.make_album)
        val token = "JWT " + GlobalApplication.prefs.token
        val user_id =  GlobalApplication.prefs.user_id
        var params:HashMap<String, Any> = HashMap<String, Any>()
        var retrofit = Retrofit.Builder()
            .baseUrl(prefs.damdaServer)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        var requestService: RequestService = retrofit.create(
            RequestService::class.java)
        fun bind (waitUser: Int) {
            var nullalbumList = emptyArray<Album>()
            var albumId = 0
            var retrofit = Retrofit.Builder()
                .baseUrl(prefs.damdaServer)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val jwt = GlobalApplication.prefs.token
            val family_id = GlobalApplication.prefs.family_id.toString()
            var albumsService: AlbumsService = retrofit.create(
                AlbumsService::class.java)
            albumsService.nullAlbums("JWT $jwt", family_id, prefs.user_id!!).enqueue(object: Callback<Albums> {
                override fun onFailure(call: Call<Albums>, t: Throwable) {
                    var dialog = androidx.appcompat.app.AlertDialog.Builder(parentContext)
                    dialog.setTitle("에러")
                    dialog.setMessage("호출실패했습니다.")
                    dialog.show()
                }

                override fun onResponse(call: Call<Albums>, response: Response<Albums>) {
                    val albums = response.body()
                    nullalbumList = albums!!.data
                    if (nullalbumList.size === 1) {
                        spinner.visibility = View.GONE
                        album.visibility = View.VISIBLE
                    } else {
                        val adapter = NullAlbumAdapter(nullalbumList)
                        spinner.adapter = adapter
                        spinner.setSelection(nullalbumList.size - 1)
                        spinner.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                }

                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    albumId = adapter.getItem(position) as Int
                                }
                            }
                    }
                }
            })
            button1.setOnClickListener {
                if (albumId == 0) {
                    var dialog = androidx.appcompat.app.AlertDialog.Builder(parentContext)
                    dialog.setTitle("에러")
                    dialog.setMessage("앨범을 선택해주세요.")
                    dialog.show()
                }
                params.put("username",tvTitle.text.toString())
                params.put("albumId", albumId)
                requestService.requestAccept(token,user_id.toString(),params).enqueue(object: Callback<User> {
                    override fun onFailure(call: Call<User>, t: Throwable) {
                        Log.e("LOGIN",t.message)
                    }
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if (response.code() == 200) {
                            button1.visibility = View.GONE
                            button2.visibility = View.GONE
                            itemView.response.visibility = View.VISIBLE
                            itemView.response.text = "요청이 수락되었습니다."
                        } else {
                            var dialog = androidx.appcompat.app.AlertDialog.Builder(parentContext)
                            dialog.setTitle("에러")
                            dialog.setMessage("앨범을 선택해주세요.")
                            dialog.show()
                        }
                    }
                })
            }
            button2.setOnClickListener {
                requestService.requestDelete(token, waitUser).enqueue(object: Callback<Message> {
                    override fun onFailure(call: Call<Message>, t: Throwable) {
                        Log.e("LOGIN",t.message)
                    }
                    override fun onResponse(call: Call<Message>, response: Response<Message>) {
                        button1.visibility = View.GONE
                        button2.visibility = View.GONE
                        itemView.response.visibility = View.VISIBLE
                        itemView.response.text = "요청이 거절되었습니다."
                    }
                })
            }
        }
    }
    fun finishActivity() {
        var intent = Intent(ra, CropperActivity::class.java)
        ra.startActivity(intent)
        ra.finish()
    }
}


