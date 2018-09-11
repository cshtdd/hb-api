package com.tddapps.model.aws;

import com.tddapps.model.Settings;
import com.tddapps.model.SettingsReader;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DynamoDBClientFactoryResolverTest {
    private final DynamoDBClientFactoryResolver resolver = new DynamoDBClientFactoryResolver();
    private final SettingsReader settingsReaderMock = mock(SettingsReader.class);

    private final DynamoDBClientFactory defaultClient = mock(DynamoDBClientFactory.class);
    private final DynamoDBClientFactory localClient = mock(DynamoDBClientFactory.class);

    @Test
    public void ReturnsDefaultClient(){
        when(settingsReaderMock.ReadString(Settings.DYNAMO_DB_ENDPOINT_OVERRIDE)).thenReturn("");

        val client = resolver.createClient(settingsReaderMock, defaultClient, localClient);

        assertTrue(client == defaultClient);
    }

    @Test
    public void ReturnsDefaultClientWhenThereIsAnEndpointOverride(){
        when(settingsReaderMock.ReadString(Settings.DYNAMO_DB_ENDPOINT_OVERRIDE)).thenReturn("blah");

        val client = resolver.createClient(settingsReaderMock, defaultClient, localClient);

        assertTrue(client == localClient);
    }
}
