package com.alerts;

public interface AlertInterface {

    int getPatientId();
    String getCondition();
    long getTimestamp();
    void triggerAlert();
}