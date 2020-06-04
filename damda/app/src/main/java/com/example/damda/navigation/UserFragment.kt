package com.example.damda.navigation

import android.app.NotificationChannel
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.damda.GlobalApplication
import com.example.damda.GlobalApplication.Companion.prefs
import com.example.damda.R
import com.example.damda.activity.*
import com.example.damda.retrofit.model.DetailFamily
import com.example.damda.retrofit.model.Message
import com.example.damda.retrofit.model.User
import com.example.damda.retrofit.service.FamilyService
import com.jakewharton.rxbinding2.widget.color
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.fragment_user.view.*
import kotlinx.android.synthetic.main.list_item_request.view.*
import okhttp3.FormBody
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

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
                view.family_id.text = id
                view.main_member.text = familyList?.main_member
            }
        })
        view.logout.setOnClickListener {
            val jwt = GlobalApplication.prefs.token
            val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id", "${prefs.user_id}")
                .addFormDataPart("device_token", "${prefs.device_token}")
                .build()

            val request = Request.Builder().addHeader("Authorization", "JWT $jwt")
                .url(prefs.damdaServer + "/api/accounts/logout/")
                .post(requestBody)
                .build()

            val client = OkHttpClient()
            val callback = Callback1()

            client.newCall(request).enqueue(callback)

            prefs.token = ""
            prefs.user_id = ""
            prefs.family_id = ""
            prefs.state = ""
            prefs.my_album = false
            prefs.gender = 0

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
        view.alarm.setOnClickListener {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, activity?.packageName)
            } else {
                intent.putExtra("app_package", activity?.packageName)
                intent.putExtra("app_uid", activity?.applicationInfo?.uid)
            }
            startActivity(intent)
        }
        view.editUser.setOnClickListener {
            var intent = Intent(context, EditUserActivity::class.java)
            intent.putExtra("isKakao", 0)
            startActivity(intent)
        }

        view.auto_upload_switch.setOnCheckedChangeListener{ buttonView, isChecked ->
            prefs.autoStatus = isChecked
            if (isChecked) {
                view.use_data_switch.isClickable = true
                view.use_data_switch.setTextColor(Color.BLACK)
            } else {
                view.use_data_switch.isClickable = false
                view.use_data_switch.setTextColor(Color.GRAY)
            }
        }
        if (prefs.autoStatus) {
            view.use_data_switch.setTextColor(Color.BLACK)
        } else {
            view.use_data_switch.isClickable = false
            view.use_data_switch.setTextColor(Color.GRAY)
        }
        view.use_data_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            prefs.mobileAutoUpload = isChecked
        }
        view.auto_upload_switch.isChecked = prefs.autoStatus
        view.use_data_switch.isChecked = prefs.mobileAutoUpload

        return view
    }

    inner class Callback1 : okhttp3.Callback {
        override fun onFailure(call: okhttp3.Call, e: IOException) {
            Log.d("Sever response", "error: $e")

        }

        override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
            val status = response.code()
            Log.d("server response", "$status - 디바이스 삭제 완료")
        }
    }
}