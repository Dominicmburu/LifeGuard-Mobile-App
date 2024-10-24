package com.example.urgentcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.urgentcare.adapter.firstGuideAdapter;

public class FirstUserGuideActivity extends AppCompatActivity {
    private String[] userGuidelines;
    private RecyclerView guideRecyclerView;
    private firstGuideAdapter guideAdapter;
    private Button finishGuideButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_user_guide);

        userGuidelines = new String[] {
                "Always Enable Location Services: Keep your phone's GPS location turned on.",
                "Keep Your Phone Charged: Ensure your phone battery is always sufficiently charged.",
                "Maintain Sufficient Phone Credit: Have enough credit to send SMS messages for emergencies.",
                "Ensure Internet Connectivity: A stable internet connection is needed for various app features.",
                "Update Emergency Contacts Regularly: Keep your emergency contact list current.",
                "Familiarize Yourself with the App: Regularly explore and practice using the app's features.",
                "Test Features Periodically: Check functions like the flashlight, siren, and SOS alerts.",
                "Update the App Regularly: Keep the app updated for the latest features and security.",
                "Respect Privacy and Consent Laws: Be mindful of privacy when using audio and video recording.",
                "Learn Basic First Aid: Basic first-aid knowledge can be invaluable.",
                "Use Emergency Button Wisely: Only use it in real emergencies to avoid misdirecting services."
        };

        guideRecyclerView = findViewById(R.id.first_guideRecy);
        guideRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        guideAdapter = new firstGuideAdapter(userGuidelines);
        guideRecyclerView.setAdapter(guideAdapter);

        finishGuideButton = findViewById(R.id.finishGuide);
        finishGuideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to LoginActivity
                startActivity(new Intent(FirstUserGuideActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}