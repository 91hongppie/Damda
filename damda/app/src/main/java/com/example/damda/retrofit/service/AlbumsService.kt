package com.example.damda.retrofit.service

import com.example.damda.retrofit.model.Albums
import com.example.damda.retrofit.model.Face
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*


interface AlbumsService {
    @GET("/api/albums/{id}/")
    fun requestAlbums(@Header("Authorization") jwt:String,
                      @Path("id") id:String) : Call<Albums>

    @Multipart
    @POST("/api/albums/{id}/face/")
    fun updateFace(
        @Header("Authorization") jwt:String,
        @Path("id") id: String,
        @Part("album_name") album_name: String,
        @Part img_name: MultipartBody.Part?
    ): Call<Face>
}
