package com.rohan.fablefit.ui.model

import com.google.gson.annotations.SerializedName

data class UserModel(
    @SerializedName("_id")
    val id: String = "",

    val uid: String = "",

    @SerializedName("vton_image")
    val vtonImage: String? = null,

    val type: String = "normal",

    val phone: String? = null,

    val address: List<String> = emptyList()
)