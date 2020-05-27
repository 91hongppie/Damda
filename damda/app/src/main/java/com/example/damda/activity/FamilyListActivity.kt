package com.example.damda.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.damda.GlobalApplication
import com.example.damda.R
import com.example.damda.adapter.FamilyListAdapter
import com.example.damda.adapter.MemberAdapter
import com.example.damda.retrofit.model.DetailFamily
import com.example.damda.retrofit.model.Face
import com.example.damda.retrofit.model.Faces
import com.example.damda.retrofit.model.User
import com.example.damda.retrofit.service.AlbumsService
import com.example.damda.retrofit.service.FamilyService
import kotlinx.android.synthetic.main.activity_add_member.*
import kotlinx.android.synthetic.main.activity_family_list.*
import kotlinx.android.synthetic.main.fragment_user.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FamilyListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_family_list)
        setSupportActionBar(findViewById(R.id.toolbar))
        val actionBar = supportActionBar
        actionBar?.setDisplayShowCustomEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        val token = "JWT " + GlobalApplication.prefs.token
        val id =  GlobalApplication.prefs.family_id.toString()
        var familyList : DetailFamily? = null
        var retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.damda_server))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        var familyService : FamilyService = retrofit.create(
            FamilyService::class.java)
        familyService.detailFamily(token, id).enqueue(object: Callback<DetailFamily> {
            override fun onFailure(call: Call<DetailFamily>, t: Throwable) {
                Log.e("LOGIN",t.message)
            }
            override fun onResponse(call: Call<DetailFamily>, response: Response<DetailFamily>) {
                familyList = response.body()
                family_list.adapter = FamilyListAdapter(familyList!!.members)
            }
        })
        family_list.layoutManager = LinearLayoutManager(this)
        family_list.addItemDecoration(DividerItemDecoration(this@FamilyListActivity, LinearLayoutManager.VERTICAL))
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
