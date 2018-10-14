package com.tddapps.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.tddapps.ioc.IocContainer;
import com.tddapps.model.*;
import com.tddapps.model.aws.DynamoDBEventParser;
import com.tddapps.model.heartbeats.HeartBeat;
import com.tddapps.model.heartbeats.HeartBeatChangeEvent;
import com.tddapps.model.heartbeats.RequestHandlerHelper;
import com.tddapps.model.notifications.HeartBeatChangeEventNotificationBuilder;
import com.tddapps.model.notifications.Notification;
import com.tddapps.model.notifications.NotificationSender;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import lombok.var;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.tddapps.utils.CollectionExtensions.Difference;
import static com.tddapps.utils.CollectionExtensions.Intersection;

@Log4j2
@SuppressWarnings("unused")
public class HeartBeatChange implements RequestHandler<DynamodbEvent, Boolean> {
    private static final String FALSE_NUMERIC_STRING = "0";
    private final HeartBeatChangeEventNotificationBuilder notificationBuilder;
    private final NotificationSender notificationSender;
    private final RequestHandlerHelper requestHandlerHelper;
    private final DynamoDBEventParser eventParser;

    public HeartBeatChange(){
        this(
                IocContainer.getInstance().Resolve(HeartBeatChangeEventNotificationBuilder.class),
                IocContainer.getInstance().Resolve(NotificationSender.class),
                IocContainer.getInstance().Resolve(RequestHandlerHelper.class),
                IocContainer.getInstance().Resolve(DynamoDBEventParser.class)
        );
    }

    public HeartBeatChange(
            HeartBeatChangeEventNotificationBuilder notificationBuilder,
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

        val allDeletedHeartBeats = readDeletedHeartBeats(input);
        val allInsertedHeartBeats = readInsertedHeartBeats(input);
        val intersection = Intersection(allDeletedHeartBeats, allInsertedHeartBeats);
        logHeartBeatsThatFlipped(intersection);

        val deletedHeartBeats = Difference(allDeletedHeartBeats, intersection);
        val insertedHeartBeats = Difference(allInsertedHeartBeats, intersection);

        val events = new ArrayList<HeartBeatChangeEvent>(){{
            addAll(buildEvents("Hosts missing", deletedHeartBeats));
            addAll(buildEvents("Hosts registered", insertedHeartBeats));
        }}.toArray(new HeartBeatChangeEvent[0]);
        logEvents(events);

        val notifications = notificationBuilder.build(events);
        val result = sendNotifications(notifications);

        log.info(String.format("HeartBeat Change Completed; Result: %s", result));

        return result;
    }

    private List<HeartBeat> readDeletedHeartBeats(DynamodbEvent input){
        return filter("Deletions", () -> eventParser.readDeletions(input, HeartBeat.class));
    }

    private List<HeartBeat> readInsertedHeartBeats(DynamodbEvent input){
        return filter("Insertions", () -> eventParser.readInsertions(input, HeartBeat.class));
    }

    private List<HeartBeat> filter(String listName, Supplier<List<HeartBeat>> fn){
        val all = fn.get().toArray(new HeartBeat[0]);

        val result = Arrays.stream(requestHandlerHelper.filter(all))
                .collect(Collectors.toList());

        logMismatch(listName, all, result.toArray(new HeartBeat[0]));

        return result;
    }

    private void logEvents(HeartBeatChangeEvent[] events) {
        for (val e : events){
            log.info(String.format("Host Change; %s", e.toString()));
        }
    }

    private void logMismatch(String listName, HeartBeat[] allHeartBeats, HeartBeat[] subsetCount) {
        log.info(String.format("%sHeartBeatCount: %d; ResultCount: %d;",
                listName, allHeartBeats.length, subsetCount.length));
    }

    private static void logHeartBeatsThatFlipped(List<HeartBeat> heartBeats){
        for (val hb : heartBeats){
            log.info(String.format("Flipped Host; %s", hb.toString()));
        }
    }

    private List<HeartBeatChangeEvent> buildEvents(String type, List<HeartBeat> heartBeats) {
        return heartBeats
                .stream()
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
