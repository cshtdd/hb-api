package com.tddapps.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import static com.tddapps.utils.StringExtensions.*;

@Data
public class HttpJsonActionStub implements HttpJsonAction<String, String>{
    private ActionBodyParseException seededParseException = null;
    private String seededParsedBody = null;
    private ActionProcessException seededProcessException = null;
    private int seededStatusCode = -1;
    private String seededResultBody = null;

    @Override
    public String parse(JsonNode body) throws ActionBodyParseException {
        if (seededParseException != null){
            throw seededParseException;
        }

        return seededParsedBody;
    }

    @Override
    public HttpJsonResponse<String> process(String body) throws ActionProcessException {
        if (seededProcessException != null){
            throw seededProcessException;
        }

        if (!EmptyWhenNull(seededParsedBody).equals(EmptyWhenNull(body))){
            throw new RuntimeException(String.format(
                    "Received unexpected process call. Expected: \"%s\", Actual: \"%s\"",
                    seededParsedBody,
                    body
            ));
        }

        return new HttpJsonResponse<>(seededStatusCode, seededResultBody);
    }
}
