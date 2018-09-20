#!/usr/bin/bash

HOST_ID=$1
TABLE_NAME=$2

if [[ $1 ]]; then
    HOST_ID=$1
else
    echo "Error: missing hostId"
    echo ""
    echo "Usage:"
    echo "------"
    echo "delete_host.sh <hostId> [dynamoTableName]"
    exit 1
fi

if [[ $2 ]]; then
    TABLE_NAME=$2
else
    TABLE_NAME="hb-api-dev-heartbeats"
fi

echo "Deleting hostId:$HOST_ID from tableName:$TABLE_NAME"

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

# echo "DEBUG: updating test attribute"
aws dynamodb update-item --table-name "${TABLE_NAME}" \
  --key file://${TMP_KEY_FILE} --update-expression "SET test = :t" \
  --expression-attribute-values file://$TMP_VALUE_FILE

# echo "DEBUG: deleting item"
aws dynamodb delete-item --table-name "${TABLE_NAME}" --key file://${TMP_KEY_FILE}

rm ${TMP_KEY_FILE}
rm ${TMP_VALUE_FILE}

# echo "DEBUG: OK"
