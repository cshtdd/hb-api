#!/bin/bash

BUCKET=$1
REGION=$2
SERVICE=hb-api
STAGE=dev

if [[ -z $1 ]]; then
    echo "ERROR: missing bucket"
    echo ""
    echo "Usage:"
    echo "------"
    echo "init_terraform.sh <state_bucket_name> <region> [stage]"
    exit 1
fi

if [[ -z $2 ]]; then
    echo "ERROR: missing region"
    echo ""
    echo "Usage:"
    echo "------"
    echo "init_terraform.sh <state_bucket_name> <region> [stage]"
    exit 1
fi

if [[ $3 ]]; then
    STAGE=$3
fi

terraform init \
     -backend-config "bucket=${BUCKET}" \
     -backend-config "region=${REGION}" \
     -backend-config "key=${SERVICE}/${STAGE}/terraform.tfstate"