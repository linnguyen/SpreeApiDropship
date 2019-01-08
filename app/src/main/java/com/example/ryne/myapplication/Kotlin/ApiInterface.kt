package com.example.ryne.myapplication.Kotlin

import com.example.ryne.myapplication.Kotlin.entity.request.response.ListProductResponse
import com.example.ryne.myapplication.Kotlin.entity.request.response.ProductResponse
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiInterface {
    @GET("v1/products")
    fun getProducts(@Query("token") token: String): Call<ListProductResponse>

    @POST("v1/products")
    fun createProduct(@Query("token") token: String, @Body jsonObject: JsonObject): Call<ProductResponse>
}