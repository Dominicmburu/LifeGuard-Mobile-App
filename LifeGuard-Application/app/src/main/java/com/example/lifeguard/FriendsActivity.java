package com.example.lifeguard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lifeguard.adapter.EmergencyContactAdapter;
import com.example.lifeguard.helper.EmergencyContact;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendsActivity extends AppCompatActivity implements EmergencyContactAdapter.OnContactDeleteListener {
    private ImageButton gotoregNumbers, gotoeditText;
    private EditText messageDisplayed;
    private RecyclerView registeredNumbersDisplay;
    private DatabaseReference usersReference;
    private List<EmergencyContact> contactList = new ArrayList<>();
    private EmergencyContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);


        gotoregNumbers = findViewById(R.id.gotoregisterNumbers);
        gotoeditText = findViewById(R.id.gotoEditText);
        messageDisplayed = findViewById(R.id.messageDisplayView);

        // Set EditText as non-editable
        messageDisplayed.setKeyListener(null);

        registeredNumbersDisplay = findViewById(R.id.registeredNumbersView);
        registeredNumbersDisplay.setLayoutManager(new LinearLayoutManager(this));

        // Get current user and database reference
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            usersReference = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);

            // Load contacts and emergency message
            loadEmergencyContacts();
            loadEmergencyMessage();
        }

        // Set up buttons and listeners
        setupButtonListeners();

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setupButtonListeners() {
        ImageView backButton = findViewById(R.id.toolbar_app_back);
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(FriendsActivity.this, MainActivity.class));
            finish();
        });

        gotoregNumbers.setOnClickListener(v ->
//                startActivity(new Intent(FriendsActivity.this, ContactsActivity.class)));
                startActivity(new Intent(FriendsActivity.this, RegisterNumberActivity.class)));


        gotoeditText.setOnClickListener(v ->
                startActivity(new Intent(FriendsActivity.this, MessageActivity.class)));

    }

    private void loadEmergencyContacts() {
        DatabaseReference contactRef = usersReference.child("emergencyContact");
        contactRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                EmergencyContact contact = dataSnapshot.getValue(EmergencyContact.class);
                if (contact != null) {
                    contact.setKey(dataSnapshot.getKey());
                    contactList.add(contact);
                    updateRecyclerView();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                // Handle updates to contacts
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // Find and remove the contact from the list, then update the RecyclerView
                String key = dataSnapshot.getKey();
                for (int i = 0; i < contactList.size(); i++) {
                    if (contactList.get(i).getKey().equals(key)) {
                        contactList.remove(i);
                        adapter.notifyItemRemoved(i);
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                // Handle if needed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FriendsActivity.this, "Failed to load contacts", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void loadEmergencyMessage() {
        DatabaseReference messageRef = usersReference.child("emergencyMessage");
        messageRef.addListenerForSingleValueEvent(new ValueEventListener
                () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String message = dataSnapshot.getValue(String.class);
                    messageDisplayed.setText(message);
                } else {
                    messageDisplayed.setText("Set your emergency message");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FriendsActivity.this, "Failed to load emergency message", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRecyclerView() {
        if (adapter == null) {
            adapter = new EmergencyContactAdapter(contactList, this);
            registeredNumbersDisplay.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onDeleteContact(EmergencyContact contact, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);

        // Create a custom TextView for the message
        TextView messageView = new TextView(this);
        messageView.setText("Are you sure you want to delete this contact?");
        messageView.setTextColor(getResources().getColor(R.color.white)); // Set text color to white
        // Optionally, set other text properties like padding, text size, etc.
        // messageView.setPadding(40, 40, 40, 40); // Example padding
        // messageView.setTextSize(16); // Example text size

        messageView.setPadding(40, 40, 40, 40); // Example padding
        messageView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        builder.setTitle("Confirm Delete")
                .setCustomTitle(messageView) // Use custom TextView as message
                .setPositiveButton("Yes", (dialog, which) -> deleteContact(contact, position))
                .setNegativeButton("No", null)
                .show();
    }



    private void deleteContact(EmergencyContact contact, int position) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && contact.getKey() != null) {
            DatabaseReference contactRef = usersReference.child("emergencyContact").child(contact.getKey());
            contactRef.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (position < contactList.size()) {
                        contactList.remove(position);
                        adapter.notifyItemRemoved(position);
                    }
                    Toast.makeText(FriendsActivity.this, "Contact deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FriendsActivity.this, "Failed to delete contact", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}