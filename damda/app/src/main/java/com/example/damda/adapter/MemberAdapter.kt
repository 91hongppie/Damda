package com.example.damda.adapter

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.damda.R
import com.example.damda.navigation.model.Album
import com.example.damda.retrofit.model.Face
import com.example.damda.retrofit.model.RequestData
import kotlinx.android.synthetic.main.list_item_member.view.*
import kotlinx.android.synthetic.main.list_item_request.view.*

class MemberAdapter(val albumList: Array<Face>) : RecyclerView.Adapter<MemberAdapter.MainViewHolder>() {

    var items: MutableList<RequestData> = mutableListOf(RequestData("Title1"),
        RequestData("Title2"),RequestData("Title3"))

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = MainViewHolder(parent)


    override fun getItemCount(): Int = albumList.size

    override fun onBindViewHolder(holer: MainViewHolder, position: Int) {
        albumList[position].let { item ->
            with(holer) {
                Log.v("asdf", item.toString())
                title.text = item.name
            }
        }
        holer.bind()
    }

    inner class MainViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.list_item_member, parent, false)) {
        val title = itemView.member_name
        val content = itemView.list_item_memeber_layout
        val builder = AlertDialog.Builder(parent.context)
        fun bind () {
            content.setOnClickListener {
                val singleItems = arrayOf("Item 1", "Item 2", "Item 3")
                val checkedItem = 1
                builder.setTitle("연결 멤버를 선택하세요")
                builder.setNeutralButton(R.string.cancel) { dialog, which ->
                }
                builder.setPositiveButton(R.string.ok) { dialog, which ->
                }
                builder.setSingleChoiceItems(singleItems, checkedItem) { dialog, which ->
                }
                builder.show()
                 }
        }
    }
}


