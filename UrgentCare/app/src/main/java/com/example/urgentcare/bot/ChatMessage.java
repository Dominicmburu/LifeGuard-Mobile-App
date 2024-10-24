package com.example.urgentcare.bot;

import java.util.Objects;

public class ChatMessage {
    private String text;
    private boolean isUser;
    private boolean isResponse;

    public ChatMessage(String text, boolean isUser, boolean isResponse) {
        this.text = text;
        this.isUser = isUser;
        this.isResponse = isResponse;
    }

    public String getText() {
        return text;
    }

    public boolean isUser() {
        return isUser;
    }

    public boolean isResponse() {
        return isResponse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessage that = (ChatMessage) o;
        return isUser == that.isUser &&
                isResponse == that.isResponse &&
                Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, isUser, isResponse);
    }
}

