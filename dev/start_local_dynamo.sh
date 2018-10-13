#!/bin/bash

if [[ "$1" != "" ]]; then
    PORT="$1"
else
    PORT="8000"
fi

ENDPOINT="http://localhost:$PORT"
echo "Dynamo endpoint: ${ENDPOINT}"

echo "Checking Local Dynamo..."
LOCAL_DYNAMO_TABLES=`aws dynamodb list-tables --endpoint-url ${ENDPOINT}`

if [[ ${LOCAL_DYNAMO_TABLES} = *"\"TableNames\""* ]]; then
    echo "Success! Local Dynamo Already Running"
else
    echo "Starting Local Dynamo..."
    sls dynamodb start --stage dev --port ${PORT} &
    sleep 2

    echo "Checking Local Dynamo..."
    LOCAL_DYNAMO_TABLES=`aws dynamodb list-tables --endpoint-url ${ENDPOINT}`
    if [[ ${LOCAL_DYNAMO_TABLES} = *"\"TableNames\""* ]]; then
        echo "Success! Local Dynamo Running"
    else
        echo "Error! Local Dynamo Not Running"
        exit 1
    fi
fi
