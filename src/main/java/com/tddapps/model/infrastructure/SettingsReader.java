package com.tddapps.model.infrastructure;

public interface SettingsReader {
    String ReadString(String name);
    String ReadString(String name, String defaultValue);
}
