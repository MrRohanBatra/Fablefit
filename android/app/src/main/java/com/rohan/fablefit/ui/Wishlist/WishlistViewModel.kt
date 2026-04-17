package com.rohan.fablefit.ui.Wishlist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohan.fablefit.ui.model.WishlistItem
import kotlinx.coroutines.launch

class WishlistViewModel : ViewModel() {

    private val repository = WishlistRepository()

    // Full list kept in memory — used for isWishlisted() checks across screens
    var wishlistItems by mutableStateOf<List<WishlistItem>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadWishlist(uid: String) {
        viewModelScope.launch {
            isLoading = true
            repository.getWishlist(uid)
                .onSuccess { wishlistItems = it }
                .onFailure { errorMessage = it.message }
            isLoading = false
        }
    }

    /**
     * Toggle wishlist state for a product. Optimistically updates local state
     * so the heart icon flips instantly, then confirms with the server.
     */
    fun toggle(uid: String, productId: String, currentPrice: Double) {
        val alreadyIn = isWishlisted(productId)

        // Optimistic update
        wishlistItems = if (alreadyIn) {
            wishlistItems.filter { it.productId != productId }
        } else {
            wishlistItems + WishlistItem(
                uid       = uid,
                productId = productId,
                priceAtAdd = currentPrice,
            )
        }

        viewModelScope.launch {
            repository.toggleWishlist(uid, productId, currentPrice)
                .onFailure {
                    // Revert on failure
                    errorMessage = it.message
                    wishlistItems = if (alreadyIn) {
                        // Was in wishlist — put it back
                        wishlistItems + WishlistItem(uid = uid, productId = productId, priceAtAdd = currentPrice)
                    } else {
                        wishlistItems.filter { item -> item.productId != productId }
                    }
                }
        }
    }

    fun isWishlisted(productId: String): Boolean =
        wishlistItems.any { it.productId == productId }
}
