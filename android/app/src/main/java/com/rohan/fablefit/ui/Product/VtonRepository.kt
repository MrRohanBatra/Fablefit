package com.rohan.fablefit.ui.Product

import com.rohan.fablefit.network.RetrofitInstance
import com.rohan.fablefit.ui.model.TryOnResponseModel
import com.rohan.fablefit.ui.model.VtonReponseModel
import com.rohan.fablefit.ui.model.VtonRequestModel

class VtonRepository {
    suspend fun tryon(item: VtonRequestModel): Result<VtonReponseModel> {
        return runCatching {
            val response = RetrofitInstance.api.tryon(item)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Vton response is null")
            } else {
                throw Exception("HTTP ${response.code()}: ${response.errorBody()?.string()}")
            }
        }
    }

    suspend fun getStatus(taskId: String): Result<TryOnResponseModel> {
        return runCatching {
            val response = RetrofitInstance.api.getStatus(taskId)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Response is null")
            } else {
                throw Exception("HTTP ${response.code()}: ${response.errorBody()?.string()}")
            }
        }
    }

}