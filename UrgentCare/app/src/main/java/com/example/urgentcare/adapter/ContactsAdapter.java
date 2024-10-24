package com.example.urgentcare.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.urgentcare.R;
import com.example.urgentcare.helper.Contact;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private List<Contact> contacts;
    private OnContactSelectedListener listener;
    private Set<String> selectedContacts = new HashSet<>();

    public ContactsAdapter(List<Contact> contacts, OnContactSelectedListener listener) {
        this.contacts = contacts;
        this.listener = listener;
    }

    public interface OnContactSelectedListener {
        void onSelectedContactsChanged(Set<String> selectedContacts);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.bind(contact, selectedContacts.contains(contact.getNumber()));

        // Toggle selection state when an item is clicked
        holder.itemView.setOnClickListener(v -> {
            if (selectedContacts.contains(contact.getNumber())) {
                selectedContacts.remove(contact.getNumber());
            } else {
                selectedContacts.add(contact.getNumber());
            }
            notifyItemChanged(position);
            listener.onSelectedContactsChanged(selectedContacts);
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void clearSelection() {
        selectedContacts.clear(); // Clear all selections
        notifyDataSetChanged(); // Notify the adapter to refresh the UI
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvContactName;
        private final TextView tvContactNumber;
        private final CheckBox chkSelected; // Assuming you're using a CheckBox for selection

        public ViewHolder(View itemView) {
            super(itemView);
            tvContactName = itemView.findViewById(R.id.tvContactName);
            tvContactNumber = itemView.findViewById(R.id.tvContactNumber);
            chkSelected = itemView.findViewById(R.id.chkSelected); // This assumes you have a CheckBox in your layout
        }

        public void bind(Contact contact, boolean isSelected) {
            tvContactName.setText(contact.getName());
            tvContactNumber.setText(contact.getNumber());
            chkSelected.setChecked(isSelected);

            // Optional: if you want to change the background or appearance of the item based on selection
            itemView.setBackgroundColor(isSelected ? Color.LTGRAY : Color.TRANSPARENT); // Example: change background color

            // Optional: Set a click listener for the CheckBox if you want changes to be reflected immediately upon clicking the CheckBox itself
            chkSelected.setOnClickListener(v -> {
                // This part is pseudo-code and requires implementing a callback mechanism to update the adapter's selected contacts set
                // Example: if you have a method in your adapter to handle selection toggling:
                // adapter.toggleSelection(contact.getNumber());
            });
        }
    }
}
