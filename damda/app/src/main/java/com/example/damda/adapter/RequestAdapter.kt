package com.example.damda.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.damda.GlobalApplication
import com.example.damda.R
import com.example.damda.retrofit.model.Face
import com.example.damda.retrofit.model.Message
import com.example.damda.retrofit.model.User
import com.example.damda.retrofit.model.WaitUser
import com.example.damda.retrofit.service.RequestService
import kotlinx.android.synthetic.main.list_item_request.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Member

class RequestAdapter(val requestList: Array<WaitUser>) : RecyclerView.Adapter<RequestAdapter.MainViewHolder>() {

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
        val tvTitle = itemView.search_fullname
        val button1 = itemView.findViewById<Button>(R.id.button1)
        val button2 = itemView.findViewById<Button>(R.id.button2)
        val token = "JWT " + GlobalApplication.prefs.token
        val user_id =  GlobalApplication.prefs.user_id
        var params:HashMap<String, Any> = HashMap<String, Any>()
        var retrofit = Retrofit.Builder()
            .baseUrl(parent.context.getString(R.string.damda_server))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        var requestService: RequestService = retrofit.create(
            RequestService::class.java)
        fun bind (waitUser: Int) {
            button1.setOnClickListener {
                params.put("username",tvTitle.text.toString())
                Log.v("asdf1", params.toString())
                requestService.requestAccept(token,user_id.toString(),params).enqueue(object: Callback<User> {
                    override fun onFailure(call: Call<User>, t: Throwable) {
                        Log.e("LOGIN",t.message)
                    }
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if (response.code() == 400) {
                            Log.v("400", response.body().toString())
                        } else {
                            Log.v("good",response.body().toString())
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
                        if (response.code() == 400) {
                            Log.v("400", response.body().toString())
                        } else {
                            Log.v("good",response.body().toString())
                        }
                    }
                })
            }
        }
    }
}


