#!/bin/bash

STACK_NAME=hb-api-dev
REGION=us-east-1

if [[ $1 ]]; then
    STACK_NAME=$1
fi

if [[ $2 ]]; then
    REGION=$2
fi

HEALTH_CHECK_NAME="${STACK_NAME}-${REGION}-${RANDOM}"
echo "INFO: Creating Health Check: ${HEALTH_CHECK_NAME}"

SERVICE_ENDPOINT=$(aws cloudformation describe-stacks --stack-name ${STACK_NAME} --region ${REGION} --query 'Stacks[0].Outputs[?OutputKey==`ServiceEndpoint`].OutputValue' --output text)
# echo "DEBUG: endpoint: ${SERVICE_ENDPOINT}"
# # https://stackoverflow.com/questions/2497215/extract-domain-name-from-url
HEALTH_CHECK_DOMAIN=$(echo ${SERVICE_ENDPOINT} | awk -F[/:] '{print $4}')
# echo "DEBUG: domain: ${HEALTH_CHECK_DOMAIN}"
HEALTH_CHECK_ID=$(aws route53 list-health-checks --region ${REGION} --output text --query 'HealthChecks[?HealthCheckConfig.FullyQualifiedDomainName==`'${HEALTH_CHECK_DOMAIN}'`].Id')
# echo "DEBUG: existing healthCheckId: ${HEALTH_CHECK_ID}"

STAGE=$(aws cloudformation describe-stacks --stack-name ${STACK_NAME} --region ${REGION} --query 'Stacks[0].Tags[?Key==`STAGE`].Value' --output text)
# echo "DEBUG: stage: ${STAGE}"
RESOURCE_PATH="/${STAGE}/v1/status"
# echo "DEBUG: ResourcePath: ${RESOURCE_PATH}"

if [[ "${HEALTH_CHECK_ID}" == "" ]]; then
  echo "INFO: no previous health check found. Creating a new one"
  aws route53 create-health-check --caller-reference ${HEALTH_CHECK_NAME} \
    --health-check-config '{
      "Port": 443,
      "Type": "HTTPS",
      "ResourcePath": "'${RESOURCE_PATH}'",
      "FullyQualifiedDomainName": "'${HEALTH_CHECK_DOMAIN}'",
      "MeasureLatency": true,
      "Regions": ["us-east-1", "us-west-1", "us-west-2"]
    }'
fi

HEALTH_CHECK_ID=$(aws route53 list-health-checks --region ${REGION} --output text --query 'HealthChecks[?HealthCheckConfig.FullyQualifiedDomainName==`'${HEALTH_CHECK_DOMAIN}'`].Id')
# echo "DEBUG: existing healthCheckId: ${HEALTH_CHECK_ID}"

echo "INFO: Updating the existing one ${HEALTH_CHECK_ID} at ${RESOURCE_PATH}, ${HEALTH_CHECK_DOMAIN}"
aws route53 update-health-check --health-check-id ${HEALTH_CHECK_ID} \
  --resource-path ${RESOURCE_PATH} \
  --port 443 \
  --fully-qualified-domain-name ${HEALTH_CHECK_DOMAIN} \
  --failure-threshold 1 \
  --regions "us-east-1" "us-west-1" "us-west-2"
