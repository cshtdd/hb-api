package com.tddapps.ioc;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.tddapps.model.heartbeats.HeartBeatJsonConverter;
import com.tddapps.model.heartbeats.HeartBeatParser;
import com.tddapps.model.heartbeats.internal.HeartBeatSerializer;
import com.tddapps.model.infrastructure.internal.InMemoryKeysCacheWithExpiration;
import com.tddapps.model.infrastructure.KeysCache;
import com.tddapps.model.heartbeats.HeartBeatRepository;
import com.tddapps.model.heartbeats.RequestHandlerHelper;
import com.tddapps.model.heartbeats.internal.RequestHandlerHelperCurrentRegion;
import com.tddapps.model.infrastructure.internal.EnvironmentSettingsReader;
import com.tddapps.model.infrastructure.SettingsReader;
import com.tddapps.model.internal.aws.DynamoDBEventParser;
import com.tddapps.model.internal.aws.DynamoDBEventParserMarshaller;
import com.tddapps.model.internal.aws.HeartBeatRepositoryDynamo;
import com.tddapps.model.internal.aws.NotificationSenderSns;
import com.tddapps.model.notifications.*;
import com.tddapps.model.notifications.internal.NotificationBuilderGrouped;
import com.tddapps.model.notifications.internal.SingleNotificationBuilder;
import com.tddapps.utils.NowReader;
import com.tddapps.utils.internal.NowReaderImpl;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IocContainerTest {
    @Test
    void DependenciesAreNotSingletonByDefault(){
        val repository1 = IocContainer.getInstance().Resolve(HeartBeatRepository.class);
        val repository2 = IocContainer.getInstance().Resolve(HeartBeatRepository.class);

        assertFalse(repository1 == repository2);
    }

    @Test
    void RegisterDependencies(){
        assertTrue(IocContainer.getInstance().Resolve(NowReader.class) instanceof NowReaderImpl);
        assertTrue(IocContainer.getInstance().Resolve(RequestHandlerHelper.class) instanceof RequestHandlerHelperCurrentRegion);
        assertTrue(IocContainer.getInstance().Resolve(HeartBeatRepository.class) instanceof HeartBeatRepositoryDynamo);
        assertTrue(IocContainer.getInstance().Resolve(NotificationSender.class) instanceof NotificationSenderSns);
        assertTrue(IocContainer.getInstance().Resolve(NotificationSenderStatus.class) instanceof NotificationSenderSns);
        assertTrue(IocContainer.getInstance().Resolve(SettingsReader.class) instanceof EnvironmentSettingsReader);
        assertTrue(IocContainer.getInstance().Resolve(HeartBeatNotificationBuilder.class) instanceof SingleNotificationBuilder);
        assertTrue(IocContainer.getInstance().Resolve(HeartBeatChangeEventNotificationBuilder.class) instanceof NotificationBuilderGrouped);
        assertTrue(IocContainer.getInstance().Resolve(DynamoDBEventParser.class) instanceof DynamoDBEventParserMarshaller);
        assertTrue(IocContainer.getInstance().Resolve(HeartBeatParser.class) instanceof HeartBeatSerializer);
        assertTrue(IocContainer.getInstance().Resolve(HeartBeatJsonConverter.class) instanceof HeartBeatSerializer);

        assertNotNull(IocContainer.getInstance().Resolve(AmazonDynamoDB.class));
    }

    @Test
    void RegistersDynamoDBMapperAsASingleton(){
        assertNotNull(IocContainer.getInstance().Resolve(DynamoDBMapper.class));

        val mapper1 = IocContainer.getInstance().Resolve(DynamoDBMapper.class);
        val mapper2 = IocContainer.getInstance().Resolve(DynamoDBMapper.class);

        assertTrue(mapper1 == mapper2);
    }

    @Test
    void RegistersInMemoryKeysCacheWithExpirationAsASingleton(){
        assertTrue(IocContainer.getInstance().Resolve(KeysCache.class) instanceof InMemoryKeysCacheWithExpiration);

        val cache1 = IocContainer.getInstance().Resolve(KeysCache.class);
        val cache2 = IocContainer.getInstance().Resolve(KeysCache.class);

        assertTrue(cache1 == cache2);
    }
}
