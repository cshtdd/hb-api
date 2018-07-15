package com.tddapps.actions.response;

import org.junit.jupiter.api.Test;

import static com.tddapps.utils.EqualityAssertions.*;
import static org.junit.Assert.*;

public class TextMessageTest {
    @Test
    public void ReturnsARelevantStringRepresentation(){
        assertEquals("TextMessage:", TextMessage.create(null).toString());
        assertEquals("TextMessage:", TextMessage.create("").toString());
        assertEquals("TextMessage:sample", TextMessage.create("sample").toString());
    }

    @Test
    public void CanBeConvertedAsJson(){
        assertEquals("{\"message\": \"\"}", TextMessage.create(null).asJson());
        assertEquals("{\"message\": \"\"}", TextMessage.create("").asJson());
        assertEquals("{\"message\": \"sample\"}", TextMessage.create("sample").asJson());
    }

    @Test
    public void CanBeCompared(){
        TextMessage message1 = TextMessage.create("sample1");
        shouldBeEqual(message1, message1);

        shouldNotBeEqual(null, message1);

        TextMessage message1Equivalent = TextMessage.create("sample1");
        shouldBeEqual(message1, message1Equivalent);

        shouldBeEqual(TextMessage.create(null), TextMessage.create(null));
        shouldBeEqual(TextMessage.create(""), TextMessage.create(""));
        shouldBeEqual(TextMessage.create(""), TextMessage.create(null));
        shouldBeEqual(TextMessage.create(null), TextMessage.create(""));

        shouldBeEqual(TextMessage.create("sample1"), TextMessage.create("sample1"));
        shouldBeEqual(TextMessage.create("sample1"), TextMessage.create("sam" + "ple1"));

        shouldNotBeEqual(TextMessage.create("sample1"), TextMessage.create("sample2"));
        shouldNotBeEqual(TextMessage.create("sample2"), TextMessage.create("sample1"));
    }

    @Test
    public void PredefinedMessages(){
        assertEquals("OK", TextMessage.OK.getMessage());
    }

}
