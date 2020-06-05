package com.ebgbs.damda.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ebgbs.damda.R
import com.ebgbs.damda.retrofit.model.Face
import kotlinx.android.synthetic.main.list_item_member.view.*


class MemberAdapter(val albumList: Array<Face>) :
    RecyclerView.Adapter<MemberAdapter.MainViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = MainViewHolder(parent)


    override fun getItemCount(): Int = albumList.size

    override fun onBindViewHolder(holer: MainViewHolder, position: Int) {
        albumList[position].let { item ->
            with(holer) {
                val username = item.member_account
                title.text = item.call
                if (username !== null) {
                    account.text = username.username
                }
            }
        }
    }

    inner class MainViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.list_item_member, parent, false)
    ) {
        val title = itemView.member_name
        val account = itemView.connect_account
    }
}


