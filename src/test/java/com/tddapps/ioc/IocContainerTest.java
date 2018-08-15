package com.tddapps.ioc;

import com.tddapps.dal.DynamoDBMapperFactory;
import com.tddapps.dal.DynamoDBMapperFactoryWithTablePrefix;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class IocContainerTest {
    @Test
    public void RegisterDependencies(){
        assertTrue(IocContainer.getInstance().Resolve(DynamoDBMapperFactory.class) instanceof DynamoDBMapperFactoryWithTablePrefix);
    }
}
