package com.tddapps.dal;

public interface SettingsReader {
    String ReadString(String name);
    String ReadString(String name, String defaultValue);
}
