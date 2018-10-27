package com.tddapps.model.internal.aws;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.tddapps.model.infrastructure.Settings;
import com.tddapps.model.infrastructure.SettingsReader;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AmazonDynamoDBFactoryTest {
    private final AmazonDynamoDB seededClient = AmazonDynamoDBClientBuilder.defaultClient();
    private final AmazonDynamoDBFactory factory = mock(AmazonDynamoDBFactory.class);
    private final SettingsReader settingsReaderMock = mock(SettingsReader.class);

    @Test
    public void ReturnsDefaultClient(){
        when(settingsReaderMock.ReadString(Settings.DYNAMO_DB_ENDPOINT_OVERRIDE)).thenReturn("");
        when(factory.getDefaultClient()).thenReturn(seededClient);
        when(factory.createClient(settingsReaderMock)).thenCallRealMethod();

        val client = factory.createClient(settingsReaderMock);

        assertTrue(client == seededClient);
    }

    @Test
    public void ReturnsDefaultClientWhenThereIsAnEndpointOverride(){
        when(settingsReaderMock.ReadString(Settings.DYNAMO_DB_ENDPOINT_OVERRIDE)).thenReturn("blah");
        when(factory.getLocalClient("blah")).thenReturn(seededClient);
        when(factory.createClient(settingsReaderMock)).thenCallRealMethod();

        val client = factory.createClient(settingsReaderMock);

        assertTrue(client == seededClient);
    }
}
