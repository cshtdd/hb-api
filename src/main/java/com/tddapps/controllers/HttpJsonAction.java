package com.tddapps.controllers;

import com.fasterxml.jackson.databind.JsonNode;

public interface HttpJsonAction<Request, Response> {
    Request parse(JsonNode body) throws BodyParseException;
    HttpJsonResponse<Response> process(Request body);
}

