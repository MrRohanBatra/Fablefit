package com.rohan.fablefit.ui.Wishlist

import com.rohan.fablefit.network.RetrofitInstance
import com.rohan.fablefit.ui.model.WishlistItem
import com.rohan.fablefit.ui.model.WishlistToggleRequest
import com.rohan.fablefit.ui.model.WishlistToggleResponse

class WishlistRepository {

    suspend fun getWishlist(uid: String): Result<List<WishlistItem>> {
        return runCatching {
            val response = RetrofitInstance.api.getWishlist(uid)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty wishlist body")
            } else {
                throw Exception("HTTP ${response.code()}")
            }
        }
    }

    suspend fun toggleWishlist(
        uid: String,
        productId: String,
        priceAtAdd: Double,
    ): Result<WishlistToggleResponse> {
        return runCatching {
            val response = RetrofitInstance.api.toggleWishlist(
                WishlistToggleRequest(uid, productId, priceAtAdd)
            )
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty toggle response")
            } else {
                throw Exception("HTTP ${response.code()}")
            }
        }
    }
}
