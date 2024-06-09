package data_management;

import static org.junit.jupiter.api.Assertions.*;

import com.alerts.AlertGenerator;
import com.data_management.DataReaderImplementation;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.Test;

import java.util.List;

class DataStorageTest {

    @Test
    void testAddAndGetRecords() {
        // TODO Perhaps you can implement a mock data reader to mock the test data?
        // DataReader reader
        // DataStorage storage = new DataStorage(reader);
        DataStorage storage = DataStorage.getInstance();
        storage.addPatientData(1, 100.0, "WhiteBloodCells", 1714376789050L);
        storage.addPatientData(1, 200.0, "WhiteBloodCells", 1714376789051L);

        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789051L);
        assertEquals(2, records.size()); // Check if two records are retrieved
        assertEquals(100.0, records.get(0).getMeasurementValue()); // Validate first record
    }

    @Test
    void testLowSaturationAlert() {

        DataStorage storage = DataStorage.getInstance();
        AlertGenerator alertGenerator = new AlertGenerator(storage);
        Patient patient = new Patient(1);
        Patient patient2 = new Patient(2);
        Patient patient3 = new Patient(3);

        storage.addPatientData(1, 92, "Saturation", 1714376789050L);
        storage.addPatientData(2, 91, "Saturation", 1714376789050L);
        storage.addPatientData(3, 93, "Saturation", 1714376789050L);

        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789051L);
        List<PatientRecord> records2 = storage.getRecords(2, 1714376789050L, 1714376789051L);
        List<PatientRecord> records3 = storage.getRecords(3, 1714376789050L, 1714376789051L);

        // checking edge case, should not trigger alert
        assertFalse(alertGenerator.checkLowSaturationAlert(patient, records));

        // checking low saturation, should trigger alert
        assertTrue(alertGenerator.checkLowSaturationAlert(patient2, records2));

        // checking healthy state, should not trigger alert
        assertFalse(alertGenerator.checkLowSaturationAlert(patient3, records3));

    }


    @Test
    void testSaturationDrop() {

        DataStorage storage = DataStorage.getInstance();
        AlertGenerator alertGenerator = new AlertGenerator(storage);
        Patient patient = new Patient(1);

        // Add two saturation records with a difference of more than 5 within 10 minutes
        storage.addPatientData(1, 98, "BloodSaturation", 1714376789050L); // Initial saturation
        storage.addPatientData(1, 100.0, "WhiteBloodCells", 1714376789050L); // check if filtrates BloodSaturation record types
        storage.addPatientData(1, 93, "BloodSaturation", 1714376799050L); // Rapid drop after less than 10 minutes

        // Retrieve records for the patient within a time range
        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376799050L);

        // check rapid drop saturation, should trigger alert
        assertTrue(alertGenerator.checkRapidDropAlert(patient, records));

        // checking healthy state, should not trigger alert



    }

    @Test
    void testBloodPressureTrendAlert(){

        DataStorage storage = DataStorage.getInstance();
        AlertGenerator alertGenerator = new AlertGenerator(storage);
        Patient patient1 = new Patient(1);
        Patient patient2 = new Patient(2);
        Patient patient3 = new Patient(3);
        Patient patient4 = new Patient(4);
        Patient patient5 = new Patient(5);
        Patient patient6 = new Patient(6);

        //add data to trigger alert for increasing trend in systolic pressure (test 1)
        storage.addPatientData(1, 100, "SystolicPressure", 1714376789050L);
        storage.addPatientData(1, 115, "SystolicPressure", 1714376789051L);
        storage.addPatientData(1, 127, "SystolicPressure", 1714376789052L);

        //add data to trigger alert for increasing trend in diastolic pressure (test 2)
        storage.addPatientData(2, 60, "DiastolicPressure", 1714376789050L);
        storage.addPatientData(2, 72, "DiastolicPressure", 1714376789051L);
        storage.addPatientData(2, 85, "DiastolicPressure", 1714376789052L);

        //add data to trigger alert for decreasing trend in systolic pressure (test 3)
        storage.addPatientData(3, 127, "SystolicPressure", 1714376789060L);
        storage.addPatientData(3, 115, "SystolicPressure", 1714376789061L);
        storage.addPatientData(3, 100, "SystolicPressure", 1714376789062L);

        //add data to trigger alert for decreasing trend in systolic pressure (test 4)
        storage.addPatientData(4, 85, "DiastolicPressure", 1714376789060L);
        storage.addPatientData(4, 72, "DiastolicPressure", 1714376789061L);
        storage.addPatientData(4, 60, "DiastolicPressure", 1714376789062L);

        //add data to not trigger alert for increasing/decreasing trend in systolic pressure (test 5)
        storage.addPatientData(5, 100, "SystolicPressure", 1714376789070L);
        storage.addPatientData(5, 108, "SystolicPressure", 1714376789071L);
        storage.addPatientData(5, 99, "SystolicPressure", 1714376789072L);

        //add data to not trigger alert for increasing/decreasing trend in diastolic pressure (test 6)
        storage.addPatientData(6, 60, "DiastolicPressure", 1714376789070L);
        storage.addPatientData(6, 72, "DiastolicPressure", 1714376789071L);
        storage.addPatientData(6, 60, "DiastolicPressure", 1714376789072L);

        // Retrieve records for the patient within a time range
        List<PatientRecord> records1 = storage.getRecords(1, 1714376789049L, 1714376799073L);
        List<PatientRecord> records2 = storage.getRecords(2, 1714376789049L, 1714376799073L);
        List<PatientRecord> records3 = storage.getRecords(3, 1714376789049L, 1714376799073L);
        List<PatientRecord> records4 = storage.getRecords(4, 1714376789049L, 1714376799073L);
        List<PatientRecord> records5 = storage.getRecords(5, 1714376789049L, 1714376799073L);
        List<PatientRecord> records6 = storage.getRecords(6, 1714376789049L, 1714376799073L);

        // Check if the alert (test 1) is triggered
        assertTrue(alertGenerator.checkBloodPressureTrend(patient1, records1));
        // Check if the alert (test 2) is triggered
        assertTrue(alertGenerator.checkBloodPressureTrend(patient2, records2));
        // Check if the alert (test 3) is triggered
        assertTrue(alertGenerator.checkBloodPressureTrend(patient3, records3));
        // Check if the alert (test 4) is triggered
        assertTrue(alertGenerator.checkBloodPressureTrend(patient4, records4));
        // Check if the alert (test 5) is not triggered
        assertFalse(alertGenerator.checkBloodPressureTrend(patient5, records5));
        // Check if the alert (test 6) is not triggered
        assertFalse(alertGenerator.checkBloodPressureTrend(patient6, records6));

    }

    @Test
    void testBloodPressureCriticalThresholdAlert(){
        DataStorage storage = DataStorage.getInstance();
        AlertGenerator alertGenerator = new AlertGenerator(storage);
        Patient patient1 = new Patient(1);
        Patient patient2 = new Patient(2);
        Patient patient3 = new Patient(3);
        Patient patient4 = new Patient(4);
        Patient patient5 = new Patient(5);

        //add data to trigger alert for systolic blood pressure > 180
        storage.addPatientData(1, 185, "SystolicPressure", 1714376789050L);

        //add data to trigger alert for systolic blood pressure < 90
        storage.addPatientData(2, 85, "SystolicPressure", 1714376789050L);

        //add data to trigger alert for diastolic blood pressure > 120
        storage.addPatientData(3, 125, "DiastolicPressure", 1714376789050L);

        //add data to trigger alert for diastolic blood pressure < 60
        storage.addPatientData(4, 55, "DiastolicPressure", 1714376789050L);

        //add data to not trigger alert for systolic blood pressure < 90 && > 180 and diastolic blood pressure < 60 && > 120
        storage.addPatientData(5, 170, "SystolicPressure", 1714376789050L);
        storage.addPatientData(5, 110, "DiastolicPressure", 1714376789050L);

        // Retrieve records for the patient within a time range
        List<PatientRecord> records1 = storage.getRecords(1, 1714376789049L, 1714376799073L);
        List<PatientRecord> records2 = storage.getRecords(2, 1714376789049L, 1714376799073L);
        List<PatientRecord> records3 = storage.getRecords(3, 1714376789049L, 1714376799073L);
        List<PatientRecord> records4 = storage.getRecords(4, 1714376789049L, 1714376799073L);
        List<PatientRecord> records5 = storage.getRecords(5, 1714376789049L, 1714376799073L);
        
        // Check if the alert (test 1) is triggered
        assertTrue(alertGenerator.checkBloodPressureCriticalThreshold(patient1, records1));
        // Check if the alert (test 2) is triggered
        assertTrue(alertGenerator.checkBloodPressureCriticalThreshold(patient2, records2));
        // Check if the alert (test 3) is triggered
        assertTrue(alertGenerator.checkBloodPressureCriticalThreshold(patient3, records3));
        // Check if the alert (test 4) is triggered
        assertTrue(alertGenerator.checkBloodPressureCriticalThreshold(patient4, records4));
        // Check if the alert (test 5) is not triggered
        assertFalse(alertGenerator.checkBloodPressureCriticalThreshold(patient5, records5));

    }

    @Test
    void testECGAlert(){

        DataStorage storage = DataStorage.getInstance();
        AlertGenerator alertGenerator = new AlertGenerator(storage);
        Patient patient1 = new Patient(1);
        Patient patient2 = new Patient(2);
        Patient patient3 = new Patient(3);

        //add data to trigger alert for too low ecg
        storage.addPatientData(1, 80, "ECG", 1714376789050L);
        storage.addPatientData(1, 70, "ECG", 1714376789052L);
        storage.addPatientData(1, 75, "ECG", 1714376789054L);
        storage.addPatientData(1, 50, "ECG", 1714376789056L);

        //add data to trigger alert for too high ecg
        storage.addPatientData(2, 80, "ECG", 1714376789050L);
        storage.addPatientData(2, 70, "ECG", 1714376789052L);
        storage.addPatientData(2, 75, "ECG", 1714376789054L);
        storage.addPatientData(2, 99, "ECG", 1714376789056L);

        //add data to not trigger alert for ecg
        storage.addPatientData(3, 80, "ECG", 1714376789050L);
        storage.addPatientData(3, 70, "ECG", 1714376789052L);
        storage.addPatientData(3, 75, "ECG", 1714376789054L);
        storage.addPatientData(3, 75, "ECG", 1714376789056L);

        // Retrieve records for the patient within a time range
        List<PatientRecord> records1 = storage.getRecords(1, 1714376789050L, 1714376789050L + 120000L);
        List<PatientRecord> records2 = storage.getRecords(2, 1714376789050L, 1714376789050L + 120000L);
        List<PatientRecord> records3 = storage.getRecords(3, 1714376789050L, 1714376789050L + 120000L);

        // Check if the alert (test 1) is triggered
        assertTrue(alertGenerator.checkECG(patient1, records1));
        // Check if the alert (test 2) is triggered
        assertTrue(alertGenerator.checkECG(patient2, records2));
        // Check if the alert (test 3) is not triggered
        assertFalse(alertGenerator.checkECG(patient3, records3));

    }

    @Test
    void testHypotensiveApoxemiaAlert() {

        DataStorage storage = DataStorage.getInstance();
        AlertGenerator alertGenerator = new AlertGenerator(storage);
        Patient patient = new Patient(1);
        Patient patient2 = new Patient(2);
        Patient patient3 = new Patient(3);

        storage.addPatientData(1, 92, "Saturation", 1714376789050L);
        storage.addPatientData(1, 90, "SystolicPressure", 1714376789050L);

        storage.addPatientData(2, 91, "Saturation", 1714376789050L);
        storage.addPatientData(2, 89, "SystolicPressure", 1714376789050L);

        storage.addPatientData(3, 93, "Saturation", 1714376789050L);
        storage.addPatientData(3, 91, "SystolicPressure", 1714376789050L);

        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789051L);
        List<PatientRecord> records2 = storage.getRecords(2, 1714376789050L, 1714376789051L);
        List<PatientRecord> records3 = storage.getRecords(3, 1714376789050L, 1714376789051L);

        // testing edge cases, should not trigger alert
        assertFalse(alertGenerator.checkHypotensiveHypoxemiaAlert(patient, records));

        // testing critical state, should trigger alert
        assertTrue(alertGenerator.checkHypotensiveHypoxemiaAlert(patient2, records2));

        // testing healthy state, should not trigger alert
        assertFalse(alertGenerator.checkHypotensiveHypoxemiaAlert(patient3, records3));


    }

}
