package data_management;

import com.data_management.DataStorage;
import org.testng.annotations.Test;
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
}
