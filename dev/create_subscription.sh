#!/usr/bin/bash

INPUT_ENDPOINT=$1
INPUT_PROTOCOL=$2
REGION=$3
TOPIC_NAME="hb-api-dev-heartbeat-notifications"

if [[ -z $1 ]]; then
    echo "ERROR: missing endpoint"
    echo ""
    echo "Usage:"
    echo "------"
    echo "create_subscription.sh <endpoint> <email|sms> <region> [snsTopicName]"
    echo "------"
    echo "example 1:"
    echo "create_subscription.sh test@test.com email us-east-1 my-topic"
    echo "example 2:"
    echo "create_subscription.sh +17861234567 sms us-east-1 my-topic"
    exit 1
fi

if [[ -z $2 || ("$2" != "email" && "$2" != "sms") ]]; then
    echo "ERROR: missing/invalid protocol"
    echo ""
    echo "Usage:"
    echo "------"
    echo "create_subscription.sh <endpoint> <email|sms> <region> [snsTopicName]"
    echo "------"
    echo "example 1:"
    echo "create_subscription.sh test@test.com email us-east-1 my-topic"
    echo "example 2:"
    echo "create_subscription.sh +17861234567 sms us-east-1 my-topic"
    exit 1
fi

if [[ -z $3 ]]; then
    echo "ERROR: missing region"
    echo ""
    echo "Usage:"
    echo "------"
    echo "create_subscription.sh <endpoint> <email|sms> <region> [snsTopicName]"
    echo "------"
    echo "example 1:"
    echo "create_subscription.sh test@test.com email us-east-1 my-topic"
    echo "example 2:"
    echo "create_subscription.sh +17861234567 sms us-east-1 my-topic"
    exit 1
fi

if [[ $4 ]]; then
    TOPIC_NAME=$4
fi

echo "INFO: Subscribing ${INPUT_PROTOCOL}:${INPUT_ENDPOINT} to topic:${TOPIC_NAME}[${REGION}]"

TOPIC_ARN=$(aws sns list-topics --region ${REGION} | jq -r ".Topics[].TopicArn | select(. | contains(\":${TOPIC_NAME}\"))")
if [[ "${TOPIC_ARN}" == "" ]]; then
    echo "ERROR: topic not found"
    exit 1
fi

# echo "DEBUG: TOPIC_ARN=${TOPIC_ARN}" 
aws sns subscribe --topic-arn ${TOPIC_ARN} --region ${REGION} \
    --protocol ${INPUT_PROTOCOL} --notification-endpoint ${INPUT_ENDPOINT}
