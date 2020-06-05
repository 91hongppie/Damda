package com.ebgbs.damda.retrofit.service

import com.ebgbs.damda.retrofit.model.*
import retrofit2.Call
import retrofit2.http.*


interface RequestService {
    @GET("/api/accounts/user/{id}/")
    fun requestWaitUser(@Header("Authorization") jwt:String,
                      @Path("id") id:String) : Call<WaitUsers>

    @POST("/api/accounts/user/{id}/")
    fun requestAccept(@Header("Authorization") token: String,
                      @Path("id") id: String,
                      @Body parameters: HashMap<String, Any>): Call<User>

    @DELETE("/api/accounts/user/{id}/")
    fun requestDelete(@Header("Authorization") jwt:String,
                      @Path("id") id: Int) : Call<Message>
}
