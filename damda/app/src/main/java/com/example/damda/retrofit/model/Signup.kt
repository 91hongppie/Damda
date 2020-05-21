package com.example.damda.retrofit.model

data class SignUp(
    val id: Int,
    val username: String,
    val is_main_member: Boolean,
    val family: Int
)
data class CheckEmail(
    val token: String
)
