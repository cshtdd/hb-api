package com.tddapps.handlers;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.Record;
import com.amazonaws.services.dynamodbv2.model.StreamRecord;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.tddapps.ioc.IocContainer;
import com.tddapps.model.*;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import lombok.var;

import java.util.Map;

@Log4j2
@SuppressWarnings("unused")
public class HeartBeatChange implements RequestHandler<DynamodbEvent, Boolean> {
    private static final String FALSE_NUMERIC_STRING = "0";
    private final HeartBeatNotificationBuilder notificationBuilder;
    private final NotificationSender notificationSender;
    private final DynamoDBMapper mapper;
    private final SettingsReader settingsReader;

    public HeartBeatChange(){
        this(
                IocContainer.getInstance().Resolve(HeartBeatNotificationBuilder.class),
                IocContainer.getInstance().Resolve(NotificationSender.class),
                IocContainer.getInstance().Resolve(DynamoDBMapper.class),
                IocContainer.getInstance().Resolve(SettingsReader.class)
        );
    }

    public HeartBeatChange(
            HeartBeatNotificationBuilder notificationBuilder,
            NotificationSender notificationSender,
            DynamoDBMapper mapper,
            SettingsReader settingsReader) {
        this.notificationBuilder = notificationBuilder;
        this.notificationSender = notificationSender;
        this.mapper = mapper;
        this.settingsReader = settingsReader;
    }

    @Override
    public Boolean handleRequest(DynamodbEvent input, Context context) {
        log.debug("HeartBeat Change");

        val heartBeats = readDeletedHeartBeats(input);
        logHeartBeats(heartBeats);
        val notifications = notificationBuilder.build(heartBeats);
        val result = sendNotifications(notifications);

        log.info(String.format("HeartBeat Change Completed; Result: %s", result));

        return result;
    }

    private void logHeartBeats(HeartBeat[] heartBeats) {
        for (val hb : heartBeats){
            log.info(String.format("Host missing; %s", hb.toString()));
        }
    }

    private Boolean sendNotifications(Notification[] notifications) {
        var result = true;

        for (val n : notifications){
            try {
                notificationSender.Send(n);
            } catch (DalException e) {
                log.error("Action processing failed", e);
                result = false;
            }
        }

        return result;
    }

    private HeartBeat[] readDeletedHeartBeats(DynamodbEvent input) {
        return input.getRecords()
                .stream()
                .filter(HeartBeatChange::isRecordDeletion)
                .map(Record::getDynamodb)
                .map(StreamRecord::getOldImage)
                .map(this::buildHeartBeat)
                .filter(HeartBeat::isNotTest)
                .filter(this::heartBeatLastUpdatedInCurrentRegion)
                .toArray(HeartBeat[]::new);
    }

    private HeartBeat buildHeartBeat(Map<String, AttributeValue> map) {
        return mapper.marshallIntoObject(HeartBeat.class, map);
    }

    private static boolean isRecordDeletion(DynamodbEvent.DynamodbStreamRecord record){
        return record.getEventName().equals("REMOVE");
    }

    private boolean heartBeatLastUpdatedInCurrentRegion(HeartBeat hb){
        return ReadRegion().equals(hb.getRegion());
    }

    private String ReadRegion() {
        return settingsReader.ReadString(Settings.AWS_REGION);
    }
}
