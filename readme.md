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
mvn clean install && sls deploy
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
