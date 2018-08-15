package com.tddapps.ioc;

import com.tddapps.actions.HeartBeatPostAction;
import com.tddapps.actions.StatusGetAction;
import com.tddapps.dal.DynamoDBMapperFactory;
import com.tddapps.dal.DynamoDBMapperFactoryWithTablePrefix;
import com.tddapps.dal.HeartBeatRepository;
import com.tddapps.dal.HeartBeatRepositoryDynamo;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class IocContainerTest {
    @Test
    public void RegisterDependencies(){
        assertTrue(IocContainer.getInstance().Resolve(DynamoDBMapperFactory.class) instanceof DynamoDBMapperFactoryWithTablePrefix);
        assertTrue(IocContainer.getInstance().Resolve(HeartBeatRepository.class) instanceof HeartBeatRepositoryDynamo);
    }

    @Test
    public void RegistersActions(){
        assertNotNull(IocContainer.getInstance().Resolve(HeartBeatPostAction.class));
        assertNotNull(IocContainer.getInstance().Resolve(StatusGetAction.class));
    }
}
