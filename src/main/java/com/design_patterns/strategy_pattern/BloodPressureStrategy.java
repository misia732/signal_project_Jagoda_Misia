package com.design_patterns.strategy_pattern;

import com.data_management.Patient;
import com.data_management.PatientRecord;
import com.design_patterns.factory_pattern.BloodPressureAlertFactory;

import java.util.ArrayList;
import java.util.List;

public class BloodPressureStrategy implements AlertStrategy {
    @Override
    public boolean checkAlert(Patient patient, List<PatientRecord> records) {
        checkBloodPressureTrend(patient, records);
        return checkBloodPressureCriticalThreshold(patient, records);
    }

    public boolean checkBloodPressureTrend(Patient patient, List<PatientRecord> records) {

        List<Double> sysValues = new ArrayList<>();
        List<Double> diaValues = new ArrayList<>();

        for (int i = records.size() - 1; i >= 0; i--) {
            PatientRecord lastRecord = records.get(i);

            if (lastRecord.getRecordType().equals("SystolicPressure") && sysValues.size() < 3) {
                sysValues.add(lastRecord.getMeasurementValue());
            } else if (lastRecord.getRecordType().equals("DiastolicPressure") && diaValues.size() < 3) {
                diaValues.add(lastRecord.getMeasurementValue());
            }

            if (sysValues.size() >= 3 && diaValues.size() >= 3) {
                break;
            }
        }

        if (sysValues.size() == 3) {
            if ((sysValues.get(0) - sysValues.get(1) > 10 && sysValues.get(1) - sysValues.get(2) > 10) ||
                    (sysValues.get(2) - sysValues.get(1) > 10 && sysValues.get(1) - sysValues.get(0) > 10)) {
                BloodPressureAlertFactory bloodPressureAlert = new BloodPressureAlertFactory();
                bloodPressureAlert.createAlert(patient.getPatientId(), "Blood Pressure Trend Alert", System.currentTimeMillis());
                return true;
            }
        }

        if (diaValues.size() == 3) {
            if ((diaValues.get(0) - diaValues.get(1) > 10 && diaValues.get(1) - diaValues.get(2) > 10) ||
                    (diaValues.get(2) - diaValues.get(1) > 10 && diaValues.get(1) - diaValues.get(0) > 10)) {
                BloodPressureAlertFactory bloodPressureAlert = new BloodPressureAlertFactory();
                bloodPressureAlert.createAlert(patient.getPatientId(), "Blood Pressure Trend Alert", System.currentTimeMillis());
                return true;
            }
        }

        return false;
    }

    public boolean checkBloodPressureCriticalThreshold(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> patientRecord = records;

        Double sysValue = null;
        Double diaValue = null;

        for (int i = patientRecord.size() - 1; i >= 0; i--) {
            PatientRecord lastRecord = patientRecord.get(i);

            if (sysValue == null && lastRecord.getRecordType().equals("SystolicPressure")) {
                sysValue = lastRecord.getMeasurementValue();
            } else if (diaValue == null && lastRecord.getRecordType().equals("DiastolicPressure")) {
                diaValue = lastRecord.getMeasurementValue();
            }

            if (sysValue != null && diaValue != null) {
                break;
            }
        }

        if (sysValue != null && (sysValue > 180 || sysValue < 90)) {
            BloodPressureAlertFactory bloodPressureAlert = new BloodPressureAlertFactory();
            bloodPressureAlert.createAlert(patient.getPatientId(), "Blood Pressure Systolic Threshold Alert", System.currentTimeMillis());

            return true;
        }

        if (diaValue != null && (diaValue > 120 || diaValue < 60)) {
            BloodPressureAlertFactory bloodPressureAlert = new BloodPressureAlertFactory();
            bloodPressureAlert.createAlert(patient.getPatientId(), "Blood Pressure Diastolic Threshold Alert", System.currentTimeMillis());
            return true;
        }

        return false;
    }
}
