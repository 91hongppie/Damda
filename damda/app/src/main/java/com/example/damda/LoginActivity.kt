package com.example.damda

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {
    var login:Login? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var retrofit = Retrofit.Builder()
            .baseUrl("https://loacalhost:8000/damda/login")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        var loginService: LoginService = retrofit.create(LoginService::class.java)

        login_button.setOnClickListener{
            var text1 = email.text.toString()
            var text2 = password.text.toString()

            loginService.requestLogin(text1, text2).enqueue(object: Callback<Login>{
                override fun onFailure(call: Call<Login>, t: Throwable) {
                    Log.e("LOGIN",t.message)
                    var dialog = AlertDialog.Builder(this@LoginActivity)
                    dialog.setTitle("에러")
                    dialog.setMessage("호출실패했습니다.")
                    dialog.show()
                }
                override fun onResponse(call: Call<Login>, response: Response<Login>) {
                    login = response.body()
                    Log.d("LOGIN","token : "+login?.token)
                    var dialog = AlertDialog.Builder(this@LoginActivity)
                    dialog.setTitle("토큰")
                    dialog.setMessage(login?.token)
                    dialog.show()
                }
            })

        }
    }
}
