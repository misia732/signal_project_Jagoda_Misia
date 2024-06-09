package com.design_patterns.decorator_pattern;

import com.alerts.Alert;
import com.design_patterns.decorator_pattern.AlertDecorator;

public class PriorityAlertDecorator extends AlertDecorator {
    
    public String priorityLevel; // low, medium, high
    private boolean triggered;

    public PriorityAlertDecorator(Alert alert, String priorityLevel){
        super(alert);
        this.priorityLevel = priorityLevel;
        triggerAlert();

    }
    
    @Override
    public void triggerAlert() {

        System.out.println("Priority: " + priorityLevel);
        triggered = true;
        decoratedAlert.triggerAlert();
    }

    public boolean isTriggered(){
        return this.triggered;
    }
}
