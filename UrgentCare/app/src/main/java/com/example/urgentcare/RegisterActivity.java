package com.example.urgentcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.urgentcare.Utils.NetworkUtil;
import com.example.urgentcare.helper.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {
    private EditText phonenumber, email, username, password;
    private TextView gotoLogin;
    private Button register;
    private FirebaseAuth mAuth;
    private boolean passwordVisible = false;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        phonenumber = findViewById(R.id.editTextRegPhoneNumber);
        email = findViewById(R.id.editTextRegEmail);
        username = findViewById(R.id.editTextRegUsername);
        password = findViewById(R.id.editTextRegPassword);
        gotoLogin = findViewById(R.id.textViewGoToLogin);
        register = findViewById(R.id.buttonRegister);
        progressBar = findViewById(R.id.progressBarRegister);

        gotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        // Toggle password visibility
        password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0);
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cursorPosition = password.getSelectionStart();
                if (passwordVisible) {
                    passwordVisible = false;
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0);
                } else {
                    passwordVisible = true;
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_24, 0);
                }
                password.setSelection(cursorPosition);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performRegistration();
            }
        });
    }

    private void performRegistration() {
        if (!NetworkUtil.isNetworkConnected(this)) {
            Toast.makeText(this, "No internet connection. Please check your network settings.", Toast.LENGTH_LONG).show();
            return;
        }

        String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString().trim();
        String usernameText = username.getText().toString().trim();
        String phoneNumberText = phonenumber.getText().toString().trim();

        if (TextUtils.isEmpty(emailText) || TextUtils.isEmpty(passwordText) || TextUtils.isEmpty(usernameText) || TextUtils.isEmpty(phoneNumberText)) {
            Toast.makeText(RegisterActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidPhoneNumber(phoneNumberText)) {
            phonenumber.setError("Invalid phone number format (e.g., 0700000000)");
            return;
        }

        if (passwordText.length() < 6) {
            password.setError("Password should be at least 6 characters long");
            return;
        }

        if (usernameText.length() < 3) {
            username.setError("Username should be at least 3 characters long");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean emailExists = false, usernameExists = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        if (user.getEmail().equalsIgnoreCase(emailText)) emailExists = true;
                        if (user.getUsername().equalsIgnoreCase(usernameText)) usernameExists = true;
                    }
                }

                if (emailExists) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "Email already exists. Please choose a different email.", Toast.LENGTH_SHORT).show();
                } else if (usernameExists) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "Username already exists. Please choose a different username.", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(emailText, passwordText, usernameText, phoneNumberText);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void registerUser(String email, String password, String username, String phoneNumber) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    User newUser = new User(username, email, phoneNumber);

                    FirebaseDatabase.getInstance().getReference("users")
                            .child(firebaseUser.getUid())
                            .setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegisterActivity.this, FirstUserGuideActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Failed to register user details.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^\\d{10}$");
    }
}
