package com.example.damda.retrofit.service

import com.example.damda.retrofit.model.Family
import retrofit2.Call
import retrofit2.http.*


interface FamilyService{
//    @FormUrlEncoded
//    @POST("/api-token-auth/")
//    fun requestLogin(
//        @Field("username") email:String,
//        @Field("password") password:String
//    ) : Call<Login>
//
//    @Headers("Accept: application/json")
//    @POST("/api/accounts/rest-auth/kakao/")
//    fun requestKakao(@Body parameters: HashMap<String, Any>): Call<KakaoLogin>

    @POST("/api/accounts/family/")
    fun requestUser(@Header("Authorization") token: String): Call<Family>
}
