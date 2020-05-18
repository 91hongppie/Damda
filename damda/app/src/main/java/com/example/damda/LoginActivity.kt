package com.example.damda

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.kakao.auth.ApiResponseCallback
import com.kakao.auth.AuthService
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.auth.network.response.AccessTokenInfoResponse
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class LoginActivity : AppCompatActivity() {
    var login:Login? = null
    var kakaoLogin:KakaoLogin? = null
    var userInfo:UserInfo? = null
    private var callback: SessionCallback = SessionCallback()
    var retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8000")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    var loginService: LoginService = retrofit.create(LoginService::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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
                    Log.v("response", login.toString())

                    GlobalApplication.prefs.myEditText = login?.token
                    if (login?.family === 0) {
                        var intent = Intent(this@LoginActivity, AddFamilyActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        var intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            })

        }
        signup_button.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
        if (GlobalApplication.prefs.myEditText !== "") {
            var intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
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

                    loginService.requestKakao(params)?.enqueue(object: Callback<KakaoLogin>{
                        override fun onFailure(call: Call<KakaoLogin>, t: Throwable) {
                            Log.e("LOGIN",t.message)
                            var dialog = AlertDialog.Builder(this@LoginActivity)
                            dialog.setTitle("에러")
                            dialog.setMessage("호출실패했습니다.")
                            dialog.show()
                        }
                        override fun onResponse(call: Call<KakaoLogin>, response: Response<KakaoLogin>) {
                            kakaoLogin = response.body()
                            GlobalApplication.prefs.myEditText = kakaoLogin?.token
                            val token = "JWT " + kakaoLogin?.token
                            loginService.requestUser(token)?.enqueue(object: Callback<UserInfo>{
                                override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                                    Log.e("LOGIN",t.message)
                                    var dialog = AlertDialog.Builder(this@LoginActivity)
                                    dialog.setTitle("에러")
                                    dialog.setMessage("호출실패했습니다.")
                                    dialog.show()
                                }
                                override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
                                    userInfo = response.body()
                                    Log.v("UserInfo",userInfo?.family.toString())
                                    if (userInfo?.family == 0) {
                                        var intent = Intent(this@LoginActivity, AddFamilyActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        var intent = Intent(this@LoginActivity, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
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

    private fun redirectSignupActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}


