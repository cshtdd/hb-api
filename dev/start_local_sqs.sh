#!/bin/bash

pip install --user localstack awscli-local
docker pull localstack/localstack

#docker rm -f localstack_main
#
#docker run -it -d --rm \
#  -e LOCALSTACK_HOSTNAME="localhost" \
#  --name localstack_main \
#  -p 8080:8080 -p 8081:8081 \
#  -p 443:443 -p 4567-4609:4567-4609 \
#  "localstack/localstack"

#sls deploy --stage local