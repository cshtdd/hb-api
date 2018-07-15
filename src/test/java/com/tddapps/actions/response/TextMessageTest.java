package com.tddapps.actions.response;

import org.junit.jupiter.api.Test;
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
        shouldEqual(message1, message1);

        shouldNotBeEqual(null, message1);

        TextMessage message1Equivalent = TextMessage.create("sample1");
        shouldEqual(message1, message1Equivalent);

        shouldEqual(TextMessage.create(null), TextMessage.create(null));
        shouldEqual(TextMessage.create(""), TextMessage.create(""));
        shouldEqual(TextMessage.create(""), TextMessage.create(null));
        shouldEqual(TextMessage.create(null), TextMessage.create(""));

        shouldEqual(TextMessage.create("sample1"), TextMessage.create("sample1"));
        shouldEqual(TextMessage.create("sample1"), TextMessage.create("sam" + "ple1"));

        shouldNotBeEqual(TextMessage.create("sample1"), TextMessage.create("sample2"));
        shouldNotBeEqual(TextMessage.create("sample2"), TextMessage.create("sample1"));
    }

    @Test
    public void PredefinedMessages(){
        assertEquals("OK", TextMessage.OK.getMessage());
    }

    private void shouldEqual(TextMessage t1, TextMessage t2){
        assertEquals(t1, t2);
        assertEquals(t2, t1);

        if (t1 != null && t2 != null){
            assertEquals(t1.hashCode(), t2.hashCode());
            assertEquals(t2.hashCode(), t1.hashCode());
        }
    }

    private void shouldNotBeEqual(TextMessage t1, TextMessage t2){
        assertNotEquals(t1, t2);
        assertNotEquals(t2, t1);

        if (t1 != null && t2 != null){
            assertNotEquals(t1.hashCode(), t2.hashCode());
            assertNotEquals(t2.hashCode(), t1.hashCode());
        }
    }
}
