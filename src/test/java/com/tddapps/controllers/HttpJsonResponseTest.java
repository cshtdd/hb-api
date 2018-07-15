package com.tddapps.controllers;

import com.tddapps.actions.response.TextMessage;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

public class HttpJsonResponseTest {
    @Test
    public void CanBeCompared(){
        HttpJsonResponse<TextMessage> response1 = new HttpJsonResponse<>(200, TextMessage.OK);
        assertEquals(response1, response1);
        assertEquals(response1.hashCode(), response1.hashCode());

        HttpJsonResponse<TextMessage> response1Clone = new HttpJsonResponse<>(200, TextMessage.OK);
        assertEquals(response1, response1Clone);
        assertEquals(response1Clone, response1);
        assertEquals(response1Clone.hashCode(), response1.hashCode());

        assertNotEquals(null, response1);
        assertNotEquals(response1, null);

        assertEquals(
                new HttpJsonResponse<>(200, TextMessage.OK),
                new HttpJsonResponse<>(200, TextMessage.OK)
        );
        assertEquals(
                new HttpJsonResponse<>(500, "blah"),
                new HttpJsonResponse<>(500, "blah")
        );
        assertEquals(
                new HttpJsonResponse<>(500, "blah").hashCode(),
                new HttpJsonResponse<>(500, "blah").hashCode()
        );
        assertNotEquals(
                new HttpJsonResponse<>(500, "foo"),
                new HttpJsonResponse<>(500, "bar")
        );
        assertNotEquals(
                new HttpJsonResponse<>(501, "blah"),
                new HttpJsonResponse<>(500, "blah")
        );
        assertNotEquals(
                new HttpJsonResponse<>(501, "blah").hashCode(),
                new HttpJsonResponse<>(500, "blah").hashCode()
        );

        assertEquals(
                new HttpJsonResponse<>(200, null),
                new HttpJsonResponse<>(200, null)
        );
        assertNotEquals(
                new HttpJsonResponse<>(200, TextMessage.OK),
                new HttpJsonResponse<>(200, null)
        );
        assertNotEquals(
                new HttpJsonResponse<>(200, null),
                new HttpJsonResponse<>(200, TextMessage.OK)
        );
    }
}
