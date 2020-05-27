package com.example.damda.retrofit.model

import com.example.damda.navigation.model.Album
import java.util.*

data class Albums(
    val data: Array<Album>
)

data class Face(
    val id: Int,
    val album: String,
    val image: String,
    val name: String,
    val member: Int,
    val member_account: Account,
    val message: String
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