#!/usr/bin/bash

HOST_ID=$1
REGION=$2
TABLE_NAME="hb-api-dev-heartbeats"
SET_TEST_BEFORE_DELETION=1

if [[ -z $1 ]]; then
    echo "ERROR: missing hostId"
    echo ""
    echo "Usage:"
    echo "------"
    echo "delete_host.sh <hostId> <region> [dynamoTableName] [--skip-set-test]"
    exit 1
fi

if [[ -z $2 ]]; then
    echo "ERROR: missing region"
    echo ""
    echo "Usage:"
    echo "------"
    echo "delete_host.sh <hostId> <region> [dynamoTableName] [--skip-set-test]"
    exit 1
fi

if [[ $3 ]]; then
    TABLE_NAME=$3
fi

if [[ "$4" == "--skip-set-test" ]]; then
    SET_TEST_BEFORE_DELETION=0
fi

echo "INFO: Deleting hostId:${HOST_ID} from tableName:${TABLE_NAME}[${REGION}]"

TMP_KEY_FILE=$(mktemp)
bash -c "cat <<EOF > ${TMP_KEY_FILE}
{ \"host_id\": {\"S\": \"${HOST_ID}\"} }
EOF"
# echo "DEBUG: TMP_KEY_FILE=${TMP_KEY_FILE}\n`cat ${TMP_KEY_FILE}`\n"

TMP_VALUE_FILE=$(mktemp)
bash -c "cat <<EOF > ${TMP_VALUE_FILE}
{ \":t\": { \"N\": \"1\" } }
EOF"
# echo "DEBUG: TMP_VALUE_FILE=${TMP_VALUE_FILE}\n`cat ${TMP_VALUE_FILE}`\n"

if [[ ${SET_TEST_BEFORE_DELETION} -eq 1 ]]; then
  # echo "DEBUG: updating test attribute"
  aws dynamodb update-item --table-name "${TABLE_NAME}" \
    --key file://${TMP_KEY_FILE} --update-expression "SET test = :t" \
    --expression-attribute-values file://${TMP_VALUE_FILE} \
    --region ${REGION}
fi

# echo "DEBUG: deleting item"
aws dynamodb delete-item --table-name "${TABLE_NAME}" \
    --key file://${TMP_KEY_FILE} --region ${REGION}

rm ${TMP_KEY_FILE}
rm ${TMP_VALUE_FILE}

# echo "DEBUG: OK"
