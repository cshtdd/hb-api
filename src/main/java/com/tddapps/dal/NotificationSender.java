package com.tddapps.dal;

public interface NotificationSender {
    void Send(Notification notification) throws DalException;
}
