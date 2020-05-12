# android http 통신

## Retrofit

**모야뭐야 로그인 기준 작성**

gradle Scripts -> buildgradle (module: app) 파일에 추가

```kotlin
implementation 'com.google.code.gson:gson:2.8.5'
implementation 'com.squareup.retrofit2:retrofit:2.6.0'
implementation 'com.squareup.retrofit2:converter-gson:2.6.0'
```



레이아웃 생성 (ex.  ID or PW ...)

res -> layout -> activity_main.xml 에 email, password, button 생성



output 생성

``` kotlin
// Login.kt (MainActivity와 같은 폴더)

package com.example.myapplication
// response 구조
data class Login(
    val token: String
)
```



input, service 생성

```kotlin
// LoginService.kt (MainActivity와 같은 폴더)
package com.example.myapplication

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
interface LoginService{

    @FormUrlEncoded
    @POST("/api/api-token-auth/")
    fun requestLogin(
        @Field("email") email:String,
        @Field("password") password:String
    ) : Call<Login>
}
```



MainActivity

```kotlin
package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    var login:Login? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var retrofit = Retrofit.Builder()
            .baseUrl("https://i02b105.p.ssafy.io")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        var loginService: LoginService = retrofit.create(LoginService::class.java)

        button.setOnClickListener{
            var text1 = editText.text.toString()
            var text2 = editText2.text.toString()

        loginService.requestLogin(text1, text2).enqueue(object: Callback<Login>{
            override fun onFailure(call: Call<Login>, t: Throwable) {
                Log.e("LOGIN",t.message)
                var dialog = AlertDialog.Builder(this@MainActivity)
                dialog.setTitle("에러")
                dialog.setMessage("호출실패했습니다.")
                dialog.show()
            }
            override fun onResponse(call: Call<Login>, response: Response<Login>) {
                login = response.body()
                Log.d("LOGIN","token : "+login?.token)
                var dialog = AlertDialog.Builder(this@MainActivity)
                dialog.setTitle("토큰")
                dialog.setMessage(login?.token)
                dialog.show()
            }
        })

        }
    }
}
```

