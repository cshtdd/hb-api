package com.tddapps.actions;

import com.fasterxml.jackson.databind.JsonNode;
import com.tddapps.controllers.BodyParseException;
import com.tddapps.controllers.BodyProcessException;
import com.tddapps.controllers.HttpJsonAction;
import com.tddapps.controllers.HttpJsonResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.tddapps.utils.StringExtensions.EmptyWhenNull;


public class HeartBeatPostAction implements HttpJsonAction<HeartBeatPostActionInput, String> {
    private static final Logger LOG = LogManager.getLogger(HeartBeatPostAction .class);

    @Override
    public HeartBeatPostActionInput parse(JsonNode body) throws BodyParseException {
        String hostId = readHostId(body);

        if (hostId.trim().isEmpty()){
            throw new BodyParseException("Invalid hostId");
        }

        if (!StringUtils.isAlphanumeric(hostId)){
            throw new BodyParseException("Invalid hostId");
        }

        return new HeartBeatPostActionInput(hostId);
    }

    private static String readHostId(JsonNode body){
        JsonNode hostIdNode = body.get("hostId");

        if (hostIdNode == null){
            return "";
        }

        return EmptyWhenNull(hostIdNode.asText());
    }

    @Override
    public HttpJsonResponse<String> process(HeartBeatPostActionInput body) throws BodyProcessException {
        LOG.info(String.format("hostId: %s", body.getHostId()));

        return new HttpJsonResponse<>(200, "OK");
    }
}
