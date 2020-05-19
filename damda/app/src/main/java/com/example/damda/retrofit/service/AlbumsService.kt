package com.example.damda.retrofit.service

import com.example.damda.retrofit.model.Albums
import retrofit2.Call
import retrofit2.http.*

interface AlbumsService {
    @GET("/api/albums/")
    fun requestAlbums(@Header("Authorization") jwt:String) : Call<Albums>
}
