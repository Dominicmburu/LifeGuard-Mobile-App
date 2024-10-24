package com.example.urgentcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.urgentcare.adapter.HospitalDetailsAdapter;
import com.example.urgentcare.helper.HospitalDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class HospitalDetailsActivity extends AppCompatActivity {
    private String facilityName;
    private List<HospitalDetail> hospitalDetails;
    private HospitalDetailsAdapter adapter;
    private RecyclerView recyclerView;
    private Button viewLocationBtn;
    TextView facilityNameTextView;
    private double hospitalLatitude, hospitalLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_details);

        recyclerView = findViewById(R.id.hospitaldetailsRecy);
        facilityNameTextView = findViewById(R.id.facility_name);
        viewLocationBtn = findViewById(R.id.viewLocation);
        hospitalDetails = new ArrayList<>();
        adapter = new HospitalDetailsAdapter(hospitalDetails);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        if (intent != null) {
            facilityName = intent.getStringExtra("facilityName");
            facilityNameTextView.setText(facilityName);
            loadHospitalDetails(facilityName);
        }

        viewLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(HospitalDetailsActivity.this, MapActivity.class);
                mapIntent.putExtra("HospitalLat", hospitalLatitude);
                mapIntent.putExtra("HospitalLng", hospitalLongitude);
                startActivity(mapIntent);
            }
        });

        ImageView backButton = findViewById(R.id.toolbar_hospital_details_back);
        backButton.setOnClickListener(v -> finish());
    }

    private void loadHospitalDetails(String facilityName) {
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

                if (name.equals(facilityName)) {

                    hospitalDetails.clear(); // Clear existing details

                    hospitalLatitude = properties.getDouble("Latitude");
                    hospitalLongitude = properties.getDouble("Longitude");


                    // Adding all details of the hospital to the list
                    addHospitalDetail(properties, "Facility Name", "Facility_N");
                    addHospitalDetail(properties, "Type", "Type");
                    addHospitalDetail(properties, "Owner", "Owner");
                    addHospitalDetail(properties, "County", "County");
                    addHospitalDetail(properties, "Sub County", "Sub_County");
                    addHospitalDetail(properties, "Division", "Division");
                    addHospitalDetail(properties, "Location", "Location");
                    addHospitalDetail(properties, "Sub Location", "Sub_Locati");
                    addHospitalDetail(properties, "Constituency", "Constituen");
                    addHospitalDetail(properties, "Nearest To", "Nearest_To");
                    addHospitalDetail(properties, "Latitude", "Latitude");
                    addHospitalDetail(properties, "Longitude", "Longitude");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed(); // This should just finish HospitalDetailsActivity
    }


    private void addHospitalDetail(JSONObject properties, String detailName, String jsonKey) throws JSONException {
        String detailValue = properties.optString(jsonKey, "Not available");
        hospitalDetails.add(new HospitalDetail(detailName, detailValue));
    }
}