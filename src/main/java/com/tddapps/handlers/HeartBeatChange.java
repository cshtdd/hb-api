package com.tddapps.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.tddapps.ioc.IocContainer;
import com.tddapps.model.NotificationSender;
import lombok.extern.log4j.Log4j2;
import lombok.val;

@Log4j2
@SuppressWarnings("unused")
public class HeartBeatChange implements RequestHandler<DynamodbEvent, Boolean> {
    private final NotificationSender notificationSender;

    public HeartBeatChange(){
        this(IocContainer.getInstance().Resolve(NotificationSender.class));
    }

    public HeartBeatChange(NotificationSender notificationSender) {
        this.notificationSender = notificationSender;
    }

    @Override
    public Boolean handleRequest(DynamodbEvent input, Context context) {

        for (val r : input.getRecords()) {
            log.info(String.format("EventName: %s, Key: %s", r.getEventName(), r.getDynamodb().getKeys().get("host_id")));
        }

        return true;
    }
}
