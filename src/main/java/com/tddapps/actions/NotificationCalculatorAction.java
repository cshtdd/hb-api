package com.tddapps.actions;

import com.tddapps.actions.response.TextMessage;
import com.tddapps.controllers.ActionProcessException;
import com.tddapps.controllers.HttpJsonResponse;
import com.tddapps.controllers.HttpSupplierAction;
import com.tddapps.model.*;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Date;

import static com.tddapps.utils.DateExtensions.UtcNowPlusMs;

public class NotificationCalculatorAction implements HttpSupplierAction<TextMessage> {
    private static final Logger LOG = LogManager.getLogger(NotificationCalculatorAction.class);
    private final HeartBeatRepository heartBeatRepository;
    private final HeartBeatNotificationBuilder notificationBuilder;
    private final NotificationSender notificationSender;

    public NotificationCalculatorAction(
            HeartBeatRepository heartBeatRepository,
            HeartBeatNotificationBuilder notificationBuilder,
            NotificationSender notificationSender) {
        this.heartBeatRepository = heartBeatRepository;
        this.notificationBuilder = notificationBuilder;
        this.notificationSender = notificationSender;
    }

    @Override
    public HttpJsonResponse<TextMessage> process() throws ActionProcessException {
        LOG.info("calculating notifications");

        try {
            HeartBeat[] expiredHeartBeats = readExpiredHeartBeats();
            logExpiredHeartBeats(expiredHeartBeats);
            sendNotifications(expiredHeartBeats);
            updateExpiredHeartBeats(expiredHeartBeats);
        } catch (DalException e) {
            throw new ActionProcessException(e.getMessage());
        }

        return HttpJsonResponse.Success(TextMessage.OK);
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

        for (val hb : updatedHeartbeats) {
            heartBeatRepository.Save(hb);
        }
    }

    private HeartBeat[] readExpiredHeartBeats() throws DalException {
        return Arrays.stream(readHeartBeats())
                .filter(HeartBeat::isNotTest)
                .filter(HeartBeat::isExpired)
                .toArray(HeartBeat[]::new);
    }

    private void logExpiredHeartBeats(HeartBeat[] expiredHeartBeats) {
        for (val hb : expiredHeartBeats){
            LOG.info(String.format("Host missing; %s", hb.toString()));
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
