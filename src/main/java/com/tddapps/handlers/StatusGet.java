package com.tddapps.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.tddapps.actions.response.TextMessage;
import com.tddapps.handlers.infrastructure.ApiGatewayResponse;
import com.tddapps.infrastructure.KeysCache;
import com.tddapps.ioc.IocContainer;
import com.tddapps.model.DalException;
import com.tddapps.model.HeartBeat;
import com.tddapps.model.HeartBeatRepository;
import com.tddapps.model.NotificationSenderStatus;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.util.Map;

import static com.tddapps.utils.DateExtensions.UtcNowPlusMs;

@SuppressWarnings("unused")
@Log4j2
public class StatusGet implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    private final HeartBeatRepository heartBeatRepository;
    private final NotificationSenderStatus notificationSenderStatus;
    private final KeysCache cache;

    public StatusGet(){
        this(IocContainer.getInstance().Resolve(HeartBeatRepository.class),
                IocContainer.getInstance().Resolve(NotificationSenderStatus.class),
                IocContainer.getInstance().Resolve(KeysCache.class));
    }

    public StatusGet(HeartBeatRepository heartBeatRepository,
                     NotificationSenderStatus notificationSenderStatus,
                     KeysCache cache){
        this.heartBeatRepository = heartBeatRepository;
        this.notificationSenderStatus = notificationSenderStatus;
        this.cache = cache;
    }

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        log.debug(String.format("Input: %s", input));

        try {
            VerifyApiStatus();

            log.debug(String.format("StatusCode: %s, ResponseBody: %s", 200, TextMessage.OK.asJson()));
            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody(TextMessage.OK)
                    .build();

        } catch (DalException e) {
            log.error("Action processing failed", e);

            val errorResponse = TextMessage.create(e.getMessage());

            log.debug(String.format("StatusCode: %s, ResponseBody: %s", 500, errorResponse));
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(errorResponse)
                    .build();
        }
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
