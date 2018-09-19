package com.tddapps.model.aws;

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
}
