package com.example.damda.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.damda.*
import com.example.damda.retrofit.model.KakaoLogin
import com.example.damda.retrofit.model.Login
import com.example.damda.retrofit.model.UserInfo
import com.example.damda.retrofit.service.LoginService
import com.google.firebase.iid.FirebaseInstanceId
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


class LoginActivity : AppCompatActivity() {
    var login: Login? = null
    var kakaoLogin: KakaoLogin? = null
    var userInfo: UserInfo? = null
    var retrofit:Retrofit? = null
    var loginService: LoginService? = null
    private var callback: SessionCallback = SessionCallback()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        retrofit = Retrofit.Builder()
        .baseUrl(getString(R.string.damda_server))
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        loginService = retrofit?.create(
            LoginService::class.java)
        var token = FirebaseInstanceId.getInstance().token
        if (token != null) {
            sendRegistrationToServer(token)
            Log.d("Exist token", "Token: $token")
        }

        login_button.setOnClickListener{
            var text1 = email.text.toString()
            var text2 = password.text.toString()

            loginService?.requestLogin(text1, text2)?.enqueue(object: Callback<Login>{
                override fun onFailure(call: Call<Login>, t: Throwable) {
                    Log.e("LOGIN",t.message)
                    var dialog = AlertDialog.Builder(this@LoginActivity)
                    dialog.setTitle("에러")
                    dialog.setMessage("호출실패했습니다.")
                    dialog.show()
                }
                override fun onResponse(call: Call<Login>, response: Response<Login>) {
                    login = response.body()
                    if (response.code() == 400) {
                        var dialog = AlertDialog.Builder(this@LoginActivity)
                        dialog.setTitle("에러")
                        dialog.setMessage("아이디와 비밀번호를 확인해주세요.")
                        dialog.show()
                    } else {
                        Log.v("response", login.toString())
                        GlobalApplication.prefs.token = login?.token
                        GlobalApplication.prefs.user_id = login?.id.toString()
                        GlobalApplication.prefs.family_id = login?.family.toString()
                        GlobalApplication.prefs.state = login?.state.toString()
                        moveActivity(login?.state!!.toInt())
                    }
                }
            })
        }
        signup_button.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
        if (GlobalApplication.prefs.token !== "") {
            if (GlobalApplication.prefs.state == "1" || GlobalApplication.prefs.state == "0") {
                var intent = Intent(this@LoginActivity, AddFamilyActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                var intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        } else {
        Session.getCurrentSession().addCallback(callback)}
    }

    private inner class SessionCallback : ISessionCallback {
        override fun onSessionOpened() {
            // 로그인 세션이 열렸을 때
            UserManagement.getInstance().me( object : MeV2ResponseCallback() {
                override fun onSuccess(result: MeV2Response?) {
                    // 로그인이 성공했을 때
                    val accessToken = Session.getCurrentSession().tokenInfo.accessToken
                    var params:HashMap<String, Any> = HashMap<String, Any>()
                    params.put("access_token", accessToken)

                    loginService?.requestKakao(params)?.enqueue(object: Callback<KakaoLogin>{
                        override fun onFailure(call: Call<KakaoLogin>, t: Throwable) {
                            Log.e("LOGIN",t.message)
                            var dialog = AlertDialog.Builder(this@LoginActivity)
                            dialog.setTitle("에러")
                            dialog.setMessage("호출실패했습니다.")
                            dialog.show()
                        }
                        override fun onResponse(call: Call<KakaoLogin>, response: Response<KakaoLogin>) {
                            kakaoLogin = response.body()
                            GlobalApplication.prefs.token = kakaoLogin?.token
                            val token = "JWT " + kakaoLogin?.token
                            Log.v("token", token)
                            loginService?.requestUser(token)?.enqueue(object: Callback<UserInfo>{
                                override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                                    Log.e("LOGIN",t.message)
                                    var dialog = AlertDialog.Builder(this@LoginActivity)
                                    dialog.setTitle("에러")
                                    dialog.setMessage("호출실패했습니다.")
                                    dialog.show()
                                }
                                override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
                                    userInfo = response.body()
                                    GlobalApplication.prefs.user_id = userInfo?.id.toString()
                                    GlobalApplication.prefs.family_id = userInfo?.family.toString()
                                    GlobalApplication.prefs.state = login?.state.toString()
                                    Log.v("UserInfo",userInfo?.toString())
                                    moveActivity(userInfo?.state!!.toInt())
                                }
                            })
                        }
                    })
                }
                override fun onSessionClosed(errorResult: ErrorResult?) {
                    // 로그인 도중 세션이 비정상적인 이유로 닫혔을 때
                    Toast.makeText(
                        this@LoginActivity,
                        "세션이 닫혔습니다. 다시 시도해주세요 : ${errorResult.toString()}",
                        Toast.LENGTH_SHORT).show()
                }
            })
        }
        override fun onSessionOpenFailed(exception: KakaoException?) {
            // 로그인 세션이 정상적으로 열리지 않았을 때
            if (exception != null) {
                com.kakao.util.helper.log.Logger.e(exception)
                Toast.makeText(
                    this@LoginActivity,
                    "로그인 도중 오류가 발생했습니다. 인터넷 연결을 확인해주세요 : $exception",
                    Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun moveActivity(state: Int) {
        if (state < 2) {
            var intent = Intent(this@LoginActivity, AddFamilyActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            var intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun sendRegistrationToServer(token: String) {
        var thread = NetworkThread()
        thread.start()
    }

    inner class NetworkThread: Thread() {
        override fun run() {

            var client = OkHttpClient()

            var builder = Request.Builder()
            var url = builder.url("http://google.com")
            var request = url.build()

            var callback = Callback1()

            client.newCall(request).enqueue(callback)

        }
    }

    inner class Callback1: okhttp3.Callback {
        override fun onFailure(call: okhttp3.Call, e: IOException) {

        }

        override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {

            var result = response.body()?.string()

            Log.d("response", "result: $result")
        }
    }
}


