package com.tddapps.model;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;

import static com.tddapps.utils.DateExtensions.UtcNowPlusMs;

public abstract class HeartBeatFactory {
    private static final Random random = new Random();

    public static HeartBeat create(){
        return create(1)[0];
    }

    public static HeartBeat[] create(int count){
        return create(count, HeartBeatFactory::createSingleHeartBeat);
    }

    public static HeartBeat[] create(int count, Function<Integer, HeartBeat> heartBeatGenerator){
        return IntStream.range(0, count)
                .mapToObj(heartBeatGenerator::apply)
                .toArray(HeartBeat[]::new);
    }

    private static HeartBeat createSingleHeartBeat(int position){
        return new HeartBeat(
                String.format("test-host-%d", position),
                UtcNowPlusMs(random.nextInt(1000000) + 10000),
                true
        );
    }

}
