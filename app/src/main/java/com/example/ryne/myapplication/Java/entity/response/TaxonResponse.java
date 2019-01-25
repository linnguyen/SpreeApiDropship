package com.example.ryne.myapplication.Java.entity.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TaxonResponse {
    @SerializedName("taxons")
    @Expose
    private List<Taxon> taxons;

    public List<Taxon> getTaxons() {
        return taxons;
    }

    public void setTaxons(List<Taxon> taxons) {
        this.taxons = taxons;
    }
}
