package com.rohan.fablefit.ui.Profile

import com.rohan.fablefit.network.RetrofitInstance
import com.rohan.fablefit.ui.model.UserModel

class ProfileRepository {
    suspend fun getUserProfile(uid: String): Result<UserModel> {
        return runCatching {
            val response = RetrofitInstance.api.getUser(uid)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty user body")
            } else {
                throw Exception("HTTP ${response.code()}")
            }
        }
    }
}
