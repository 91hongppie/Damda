package com.example.damda

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface SignupService{

    @POST("/api/accounts/checkemail/")
    fun requestCheckEmail(
        @Body parameters: HashMap<String,Any>
    ) : Call<CheckEmail>

}
