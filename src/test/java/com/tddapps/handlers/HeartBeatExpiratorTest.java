package com.tddapps.handlers;

import com.tddapps.model.*;
import com.tddapps.model.heartbeats.HeartBeat;
import com.tddapps.model.heartbeats.HeartBeatRepository;
import com.tddapps.model.heartbeats.RequestHandlerHelper;
import com.tddapps.model.heartbeats.test.HeartBeatFactory;
import com.tddapps.utils.NowReader;
import lombok.val;
import org.junit.jupiter.api.*;

import java.util.Arrays;

import static com.tddapps.utils.DateExtensions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HeartBeatExpiratorTest {
    private final HeartBeatRepository heartBeatRepository = mock(HeartBeatRepository.class);
    private final RequestHandlerHelper requestHandlerHelper = mock(RequestHandlerHelper.class);
    private final NowReader nowReader = mock(NowReader.class);
    private final HeartBeatExpirator handler = new HeartBeatExpirator(heartBeatRepository, nowReader, requestHandlerHelper);

    private final long NOW_EPOCH_SECOND = 1538395893;
    private final String PREVIOUS_MINUTE_STRING = ToReverseUtcMinuteString(NOW_EPOCH_SECOND - 60);
    private final int MAX_COUNT = 25;

    @BeforeEach
    public void Setup(){
        when(nowReader.ReadEpochSecond()).thenReturn(NOW_EPOCH_SECOND);
    }

    @Test
    public void CanBeConstructedUsingTheDefaultConstructor(){
        assertNotNull(new HeartBeatExpirator());
    }

    @Test
    public void ReadsTheExpiredHeartBeatsInThePreviousMinute() throws DalException {
        when(heartBeatRepository.Read(PREVIOUS_MINUTE_STRING, MAX_COUNT))
                .thenReturn(new HeartBeat[]{});

        assertTrue(handleRequest());

        verify(heartBeatRepository)
                .Read(PREVIOUS_MINUTE_STRING, MAX_COUNT);
    }

    @Test
    public void ReturnsFalseWhenExpiredHeartBeatsCannotBeRead() throws DalException {
        doThrow(new DalException("Read failed"))
            .when(heartBeatRepository)
            .Read(any(String.class), any(int.class));

        assertFalse(handleRequest());
    }

    @Test
    public void DeletesTheExpiredHeartBeatsAfterFilteringThem() throws DalException{
        val allHeartBeats = HeartBeatFactory.Create(30);
        val seededHeartBeats = Arrays.copyOfRange(allHeartBeats, 0, 15);
        val filteredHeartBeats = Arrays.copyOfRange(allHeartBeats, 15, 30);
        when(heartBeatRepository.Read(PREVIOUS_MINUTE_STRING, MAX_COUNT)).thenReturn(seededHeartBeats);
        when(requestHandlerHelper.filter(seededHeartBeats)).thenReturn(filteredHeartBeats);

        assertTrue(handleRequest());

        verify(heartBeatRepository).Delete(filteredHeartBeats);
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
