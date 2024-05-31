package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

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

    private void checkLowSaturationAlert(Patient patient, List<PatientRecord> records) {
        int patientId = patient.getPatientId();
        for (PatientRecord record : records) {
            if (record.getPatientId() == patientId && record.getRecordType().equals("Saturation") &&
                    record.getMeasurementValue() < 92.0) {
                triggerAlert(new Alert(
                        patientId,
                        "Blood oxygen saturation level fell below 92%.",
                        record.getTimestamp()
                ));
            }
        }
    }

    private void checkRapidDropAlert(Patient patient, List<PatientRecord> records) {
        int lastIndex = records.size() - 1;
        PatientRecord currentRecord = records.get(lastIndex);

        // Start from the second last record and iterate backwards
        for (int i = lastIndex; i >= 0; i--) {
            PatientRecord previousRecord = records.get(i);

            if (previousRecord.getRecordType().equals("BloodSaturation")) {
                // Found the previous saturation record

                if ((currentRecord.getTimestamp() - previousRecord.getTimestamp()) <= 600000 &&
                        (previousRecord.getMeasurementValue() - currentRecord.getMeasurementValue()) >= 5.0) {

                    triggerAlert(new Alert(
                            patient.getPatientId(),
                            "Blood oxygen saturation level dropped by 5% or more within 10 minutes.",
                            currentRecord.getTimestamp()));
                }

                // Exit the loop once we find the first previous saturation record
                break;
            }
        }
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

    private void checkBloodPressureTrend(int patientId){
        List<PatientRecord> patientRecord = DataStorage.getRecords(patientId, 0, System.currentTimeMillis());

        ArrayList<PatientRecord> sysArr = new ArrayList<>();
        ArrayList<PatientRecord> diaArr = new ArrayList<>();

        int sysToFind = 3;
        int diaToFind = 3;

        for (int i = 1; i <= patientRecord.size(); i++){

            PatientRecord lastRecord = patientRecord.get(patientRecord.size() - i);

            if (sysToFind > 0 && lastRecord.getRecordType().equals("SystolicPressure")){
                sysArr.add(lastRecord);
                sysToFind--;
            } else if (diaToFind > 0 && lastRecord.getRecordType().equals("DiastolicPressure")){
                diaArr.add(lastRecord);
                diaToFind--;
            }

            if(sysToFind <= 0 && diaToFind <=0) break;

        }

        if ((sysArr.get(0).getMeasurementValue() - sysArr.get(1).getMeasurementValue() > 10 && sysArr.get(1).getMeasurementValue() - sysArr.get(2).getMeasurementValue() > 10) ||
                (sysArr.get(2).getMeasurementValue() - sysArr.get(1).getMeasurementValue() > 10 && sysArr.get(1).getMeasurementValue() - sysArr.get(0).getMeasurementValue() > 10)) {
            triggerAlert(new Alert(patientId, "Blood Pressure Systolic Trend Alert", System.currentTimeMillis()));
        }

        if ((diaArr.get(0).getMeasurementValue() - diaArr.get(1).getMeasurementValue() > 10 && diaArr.get(1).getMeasurementValue() - diaArr.get(2).getMeasurementValue() > 10) ||
                (diaArr.get(2).getMeasurementValue() - diaArr.get(1).getMeasurementValue() > 10 && diaArr.get(1).getMeasurementValue() - diaArr.get(0).getMeasurementValue() > 10)) {
            triggerAlert(new Alert(patientId, "Blood Pressure Diastolic Trend Alert", System.currentTimeMillis()));
        }

    }

    private void checkBloodPressureCriticalThreshold(int patientId){

        List<PatientRecord> patientRecord = dataStorage.getRecords(patientId, 0, System.currentTimeMillis());

        ArrayList<PatientRecord> sysArr = new ArrayList<>();
        ArrayList<PatientRecord> diaArr = new ArrayList<>();

        int sysToFind = 1;
        int diaToFind = 1;

        for (int i = 1; i <= patientRecord.size(); i++){

            PatientRecord lastRecord = patientRecord.get(patientRecord.size() - i);

            if (sysToFind > 0 && lastRecord.getRecordType().equals("SystolicPressure")){
                sysArr.add(lastRecord);
                sysToFind--;
            } else if (diaToFind > 0 && lastRecord.getRecordType().equals("DiastolicPressure")){
                diaArr.add(lastRecord);
                diaToFind--;
            }

            if(sysToFind <= 0 && diaToFind <=0) break;

        }

        if(sysArr.get(0).getMeasurementValue() > 180 || sysArr.get(0).getMeasurementValue() < 90){
            triggerAlert(new Alert(patientId, "Blood Pressure Systolic Threshold Alert", System.currentTimeMillis()));
        }

        if(diaArr.get(0).getMeasurementValue() > 120 || diaArr.get(0).getMeasurementValue() < 60){
            triggerAlert(new Alert(patientId, "Blood Pressure Diastolic Threshold Alert", System.currentTimeMillis()));
        }

    }

    private void checkECG(int patientId){

        List<PatientRecord> patientRecord = dataStorage.getRecords(patientId, 0, System.currentTimeMillis());

        PatientRecord lastRecord = null;
        ArrayList<PatientRecord> ecgAcrossOneMinute = new ArrayList<>();

        int numberOfRecordsToFind = 1;

        for (int i = 1; i <= patientRecord.size(); i++){

            if (patientRecord.get(patientRecord.size() - i).getRecordType().equals("ECG")){
                lastRecord = patientRecord.get(patientRecord.size() - i);
                numberOfRecordsToFind --;
            }

            if(numberOfRecordsToFind <=0) break;

        }

        if (lastRecord == null) {
            System.err.println("No ECG record found for patient " + patientId);
            return;
        }

        PatientRecord currentRecord = lastRecord;
        double sumOfMeasurements = 0;
        double numOfMeasurements = 0;

        for (int i = 1; i <= patientRecord.size(); i++){

            if (patientRecord.get(patientRecord.size() - i).getTimestamp() < lastRecord.getTimestamp() && patientRecord.get(patientRecord.size() - i).getTimestamp() >= lastRecord.getTimestamp() - 60000){

                if (patientRecord.get(patientRecord.size() - i).getRecordType().equals("ECG")){
                    currentRecord = patientRecord.get(patientRecord.size() - i);
                    sumOfMeasurements += currentRecord.getMeasurementValue();
                    numOfMeasurements ++;
                }

            } else if (patientRecord.get(patientRecord.size() - i).getTimestamp() < lastRecord.getTimestamp() - 60000 ) break;

        }

        double average = sumOfMeasurements / numOfMeasurements;

        if (Math.abs(lastRecord.getMeasurementValue()) > 1.2 * average || Math.abs(lastRecord.getMeasurementValue()) < 0.8 * average){
            // We chose these thresholds for ecg, because a deviation of more than 20% is considered significant.
            triggerAlert(new Alert(patientId, "ECG Abnormal Data Alert", System.currentTimeMillis()));
        }

    }

}
}
