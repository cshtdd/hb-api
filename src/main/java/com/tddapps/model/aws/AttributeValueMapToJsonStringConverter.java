package com.tddapps.model.aws;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.Map;

public abstract class AttributeValueMapToJsonStringConverter {
    public static String ToJson(Map<String, AttributeValue> input){
        if (input == null){
            throw new NullPointerException("input");
        }
        return "{}";
    }
}
