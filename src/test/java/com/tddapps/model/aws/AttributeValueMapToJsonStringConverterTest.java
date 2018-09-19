package com.tddapps.model.aws;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static com.tddapps.model.aws.AttributeValueMapToJsonStringConverter.ToJson;
import static org.junit.jupiter.api.Assertions.*;

public class AttributeValueMapToJsonStringConverterTest {
    @Test
    public void NoMapThrowsANullPointerException(){
        try{
            ToJson(null);
            fail("should have thrown");
        }catch (NullPointerException e){
            assertNotNull(e);
        }
    }

    @Test
    public void EmptyMapProducesEmptyJson(){
        assertEquals("{}", ToJson(new HashMap<>()));
    }

    @Test
    public void ConvertsNumericValues(){
        val input = new HashMap<String, AttributeValue>(){{
            put("AAA", new AttributeValue().withN("25"));
        }};

        assertEquals("{\"AAA\":25}", ToJson(input));
    }
}
