package com.example.damda

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
interface LoginService{

    @FormUrlEncoded
    @POST("/api-token-auth/")
    fun requestLogin(
        @Field("username") email:String,
        @Field("password") password:String
    ) : Call<Login>
}
