package com.example.ryne.myapplication.Kotlin.adapter

import android.content.Context
import android.widget.ArrayAdapter
import com.example.ryne.myapplication.Java.entity.response.Taxon

class TaxonAdapter(context: Context?, resource: Int, objects: MutableList<Taxon>?) : ArrayAdapter<Taxon>(context, resource, objects) {
}