package com.ebgbs.damda.retrofit.model

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
    val family : Int,
    val first_name: String,
    val birth: String
)

//data class UserInfo(
//    val id: Int,
//    val username: String,
//    val state: Int,
//    val family: Int
//

data class Members(
    val data: Array<UserInfo>
)

data class Message(
    val message: String
)

data class  DetailFamily(
    val id: Int,
    val main_member: String,
    val members: Array<User>
)
