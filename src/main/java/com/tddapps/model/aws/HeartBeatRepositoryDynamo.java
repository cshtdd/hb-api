package com.tddapps.model.aws;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.tddapps.model.DalException;
import com.tddapps.model.HeartBeat;
import com.tddapps.model.HeartBeatRepository;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.tddapps.utils.ArrayBatchExtensions.Split;

@Log4j2
public class HeartBeatRepositoryDynamo implements HeartBeatRepository {
    public static final int DYNAMO_MAX_BATCH_SIZE = 25;
    private final DynamoDBMapper mapper;

    public HeartBeatRepositoryDynamo(DynamoDBMapper mapper){
        this.mapper = mapper;
    }

    @Override
    public void Save(HeartBeat[] heartBeats) throws DalException {
        try {
            val batches = Split(heartBeats, DYNAMO_MAX_BATCH_SIZE);
            for (int i = 0; i < batches.length; i++) {
                log.debug(String.format("Save; batchIndex:%s, batchCount:%s", i, batches.length));
                mapper.batchWrite(Arrays.asList(batches[i]), new ArrayList<HeartBeat>());
            }
        } catch (AmazonClientException e){
            log.debug("HeartBeat Save Error", e);
            throw new DalException(e.getMessage());
        }
    }

    HeartBeat[] All() throws DalException {
        try {
            return mapper
                    .scan(HeartBeat.class, new DynamoDBScanExpression())
                    .toArray(new HeartBeat[0]);
        } catch (AmazonClientException e){
            log.debug("HeartBeat All Error", e);
            throw new DalException(e.getMessage());
        }
    }

    @Override
    public HeartBeat[] ReadOlderThan(String expirationMinuteUtc, long ttl, int maxCount) throws DalException {
        try {
            val query = new DynamoDBQueryExpression<HeartBeat>()
                    .withLimit(maxCount)
                    .withIndexName("ExpirationMinuteIndex")
                    .withConsistentRead(false)
                    .withKeyConditionExpression("#name1 = :val1 AND #name2 < :val2")
                    .withExpressionAttributeNames(new HashMap<String, String>(){{
                        put("#name1", "expiration_minute_utc");
                        put("#name2", "ttl");
                    }})
                    .withExpressionAttributeValues(new HashMap<String, AttributeValue>(){{
                        put(":val1", new AttributeValue().withS(expirationMinuteUtc));
                        put(":val2", new AttributeValue().withN(String.format("%d", ttl)));
                    }});

            return mapper.queryPage(HeartBeat.class, query)
                    .getResults()
                    .toArray(new HeartBeat[0]);
        } catch (AmazonClientException e){
            log.debug("HeartBeat ReadOlderThan Error", e);
            throw new DalException(e.getMessage());
        }
    }
}
