#!/bin/bash

API_KEY_NAME=$1
REGION_1=us-east-1
REGION_2=us-west-2

if [[ $1 ]]; then
    API_KEY_NAME=$1
else
    echo "ERROR: missing api key name"
    echo ""
    echo "Usage:"
    echo "------"
    echo "delete_api_key.sh <apiKeyName> [region1] [region2]"
    echo "------"
    exit 1
fi

if [[ $2 ]]; then
    REGION_1=$2
fi

if [[ $3 ]]; then
    REGION_2=$3
fi

USAGE_PLAN_ID_1=$(aws apigateway get-usage-plans --region ${REGION_1} | jq -r '.items | .[] | .id' | head -1)
# echo "DEBUG: usagePlanId[${REGION_1}]: ${USAGE_PLAN_ID_1}"

USAGE_PLAN_ID_2=$(aws apigateway get-usage-plans --region ${REGION_2} | jq -r '.items | .[] | .id' | head -1)
# echo "DEBUG: usagePlanId[${REGION_2}]: ${USAGE_PLAN_ID_2}"

API_KEY_ID_1=$(aws apigateway get-api-keys --name-query ${API_KEY_NAME} --region ${REGION_1} | jq -r '.items[0].id')
# echo "DEBUG: apiKeyId[${REGION_1}]: ${API_KEY_ID_1}"

API_KEY_ID_2=$(aws apigateway get-api-keys --name-query ${API_KEY_NAME} --region ${REGION_2} | jq -r '.items[0].id')
# echo "DEBUG: apiKeyId[${REGION_2}]: ${API_KEY_ID_2}"

aws apigateway delete-api-key --api-key ${API_KEY_ID_1} --region ${REGION_1}
echo "INFO: Deleted apiKeyId[${REGION_1}]: ${API_KEY_ID_1}"

aws apigateway delete-api-key --api-key ${API_KEY_ID_2} --region ${REGION_2}
echo "INFO: Deleted apiKeyId[${REGION_2}]: ${API_KEY_ID_2}"
