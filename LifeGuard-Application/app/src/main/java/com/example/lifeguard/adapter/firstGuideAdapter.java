package com.example.lifeguard.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lifeguard.R;

public class firstGuideAdapter extends RecyclerView.Adapter<firstGuideAdapter.ViewHolder> {

    private final String[] userGuidelines;

    public firstGuideAdapter(String[] userGuidelines) {
        this.userGuidelines = userGuidelines;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.first_guide_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String guideline = userGuidelines[position];
        holder.guideNumber.setText(String.valueOf(position + 1));
        holder.guideText.setText(guideline);
    }

    @Override
    public int getItemCount() {
        return userGuidelines.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView guideNumber, guideText;

        public ViewHolder(View itemView) {
            super(itemView);
            guideNumber = itemView.findViewById(R.id.first_guide_number);
            guideText = itemView.findViewById(R.id.first_guide_rowText);
        }
    }
}
