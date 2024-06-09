package com.alerts;

// Represents an alert
public class Alert implements AlertInterface{
    private int patientId;
    private String condition;
    private long timestamp;


    public Alert(int patientId, String condition, long timestamp) {
        this.patientId = patientId;
        this.condition = condition;
        this.timestamp = timestamp;

        triggerAlert();
    }

    public int getPatientId() {
        return patientId;
    }

    public String getCondition() {
        return condition;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void triggerAlert() {
        System.out.println("Alert created!!! \nPatient ID: " + patientId + "\nCondition: " + condition + "\nTimestamp: " + timestamp);
    }
}
