package com.design_patterns.factory_pattern;

import com.alerts.BloodPressureAlert;
import com.design_patterns.factory_pattern.AlertFactory;

public class BloodPressureAlertFactory extends AlertFactory {
    
    @Override
    public BloodPressureAlert createAlert(int patientId, String condition, long timestamp){

        return new BloodPressureAlert(patientId, condition, timestamp);
        
    }
}