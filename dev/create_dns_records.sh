SUBDOMAIN=$1
TLD=$2
STACKNAME=hb-api-dev
REGION_1=us-east-1
REGION_2=us-west-2

if [[ -z $1 ]]; then
  echo "ERROR: missing subdomain"
  echo ""
  echo "Usage:"
  echo "------"
  echo "create_dns_records.sh <subdomain> <tld> [stackName] [region1] [region2]"
  echo "------"
  echo "example:"
  echo "create_dns_records.sh api myhost.com hb-api-dev us-east-1 us-east-2"
  exit 1
fi

if [[ -z $2 ]]; then
  echo "ERROR: missing tld"
  echo ""
  echo "Usage:"
  echo "------"
  echo "create_dns_records.sh <subdomain> <tld> [stackName] [region1] [region2]"
  echo "------"
  echo "example:"
  echo "create_dns_records.sh api myhost.com hb-api-dev us-east-1 us-east-2"
  exit 1
fi

if [[ $3 ]]; then
    STACKNAME=$3
fi

if [[ $4 ]]; then
    REGION_1=$4
fi

if [[ $5 ]]; then
    REGION_2=$5
fi

DOMAIN_NAME=${SUBDOMAIN}.${TLD}

echo "INFO: Creating domain records ${DOMAIN_NAME} in ${STACKNAME}[${REGION_1}, ${REGION_2}]"

HOSTEDZONE=$(aws route53 list-hosted-zones --query 'HostedZones[?Name==`'${TLD}'.`].Id' --output text)
echo "DEBUG: hostedZone: ${HOSTEDZONE}"

REGION_1_DOMAIN=$(aws cloudformation describe-stacks --stack-name ${STACKNAME} --region ${REGION_1} --query 'Stacks[0].Outputs[?OutputKey==`DomainName`].OutputValue' --output text)
echo "DEBUG: domain1: ${REGION_1_DOMAIN}"

REGION_2_DOMAIN=$(aws cloudformation describe-stacks --stack-name ${STACKNAME} --region ${REGION_2} --query 'Stacks[0].Outputs[?OutputKey==`DomainName`].OutputValue' --output text)
echo "DEBUG: domain2: ${REGION_2_DOMAIN}"

aws route53 change-resource-record-sets \
  --hosted-zone-id ${HOSTEDZONE} \
  --change-batch  '{
      "Changes": [
        {
          "Action": "UPSERT",
          "ResourceRecordSet": {
            "Name": "'${DOMAIN_NAME}'",
            "Type": "CNAME",
            "TTL": 300,
            "SetIdentifier": "'${REGION_1}'",
            "Region": "'${REGION_1}'",
            "ResourceRecords": [
              {
                "Value": "'${REGION_1_DOMAIN}'"
              }
            ]
          }
        },
        {
          "Action": "UPSERT",
          "ResourceRecordSet": {
            "Name": "'${DOMAIN_NAME}'",
            "Type": "CNAME",
            "TTL": 300,
            "SetIdentifier": "'${REGION_2}'",
            "Region": "'${REGION_2}'",
            "ResourceRecords": [
              {
                "Value": "'${REGION_2_DOMAIN}'"
              }
            ]
          }
        }
      ]
    }'