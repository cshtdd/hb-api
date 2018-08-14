package com.tddapps.actions;

import com.fasterxml.jackson.databind.JsonNode;
import com.tddapps.actions.response.TextMessage;
import com.tddapps.controllers.ActionBodyParseException;
import com.tddapps.controllers.ActionProcessException;
import com.tddapps.controllers.HttpJsonAction;
import com.tddapps.controllers.HttpJsonResponse;
import com.tddapps.dal.HeartBeatRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.tddapps.utils.JsonNodeHelper.readInt;
import static com.tddapps.utils.JsonNodeHelper.readString;

public class HeartBeatPostAction implements HttpJsonAction<HeartBeatPostActionInput, TextMessage> {
    private static final Logger LOG = LogManager.getLogger(HeartBeatPostAction.class);
    private final HeartBeatRepository heartBeatRepository;

    public HeartBeatPostAction(HeartBeatRepository heartBeatRepository){
        this.heartBeatRepository = heartBeatRepository;
    }

    @Override
    public HeartBeatPostActionInput parse(JsonNode body) throws ActionBodyParseException {
        return new HeartBeatPostActionInput(
                readHostId(body),
                readIntervalMs(body)
        );
    }

    private String readHostId(JsonNode body) throws ActionBodyParseException {
        String result = readString(body, "hostId");

        if (!StringUtils.isAlphanumeric(result)){
            throw new ActionBodyParseException("Invalid hostId");
        }

        if (result.length() > 100){
            throw new ActionBodyParseException("Invalid hostId");
        }

        return result;
    }

    private int readIntervalMs(JsonNode body) throws ActionBodyParseException {
        int result = readInt(body, "intervalMs", HeartBeatPostActionInput.DEFAULT_INTERVAL_MS);

        if (result < HeartBeatPostActionInput.MIN_INTERVAL_MS ||
                result > HeartBeatPostActionInput.MAX_INTERVAL_MS){
            throw new ActionBodyParseException("Invalid intervalMs");
        }

        return result;
    }

    @Override
    public HttpJsonResponse<TextMessage> process(HeartBeatPostActionInput body) throws ActionProcessException {
        LOG.info(String.format("hostId: %s", body.getHostId()));

        heartBeatRepository.Save(body.toHeartBeat());

        return new HttpJsonResponse<>(200, TextMessage.OK);
    }
}
