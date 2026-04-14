package com.rohan.fablefit.ui.model

data class UserResponseModel(
    val message: String,
    val user: UserModel,

)

data class UserFileRepsonseModel(
    val message: String,
    val file: String,
)

data class UserUploadImageRepsonse(
    val message:String,
    val file:String,
)