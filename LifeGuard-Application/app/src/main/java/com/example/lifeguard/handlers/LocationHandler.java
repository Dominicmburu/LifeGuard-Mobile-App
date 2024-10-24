package com.example.lifeguard.handlers;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationHandler {
    public static final int REQUEST_LOCATION = 3;
    private final Activity activity;
    private final FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    public LocationHandler(Activity activity) {
        this.activity = activity;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    public interface CustomLocationCallback {
        void onCustomLocationResult(String location);
    }

    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    public void getCurrentLocation(int requestCode, CustomLocationCallback customLocationCallback) {
        // Check if permission is granted
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);
            return;
        }

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    String locationString = "Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude();
                    customLocationCallback.onCustomLocationResult(locationString);
                    stopLocationUpdates();
                    break; // Process only the first location update
                }
            }
        };

        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                String locationString = "Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude();
                                customLocationCallback.onCustomLocationResult(locationString);
                            } else {
                                // Request fresh location updates
                                fusedLocationClient.requestLocationUpdates(createLocationRequest(), locationCallback, Looper.getMainLooper());
                            }
                        }
                    });
        } catch (SecurityException e) {
            // Handle the security exception
            Toast.makeText(activity, "Location permission not granted", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopLocationUpdates() {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }


}
