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

data class WaitUsers(
    val data: Array<WaitUser>
)

data class User(
    val id: Int,
    val username : String,
    val state : Int,
    val family : Int
)

//data class UserInfo(
//    val id: Int,
//    val username: String,
//    val state: Int,
//    val family: Int
//)
