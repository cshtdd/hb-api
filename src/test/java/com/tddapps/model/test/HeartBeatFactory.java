package com.tddapps.model.test;

import com.tddapps.model.HeartBeat;
import lombok.val;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;

import static com.tddapps.utils.DateExtensions.EpochSecondsPlusMs;
import static com.tddapps.utils.DateExtensions.ToReverseUtcMinuteString;

public abstract class HeartBeatFactory {
    public static final String TEST_REGION_DEFAULT = "us-test-1";

    private static final Random random = new Random();

    public static HeartBeat Create(){
        return Create(1)[0];
    }

    public static HeartBeat Create(String hostId){
        val result = Create();
        result.setHostId(hostId);
        return result;
    }

    public static HeartBeat[] Create(int count){
        return Create(count, HeartBeatFactory::CreateSingleHeartBeat);
    }

    public static HeartBeat[] Create(int count, Function<Integer, HeartBeat> heartBeatGenerator){
        return IntStream.range(0, count)
                .mapToObj(heartBeatGenerator::apply)
                .toArray(HeartBeat[]::new);
    }

    private static HeartBeat CreateSingleHeartBeat(int position){
        long ttl = EpochSecondsPlusMs(random.nextInt(1000000) + 10000);
        return new HeartBeat(
                HeartBeatHost(position),
                ttl,
                ToReverseUtcMinuteString(ttl),
                TEST_REGION_DEFAULT,
                false
        );
    }

    private static String HeartBeatHost(Integer position) {
        return String.format("test-host-%d", position);
    }
}
