package com.tddapps.model.infrastructure;

import lombok.val;

public class EnvironmentSettingsReader implements SettingsReader {
    @Override
    public String ReadString(String name) {
        return ReadString(name, "");
    }

    @Override
    public String ReadString(String name, String defaultValue) {
        val result = System.getenv(name);

        if (result == null){
            return defaultValue;
        }

        return result;
    }
}
