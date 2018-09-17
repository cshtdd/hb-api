package com.tddapps.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import lombok.extern.log4j.Log4j2;
import lombok.val;

@Log4j2
@SuppressWarnings("unused")
public class HeartBeatChange implements RequestHandler<DynamodbEvent, Boolean> {
    @Override
    public Boolean handleRequest(DynamodbEvent input, Context context) {

        for (val r : input.getRecords()) {
            log.info(String.format("EventName: %s, Key: %s", r.getEventName(), r.getDynamodb().getKeys().get("host_id")));
        }

        return true;
    }
}
