package com.rohan.fablefit.ui.Cart

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohan.fablefit.ui.model.CartItem
import com.rohan.fablefit.ui.model.CartModel
import com.rohan.fablefit.ui.model.CartUpdate
import kotlinx.coroutines.launch

class CartViewModel: ViewModel(){
    private val cartRepository= CartRepository()
    var currentCart by  mutableStateOf<CartModel?>(null)
        private set

    var uiState by mutableStateOf<CartModelUiState>(CartModelUiState.Loading)
        private set
    fun getUserCart(uid: String){
        viewModelScope.launch {
            uiState= CartModelUiState.Loading;
            cartRepository.getCartForUser(uid)
                .onSuccess {
                    currentCart=it
                uiState= CartModelUiState.Success(it)
                }
                .onFailure{
                    uiState= CartModelUiState.Error(it.message?:"Failed to load Cart")
                }

        }
    }
    fun addItemToCart(item: CartUpdate){
        viewModelScope.launch {
            uiState= CartModelUiState.Loading;
            cartRepository.addItemToCart(item)
                .onSuccess {
                    currentCart=it.cart
                    uiState= CartModelUiState.Success(it.cart)
                }
                .onFailure {
                    uiState= CartModelUiState.Error(it.message?:"Failed to add item in cart");
                }
        }
    }
    fun removeItemFromCart(item: CartUpdate){
        viewModelScope.launch {
//            uiState= CartModelUiState.Loading;
            uiState= CartModelUiState.ItemUpdate
            cartRepository.removeItemFromCart(item)
                .onSuccess {
                    currentCart=it.cart
                    uiState= CartModelUiState.Success(it.cart)
                }
                .onFailure {
                    uiState= CartModelUiState.Error(it.message?:"Failed to remove item from cart");
                }
        }
    }
    fun updateItemInCart(item: CartUpdate){
        viewModelScope.launch {
            uiState= CartModelUiState.ItemUpdate
            cartRepository.updateCart(item)
                .onSuccess {
                    currentCart=it.cart
                    uiState= CartModelUiState.Success(it.cart);
                }
                .onFailure {
                    uiState= CartModelUiState.Error(it.message?:"Failed to update item in cart")
                }
        }
    }
    fun isProductInCart(productId: String, size: String): Boolean {
        return currentCart?.items?.any { it.productId == productId && it.size == size } ?: false
    }

    fun getProductQuantity(productId: String, size: String): Int {
        return currentCart?.items?.find { it.productId == productId && it.size == size }?.quantity ?: 1
    }
}

sealed class CartModelUiState {
    object Loading : CartModelUiState()
    data class Success(val cart: CartModel) : CartModelUiState()
//    data class SuccessList(val products:List<Product>): CartModelUiState()
    data class Error(val message: String) : CartModelUiState()
    object ItemUpdate: CartModelUiState()
}
