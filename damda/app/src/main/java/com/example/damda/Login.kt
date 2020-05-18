package com.example.damda

// response 구조
data class Login(
    val token: String,
    val id: Int,
    val username: String,
    val is_main_member: Boolean,
    val family: Int
)

data class KakaoLogin(
    val token: String
)

data class UserInfo(
    val id: Int,
    val username: String,
    val is_main_member: Boolean,
    val family: Int
)
