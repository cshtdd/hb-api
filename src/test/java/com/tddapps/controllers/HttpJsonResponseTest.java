package com.tddapps.controllers;

import com.tddapps.actions.response.TextMessage;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

public class HttpJsonResponseTest {
    @Test
    public void PredefinedResponses(){
        shouldBeEqual(
                new HttpJsonResponse<>(200, TextMessage.OK),
                HttpJsonResponse.Success(TextMessage.OK)
        );

        shouldBeEqual(
                new HttpJsonResponse<>(400, TextMessage.create("missing parameter")),
                HttpJsonResponse.BadRequest(TextMessage.create("missing parameter"))

        );

        shouldBeEqual(
                new HttpJsonResponse<>(400, TextMessage.create("missing parameter")),
                HttpJsonResponse.BadRequestWithMessage("missing parameter")
        );

        shouldBeEqual(
                new HttpJsonResponse<>(500, TextMessage.create("database error")),
                HttpJsonResponse.ServerError(TextMessage.create("database error"))

        );

        shouldBeEqual(
                new HttpJsonResponse<>(500, TextMessage.create("database error")),
                HttpJsonResponse.ServerErrorWithMessage("database error")
        );
    }

    @Test
    public void CanBeCompared(){
        HttpJsonResponse<TextMessage> response1 = new HttpJsonResponse<>(200, TextMessage.OK);
        shouldBeEqual(response1, response1);

        HttpJsonResponse<TextMessage> response1Clone = new HttpJsonResponse<>(200, TextMessage.OK);
        shouldBeEqual(response1, response1Clone);

        shouldNotBeEqual(null, response1);

        shouldBeEqual(
                new HttpJsonResponse<>(200, TextMessage.OK),
                new HttpJsonResponse<>(200, TextMessage.OK)
        );
        shouldBeEqual(
                new HttpJsonResponse<>(500, "blah"),
                new HttpJsonResponse<>(500, "blah")
        );

        shouldNotBeEqual(
                new HttpJsonResponse<>(500, "foo"),
                new HttpJsonResponse<>(500, "bar")
        );
        shouldNotBeEqual(
                new HttpJsonResponse<>(501, "blah"),
                new HttpJsonResponse<>(500, "blah")
        );

        shouldBeEqual(
                new HttpJsonResponse<>(200, null),
                new HttpJsonResponse<>(200, null)
        );

        shouldNotBeEqual(
                new HttpJsonResponse<>(200, TextMessage.OK),
                new HttpJsonResponse<>(200, null)
        );
        shouldNotBeEqual(
                new HttpJsonResponse<>(200, null),
                new HttpJsonResponse<>(200, TextMessage.OK)
        );
    }

    @Test
    public void CreatesDecentStringRepresentations(){
        assertEquals(
                "HttpJsonResponse(400) TextMessage:sample",
                HttpJsonResponse.BadRequestWithMessage("sample").toString()
        );

        assertEquals(
                "HttpJsonResponse(100) null",
                new HttpJsonResponse<String>(100, null).toString()
        );

        assertEquals(
                "HttpJsonResponse(100) 234",
                new HttpJsonResponse<Integer>(100, 234).toString()
        );
    }

    private void shouldBeEqual(HttpJsonResponse r1, HttpJsonResponse r2){
        assertEquals(r1, r2);
        assertEquals(r2, r1);

        if (r1 != null && r2 != null){
            assertEquals(r1.hashCode(), r2.hashCode());
            assertEquals(r2.hashCode(), r1.hashCode());
        }
    }

    private void shouldNotBeEqual(HttpJsonResponse r1, HttpJsonResponse r2){
        assertNotEquals(r1, r2);
        assertNotEquals(r2, r1);

        if (r1 != null && r2 != null) {
            assertNotEquals(r1.hashCode(), r2.hashCode());
            assertNotEquals(r2.hashCode(), r1.hashCode());
        }
    }
}
