package com.example.damda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_add_family.*
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AddFamilyActivity : AppCompatActivity() {
    var retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8000")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    var familyService: FamilyService = retrofit.create(FamilyService::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_family)
        make_family.setOnClickListener{
            familyService.requestLogin(text1, text2).enqueue(object: Callback<Login> {
                override fun onFailure(call: Call<Login>, t: Throwable) {
                    Log.e("LOGIN",t.message)
                    var dialog = AlertDialog.Builder(this@LoginActivity)
                    dialog.setTitle("에러")
                    dialog.setMessage("호출실패했습니다.")
                    dialog.show()
                }
                override fun onResponse(call: Call<Login>, response: Response<Login>) {
                    login = response.body()
                    Log.v("response", login.toString())

                    GlobalApplication.prefs.myEditText = login?.token
                    var intent = Intent(this@AddFamilyActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            })
        }
        req_btn.setOnClickListener{
            familyService.requestLogin(req.text.toString()).enqueue(object: Callback<Login> {
                override fun onFailure(call: Call<Login>, t: Throwable) {
                    Log.e("LOGIN",t.message)
                    var dialog = AlertDialog.Builder(this@LoginActivity)
                    dialog.setTitle("에러")
                    dialog.setMessage("호출실패했습니다.")
                    dialog.show()
                }
                override fun onResponse(call: Call<Login>, response: Response<Login>) {
                    login = response.body()
                    Log.v("response", login.toString())

                    GlobalApplication.prefs.myEditText = login?.token
                    var intent = Intent(this@AddFamilyActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            })
        }
    }
}
