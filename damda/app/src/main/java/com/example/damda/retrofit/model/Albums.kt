package com.example.damda.retrofit.model

import com.example.damda.navigation.model.Album

data class Albums(
    val data: Array<Album>
)

data class Face(
    val id: Int,
    val album_name: String,
    val path: String
)