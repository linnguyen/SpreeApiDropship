package com.example.ryne.myapplication.Kotlin

import com.example.ryne.myapplication.Java.Constant
import com.example.ryne.myapplication.Kotlin.entity.request.response.ListProductResponse
import com.example.ryne.myapplication.Kotlin.entity.request.response.ProductResponse
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {
//    val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
//    val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

    private val apiInterface: ApiInterface

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        apiInterface = retrofit.create(ApiInterface::class.java)
    }

    fun getProducts(token: String): Call<ListProductResponse> {
        return apiInterface.getProducts(token)
    }

    fun createProduct(token: String, jsonObject: JsonObject): Call<ProductResponse> {
        return apiInterface.createProduct(token, jsonObject)
    }
}