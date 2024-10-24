package com.example.urgentcare;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.urgentcare.adapter.ContactsAdapter;
import com.example.urgentcare.helper.Contact;
import com.example.urgentcare.helper.EmergencyContact;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContactsActivity extends AppCompatActivity implements ContactsAdapter.OnContactSelectedListener {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private RecyclerView rvContactsList;
    private TextView tvSelectedContacts;
    private Button btnAddContacts;
    private ContactsAdapter contactsAdapter;
    private List<Contact> contactsList = new ArrayList<>();
    private DatabaseReference usersReference;
    private Set<String> selectedContactsNumbers = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        rvContactsList = findViewById(R.id.rvContactsList);
        tvSelectedContacts = findViewById(R.id.tvSelectedContacts);
        btnAddContacts = findViewById(R.id.btnAddContacts);

        rvContactsList.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firebase Database Reference
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            usersReference = FirebaseDatabase.getInstance().getReference("users").child(currentUserId).child("emergencyContact");
        }

        checkPermissionsAndLoadContacts();

        btnAddContacts.setOnClickListener(v -> addSelectedContactsToDatabase());
    }

    private void checkPermissionsAndLoadContacts() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            loadContacts();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadContacts();
        } else {
            tvSelectedContacts.setText("Permission denied to read contacts");
        }
    }

    private void loadContacts() {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            // Ensure both indices are valid
            if (nameIndex != -1 && numberIndex != -1) {
                while (cursor.moveToNext()) {
                    String name = cursor.getString(nameIndex);
                    String number = cursor.getString(numberIndex);
                    contactsList.add(new Contact(name, number));
                }
            } else {
                // Handle the case where indices are invalid
                Toast.makeText(this, "Failed to load contacts: Invalid column index.", Toast.LENGTH_LONG).show();
            }
            cursor.close();
        }

        contactsAdapter = new ContactsAdapter(contactsList, this);
        rvContactsList.setAdapter(contactsAdapter);
    }

    @Override
    public void onSelectedContactsChanged(Set<String> selectedContacts) {
        tvSelectedContacts.setText("Selected Contacts: " + selectedContacts.size());
    }
    private void addSelectedContactsToDatabase() {
        for (String contactNumber : selectedContactsNumbers) {
            // Assuming you have a way to get the contact's name from the contact number
            String contactName = findContactNameByNumber(contactNumber); // Placeholder for the contact's name

            for (Contact contact : contactsList) {
                if (contact.getNumber().equals(contactNumber)) {
                    contactName = contact.getName();
                    break;
                }
            }

            EmergencyContact emergencyContact = new EmergencyContact(contactName, contactNumber);
            usersReference.push().setValue(emergencyContact)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(ContactsActivity.this, "Contact added successfully", Toast.LENGTH_SHORT).show();
                        // Clear the selection after adding contacts
                        selectedContactsNumbers.clear();
                        contactsAdapter.notifyDataSetChanged(); // Refresh the adapter view after clearing selections
                        tvSelectedContacts.setText("Selected Contacts: 0");

                        // Redirect to FriendsActivity
                        Intent intent = new Intent(ContactsActivity.this, FriendsActivity.class);
                        startActivity(intent);
                        finish(); // Optionally call finish() if you don't want users to return to this activity on back press from FriendsActivity
                    })
                    .addOnFailureListener(e -> Toast.makeText(ContactsActivity.this, "Failed to add contact: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }

        // Optionally clear the selection and update the UI accordingly
        selectedContactsNumbers.clear(); // Clear selected contact numbers
        contactsAdapter.notifyDataSetChanged(); // Notify adapter to refresh and reflect the cleared selection
        tvSelectedContacts.setText("Selected Contacts: 0");
    }



    private String findContactNameByNumber(String number) {
        for (Contact contact : contactsList) {
            if (contact.getNumber().equals(number)) {
                return contact.getName();
            }
        }
        return "Unknown"; // Default name if not found
    }

}
