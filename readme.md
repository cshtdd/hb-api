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
mvn clean install && serverless deploy
```
