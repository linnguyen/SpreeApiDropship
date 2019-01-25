package com.example.ryne.myapplication.Java.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.ryne.myapplication.Java.entity.response.Taxon;
import com.example.ryne.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class TaxonAdpater extends ArrayAdapter<Taxon> {
    private Context context;
    private List<Taxon> lstTaxon;

    public TaxonAdpater(Context context) {
        this(context, R.layout.item_taxon, R.id.tv_taxon);
    }

    public TaxonAdpater(@NonNull Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
        this.context = context;
        this.lstTaxon = new ArrayList<>();
    }


    public void setTaxons(List<Taxon> lstTaxon) {
        this.lstTaxon.clear();
        this.lstTaxon.addAll(lstTaxon);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        if (lstTaxon == null) return 0;
        return lstTaxon.size();
    }

    @Nullable
    @Override
    public Taxon getItem(int position) {
        return lstTaxon.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        label.setText(getItem(position).getPrettyName());
        return label;
    }
}
