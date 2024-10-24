package com.example.lifeguard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lifeguard.helper.EmergencyContact;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterNumberActivity extends AppCompatActivity {
    private EditText nameEditText, phoneEditText;
    private Button registerButton;
    private DatabaseReference usersReference;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_number);

        // Initialize Firebase Auth and Database Reference
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            usersReference = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);
        }

        // Get references to the views
        nameEditText = findViewById(R.id.editTextEmegName);
        phoneEditText = findViewById(R.id.editTextEmegPhoneNumber);
        registerButton = findViewById(R.id.RegisterNewNumber);

        ImageView backButton = findViewById(R.id.toolbar_reg_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterNumberActivity.this, FriendsActivity.class));
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countContactsAndRegister();
            }
        });
    }

    private void countContactsAndRegister() {
        usersReference.child("emergencyContact").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() < 10) {
                    registerNumber();
                } else {
                    Toast.makeText(RegisterNumberActivity.this, "Contact limit reached", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RegisterNumberActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void registerNumber() {
        String name = nameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

        if (name.isEmpty()) {
            nameEditText.setError("Name is required");
            nameEditText.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            phoneEditText.setError("Phone number is required");
            phoneEditText.requestFocus();
            return;
        }

        if (phone.length() != 10) {
            phoneEditText.setError("Enter a valid 10-digit phone number");
            phoneEditText.requestFocus();
            return;
        }

//        EmergencyContact contact = new EmergencyContact(name, phone);
//        usersReference.child("emergencyContact").push().setValue(contact)
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(RegisterNumberActivity.this, "Contact saved successfully", Toast.LENGTH_LONG).show();
//                    clearInputs();
//                })
//                .addOnFailureListener(e ->
//                        Toast.makeText(RegisterNumberActivity.this, "Failed to save contact", Toast.LENGTH_LONG).show());


        // Retrieve the current user's phone number
        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String currentUserPhone = dataSnapshot.child("phoneNumber").getValue(String.class);
                if (phone.equals(currentUserPhone)) {
                    phoneEditText.setError("Cannot register your own phone number");
                    phoneEditText.requestFocus();
                } else {
                    // Proceed to save the contact if the numbers are not the same
                    EmergencyContact contact = new EmergencyContact(name, phone);
                    usersReference.child("emergencyContact").push().setValue(contact)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(RegisterNumberActivity.this, "Contact saved successfully", Toast.LENGTH_LONG).show();
                                clearInputs();
                                startActivity(new Intent(RegisterNumberActivity.this, FriendsActivity.class));
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(RegisterNumberActivity.this, "Failed to save contact", Toast.LENGTH_LONG).show());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RegisterNumberActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void clearInputs() {
        nameEditText.setText("");
        phoneEditText.setText("");
    }
}