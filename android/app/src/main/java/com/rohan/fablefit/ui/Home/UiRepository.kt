package com.rohan.fablefit.ui.Home

import android.content.Context
import com.rohan.fablefit.Cache.CacheRepository
import com.rohan.fablefit.network.RetrofitInstance
import com.rohan.fablefit.ui.model.HomeSection
class UiRepository(private val context: Context) {
    private val cacheRepo = CacheRepository()
    private val CACHE_KEY = "homeDynamicUi" // Use a constant to avoid typos

    suspend fun getHomeSections(): Result<List<HomeSection>> {
        return runCatching {
            try {
                val response = RetrofitInstance.api.getHomeSections()

                if (response.isSuccessful) {
                    val data = response.body() ?: throw Exception("Body is null")

                    val jsonString = cacheRepo.gson.toJson(data)
                    cacheRepo.writeJson(context, CACHE_KEY, jsonString)

                    data
                } else {
                    // Server returned an error (e.g., 500), try cache
                    fetchFromCacheOrThrow(Exception("Server Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                // Network failure (e.g., No Internet), try cache
                fetchFromCacheOrThrow(e)
            }
        }
    }

    private fun fetchFromCacheOrThrow(originalException: Exception): List<HomeSection> {
        val cachedData = cacheRepo.readJson<List<HomeSection>>(context, CACHE_KEY)
        return cachedData ?: throw originalException
    }
}