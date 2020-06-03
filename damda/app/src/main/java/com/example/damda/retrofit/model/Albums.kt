package com.example.damda.retrofit.model

import com.example.damda.navigation.model.Album
import com.example.damda.navigation.model.Video
import java.util.*

data class Albums(
    val data: Array<Album>
)

data class Face(
    val id: Int,
    val image: String,
    val title: String,
    val member: Int,
    val member_account: Account,
    val message: String,
    val call: String
)

data class Faces(
    val data: Array<Face>
)

data class DeleteAlbum(
    val data: String
)

data class PutAlbum(
    val id: Int,
    val image: String
)

data class Videos(
    val data: Array<Video>
)