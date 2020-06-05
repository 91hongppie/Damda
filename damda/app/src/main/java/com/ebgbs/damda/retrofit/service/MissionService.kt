package com.ebgbs.damda.retrofit.service


import com.ebgbs.damda.navigation.model.Mission
import com.ebgbs.damda.navigation.model.Missions
import retrofit2.Call
import retrofit2.http.*

interface MissionService {
    @GET("/api/accounts/mission/{user_id}/{id}/")
    fun requestMission(@Header("Authorization") jwt:String,
                       @Path("user_id") user_id:String,
                       @Path("id") id:Int) : Call<Missions>

    @Multipart
    @PUT("/api/accounts/mission/{user_id}/{id}/")
    fun changeMission(@Header("Authorization") jwt:String,
                      @Path("user_id") user_id:String,
                      @Path("id") id:Int,
                      @Part("mission_title") mission_title:String,
                      @Part("mission_id") mission_id:Int) : Call<Mission>

    @GET("/api/albums/mission/{family_id}/{user_id}/")
    fun requestParentPhotos(@Header("Authorization") jwt:String,
                            @Path("family_id") family_id:String,
                            @Path("user_id") user_id:String) : Call<Int>
}