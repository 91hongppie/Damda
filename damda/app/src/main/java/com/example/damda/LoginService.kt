package com.example.damda

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*


interface LoginService{
    @FormUrlEncoded
    @POST("/api-token-auth/")
    fun requestLogin(
        @Field("username") email:String,
        @Field("password") password:String
    ) : Call<Login>

    @Headers("Accept: application/json")
    @POST("/api/accounts/rest-auth/kakao/")
    fun requestKakao(@Body parameters: HashMap<String, Any>): Call<KakaoLogin>
}
