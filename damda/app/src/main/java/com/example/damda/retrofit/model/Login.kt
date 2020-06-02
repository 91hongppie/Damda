package com.example.damda.retrofit.model

// response 구조
data class Login(
    val token: String,
    val id: Int,
    val username: String,
    val state: Int,
    val family: Int,
    val my_album: Boolean
)

data class KakaoLogin(
    val token: String
)

data class UserInfo(
    val id: Int,
    val username: String,
    val state: Int,
    val family: Int,
    val my_album: Boolean,
    val first_name: String,
    val birth: String,
    val is_lunar: Boolean,
    val gender: Int
)

data class Account(
    val username: String
)