package data_management;

import com.alerts.Alert;
import com.cardio_generator.HealthDataSimulator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import com.design_patterns.decorator_pattern.PriorityAlertDecorator;
import com.design_patterns.decorator_pattern.RepeatedAlertDecorator;
import java.lang.reflect.Field;
import java.util.List;

import com.design_patterns.strategy_pattern.BloodPressureStrategy;
import com.design_patterns.strategy_pattern.HeartRateStrategy;
import com.design_patterns.strategy_pattern.OxygenSaturationStrategy;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DesignPatternTest {

    @Test
    public void testPriorityAlertDecorator() {
        Alert alert = new Alert(1, "condition", System.currentTimeMillis());
        PriorityAlertDecorator priorityAlert = new PriorityAlertDecorator(alert, "HIGH");

        // Checking if the decorated alert retains the properties of the original alert
        assertEquals(1, priorityAlert.getPatientId());
        assertEquals("condition", priorityAlert.getCondition());
        assertEquals(alert.getTimestamp(), priorityAlert.getTimestamp());

        priorityAlert.triggerAlert(); // Verifies that the decorator correctly prints the priority (additional behavior) and triggers the alert
        assertTrue(priorityAlert.isTriggered());
    }

    @Test
    public void testRepeatedAlertDecorator() {

        Alert alert = new Alert(1, "condition", System.currentTimeMillis());
        RepeatedAlertDecorator repeatedAlert = new RepeatedAlertDecorator(alert, 100000);

        assertEquals(1, repeatedAlert.getPatientId());
        assertEquals("condition", repeatedAlert.getCondition());
        assertEquals(alert.getTimestamp(), repeatedAlert.getTimestamp());

        // Trigger the alert immediately after creation
        repeatedAlert.triggerAlert();

        // Check if alert was triggered immediately
        assertTrue(repeatedAlert.isTriggered()); 

        // Reset the state of the RepeatedAlertDecorator
        repeatedAlert.resetTriggered();

        // Trigger the alert again before the interval has passed
        repeatedAlert.triggerAlert();

        // Check if alert was not triggered again
        assertFalse(repeatedAlert.isTriggered()); 
    }

    // ensures that the getInstance() method always returns the same instance
    @Test
    public void testSingletonInstance() {
        DataStorage instance1 = DataStorage.getInstance();
        DataStorage instance2 = DataStorage.getInstance();

        assertNotNull(instance1, "First instance should not be null");
        assertNotNull(instance2, "Second instance should not be null");
        assertSame(instance1, instance2, "Both instances should be the same");

        HealthDataSimulator instance3 = HealthDataSimulator.getInstance();
        HealthDataSimulator instance4 = HealthDataSimulator.getInstance();

        assertNotNull(instance3, "First instance should not be null");
        assertNotNull(instance4, "Second instance should not be null");
        assertSame(instance3, instance4, "Both instances should be the same");
    }

    /*
    ensures that the singleton instance is thread-safe by calling getInstance()
    from multiple threads and checking that the same instance is returned
     */
    @Test
    public void testThreadSafety() throws InterruptedException {
        final DataStorage[] instances = new DataStorage[2];

        Thread thread1 = new Thread(() -> instances[0] = DataStorage.getInstance());
        Thread thread2 = new Thread(() -> instances[1] = DataStorage.getInstance());

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        assertNotNull(instances[0], "First thread instance should not be null");
        assertNotNull(instances[1], "Second thread instance should not be null");
        assertSame(instances[0], instances[1], "Both thread instances should be the same");

        final HealthDataSimulator[] instances2 = new HealthDataSimulator[2];

        Thread thread3 = new Thread(() -> instances2[0] = HealthDataSimulator.getInstance());
        Thread thread4 = new Thread(() -> instances2[1] = HealthDataSimulator.getInstance());

        thread3.start();
        thread4.start();

        thread3.join();
        thread4.join();

        assertNotNull(instances2[0], "First thread instance should not be null");
        assertNotNull(instances2[1], "Second thread instance should not be null");
        assertSame(instances2[0], instances2[1], "Both thread instances should be the same");
    }

    @Test
    void testBloodPressureStrategy() {
        BloodPressureStrategy strategy = new BloodPressureStrategy();

        DataStorage storage = DataStorage.getInstance();
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
        assertTrue(strategy.checkAlert(patient1, records1));
        // Check if the alert (test 2) is triggered
        assertTrue(strategy.checkAlert(patient2, records2));
        // Check if the alert (test 3) is triggered
        assertTrue(strategy.checkAlert(patient3, records3));
        // Check if the alert (test 4) is triggered
        assertTrue(strategy.checkAlert(patient4, records4));
        // Check if the alert (test 5) is not triggered
        assertFalse(strategy.checkAlert(patient5, records5));
        // Check if the alert (test 6) is not triggered
        assertFalse(strategy.checkAlert(patient6, records6));


    }

    @Test
    void testHeartRateStrategy() {
        HeartRateStrategy strategy = new HeartRateStrategy();
        DataStorage storage = DataStorage.getInstance();
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

        // ecg too low
        boolean alert1 = strategy.checkAlert(patient1, records1);
        assertTrue(alert1, "Heart rate alert should trigger due to ECG being too low");

        // ecg too high
        boolean alert2 = strategy.checkAlert(patient2, records2);
        assertTrue(alert2, "Heart rate alert should trigger due to ECG being too high");

        // healthy patient
        boolean alert3 = strategy.checkAlert(patient3, records3);
        assertFalse(alert3, "Heart rate alert should not trigger alert");

    }

    @Test
    void testOxygenSaturationStrategy() {
        OxygenSaturationStrategy strategy = new OxygenSaturationStrategy();
        List<PatientRecord> records, records2, records3, records4;

        Patient patient = new Patient(1);
        Patient patient2 = new Patient(2);
        Patient patient3 = new Patient(3);
        Patient patient4 = new Patient(4);

        DataStorage storage = DataStorage.getInstance();

        storage.addPatientData(1, 92, "Saturation", 1714376789050L);
        storage.addPatientData(2, 91, "Saturation", 1714376789050L);
        storage.addPatientData(3, 98, "BloodSaturation", 1714376789050L); // Initial saturation
        storage.addPatientData(3, 100.0, "WhiteBloodCells", 1714376789050L); // check if filtrates BloodSaturation record types
        storage.addPatientData(3, 93, "BloodSaturation", 1714376799050L); // Rapid drop after less than 10 minutes
        storage.addPatientData(4, 91, "Saturation", 1714376789050L);
        storage.addPatientData(4, 85, "Saturation", 1714376799050L); // Rapid drop after less than 10 minutes


        records = storage.getRecords(1, 1714376789050L, 1714376799050L);
        records2 = storage.getRecords(2, 1714376789050L, 1714376799050L);
        records3 = storage.getRecords(3, 1714376789050L, 1714376799050L);
        records4 = storage.getRecords(4, 1714376789050L, 1714376799050L);

        // healthy patient
        boolean alert = strategy.checkAlert(patient, records);
        // checking edge case, should not trigger alert
        assertFalse(alert, "Oxygen Saturation Strategy should not trigger alert when saturation is 92%");

        // saturation below 92, alert drop is fine
        boolean alert2 = strategy.checkAlert(patient2, records2);
        assertTrue(alert2, "Oxygen Saturation Strategy should trigger alert when saturation is below 92%");

        // saturation is above 92, but has a rapid drop
        boolean alert3 = strategy.checkAlert(patient3, records3);
        assertTrue(alert3,"Oxygen Saturation Strategy should trigger alert when saturation drops suddenly");

        // both saturation below 92% and a rapid drop in saturation level
        boolean alert4 = strategy.checkAlert(patient4, records4);
        assertTrue(alert4, "Oxygen Saturation Strategy should trigger alert when saturation drops suddenly and is below 92%");

    }
    
}
