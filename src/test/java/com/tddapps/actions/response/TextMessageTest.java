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
        assertEquals(message1, message1);
        assertNotEquals(null, message1);
        assertNotEquals(message1, null);


        TextMessage message1Equivalent = new TextMessage("sample1");
        assertEquals(message1, message1Equivalent);
        assertEquals(message1Equivalent, message1);

        assertEquals(new TextMessage(null), new TextMessage(null));
        assertEquals(new TextMessage(""), new TextMessage(""));
        assertEquals(new TextMessage(""), new TextMessage(null));
        assertEquals(new TextMessage(null), new TextMessage(""));

        assertEquals(new TextMessage("sample1"), new TextMessage("sample1"));
        assertEquals(new TextMessage("sample1"), new TextMessage("sam" + "ple1"));

        assertNotEquals(new TextMessage("sample1"), new TextMessage("sample2"));
        assertNotEquals(new TextMessage("sample2"), new TextMessage("sample1"));
    }
}
