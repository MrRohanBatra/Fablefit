package com.rohan.fablefit.ui.Product

import com.rohan.fablefit.network.RetrofitInstance
import com.rohan.fablefit.ui.model.Product
import retrofit2.Retrofit

class ProductRepository {
    suspend fun getProductById(id: String): Result<Product>{
        return runCatching {
            val response = RetrofitInstance.api.getProduct(id)

            if (response.isSuccessful) {
                response.body()
                    ?: throw Exception("Product Body is null")
            } else {
                throw Exception("HTTP ${response.code()}")
            }
        }
    }
}