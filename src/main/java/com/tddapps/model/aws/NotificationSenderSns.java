package com.tddapps.model.aws;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.Topic;
import com.tddapps.model.*;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NotificationSenderSns implements NotificationSender, NotificationSenderStatus {
    private static final Logger LOG = LogManager.getLogger(NotificationSenderSns.class);

    private final SettingsReader settingsReader;

    public NotificationSenderSns(SettingsReader settingsReader){
        this.settingsReader = settingsReader;
    }

    @Override
    public void Verify() throws DalException {
        try {
            val anyTopicArn = AmazonSNSClientBuilder
                    .defaultClient()
                    .listTopics()
                    .getTopics()
                    .stream()
                    .map(Topic::getTopicArn)
                    .findFirst()
                    .orElse("");

            if (anyTopicArn == ""){
                throw new DalException("Topic Arn could not be read");
            }
        }
        catch (AmazonClientException e){
            LOG.error("Notification VerifySendCapability Error", e);
            throw new DalException(e.getMessage());
        }
    }

    @Override
    public void Send(Notification notification) throws DalException {
        try{
            AmazonSNSClientBuilder
                    .defaultClient()
                    .publish(
                            getTopicName(),
                            notification.getMessage(),
                            notification.getSubject()
                    );
        }
        catch (AmazonClientException e){
            LOG.error("Notification Send Error", e);
            throw new DalException(e.getMessage());
        }
    }

    private String getTopicName() {
        return settingsReader.ReadString(Settings.TOPIC_NAME);
    }
}
