package com.example.ryne.myapplication.Kotlin.entity.response

import com.google.gson.annotations.SerializedName

class TaxonResponse(@SerializedName("taxons") var taxons: List<Taxon>)
