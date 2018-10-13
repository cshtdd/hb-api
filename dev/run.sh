#!/bin/bash

if [[ "$1" == "--debug" ]]; then
    DEBUG_INFO="-d 5858 "
else
    DEBUG_INFO=""
fi

mvn clean install -DskipTests && \
    sls sam export --output .sam_template.yml --dynamoDbEndpointOverride="http://docker.for.mac.localhost:8000" && \
    (trap 'kill 0' SIGINT; \
        sls dynamodb start --stage dev & \
        ( \
            sam local start-api ${DEBUG_INFO} -t .sam_template.yml \
        ) \
    )
