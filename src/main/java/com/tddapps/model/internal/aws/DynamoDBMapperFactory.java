package com.tddapps.model.internal.aws;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.tddapps.model.infrastructure.Settings;
import com.tddapps.model.infrastructure.SettingsReader;
import lombok.val;
import org.picocontainer.PicoContainer;
import org.picocontainer.injectors.FactoryInjector;

import java.lang.reflect.Type;

public class DynamoDBMapperFactory extends FactoryInjector<DynamoDBMapper>{
    @Override
    public DynamoDBMapper getComponentInstance(PicoContainer container, Type into) {
        val settingsReader = container.getComponent(SettingsReader.class);
        val dynamoDBClient = container.getComponent(AmazonDynamoDB.class);

        return createMapper(settingsReader, dynamoDBClient);
    }

    public DynamoDBMapper createMapper(SettingsReader settingsReader, AmazonDynamoDB dynamoDBClient){
        return new DynamoDBMapper(
                dynamoDBClient,
                getConfigBuilder(settingsReader).build()
        );
    }

    private DynamoDBMapperConfig.Builder getConfigBuilder(SettingsReader settingsReader) {
        val tablePrefix = settingsReader.ReadString(Settings.TABLE_PREFIX);
        val tableNameOverride = DynamoDBMapperConfig.TableNameOverride.withTableNamePrefix(tablePrefix);

        val configBuilder = DynamoDBMapperConfig.builder();
        configBuilder.setTableNameOverride(tableNameOverride);
        return configBuilder;
    }
}
