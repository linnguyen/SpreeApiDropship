package com.example.ryne.myapplication.Kotlin

import com.example.ryne.myapplication.Kotlin.entity.request.response.ListProductResponse
import com.example.ryne.myapplication.Kotlin.entity.request.response.ProductResponse
import com.example.ryne.myapplication.Kotlin.entity.response.ImageResponse
import com.example.ryne.myapplication.Kotlin.entity.response.OptionValue
import com.example.ryne.myapplication.Kotlin.entity.response.TaxonResponse
import com.example.ryne.myapplication.Kotlin.entity.response.Variant
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

    @POST("v1/option_types/{option_type_id}/find_or_create_option_value")
    fun findOrCreateOptionValue(@Path(value = "option_type_id", encoded = true) optionTypeId: String, @Query("token") token: String, @Body jsonObject: JsonObject): Call<OptionValue>

    @POST("v1/products/{product_id}/variants")
    fun createVariant(@Path(value = "product_id", encoded = true) productId: String, @Query("token") token: String, @Body jsonObject: JsonObject): Call<Variant>
}