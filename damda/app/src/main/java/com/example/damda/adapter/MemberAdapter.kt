package com.example.damda.adapter

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.damda.GlobalApplication
import com.example.damda.GlobalApplication.Companion.prefs
import com.example.damda.R
import com.example.damda.retrofit.model.Face
//import com.example.damda.retrofit.model.RequestData
import com.example.damda.retrofit.model.*
import com.example.damda.retrofit.service.FamilyService
import kotlinx.android.synthetic.main.list_item_member.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MemberAdapter(val albumList: Array<Face>) : RecyclerView.Adapter<MemberAdapter.MainViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = MainViewHolder(parent)


    override fun getItemCount(): Int = albumList.size

    override fun onBindViewHolder(holer: MainViewHolder, position: Int) {
        albumList[position].let { item ->
            with(holer) {
                val username = item.member_account
                title.text = item.call
                if (username !== null) {
                    account.text = username.username}
                holer.bind(item.member, item.id)
            }
        }
    }

    inner class MainViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.list_item_member, parent, false)) {
        val title = itemView.member_name
        val account = itemView.connect_account
        val content = itemView.list_item_memeber_layout
        val builder = AlertDialog.Builder(parent.context)
        var members: Members? = null
        val context = parent.context
        val url = prefs.damdaServer
        fun bind (chk: Int, face_id: Int) {
            content.setOnClickListener {
                if (GlobalApplication.prefs.state == "3") {
                    val jwt = GlobalApplication.prefs.token
                    val family_id = GlobalApplication.prefs.family_id.toString()
                    var retrofit = Retrofit.Builder()
                        .baseUrl(url)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                    var familyService: FamilyService = retrofit.create(
                        FamilyService::class.java
                    )
                    var singleItems = mutableListOf("선택 안함")
                    var member_id = mutableListOf(0)
                    var checkedItem = 0
                    familyService.requestFamilyMember("JWT $jwt", family_id)
                        .enqueue(object : Callback<Members> {
                            override fun onFailure(call: Call<Members>, t: Throwable) {
                            }

                            override fun onResponse(
                                call: Call<Members>,
                                response: Response<Members>
                            ) {
                                members = response.body()
                                for ((index, i) in members!!.data.withIndex()) {
                                    singleItems.add(i.username)
                                    member_id.add(i.id)
                                    if (i.username == account.text) {
                                        checkedItem = index + 1
                                    }
                                }
                                val userList = singleItems.toTypedArray()
                                builder.setTitle("연결 멤버를 선택하세요")
                                builder.setNeutralButton(R.string.cancel) { dialog, which ->
                                }
                                builder.setPositiveButton(R.string.ok) { dialog, which ->
                                    var params: HashMap<String, Any> = HashMap<String, Any>()
                                    params.put("member_id", member_id[checkedItem])
                                    params.put("face_id", face_id)
                                    familyService.requestSelectMember("JWT $jwt", family_id, params)
                                        .enqueue(object : Callback<Face> {
                                            override fun onFailure(call: Call<Face>, t: Throwable) {
                                            }

                                            override fun onResponse(
                                                call: Call<Face>,
                                                response: Response<Face>
                                            ) {
                                                account.text = singleItems[checkedItem]
                                            }
                                        })
                                }
                                builder.setSingleChoiceItems(
                                    userList,
                                    checkedItem
                                ) { dialog, which ->
                                    checkedItem = which
                                }
                                builder.show()
                            }
                        })
                } else {
                    Toast.makeText(context, "계정 변경은 담장만 가능합니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}


