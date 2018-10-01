package com.tddapps.handlers;

import com.tddapps.model.*;
import com.tddapps.utils.NowReader;
import lombok.val;
import org.junit.jupiter.api.*;

import java.util.Arrays;

import static com.tddapps.model.HeartBeatFactory.TEST_REGION_DEFAULT;
import static com.tddapps.utils.DateExtensions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HeartBeatExpiratorTest {
    private final HeartBeatRepository heartBeatRepository = mock(HeartBeatRepository.class);
    private final SettingsReader settingsReader = mock(SettingsReader.class);
    private final NowReader nowReader = mock(NowReader.class);
    private final HeartBeatExpirator handler = new HeartBeatExpirator(heartBeatRepository, settingsReader, nowReader);

    private final long NOW_EPOCH_SECOND = 1538395893;
    private final String PREVIOUS_MINUTE_STRING = ToReverseUtcMinuteString(NOW_EPOCH_SECOND - 60);
    private final int MAX_COUNT = 25;

    @BeforeEach
    public void Setup(){
        when(settingsReader.ReadString(Settings.AWS_REGION)).thenReturn(TEST_REGION_DEFAULT);
        when(nowReader.ReadEpochSecond()).thenReturn(NOW_EPOCH_SECOND);
    }

    @Test
    public void CanBeConstructedUsingTheDefaultConstructor(){
        assertNotNull(new HeartBeatExpirator());
    }

    @Test
    public void ReadsTheExpiredHeartBeatsInTheCurrentMinute() throws DalException {
        when(heartBeatRepository.ReadOlderThan(PREVIOUS_MINUTE_STRING, NOW_EPOCH_SECOND, MAX_COUNT))
                .thenReturn(new HeartBeat[]{});

        assertTrue(handleRequest());

        verify(heartBeatRepository)
                .ReadOlderThan(PREVIOUS_MINUTE_STRING, NOW_EPOCH_SECOND, MAX_COUNT);
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
        when(heartBeatRepository.ReadOlderThan(PREVIOUS_MINUTE_STRING, NOW_EPOCH_SECOND, MAX_COUNT))
                .thenReturn(seededHeartBeats);

        assertTrue(handleRequest());

        verify(heartBeatRepository).Delete(seededHeartBeats);
    }

    @Test
    public void ReturnsFalseWhenExpiredHeartBeatsCannotBeDeleted() throws DalException{
        when(heartBeatRepository.ReadOlderThan(PREVIOUS_MINUTE_STRING, NOW_EPOCH_SECOND, MAX_COUNT))
                .thenReturn(new HeartBeat[]{});
        doThrow(new DalException("Delete failed"))
                .when(heartBeatRepository)
                .Delete(any());

        assertFalse(handleRequest());
    }

    @Test
    public void OnlyDeletesTheExpiredHeartBeatsFromTheCurrentRegion() throws DalException {
        val seededHeartBeats = HeartBeatFactory.Create(10);
        seededHeartBeats[8].setRegion("us-test-2");
        seededHeartBeats[9].setRegion("us-test-2");
        val expectedDeletions = Arrays.copyOfRange(seededHeartBeats, 0, 8);

        when(heartBeatRepository.ReadOlderThan(PREVIOUS_MINUTE_STRING, NOW_EPOCH_SECOND, MAX_COUNT))
                .thenReturn(seededHeartBeats);

        assertTrue(handleRequest());

        verify(heartBeatRepository).Delete(expectedDeletions);
    }

    private boolean handleRequest(){
        return handler.handleRequest(null, null);
    }
}
