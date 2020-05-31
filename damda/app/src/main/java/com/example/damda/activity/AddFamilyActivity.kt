package com.example.damda.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.damda.retrofit.model.Family
import com.example.damda.retrofit.service.FamilyService
import com.example.damda.GlobalApplication
import com.example.damda.GlobalApplication.Companion.prefs
import com.example.damda.R
import com.example.damda.retrofit.model.Message
import com.example.damda.retrofit.model.WaitUser
import kotlinx.android.synthetic.main.activity_add_family.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AddFamilyActivity : AppCompatActivity() {
    var family_info: Family? = null
    var res: Message? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_family)
        var retrofit = Retrofit.Builder()
            .baseUrl(prefs.damdaServer)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        var familyService: FamilyService = retrofit.create(
            FamilyService::class.java)
        val token = "JWT " + GlobalApplication.prefs.token
        val state = GlobalApplication.prefs.state
        if (state == "0") {
            req_ing.visibility = View.GONE
            delete_btn.visibility = View.GONE
        } else {
            req_btn.visibility = View.GONE
            req.visibility = View.GONE
            make_family.isEnabled = false
        }
        delete_btn.setOnClickListener {
            familyService.deleteRequest(token).enqueue(object: Callback<Message> {
                override fun onFailure(call: Call<Message>, t: Throwable) {
                    Log.e("LOGIN",t.message)
                    var dialog = AlertDialog.Builder(this@AddFamilyActivity)
                    dialog.setTitle("에러")
                    dialog.setMessage("호출실패했습니다.")
                    dialog.show()
                }
                override fun onResponse(call: Call<Message>, response: Response<Message>) {
                    res = response.body()
                    if (response.code() == 200) {
                        req_ing.visibility = View.GONE
                        delete_btn.visibility = View.GONE
                        req_btn.visibility = View.VISIBLE
                        req.visibility = View.VISIBLE
                        make_family.isEnabled = true
                        GlobalApplication.prefs.state = "0" }
                    Toast.makeText(
                        this@AddFamilyActivity,
                        res?.message,
                        Toast.LENGTH_SHORT).show()
                }
            })
        }

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
                    family_info = response.body()
                    Log.v("response", family_info?.id.toString())
                    GlobalApplication.prefs.family_id = family_info?.id.toString()
                    GlobalApplication.prefs.state = "3"
                    var intent = Intent(this@AddFamilyActivity, CropperActivity::class.java)
                    intent.putExtra("before", "addFamily")
                    startActivity(intent)
                    finish()
                }
            })
        }
        req_btn.setOnClickListener{
            familyService.requestFamily(token, req.text.toString()).enqueue(object: Callback<WaitUser> {
                override fun onFailure(call: Call<WaitUser>, t: Throwable) {
                    Log.e("LOGIN",t.message)
                    var dialog = AlertDialog.Builder(this@AddFamilyActivity)
                    dialog.setTitle("에러")
                    dialog.setMessage("호출실패했습니다.")
                    dialog.show()
                }
                override fun onResponse(call: Call<WaitUser>, response: Response<WaitUser>) {
                    req_ing.visibility = View.VISIBLE
                    delete_btn.visibility = View.VISIBLE
                    req_btn.visibility = View.GONE
                    req.visibility = View.GONE
                    make_family.isEnabled = false
                    req?.text = null
                    GlobalApplication.prefs.state = "1"
                    Toast.makeText(
                        this@AddFamilyActivity,
                        "요청이 완료되었습니다.",
                        Toast.LENGTH_SHORT).show()
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
