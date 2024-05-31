package com.alerts;

public class ManualAlert {

    private boolean alertTriggered;

    public ManualAlert(int patientId) {
        this.alertTriggered = false;

    }

    public void triggerAlert(int patientId) {
        this.alertTriggered = true;
        System.out.println("Alert triggered!");
    }

    public void untriggerAlert(int patientId) {
        this.alertTriggered = false;
        System.out.println("Alert untriggered!");
    }

    public boolean isAlertTriggered() {
        return this.alertTriggered;
    }
}
