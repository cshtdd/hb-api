package com.tddapps.model.notifications;

import com.tddapps.model.DalException;

public interface NotificationSender {
    void Send(Notification notification) throws DalException;
}
