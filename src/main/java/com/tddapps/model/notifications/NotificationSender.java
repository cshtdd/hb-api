package com.tddapps.model.notifications;

import com.tddapps.model.DalException;
import com.tddapps.model.notifications.Notification;

public interface NotificationSender {
    void Send(Notification notification) throws DalException;
}
