package com.tddapps.ioc;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.tddapps.actions.NotificationCalculatorAction;
import com.tddapps.model.*;
import com.tddapps.model.aws.*;
import com.tddapps.infrastructure.InMemoryKeysCacheWithExpiration;
import com.tddapps.infrastructure.KeysCache;
import com.tddapps.utils.UtcNowReader;
import com.tddapps.utils.UtcNowReaderImpl;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IocContainerTest {
    @Test
    public void DependenciesAreNotSingletonByDefault(){
        val repository1 = IocContainer.getInstance().Resolve(HeartBeatRepository.class);
        val repository2 = IocContainer.getInstance().Resolve(HeartBeatRepository.class);

        assertFalse(repository1 == repository2);
    }

    @Test
    public void RegisterDependencies(){
        assertTrue(IocContainer.getInstance().Resolve(UtcNowReader.class) instanceof UtcNowReaderImpl);
        assertTrue(IocContainer.getInstance().Resolve(HeartBeatRepository.class) instanceof HeartBeatRepositoryDynamo);
        assertTrue(IocContainer.getInstance().Resolve(NotificationSender.class) instanceof NotificationSenderSns);
        assertTrue(IocContainer.getInstance().Resolve(NotificationSenderStatus.class) instanceof NotificationSenderSns);
        assertTrue(IocContainer.getInstance().Resolve(SettingsReader.class) instanceof EnvironmentSettingsReader);
        assertTrue(IocContainer.getInstance().Resolve(HeartBeatNotificationBuilder.class) instanceof SingleNotificationBuilder);

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
    public void RegistersInMemoryKeysCacheWithExpirationAsASingleton(){
        assertTrue(IocContainer.getInstance().Resolve(KeysCache.class) instanceof InMemoryKeysCacheWithExpiration);

        val cache1 = IocContainer.getInstance().Resolve(KeysCache.class);
        val cache2 = IocContainer.getInstance().Resolve(KeysCache.class);

        assertTrue(cache1 == cache2);
    }

    @Test
    public void RegistersActions(){
        assertNotNull(IocContainer.getInstance().Resolve(NotificationCalculatorAction.class));
    }
}
