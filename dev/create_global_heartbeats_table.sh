#!/bin/bash

TABLE_NAME=hb-api-dev-heartbeats
REGION_1=us-east-1
REGION_2=us-west-2

if [[ $1 ]]; then
    TABLE_NAME=$1
fi

if [[ $2 ]]; then
    REGION_1=$2
fi

if [[ $3 ]]; then
    REGION_2=$3
fi

echo "INFO: creating global table: ${TABLE_NAME} in regions: [${REGION_1}, ${REGION_2}]"

# echo "DEBUG: deleting hosts from ${TABLE_NAME}[${REGION_1}]"
aws dynamodb scan --table-name ${TABLE_NAME} --region ${REGION_1} \
    | jq -r '.Items[].host_id.S' \
    | xargs -I{} sh $(dirname $0)/delete_host.sh {} ${REGION_1} ${TABLE_NAME}

# echo "DEBUG: deleting hosts from ${TABLE_NAME}[${REGION_2}]"
aws dynamodb scan --table-name ${TABLE_NAME} --region ${REGION_2} \
    | jq -r '.Items[].host_id.S' \
    | xargs -I{} sh $(dirname $0)/delete_host.sh {} ${REGION_2} ${TABLE_NAME}

# echo "DEBUG: creating global table"
aws dynamodb create-global-table \
  --global-table-name ${TABLE_NAME} \
  --replication-group RegionName=${REGION_1} RegionName=${REGION_2} \
  --region ${REGION_1}
