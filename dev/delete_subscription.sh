#!/bin/bash

INPUT_ENDPOINT=$1
REGION=$2
TOPIC_NAME="hb-api-dev-heartbeat-notifications"

if [[ -z $1 ]]; then
    echo "ERROR: missing endpoint"
    echo ""
    echo "Usage:"
    echo "------"
    echo "delete_subscription.sh <endpoint> <region> [snsTopicName]"
    echo "------"
    echo "example 1:"
    echo "delete_subscription.sh test@test.com us-east-1 my-topic"
    echo "example 2:"
    echo "delete_subscription.sh +17861234567 us-east-1 my-topic"
    exit 1
fi

if [[ -z $2 ]]; then
    echo "ERROR: missing region"
    echo ""
    echo "Usage:"
    echo "------"
    echo "delete_subscription.sh <endpoint> <region> [snsTopicName]"
    echo "------"
    echo "example 1:"
    echo "delete_subscription.sh test@test.com us-east-1 my-topic"
    echo "example 2:"
    echo "delete_subscription.sh +17861234567 us-east-1 my-topic"
    exit 1
fi

if [[ $3 ]]; then
    TOPIC_NAME=$3
fi

echo "INFO: Deleting endpoint:${INPUT_ENDPOINT} from topic:${TOPIC_NAME}[${REGION}]"

TOPIC_ARN=$(aws sns list-topics --region ${REGION} | jq -r ".Topics[].TopicArn | select(. | contains(\":${TOPIC_NAME}\"))")
if [[ "${TOPIC_ARN}" == "" ]]; then
    echo "ERROR: topic not found"
    exit 1
fi

SUBSCRIPTIONS_BY_TOPIC=$(aws sns list-subscriptions-by-topic --topic-arn ${TOPIC_ARN} --region ${REGION})
SUBSCRIPTION_ARN=$(echo $SUBSCRIPTIONS_BY_TOPIC | jq -r ".Subscriptions[] | select(.Endpoint==\"${INPUT_ENDPOINT}\") | .SubscriptionArn")
echo "DEBUG: SubscriptionArn=${SUBSCRIPTION_ARN}"

aws sns unsubscribe --subscription-arn "${SUBSCRIPTION_ARN}" --region ${REGION}
