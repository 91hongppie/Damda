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
    var family_id:Family? = null
    var retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8000")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    var familyService: FamilyService = retrofit.create(FamilyService::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_family)
        make_family.setOnClickListener{
            val token = "JWT " + GlobalApplication.prefs.myEditText
            familyService.requestUser(token).enqueue(object: Callback<Family> {
                override fun onFailure(call: Call<Family>, t: Throwable) {
                    Log.e("LOGIN",t.message)
                    var dialog = AlertDialog.Builder(this@AddFamilyActivity)
                    dialog.setTitle("에러")
                    dialog.setMessage("호출실패했습니다.")
                    dialog.show()
                }
                override fun onResponse(call: Call<Family>, response: Response<Family>) {
                    family_id = response.body()
                    Log.v("response", family_id.toString())
//                    var intent = Intent(this@AddFamilyActivity, MainActivity::class.java)
//                    startActivity(intent)
//                    finish()
                }
            })
        }
//        req_btn.setOnClickListener{
//            familyService.requestUser(req.text.toString()).enqueue(object: Callback<Family> {
//                override fun onFailure(call: Call<Login>, t: Throwable) {
//                    Log.e("LOGIN",t.message)
//                    var dialog = AlertDialog.Builder(this@AddFamilyActivity)
//                    dialog.setTitle("에러")
//                    dialog.setMessage("호출실패했습니다.")
//                    dialog.show()
//                }
//                override fun onResponse(call: Call<Login>, response: Response<Family>) {
//                    Log.v("response", response.body().toString())
//                    var intent = Intent(this@AddFamilyActivity, MainActivity::class.java)
//                    startActivity(intent)
//                    finish()
//                }
//            })
//        }
    }
}
