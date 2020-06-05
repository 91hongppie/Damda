package com.ebgbs.damda.retrofit.model

import com.ebgbs.damda.navigation.model.Album
import com.ebgbs.damda.navigation.model.Video

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