package com.example.urgentcare.helper;

public class User {
    private String username;
    private String email;
    private String phoneNumber;

    public User() {
        // Default constructor required for Firebase
    }

    public User(String username, String email, String phoneNumber) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
