package com.example.damda.navigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.damda.GlobalApplication
import com.example.damda.R
import com.example.damda.activity.*
import com.example.damda.retrofit.model.DetailFamily
import com.example.damda.retrofit.model.Message
import com.example.damda.retrofit.model.User
import com.example.damda.retrofit.service.FamilyService
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.fragment_user.view.*
import kotlinx.android.synthetic.main.list_item_request.view.*
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_user, container, false)
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
                view.family_id.text = id
                view.main_member.text = familyList?.main_member
            }
        })
        view.logout.setOnClickListener {
            GlobalApplication.prefs.token = ""
            GlobalApplication.prefs.user_id = ""
            GlobalApplication.prefs.family_id = ""
            GlobalApplication.prefs.state = ""
            var intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
        view.list_request.setOnClickListener {
            if (GlobalApplication.prefs.state == "3") {
            var intent = Intent(context, RequestListActivity::class.java)
            startActivity(intent)
            } else {
                Toast.makeText(context, "담장만 이용 가능합니다.", Toast.LENGTH_SHORT).show()
            }
        }
        view.family_list.setOnClickListener {
            var intent = Intent(context, FamilyListActivity::class.java)
            startActivity(intent)
        }
        view.family_btn.setOnClickListener {
            var intent = Intent(context, AddMemberActivity::class.java)
            startActivity(intent)
        }
        return view
    }
}