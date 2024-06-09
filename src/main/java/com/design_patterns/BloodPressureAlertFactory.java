package com.design_patterns;

import com.alerts.Alert;
import com.alerts.BloodPressureAlert;

public class BloodPressureAlertFactory extends AlertFactory{
    
    @Override
    public BloodPressureAlert createAlert(int patientId, String condition, long timestamp){

        return new BloodPressureAlert(patientId, condition, timestamp);
        
    }
}