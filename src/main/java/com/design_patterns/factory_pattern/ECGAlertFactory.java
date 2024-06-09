package com.design_patterns.factory_pattern;

import com.alerts.ECGAlert;
import com.design_patterns.factory_pattern.AlertFactory;

public class ECGAlertFactory extends AlertFactory {
    
    @Override
    public ECGAlert createAlert(int patientId, String condition, long timestamp){

        return new ECGAlert(patientId, condition, timestamp);
        
    }
}
