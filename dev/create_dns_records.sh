SUBDOMAIN=$1
TLD=$2
STACKNAME=hb-api-dev
REGION=us-east-1

if [[ -z $1 ]]; then
  echo "ERROR: missing subdomain"
  echo ""
  echo "Usage:"
  echo "------"
  echo "create_dns_records.sh <subdomain> <tld> [stackName] [region]"
  echo "------"
  echo "example:"
  echo "create_dns_records.sh api myhost.com hb-api-dev us-east-1"
  exit 1
fi

if [[ -z $2 ]]; then
  echo "ERROR: missing tld"
  echo ""
  echo "Usage:"
  echo "------"
  echo "create_dns_records.sh <subdomain> <tld> [stackName] [region]"
  echo "------"
  echo "example:"
  echo "create_dns_records.sh api myhost.com hb-api-dev us-east-1"
  exit 1
fi

if [[ $3 ]]; then
    STACKNAME=$3
fi

if [[ $4 ]]; then
    REGION=$4
fi

DOMAIN_NAME=${SUBDOMAIN}.${TLD}

echo "INFO: Creating domain records ${DOMAIN_NAME} in ${STACKNAME}[${REGION}]"

HOSTEDZONE=$(aws route53 list-hosted-zones --query 'HostedZones[?Name==`'${TLD}'.`].Id' --output text)
echo "DEBUG: hostedZone: ${HOSTEDZONE}"

DNS_DOMAIN=$(aws cloudformation describe-stacks --stack-name ${STACKNAME} --region ${REGION} --query 'Stacks[0].Outputs[?OutputKey==`DomainName`].OutputValue' --output text)
echo "DEBUG: domain: ${DNS_DOMAIN}"

SERVICE_ENDPOINT=$(aws cloudformation describe-stacks --stack-name ${STACKNAME} --region ${REGION} --query 'Stacks[0].Outputs[?OutputKey==`ServiceEndpoint`].OutputValue' --output text)
echo "DEBUG: endpoint: ${SERVICE_ENDPOINT}"
# https://stackoverflow.com/questions/2497215/extract-domain-name-from-url
HEALTH_CHECK_DOMAIN=$(echo $SERVICE_ENDPOINT | awk -F[/:] '{print $4}')
echo "DEBUG: healthCheckDomain: ${HEALTH_CHECK_DOMAIN}"
HEALTH_CHECK_ID=$(aws route53 list-health-checks --region ${REGION} --output text --query 'HealthChecks[?HealthCheckConfig.FullyQualifiedDomainName==`'${HEALTH_CHECK_DOMAIN}'`].Id')
echo "DEBUG: existing healthCheckId: ${HEALTH_CHECK_ID}"

aws route53 change-resource-record-sets \
  --hosted-zone-id ${HOSTEDZONE} \
  --change-batch  '{
      "Changes": [
        {
          "Action": "UPSERT",
          "ResourceRecordSet": {
            "Name": "'${DOMAIN_NAME}'",
            "Type": "CNAME",
            "TTL": 60,
            "SetIdentifier": "'${REGION}'",
            "Region": "'${REGION}'",
            "HealthCheckId": "'${HEALTH_CHECK_ID}'",
            "ResourceRecords": [
              {
                "Value": "'${DNS_DOMAIN}'"
              }
            ]
          }
        }
      ]
    }'