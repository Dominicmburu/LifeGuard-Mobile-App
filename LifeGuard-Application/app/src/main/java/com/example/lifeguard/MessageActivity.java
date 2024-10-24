package com.example.lifeguard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MessageActivity extends AppCompatActivity {
    private EditText editTextMessage;
    private Button buttonSaveMessage;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private TextView characterCountText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSaveMessage = findViewById(R.id.buttonSaveMessage);
        characterCountText = findViewById(R.id.characterCountText);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Update the reference to point to the user-specific path
            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid()).child("emergencyMessage");
        }

        // Load existing or default message
        loadMessage();

        // Set character count when activity is created
        updateCharacterCount();

        // Add a TextWatcher to monitor changes in the EditText
        editTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                // Update character count as the user types
                updateCharacterCount();
            }
        });

        buttonSaveMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editTextMessage.getText().toString();
                if (!message.isEmpty()) {
                    saveMessage(message);
                }
            }
        });

        ImageView backButton = findViewById(R.id.toolbar_message_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessageActivity.this, FriendsActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void loadMessage() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String message = dataSnapshot.getValue(String.class);
                    editTextMessage.setText(message);
                } else {
                    // Set default message if not present
                    editTextMessage.setText("Hello, I am in danger. I need your help");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible database errors
                Toast.makeText(MessageActivity.this, "Error loading message", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void saveMessage(String message) {
        databaseReference.setValue(message)
                .addOnSuccessListener(aVoid -> {
                    // Handle success
                    Toast.makeText(MessageActivity.this, "Message saved successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MessageActivity.this, FriendsActivity.class));
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(MessageActivity.this, "Failed to save message", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateCharacterCount() {
        int characterCount = editTextMessage.getText().length();
        characterCountText.setText(characterCount + "/45");
        if (characterCount > 50) {
            // Disable the button if the character limit is exceeded
            buttonSaveMessage.setEnabled(false);
            Toast.makeText(this, "Do not exceed the limit", Toast.LENGTH_SHORT).show();
        } else {
            buttonSaveMessage.setEnabled(true);
        }
    }
}