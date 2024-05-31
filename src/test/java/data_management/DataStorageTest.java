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
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 100.0, "WhiteBloodCells", 1714376789050L);
        storage.addPatientData(1, 200.0, "WhiteBloodCells", 1714376789051L);

        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789051L);
        assertEquals(2, records.size()); // Check if two records are retrieved
        assertEquals(100.0, records.get(0).getMeasurementValue()); // Validate first record
    }

    @Test
    void shouldReturnLowSaturationAlert() {

        DataStorage storage = new DataStorage();
        AlertGenerator alertGenerator = new AlertGenerator(storage);
        Patient patient = new Patient(1);

        storage.addPatientData(1, 91, "Saturation", 1714376789050L);

        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789051L);

        assertTrue(alertGenerator.checkLowSaturationAlert(patient, records)); // Check if the low saturation alert is triggered

    }

    @Test
    void shouldNotReturnLowSaturationAlert() {

        DataStorage storage = new DataStorage();
        AlertGenerator alertGenerator = new AlertGenerator(storage);
        Patient patient = new Patient(1);

        storage.addPatientData(2, 93, "Saturation", 1714376789050L);

        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789051L);

        assertFalse(alertGenerator.checkLowSaturationAlert(patient, records)); // Check if the low saturation alert is triggered

    }

    @Test
    void testSaturationDrop() {

        DataStorage storage = new DataStorage();
        AlertGenerator alertGenerator = new AlertGenerator(storage);
        Patient patient = new Patient(1);

        // Add two saturation records with a difference of more than 5 within 10 minutes
        storage.addPatientData(1, 98, "BloodSaturation", 1714376789050L); // Initial saturation
        storage.addPatientData(1, 100.0, "WhiteBloodCells", 1714376789050L); // check if filtrates BloodSaturation record types
        storage.addPatientData(1, 93, "BloodSaturation", 1714376799050L); // Rapid drop after less than 10 minutes

        // Retrieve records for the patient within a time range
        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376799050L);

        // Check if the rapid drop alert is triggered
        assertTrue(alertGenerator.checkRapidDropAlert(patient, records));

    }



}
