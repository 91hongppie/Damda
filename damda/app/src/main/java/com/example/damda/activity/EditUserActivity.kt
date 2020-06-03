package com.example.damda.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.damda.GlobalApplication
import com.example.damda.R
import com.example.damda.adapter.FamilyListAdapter
import com.example.damda.retrofit.model.CheckEmail
import com.example.damda.retrofit.model.DetailFamily
import com.example.damda.retrofit.model.SignUp
import com.example.damda.retrofit.model.UserInfo
import com.example.damda.retrofit.service.FamilyService
import com.example.damda.retrofit.service.LoginService
import com.example.damda.retrofit.service.SignupService
import kotlinx.android.synthetic.main.activity_family_list.*
import kotlinx.android.synthetic.main.activity_signup.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EditUserActivity : AppCompatActivity() {
    private var gender= ""
    private val items = listOf("성별", "남자", "여자")
    private val token = "JWT " + GlobalApplication.prefs.token
    private var retrofit = Retrofit.Builder()
        .baseUrl(GlobalApplication.prefs.damdaServer)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)
        setSupportActionBar(findViewById(R.id.toolbar))
        val actionBar = supportActionBar
        actionBar?.setDisplayShowCustomEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        var isKakao = intent.getIntExtra("isKakao", 0)
        init(isKakao)
        setListener(isKakao)
    }
    private fun init(isKakao: Int) {
        val adapter = ArrayAdapter(this, R.layout.list_item_gender, items)
        var email = findViewById<TextView>(R.id.editEmail)
        var loginService: LoginService = retrofit.create(
            LoginService::class.java)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                gender = position.toString()
            }
        }
        if (isKakao == 0) {
        loginService?.requestUser(token)?.enqueue(object: Callback<UserInfo>{
            override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                var dialog = AlertDialog.Builder(this@EditUserActivity)
                dialog.setTitle("에러")
                dialog.setMessage("호출실패했습니다.")
                dialog.show()
            }
            override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
                val userInfo = response.body()
                email.setText(userInfo?.username.toString())
                name.setText(userInfo?.first_name.toString())
                spinner.setSelection(userInfo!!.gender)
                is_lunar.setChecked(userInfo!!.is_lunar)
                var array = userInfo.birth.split("-")
                dataPicker.updateDate(array[0].toInt(), array[1].toInt()-1, array[2].toInt())
            }
        })
    } else {
            email.setText(intent.getStringExtra("username"))
            name.setText(intent.getStringExtra("name"))
            dataPicker.updateDate(1990, 0,1)
        }
    }

    private fun setListener(isKakao: Int) {
        var signupService: SignupService = retrofit.create(
            SignupService::class.java)
        btnDone.setOnClickListener {
            if (gender == "0") {
                toast("성별을 선택해주세요.")
            } else {
            var params:HashMap<String, Any> = HashMap<String, Any>()
            var text1 = findViewById<TextView>(R.id.editEmail)
            params.put("username", text1.text)
            params.put("first_name", name.text.toString())
            params.put("birth", "${dataPicker.year}-${dataPicker.month + 1}-${dataPicker.dayOfMonth}")
            params.put("is_lunar", is_lunar.isChecked)
            params.put("gender", gender)
            Log.v("asdf", gender)
            signupService.ediUser(token, params).enqueue(object:Callback<SignUp>{
                override fun onFailure(call: Call<SignUp>, t: Throwable) {
                    Log.e("LOGIN", t.message)
                    var dialog = AlertDialog.Builder(this@EditUserActivity)
                    dialog.setTitle("에러")
                    dialog.setMessage("호출실패했습니다.")
                    dialog.show()
                }

                override fun onResponse(call: Call<SignUp>, response: Response<SignUp>) {
                    toast("수정 되었습니다.")
                    prefs.gender = gender.toInt()
                    if (isKakao == 1) {
                        if (GlobalApplication.prefs.state!!.toInt() < 2) {
                            var intent = Intent(this@EditUserActivity, AddFamilyActivity::class.java)
                            startActivity(intent)
                        } else {
                            var intent = Intent(this@EditUserActivity, MainActivity::class.java)
                            startActivity(intent)
                        }
                    }
                    finish()
                }
            })
            }
        }
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

