package com.rohan.fablefit.ui.model

import com.google.gson.annotations.SerializedName

data class CartModel(
    @SerializedName("_id")
    val id: String,

    val uid: String,
    val items: List<CartItem>,
    val totalPrice: Float,

    // Loyalty fields injected by the backend on every cart response
    val tier: String = "Bronze",
    val discountPct: Float = 0f,
) {
    /** Human-readable discount label, e.g. "15% OFF" */
    val discountLabel: String
        get() = if (discountPct > 0f) "${(discountPct * 100).toInt()}% OFF" else ""

    val hasDiscount: Boolean
        get() = discountPct > 0f
}
