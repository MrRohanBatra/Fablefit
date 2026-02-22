package com.rohan.fablefit.ui.Home

import com.rohan.fablefit.network.RetrofitInstance
import com.rohan.fablefit.ui.model.HomeSection

class UiRepository {

    suspend fun getHomeSections(): Result<List<HomeSection>>{
        return runCatching {
            val response = RetrofitInstance.api.getHomeSections()

            if (response.isSuccessful) {
                response.body()
                    ?: throw Exception("Home sections body is null")
            } else {
                throw Exception("HTTP ${response.code()}")
            }
        }
    }
}