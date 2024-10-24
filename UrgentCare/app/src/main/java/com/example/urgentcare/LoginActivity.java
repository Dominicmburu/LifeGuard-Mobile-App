package com.example.urgentcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.urgentcare.Utils.NetworkUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private EditText username, password;
    private TextView gotoReg, forgotPassword;
    private Button btnLogin;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private boolean passwordVisible = false;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        username = findViewById(R.id.editTextLogUsername);
        password = findViewById(R.id.editTextLogPassword);
        gotoReg = findViewById(R.id.textViewGoToRegister);
        btnLogin = findViewById(R.id.buttonLogin);
        forgotPassword = findViewById(R.id.textViewForgetPassword);
        progressBar = findViewById(R.id.progressBarLogin);

        // Toggle password visibility when the user clicks the visibility icon
        password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0); // Set initial icon
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cursorPosition = password.getSelectionStart();
                if (passwordVisible) {
                    passwordVisible = false;
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD); // Hide password
                    password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0);
                } else {
                    passwordVisible = true;
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD); // Show password
                    password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_24, 0);
                }
                password.setSelection(cursorPosition); // Restore cursor position
            }
        });

        gotoReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement Forgot Password functionality here if needed
                sendPasswordResetEmail();
            }
        });
    }


    private void loginUser() {
        String emailText = username.getText().toString().trim(); // Get and trim the email input
        String passwordText = password.getText().toString().trim(); // Get and trim the password input

        // Check if either the email or password fields are empty
        if (TextUtils.isEmpty(emailText) || TextUtils.isEmpty(passwordText)) {
            Toast.makeText(LoginActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check network connection
        if (!NetworkUtil.isNetworkConnected(this)) {
            Toast.makeText(this, "No internet connection. Please check your network settings.", Toast.LENGTH_LONG).show();
            return;
        }

        // Show progress bar or loading indicator
        progressBar.setVisibility(View.VISIBLE);

        // Perform sign in with the provided email and password
        mAuth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Hide progress bar or loading indicator
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            // Login successful
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Fetch additional user details if required, such as username
                                fetchUsernameFromDatabase(user.getUid());
                            } else {
                                // User is null, handle this case
                                Toast.makeText(LoginActivity.this, "Failed to retrieve user details.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Check if the exception is related to network
                            if (task.getException() instanceof FirebaseAuthException) {
                                Toast.makeText(LoginActivity.this, "Network error, please try again later.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Login failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }



    private void fetchUsernameFromDatabase(String userId) {
        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String fetchedUsername = dataSnapshot.child("username").getValue(String.class);
                    if (fetchedUsername != null) {
                        // Display the welcome toast with the fetched username
                        Toast.makeText(LoginActivity.this, "Welcome, " + fetchedUsername + "!", Toast.LENGTH_SHORT).show();
                    }
                }
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the database error if needed
            }
        });
    }

    private void sendPasswordResetEmail() {
        final EditText resetMail = new EditText(this);
        AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(this, R.style.CustomAlertDialog);

        TextView messageView = new TextView(this);
        messageView.setText("Enter your email to receive reset link.");
        messageView.setTextColor(getResources().getColor(R.color.white));
        messageView.setPadding(40, 40, 40, 40);
        messageView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        passwordResetDialog.setTitle("Reset Password?");
        passwordResetDialog.setCustomTitle(messageView);
        passwordResetDialog.setView(resetMail);

        passwordResetDialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mail = resetMail.getText().toString();
                checkEmailExists(mail);
            }
        });

        passwordResetDialog.setNegativeButton("Cancel", null);
        passwordResetDialog.create().show();
    }

    private void checkEmailExists(String email) {
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mAuth.sendPasswordResetEmail(email)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(LoginActivity.this, "Reset link sent to your email.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(LoginActivity.this, "Error! Reset link is not sent: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(LoginActivity.this, "Email does not exist.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}