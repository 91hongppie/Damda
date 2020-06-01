package com.example.damda.retrofit.service


import com.example.damda.navigation.model.Mission
import com.example.damda.navigation.model.Missions
import com.example.damda.retrofit.model.*
import retrofit2.Call
import retrofit2.http.*

interface MissionService {
    @GET("/api/accounts/mission/{user_id}/{id}/")
    fun requestMission(@Header("Authorization") jwt:String,
                       @Path("user_id") user_id:String,
                       @Path("id") id:Int) : Call<Missions>
}