package com.alerts;

import java.util.ArrayList;
import java.util.List;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private DataStorage dataStorage;


    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage) {

        this.dataStorage = dataStorage;
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert}
     * method. This method should define the specific conditions under which an
     * alert
     * will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        // Implementation goes here
    }

    /**
     * Triggers an alert for the monitoring system. This method can be extended to
     * notify medical staff, log the alert, or perform other actions. The method
     * currently assumes that the alert information is fully formed when passed as
     * an argument.
     *
     * @paramalert the alert object containing details about the alert condition
     */

    public boolean checkLowSaturationAlert(Patient patient, List<PatientRecord> records) {
        int patientId = patient.getPatientId();
        for (PatientRecord record : records) {
            if (record.getPatientId() == patientId && record.getRecordType().equals("Saturation") &&
                    record.getMeasurementValue() < 92.0) {
                triggerAlert(new Alert(
                        patientId,
                        "Blood oxygen saturation level fell below 92%.",
                        record.getTimestamp()
                ));
                return true;
            }
        }
        return false;
    }

    public boolean checkHypotensiveHypoxemiaAlert(Patient patient, List<PatientRecord> records) {
        int patientId = patient.getPatientId();
        boolean lowSaturation = false;
        boolean lowBloodPressure = false;

        for (PatientRecord record : records) {
            if (record.getPatientId() == patientId) {
                if (record.getRecordType().equals("Saturation") && record.getMeasurementValue() < 92.0) {
                    lowSaturation = true;
                }
                if (record.getRecordType().equals("SystolicPressure") && record.getMeasurementValue() < 90.0) {
                    lowBloodPressure = true;
                }
            }

            if (lowSaturation && lowBloodPressure) {
                triggerAlert(new Alert(
                        patientId,
                        "Hypotensive Hypoxemia Alert: Blood oxygen saturation level fell below 92% and systolic blood pressure fell below 90 mmHg.",
                        record.getTimestamp()

                ));
                return true;
            }
        }

        return false;
    }

    public boolean checkRapidDropAlert(Patient patient, List<PatientRecord> records) {
        int lastIndex = records.size() - 1;
        PatientRecord currentRecord = records.get(lastIndex);
        System.out.println(currentRecord);

        // Start from the second last record and iterate backwards
        for (int i = lastIndex; i >= 0; i--) {
            PatientRecord previousRecord = records.get(i-1);

            if (previousRecord.getRecordType().equals("BloodSaturation")) {
                // Found the previous saturation record

                if ((currentRecord.getTimestamp() - previousRecord.getTimestamp()) <= 600000 &&
                        ((currentRecord.getMeasurementValue() - previousRecord.getMeasurementValue()) /  previousRecord.getMeasurementValue()) <= - 0.05) {

                    triggerAlert(new Alert(
                            patient.getPatientId(),
                            "Blood oxygen saturation level dropped by 5% or more within 10 minutes.",
                            currentRecord.getTimestamp()));
                    return true;
                }

                // Exit the loop once we find the first previous saturation record
                break;
            }
        }
        return false;
    }

    private void triggerAlert(Alert alert) {
        // Implementation might involve logging the alert or notifying staff
    }

    /**
     * Manually triggers an alert for a specific patient. This method can be called
     * by nurses or patients to indicate a potential issue that requires attention.
     *
     * @param patientId the ID of the patient for whom the alert is triggered
     */
    public void triggerManualAlert(int patientId) {

        ManualAlert manualAlert = new ManualAlert(patientId);
        manualAlert.triggerAlert(patientId);

    }

    /**
     * Manually untriggers an alert for a specific patient. This method can be called
     * by nurses or patients to indicate that a previously triggered alert is no longer
     * relevant.
     *
     * @param patientId the ID of the patient for whom the alert is untriggered
     */
    public void untriggerManualAlert(int patientId) {

        ManualAlert manualAlert = new ManualAlert(patientId);
        manualAlert.untriggerAlert(patientId);
    }

    public boolean checkBloodPressureTrend(Patient patient, List<PatientRecord> records) {
    List<PatientRecord> patientRecord = records;

    List<Double> sysValues = new ArrayList<>();
    List<Double> diaValues = new ArrayList<>();

    for (int i = patientRecord.size() - 1; i >= 0; i--) {
        PatientRecord lastRecord = patientRecord.get(i);

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
            triggerAlert(new Alert(patient.getPatientId(), "Blood Pressure Trend Alert", System.currentTimeMillis()));
            return true;
        }
    }

    if (diaValues.size() == 3) {
        if ((diaValues.get(0) - diaValues.get(1) > 10 && diaValues.get(1) - diaValues.get(2) > 10) ||
            (diaValues.get(2) - diaValues.get(1) > 10 && diaValues.get(1) - diaValues.get(0) > 10)) {
            triggerAlert(new Alert(patient.getPatientId(), "Blood Pressure Trend Alert", System.currentTimeMillis()));
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
            triggerAlert(new Alert(patient.getPatientId(), "Blood Pressure Systolic Threshold Alert", System.currentTimeMillis()));
            return true;
        }

        if (diaValue != null && (diaValue > 120 || diaValue < 60)) {
            triggerAlert(new Alert(patient.getPatientId(), "Blood Pressure Diastolic Threshold Alert", System.currentTimeMillis()));
            return true;
        }

        return false;
    }

    public boolean checkECG(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> patientRecord = records;
    
        PatientRecord lastRecord = null;
    
        // Find the most recent ECG record
        for (int i = patientRecord.size() - 1; i >= 0; i--) {
            if (patientRecord.get(i).getRecordType().equals("ECG")) {
                lastRecord = patientRecord.get(i);
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
        for (int i = patientRecord.size() - 1; i >= 0; i--) {
            PatientRecord currentRecord = patientRecord.get(i);
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
            triggerAlert(new Alert(patient.getPatientId(), "ECG Abnormal Data Alert", System.currentTimeMillis()));
            return true;
        }
    
        return false;
    }

}

