package com.example.damda.retrofit.service

import com.example.damda.retrofit.model.CheckEmail
import com.example.damda.retrofit.model.SignUp
import retrofit2.Call
import retrofit2.http.*


interface SignupService{
    @Headers("Accept: application/json")
    @GET("/api/accounts/checkemail/")
    fun requestCheckEmail(
        @Query ("username") text1:String
    ) : Call<CheckEmail>
    @FormUrlEncoded
    @POST("/api/accounts/signup/")
    fun signUp(
        @FieldMap parameters:HashMap<String,Any>
    ): Call<SignUp>



}
