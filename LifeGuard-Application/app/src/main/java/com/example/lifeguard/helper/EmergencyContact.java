package com.example.lifeguard.helper;

public class EmergencyContact {
    public String name, phone, key;

    public EmergencyContact(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public EmergencyContact() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
