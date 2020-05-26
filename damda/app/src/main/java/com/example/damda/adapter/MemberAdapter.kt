package com.example.damda.adapter

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.damda.GlobalApplication
import com.example.damda.R
import com.example.damda.navigation.model.Album
import com.example.damda.retrofit.model.Face
//import com.example.damda.retrofit.model.RequestData
import com.example.damda.retrofit.model.*
import com.example.damda.retrofit.service.FamilyService
import com.example.damda.retrofit.service.SignupService
import kotlinx.android.synthetic.main.activity_add_member.*
import kotlinx.android.synthetic.main.list_item_member.view.*
import kotlinx.android.synthetic.main.list_item_request.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Member

class MemberAdapter(val albumList: Array<Face>) : RecyclerView.Adapter<MemberAdapter.MainViewHolder>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = MainViewHolder(parent)


    override fun getItemCount(): Int = albumList.size

    override fun onBindViewHolder(holer: MainViewHolder, position: Int) {
        albumList[position].let { item ->
            with(holer) {
                title.text = item.name
                holer.bind(item.member, item.id)
            }
        }
    }

    inner class MainViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.list_item_member, parent, false)) {
        val title = itemView.member_name
        val content = itemView.list_item_memeber_layout
        val builder = AlertDialog.Builder(parent.context)
        var members: Members? = null

        fun bind (chk: Int, face_id: Int) {
            content.setOnClickListener {
                val jwt = GlobalApplication.prefs.token
                val family_id = GlobalApplication.prefs.family_id.toString()
                var retrofit = Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8000")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                var familyService: FamilyService = retrofit.create(
                    FamilyService::class.java)
                var singleItems = mutableListOf("선택 안함")
                var member_id = mutableListOf(0)
                var checkedItem = chk
                familyService.requestFamilyMember("JWT $jwt", family_id).enqueue(object: Callback<Members> {
                    override fun onFailure(call: Call<Members>, t: Throwable) {
                    }
                    override fun onResponse(call: Call<Members>, response: Response<Members>) {
                        members = response.body()
                        for (i in members!!.data) {
                            singleItems.add(i.username)
                            member_id.add(i.id)
                        }
                        val userList = singleItems.toTypedArray()
                        builder.setTitle("연결 멤버를 선택하세요")
                        builder.setNeutralButton(R.string.cancel) { dialog, which ->
                        }
                        builder.setPositiveButton(R.string.ok) { dialog, which ->
                            var params:HashMap<String, Any> = HashMap<String, Any>()
                            params.put("member_id", member_id[checkedItem])
                            params.put("face_id", face_id)
                            familyService.requestSelectMember("JWT $jwt", family_id, params).enqueue(object: Callback<Face> {
                                override fun onFailure(call: Call<Face>, t: Throwable) {
                                }

                                override fun onResponse(call: Call<Face>, response: Response<Face>) {
                                }
                            })
                        }
                        builder.setSingleChoiceItems(userList, checkedItem) { dialog, which ->
                            checkedItem = which
                        }
                        builder.show()
                    }
                })
            }
        }
    }
}


