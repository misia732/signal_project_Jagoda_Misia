package com.design_patterns.decorator_pattern;

import com.alerts.Alert;
import com.alerts.AlertInterface;

public class AlertDecorator implements AlertInterface{
    public Alert decoratedAlert;

    public AlertDecorator(Alert alert) {
        this.decoratedAlert = alert;
    }

    @Override
    public int getPatientId() {
        return decoratedAlert.getPatientId();
    }

    @Override
    public String getCondition() {
        return decoratedAlert.getCondition();
    }

    @Override
    public long getTimestamp() {
        return decoratedAlert.getTimestamp();
    }

    @Override
    public void triggerAlert() {
        decoratedAlert.triggerAlert();
    }

}