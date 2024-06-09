package com.design_patterns;

import com.alerts.Alert;

public class PriorityAlertDecorator extends AlertDecorator {
    
    public String priorityLevel; // low, medium, high

    public PriorityAlertDecorator(Alert alert, String priorityLevel){
        super(alert);
        this.priorityLevel = priorityLevel;
        triggerAlert();
    }
    
    @Override
    public void triggerAlert() {

        System.out.println("Priority: " + priorityLevel);
        decoratedAlert.triggerAlert();
    }
}
