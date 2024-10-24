package com.example.lifeguard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.lifeguard.adapter.GuideAdapter;

public class GuideActivity extends AppCompatActivity {
    private String[] userGuidelines;
    private RecyclerView guideRecyclerView;
    private GuideAdapter guideAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        ImageView backButton = findViewById(R.id.toolbar_guide_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GuideActivity.this, SetingsActivity.class));
                finish();
            }
        });

        userGuidelines = new String[] {
                "Always Enable Location Services: Keep your phone's GPS location turned on.",
                "Keep Your Phone Charged: Ensure your phone battery is always sufficiently charged.",
                "Maintain Sufficient Phone Credit: Have enough credit to send SMS messages for emergencies.",
                "Ensure Internet Connectivity: A stable internet connection is needed for various app features.",
                "Update Emergency Contacts Regularly: Keep your emergency contact list current.",
                "Familiarize Yourself with the App: Regularly explore and practice using the app's features.",
                "Test Features Periodically: Check functions like the flashlight, siren, and SOS alerts.",
                "Update the App Regularly: Keep the app updated for the latest features and security.",
                "Respect Privacy and Consent Laws: When using this application.",
                "Learn Basic First Aid: Basic first-aid knowledge can be invaluable.",
                "Use Emergency Button Wisely: Only use it in real emergencies to avoid misdirecting services."
        };

        guideRecyclerView = findViewById(R.id.guideRecy);
        guideRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        guideAdapter = new GuideAdapter(userGuidelines);
        guideRecyclerView.setAdapter(guideAdapter);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

