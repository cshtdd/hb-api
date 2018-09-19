package com.tddapps.model.aws;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import lombok.val;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;

public abstract class AttributeValueMapToJsonStringConverter {
    public static String ToJson(Map<String, AttributeValue> input){
        if (input == null){
            throw new NullPointerException("input");
        }

        val m = new HashMap<String, Object>();

        for(val k: input.keySet()) {
            val value = input.get(k);
            val valueN = value.getN();

            m.put(k, parseInt(valueN));
        }

        return new JSONObject(m)
                .toString();
    }
}
