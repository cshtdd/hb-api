#!/usr/bin/bash

mvn clean install && \
    sls sam export --output .sam_template.yml && \
    sam local start-api -t .sam_template.yml