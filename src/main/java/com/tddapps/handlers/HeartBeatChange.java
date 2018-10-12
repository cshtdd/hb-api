package com.tddapps.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.tddapps.ioc.IocContainer;
import com.tddapps.model.*;
import com.tddapps.model.aws.DynamoDBEventParser;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import lombok.var;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@SuppressWarnings("unused")
public class HeartBeatChange implements RequestHandler<DynamodbEvent, Boolean> {
    private static final String FALSE_NUMERIC_STRING = "0";
    private final HeartBeatNotificationBuilder notificationBuilder;
    private final NotificationSender notificationSender;
    private final RequestHandlerHelper requestHandlerHelper;
    private final DynamoDBEventParser eventParser;

    public HeartBeatChange(){
        this(
                IocContainer.getInstance().Resolve(HeartBeatNotificationBuilder.class),
                IocContainer.getInstance().Resolve(NotificationSender.class),
                IocContainer.getInstance().Resolve(RequestHandlerHelper.class),
                IocContainer.getInstance().Resolve(DynamoDBEventParser.class)
        );
    }

    public HeartBeatChange(
            HeartBeatNotificationBuilder notificationBuilder,
            NotificationSender notificationSender,
            RequestHandlerHelper requestHandlerHelper,
            DynamoDBEventParser eventParser) {
        this.notificationBuilder = notificationBuilder;
        this.notificationSender = notificationSender;
        this.requestHandlerHelper = requestHandlerHelper;
        this.eventParser = eventParser;
    }

    @Override
    public Boolean handleRequest(DynamodbEvent input, Context context) {
        log.debug("HeartBeat Change");

        val deletedHeartBeats = eventParser.readDeletions(input, HeartBeat.class);
        val insertedHeartBeats = eventParser.readInsertions(input, HeartBeat.class);

        val events = new ArrayList<HeartBeatChangeEvent>(){{
            addAll(buildEvents("Hosts missing", deletedHeartBeats));
            addAll(buildEvents("Hosts registered", insertedHeartBeats));
        }}.toArray(new HeartBeatChangeEvent[0]);

        val notifications = notificationBuilder.build(events);
        val result = sendNotifications(notifications);

        log.info(String.format("HeartBeat Change Completed; Result: %s", result));

        return result;
    }

    private List<HeartBeatChangeEvent> buildEvents(String type, List<HeartBeat> heartBeats) {
        val eventHeartBeats = requestHandlerHelper.filter(heartBeats.toArray(new HeartBeat[0]));

        return Arrays.stream(eventHeartBeats)
                .map(hb -> new HeartBeatChangeEvent(type, hb))
                .collect(Collectors.toList());
    }

    private Boolean sendNotifications(Notification[] notifications) {
        var result = true;

        for (val n : notifications){
            try {
                notificationSender.Send(n);
            } catch (DalException e) {
                log.error("Send notification failed", e);
                result = false;
            }
        }

        return result;
    }
}
