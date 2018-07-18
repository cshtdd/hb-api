package com.tddapps.actions;

import com.fasterxml.jackson.databind.JsonNode;
import com.tddapps.actions.response.TextMessage;
import com.tddapps.controllers.ActionBodyParseException;
import com.tddapps.controllers.ActionProcessException;
import com.tddapps.controllers.HttpJsonAction;
import com.tddapps.controllers.HttpJsonResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.tddapps.utils.StringExtensions.EmptyWhenNull;

public class HeartBeatPostAction implements HttpJsonAction<HeartBeatPostActionInput, TextMessage> {
    private static final Logger LOG = LogManager.getLogger(HeartBeatPostAction.class);

    @Override
    public HeartBeatPostActionInput parse(JsonNode body) throws ActionBodyParseException {
        String hostId = readHostId(body);
        int intervalMs = readIntervalMs(body);

        if (intervalMs < HeartBeatPostActionInput.MIN_INTERVAL_MS ||
                intervalMs > HeartBeatPostActionInput.MAX_INTERVAL_MS){
            throw new ActionBodyParseException("Invalid intervalMs");
        }

        if (!StringUtils.isAlphanumeric(hostId)){
            throw new ActionBodyParseException("Invalid hostId");
        }

        if (hostId.length() > 100){
            throw new ActionBodyParseException("Invalid hostId");
        }

        return new HeartBeatPostActionInput(hostId, intervalMs);
    }

    private static String readHostId(JsonNode body){
        JsonNode value = body.get("hostId");

        if (value == null){
            return "";
        }

        return EmptyWhenNull(value.asText());
    }

    private static int readIntervalMs(JsonNode body){
        JsonNode value = body.get("intervalMs");

        if (value == null){
            return HeartBeatPostActionInput.DEFAULT_INTERVAL_MS;
        }

        return value.asInt();
    }

    @Override
    public HttpJsonResponse<TextMessage> process(HeartBeatPostActionInput body) throws ActionProcessException {
        LOG.info(String.format("hostId: %s", body.getHostId()));

        return new HttpJsonResponse<>(200, TextMessage.OK);
    }
}
