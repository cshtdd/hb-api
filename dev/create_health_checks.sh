STACKNAME=hb-api-dev
REGION=us-east-1

if [[ $1 ]]; then
    STACKNAME=$1
fi

if [[ $2 ]]; then
    REGION=$2
fi

HEALTH_CHECK_NAME="${STACKNAME}-${REGION}"

echo "INFO: Creating Health Check: ${HEALTH_CHECK_NAME}"

REGION_DOMAIN=$(aws cloudformation describe-stacks --stack-name ${STACKNAME} --region ${REGION} --query 'Stacks[0].Outputs[?OutputKey==`DomainName`].OutputValue' --output text)
echo "DEBUG: domain: ${REGION_DOMAIN}"

STAGE=$(aws cloudformation describe-stacks --stack-name ${STACKNAME} --region ${REGION} --query 'Stacks[0].Tags[?Key==`STAGE`].Value' --output text)
echo "DEBUG: stage: ${STAGE}"

HEALTH_CHECK_ID=$(aws route53 list-health-checks --region ${REGION} --output text --query 'HealthChecks[?HealthCheckConfig.FullyQualifiedDomainName==`'${REGION_DOMAIN}'`].Id')
echo "DEBUG: existing healthCheckId: ${HEALTH_CHECK_ID}"

RESOURCE_PATH="/${STAGE}/v1/status"
echo "DEBUG: ResourcePath: ${RESOURCE_PATH}"

if [[ "${HEALTH_CHECK_ID}" == "" ]]; then
  echo "INFO: no previous health check found. Creating a new one"
  aws route53 create-health-check --caller-reference ${HEALTH_CHECK_NAME} \
    --health-check-config '{
      "Port": 443,
      "Type": "HTTPS",
      "ResourcePath": "'${RESOURCE_PATH}'",
      "FullyQualifiedDomainName": "'${REGION_DOMAIN}'",
      "MeasureLatency": true
    }'
else
  echo "INFO: Found previous health check. Updating the existing one"
fi
