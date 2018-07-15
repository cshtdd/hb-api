package com.tddapps.actions.response;

import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;

public class TextMessageTest {
    @Test
    public void CanBeConvertedAsJson(){
        assertEquals("{\"message\": \"\"}", new TextMessage(null).asJson());
        assertEquals("{\"message\": \"\"}", new TextMessage("").asJson());
        assertEquals("{\"message\": \"sample\"}", new TextMessage("sample").asJson());
    }
}
