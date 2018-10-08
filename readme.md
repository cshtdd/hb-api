# hb-api  

Heartbeat api backed by AWS lambdas

# Index  

[Code Architecture](docs/code-architecture.md)  

# Development  

## Prerequisites  

- [aws cli](https://docs.aws.amazon.com/cli/latest/userguide/installing.html)  
- [aws sam cli](https://github.com/awslabs/aws-sam-cli/blob/develop/docs/installation.rst)  
- Maven
- Node
- `npm install`

## Building the Code  

```bash
mvn clean install
```

## Running the Tests  

```bash
mvn clean test
```

## Running the Integration Tests  

```bash
mvn clean integration-test
```

## Dependency vulnerability scan  

```bash
npm run osa
```

## Run it locally  

```bash
npm start
```

### Verify everything is running locally  

```bash
lsof -Pn -i4 | grep -E ':8000|:3000'
```

## Local Debugging with IntelliJ  

1- Create a [Remote JVM Configuration](https://www.jetbrains.com/help/idea/run-debug-configuration-remote-debug.html) for port `localhost:5858`. Make sure to load the `hb-api` module classpath.  
2- Place a breakpoint in the desired location  
3- Run `npm run debug`  
4- Invoke the lambda  
5- Click the Debug button next to the newly created configuration  

## Deployment  

### Prerequisites  
  
1- Make sure your domain is managed by Route53  
2- Manually create a certificate using AWS ACM. They are free. The certificate can allow any subdomain `*.mydomain.com`  

### Deployment process  

```bash
npm run deploy
```

**Note**: Make sure there is a basepath in the custom domain. If not, follow the steps in this [Github Issue](https://github.com/amplify-education/serverless-domain-manager/issues/57) to correct it. There is some buggy behavior between CloudFormation and the domain manager plugin

### Make sure to create the Global HeartBeats table  

Create the global dynamo tables after the first deployment  

```bash
sh ./dev/create_global_heartbeats_table.sh
```

## Create API Keys  

```bash
sh ./dev/create_api_key.sh server1
```

## Delete API Keys  

```bash
sh ./dev/delete_api_key.sh server1
```

## API Validation  

Prerequisite, store the api key in a variable

```bash
 HB_API_KEY=SUPER_SECRET_KEY
```

The status page should be up

```bash
curl -w "\n%{http_code}\n" https://hbapidev.cshtdd.com/v1/status
```

Posting a heartbeat should succeed

```bash
curl -w "\n%{http_code}\n" -H "x-api-key: $HB_API_KEY" -d '{"hostId": "testHost1"}' -X POST https://hbapidev.cshtdd.com/v1/hearbeat
```

# Usage  

Launch the crontab editor

```bash
crontab -e
```

Add the following lines to the file

```bash
HB_API_KEY=SUPER_SECRET_KEY

*/5 * * * * curl -i -H "x-api-key: $HB_API_KEY" -d '{"hostId": "CHANGEME"}' -X POST https://hbapidev.cshtdd.com/v1/hearbeat | logger -p local0.notice
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
