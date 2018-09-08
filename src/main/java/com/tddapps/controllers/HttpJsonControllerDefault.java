package com.tddapps.controllers;

import com.tddapps.utils.JsonNodeHelper;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.io.IOException;
import java.util.Map;

@Log4j2
public class HttpJsonControllerDefault implements HttpJsonController {
    private final HttpJsonAction action;

    public HttpJsonAction getAction() {
        return action;
    }

    public HttpJsonControllerDefault(HttpJsonAction action) {
        this.action = action;
    }

    @Override
    public HttpJsonResponse process(Map<String, Object> input){
        try {
            val requestBody = readBodyFrom(input);
            log.debug(String.format("Body: %s", requestBody));

            if (requestBody.trim().isEmpty()){
                return HttpJsonResponse.BadRequestWithMessage("Empty Request Body");
            }

            val jsonBody = JsonNodeHelper.parse(requestBody);
            val parsedBody = action.parse(jsonBody);
            return action.process(parsedBody);

        } catch (IOException e) {
            log.warn("Invalid json in request body", e);
            return HttpJsonResponse.BadRequestWithMessage("Invalid json in request body");
        } catch (ActionBodyParseException e) {
            log.warn("Action parsing failed", e);
            return HttpJsonResponse.BadRequestWithMessage(e.getMessage());
        } catch (ActionProcessException e) {
            log.error("Action processing failed", e);
            return HttpJsonResponse.ServerErrorWithMessage(e.getMessage());
        }
    }

    private String readBodyFrom(Map<String, Object> input){
        val bodyObject = input.getOrDefault("body", "");

        if (bodyObject == null){
            return "";
        }

        return bodyObject.toString();
    }
}

