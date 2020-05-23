package com.example.damda.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.damda.R
import com.example.damda.retrofit.model.RequestData
import kotlinx.android.synthetic.main.list_item_request.view.*

class RequestAdapter : RecyclerView.Adapter<RequestAdapter.MainViewHolder>() {

    var items: MutableList<RequestData> = mutableListOf(RequestData("Title1"),
        RequestData("Title2"),RequestData("Title3"))

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = MainViewHolder(parent)


    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holer: MainViewHolder, position: Int) {
        items[position].let { item ->
            with(holer) {
                Log.v("asdf", item.toString())
                tvTitle.text = item.title
            }
        }
        holer.bind()
    }

    inner class MainViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.list_item_request, parent, false)) {
        val tvTitle = itemView.search_fullname
        val button1 = itemView.findViewById<Button>(R.id.button1)
        val button2 = itemView.findViewById<Button>(R.id.button2)
        fun bind () {
            button1.setOnClickListener {
                Log.v("asdf1", tvTitle.text.toString()) }
            button2.setOnClickListener {
                Log.v("asdf2]", tvTitle.toString())
            }
        }
    }
}


