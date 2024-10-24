package com.example.lifeguard.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lifeguard.R;
import com.example.lifeguard.helper.EmergencyContact;

import java.util.List;

public class EmergencyContactAdapter extends RecyclerView.Adapter<EmergencyContactAdapter.ViewHolder> {
    private List<EmergencyContact> contactList;
    private OnContactDeleteListener deleteListener;

    public interface OnContactDeleteListener {
        void onDeleteContact(EmergencyContact contact, int position);
    }

    public EmergencyContactAdapter(List<EmergencyContact> contactList, OnContactDeleteListener deleteListener) {
        this.contactList = contactList;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.numbers_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EmergencyContact contact = contactList.get(position);
        holder.userName.setText(contact.getName());
        holder.userNumber.setText(contact.getPhone());

        holder.deleteContact.setOnClickListener(view -> {
            if (deleteListener != null) {
                deleteListener.onDeleteContact(contact, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userNumber;
        Button deleteContact;

        ViewHolder(View view) {
            super(view);
            userName = view.findViewById(R.id.userName);
            userNumber = view.findViewById(R.id.userNumber);
            deleteContact = view.findViewById(R.id.deleteContact);
        }
    }
}
