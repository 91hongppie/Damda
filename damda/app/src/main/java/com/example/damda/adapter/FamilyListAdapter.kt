package com.example.damda.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.damda.R
import com.example.damda.retrofit.model.*
import kotlinx.android.synthetic.main.list_item_family.view.*

class FamilyListAdapter(val members: Array<User>) :
    RecyclerView.Adapter<FamilyListAdapter.MainViewHolder>() {

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
        LayoutInflater.from(parent.context).inflate(R.layout.list_item_family, parent, false)
    ) {
        val name = itemView.name
        val account = itemView.account
        val birthday = itemView.birthday
    }
}


