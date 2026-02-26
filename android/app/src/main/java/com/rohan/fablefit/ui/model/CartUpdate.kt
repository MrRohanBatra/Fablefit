package com.rohan.fablefit.ui.model

import com.google.gson.annotations.SerializedName

data class CartUpdate(
    val uid: String,
    @SerializedName("product")
    val productId:String,
    val size: String,
    val color:String?=null,
    val quantity:Int,

)
