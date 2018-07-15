package com.tddapps.actions.response;

import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;

public class TextMessageTest {
    @Test
    public void ReturnsARelevantStringRepresentation(){
        assertEquals("TextMessage:", new TextMessage(null).toString());
        assertEquals("TextMessage:", new TextMessage("").toString());
        assertEquals("TextMessage:sample", new TextMessage("sample").toString());
    }

    @Test
    public void CanBeConvertedAsJson(){
        assertEquals("{\"message\": \"\"}", new TextMessage(null).asJson());
        assertEquals("{\"message\": \"\"}", new TextMessage("").asJson());
        assertEquals("{\"message\": \"sample\"}", new TextMessage("sample").asJson());
    }

    @Test
    public void CanBeCompared(){
        TextMessage message1 = new TextMessage("sample1");
        shouldEqual(message1, message1);

        shouldNotBeEqual(null, message1);

        TextMessage message1Equivalent = new TextMessage("sample1");
        shouldEqual(message1, message1Equivalent);

        shouldEqual(new TextMessage(null), new TextMessage(null));
        shouldEqual(new TextMessage(""), new TextMessage(""));
        shouldEqual(new TextMessage(""), new TextMessage(null));
        shouldEqual(new TextMessage(null), new TextMessage(""));

        shouldEqual(new TextMessage("sample1"), new TextMessage("sample1"));
        shouldEqual(new TextMessage("sample1"), new TextMessage("sam" + "ple1"));

        shouldNotBeEqual(new TextMessage("sample1"), new TextMessage("sample2"));
        shouldNotBeEqual(new TextMessage("sample2"), new TextMessage("sample1"));
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
