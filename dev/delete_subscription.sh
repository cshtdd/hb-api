#!/usr/bin/bash

INPUT_ENDPOINT=$1
TOPIC_NAME=$2

if [[ $1 ]]; then
    INPUT_ENDPOINT=$1
else
    echo "ERROR: missing endpoint"
    echo ""
    echo "Usage:"
    echo "------"
    echo "delete_subscription.sh <endpoint> [snsTopicName]"
    echo "------"
    echo "example 1:"
    echo "delete_subscription.sh test@test.com my-topic"
    echo "example 2:"
    echo "delete_subscription.sh +17861234567 my-topic"
    exit 1
fi

if [[ $2 ]]; then
    TOPIC_NAME=$2
else
    TOPIC_NAME="hb-api-dev-heartbeat-notifications"
fi

echo "INFO: Deleting endpoint:${INPUT_ENDPOINT} from topic:${TOPIC_NAME}"

TOPIC_ARN=$(aws sns list-topics | jq -r ".Topics[].TopicArn | select(. | contains(\":${TOPIC_NAME}\"))")
if [[ "${TOPIC_ARN}" == "" ]]; then
    echo "ERROR: topic not found"
    exit 1
fi

SUBSCRIPTIONS_BY_TOPIC=$(aws sns list-subscriptions-by-topic --topic-arn ${TOPIC_ARN})
SUBSCRIPTION_ARN=$(echo $SUBSCRIPTIONS_BY_TOPIC | jq -r ".Subscriptions[] | select(.Endpoint==\"${INPUT_ENDPOINT}\") | .SubscriptionArn")
echo "DEBUG: SubscriptionArn=${SUBSCRIPTION_ARN}"

aws sns unsubscribe --subscription-arn "${SUBSCRIPTION_ARN}"
