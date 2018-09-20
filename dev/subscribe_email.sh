#!/usr/bin/bash

INPUT_EMAIL=$1
TOPIC_NAME=$2

if [[ $1 ]]; then
    INPUT_EMAIL=$1
else
    echo "ERROR: missing email"
    echo ""
    echo "Usage:"
    echo "------"
    echo "subscribe_email.sh <email> [snsTopicName]"
    exit 1
fi

if [[ $2 ]]; then
    TOPIC_NAME=$2
else
    TOPIC_NAME="hb-api-dev-heartbeat-notifications"
fi

echo "INFO: Subscribing email:${INPUT_EMAIL} to topic:${TOPIC_NAME}"

TOPIC_ARN=$(aws sns list-topics | jq -r ".Topics[].TopicArn | select(. | contains(\":${TOPIC_NAME}\"))")
if [[ "${TOPIC_ARN}" == "" ]]; then
    echo "ERROR: topic not found"
    exit 1
fi

# echo "DEBUG: TOPIC_ARN=${TOPIC_ARN}" 
aws sns subscribe --topic-arn $TOPIC_ARN \
    --protocol email --notification-endpoint $INPUT_EMAIL
