package com.tddapps.model.aws;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.tddapps.model.DalException;
import com.tddapps.model.HeartBeat;
import com.tddapps.model.HeartBeatRepository;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class HeartBeatRepositoryDynamo implements HeartBeatRepository {
    private final DynamoDBMapperFactory mapperFactory;

    public HeartBeatRepositoryDynamo(DynamoDBMapperFactory mapperFactory){
        this.mapperFactory = mapperFactory;
    }

    @Override
    public void Save(HeartBeat heartBeat) throws DalException {
        try {
            mapperFactory.getMapper().save(heartBeat);
        } catch (AmazonClientException e){
            log.error("HeartBeat Save Error", e);
            throw new DalException(e.getMessage());
        }
    }

    @Override
    public HeartBeat[] All() throws DalException {
        try {
            return mapperFactory.getMapper()
                    .scan(HeartBeat.class, new DynamoDBScanExpression())
                    .toArray(new HeartBeat[0]);
        } catch (AmazonClientException e){
            log.error("HeartBeat All Error", e);
            throw new DalException(e.getMessage());
        }
    }
}
