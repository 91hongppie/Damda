package com.example.damda.retrofit.service

import com.example.damda.retrofit.model.Albums
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import retrofit2.Call
import retrofit2.http.*

interface AlbumsService {
    @GET("/api/albums/{id}")
    fun requestAlbums(@Header("Authorization") jwt:String,
                      @Path("id") id:String) : Call<Albums>
}
