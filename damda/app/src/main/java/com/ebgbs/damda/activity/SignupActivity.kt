package com.ebgbs.damda.activity


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ebgbs.damda.*
import com.ebgbs.damda.GlobalApplication.Companion.prefs
import com.ebgbs.damda.retrofit.model.CheckEmail
import com.ebgbs.damda.retrofit.model.SignUp
import com.ebgbs.damda.retrofit.service.SignupService
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_signup.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.regex.Pattern


class SignupActivity : AppCompatActivity() {
    var checkemail: CheckEmail? = null
    var gender = ""
    val items = listOf("성별", "남자", "여자")

    internal val viewDisposables = CompositeDisposable()

    private lateinit var inputDataField: Array<EditText>
    private lateinit var textInputLayoutArray: Array<TextInputLayout>
    private lateinit var inputInfoMessage: Array<String>
    private var isInputCorrectData: Array<Boolean> = arrayOf(false, false, false)
    private var isCheckID = false
        set(value) {
            when (value) {
                true -> {
                    btnCheckExistID.setBackgroundColor(resources.getColor(R.color.disableButton))
                }
                false -> {
                    btnCheckExistID.setBackgroundColor(resources.getColor(R.color.gray))
                }
            }
            field = value
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        init()
        typingListener()
        setListener()
    }

    private fun init() {
        inputDataField = arrayOf(editEmail, editPWD, editPWDConfirm)
        textInputLayoutArray = arrayOf(editEmailLayout, editPWDLayout, editPWDConfirmLayout)
        inputInfoMessage = arrayOf(
            getString(R.string.error_discorrent_email), getString(
                R.string.txtInputInfoPWD
            ), getString(R.string.txtInputInfoRePWD)
        )
        typingListener()
        val adapter = ArrayAdapter(this, R.layout.list_item_gender, items)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
        signup_layout.setOnClickListener {
            var view = this.currentFocus

            if (view != null) {
                val inputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }

    private fun setListener() {
        dataPicker.updateDate(1990, 0, 1)
        isCheckID = false
        var retrofit = Retrofit.Builder()
            .baseUrl(prefs.damdaServer)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        var signupService: SignupService = retrofit.create(
            SignupService::class.java
        )
        btnDone.setOnClickListener {

            var params: HashMap<String, Any> = HashMap<String, Any>()
            var text1 = editEmail.text.toString()
            var text2 = editPWD.text.toString()
            params.put("username", text1)
            params.put("password", text2)
            params.put("first_name", name.text.toString())
            params.put(
                "birth",
                "${dataPicker.year}-${dataPicker.month + 1}-${dataPicker.dayOfMonth}"
            )
            params.put("is_lunar", is_lunar.isChecked)
            params.put("gender", gender)

            if (isCheckID) {
                if (gender == "0") {
                    toast("성별을 선택해주세요.")
                } else {
                    signupService.signUp(params).enqueue(object : Callback<SignUp> {
                        override fun onFailure(call: Call<SignUp>, t: Throwable) {
                            Log.e("LOGIN", t.message)
                            var dialog = AlertDialog.Builder(this@SignupActivity)
                            dialog.setTitle("에러")
                            dialog.setMessage("호출실패했습니다.")
                            dialog.show()
                        }

                        override fun onResponse(call: Call<SignUp>, response: Response<SignUp>) {
                            toast("회원 가입 되었습니다.")
                            val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finishAffinity()
                        }
                    })
                }
            } else {
                toast(getString(R.string.error_do_not_check_id))
            }

        }
        btnCheckExistID.setOnClickListener {
            var text1 = editEmail.text.toString()
            if (editEmail.text.toString().isEmpty()) {
                isCheckID = false
                toast(getString(R.string.error_do_not_input_id))
                return@setOnClickListener
            }
            signupService.requestCheckEmail(text1).enqueue(object : Callback<CheckEmail> {
                override fun onFailure(call: Call<CheckEmail>, t: Throwable) {
                    Log.e("LOGIN", t.message)
                    var dialog = AlertDialog.Builder(this@SignupActivity)
                    dialog.setTitle("에러")
                    dialog.setMessage("호출실패했습니다.")
                    dialog.show()
                }

                override fun onResponse(call: Call<CheckEmail>, response: Response<CheckEmail>) {
                    checkemail = response.body()
                    Log.d("ChekID", "token : " + checkemail?.token)

                    if (checkemail?.token == "false") {
                        toast(getString(R.string.error_exist_email))
                        isCheckID = false
                    } else {
                        toast(getString(R.string.can_use_email))
                        isCheckID = true
                    }
                }
            })
        }
    }

    /**
     * 각 필드별 회원가입 조건이 맞는지 비동기 체크
     */
    private fun typingListener() {
        // ID
        val disposableID = RxTextView.textChanges(inputDataField[0])
            .map { t -> t.length in 1..7 }
            .subscribe({ it ->
                isCheckID = false
                reactiveInputTextViewData(0, !it)
            }) {
                //Error Block
                settingEmptyInputUI(0)
            }

        // Password
        val disposablePwd = RxTextView.textChanges(inputDataField[1])
            .map { t -> t.isEmpty() || Pattern.matches(Constants.PASSWORD_RULS, t) }
            .subscribe({ it ->
                inputDataField[2].setText("")
                reactiveInputTextViewData(1, it)
            }) {
                //Error Block
            }

        // RePassword
        val disposableRePwd = RxTextView.textChanges(inputDataField[2])
            .map { t -> t.isEmpty() || inputDataField[1].text.toString() == inputDataField[2].text.toString() }
            .subscribe({ it ->
                reactiveInputTextViewData(2, it)
            }) {
                //Error Block
            }


        viewDisposables.addAll(disposableID, disposablePwd, disposableRePwd)
    }

    var isSuccess = false

    /**
     * 올바른 회원정보를 입력 받았는지 체크
     */
    private fun reactiveCheckCorrectData() {
        for (check in isInputCorrectData) {
            if (!check) {
                btnDone.setBackgroundColor(resources.getColor(R.color.gray))
                btnDone.setTextColor(resources.getColor(R.color.white))
                btnDone.isEnabled = false
                isSuccess = false
                return
            }
        }
        btnDone.setBackgroundColor(resources.getColor(R.color.disableButton))
        btnDone.setTextColor(resources.getColor(R.color.white))
        btnDone.isEnabled = true
        isSuccess = true
    }

    /**
     * ReActive 로 입력 들어오는 데이터에 대한 결과를 UI 로 표시합니다
     */
    private fun reactiveInputTextViewData(indexPath: Int, it: Boolean) {
        if (!inputDataField[indexPath].text.toString().isEmpty()) {
            isInputCorrectData[indexPath] = it
        } else {
            isInputCorrectData[indexPath] = false
        }
        if (!textInputLayoutArray[indexPath].isErrorEnabled) {
        textInputLayoutArray[indexPath].error = inputInfoMessage[indexPath]
        }
        textInputLayoutArray[indexPath].isErrorEnabled = !it

        reactiveCheckCorrectData()
    }

    private fun settingEmptyInputUI(indexPath: Int) {
        isInputCorrectData[indexPath] = false
        textInputLayoutArray[indexPath].isErrorEnabled = false
    }
}

