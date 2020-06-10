package com.ebgbs.damda.retrofit.service

import com.ebgbs.damda.retrofit.model.KakaoLogin
import com.ebgbs.damda.retrofit.model.Login
import com.ebgbs.damda.retrofit.model.UserInfo
import retrofit2.Call
import retrofit2.http.*


interface LoginService{
    @FormUrlEncoded
    @POST("/api/api-token-auth/")
    fun requestLogin(
        @Field("username") email:String,
        @Field("password") password:String
    ) : Call<Login>

    @Headers("Accept: application/json")
    @POST("/api/accounts/rest-auth/kakao/")
    fun requestKakao(@Body parameters: HashMap<String, Any>): Call<KakaoLogin>

    @GET("/api/accounts/user/")
    fun requestUser(@Header("Authorization") token: String): Call<UserInfo>

}
