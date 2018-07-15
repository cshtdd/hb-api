# hb-api  

Heartbeat api backed by AWS lambdas

# Development  

## Building the Code  

```bash
mvn clean install
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

# Code Architecture

Function handlers should be placed in `com.tddapps.handlers`. Aim for these to have no code. These classes have some knowledge of the serverless framework. Most likely, they won't be unit tested.  
Function handlers will directly invoke classes from the `com.tddapps.controllers` package. This is where our application logic will start to take place. Most of the controllers will have 90% of the same code. Probably we will only have one or two classes in the `com.tddapps.controllers` package.  
The actual implementation of the functions will live under the `com.tddapps.actions` package. This will receive and return boundary objects. These classes will be framework agnostic.      
