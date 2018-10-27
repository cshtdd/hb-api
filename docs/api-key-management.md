# API Key management  

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
curl -w "\n%{http_code}\n" -H "x-api-key: ${HB_API_KEY}" -d '{"hostId": "testHost1"}' -X POST https://hbapidev.cshtdd.com/v1/hearbeat
```