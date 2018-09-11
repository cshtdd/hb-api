#!/usr/bin/bash

mvn clean install && \
    sls deploy
