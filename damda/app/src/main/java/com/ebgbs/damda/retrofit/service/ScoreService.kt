package com.ebgbs.damda.retrofit.service

import com.ebgbs.damda.navigation.model.Score
import retrofit2.Call
import retrofit2.http.*

interface ScoreService {
    @GET("/api/accounts/score/{user_id}/")
    fun requestScore(@Header("Authorization") jwt:String,
                       @Path("user_id") user_id:String) : Call<Score>

    @Multipart
    @PUT("/api/accounts/score/{user_id}/")
    fun changeScore(@Header("Authorization") jwt:String,
                    @Path("user_id") user_id:String,
                    @Part("score") score:Int,
                    @Part("mission_id") mission_id:Int) : Call<Score>
}