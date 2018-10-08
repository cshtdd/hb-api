# hb-api  

AWS Serverless API to receive heartbeats and sends notifications when computers disappear.  

## Table of Contents  

- [Introduction](#introduction)  
- [Getting Started](#getting-started)  
- [Design Considerations](#design-considerations)  
- [Development](docs/development.md)  
- [API Key Management](docs/api-key-management.md)  
- [Deployment](docs/deployment.md)  
- [Usage](docs/usage.md)  
- [Code Architecture](docs/code-architecture.md)  
- [Changelog](changelog.md)  
- [License](LICENSE)  
- [Code of Conduct](CODE_OF_CONDUCT.md)  

## Introduction  

### The Problem  

There is no easy way to know when a computer dies. Specially in large networks where computers are geographically distributed.  
Think in every cash register of every fast food restaurant in the entire world. Thousands of computers that may disappear at any time because of power outages or connectivity issues.  
Directly testing each computer is not always feasible, most of the time restaurant chains control the computer but not the network or the firewall rules. 
Think about all the work required to setup thousands of port forwarding rules throughout the Globe. 
And the additional security risks of doing so.  

### The solution  

Rather than actively monitoring each computer. We make every computer send a message whenever it can. A lack of messages from a computer for a sustained period of time indicate a problem.  
This pattern is known as [Heartbeat](https://en.wikipedia.org/wiki/Heartbeat_(computing)) and is widely used through the industry.  

## Getting Started  

TODO

## Design considerations  

The application runs simultaneously on two different AWS regions.  
Load balancing and region failover is managed by Route53 healthchecks.  
Data is globally replicated with DynamoDB Global Tables.  
Notifications are sent using SNS Topics and we did our best to ensure they are delivered exactly once.  
