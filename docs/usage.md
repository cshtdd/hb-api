# Usage  

These instructions configure a machine to send heartbeats at frequent intervals  

## Prerequisites  

Make sure to have an api key as described in [API Key Management](api-key-management.md)  

## Installation  

Launch the `crontab` editor

```bash
crontab -e
```

Add the following lines to the file

```bash
HB_API_KEY=SUPER_SECRET_KEY
HOST_ID=YOUR_HOST_NAME

*/5 * * * * curl -i -H "x-api-key: ${HB_API_KEY}" -d '{"hostId": "'${HOST_ID}'"}' -X POST https://hbapidev.cshtdd.com/v1/hearbeat | logger -p local0.notice
```

## Verification  

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
