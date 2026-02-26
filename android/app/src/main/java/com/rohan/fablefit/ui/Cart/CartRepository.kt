package com.rohan.fablefit.ui.Cart

import com.rohan.fablefit.network.RetrofitInstance
import com.rohan.fablefit.ui.model.CartModel
import com.rohan.fablefit.ui.model.CartResponse
import com.rohan.fablefit.ui.model.CartUpdate

class CartRepository {
    suspend fun getCartForUser(uid: String): Result<CartModel> {
        return runCatching {
            val response= RetrofitInstance.api.getCartForUser(uid)
            if(response.isSuccessful){
                response.body()?:throw Exception("Empty Cart returned from server");
            }
            else{
                throw Exception("HTTP ${response.code()}")
            }
        }
    }
    suspend fun updateCart(item: CartUpdate): Result<CartResponse>{
        return runCatching {
            val response= RetrofitInstance.api.updateCart(item);
            if(response.isSuccessful){
                response.body()
                    ?:throw Exception("Empty body")
            }
            else{
                throw Exception("HTTP ${response.code()}")
            }
        }
    }
    suspend fun addItemToCart(item: CartUpdate): Result<CartResponse>{
        return runCatching {
        val response= RetrofitInstance.api.addProductInCart(item)
            if(response.isSuccessful){
                response.body()
                    ?:throw Exception("Error receiving confirmation for updated cart")
            }
            else{
                throw Exception("HTTP ${response.code()}")
            }
        }
    }
    suspend fun removeItemFromCart(item: CartUpdate): Result<CartResponse>{
        return runCatching {
            val response= RetrofitInstance.api.removeProductFromCart(item)
            if(response.isSuccessful){
                response.body()
                    ?:throw Exception("Error in receiving confirmation for removal of item");
            }
            else{
                throw Exception("HTTP ${response.code()}")
            }
        }
    }
}
