# Deployment  

## Prerequisites  
  
1- Make sure your domain is managed by Route53  
2- Manually create a certificate using AWS ACM. They are free. The certificate can allow any subdomain `*.mydomain.com`  

## Deployment process  

```bash
npm run deploy
```

**Note**: Make sure there is a basepath in the custom domain. If not, follow the steps in this [Github Issue](https://github.com/amplify-education/serverless-domain-manager/issues/57) to correct it. There is some buggy behavior between CloudFormation and the domain manager plugin

## Make sure to create the Global HeartBeats table  

Create the global dynamo tables after the first deployment  

```bash
sh ./dev/create_global_heartbeats_table.sh
```
