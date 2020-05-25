package com.example.damda.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.damda.R
import com.example.damda.navigation.model.Album
import com.example.damda.retrofit.model.RequestData
import kotlinx.android.synthetic.main.list_item_member.view.*
import kotlinx.android.synthetic.main.list_item_request.view.*

class MemberAdapter(val albumList: Array<Album>) : RecyclerView.Adapter<MemberAdapter.MainViewHolder>() {

    var items: MutableList<RequestData> = mutableListOf(RequestData("Title1"),
        RequestData("Title2"),RequestData("Title3"))

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = MainViewHolder(parent)


    override fun getItemCount(): Int = albumList.size

    override fun onBindViewHolder(holer: MainViewHolder, position: Int) {
        albumList[position].let { item ->
            with(holer) {
                Log.v("asdf", item.toString())
                title.text = item.title
            }
        }
    }

    inner class MainViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.list_item_member, parent, false)) {
        val title = itemView.member_name
    }
}


