package com.rohan.fablefit.ui.model

import com.google.gson.annotations.SerializedName

data class VtonRequestModel(
    @SerializedName("uid")
    val uid: String,
    @SerializedName("product_id")
    val productId: String,
)

