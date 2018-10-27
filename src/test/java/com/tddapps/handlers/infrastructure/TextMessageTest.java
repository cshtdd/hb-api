package com.tddapps.handlers.infrastructure;

import lombok.val;
import org.junit.jupiter.api.Test;

import static com.tddapps.utils.EqualityAssertions.shouldBeEqual;
import static com.tddapps.utils.EqualityAssertions.shouldNotBeEqual;
import static org.junit.Assert.*;

class TextMessageTest {
    @Test
    void MessageCannotBeNull(){
        try{
            TextMessage.create(null);
            fail("Should have thrown exception");
        }
        catch (NullPointerException e){
            assertNotNull(e);
        }
    }

    @Test
    void ReturnsARelevantStringRepresentation(){
        assertEquals("TextMessage:", TextMessage.create("").toString());
        assertEquals("TextMessage:sample", TextMessage.create("sample").toString());
    }

    @Test
    void CanBeConvertedAsJson(){
        assertEquals("{\"message\": \"\"}", TextMessage.create("").asJson());
        assertEquals("{\"message\": \"sample\"}", TextMessage.create("sample").asJson());
    }

    @Test
    void CanBeCompared(){
        val message1 = TextMessage.create("sample1");
        shouldBeEqual(message1, message1);

        shouldNotBeEqual(null, message1);

        val message1Equivalent = TextMessage.create("sample1");
        shouldBeEqual(message1, message1Equivalent);

        shouldBeEqual(TextMessage.create(""), TextMessage.create(""));

        shouldBeEqual(TextMessage.create("sample1"), TextMessage.create("sample1"));
        shouldBeEqual(TextMessage.create("sample1"), TextMessage.create("sam" + "ple1"));

        shouldNotBeEqual(TextMessage.create("sample1"), TextMessage.create("sample2"));
    }

    @Test
    void PredefinedMessages(){
        assertEquals("OK", TextMessage.OK.getMessage());
    }

}
