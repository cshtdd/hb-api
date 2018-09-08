package com.tddapps.actions;

import com.tddapps.actions.response.TextMessage;
import com.tddapps.controllers.ActionProcessException;
import com.tddapps.controllers.HttpJsonResponse;
import com.tddapps.controllers.HttpSupplierAction;
import com.tddapps.model.DalException;
import com.tddapps.model.HeartBeat;
import com.tddapps.model.HeartBeatRepository;
import com.tddapps.infrastructure.KeysCache;
import com.tddapps.model.NotificationSenderStatus;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.tddapps.utils.DateExtensions.UtcNowPlusMs;

public class StatusGetAction implements HttpSupplierAction<TextMessage> {
    private static final Logger LOG = LogManager.getLogger(StatusGetAction.class);

    private final HeartBeatRepository heartBeatRepository;
    private final KeysCache cache;
    private final NotificationSenderStatus notificationSenderStatus;

    public StatusGetAction(
            HeartBeatRepository heartBeatRepository,
            NotificationSenderStatus notificationSenderStatus,
            KeysCache cache) {
        this.heartBeatRepository = heartBeatRepository;
        this.cache = cache;
        this.notificationSenderStatus = notificationSenderStatus;
    }

    @Override
    public HttpJsonResponse<TextMessage> process() throws ActionProcessException {
        if (ShouldReturnCachedResponse()){
            return getCachedResponse();
        }

        LOG.info("Cache miss");

        VerifyDatabase();
        VerifyNotificationsCanBeSent();

        CacheResponse();
        return getCachedResponse();
    }

    private void VerifyNotificationsCanBeSent() throws ActionProcessException{
        try {
            notificationSenderStatus.Verify();
        } catch (DalException e) {
            throw new ActionProcessException(e.getMessage());
        }
    }

    private void VerifyDatabase() throws ActionProcessException {
        val hb = new HeartBeat(
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
