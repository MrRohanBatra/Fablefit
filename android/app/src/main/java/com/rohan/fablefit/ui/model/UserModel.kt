package com.rohan.fablefit.ui.model

import com.google.gson.annotations.SerializedName

data class UserModel(
    val uid: String = "",
    val phone: String? = null,
    @SerializedName("vton_image")
    val vtonImage: String? = null,
    val type: String = "normal",
    @SerializedName("total_spent")
    val totalSpent: Float = 0f,
    val tier: String = "Bronze"
)
