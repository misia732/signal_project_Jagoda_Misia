package com.data_management;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alerts.AlertGenerator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Manages storage and retrieval of patient data within a healthcare monitoring
 * system.
 * This class serves as a repository for all patient records, organized by
 * patient IDs.
 */
public class DataStorage {
    private Map<Integer, Patient> patientMap; // Stores patient objects indexed by their unique patient ID.
    private String dataToProcess;
    private Lock lock; // lock to ensure thread safety
    /**
     * Constructs a new instance of DataStorage, initializing the underlying storage
     * structure.
     */
    public DataStorage() {


        this.patientMap = new HashMap<>();
        this.lock = new ReentrantLock();
        this.dataToProcess = "";
    }

    /**
     * Adds or updates patient data in the storage.
     * If the patient does not exist, a new Patient object is created and added to
     * the storage.
     * Otherwise, the new data is added to the existing patient's records.
     *
     * @param patientId        the unique identifier of the patient
     * @param measurementValue the value of the health metric being recorded
     * @param recordType       the type of record, e.g., "HeartRate",
     *                         "BloodPressure"
     * @param timestamp        the time at which the measurement was taken, in
     *                         milliseconds since the Unix epoch
     */
    public void addPatientData(int patientId, double measurementValue, String recordType, long timestamp) {
        lock.lock(); // acquire the lock before updating patient data
        try {
            Patient patient = patientMap.get(patientId);
            if (patient == null) {
                patient = new Patient(patientId);
                patientMap.put(patientId, patient);
            }
            patient.addRecord(measurementValue, recordType, timestamp);
        } finally {
            lock.unlock(); // release the lock after updating patient data
        }
    }

    /**
     * Retrieves a list of PatientRecord objects for a specific patient, filtered by
     * a time range.
     *
     * @param patientId the unique identifier of the patient whose records are to be
     *                  retrieved
     * @param startTime the start of the time range, in milliseconds since the Unix
     *                  epoch
     * @param endTime   the end of the time range, in milliseconds since the Unix
     *                  epoch
     * @return a list of PatientRecord objects that fall within the specified time
     *         range
     */
    public List<PatientRecord> getRecords(int patientId, long startTime, long endTime) {
        lock.lock(); // acquire the lock before accessing patient records
        try {
            Patient patient = patientMap.get(patientId);
            if (patient != null) {
                return patient.getRecords(startTime, endTime);
            }
            return new ArrayList<>(); // return an empty list if no patient is found
        } finally {
            lock.unlock(); // release the lock after accessing patient records
        }
    }

    /**
     * Retrieves a collection of all patients stored in the data storage.
     *
     * @return a list of all patients
     */
    public List<Patient> getAllPatients() {

        lock.lock(); // Acquire the lock before accessing patientMap
        try {
            return new ArrayList<>(patientMap.values());
        } finally {
            lock.unlock(); // Release the lock after accessing patientMap
        }
    }

    /**
     * The main method for the DataStorage class.
     * Initializes the system, reads data into storage, and continuously monitors
     * and evaluates patient data.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        DataStorage storage = new DataStorage();
        // Initialize the AlertGenerator with the storage
        AlertGenerator alertGenerator = new AlertGenerator(storage);

        // Continuously monitor and evaluate patient data
        while (true) {
            // Process incoming real-time data
            storage.processData();

            // Evaluate all patients' data to check for conditions that may trigger alerts
            for (Patient patient : storage.getAllPatients()) {
                alertGenerator.evaluateData(patient);
            }

            // Sleep for a certain period before processing the next batch of data
            try {
                Thread.sleep(1000); // Sleep for 1 second (adjust as needed)
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void storeData(String line) {

        lock.lock(); // Acquire the lock before updating dataToProcess
        try {
            dataToProcess += line;
            processData(); // Process the newly added data
        } finally {
            lock.unlock(); // Release the lock after updating dataToProcess
        }
    }

    private void processData() {

        // Split the dataToProcess string into individual data lines
        String[] dataLines = dataToProcess.split("\\n");

        // Process each data line
        for (String line : dataLines) {
            // Split the line into fields (assuming comma-separated values)
            String[] fields = line.split(",");

            // Check if the line has the expected number of fields
            if (fields.length == 4) {
                try {
                    // Extract patient information from the fields
                    int patientId = Integer.parseInt(fields[0]);
                    String data = fields[1];
                    String label = fields[2];
                    long timestamp = Long.parseLong(fields[3]);

                    // Update patient records
                    addPatientData(patientId, Double.parseDouble(data), label, timestamp);
                } catch (NumberFormatException e) {
                    // Handle parsing errors if necessary
                    System.err.println("Error parsing data: " + line);
                    e.printStackTrace();
                }
            } else {
                // Handle invalid data format if necessary
                System.err.println("Invalid data format: " + line);
            }
        }

        // Clear the dataToProcess string after processing
        dataToProcess = "";


    }
}
