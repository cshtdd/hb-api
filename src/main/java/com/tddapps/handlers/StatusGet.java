package com.tddapps.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.tddapps.actions.response.TextMessage;
import com.tddapps.controllers.ActionProcessException;
import com.tddapps.controllers.HttpJsonResponse;
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
        this(
                IocContainer.getInstance().Resolve(HeartBeatRepository.class),
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
            val successResponse = process();

            log.debug(String.format("StatusCode: %s, ResponseBody: %s", successResponse.getStatusCode(), successResponse.getBody()));

            return ApiGatewayResponse.builder()
                    .setStatusCode(successResponse.getStatusCode())
                    .setObjectBody(successResponse.getBody())
                    .build();

        } catch (ActionProcessException e) {
            log.error("Action processing failed", e);

            val errorResponse = HttpJsonResponse.ServerErrorWithMessage(e.getMessage());

            return ApiGatewayResponse.builder()
                    .setStatusCode(errorResponse.getStatusCode())
                    .setObjectBody(errorResponse.getBody())
                    .build();
        }
    }

    private HttpJsonResponse<TextMessage> process() throws ActionProcessException {
        if (ShouldReturnCachedResponse()){
            return getCachedResponse();
        }

        log.info("Cache miss");

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

    private HttpJsonResponse<TextMessage> getCachedResponse() {
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
