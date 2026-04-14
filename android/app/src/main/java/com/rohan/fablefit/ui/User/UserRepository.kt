package com.rohan.fablefit.ui.User

import android.content.Context
import android.net.Uri
import com.rohan.fablefit.network.RetrofitInstance
import com.rohan.fablefit.ui.model.Product
import com.rohan.fablefit.ui.model.UserModel
import com.rohan.fablefit.ui.model.UserResponseModel
import com.rohan.fablefit.ui.model.UserUploadImageRepsonse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UserRepository {
    suspend fun updateUserData(user: UserModel): Result<UserResponseModel>{
        return runCatching {
            val response= RetrofitInstance.api.addUser(user);
            if (response.isSuccessful){
                response.body()?:
                throw Exception("Empty Response Body")
            }
            else{
                throw Exception("HTTP ${response.code()} ${response.errorBody()}")
            }
        }
    }
    suspend fun getUserData(uid: String):Result<UserModel>{
        return runCatching {
            val response = RetrofitInstance.api.getUserData(uid);
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty User Data")
            } else {
                throw Exception("HTTP ${response.code()} ${response.errorBody()}")
            }
        }
    }
    suspend fun uploadImage(context: Context,uid: String,uri: Uri): Result<UserUploadImageRepsonse>{
        return runCatching {
            try {
                val file=uriToFile(context, uri);
                val uidPart = createUidPart(uid)
                val imagePart = createImagePart(file);
                val repsonse= RetrofitInstance.api.UploadUserImage(uidPart,imagePart)
                if(repsonse.isSuccessful){
                    repsonse.body()?:
                    throw Exception("Response Body not Found");
                }
                else{
                    throw Exception("HTTP ${repsonse.code()} ${repsonse.errorBody()}")
                }
            }
            catch (e: Exception){
                throw e
            }
        }
    }
    private fun createUidPart(uid: String): RequestBody {
        return uid.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    private fun createImagePart(file: File): MultipartBody.Part {

        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())

        return MultipartBody.Part.createFormData(
            "image",
            file.name,
            requestFile
        )
    }

    private fun uriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)!!
        val file = File.createTempFile("upload", ".jpg", context.cacheDir)

        file.outputStream().use { output ->
            inputStream.copyTo(output)
        }

        return file
    }
}