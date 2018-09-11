package com.tddapps.model.aws;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.tddapps.model.Settings;
import com.tddapps.model.SettingsReader;
import lombok.val;

public class DynamoDBClientFactoryWithLocalSupport implements DynamoDBClientFactory {
    private final SettingsReader settingsReader;
    private final DynamoDBClientFactoryDefault clientFactoryDefault;
    private final DynamoDBClientFactoryLocal clientFactoryLocal;

    public DynamoDBClientFactoryWithLocalSupport(
            SettingsReader settingsReader,
            DynamoDBClientFactoryDefault clientFactoryDefault,
            DynamoDBClientFactoryLocal clientFactoryLocal) {
        this.settingsReader = settingsReader;
        this.clientFactoryDefault = clientFactoryDefault;
        this.clientFactoryLocal = clientFactoryLocal;
    }

    @Override
    public AmazonDynamoDB getClient() {
        val dynamoDbEndpointOverride = settingsReader.ReadString(Settings.DYNAMO_DB_ENDPOINT_OVERRIDE);
        if (dynamoDbEndpointOverride.isEmpty()){
            return clientFactoryDefault.getClient();
        }

        return clientFactoryLocal.getClient();
    }
}
