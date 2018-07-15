package com.tddapps.utils;

public abstract class StringExtensions {
    public static String EmptyWhenNull(String value){
        return value != null ? value : "";
    }
}

