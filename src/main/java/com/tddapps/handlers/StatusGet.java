package com.tddapps.handlers;

import com.tddapps.handlers.infrastructure.ApiGatewayHandler;
import com.tddapps.handlers.infrastructure.ApiGatewayResponse;
import com.tddapps.model.infrastructure.KeysCache;
import com.tddapps.ioc.IocContainer;
import com.tddapps.model.*;
import com.tddapps.model.heartbeats.HeartBeat;
import com.tddapps.model.heartbeats.HeartBeatRepository;
import com.tddapps.model.infrastructure.Settings;
import com.tddapps.model.infrastructure.SettingsReader;
import com.tddapps.model.notifications.NotificationSenderStatus;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.util.Map;

import static com.tddapps.utils.DateExtensions.EpochSecondsPlusMs;

@SuppressWarnings("unused")
@Log4j2
public class StatusGet extends ApiGatewayHandler {
    private final HeartBeatRepository heartBeatRepository;
    private final NotificationSenderStatus notificationSenderStatus;
    private final SettingsReader settingsReader;
    private final KeysCache cache;

    public StatusGet(){
        this(IocContainer.getInstance().Resolve(HeartBeatRepository.class),
                IocContainer.getInstance().Resolve(NotificationSenderStatus.class),
                IocContainer.getInstance().Resolve(SettingsReader.class),
                IocContainer.getInstance().Resolve(KeysCache.class));
    }

    public StatusGet(HeartBeatRepository heartBeatRepository,
                     NotificationSenderStatus notificationSenderStatus,
                     SettingsReader settingsReader,
                     KeysCache cache){
        this.heartBeatRepository = heartBeatRepository;
        this.notificationSenderStatus = notificationSenderStatus;
        this.settingsReader = settingsReader;
        this.cache = cache;
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
                ReadStatusHostId(),
                EpochSecondsPlusMs(4*60*60*1000),
                ReadRegion(),
                true
        );

        heartBeatRepository.Save(new HeartBeat[]{ hb });
    }

    private String ReadStatusHostId(){
        return String.format("%s-%s",
                getClass().getSimpleName(),
                ReadRegion());
    }

    private String ReadRegion() {
        return settingsReader.ReadString(Settings.AWS_REGION);
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
