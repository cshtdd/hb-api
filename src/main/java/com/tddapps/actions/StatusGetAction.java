package com.tddapps.actions;

import com.tddapps.actions.response.TextMessage;
import com.tddapps.controllers.ActionProcessException;
import com.tddapps.controllers.HttpJsonResponse;
import com.tddapps.controllers.HttpSupplierAction;
import com.tddapps.dal.DalException;
import com.tddapps.dal.HeartBeat;
import com.tddapps.dal.HeartBeatRepository;
import com.tddapps.infrastructure.KeysCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.tddapps.utils.DateExtensions.UtcNowPlusMs;

public class StatusGetAction implements HttpSupplierAction<TextMessage> {
    private static final Logger LOG = LogManager.getLogger(StatusGetAction.class);

    private final HeartBeatRepository heartBeatRepository;
    private final KeysCache cache;

    public StatusGetAction(HeartBeatRepository heartBeatRepository, KeysCache cache) {
        this.heartBeatRepository = heartBeatRepository;
        this.cache = cache;
    }

    @Override
    public HttpJsonResponse<TextMessage> process() throws ActionProcessException {
        if (ShouldReturnCachedResponse()){
            return getCachedResponse();
        }

        LOG.info("Cache miss");

        VerifyDatabase();

        CacheResponse();
        return getCachedResponse();
    }

    private void VerifyDatabase() throws ActionProcessException {
        HeartBeat hb = new HeartBeat(
                getClass().getSimpleName(),
                UtcNowPlusMs(4*60*60*1000),
                true
        );

        try {
            heartBeatRepository.Save(hb);
        } catch (DalException e) {
            throw new ActionProcessException(e.getMessage());
        }
    }

    private HttpJsonResponse getCachedResponse() {
        return HttpJsonResponse.Success(TextMessage.OK);
    }

    private void CacheResponse() {
        cache.Add(getCacheKey());
    }

    private boolean ShouldReturnCachedResponse() {
        return cache.Contains(getCacheKey());
    }

    private String getCacheKey() {
        return getClass().getName();
    }
}
