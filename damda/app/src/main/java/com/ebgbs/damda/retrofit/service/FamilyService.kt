package com.ebgbs.damda.retrofit.service

import com.ebgbs.damda.retrofit.model.*
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

    @DELETE("/api/accounts/family/")
    fun deleteRequest(@Header("Authorization") token: String): Call<Message>

    @GET("/api/accounts/family/{id}/")
    fun detailFamily(@Header("Authorization") token: String,
                            @Path("id") id: String): Call<DetailFamily>

    @GET("/api/accounts/family_info/{id}/")
    fun requestFamilyMember(@Header("Authorization") token: String,
                      @Path("id") id: String): Call<Members>

    @POST("/api/accounts/family_info/{id}/")
    fun requestSelectMember(@Header("Authorization") token: String,
                            @Path("id") id: String,
                            @Body parameters: HashMap<String, Any>): Call<Face>

}
