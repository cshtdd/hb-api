#!/bin/bash

USAGE_PLAN_ID=`aws apigateway get-usage-plans | jq -r '.items | .[] | .id' | head -1`
#echo ${USAGE_PLAN_ID}

API_KEY_ID=`aws apigateway create-api-key --enabled --name devkey | jq -r '.id'`
#echo ${API_KEY_ID}

ADD_API_TO_USAGE_PLAN_RESULT=`aws apigateway create-usage-plan-key --key-type "API_KEY" --usage-plan-id "${USAGE_PLAN_ID}" --key-id "${API_KEY_ID}"`

API_KEY_VALUE=`aws apigateway get-api-key --api-key "${API_KEY_ID}" --include-value | jq -r '.value'`

echo ${API_KEY_VALUE}