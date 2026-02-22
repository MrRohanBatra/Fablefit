package com.rohan.fablefit.network

import com.rohan.fablefit.ui.model.HomeSection
import com.rohan.fablefit.ui.model.Product
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("/api/ui/banners")
    suspend fun getHomeSections(): Response<List<HomeSection>>

    @GET("/api/products/id/{id}")
    suspend fun getProduct(
        @Path("id") id: String
    ): Response<Product>
}