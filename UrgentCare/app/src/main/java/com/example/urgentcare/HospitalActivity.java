package com.example.urgentcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.urgentcare.adapter.HospitalAdapter;
import com.example.urgentcare.helper.Hospital;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class HospitalActivity extends AppCompatActivity implements HospitalAdapter.OnHospitalClickListener{
    double userLatitude;
    double userLongitude;
    private SearchView searchView;
    private HospitalAdapter adapter;
    private List<Hospital> allHospitals;
    private List<Hospital> displayedHospitals;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);

        recyclerView = findViewById(R.id.hospitalRecy);
        searchView = findViewById(R.id.searchHospitals);

        fetchUserLocationAndLoadHospitals();

        ImageView backButton = findViewById(R.id.toolbar_hospitals_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HospitalActivity.this, MainActivity.class));
                finish();
            }
        });

        setupSearchView();
    }

    private void fetchUserLocationAndLoadHospitals() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference locationRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("currentLocation");

        locationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String locationString = dataSnapshot.getValue(String.class);
                parseLocationString(locationString);

                allHospitals = loadHospitalsFromJson(userLatitude, userLongitude);
                displayedHospitals = new ArrayList<>(allHospitals);

                adapter = new HospitalAdapter(displayedHospitals, userLatitude, userLongitude, HospitalActivity.this);
                recyclerView.setLayoutManager(new LinearLayoutManager(HospitalActivity.this));
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HospitalActivity.this, "Failed to load location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void parseLocationString(String locationString) {
        if (locationString != null && !locationString.isEmpty()) {
            String[] parts = locationString.split(",");
            if (parts.length == 2) {
                String latPart = parts[0].split(":")[1].trim();
                String lonPart = parts[1].split(":")[1].trim();
                userLatitude = Double.parseDouble(latPart);
                userLongitude = Double.parseDouble(lonPart);
            }
        }
    }


    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterHospitals(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterHospitals(newText);
                return true;
            }
        });
    }

    private void filterHospitals(String query) {
        query = query.toLowerCase();
        displayedHospitals.clear();

        if (query.isEmpty()) {
            displayedHospitals.addAll(allHospitals);
        } else {
            for (Hospital hospital : allHospitals) {
                if (hospital.getName().toLowerCase().contains(query)) {
                    displayedHospitals.add(hospital);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    private List<Hospital> loadHospitalsFromJson(double userLatitude, double userLongitude) {
        List<Hospital> hospitals = new ArrayList<>();
        try {
            InputStream is = getAssets().open("healthcare_facilities.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer, "UTF-8");
            JSONObject jsonObject = new JSONObject(json);
            JSONArray features = jsonObject.getJSONArray("features");

            for (int i = 0; i < features.length(); i++) {
                JSONObject feature = features.getJSONObject(i);
                JSONObject properties = feature.getJSONObject("properties");
                String name = properties.getString("Facility_N");
                String type = properties.getString("Type");
                double latitude = properties.getDouble("Latitude");
                double longitude = properties.getDouble("Longitude");

                if (isWithin50Km(userLatitude, userLongitude, latitude, longitude)) {
                    hospitals.add(new Hospital(name, type, latitude, longitude));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Sorting hospitals by distance
        hospitals.sort((h1, h2) -> {
            float[] results1 = new float[1];
            float[] results2 = new float[1];
            Location.distanceBetween(userLatitude, userLongitude, h1.getLatitude(), h1.getLongitude(), results1);
            Location.distanceBetween(userLatitude, userLongitude, h2.getLatitude(), h2.getLongitude(), results2);
            return Float.compare(results1[0], results2[0]);
        });

        return hospitals;
    }

    private boolean isWithin50Km(double userLat, double userLng, double hospitalLat, double hospitalLng) {
        float[] results = new float[1];
        Location.distanceBetween(userLat, userLng, hospitalLat, hospitalLng, results);
        return results[0] <= 50000; // 50km in meters
    }

    @Override
    public void onHospitalClick(Hospital hospital) {
        // Handle the hospital click event
        Intent intent = new Intent(this, HospitalDetailsActivity.class);
        intent.putExtra("facilityName", hospital.getName());
        startActivity(intent);
    }
}