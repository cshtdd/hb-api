package com.tddapps.handlers;

import com.amazonaws.services.dynamodbv2.model.Record;
import com.amazonaws.services.dynamodbv2.model.StreamRecord;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.tddapps.ioc.IocContainer;
import com.tddapps.model.DalException;
import com.tddapps.model.Notification;
import com.tddapps.model.NotificationSender;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.util.Arrays;

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
        val deletedHostIds = input.getRecords()
                .stream()
                .filter(r -> r.getEventName().equals("REMOVE"))
                .map(Record::getDynamodb)
                .map(StreamRecord::getKeys)
                .map(k -> k.get("host_id").getS())
                .toArray(String[]::new);

        val notifications = Arrays.stream(deletedHostIds)
                .map(HeartBeatChange::buildNotification)
                .toArray(Notification[]::new);

        for (val n : notifications){
            try {
                notificationSender.Send(n);
            } catch (DalException e) {
                //TODO handle this better
            }
        }

        return true;
    }

    private static Notification buildNotification(String hostId){
        val subject = String.format("Host missing [%s]", hostId);
        return new Notification(subject, subject);
    }
}
