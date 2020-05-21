package com.example.damda.retrofit.service

import com.example.damda.retrofit.model.Family
import com.example.damda.retrofit.model.WaitUser
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
    fun makeFamily(@Header("Authorization") token: String): Call<Family>

    @GET("/api/accounts/family/")
    fun requestFamily(@Header("Authorization") token: String,
                      @Query ("req") req:String): Call<WaitUser>
}
