package com.example.urgentcare.adapter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.urgentcare.R;
import com.example.urgentcare.helper.Contact;

public class ContactsViewHolder extends RecyclerView.ViewHolder {
    TextView tvContactName;
    TextView tvContactNumber;
    CheckBox chkSelected;

    public ContactsViewHolder(View itemView) {
        super(itemView);
        tvContactName = itemView.findViewById(R.id.tvContactName);
        tvContactNumber = itemView.findViewById(R.id.tvContactNumber);
        chkSelected = itemView.findViewById(R.id.chkSelected);
    }

    void bind(Contact contact, boolean isSelected, View.OnClickListener onClickListener) {
        tvContactName.setText(contact.getName());
        tvContactNumber.setText(contact.getNumber());
        chkSelected.setChecked(isSelected);
        itemView.setOnClickListener(onClickListener);
    }
}
