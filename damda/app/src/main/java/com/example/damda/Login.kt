package com.example.damda

// response 구조
data class Login(
    val token: String,
    val id: Int,
    val username: String,
    val state: Int,
    val family_id: Int
)

data class KakaoLogin(
    val token: String
)

data class UserInfo(
    val id: Int,
    val username: String,
    val state: Int,
    val family_id: Int
)
