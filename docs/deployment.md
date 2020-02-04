# Deployment  

## Prerequisites  
  
1- Make sure your domain is managed by Route53  
2- Manually create a certificate using AWS ACM. They are free. The certificate can allow any subdomain `*.mydomain.com`  

## Deployment process  

```bash
npm config set hb-api:stage dev &&
  npm config set hb-api:region1 us-east-1 &&
  npm config set hb-api:region2 us-west-2 &&
  npm config set hb-api:domainname hbapidev.cshtdd.com &&
  npm config set hb-api:subdomain hbapidev &&
  npm config set hb-api:tld cshtdd.com &&
  npm run deploy
```

**Note**: Make sure there is a basepath in the custom domain. If not, follow the steps in this [Github Issue](https://github.com/amplify-education/serverless-domain-manager/issues/57) to correct it. There is some buggy behavior between CloudFormation and the domain manager plugin

## Make sure to create the Global HeartBeats table  

Create the global dynamo tables after the first deployment  

```bash
sh ./dev/create_global_heartbeats_table.sh
```

## Single Region Deployment  

```bash
npm config set hb-api:stage prod &&
  npm config set hb-api:region1 us-east-1 &&
  npm config set hb-api:domainname hbapi.tddapps.com &&
  npm config set hb-api:subdomain hbapi &&
  npm config set hb-api:tld tddapps.com &&
  npm run deploy-single-region
```

Create a `CNAME` record in the DNS provider pointing to the API Gateway domain `Distribution Domain Name`.

**Note**: The single region deployment does not use Route53. No need to create the global tables either.  

**Note**: Make sure there is a basepath in the custom domain. If not, follow the steps in this [Github Issue](https://github.com/amplify-education/serverless-domain-manager/issues/57) to correct it. There is some buggy behavior between CloudFormation and the domain manager plugin.

