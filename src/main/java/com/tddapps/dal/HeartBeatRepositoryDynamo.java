package com.tddapps.dal;

import com.amazonaws.AmazonClientException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HeartBeatRepositoryDynamo implements HeartBeatRepository {
    private static final Logger LOG = LogManager.getLogger(HeartBeatRepositoryDynamo.class);

    private final DynamoDBMapperFactory mapperFactory;

    public HeartBeatRepositoryDynamo(DynamoDBMapperFactory mapperFactory){
        this.mapperFactory = mapperFactory;
    }

    @Override
    public void Save(HeartBeat heartBeat) throws DalException{
        try {
            mapperFactory.getMapper().save(heartBeat);
        } catch (AmazonClientException e){
            LOG.error("HeartBeat Save Error", e);
            throw new DalException(e.getMessage());
        }
    }
}
