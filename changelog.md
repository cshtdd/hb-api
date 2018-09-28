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
