#!/usr/bin/bash

mvn clean integration-test && \
    mvn clean install && \
    sls deploy
