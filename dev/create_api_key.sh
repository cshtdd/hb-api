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
    echo "create_api_key.sh <apiKeyName> [region1] [region2]"
    echo "------"
    exit 1
fi

if [[ $2 ]]; then
    REGION_1=$2
fi

if [[ $3 ]]; then
    REGION_2=$3
fi

# echo "DEBUG: creating key: ${API_KEY_NAME} in regions: [${REGION_1}, ${REGION_2}]"

USAGE_PLAN_ID_1=$(aws apigateway get-usage-plans --region ${REGION_1} | jq -r '.items | .[] | .id' | head -1)
# echo "DEBUG: usagePlanId[${REGION_1}]: ${USAGE_PLAN_ID_1}"

USAGE_PLAN_ID_2=$(aws apigateway get-usage-plans --region ${REGION_2} | jq -r '.items | .[] | .id' | head -1)
# echo "DEBUG: usagePlanId[${REGION_2}]: ${USAGE_PLAN_ID_2}"

API_KEY_ID=$(aws apigateway create-api-key --enabled --name ${API_KEY_NAME} --region ${REGION_1} | jq -r '.id')
# echo "DEBUG: apiKeyId[${REGION_1}]: ${API_KEY_ID}"

KEY_CREATION_RESULT=$(aws apigateway create-usage-plan-key --key-type "API_KEY" --usage-plan-id "${USAGE_PLAN_ID_1}" --key-id "${API_KEY_ID}" --region ${REGION_1})
# echo "DEBUG: created key:${API_KEY_NAME} region:${REGION_1}"

API_KEY_VALUE=$(aws apigateway get-api-key --api-key "${API_KEY_ID}" --include-value --region ${REGION_1} | jq -r '.value')

TMP_KEY_FILE=$(mktemp)
bash -c "cat <<EOF > ${TMP_KEY_FILE}
Enabled,Name,key,UsageplanIds
true,${API_KEY_NAME},${API_KEY_VALUE},${USAGE_PLAN_ID_2}
EOF"

KEY_IMPORT_RESULT=$(aws apigateway import-api-keys --body file://${TMP_KEY_FILE} --format csv --region ${REGION_2})
# echo "DEBUG: created key:${API_KEY_NAME} region:${REGION_2}"

rm ${TMP_KEY_FILE}

echo ${API_KEY_VALUE}
