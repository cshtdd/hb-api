package com.tddapps.dal;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NotificationSenderSns implements NotificationSender {
    private static final Logger LOG = LogManager.getLogger(NotificationSenderSns.class);

    private final SettingsReader settingsReader;

    public NotificationSenderSns(SettingsReader settingsReader){
        this.settingsReader = settingsReader;
    }

    @Override
    public void Send(String message, String subject) throws DalException {
        String topicName = settingsReader.ReadString(Settings.TOPIC_NAME);

        try{
            AmazonSNSClientBuilder
                    .defaultClient()
                    .publish(topicName, message, subject);
        }
        catch (AmazonClientException e){
            LOG.error("Notification Send Error", e);
            throw new DalException(e.getMessage());
        }
    }
}
