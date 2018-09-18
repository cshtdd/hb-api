package com.tddapps.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.tddapps.ioc.IocContainer;
import com.tddapps.model.*;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.util.Arrays;

import static com.tddapps.utils.DateExtensions.UtcNowPlusMs;

@SuppressWarnings("unused")
@Log4j2
public class NotificationCalculator implements RequestHandler<Boolean, Boolean> {
    private final HeartBeatRepository heartBeatRepository;
    private final HeartBeatNotificationBuilder notificationBuilder;
    private final NotificationSender notificationSender;

    public NotificationCalculator(){
        this(
                IocContainer.getInstance().Resolve(HeartBeatRepository.class),
                IocContainer.getInstance().Resolve(HeartBeatNotificationBuilder.class),
                IocContainer.getInstance().Resolve(NotificationSender.class));
    }

    public NotificationCalculator(HeartBeatRepository heartBeatRepository,
                                  HeartBeatNotificationBuilder notificationBuilder,
                                  NotificationSender notificationSender){
        this.heartBeatRepository = heartBeatRepository;
        this.notificationBuilder = notificationBuilder;
        this.notificationSender = notificationSender;
    }

    @Override
    public Boolean handleRequest(Boolean input, Context context) {
        try {
            log.info("Calculating notifications");

            val expiredHeartBeats = readExpiredHeartBeats();
            logExpiredHeartBeats(expiredHeartBeats);
            sendNotifications(expiredHeartBeats);
            updateExpiredHeartBeats(expiredHeartBeats);

            log.info("Calculating notifications Completed");

            return true;
        } catch (DalException e) {
            log.warn("Calculating notifications failed", e);
            return false;
        }
    }

    private HeartBeat[] readExpiredHeartBeats() throws DalException {
        return Arrays.stream(readHeartBeats())
                .filter(HeartBeat::isNotTest)
                .filter(HeartBeat::isExpired)
                .toArray(HeartBeat[]::new);
    }

    private void sendNotifications(HeartBeat[] expiredHeartBeats) throws DalException {
        val notifications = notificationBuilder.build(expiredHeartBeats);
        for (val notification : notifications) {
            notificationSender.Send(notification);
        }
    }

    private void updateExpiredHeartBeats(HeartBeat[] expiredHeartBeats) throws DalException {
        val updatedDate = UtcNowPlusMs(24*60*60*1000);

        val updatedHeartbeats = Arrays.stream(expiredHeartBeats)
                .map(hb -> hb.clone(updatedDate))
                .toArray(HeartBeat[]::new);

        heartBeatRepository.Save(updatedHeartbeats);
    }

    private void logExpiredHeartBeats(HeartBeat[] expiredHeartBeats) {
        for (val hb : expiredHeartBeats){
            log.info(String.format("Host missing; %s", hb.toString()));
        }
    }

    private HeartBeat[] readHeartBeats() throws DalException {
        val result = heartBeatRepository.All();

        if (result == null){
            return new HeartBeat[]{};
        }

        return result;
    }
}
