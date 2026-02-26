package com.rohan.fablefit.ui.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

data class CartItem(
    @SerializedName("product")
    val productId: String,
    val size: String,
    val color:String?=null,
    val quantity:Int,

)
