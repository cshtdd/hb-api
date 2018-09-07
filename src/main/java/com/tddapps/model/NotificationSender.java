package com.tddapps.model;

public interface NotificationSender {
    void Send(Notification notification) throws DalException;
}
