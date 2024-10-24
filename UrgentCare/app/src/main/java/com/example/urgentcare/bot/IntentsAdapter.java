package com.example.urgentcare.bot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.urgentcare.R;

import java.util.List;

public class IntentsAdapter extends RecyclerView.Adapter<IntentsAdapter.ViewHolder> {
    private List<String> questions;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String question);
    }

    public IntentsAdapter(List<String> questions, OnItemClickListener listener) {
        this.questions = questions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String question = questions.get(position);
        holder.questionTextView.setText(question);
        holder.itemView.setOnClickListener(v -> listener.onItemClick(question));
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView questionTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.questionTextView);
        }
    }
}
