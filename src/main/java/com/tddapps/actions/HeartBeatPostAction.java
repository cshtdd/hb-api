package com.tddapps.actions;

import com.fasterxml.jackson.databind.JsonNode;
import com.tddapps.controllers.BodyParseException;
import com.tddapps.controllers.BodyProcessException;
import com.tddapps.controllers.HttpJsonAction;
import com.tddapps.controllers.HttpJsonResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class HeartBeatPostAction implements HttpJsonAction<HeartBeatPostActionInput, String> {
    private static final Logger LOG = LogManager.getLogger(HeartBeatPostAction .class);

    @Override
    public HeartBeatPostActionInput parse(JsonNode body) throws BodyParseException {
        JsonNode hostIdNode = body.get("hostId");

        String hostId = "";

        if (hostIdNode != null){
            hostId = hostIdNode.asText();
        }

        if (hostId == null ||
            hostId.isEmpty() ||
            hostId.trim().isEmpty()){
            throw new BodyParseException("Invalid hostId");
        }

        return new HeartBeatPostActionInput(hostId);
    }

    @Override
    public HttpJsonResponse<String> process(HeartBeatPostActionInput body) throws BodyProcessException {
        LOG.info(String.format("hostId: %s", body.getHostId()));

        return new HttpJsonResponse<>(200, "OK");
    }
}
