package com.example.urgentcare.helper;

public class ChatsModel {
    private String message;
    private String sender; // "user" or "bot"

    public ChatsModel(String message, String sender) {
        this.message = message;
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }
}
