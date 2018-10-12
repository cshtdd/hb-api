package com.tddapps.handlers;

import com.tddapps.handlers.infrastructure.ApiGatewayResponse;
import com.tddapps.model.*;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static com.tddapps.model.test.HeartBeatFactory.TEST_REGION_DEFAULT;
import static com.tddapps.utils.DateExtensions.EpochSecondsPlusMs;
import static com.tddapps.utils.DateExtensions.ToReverseUtcMinuteString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class HeartBeatPostTest {
    private final HeartBeatRepository heartBeatRepository = mock(HeartBeatRepository.class);
    private final SettingsReader settingsReader = mock(SettingsReader.class);
    private final HeartBeatPost handler = new HeartBeatPost(heartBeatRepository, settingsReader);

    @BeforeEach
    public void Setup(){
        when(settingsReader.ReadString(Settings.AWS_REGION)).thenReturn(TEST_REGION_DEFAULT);
    }

    @Test
    public void CanBeConstructedUsingADefaultConstructor(){
        assertNotNull(new HeartBeatPost());
    }

    @Test
    public void ReturnsClientErrorWhenInputIsInvalid() throws DalException{
        val result = handleRequest("{incorrect json");

        assertEquals(400, result.getStatusCode());
        assertEquals("{\"message\":\"Invalid json\"}", result.getBody());
        verify(heartBeatRepository, times(0)).Save(any(HeartBeat[].class));
    }

    @Test
    public void ProcessWritesTheHeartBeat() throws DalException {
        val expectedHeartBeats = new HeartBeat[] {
                new HeartBeat(
                        "testHostA",
                        EpochSecondsPlusMs(34000),
                        ToReverseUtcMinuteString(EpochSecondsPlusMs(34000)),
                        TEST_REGION_DEFAULT,
                        false
                )
        };

        val result = handleRequest("{\"hostId\": \"testHostA\", \"intervalMs\": 34000}");

        assertEquals(200, result.getStatusCode());
        assertEquals("{\"message\":\"OK\"}", result.getBody());
        verify(heartBeatRepository).Save(expectedHeartBeats);
    }

    @Test
    public void ProcessThrowsAnActionProcessExceptionWhenTheHeartBeatCouldNotBeSaved() throws DalException {
        doThrow(new DalException("Save failed"))
                .when(heartBeatRepository)
                .Save(any(HeartBeat[].class));

        val result = handleRequest("{\"hostId\": \"testHostA\", \"intervalMs\": 34000}");

        assertEquals(500, result.getStatusCode());
        assertEquals("{\"message\":\"Save failed\"}", result.getBody());
    }

    private ApiGatewayResponse handleRequest(String body) {
        return handler.handleRequest(new HashMap<String, Object>() {{
            put("body", body);
        }}, null);
    }
}
