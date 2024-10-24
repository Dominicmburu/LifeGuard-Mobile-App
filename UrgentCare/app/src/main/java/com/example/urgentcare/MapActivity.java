package com.example.urgentcare;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.PolyUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final float DEFAULT_ZOOM = 15;
    private double hospitalLat;
    private double hospitalLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ImageView backButton = findViewById(R.id.toolbar_map_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapActivity.this, HospitalActivity.class));
                finish();
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Assume these are the hospital's latitude and longitude you got from the previous activity
        hospitalLat = getIntent().getDoubleExtra("HospitalLat", 0);
        hospitalLng = getIntent().getDoubleExtra("HospitalLng", 0);
        LatLng hospitalLocation = new LatLng(hospitalLat, hospitalLng);

        // Add a marker for the hospital and move the camera
        mMap.addMarker(new MarkerOptions().position(hospitalLocation).title("Hospital Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hospitalLocation, DEFAULT_ZOOM));

        updateLocationUI();
        getDeviceLocation();

    }

//    private void updateLocationUI() {
//        if (mMap == null) {
//            return;
//        }
//        try {
//            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
//                    android.Manifest.permission.ACCESS_FINE_LOCATION)
//                    == PackageManager.PERMISSION_GRANTED) {
//                mMap.setMyLocationEnabled(true);
//                mMap.getUiSettings().setMyLocationButtonEnabled(true);
//            } else {
//                mMap.setMyLocationEnabled(false);
//                mMap.getUiSettings().setMyLocationButtonEnabled(false);
//                ActivityCompat.requestPermissions(this, new String[]{
//                        Manifest.permission.ACCESS_FINE_LOCATION
//                }, 1);
//            }
//        } catch (SecurityException e)  {
//            e.printStackTrace();
//        }
//    }

    private void updateLocationUI() {
        if (mMap != null) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    private void getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    // Set the map's camera position to the current location of the device.
                    Location lastKnownLocation = task.getResult();
                    if (lastKnownLocation != null) {
                        LatLng currentLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, DEFAULT_ZOOM));

                        // Draw a route following the roads using Google Directions API
                        drawRouteFollowingRoads(currentLocation, new LatLng(hospitalLat, hospitalLng));
                    }
                } else {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), DEFAULT_ZOOM));
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void drawRouteFollowingRoads(LatLng origin, LatLng destination) {
        // Here you would make a network call to the Directions API, then parse the results
        // For demonstration purposes, this is a mock function that assumes you have the route information
        // You must replace this with a real network call and JSON parsing (e.g., using Retrofit or Volley)

        // This is a mock function to simulate network operation
        getDirectionsRoute(origin, destination, route -> {
            // Assuming 'route' is a List<LatLng> representing the route points
            mMap.addPolyline(new PolylineOptions().addAll(route).width(10).color(ContextCompat.getColor(this, R.color.user_back_color)));
        });
    }

    // Mock interface to simulate getting a directions route
    private interface DirectionsResultCallback {
        void onRouteReady(List<LatLng> route);
    }

    // Mock method to simulate network operation
    private void getDirectionsRoute(LatLng origin, LatLng destination, DirectionsResultCallback callback) {
        // Construct the Directions API URL
        String apiKey = "AIzaSyDHQgYOGStyNxUi1cmDTwOmdxmxG6szo5Y"; // Replace with your actual API key
        String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + origin.latitude + "," + origin.longitude +
                "&destination=" + destination.latitude + "," + destination.longitude +
                "&key=" + apiKey;

        // Create OkHttp client
        OkHttpClient client = new OkHttpClient();

        // Build the request
        Request request = new Request.Builder().url(url).build();

        // Enqueue the request
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
                // Handle failure
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        // Parse the response
                        String jsonData = response.body().string();
                        JSONObject jsonObject = new JSONObject(jsonData);
                        // Assuming the response has a "routes" JSONArray
                        String polyline = jsonObject
                                .getJSONArray("routes")
                                .getJSONObject(0)
                                .getJSONObject("overview_polyline")
                                .getString("points");

                        List<LatLng> route = PolyUtil.decode(polyline);
                        // Run on the UI thread
                        runOnUiThread(() -> callback.onRouteReady(route));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // Handle exceptions
                }
            }
        });
    }





//    private void getDeviceLocation() {
//        try {
//            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
//                    android.Manifest.permission.ACCESS_FINE_LOCATION)
//                    == PackageManager.PERMISSION_GRANTED) {
//                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
//                locationResult.addOnCompleteListener(this, task -> {
//                    if (task.isSuccessful()) {
//                        // Set the map's camera position to the current location of the device.
//                        Location lastKnownLocation = task.getResult();
//                        if (lastKnownLocation != null) {
//                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
//                                    new LatLng(lastKnownLocation.getLatitude(),
//                                            lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
//
//                            // Draw a line between user location and hospital location
//                            mMap.addPolyline(new PolylineOptions()
//                                    .add(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()))
//                                    .add(new LatLng(hospitalLat, hospitalLng))
//                                    .width(5)
//                                    .color(ContextCompat.getColor(MapActivity.this, R.color.fab_color)));
//                        }
//                    } else {
//                        mMap.moveCamera(CameraUpdateFactory
//                                .newLatLngZoom(new LatLng(0, 0), DEFAULT_ZOOM));
//                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
//                    }
//                });
//            }
//        } catch (SecurityException e)  {
//            e.printStackTrace();
//        }
//
//
//    }
}
