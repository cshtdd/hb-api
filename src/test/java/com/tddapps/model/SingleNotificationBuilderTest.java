package com.tddapps.model;

import com.tddapps.utils.NowReader;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.tddapps.utils.DateExtensions.ToUtcString;
import static com.tddapps.utils.DateExtensions.UtcNowPlusMs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SingleNotificationBuilderTest {
    private final NowReader nowReaderMock = mock(NowReader.class);
    private final SingleNotificationBuilder builder = new SingleNotificationBuilder(nowReaderMock);

    private String utcNowFormatted;

    @BeforeEach
    public void Setup(){
        val seededDate = UtcNowPlusMs(1000);
        utcNowFormatted = ToUtcString(seededDate);

        when(nowReaderMock.ReadUtc())
                .thenReturn(seededDate);
    }

    @Test
    public void DoesNotSendNotificationWhenNoHeartBeatsAreProvided(){
        assertEquals(0, builder.build(new NotificationMetadata(""), new HeartBeat[0]).length);
    }
}
