package com.tddapps.model.notifications;

import com.tddapps.model.DalException;

public interface NotificationSenderStatus{
    void Verify() throws DalException;
}
