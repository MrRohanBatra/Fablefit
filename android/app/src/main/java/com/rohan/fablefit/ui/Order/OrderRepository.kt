package com.rohan.fablefit.ui.Order

import com.rohan.fablefit.network.ApiService
import com.rohan.fablefit.network.RetrofitInstance.api
import com.rohan.fablefit.ui.model.OrderCancelResponse
import com.rohan.fablefit.ui.model.OrderListResponse
import com.rohan.fablefit.ui.model.OrderPlaceResponse
import com.rohan.fablefit.ui.model.OrderTrackResponse

class OrderRepository(
) {

    // 🧾 Place Order
    suspend fun placeOrder(
        userId: String,
        address: String
    ): Result<OrderPlaceResponse> {
        return try {
            val response = api.placeOrder(userId, address)

            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to place order"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // 📦 Get all orders
    suspend fun getUserOrders(
        userId: String
    ): Result<OrderListResponse> {
        return try {
            val response = api.getUserOrders(userId)

            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch orders"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // 🔍 Track order
    suspend fun trackOrder(
        orderId: String
    ): Result<OrderTrackResponse> {
        return try {
            val response = api.trackOrder(orderId)

            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to track order"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // ❌ Cancel order
    suspend fun cancelOrder(
        orderId: String
    ): Result<OrderCancelResponse> {
        return try {
            val response = api.cancelOrder(orderId)

            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to cancel order"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}