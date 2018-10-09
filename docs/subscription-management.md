# Subscription management  

Notification subscription is not managed through an api yet. The following commands leverage the AWS cli to create and delete SNS subscriptions.  

## Subscribe to notifications  

```bash
sh ./dev/create_subscription.sh test@test.com email us-east-1 && \
    sh ./dev/create_subscription.sh test@test.com email us-west-2
```

## Unsubscribe from notifications  

```bash
sh ./dev/delete_subscription.sh test@test.com us-east-1 && \
    sh ./dev/delete_subscription.sh test@test.com us-west-2
```

The aforementioned tools from the `dev` folder contain more examples.
