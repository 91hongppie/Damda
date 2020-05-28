package com.example.damda.retrofit.service

import com.example.damda.navigation.model.Album
import com.example.damda.retrofit.model.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*


interface AlbumsService {
    @GET("/api/albums/{id}/")
    fun requestAlbums(@Header("Authorization") jwt:String,
                      @Path("id") id:String) : Call<Albums>

    @GET("/api/albums/{id}/face/")
    fun requestFaces(@Header("Authorization") jwt:String,
                      @Path("id") id:String) : Call<Faces>

    @Multipart
    @POST("/api/albums/{id}/face/")
    fun updateFace(
        @Header("Authorization") jwt:String,
        @Path("id") id: String,
        @Part("album_name") album_name: String,
        @Part img_name: MultipartBody.Part?
    ): Call<Face>

    @Multipart
    @PUT("/api/albums/album/{album_id}/")
    fun changeAlbumImage(
        @Header("Authorization") jwt:String,
        @Path("album_id") album_id: Int,
        @Part("id") id: Int,
        @Part("image") image: String
    ): Call<PutAlbum>

    @DELETE("/api/albums/album/{album_id}/")
    fun deleteAlbum(
        @Header("Authorization") jwt:String,
        @Path("album_id") album_id: Int
    ): Call<DeleteAlbum>
}
