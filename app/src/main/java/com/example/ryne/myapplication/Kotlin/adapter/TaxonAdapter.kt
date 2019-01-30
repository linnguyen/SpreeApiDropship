package com.example.ryne.myapplication.Kotlin.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.ryne.myapplication.Kotlin.entity.response.Taxon

class TaxonAdapter(context: Context?, resource: Int, textViewResourceId: Int) : ArrayAdapter<Taxon>(context, resource, textViewResourceId) {

    private var applicationContext: Context
    private var lstTaxon: List<Taxon> = mutableListOf()

    init {
        this.applicationContext = context!!
    }

    fun setTaxons(lstTaxon: List<Taxon>){
        this.lstTaxon = lstTaxon;
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val label = TextView(context)
        label.setTextColor(Color.BLACK)
        label.text = getItem(position)!!.prettyName
        return label
    }

    override fun getCount(): Int {
        if (lstTaxon == null) return 0
        return lstTaxon.size
    }

    override fun getItem(position: Int): Taxon {
        return lstTaxon.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}