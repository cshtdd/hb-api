package com.tddapps.actions;

import com.tddapps.actions.response.TextMessage;
import com.tddapps.controllers.ActionProcessException;
import com.tddapps.controllers.HttpJsonResponse;
import com.tddapps.controllers.HttpSupplierAction;
import com.tddapps.dal.HeartBeatRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StatusGetAction implements HttpSupplierAction<TextMessage> {
    private static final Logger LOG = LogManager.getLogger(StatusGetAction.class);
    private final HeartBeatRepository heartBeatRepository;

    public StatusGetAction(HeartBeatRepository heartBeatRepository) {
        this.heartBeatRepository = heartBeatRepository;
    }

    @Override
    public HttpJsonResponse<TextMessage> process() throws ActionProcessException {
        return HttpJsonResponse.Success(TextMessage.OK);
    }
}
