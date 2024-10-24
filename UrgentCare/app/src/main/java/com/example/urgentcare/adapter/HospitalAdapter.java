package com.example.urgentcare.adapter;

import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.urgentcare.R;
import com.example.urgentcare.helper.Hospital;

import java.util.List;

public class HospitalAdapter extends RecyclerView.Adapter<HospitalAdapter.HospitalViewHolder> {

    private final List<Hospital> hospitals;
    private final double userLatitude;
    private final double userLongitude;
    private OnHospitalClickListener onHospitalClickListener;

    public interface OnHospitalClickListener {
        void onHospitalClick(Hospital hospital);
    }

    public HospitalAdapter(List<Hospital> hospitals, double userLatitude, double userLongitude, OnHospitalClickListener listener) {
        this.hospitals = hospitals;
        this.userLatitude = userLatitude;
        this.userLongitude = userLongitude;
        this.onHospitalClickListener = listener;
    }

    @Override
    public HospitalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hospital_rows, parent, false);
        return new HospitalViewHolder(view, onHospitalClickListener);
    }

    @Override
    public void onBindViewHolder(HospitalViewHolder holder, int position) {
        Hospital hospital = hospitals.get(position);
        holder.bind(hospital);
//        holder.tvHospitalName.setText(hospital.getName());
//        holder.tvHospitalType.setText(hospital.getType());

        float[] results = new float[1];
        Location.distanceBetween(userLatitude, userLongitude, hospital.getLatitude(), hospital.getLongitude(), results);
        float distanceInKm = results[0] / 1000; // Convert meters to kilometers
        holder.tvHospitalDistance.setText(String.format("Distance: %.2f km", distanceInKm));
    }

    @Override
    public int getItemCount() {
        return hospitals.size();
    }

    public static class HospitalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvHospitalName, tvHospitalType, tvHospitalDistance;
        OnHospitalClickListener onHospitalClickListener;
        Hospital hospital; // Add this field

        public HospitalViewHolder(View itemView, OnHospitalClickListener onHospitalClickListener) {
            super(itemView);
            tvHospitalName = itemView.findViewById(R.id.tvHospitalName);
            tvHospitalType = itemView.findViewById(R.id.tvHospitalType);
            tvHospitalDistance = itemView.findViewById(R.id.tvHospitalDistance);
            this.onHospitalClickListener = onHospitalClickListener;
            itemView.setOnClickListener(this);
        }

        public void bind(Hospital hospital) {
            this.hospital = hospital; // Bind the hospital object here
            tvHospitalName.setText(hospital.getName());
            tvHospitalType.setText(hospital.getType());
            // Rest of the binding code...
        }

        @Override
        public void onClick(View view) {
            if (onHospitalClickListener != null && hospital != null) {
                onHospitalClickListener.onHospitalClick(hospital);
            }
        }
    }
}
