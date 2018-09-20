#!/usr/bin/bash

INPUT_ENDPOINT=$1
INPUT_PROTOCOL=email
TOPIC_NAME=$2

if [[ $1 ]]; then
    INPUT_ENDPOINT=$1
else
    echo "ERROR: missing endpoint"
    echo ""
    echo "Usage:"
    echo "------"
    echo "create_subscription.sh <endpoint> [snsTopicName]"
    exit 1
fi

if [[ $2 ]]; then
    TOPIC_NAME=$2
else
    TOPIC_NAME="hb-api-dev-heartbeat-notifications"
fi

echo "INFO: Subscribing ${INPUT_PROTOCOL}:${INPUT_ENDPOINT} to topic:${TOPIC_NAME}"

TOPIC_ARN=$(aws sns list-topics | jq -r ".Topics[].TopicArn | select(. | contains(\":${TOPIC_NAME}\"))")
if [[ "${TOPIC_ARN}" == "" ]]; then
    echo "ERROR: topic not found"
    exit 1
fi

# echo "DEBUG: TOPIC_ARN=${TOPIC_ARN}" 
aws sns subscribe --topic-arn ${TOPIC_ARN} \
    --protocol ${INPUT_PROTOCOL} --notification-endpoint ${INPUT_ENDPOINT}
