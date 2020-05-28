package com.example.damda.activity

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.damda.GlobalApplication
import com.example.damda.GlobalApplication.Companion.prefs
import com.example.damda.R
import com.example.damda.adapter.RequestAdapter
import com.example.damda.retrofit.model.WaitUser
import com.example.damda.retrofit.model.WaitUsers
import com.example.damda.retrofit.service.RequestService
import kotlinx.android.synthetic.main.activity_request_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RequestListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_list)
        setSupportActionBar(findViewById(R.id.toolbar))
        val actionBar = supportActionBar
        actionBar?.setDisplayShowCustomEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)
//        rv_main_list.adapter = RequestAdapter()
//        rv_main_list.layoutManager = LinearLayoutManager(this)
        rv_main_list.layoutManager = LinearLayoutManager(this)
        var waitUsers: WaitUsers? = null
        var waitList = emptyArray<WaitUser>()
        var retrofit = Retrofit.Builder()
            .baseUrl(prefs.damdaServer)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val jwt = GlobalApplication.prefs.token
        val user_id = GlobalApplication.prefs.user_id.toString()
        var requestService: RequestService = retrofit.create(
            RequestService::class.java)

        requestService.requestWaitUser("JWT $jwt", user_id).enqueue(object: Callback<WaitUsers> {
            override fun onFailure(call: Call<WaitUsers>, t: Throwable) {
                Log.v("face", t.toString())
                var dialog = AlertDialog.Builder(this@RequestListActivity)
                dialog.setTitle("에러")
                dialog.setMessage("호출실패했습니다.")
                dialog.show()
            }

            override fun onResponse(call: Call<WaitUsers>, response: Response<WaitUsers>) {
                waitUsers = response.body()
                waitList = waitUsers!!.data
                if (waitList.size > 0) {
                    rv_main_list.adapter = RequestAdapter(waitList)
                    rv_main_list.addItemDecoration(DividerItemDecoration(this@RequestListActivity, LinearLayoutManager.VERTICAL))
                }
            }
        })

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
