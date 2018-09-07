package com.tddapps.dal;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SingleNotificationBuilderTest {
    private final SingleNotificationBuilder builder = new SingleNotificationBuilder();

    @Test
    public void DoesNotSendNotificationWhenNoHeartBeatsAreProvided(){
        assertEquals(0, builder.build(null).length);
        assertEquals(0, builder.build(new HeartBeat[]{}).length);
    }
}
