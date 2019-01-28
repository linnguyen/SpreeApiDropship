package com.example.ryne.myapplication.Kotlin.entity.response

import com.google.gson.annotations.SerializedName

class ImageResponse(
        @SerializedName("id") val id: String,
        @SerializedName("position") val position: String,
        @SerializedName("mini_url") val miniUrl: String,
        @SerializedName("small_url") val smallUrl: String,
        @SerializedName("product_url") val productUrl: String,
        @SerializedName("large_url") val largeUrl: String)