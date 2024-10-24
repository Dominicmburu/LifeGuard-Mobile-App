package com.example.urgentcare.handlers;

import android.content.Context;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.example.urgentcare.helper.EmergencyContact;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EmergencyMessageSender {

    private Context context;
    private LocationHandler locationHandler;
    private FirebaseAuth mAuth;

    public EmergencyMessageSender(Context context, LocationHandler locationHandler) {
        this.context = context;
        this.locationHandler = locationHandler;
        this.mAuth = FirebaseAuth.getInstance();
    }

    public interface MessageSentCallback {
        void onMessageSent();
    }

    public void sendEmergencyMessages(MessageSentCallback callback) {
        DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference("users")
                .child(mAuth.getCurrentUser().getUid())
                .child("emergencyMessage");

        locationHandler.getCurrentLocation(LocationHandler.REQUEST_LOCATION, locationString -> {
            messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String message = dataSnapshot.getValue(String.class);
                        if (message != null && !message.isEmpty()) {
                            String mapLink = "https://www.google.com/maps/search/?api=1&query=" +
                                    locationString.replace("Latitude: ", "").replace(", Longitude: ", ",");
                            String fullMessage = message + "\n" + mapLink;

                            ArrayList<String> messageSegments = SmsManager.getDefault().divideMessage(fullMessage);
                            for (String segment : messageSegments) {
                                sendMessagesToContacts(segment);
                            }

                            // Notify after sending all segments
                            if (callback != null) {
                                callback.onMessageSent();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(context, "Error loading message: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void sendMessagesToContacts(String fullMessage) {
        DatabaseReference contactRef = FirebaseDatabase.getInstance().getReference("users")
                .child(mAuth.getCurrentUser().getUid())
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
                Toast.makeText(context, "Error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendSMS(String phoneNumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
    }
}
