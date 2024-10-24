package com.example.urgentcare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    private EditText editTextUsername, editTextEmail, editTextPhone, editTextCurrentPassword, editTextNewPassword, editTextConfirmPassword;
    private Button buttonUpdate;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private boolean passwordVisible_1 = false;
    private boolean passwordVisible_2 = false;
    private boolean passwordVisible_3 = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        editTextUsername = findViewById(R.id.profileUsername);
        editTextEmail = findViewById(R.id.profileEmail);
        editTextPhone = findViewById(R.id.profilePhoneNumber);
        editTextCurrentPassword = findViewById(R.id.profilePassword);
        editTextNewPassword = findViewById(R.id.profileNewPassword);
        editTextConfirmPassword = findViewById(R.id.profileConfirmPassword);
        buttonUpdate = findViewById(R.id.buttonUpdate);

        // Toggle password visibility when the user clicks the visibility icon
        editTextCurrentPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0); // Set initial icon
        editTextCurrentPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cursorPosition = editTextCurrentPassword.getSelectionStart();
                if (passwordVisible_1) {
                    passwordVisible_1 = false;
                    editTextCurrentPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD); // Hide password
                    editTextCurrentPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0);
                } else {
                    passwordVisible_1 = true;
                    editTextCurrentPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD); // Show password
                    editTextCurrentPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_24, 0);
                }
                editTextCurrentPassword.setSelection(cursorPosition); // Restore cursor position
            }
        });

        // Toggle password visibility when the user clicks the visibility icon
        editTextNewPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0); // Set initial icon
        editTextNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cursorPosition = editTextNewPassword.getSelectionStart();
                if (passwordVisible_2) {
                    passwordVisible_2 = false;
                    editTextNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD); // Hide password
                    editTextNewPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0);
                } else {
                    passwordVisible_2 = true;
                    editTextNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD); // Show password
                    editTextNewPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_24, 0);
                }
                editTextNewPassword.setSelection(cursorPosition); // Restore cursor position
            }
        });

        // Toggle password visibility when the user clicks the visibility icon
        editTextConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0); // Set initial icon
        editTextConfirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cursorPosition = editTextConfirmPassword.getSelectionStart();
                if (passwordVisible_3) {
                    passwordVisible_3 = false;
                    editTextConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD); // Hide password
                    editTextConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0);
                } else {
                    passwordVisible_3 = true;
                    editTextConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD); // Show password
                    editTextConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_24, 0);
                }
                editTextConfirmPassword.setSelection(cursorPosition); // Restore cursor position
            }
        });

        ImageView backButton = findViewById(R.id.toolbar_profile_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, SetingsActivity.class));
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        if (currentUser != null) {
            loadUserProfile(currentUser);
        }

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePassword(currentUser);
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    private void loadUserProfile(FirebaseUser user) {
        String userId = user.getUid();

        mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String phone = dataSnapshot.child("phoneNumber").getValue(String.class);
                    String email = user.getEmail(); // Get email from FirebaseUser object

                    editTextUsername.setText(username != null ? username : "N/A");
                    editTextPhone.setText(phone != null ? phone : "N/A");
                    editTextEmail.setText(email != null ? email : "N/A");

                    editTextUsername.setEnabled(false);
                    editTextEmail.setEnabled(false);
                    editTextPhone.setEnabled(false);
                } else {
                    Toast.makeText(ProfileActivity.this, "User data not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updatePassword(FirebaseUser user) {
        String currentPassword = editTextCurrentPassword.getText().toString();
        String newPassword = editTextNewPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();

        if (newPassword.equals(confirmPassword) && newPassword.length() >= 6) {
            reauthenticateUser(user, user.getEmail(), currentPassword, newPassword);
        } else {
            Toast.makeText(ProfileActivity.this, "New passwords do not match or are too short.", Toast.LENGTH_SHORT).show();
        }
    }

    private void reauthenticateUser(FirebaseUser user, String email, String currentPassword, String newPassword) {
        AuthCredential credential = EmailAuthProvider.getCredential(email, currentPassword);
        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user.updatePassword(newPassword).addOnCompleteListener(updateTask -> {
                    if (updateTask.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "Password updated successfully.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ProfileActivity.this, SetingsActivity.class));
                    } else {
                        Toast.makeText(ProfileActivity.this, "Failed to update password.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(ProfileActivity.this, "Re-authentication failed. Incorrect current password.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}