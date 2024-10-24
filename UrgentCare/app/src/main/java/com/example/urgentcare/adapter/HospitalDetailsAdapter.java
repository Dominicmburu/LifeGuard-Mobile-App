package com.example.urgentcare.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.urgentcare.R;
import com.example.urgentcare.helper.HospitalDetail;

import java.util.List;

public class HospitalDetailsAdapter extends RecyclerView.Adapter<HospitalDetailsAdapter.ViewHolder> {

    private final List<HospitalDetail> details;

    public HospitalDetailsAdapter(List<HospitalDetail> details) {
        this.details = details;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hospital_details_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HospitalDetail detail = details.get(position);
        holder.detailTemplate.setText(detail.getKey() + ": ");
        holder.detailActual.setText(detail.getValue());
    }

    @Override
    public int getItemCount() {
        return details != null ? details.size() : 0;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView detailTemplate, detailActual;

        ViewHolder(View itemView) {
            super(itemView);
            detailTemplate = itemView.findViewById(R.id.tvHospitalDetailTemplate);
            detailActual = itemView.findViewById(R.id.tvHospitalDetailsActual);
        }
    }
}

