package com.example.damda


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
    var checkemail:CheckEmail? = null

    internal val viewDisposables = CompositeDisposable()

    private lateinit var inputDataField: Array<EditText>
    private lateinit var textInputLayoutArray: Array<TextInputLayout>
    private lateinit var inputInfoMessage: Array<String>
    private var isInputCorrectData: Array<Boolean> = arrayOf(false, false, false)
    private var isCheckID = false
        set(value){
            when (value) {
                true -> {
                    btnCheckExistID.setBackgroundResource(R.drawable.round_green)
                }
                false -> {
                    btnCheckExistID.setBackgroundResource(R.drawable.round_light_brown)
                }
            }
            field = value
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        init()
        setListener()
    }
    private fun init() {
        inputDataField = arrayOf(editEmail, editPWD, editPWDConfirm)
        textInputLayoutArray = arrayOf(editEmailLayout, editPWDLayout, editPWDConfirmLayout)
        inputInfoMessage = arrayOf(getString(R.string.error_discorrent_email), getString(R.string.txtInputInfoPWD), getString(R.string.txtInputInfoRePWD))

        typingListener()
    }

    private fun setListener() {

        isCheckID = false
        var retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        var signupService: SignupService = retrofit.create(SignupService::class.java)
        btnDone.setOnClickListener {
            var params:HashMap<String, Any> = HashMap<String, Any>()
            var text1 = editEmail.text.toString()
            var text2 = editPWD.text.toString()
            params.put("username", text1)
            params.put("password",text2)
            if (isCheckID) {
                signupService.signUp(params).enqueue(object:Callback<SignUp>{
                    override fun onFailure(call: Call<SignUp>, t: Throwable) {
                        Log.e("LOGIN", t.message)
                        var dialog = AlertDialog.Builder(this@SignupActivity)
                        dialog.setTitle("에러")
                        dialog.setMessage("호출실패했습니다.")
                        dialog.show()
                    }

                    override fun onResponse(call: Call<SignUp>, response: Response<SignUp>) {
                        var dialog = AlertDialog.Builder(this@SignupActivity)
                        dialog.setTitle("성공")
                        dialog.setMessage("회원가입성공.")
                        dialog.show()
                        val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                        startActivity(intent)
                    }
                })
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

                    if(checkemail?.token=="false"){
                        toast(getString(R.string.error_exist_email))
                        isCheckID = false
                    }
                    else{
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
            }){
                //Error Block
                settingEmptyInputUI(0)
            }

        // Password
        val disposablePwd = RxTextView.textChanges(inputDataField[1])
            .map { t -> t.isEmpty() || Pattern.matches(Constants.PASSWORD_RULS, t) }
            .subscribe({ it ->
                inputDataField[2].setText("")
                reactiveInputTextViewData(1, it)
            }){
                //Error Block
            }

        // RePassword
        val disposableRePwd = RxTextView.textChanges(inputDataField[2])
            .map { t -> t.isEmpty() || inputDataField[1].text.toString() == inputDataField[2].text.toString() }
            .subscribe({ it ->
                reactiveInputTextViewData(2, it)
            }){
                //Error Block
            }


        //Email
//        val disposableEmail = RxTextView.textChanges(inputDataField[3])
//            .map { t -> t.isEmpty() || Pattern.matches(Constants.EMAIL_RULS, t) }
//            .subscribe({
//                reactiveInputTextViewData(3, it)
//            }){
//                //Error Block
//            }

        viewDisposables.addAll(disposableID, disposablePwd, disposableRePwd)
    }

    var isSuccess = false
    /**
     * 올바른 회원정보를 입력 받았는지 체크
     */
    private fun reactiveCheckCorrectData() {
        for (check in isInputCorrectData) {
            if (!check) {
                btnDone.setBackgroundColor(resources.getColor(R.color.disableButton))
                btnDone.setTextColor(resources.getColor(R.color.gray))
                btnDone.isEnabled = false
                isSuccess = false
                return
            }
        }
        btnDone.setBackgroundColor(resources.getColor(R.color.enableButton))
        btnDone.setTextColor(resources.getColor(R.color.white))
        btnDone.isEnabled = true
        isSuccess =true
    }

    /**
     * ReActive 로 입력 들어오는 데이터에 대한 결과를 UI 로 표시합니다
     */
    private fun reactiveInputTextViewData(indexPath: Int, it: Boolean) {
        if(!inputDataField[indexPath].text.toString().isEmpty()){
            isInputCorrectData[indexPath] = it
        }else{
            isInputCorrectData[indexPath] = false
        }

        textInputLayoutArray[indexPath].error = inputInfoMessage[indexPath]
        textInputLayoutArray[indexPath].isErrorEnabled = !it

        reactiveCheckCorrectData()
    }
    private fun settingEmptyInputUI(indexPath: Int){
        isInputCorrectData[indexPath] = false
        textInputLayoutArray[indexPath].isErrorEnabled = false
    }
}
