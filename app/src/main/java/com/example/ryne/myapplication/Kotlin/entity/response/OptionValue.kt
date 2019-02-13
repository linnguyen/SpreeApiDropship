package com.example.ryne.myapplication.Kotlin.entity.response

import com.google.gson.annotations.SerializedName

class OptionValue(
        @SerializedName("id") val id: String,
        @SerializedName("name") val name: String,
        @SerializedName("presentation") val presentation: String,
        @SerializedName("option_type_name") val optionTypeName: String,
        @SerializedName("option_type_id") val optionTypeId: String,
        @SerializedName("option_type_presentation") val optionTypePresentation: String
)