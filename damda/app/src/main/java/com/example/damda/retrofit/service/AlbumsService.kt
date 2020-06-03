package com.example.damda.retrofit.service

import com.example.damda.navigation.model.Album
import com.example.damda.retrofit.model.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*


interface AlbumsService {
    @GET("/api/albums/{family_id}/{user_id}/")
    fun requestAlbums(@Header("Authorization") jwt:String,
                      @Path("family_id") family_id:String,
                      @Path("user_id") user_id: String) : Call<Albums>

    @GET("/api/albums/{family_id}/{user_id}/face/")
    fun requestFaces(@Header("Authorization") jwt:String,
                     @Path("family_id") family_id:String,
                     @Path("user_id") user_id:String) : Call<Faces>

    @GET("/api/albums/{family_id}/albums/{user_id}/")
    fun nullAlbums(@Header("Authorization") jwt:String,
                   @Path("family_id") family_id:String,
                   @Path("user_id") user_id: String) : Call<Albums>

    @Multipart
    @POST("/api/albums/{family_id}/{path_id}/face/")
    fun updateFace(
        @Header("Authorization") jwt:String,
        @Path("family_id") family_id: String,
        @Path("path_id") path_id: String,
        @Part("album_name") album_name: String,
        @Part("user_id") user_id: Int,
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
