package com.example.urgentcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.urgentcare.adapter.SettingAdapter;
import com.example.urgentcare.helper.SettingItem;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class SetingsActivity extends AppCompatActivity {
    private Button logoutButton;
    private RecyclerView settingsRecycler;
    private SettingAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setings);

        // Initialize the logout button
        logoutButton = findViewById(R.id.logout);

        // Set a click listener on the logout button
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log out the user
                FirebaseAuth.getInstance().signOut();

                // Redirect to the LoginActivity
                Intent intent = new Intent(SetingsActivity.this, LoginActivity.class);
                // Clear the activity stack to prevent the user from going back to the MainActivity
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                // Finish the current activity
                finish();
            }
        });

        ImageView backButton = findViewById(R.id.toolbar_settings_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SetingsActivity.this, MainActivity.class));
                finish();
            }
        });


        settingsRecycler = findViewById(R.id.settingsRecy);
        settingsRecycler.setLayoutManager(new LinearLayoutManager(this));

        List<SettingItem> settingItems = new ArrayList<>();
        settingItems.add(new SettingItem("Profile", R.drawable.baseline_person_24));
        settingItems.add(new SettingItem("Guide", R.drawable.baseline_info_24));
        settingItems.add(new SettingItem("About", R.drawable.baseline_book_24));

        adapter = new SettingAdapter(settingItems, this::onSettingItemSelected);
        settingsRecycler.setAdapter(adapter);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void onSettingItemSelected(SettingItem item) {
        switch (item.getName()) {
            case "Guide":
                // Open Guide Activity
                startActivity(new Intent(this, GuideActivity.class));
                break;
            case "Profile":
                // Open Profile Activity
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case "About":
                // Open About Activity
//                startActivity(new Intent(this, AboutActivity.class));
                Toast.makeText(this, "About under development", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}