package com.design_patterns.factory_pattern;

import com.alerts.BloodOxygenAlert;
import com.design_patterns.factory_pattern.AlertFactory;

public class BloodOxygenAlertFactory extends AlertFactory {
    
    @Override
    public BloodOxygenAlert createAlert(int patientId, String condition, long timestamp){

        return new BloodOxygenAlert(patientId, condition, timestamp);
        
    }
}