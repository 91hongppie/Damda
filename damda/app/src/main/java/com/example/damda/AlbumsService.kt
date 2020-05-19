package com.example.damda

import retrofit2.Call
import retrofit2.http.*

interface AlbumsService {
    @GET("/api/albums/")
    fun requestAlbums(@Header("Authorization") jwt:String) : Call<Albums>
}
