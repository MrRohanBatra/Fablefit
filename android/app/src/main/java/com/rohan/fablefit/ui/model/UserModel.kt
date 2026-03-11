package com.rohan.fablefit.ui.model

import com.google.gson.annotations.SerializedName

data class UserModel(
    @SerializedName("_id")
    val id: String="",
    val uid: String="",
    @SerializedName("vton_image")
    val vtonImage:String="",
    val type:String="normal",
    val phone:String="",
    val address: List<String>
)
