package com.tddapps.handlers;

import com.tddapps.model.*;
import com.tddapps.utils.NowReader;
import lombok.val;
import org.junit.jupiter.api.*;
import static com.tddapps.utils.DateExtensions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HeartBeatExpiratorTest {
    private final HeartBeatRepository heartBeatRepository = mock(HeartBeatRepository.class);
    private final SettingsReader settingsReader = mock(SettingsReader.class);
    private final NowReader nowReader = mock(NowReader.class);
    private final HeartBeatExpirator handler = new HeartBeatExpirator(heartBeatRepository, settingsReader, nowReader);

    private final long NOW_EPOCH_SECOND = 1538395893;
    private final String NOW_MINUTE_STRING = ToReverseUtcMinuteString(NOW_EPOCH_SECOND);
    private final int MAX_COUNT = 25;

    @BeforeEach
    public void Setup(){
        when(settingsReader.ReadString(Settings.AWS_REGION)).thenReturn("us-test-1");
        when(nowReader.ReadEpochSecond()).thenReturn(NOW_EPOCH_SECOND);
    }

    @Test
    public void CanBeConstructedUsingTheDefaultConstructor(){
        assertNotNull(new HeartBeatExpirator());
    }

    @Test
    public void ReadsTheExpiredHeartBeatsInTheCurrentMinute() throws DalException {
        assertTrue(handleRequest());

        verify(heartBeatRepository)
                .ReadOlderThan(NOW_MINUTE_STRING, NOW_EPOCH_SECOND, MAX_COUNT);
    }

    @Test
    public void ReturnsFalseWhenExpiredHeartBeatsCannotBeRead() throws DalException {
        doThrow(new DalException("Read failed"))
            .when(heartBeatRepository)
            .ReadOlderThan(any(String.class), any(long.class), any(int.class));

        assertFalse(handleRequest());
    }

    @Test
    public void DeletesTheExpiredHeartBeats() throws DalException{
        val seededHeartBeats = HeartBeatFactory.Create(10);
        when(heartBeatRepository.ReadOlderThan(NOW_MINUTE_STRING, NOW_EPOCH_SECOND, MAX_COUNT))
                .thenReturn(seededHeartBeats);

        assertTrue(handleRequest());

        verify(heartBeatRepository).Delete(seededHeartBeats);
    }

    @Test
    public void ReturnsFalseWhenExpiredHeartBeatsCannotBeDeleted() throws DalException{
        doThrow(new DalException("Delete failed"))
                .when(heartBeatRepository)
                .Delete(any());

        assertFalse(handleRequest());
    }

    private boolean handleRequest(){
        return handler.handleRequest(null, null);
    }
}
