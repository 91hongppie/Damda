package com.example.damda.retrofit.model

import com.example.damda.navigation.model.Album

data class Albums(
    val data: Array<Album>
)

data class Face(
    val id: Int,
    val album: String,
    val image: String
)
data class DeleteAlbum(
    val data: String
)