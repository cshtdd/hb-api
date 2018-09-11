package com.tddapps.model;

import java.util.stream.IntStream;

import static com.tddapps.utils.DateExtensions.UtcNowPlusMs;

public abstract class HeartBeatFactory {
    public static HeartBeat create(){
        return create(1)[0];
    }

    public static HeartBeat[] create(int count){
        return IntStream.range(0, count)
                .mapToObj(HeartBeatFactory::createSingleHeartBeat)
                .toArray(HeartBeat[]::new);
    }

    private static HeartBeat createSingleHeartBeat(int position){
        return new HeartBeat(
                String.format("test-host-%d", position),
                UtcNowPlusMs(10000),
                true
        );
    }

}
