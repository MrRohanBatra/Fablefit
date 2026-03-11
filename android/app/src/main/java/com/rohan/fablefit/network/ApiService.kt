package com.rohan.fablefit.network

import com.rohan.fablefit.ui.model.CartModel
import com.rohan.fablefit.ui.model.CartResponse
import com.rohan.fablefit.ui.model.CartUpdate
import com.rohan.fablefit.ui.model.HomeSection
import com.rohan.fablefit.ui.model.Product
import com.rohan.fablefit.ui.model.UserModel
import com.rohan.fablefit.ui.model.UserResponseModel
import com.rohan.fablefit.ui.model.UserUploadImageRepsonse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("/api/ui/banners")
    suspend fun getHomeSections(): Response<List<HomeSection>>
//--------Products-------------------
    @GET("/api/products/id/{id}")
    suspend fun getProduct(
        @Path("id") id: String
    ): Response<Product>

    @GET("/api/products/search")
    suspend fun searchProducts(
        @Query("s") searchQuery: String,
        @Query("q") limit: Int=20
    ): Response<List<Product>>
    //--------Cart-------------------

    @GET("/api/cart/{uid}")
    suspend fun getCartForUser(
        @Path("uid") uid: String
    ): Response<CartModel>

    @POST("/api/cart/add")
    suspend fun addProductInCart(
        @Body item: CartUpdate
    ): Response<CartResponse>

    @POST("/api/cart/remove")
    suspend fun removeProductFromCart(
        @Body item: CartUpdate,
    ): Response<CartResponse>

    @POST("/api/cart/update")
    suspend fun updateCart(
        @Body item: CartUpdate,
    ): Response<CartResponse>
    @GET("/api/user/{uid")
    suspend fun getUserData(
        @Path("uid") uid: String,
    ): Response<UserModel>

    @POST("/api/user/add")
    suspend fun addUser(
        user: UserModel,
    ): Response<UserResponseModel>

    @POST("/api/user/updatetype/{uid}")
    suspend fun updateUserType(
        @Path("uid") uid: String,
    ): Response<UserResponseModel>
    @Multipart
    @POST("/api/user/uploadimage")
    suspend fun UploadUserImage(
        @Part("uid") uid: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<UserUploadImageRepsonse>
}