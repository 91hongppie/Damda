package com.example.damda.adapter

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.damda.GlobalApplication
import com.example.damda.R
import com.example.damda.retrofit.model.Face
//import com.example.damda.retrofit.model.RequestData
import com.example.damda.retrofit.model.*
import com.example.damda.retrofit.service.FamilyService
import kotlinx.android.synthetic.main.list_item_family.view.*
import kotlinx.android.synthetic.main.list_item_member.view.*
import kotlinx.android.synthetic.main.list_item_member.view.list_item_memeber_layout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.format.DateTimeFormatter

class FamilyListAdapter(val members: Array<User>) : RecyclerView.Adapter<FamilyListAdapter.MainViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = MainViewHolder(parent)


    override fun getItemCount(): Int = members.size

    override fun onBindViewHolder(holer: MainViewHolder, position: Int) {
        members[position].let { item ->
            with(holer) {
                name.text = item.first_name
                account.text = item.username
                if (item.birth == null) {
                    birthday.text = "생일 정보가 없습니다."
                } else {
                    var array = item.birth.split("-")
                    birthday.text = "${array[1]}월 ${array[2]}일"
                }
            }
        }
    }

    inner class MainViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.list_item_family, parent, false)) {
        val name = itemView.name
        val account = itemView.account
        val birthday = itemView.birthday
    }
}


