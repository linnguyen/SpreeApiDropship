package com.example.ryne.myapplication.Kotlin

import com.example.ryne.myapplication.Kotlin.entity.request.response.ListProductResponse
import com.example.ryne.myapplication.Kotlin.entity.request.response.ProductResponse
import com.example.ryne.myapplication.Kotlin.entity.response.ImageResponse
import com.example.ryne.myapplication.Kotlin.entity.response.TaxonResponse
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {
    @GET("v1/products")
    fun getProducts(@Query("token") token: String): Call<ListProductResponse>

    @POST("v1/products")
    fun createProduct(@Query("token") token: String, @Body jsonObject: JsonObject): Call<ProductResponse>

    @POST("v1/products/{product_id}/image_upload")
    fun uploadImagev2(@Path(value = "product_id", encoded = true) productId: String, @Query("token") token: String, @Body jsonObject: JsonObject): Call<ImageResponse>

    @GET("v1/taxons")
    fun getTaxons(@Query("token") token: String, @Query("without_chidren") value: Boolean): Call<TaxonResponse>
}