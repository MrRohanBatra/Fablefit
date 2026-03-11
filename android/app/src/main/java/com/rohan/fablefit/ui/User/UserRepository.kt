package com.rohan.fablefit.ui.User

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UserRepository {
    suspend fun getUserData(uid: String){

    }
    fun createUidPart(uid: String): RequestBody {
        return uid.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    fun createImagePart(file: File): MultipartBody.Part {

        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())

        return MultipartBody.Part.createFormData(
            "image",
            file.name,
            requestFile
        )
    }
}