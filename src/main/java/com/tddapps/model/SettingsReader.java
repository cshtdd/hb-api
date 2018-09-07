package com.tddapps.model;

public interface SettingsReader {
    String ReadString(String name);
    String ReadString(String name, String defaultValue);
}
