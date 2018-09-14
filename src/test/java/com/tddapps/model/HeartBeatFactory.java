package com.tddapps.model;

import java.util.Date;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;

import static com.tddapps.utils.DateExtensions.UtcNowPlusMs;

public abstract class HeartBeatFactory {
    private static final Random random = new Random();

    public static HeartBeat Create(){
        return Create(1)[0];
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
        return new HeartBeat(
                HeartBeatHost(position),
                UtcNowPlusMs(random.nextInt(1000000) + 10000),
                true
        );
    }

    public static HeartBeat[] CreateWithExpirations(Date ... expirationDates) {
        return Create(expirationDates.length, (position) -> new HeartBeat(
                HeartBeatHost(position),
                expirationDates[position],
                true
        ));
    }

    private static String HeartBeatHost(Integer position) {
        return String.format("test-host-%d", position);
    }
}
