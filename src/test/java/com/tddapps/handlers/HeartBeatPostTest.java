package com.tddapps.handlers;

import com.tddapps.handlers.infrastructure.ApiGatewayResponse;
import com.tddapps.model.DalException;
import com.tddapps.model.HeartBeat;
import com.tddapps.model.HeartBeatRepository;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.tddapps.utils.DateExtensions.EpochSecondsPlusMs;
import static com.tddapps.utils.DateExtensions.UtcNowPlusMs;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

public class HeartBeatPostTest {
    private final HeartBeatRepository heartBeatRepository = mock(HeartBeatRepository.class);
    private final HeartBeatPost handler = new HeartBeatPost(heartBeatRepository);

    @Test
    public void CanBeConstructedUsingADefaultConstructor(){
        assertNotNull(new HeartBeatPost());
    }

    @Test
    public void ReturnsClientErrorWhenInputIsInvalid() throws DalException{
        val result = handleRequest("{incorrect json");

        assertEquals(400, result.getStatusCode());
        assertEquals("{\"message\":\"Invalid json\"}", result.getBody());
        verify(heartBeatRepository, times(0)).Save(any(HeartBeat.class));
    }

    @Test
    public void ProcessWritesTheHeartBeat() throws DalException {
        val expectedHeartBeat = new HeartBeat(
                "testHostA",
                EpochSecondsPlusMs(34000),
                false
        );

        val result = handleRequest("{\"hostId\": \"testHostA\", \"intervalMs\": 34000}");

        assertEquals(200, result.getStatusCode());
        assertEquals("{\"message\":\"OK\"}", result.getBody());
        verify(heartBeatRepository).Save(expectedHeartBeat);
    }

    @Test
    public void ProcessThrowsAnActionProcessExceptionWhenTheHeartBeatCouldNotBeSaved() throws DalException {
        doThrow(new DalException("Save failed"))
                .when(heartBeatRepository)
                .Save(any(HeartBeat.class));

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
