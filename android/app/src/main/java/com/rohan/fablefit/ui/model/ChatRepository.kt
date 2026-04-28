package com.rohan.fablefit.ui.model

import com.rohan.fablefit.network.RetrofitInstance
import com.rohan.fablefit.ui.model.ChatResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ChatRepository {
    suspend fun sendMessage(userId: String, message: String, imagePart: MultipartBody.Part? = null): Result<ChatResponse> {
        return runCatching {
            val userIdPart = userId.toRequestBody("text/plain".toMediaTypeOrNull())
            val messagePart = message.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = RetrofitInstance.api.chatWithAgent(userIdPart, messagePart, imagePart)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty AI response")
            } else {
                throw Exception("Stylist Error: ${response.code()}")
            }
        }
    }
}