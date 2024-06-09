package com.design_patterns;

import com.alerts.Alert;
import com.alerts.ECGAlert;

public class ECGAlertFactory extends AlertFactory{
    
    @Override
    public ECGAlert createAlert(int patientId, String condition, long timestamp){

        return new ECGAlert(patientId, condition, timestamp);
        
    }
}
