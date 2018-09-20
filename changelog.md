# 0.1.0 - 2018-09-16  
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

# 0.2.0 2018-09-20  
- Notifications are triggered by a lambda subscribed to a Dynamo stream on the HeartBeats table. This change reduces the precision of the notifications but significantly reduces the consumed read capacity  
- Heartbeats are expired leveraging Dynamo's TTL  
- DynamoDB autoscaling  
- Automated subscription management through CLI tools  
- Simplified code architecture  

# 0.3.0 TBD  
- Multi-region  