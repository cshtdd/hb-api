#!/usr/bin/bash

INPUT_ENDPOINT=$1
INPUT_PROTOCOL=$2
TOPIC_NAME=$3

if [[ $1 ]]; then
    INPUT_ENDPOINT=$1
else
    echo "ERROR: missing endpoint"
    echo ""
    echo "Usage:"
    echo "------"
    echo "create_subscription.sh <endpoint> <email|sms> [snsTopicName]"
    echo "------"
    echo "example 1:"
    echo "create_subscription.sh test@test.com email my-topic"
    echo "example 2:"
    echo "create_subscription.sh +17861234567 sms my-topic"
    exit 1
fi

if [[ "$2" == "email" || "$2" == "sms" ]]; then
    INPUT_PROTOCOL="$2"
else
    echo "ERROR: missing protocol"
    echo ""
    echo "Usage:"
    echo "------"
    echo "create_subscription.sh <endpoint> <email|sms> [snsTopicName]"
    echo "------"
    echo "example 1:"
    echo "create_subscription.sh test@test.com email my-topic"
    echo "example 2:"
    echo "create_subscription.sh +17861234567 sms my-topic"
    exit 1
fi

if [[ $3 ]]; then
    TOPIC_NAME=$3
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
