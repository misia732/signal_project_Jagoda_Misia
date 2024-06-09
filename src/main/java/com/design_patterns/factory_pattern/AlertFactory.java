package com.design_patterns.factory_pattern;

import com.alerts.Alert;

public abstract class AlertFactory {
    
    public Alert createAlert(int patientId, String condition, long timestamp){
        return new Alert(patientId, condition, timestamp);
    }
    
}
