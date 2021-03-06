package com.ebgbs.damda.retrofit.service

import com.ebgbs.damda.retrofit.model.*

import retrofit2.Call
import retrofit2.http.*


interface SignupService{
    @Headers("Accept: application/json")
    @GET("/api/accounts/signup/")
    fun requestCheckEmail(
        @Query ("username") text1:String
    ) : Call<CheckEmail>

    @FormUrlEncoded
    @POST("/api/accounts/signup/")
    fun signUp(
        @FieldMap parameters:HashMap<String,Any>
    ): Call<SignUp>


    @GET("/api/accounts/findpassword/")
    fun findEmail(
        @Query("username") text1:String
    ): Call<FindEmail>

    @FormUrlEncoded
    @POST("/api/accounts/findpassword/")
    fun findPassword(
        @FieldMap parameters:HashMap<String,Any>
    ): Call<ChangePassword>

    @PUT("/api/accounts/user/")
    fun ediUser(
        @Header("Authorization") jwt:String,
        @Body parameters:HashMap<String,Any>
    ): Call<SignUp>

}
