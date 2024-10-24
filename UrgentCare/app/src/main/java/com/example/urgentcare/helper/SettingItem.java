package com.example.urgentcare.helper;

public class SettingItem {
    private String name;
    private int icon;

    public SettingItem(String name, int icon) {
        this.name = name;
        this.icon = icon;
    }

    public SettingItem() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }
}
