package com.tddapps.model;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;

import static com.tddapps.utils.DateExtensions.EpochSecondsPlusMs;

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
                EpochSecondsPlusMs(random.nextInt(1000000) + 10000),
                "us-test-1",
                true
        );
    }

    private static String HeartBeatHost(Integer position) {
        return String.format("test-host-%d", position);
    }
}
