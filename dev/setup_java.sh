#!/bin/bash


# desired java versions
JAVA_VERSION_8=8.0.192-zulu


# determine if this program is being sourced
skip=false
[[ $- == *i* ]] || skip=true
[[ interactive ]] || skip=true
[[ ! $(shopt -q login_shell) ]] || skip=true

if [[ $skip == true ]]; then
  echo "This file needs to be run with sourced"
  echo "so that sdkman can be found. e.g:"
  echo "   source setup_elastic_search_java.sh"
  exit 1
fi


# installing java versions
if [[ $1 == '--skip-install' ]]; then
  echo "Skipping installation"
else
  echo "Install Java ${JAVA_VERSION_8}"
  sdk install java $JAVA_VERSION_8
fi


# reading all the java version installation paths
JAVA_HOME_8="$HOME/.sdkman/candidates/java/${JAVA_VERSION_8}"
echo "JAVA_HOME_8='${JAVA_HOME_8}'"


# generate java variable setup script
cat > /tmp/scorbot-proxy-java-vars <<EOL
export JAVA8_HOME=${JAVA_HOME_8}
export RUNTIME_JAVA_HOME=${JAVA_HOME_8}

sdk use java ${JAVA_VERSION_8}
EOL


# loading the java variables
source /tmp/scorbot-proxy-java-vars
