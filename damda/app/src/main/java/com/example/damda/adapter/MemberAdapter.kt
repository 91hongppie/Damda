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
            }
        }
    }

    inner class MainViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.list_item_member, parent, false)) {
        val title = itemView.member_name
        val account = itemView.connect_account
    }
}


