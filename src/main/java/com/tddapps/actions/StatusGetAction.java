package com.tddapps.actions;

import com.tddapps.actions.response.TextMessage;
import com.tddapps.controllers.ActionProcessException;
import com.tddapps.controllers.HttpJsonResponse;
import com.tddapps.controllers.HttpSupplierAction;
import com.tddapps.dal.DalException;
import com.tddapps.dal.HeartBeat;
import com.tddapps.dal.HeartBeatRepository;

import static com.tddapps.utils.DateExtensions.UtcNowPlusMs;

public class StatusGetAction implements HttpSupplierAction<TextMessage> {
    private final HeartBeatRepository heartBeatRepository;

    public StatusGetAction(HeartBeatRepository heartBeatRepository) {
        this.heartBeatRepository = heartBeatRepository;
    }

    @Override
    public HttpJsonResponse<TextMessage> process() throws ActionProcessException {
        HeartBeat hb = new HeartBeat(getClass().getSimpleName(), UtcNowPlusMs(4*60*60*1000), true);

        try {
            heartBeatRepository.Save(hb);
        } catch (DalException e) {
            throw new ActionProcessException(e.getMessage());
        }

        return HttpJsonResponse.Success(TextMessage.OK);
    }
}
