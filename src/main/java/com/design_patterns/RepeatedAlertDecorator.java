package com.design_patterns;

import com.alerts.Alert;

public class RepeatedAlertDecorator extends AlertDecorator {
    
    private long interval; // interval that will be checked
    private long lastTriggered; // when the alert was last triggered

    public RepeatedAlertDecorator(Alert alert, long interval){

        super(alert);
        this.interval = interval;
        this.lastTriggered = System.currentTimeMillis();

    }

    @Override
    public void triggerAlert() {
        
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastTriggered >= interval) {
            decoratedAlert.triggerAlert();
            lastTriggered = currentTime;
        }

    }

}
