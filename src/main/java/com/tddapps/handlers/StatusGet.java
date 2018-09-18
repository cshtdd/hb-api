package com.tddapps.handlers;

import com.tddapps.handlers.infrastructure.ApiGatewayHandler;
import com.tddapps.handlers.infrastructure.ApiGatewayResponse;
import com.tddapps.infrastructure.KeysCache;
import com.tddapps.ioc.IocContainer;
import com.tddapps.model.*;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.util.Map;

import static com.tddapps.utils.DateExtensions.UtcNowPlusMs;

@SuppressWarnings("unused")
@Log4j2
public class StatusGet extends ApiGatewayHandler {
    private final HeartBeatRepository heartBeatRepository;
    private final NotificationSenderStatus notificationSenderStatus;
    private final KeysCache cache;

    public StatusGet(){
        this(IocContainer.getInstance().Resolve(HeartBeatRepository.class),
                IocContainer.getInstance().Resolve(NotificationSenderStatus.class),
                IocContainer.getInstance().Resolve(KeysCache.class));
    }

    @Override
    protected ApiGatewayResponse processRequest(Map<String, Object> input) {
        try {
            VerifyApiStatus();

            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody(TextMessage.OK)
                    .build();

        } catch (DalException e) {
            log.error("Action processing failed", e);

            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(TextMessage.create(e.getMessage()))
                    .build();
        }
    }

    public StatusGet(HeartBeatRepository heartBeatRepository,
                     NotificationSenderStatus notificationSenderStatus,
                     KeysCache cache){
        this.heartBeatRepository = heartBeatRepository;
        this.notificationSenderStatus = notificationSenderStatus;
        this.cache = cache;
    }

    private void VerifyApiStatus() throws DalException {
        if (ShouldReturnCachedResponse()){
            return;
        }

        log.info("Cache miss");

        VerifyDatabase();
        VerifyNotificationsCanBeSent();

        CacheResponse();
    }

    private void VerifyNotificationsCanBeSent() throws DalException{
        notificationSenderStatus.Verify();
    }

    private void VerifyDatabase() throws DalException {
        val hb = new HeartBeat(
                getClass().getSimpleName(),
                UtcNowPlusMs(4*60*60*1000),
                true
        );

        heartBeatRepository.Save(hb);
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
