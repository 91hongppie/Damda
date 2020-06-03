package com.example.damda.retrofit.service

import com.example.damda.navigation.model.Quizs
import retrofit2.Call
import retrofit2.http.*

interface QuizService {
    @GET("/api/accounts/quiz/{family_id}/{user_id}/")
    fun requestQuiz(@Header("Authorization") jwt:String,
                    @Path("family_id") family_id:String,
                    @Path("user_id") user_id:String) : Call<Quizs>

    @Multipart
    @PUT("/api/accounts/quiz/{family_id}/{user_id}/")
    fun makeQuiz(@Header("Authorization") jwt:String,
                 @Path("family_id") family_id: String,
                 @Path("user_id") user_id: String,
                 @Part("id") quiz_id: Int,
                 @Part("answer") quiz_answer: String) : Call<Int>

}