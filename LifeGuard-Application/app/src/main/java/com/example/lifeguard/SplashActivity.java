package com.example.lifeguard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Check if user is signed in (non-null) and update UI accordingly.
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    // User is signed in, go to main activity
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                } else {
                    // No user is signed in, go to login activity
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
                finish();

//                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
//                finish();
            }
        }, 3000);
    }
}