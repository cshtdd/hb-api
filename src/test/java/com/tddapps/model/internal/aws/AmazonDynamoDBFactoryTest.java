package com.tddapps.model.internal.aws;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.tddapps.model.infrastructure.Settings;
import com.tddapps.model.infrastructure.SettingsReader;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AmazonDynamoDBFactoryTest {
    private final AmazonDynamoDB seededClient = AmazonDynamoDBClientBuilder.defaultClient();
    private final AmazonDynamoDBFactory factory = mock(AmazonDynamoDBFactory.class);
    private final SettingsReader settingsReaderMock = mock(SettingsReader.class);

    @Test
    void ReturnsDefaultClient(){
        when(settingsReaderMock.ReadString(Settings.DYNAMO_DB_ENDPOINT_OVERRIDE)).thenReturn("");
        when(factory.getDefaultClient()).thenReturn(seededClient);
        when(factory.createClient(settingsReaderMock)).thenCallRealMethod();

        val client = factory.createClient(settingsReaderMock);

        assertTrue(client == seededClient);
    }

    @Test
    void BuildsClientWhenThereIsAnEndpointOverride(){
        when(settingsReaderMock.ReadString(Settings.DYNAMO_DB_ENDPOINT_OVERRIDE)).thenReturn("blah");
        when(settingsReaderMock.ReadString(Settings.DEFAULT_REGION, Regions.DEFAULT_REGION.getName())).thenReturn("regiontest1");
        when(factory.getLocalClient("blah", "regiontest1")).thenReturn(seededClient);
        when(factory.createClient(settingsReaderMock)).thenCallRealMethod();

        val client = factory.createClient(settingsReaderMock);

        assertTrue(client == seededClient);
    }
}
