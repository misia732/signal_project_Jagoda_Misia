package com.design_patterns.strategy_pattern;

import com.data_management.Patient;
import com.data_management.PatientRecord;
import com.design_patterns.factory_pattern.ECGAlertFactory;

import java.util.List;

public class HeartRateStrategy implements AlertStrategy {


    // monitors for abnormal heart rates
    @Override
    public boolean checkAlert(Patient patient, List<PatientRecord> records) {

        return checkECG(patient, records);

    }

    public boolean checkECG(Patient patient, List<PatientRecord> records) {

        PatientRecord lastRecord = null;

        // Find the most recent ECG record
        for (int i = records.size() - 1; i >= 0; i--) {
            if (records.get(i).getRecordType().equals("ECG")) {
                lastRecord = records.get(i);
                break;
            }
        }

        if (lastRecord == null) {
            System.err.println("No ECG record found for patient " + patient.getPatientId());
            return false;
        }

        double sumOfMeasurements = 0;
        double numOfMeasurements = 0;

        // Calculate the average ECG measurement within the last minute
        for (int i = records.size() - 1; i >= 0; i--) {
            PatientRecord currentRecord = records.get(i);
            if (currentRecord.getTimestamp() < lastRecord.getTimestamp() && currentRecord.getTimestamp() >= lastRecord.getTimestamp() - 60000) {
                if (currentRecord.getRecordType().equals("ECG")) {
                    sumOfMeasurements += currentRecord.getMeasurementValue();
                    numOfMeasurements++;
                }
            } else if (currentRecord.getTimestamp() < lastRecord.getTimestamp() - 60000) {
                break;
            }
        }

        if (numOfMeasurements == 0) {
            System.err.println("No ECG records found within the last minute for patient " + patient.getPatientId());
            return false;
        }

        double average = sumOfMeasurements / numOfMeasurements;

        if (Math.abs(lastRecord.getMeasurementValue()) > 1.2 * average || Math.abs(lastRecord.getMeasurementValue()) < 0.8 * average) {
            // A deviation of more than 20% is considered significant.
            ECGAlertFactory ecgAlert = new ECGAlertFactory();
            ecgAlert.createAlert(patient.getPatientId(), "ECG Abnormal Data Alert", System.currentTimeMillis());
            return true;
        }

        return false;
    }
}
