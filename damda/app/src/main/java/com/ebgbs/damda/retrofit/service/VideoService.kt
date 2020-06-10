package com.ebgbs.damda.retrofit.service

import com.ebgbs.damda.retrofit.model.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*


interface VideoService {
    @GET("/api/albums/{id}/video/")
    fun requestVideo(@Header("Authorization") jwt:String,
                      @Path("id") id:String) : Call<Videos>
//
//    @GET("/api/albums/{id}/face/")
//    fun requestFaces(@Header("Authorization") jwt:String,
//                      @Path("id") id:String) : Call<Faces>

    @Multipart
    @POST("/api/albums/{id}/video/")
    fun updateVideo(
        @Header("Authorization") jwt:String,
        @Path("id") id: String,
        @Part("title") title: String,
        @Part file: MultipartBody.Part?
    ): Call<Message>

//    @Multipart
//    @PUT("/api/albums/album/{album_id}/")
//    fun changeAlbumImage(
//        @Header("Authorization") jwt:String,
//        @Path("album_id") album_id: Int,
//        @Part("id") id: Int,
//        @Part("image") image: String
//    ): Call<PutAlbum>
//
//    @DELETE("/api/albums/album/{album_id}/")
//    fun deleteAlbum(
//        @Header("Authorization") jwt:String,
//        @Path("album_id") album_id: Int
//    ): Call<DeleteAlbum>
}
