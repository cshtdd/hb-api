package com.tddapps.dal;

public interface NotificationSender {
    void Send(String message, String subject) throws DalException;
}
