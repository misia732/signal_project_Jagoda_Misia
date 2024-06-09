package com.design_patterns.strategy_pattern;

import com.data_management.Patient;
import com.data_management.PatientRecord;
import com.design_patterns.factory_pattern.BloodOxygenAlertFactory;

import java.util.List;

public class OxygenSaturationStrategy implements AlertStrategy {


    // observes oxygen levels for critical drops
    @Override
    public boolean checkAlert(Patient patient, List<PatientRecord> records) {

        return (checkLowSaturationAlert(patient, records) || checkRapidDropAlert(patient, records));


    }

    public boolean checkRapidDropAlert(Patient patient, List<PatientRecord> records) {
        int lastIndex = records.size() - 1;
        PatientRecord currentRecord = records.get(lastIndex);

        // Start from the second-to-last record and iterate backwards
        for (int i = lastIndex; i > 0; i--) {

            PatientRecord previousRecord = records.get(i - 1);

            if (previousRecord.getRecordType().equals("BloodSaturation")) {
                // Found the previous saturation record

                if ((currentRecord.getTimestamp() - previousRecord.getTimestamp()) <= 600000 &&
                        ((currentRecord.getMeasurementValue() - previousRecord.getMeasurementValue()) / previousRecord.getMeasurementValue()) <= -0.05) {

                    BloodOxygenAlertFactory bloodOxygenAlert = new BloodOxygenAlertFactory();
                    bloodOxygenAlert.createAlert(patient.getPatientId(), "Blood oxygen saturation level dropped by 5% or more within 10 minutes.", currentRecord.getTimestamp());
                    return true;
                }

                // Exit the loop once we find the first previous saturation record
                break;
            }
        }
        return false;
    }

    public boolean checkLowSaturationAlert(Patient patient, List<PatientRecord> records) {
        int patientId = patient.getPatientId();
        for (PatientRecord record : records) {
            if (record.getPatientId() == patientId && record.getRecordType().equals("Saturation") &&
                    record.getMeasurementValue() < 92.0) {

                BloodOxygenAlertFactory bloodOxygenAlert = new BloodOxygenAlertFactory();
                bloodOxygenAlert.createAlert(patientId, "Blood oxygen saturation level fell below 92%.", record.getTimestamp());

                return true;
            }
        }
        return false;
    }

}
