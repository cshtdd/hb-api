package com.tddapps.actions;

import com.fasterxml.jackson.databind.JsonNode;
import com.tddapps.actions.response.TextMessage;
import com.tddapps.controllers.ActionBodyParseException;
import com.tddapps.controllers.ActionProcessException;
import com.tddapps.controllers.HttpJsonAction;
import com.tddapps.controllers.HttpJsonResponse;
import com.tddapps.model.DalException;
import com.tddapps.model.HeartBeatRepository;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import static com.tddapps.utils.JsonNodeHelper.readInt;
import static com.tddapps.utils.JsonNodeHelper.readString;

@Log4j2
public class HeartBeatPostAction implements HttpJsonAction<HeartBeatPostActionInput, TextMessage> {
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
        val result = readString(body, "hostId");

        if (!StringUtils.isAlphanumeric(result)){
            throw new ActionBodyParseException("Invalid hostId");
        }

        if (result.length() > 100){
            throw new ActionBodyParseException("Invalid hostId");
        }

        return result;
    }

    private int readIntervalMs(JsonNode body) throws ActionBodyParseException {
        val result = readInt(body, "intervalMs", HeartBeatPostActionInput.DEFAULT_INTERVAL_MS);

        if (result < HeartBeatPostActionInput.MIN_INTERVAL_MS ||
                result > HeartBeatPostActionInput.MAX_INTERVAL_MS){
            throw new ActionBodyParseException("Invalid intervalMs");
        }

        return result;
    }

    @Override
    public HttpJsonResponse<TextMessage> process(HeartBeatPostActionInput body) throws ActionProcessException {
        log.info(String.format("hostId: %s", body.getHostId()));

        try {
            heartBeatRepository.Save(body.toHeartBeat());
        } catch (DalException e) {
            throw new ActionProcessException(e.getMessage());
        }

        return new HttpJsonResponse<>(200, TextMessage.OK);
    }
}
