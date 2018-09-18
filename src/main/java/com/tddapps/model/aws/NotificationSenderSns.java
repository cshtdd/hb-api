package com.tddapps.model.aws;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.Topic;
import com.tddapps.model.*;
import lombok.extern.log4j.Log4j2;
import lombok.val;

@Log4j2
public class NotificationSenderSns implements NotificationSender, NotificationSenderStatus {
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

            if (anyTopicArn.isEmpty()){
                throw new DalException("Topic Arn could not be read");
            }
        }
        catch (AmazonClientException e){
            log.debug("Notification VerifySendCapability Error", e);
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
            log.debug("Notification Send Error", e);
            throw new DalException(e.getMessage());
        }
    }

    private String getTopicName() {
        return settingsReader.ReadString(Settings.TOPIC_NAME);
    }
}
