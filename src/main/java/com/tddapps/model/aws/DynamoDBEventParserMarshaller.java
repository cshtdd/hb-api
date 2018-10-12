package com.tddapps.model.aws;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.Record;
import com.amazonaws.services.dynamodbv2.model.StreamRecord;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;

import java.util.List;
import java.util.stream.Collectors;

public class DynamoDBEventParserMarshaller implements DynamoDBEventParser {
    private final DynamoDBMapper mapper;

    public DynamoDBEventParserMarshaller(DynamoDBMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public <T> List<T> readDeletions(DynamodbEvent input, Class<T> clazz) {
        return input.getRecords()
                .stream()
                .filter(DynamoDBEventParserMarshaller::isRecordDeletion)
                .map(Record::getDynamodb)
                .map(StreamRecord::getOldImage)
                .map(r -> mapper.marshallIntoObject(clazz, r))
                .collect(Collectors.toList());
    }

    @Override
    public <T> List<T> readInsertions(DynamodbEvent input, Class<T> clazz) {
        return input.getRecords()
                .stream()
                .filter(DynamoDBEventParserMarshaller::isRecordInsertion)
                .map(Record::getDynamodb)
                .map(StreamRecord::getNewImage)
                .map(r -> mapper.marshallIntoObject(clazz, r))
                .collect(Collectors.toList());
    }

    private static boolean isRecordDeletion(DynamodbEvent.DynamodbStreamRecord record){
        return record.getEventName().equals("REMOVE");
    }

    private static boolean isRecordInsertion(DynamodbEvent.DynamodbStreamRecord record){
        return record.getEventName().equals("INSERT");
    }
}
