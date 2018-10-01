package com.tddapps.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.tddapps.ioc.IocContainer;
import com.tddapps.model.*;
import com.tddapps.utils.NowReader;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import java.util.Map;

import static com.tddapps.utils.DateExtensions.ToReverseUtcMinuteString;

@Log4j2
@SuppressWarnings("unused")
public class HeartBeatExpirator implements RequestHandler<Map<String, Object>, Boolean> {
    private final int MAX_COUNT_TO_PROCESS = 25;

    private final HeartBeatRepository heartBeatRepository;
    private final NowReader nowReader;
    private final RequestHandlerHelper requestHandlerHelper;

    public HeartBeatExpirator(){
        this(
                IocContainer.getInstance().Resolve(HeartBeatRepository.class),
                IocContainer.getInstance().Resolve(SettingsReader.class),
                IocContainer.getInstance().Resolve(NowReader.class)
        );
    }

    public HeartBeatExpirator(HeartBeatRepository heartBeatRepository, SettingsReader settingsReader, NowReader nowReader) {
        this.heartBeatRepository = heartBeatRepository;
        this.nowReader = nowReader;
        this.requestHandlerHelper = new RequestHandlerHelperCurrentRegion(settingsReader);
    }

    @Override
    public Boolean handleRequest(Map<String, Object> input, Context context) {
        try {
            log.info("Removing expired HeartBeats");

            val expiredHeartBeats = readExpiredHeartBeats();
            val heartBeatsToDelete = requestHandlerHelper.filter(expiredHeartBeats);

            heartBeatRepository.Delete(heartBeatsToDelete);

            log.info("Removing expired HeartBeats Completed");
            return true;
        } catch (DalException e) {
            log.error("Removing expired HeartBeats failed", e);
            return false;
        }
    }

    private HeartBeat[] readExpiredHeartBeats() throws DalException {
        val ttlNow = nowReader.ReadEpochSecond();
        val minuteStringNow = ToReverseUtcMinuteString(ttlNow - 60);

        return heartBeatRepository.Read(minuteStringNow, MAX_COUNT_TO_PROCESS);
    }
}
