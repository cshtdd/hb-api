# hb-api  

Heartbeat api backed by AWS lambdas

# Development  

## Building the Code  

```bash
mvn clean install
```

## Running the Tests  

```bash
mvn clean test
```

## Create API Keys  

```bash
sh ./dev/create_api_key.sh
```

## Deployment  

```bash
sh ./dev/deploy.sh
```

## API Validation  

Prerequisite, store the api key in a variable

```bash
 HB_API_KEY=SUPER_SECRET_KEY
```

The status page should be up

```bash
curl -w "\n%{http_code}\n" https://hbapidev.tddapps.com/v1/status
```

Posting a heartbeat should succeed

```bash
curl -w "\n%{http_code}\n" -H "x-api-key: $HB_API_KEY" -d '{"hostId": "testHost1"}' -X POST https://hbapidev.tddapps.com/v1/hearbeat
```

## Create the Custom Domain
  
1- Manually create a certificate using AWS ACM. They are free
2- Follow the steps from the [Serverless Domain Manager plugin](https://github.com/amplify-education/serverless-domain-manager)
3- Run the following command

```bash
sls create_domain
```

4- Deploy the application
4.1- Make sure there is a basepath in the custom domain. If not, follow the steps in this [Github Issue](https://github.com/amplify-education/serverless-domain-manager/issues/57) to correct it. There is some buggy behavior between CloudFormation and the domain manager plugin
5- Create a `CNAME` record in the DNS provider for the cloudfront domain

## Run it locally  

**NOTE** although this will run Api Gateway locally. Dynamo and SNS will still be the real AWS resources. I still need to figure out how to run all the things locally.  

1- Install the [Serverless DynamoDb Plugin](https://github.com/99xt/serverless-dynamodb-local)
1.1- Install DynamoDB local `sls dynamodb install`  
2- Install the [Serverless Sam Plugin](https://github.com/SAPessi/serverless-sam)
3- Install the [aws sam cli](https://github.com/awslabs/aws-sam-cli/blob/develop/docs/installation.rst)  

```bash
sh ./dev/run.sh
```

# Usage  

Launch the crontab editor

```bash
crontab -e
```

Add the following lines to the file

```bash
HB_API_KEY=SUPER_SECRET_KEY

*/5 * * * * curl -i -H "x-api-key: $HB_API_KEY" -d '{"hostId": "CHANGEME"}' -X POST https://hbapidev.tddapps.com/v1/hearbeat | logger -p local0.notice
```

Verify the task is scheduled

```bash
crontab -l
```

Monitor the task execution

```bash
watch -n1 grep CRON /var/log/syslog
```

The task output should also be in `syslog`

```bash
tail -f /var/log/syslog
```

# Code Architecture

## `handlers` package  
Should be placed in `com.tddapps.handlers`. Aim for these to have almost no code. These classes have some knowledge of the serverless framework.  

`BaseHttpJsonHandler` will help reduce the boilerplate code for handlers. Most of them should inherit from it. The unit tests for these classes will be limited to guarantee they return the correct kind.  

## `controllers` package  

Contains some middleware to reduce boilerplate code around request parsing and response construction. New functionality will rarely require any changes in this area. This layer might be removed altogether and merged into the `handlers.infrastructure` package at some point.  

## `actions` package  

The actual implementation of the functions will live under the `com.tddapps.actions` package. This will receive and return boundary objects. These classes will be framework agnostic.  

## `model` package  

Where most of the code and business logic will live. This package should be independent of implementation details.

## `model.aws` package  

The concrete implementation of many of the interfaces. These classes can only be tested with integration tests. When the day comes the cloud provider needs to be replaced, only the equivalent for these classes should need to be implemented.  
