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

import static com.tddapps.utils.DateExtensions.UtcNowPlusMs;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

public class HeartBeatPostTest {
    private final List<InvocationOnMock> saveInvocations = new ArrayList<>();
    private final HeartBeatRepository heartBeatRepository = mock(HeartBeatRepository.class);
    private final HeartBeatPost handler = new HeartBeatPost(heartBeatRepository);

    private final String MAXIMUM_LENGTH_ALLOWED_STRING = StringUtils.leftPad("", 100, "0");
    private final String INVALID_HOST_ID = "Invalid hostId";
    private final String INVALID_INTERVAL_MS = "Invalid intervalMs";

    @BeforeEach
    public void Setup(){
        try {
            doAnswer(saveInvocations::add)
                    .when(heartBeatRepository)
                    .Save(any(HeartBeat.class));
        } catch (DalException e) { }
    }

    @Test
    public void ReadsTheMaximumLengthHostId() {
        handleRequest(String.format(
                "{\"hostId\": \"%s\"}", MAXIMUM_LENGTH_ALLOWED_STRING
        ));

        assertEquals(MAXIMUM_LENGTH_ALLOWED_STRING, ((HeartBeat) saveInvocations.get(0).getArgument(0)).getHostId());
    }

    @Test
    public void ParsingFailsWhenHostIdIsMissing(){
        parseShouldFailWithError("{}", INVALID_HOST_ID);
        parseShouldFailWithError("{\"hostId\": \"\"}", INVALID_HOST_ID);
        parseShouldFailWithError("{\"hostId\": \"   \"}", INVALID_HOST_ID);
    }

    @Test
    public void ParsingFailsWhenHostIdIsNotAlphanumeric(){
        parseShouldFailWithError("{\"hostId\": \"-!@#$$%^%^ &^&\"}", INVALID_HOST_ID);
    }

    @Test
    public void ParsingFailsWhenHostIdIsTooLong(){
        parseShouldFailWithError(String.format(
                "{\"hostId\": \"X%s\"}", MAXIMUM_LENGTH_ALLOWED_STRING
        ), INVALID_HOST_ID);
    }

    @Test
    public void ParsingSupportsMultipleDataTypesForIntervalMs() {
        val expectedHeartBeat = new HeartBeat(
                "superHost1",
                UtcNowPlusMs(3000),
                false
        );

        handleRequest("{\"hostId\": \"superHost1\", \"intervalMs\": 3000}");
        handleRequest("{\"hostId\": \"superHost1\", \"intervalMs\": 3000.45}");
        handleRequest("{\"hostId\": \"superHost1\", \"intervalMs\": \"3000\"}");

        boolean allHeartBeatHaveDefaultExpiration = saveInvocations
                .stream()
                .map(i -> (HeartBeat) i.getArgument(0))
                .allMatch(expectedHeartBeat::almostEquals);
        assertTrue(allHeartBeatHaveDefaultExpiration);
    }

    @Test
    public void ParsingAssumesDefaultWhenIntervalMsIsNotNumeric() {
        val expectedHeartBeat = new HeartBeat(
                "superHost1",
                UtcNowPlusMs(HeartBeat.DEFAULT_INTERVAL_MS),
                false
        );

        handleRequest("{\"hostId\": \"superHost1\", \"intervalMs\": null}");
        handleRequest("{\"hostId\": \"superHost1\", \"intervalMs\": \"\"}");
        handleRequest("{\"hostId\": \"superHost1\", \"intervalMs\": \" \"}");
        handleRequest("{\"hostId\": \"superHost1\", \"intervalMs\": \"pete\"}");

        boolean allHeartBeatHaveDefaultExpiration = saveInvocations
                .stream()
                .map(i -> (HeartBeat) i.getArgument(0))
                .allMatch(expectedHeartBeat::almostEquals);
        assertTrue(allHeartBeatHaveDefaultExpiration);
    }

    @Test
    public void ParsingFailsWhenIntervalMsIsOutOfBoundaries(){
        parseShouldFailWithError("{\"hostId\": \"host1\", \"intervalMs\": 999}", INVALID_INTERVAL_MS);
        parseShouldFailWithError("{\"hostId\": \"host1\", \"intervalMs\": \"999\"}", INVALID_INTERVAL_MS);
        parseShouldFailWithError("{\"hostId\": \"host1\", \"intervalMs\": 43200001}", INVALID_INTERVAL_MS);
        parseShouldFailWithError("{\"hostId\": \"host1\", \"intervalMs\": \"43200001\"}", INVALID_INTERVAL_MS);
    }

    @Test
    public void ProcessWritesTheHeartBeat() throws DalException {
        val expectedHeartBeat = new HeartBeat(
                "testHostA",
                UtcNowPlusMs(34000),
                false
        );

        val result = handleRequest("{\"hostId\": \"testHostA\", \"intervalMs\": 34000}");

        assertEquals(200, result.getStatusCode());
        assertEquals("{\"message\":\"OK\"}", result.getBody());
        verify(heartBeatRepository).Save(argThat(expectedHeartBeat::almostEquals));
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

    private void parseShouldFailWithError(String requestBody, String errorMessage){
        val response = handleRequest(requestBody);

        assertEquals(400, response.getStatusCode());

        val expectedBody = String.format("{\"message\":\"%s\"}", errorMessage);
        assertEquals(expectedBody, response.getBody());
    }

    private ApiGatewayResponse handleRequest(String body) {
        return handler.handleRequest(new HashMap<String, Object>() {{
            put("body", body);
        }}, null);
    }
}
