package com.example.damda.retrofit.model

// response 구조
data class Family(
    val id: Int,
    val main_member: String
)

data class WaitUser(
    val id: Int,
    val main_member: String,
    val wait_user: String
)

data class Members(
    val data: Array<UserInfo>
)
