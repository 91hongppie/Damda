package com.ebgbs.damda.retrofit.model

data class SignUp(
    val id: Int,
    val username: String,
    val is_main_member: Boolean,
    val family: Int
)
data class CheckEmail(
    val token: String
)
data class FindEmail(
    val id: Int,
    val username: String,
    val password: String
)
data class ChangePassword(
    val id: Int,
    val username: String,
    val password: String
)