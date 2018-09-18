package com.tddapps.handlers;

import com.tddapps.model.NotificationSender;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class HeartBeatChangeTest {
    private final NotificationSender notificationSender = mock(NotificationSender.class);
    private final HeartBeatChange handler = new HeartBeatChange(notificationSender);

    @Test
    public void CanBeConstructedUsingTheDefaultConstructor(){
        assertNotNull(new HeartBeatChange());
    }
}
