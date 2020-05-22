package com.example.damda.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.damda.retrofit.model.Family
import com.example.damda.retrofit.service.FamilyService
import com.example.damda.GlobalApplication
import com.example.damda.R
import com.example.damda.retrofit.model.WaitUser
import kotlinx.android.synthetic.main.activity_add_family.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AddFamilyActivity : AppCompatActivity() {
    var family_info: Family? = null
    var retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8000")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    var familyService: FamilyService = retrofit.create(
        FamilyService::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_family)
        val token = "JWT " + GlobalApplication.prefs.token

        make_family.setOnClickListener{
            familyService.makeFamily(token).enqueue(object: Callback<Family> {
                override fun onFailure(call: Call<Family>, t: Throwable) {
                    Log.e("LOGIN",t.message)
                    var dialog = AlertDialog.Builder(this@AddFamilyActivity)
                    dialog.setTitle("에러")
                    dialog.setMessage("호출실패했습니다.")
                    dialog.show()
                }
                override fun onResponse(call: Call<Family>, response: Response<Family>) {
                    if (response.code() == 400) {
                        Toast.makeText(
                            this@AddFamilyActivity,
                            "잘못된 요청입니다.",
                            Toast.LENGTH_SHORT).show()
                    } else if (response.code() == 403) {
                        Toast.makeText(
                            this@AddFamilyActivity,
                            "대기중인 요청이 존재합니다.",
                            Toast.LENGTH_SHORT).show()
                    } else {
                        family_info = response.body()
                        Log.v("response", family_info?.id.toString())
                        GlobalApplication.prefs.family_id = family_info?.id.toString()
                        GlobalApplication.prefs.state = "3"
                        var intent = Intent(this@AddFamilyActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            })
        }
        req_btn.setOnClickListener{
            familyService.requestFamily(token, GlobalApplication.prefs.user_id.toString(), req.text.toString()).enqueue(object: Callback<WaitUser> {
                override fun onFailure(call: Call<WaitUser>, t: Throwable) {
                    Log.e("LOGIN",t.message)
                    var dialog = AlertDialog.Builder(this@AddFamilyActivity)
                    dialog.setTitle("에러")
                    dialog.setMessage("호출실패했습니다.")
                    dialog.show()
                }
                override fun onResponse(call: Call<WaitUser>, response: Response<WaitUser>) {
                    Log.v("response", response.body().toString())
                }
            })
        }
        logout.setOnClickListener {
            GlobalApplication.prefs.token = ""
            GlobalApplication.prefs.user_id = ""
            GlobalApplication.prefs.family_id = ""
            GlobalApplication.prefs.state = ""
            var intent = Intent(this@AddFamilyActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
