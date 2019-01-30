package com.example.ryne.myapplication.Kotlin.entity.response

import com.google.gson.annotations.SerializedName

class Taxon(
        @SerializedName("id")

        var id: String? = null,

        @SerializedName("name")

        var name: String? = null,

        @SerializedName("pretty_name")

        var prettyName: String? = null,

        @SerializedName("permalink")

        var permalink: String? = null,

        @SerializedName("parent_id")

        var parentId: String? = null,

        @SerializedName("taxonomy_id")

        var taxonomyId: String? = null,

        @SerializedName("meta_title")

        var metaTitle: String? = null,

        @SerializedName("meta_description")

        var metaDescription: String? = null
) {
    override fun toString(): String {
        return this.prettyName.toString()
    }
}

