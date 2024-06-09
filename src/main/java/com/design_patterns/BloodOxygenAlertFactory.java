package com.design_patterns;

import com.alerts.Alert;
import com.alerts.BloodOxygenAlert;

public class BloodOxygenAlertFactory extends AlertFactory{
    
    @Override
    public BloodOxygenAlert createAlert(int patientId, String condition, long timestamp){

        return new BloodOxygenAlert(patientId, condition, timestamp);
        
    }
}