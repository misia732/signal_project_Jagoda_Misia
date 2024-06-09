package com.design_patterns.decorator_pattern;

import com.alerts.Alert;
import com.design_patterns.decorator_pattern.AlertDecorator;

public class RepeatedAlertDecorator extends AlertDecorator {
    
    private long interval; // interval that will be checked
    private long lastTriggered; // when the alert was last triggered
    private boolean triggered;

    public RepeatedAlertDecorator(Alert alert, long interval){

        super(alert);
        this.interval = interval;
        this.lastTriggered = System.currentTimeMillis();
        this.triggered = true;

    }

    @Override
    public void triggerAlert() {
        
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastTriggered >= interval) {
            decoratedAlert.triggerAlert();
            lastTriggered = currentTime;
            triggered = true;
        }

    }

    public long getInterval(){
        return this.interval;
    }

    public boolean isTriggered(){
        return this.triggered;
    }


    public void resetTriggered(){
        this.triggered = false;
    }
}
