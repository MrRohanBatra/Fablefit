package com.rohan.fablefit.network
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.rohan.fablefit.BuildConfig
import com.rohan.fablefit.R
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    private const val BASE_URL = BuildConfig.BASE_URL//"https://testserver.rohan.org.in/"
    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(100, TimeUnit.SECONDS)   // time to establish connection
        .readTimeout(100, TimeUnit.SECONDS)      // time to read data
        .writeTimeout(100, TimeUnit.SECONDS)     // time to send data
        .build()
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}