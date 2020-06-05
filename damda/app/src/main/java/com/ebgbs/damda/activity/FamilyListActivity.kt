package com.ebgbs.damda.activity

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ebgbs.damda.GlobalApplication.Companion.prefs
import com.ebgbs.damda.R
import com.ebgbs.damda.adapter.FamilyListAdapter
import com.ebgbs.damda.retrofit.model.DetailFamily
import com.ebgbs.damda.retrofit.service.FamilyService
import kotlinx.android.synthetic.main.activity_family_list.*
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
        val token = "JWT " + prefs.token
        val id =  prefs.family_id.toString()
        var familyList : DetailFamily? = null
        var retrofit = Retrofit.Builder()
            .baseUrl(prefs.damdaServer)
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
