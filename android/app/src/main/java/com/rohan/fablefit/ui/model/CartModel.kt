package com.rohan.fablefit.ui.model

import com.google.gson.annotations.SerializedName

data class CartModel(
    @SerializedName("_id")
    val id: String,

    val uid: String,
    val items: List<CartItem>,
    val totalPrice: Float
)
