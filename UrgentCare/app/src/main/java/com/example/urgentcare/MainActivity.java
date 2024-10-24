package com.example.urgentcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.urgentcare.Utils.EmergencyService;
import com.example.urgentcare.handlers.EmergencyMessageSender;
import com.example.urgentcare.handlers.FlashAndMediaHandler;
import com.example.urgentcare.handlers.LocationHandler;
import com.example.urgentcare.helper.EmergencyContact;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ImageButton friends, flashSiren, videoRecordButton, locateHospitalButton, chatBotButton;
    private FlashAndMediaHandler flashAndMediaHandler;
    private boolean isFlashSirenActive = false;
    private Button sosButton;
    private static final int REQUEST_PHONE_CALL = 1;
    private static final int REQUEST_SEND_SMS = 2;

    private static final int REQUEST_LOCATION = 3; // Define location request code
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_VIDEO_CAPTURE = 101;
    private LocationHandler locationHandler;
    private long backPressedTime;

    private MediaRecorder mediaRecorder;
    private String audioFilePath;
    private boolean isRecording = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private ProgressBar progressBar;
    private EmergencyMessageSender emergencyMessageSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationHandler = new LocationHandler(this);
        emergencyMessageSender = new EmergencyMessageSender(this, locationHandler);

        friends = findViewById(R.id.friendsButton);
        flashSiren = findViewById(R.id.flashSirenButton);
        sosButton = findViewById(R.id.sosButton);
        videoRecordButton = findViewById(R.id.videoRecordButton);
        locateHospitalButton = findViewById(R.id.locateHospitalButton);
        progressBar = findViewById(R.id.progressBar);
        chatBotButton = findViewById(R.id.chatBotButton);

        videoRecordButton.setOnClickListener(view -> showRecordingConfirmationDialog());

        sosButton.setOnClickListener(view -> showCallConfirmationDialog());

        sosButton.setOnLongClickListener(view -> {
            showSMSConfirmationDialog();
            return true; // Indicates the callback consumed the long click
        });

        Intent serviceIntent = new Intent(this, EmergencyService.class);
        startService(serviceIntent);

        ImageButton settingButton = findViewById(R.id.setting);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SetingsActivity.class));
            }
        });

        locateHospitalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNetworkAvailable()) {
                    Toast.makeText(MainActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE); // Show ProgressBar
                locateHospitalButton.setEnabled(false);

                locationHandler.getCurrentLocation(REQUEST_LOCATION, new LocationHandler.CustomLocationCallback() {
                    @Override
                    public void onCustomLocationResult(String locationString) {
                        // Save locationString to Firebase database
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        userRef.child("currentLocation").setValue(locationString)
                                .addOnSuccessListener(aVoid -> {
                                    progressBar.setVisibility(View.GONE); // Hide ProgressBar
                                    locateHospitalButton.setEnabled(true); // Re-enable the button
                                    Intent intent = new Intent(MainActivity.this, HospitalActivity.class);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(e -> {
                                    progressBar.setVisibility(View.GONE); // Hide ProgressBar
                                    locateHospitalButton.setEnabled(true); // Re-enable the button
                                    Toast.makeText(MainActivity.this, "Failed to update location", Toast.LENGTH_SHORT).show();
                                });
                    }
                });
            }
        });



        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, FriendsActivity.class));
            }
        });

        chatBotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FirstAidActivity.class));
            }
        });


        // flash light and siren
        flashAndMediaHandler = new FlashAndMediaHandler(this);

        flashSiren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFlashSirenActive) {
                    flashAndMediaHandler.startFlashAndSiren();
                } else {
                    flashAndMediaHandler.stopFlashAndSiren();
                }
                isFlashSirenActive = !isFlashSirenActive;
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 4000 > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFlashSirenActive) {
            flashAndMediaHandler.stopFlashAndSiren();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFlashSirenActive) {
            flashAndMediaHandler.stopFlashAndSiren();
            isFlashSirenActive = false;
        }
    }


    // Method to check network availability
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void showCallConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);

        TextView messageView = new TextView(this);
        messageView.setText("Do you want to call 999?");
        messageView.setTextColor(getResources().getColor(R.color.white));
        messageView.setPadding(40, 40, 40, 40);
        messageView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        builder.setCustomTitle(messageView)
                .setPositiveButton("Call", (dialog, which) -> makePhoneCall())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void makePhoneCall() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:999"));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
        } else {
            startActivity(callIntent);
        }
    }

    private void showSMSConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);

        TextView messageView = new TextView(this);
        messageView.setText("Do you want to send an emergency SMS to all contacts?");
        messageView.setTextColor(getResources().getColor(R.color.white));
        messageView.setPadding(40, 40, 40, 40);
        messageView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        builder.setCustomTitle(messageView)
                .setPositiveButton("Send", (dialog, which) -> sendEmergencyMessages())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void sendEmergencyMessages() {
        DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("emergencyMessage");

        locationHandler.getCurrentLocation(REQUEST_LOCATION, locationString -> {
            messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String message = dataSnapshot.getValue(String.class);
                        if (message != null && !message.isEmpty()) {
                            // Append the location to the message and link
                            String mapLink = "https://www.google.com/maps/search/?api=1&query=" +
                                    locationString.replace("Latitude: ", "").replace(", Longitude: ", ",");

//                            String fullMessage = message + "\nLocation: " + locationString
//                                    + "\nMap: "
//                                    + mapLink;

                            String fullMessage = message + "\n"+ mapLink;

                            // Split the message into segments
                            ArrayList<String> messageSegments = SmsManager.getDefault().divideMessage(fullMessage);

                            // Send each segment as a separate SMS
                            for (String segment : messageSegments) {
                                sendMessagesToContacts(segment);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "Error loading message: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void sendMessagesToContacts(String fullMessage) {
        DatabaseReference contactRef = FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("emergencyContact");

        contactRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot contactsSnapshot) {
                for (DataSnapshot snapshot : contactsSnapshot.getChildren()) {
                    EmergencyContact contact = snapshot.getValue(EmergencyContact.class);
                    if (contact != null) {
                        sendSMS(contact.getPhone(), fullMessage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendSMS(String phoneNumber, String message) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_SEND_SMS);
        } else {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                Toast.makeText(getApplicationContext(), "SMS Sent to " + phoneNumber, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "SMS Failed to Send", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            startRecording();
        }
    }

    private void showRecordingConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);

        TextView messageView = new TextView(this);
        messageView.setText("Are you sure you want to start recording?");
        messageView.setTextColor(getResources().getColor(R.color.white));
        messageView.setPadding(40, 40, 40, 40);
        messageView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        builder.setCustomTitle(messageView)
                .setPositiveButton("Yes", (dialog, which) -> requestCameraPermission())
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording();
            } else {
                Toast.makeText(this, "Camera permission is required to record video", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startAudioRecording();
            } else {
                Toast.makeText(this, "Audio record permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void startRecording() {
        // Start Video Recording
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }

        // Start Audio Recording
        startAudioRecording();
    }

    private void startAudioRecording() {
        // Check for audio recording permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            setupMediaRecorder();
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
                isRecording = true;
                Toast.makeText(this, "Audio recording started", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                Toast.makeText(this, "Audio recording failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void stopAudioRecording() {
        if (isRecording) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;

            Toast.makeText(this, "Audio recording stopped", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        // Use the app's specific directory for storing the audio file
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        if (!storageDir.exists() && !storageDir.mkdirs()) {
            Toast.makeText(this, "Failed to create storage directory", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = "audio_recording_" + System.currentTimeMillis() + ".3gp";
        File audioFile = new File(storageDir, fileName);

        audioFilePath = audioFile.getAbsolutePath();
        mediaRecorder.setOutputFile(audioFilePath);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VIDEO_CAPTURE) {
            stopAudioRecording(); // Stop audio recording when video recording is done
        }
    }



}
