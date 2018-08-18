package com.tddapps.actions;

import com.tddapps.actions.response.TextMessage;
import com.tddapps.controllers.ActionProcessException;
import com.tddapps.controllers.HttpJsonResponse;
import com.tddapps.controllers.HttpSupplierAction;
import com.tddapps.dal.DalException;
import com.tddapps.dal.HeartBeat;
import com.tddapps.dal.HeartBeatRepository;
import com.tddapps.dal.NotificationSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.stream.Collectors;

public class NotificationCalculatorAction implements HttpSupplierAction<TextMessage> {
    private static final Logger LOG = LogManager.getLogger(NotificationCalculatorAction.class);
    private final HeartBeatRepository heartBeatRepository;
    private final NotificationSender notificationSender;

    public NotificationCalculatorAction(HeartBeatRepository heartBeatRepository, NotificationSender notificationSender) {
        this.heartBeatRepository = heartBeatRepository;
        this.notificationSender = notificationSender;
    }

    @Override
    public HttpJsonResponse<TextMessage> process() throws ActionProcessException {
        LOG.info("calculating notifications");

        try {
            HeartBeat[] expiredHeartBeats = Arrays.stream(readHeartBeats())
                    .filter(HeartBeat::isNotTest)
                    .filter(HeartBeat::isExpired)
                    .toArray(HeartBeat[]::new);

            for (HeartBeat hb : expiredHeartBeats){
                LOG.info(String.format("Host missing; %s", hb.toString()));
            }

            String[] expiredHostIds = Arrays.stream(expiredHeartBeats)
                    .map(HeartBeat::getHostId)
                    .toArray(String[]::new);

            String concatenatedHostsIds = String.join(", ", expiredHostIds);
            String notificationSubject = String.format("Hosts missing [%s]", concatenatedHostsIds);


            String[] heartBeatsDescriptionArray = Arrays.stream(expiredHeartBeats)
                    .map(HeartBeat::toString)
                    .toArray(String[]::new);
            String heartBeatsDescriptions = String.join("\n", heartBeatsDescriptionArray);
            String notificationBody = String.format("%s\n\n%s\n--", notificationSubject, heartBeatsDescriptions);

            notificationSender.Send(notificationBody , notificationSubject);
        } catch (DalException e) {
            throw new ActionProcessException(e.getMessage());
        }

        return HttpJsonResponse.Success(TextMessage.OK);
    }

    private HeartBeat[] readHeartBeats() throws DalException {
        HeartBeat[] result = heartBeatRepository.All();

        if (result == null){
            return new HeartBeat[]{};
        }

        return result;
    }
}
