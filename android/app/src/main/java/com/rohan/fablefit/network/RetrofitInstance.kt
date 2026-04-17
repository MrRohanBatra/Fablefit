package com.rohan.fablefit.network
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.rohan.fablefit.BuildConfig
import com.rohan.fablefit.R
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = BuildConfig.BASE_URL//"https://testserver.rohan.org.in/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}