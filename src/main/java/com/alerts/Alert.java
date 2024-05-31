package com.alerts;

// Represents an alert
public class Alert {
    private int patientId;
    private String condition;
    private long timestamp;


    public Alert(int patientId, String condition, long timestamp) {
        this.patientId = patientId;
        this.condition = condition;
        this.timestamp = timestamp;

        // Print the details of the alert
        System.out.println("Alert created!!! \nPatient ID: " + patientId + "\nCondition: " + condition + "\nTimestamp: " + timestamp);
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
}
