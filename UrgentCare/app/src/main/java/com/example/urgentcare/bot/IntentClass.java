package com.example.urgentcare.bot;

import java.util.List;

public class IntentClass {
    private String tag;
    private List<String> patterns;
    private List<String> responses;

    public String getTag() {
        return tag;
    }

    public List<String> getPatterns() {
        return patterns;
    }

    public List<String> getResponses() {
        return responses;
    }
}

