package com.example.ryne.myapplication.Kotlin

import com.example.ryne.myapplication.Kotlin.response.ProductResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("v1/products")
    fun getProducts(@Query("token") token: String): Call<List<ProductResponse>>
}