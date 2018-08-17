package com.tddapps.actions;

import com.tddapps.actions.response.TextMessage;
import com.tddapps.controllers.ActionProcessException;
import com.tddapps.controllers.HttpJsonResponse;
import com.tddapps.controllers.HttpSupplierAction;
import com.tddapps.dal.DalException;
import com.tddapps.dal.HeartBeat;
import com.tddapps.dal.HeartBeatRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class NotificationCalculatorAction implements HttpSupplierAction<TextMessage> {
    private static final Logger LOG = LogManager.getLogger(NotificationCalculatorAction.class);
    private final HeartBeatRepository heartBeatRepository;

    public NotificationCalculatorAction(HeartBeatRepository heartBeatRepository) {
        this.heartBeatRepository = heartBeatRepository;
    }

    @Override
    public HttpJsonResponse<TextMessage> process() throws ActionProcessException {
        LOG.info("calculating notifications");

        try {
            HeartBeat[] heartBeats = heartBeatRepository.All();

            if (heartBeats != null) {
                Arrays.stream(heartBeats).forEach(hb -> LOG.info(hb.toString()));
            }
        } catch (DalException e) {
            throw new ActionProcessException(e.getMessage());
        }

        return HttpJsonResponse.Success(TextMessage.OK);
    }
}
