package com.example.ryne.myapplication.Kotlin.entity.response

import com.google.gson.annotations.SerializedName

class OptionValue(
        @SerializedName("id") val id: String,
        @SerializedName("name") val name: String,
        @SerializedName("presentation") val presentation: String
)