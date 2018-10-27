package com.tddapps.utils;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.tddapps.utils.JsonNodeHelper.*;
import static org.junit.jupiter.api.Assertions.*;

class JsonNodeHelperTest {

    @Test
    void ReadsStringFieldWhenProvided(){
        assertEquals("value1", readString(parse("{\"field1\": \"value1\"}"), "field1"));
        assertEquals("value1", readString(parse("{\"field2\": \"value1\"}"), "field2"));
    }

    @Test
    void AssumesDefaultValueForStringFieldWhenMissing(){
        assertEquals("AAAA", readString(parse("{}"), "field1", "AAAA"));
        assertEquals("AAAA", readString(parse("{\"field2\": \"value1\"}"), "field1", "AAAA"));
        assertEquals("AAAA", readString(parse("{\"field1\": null}"), "field1", "AAAA"));
    }

    @Test
    void ReadsIntFieldWhenProvided(){
        assertEquals(15, readInt(parse("{\"n\": 15}"), "n", -1));
        assertEquals(25, readInt(parse("{\"m\": 25}"), "m", -1));
    }

    @Test
    void ReadsIntFieldWhenProvidedIsAValidNumericString(){
        assertEquals(15, readInt(parse("{\"n\": \"15\"}"), "n", -1));
        assertEquals(25, readInt(parse("{\"m\": \"25\"}"), "m", -1));
    }

    @Test
    void ReadsIntFieldTruncatesFloatValues(){
        assertEquals(15, readInt(parse("{\"n\": 15.45}"), "n", -1));
        assertEquals(15, readInt(parse("{\"n\": 15.95}"), "n", -1));
        assertEquals(15, readInt(parse("{\"n\": \"15.45\"}"), "n", -1));
        assertEquals(15, readInt(parse("{\"n\": \"15.95\"}"), "n", -1));
    }

    @Test
    void ReadIntReturnsDefaultValueWhenFieldIsMissing(){
        assertEquals(-1, readInt(parse("{}"), "n", -1));
        assertEquals(-1, readInt(parse("{\"n\": null}"), "n", -1));
    }

    @Test
    void ReadIntReturnsDefaultValueWhenFieldValueIsNotAValidNumber(){
        assertEquals(-1, readInt(parse("{\"n\": \"\"}"), "n", -1));
        assertEquals(-1, readInt(parse("{\"n\": \"  \"}"), "n", -1));
        assertEquals(-1, readInt(parse("{\"n\": \"pete\"}"), "n", -1));
    }

    private JsonNode parse(String body){
        try {
            return JsonNodeHelper.parse(body);
        } catch (IOException e) {
            fail("Parsing seeded body shouldn't throw", e);
            return null;
        }
    }
}
