package com.example.urgentcare.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.urgentcare.R;

public class GuideAdapter extends RecyclerView.Adapter<GuideAdapter.ViewHolder> {

    private final String[] userGuidelines;

    public GuideAdapter(String[] userGuidelines) {
        this.userGuidelines = userGuidelines;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.guide_row, parent, false);
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
            guideNumber = itemView.findViewById(R.id.guide_number);
            guideText = itemView.findViewById(R.id.guide_rowText);
        }
    }
}
