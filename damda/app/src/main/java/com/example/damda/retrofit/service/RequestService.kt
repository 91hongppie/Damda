package com.example.damda.retrofit.service

import com.example.damda.retrofit.model.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*


interface RequestService {
    @GET("/api/accounts/family/{id}/")
    fun requestWaitUser(@Header("Authorization") jwt:String,
                      @Path("id") id:String) : Call<WaitUsers>

    @POST("/api/accounts/family/{id}/")
    fun requestAccept(@Header("Authorization") token: String,
                      @Path("id") id: String,
                      @Body parameters: HashMap<String, Any>): Call<User>

    @DELETE("/api/accounts/family/{id}/")
    fun requestDelete(@Header("Authorization") jwt:String,
                      @Path("id") id: Int) : Call<Message>
}
