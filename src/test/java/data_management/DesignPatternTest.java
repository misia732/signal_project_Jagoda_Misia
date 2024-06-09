package data_management;

import com.alerts.Alert;
import com.data_management.DataStorage;
import com.design_patterns.decorator_pattern.PriorityAlertDecorator;
import com.design_patterns.decorator_pattern.RepeatedAlertDecorator;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DesignPatternTest {

    @Test
    public void testSingletonInstance() {
        DataStorage instance1 = DataStorage.getInstance();
        DataStorage instance2 = DataStorage.getInstance();

        assertNotNull(instance1, "First instance should not be null");
        assertNotNull(instance2, "Second instance should not be null");
        assertSame(instance1, instance2, "Both instances should be the same");
    }

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
    
}
