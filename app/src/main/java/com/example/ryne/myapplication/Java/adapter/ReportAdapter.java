package com.example.ryne.myapplication.Java.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ryne.myapplication.Java.entity.response.ProductResponse;
import com.example.ryne.myapplication.R;

import java.util.List;

/**
 * Created by ryne on 21/12/2018.
 */

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {
    List<ProductResponse> lstProduct;
    Context context;

    public ReportAdapter(List<ProductResponse> lstProduct, Context context) {
        this.lstProduct = lstProduct;
        this.context = context;
    }

    public void setData(List<ProductResponse> lstProduct) {
        this.lstProduct = lstProduct;
        notifyDataSetChanged();
    }

    @Override
    public ReportAdapter.ReportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        ReportViewHolder viewHolder = new ReportViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ReportAdapter.ReportViewHolder holder, int position) {
        ProductResponse productResponse = lstProduct.get(position);
        holder.tvId.setText(productResponse.getId());
        holder.tvStatus.setText(productResponse.getStatus());
        holder.tvProductName.setText(productResponse.getName());
        if (productResponse.getStatus() != null) {
            if (productResponse.getStatus().startsWith("s")) {
                holder.tvStatus.setTextColor(Color.GREEN);
            } else {
                holder.tvStatus.setTextColor(Color.RED);
            }
        }
    }

    @Override
    public int getItemCount() {
        return lstProduct.size();
    }

    public class ReportViewHolder extends RecyclerView.ViewHolder {
        private TextView tvId;
        private TextView tvStatus;
        private TextView tvProductName;

        public ReportViewHolder(View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tv_id);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvProductName = itemView.findViewById(R.id.tv_name);
        }
    }
}
