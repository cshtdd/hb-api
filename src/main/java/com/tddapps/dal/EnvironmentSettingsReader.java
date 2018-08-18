package com.tddapps.dal;

public class EnvironmentSettingsReader implements SettingsReader {
    @Override
    public String ReadString(String name) {
        return ReadString(name, "");
    }

    @Override
    public String ReadString(String name, String defaultValue) {
        String result = System.getenv(name);

        if (result == null){
            return defaultValue;
        }

        return result;
    }
}
