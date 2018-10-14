# 0.1.0 - 2018-09-16 - MVP  
- Notifications are triggered by a lamda that runs on a schedule and scans the HeartBeats dynamo table  
- Single region  
- Single tenant  
- Single environment for dev, test, and prod
- Fixed DynamoDB capacity  
- Authenticated api calls  
- Api Health endpoint  
- Automated deployments  
- Automated vulnerability scan  
- Ability to run mostly complete development environment locally
- Local lambda debugging  
- Manual subscription management through the AWS console  

**Shortcommings** Constrained to the availability of the components in a single region. Notifications are calculated once per minute. A full table scan is needed to determine what notifications to send.  

# 0.2.0 - 2018-09-20 - Reduce Dynamo load  
- Notifications are triggered by a lambda subscribed to a Dynamo stream on the HeartBeats table  
- Heartbeats are expired leveraging Dynamo's TTL  
- DynamoDB autoscaling  
- Automated subscription management through CLI tools  
- Simplified code architecture  

**Shortcommings** This change reduces the precision of the notifications but significantly reduces the consumed read capacity.  

# 0.3.0 - 2018-09-27 Multi-region  
- Heartbeats are replicated accross regions with a Dynamo Global table  
- Api Gateway endpoints are regional  
- Route53 returns the endpoint closest to the client  
- Route53 performs automated healthchecks on the api and can dynamically enable/disable a region based on its health  

**Shortcommings** Notifications are triggered once per region.  

# 0.4.0 - 2018-09-28 - Send notifications exactly once  
- When a host dissapears, notifications will be sent only from the region that received its last update  

**Shortcommings** If a hosts dissapears, and then DynamoDB has a regional outage, no notification will be sent  

# 0.5.0 - 2018-10-01 - More responsive notifications  
- Notifications are sent within a minute of the host expiration _mostly_  
- The Dynamo TTL expiration is still in place as a fallback  
- The Dynamo HeartBeats table has a new Global Secondary Index with the minute the host expires as `HASH` key and the TTL as `RANGE` key. A cron lambda that runs every minute will get at most 25 hosts that expired in the previous minute and delete them. **Why 25?**: To stay well below the index's read throughput.  
- The new index will have the same thoughput and autoscaling settings as the HeartBeats table  

# 0.6.0 - 2018-10-11 - Notifications on host registration  
- Notifications are sent whenever a new host is registered or reappears  

# 0.6.1 - 2018-10-14 - Notifications on host registration  
- Better separation between notification logic  
- Reduce the number of healthcheckers  

# 0.6.2 - 2018-10-14 - Notifications on host registration  
- Logging enhancements on HeartBeat change  
