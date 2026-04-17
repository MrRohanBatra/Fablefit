package com.rohan.fablefit.ui.model

import com.google.gson.annotations.SerializedName

data class WishlistItem(
    @SerializedName("_id")
    val id: String = "",
    val uid: String = "",
    @SerializedName("product_id")
    val productId: String = "",
    @SerializedName("price_at_add")
    val priceAtAdd: Double = 0.0,
)

data class WishlistToggleRequest(
    val uid: String,
    @SerializedName("product_id")
    val productId: String,
    @SerializedName("price_at_add")
    val priceAtAdd: Double,
)

data class WishlistToggleResponse(
    val action: String,       // "added" | "removed"
    val message: String,
    val wishlisted: Boolean,
)

data class FcmTokenUpdate(
    val uid: String,
    @SerializedName("fcm_token")
    val fcmToken: String,
)
