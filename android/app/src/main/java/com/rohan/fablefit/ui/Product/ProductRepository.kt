package com.rohan.fablefit.ui.Product

import com.rohan.fablefit.network.RetrofitInstance
import com.rohan.fablefit.ui.model.Product


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
    suspend fun searchProduct(searchQuery: String,limit:Int=20): Result<List<Product>>{
        return runCatching {
            val resp= RetrofitInstance.api.searchProducts(searchQuery=searchQuery,limit=10);
            if(resp.isSuccessful){
                resp.body()?:throw Exception("Product Body is null")
            }
            else{
                throw Exception("HTTP ${resp.code()}");
            }
        }
    }
}