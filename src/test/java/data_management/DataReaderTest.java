package data_management;

import com.cardio_generator.outputs.WebSocketOutputStrategy;
import com.data_management.DataReader;
import com.data_management.DataReaderImplementation;
import com.data_management.DataStorage;
import com.data_management.MyWebSocketClient;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DataReaderTest {

    public static final String TEST_DIRECTORY = "test_files";

    @Test
    public void testReadDataFromFile_Success() {
        try {
            // Prepare test data
            File file = new File(TEST_DIRECTORY, "test_data.txt");
            FileWriter writer = new FileWriter(file);
            writer.write("1,80,HeartRate,1623187420000\n");
            writer.write("2,120,BloodPressure,1623187450000\n");
            writer.close();

            // Perform the test
            DataStorage dataStorage = new DataStorage();
            DataReaderImplementation dataReader = new DataReaderImplementation(TEST_DIRECTORY);
            dataReader.readData(dataStorage);

            // Verify that data was added to the data storage
            assert dataStorage.getAllPatients().size() == 2;

            // Clean up test data
            assert file.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



//    @Test
//    public void testReadRealTimeData() throws IOException, InterruptedException {
//        // Mock DataStorage
//        DataStorage mockStorage = new DataStorage() {
//            private StringBuilder storage = new StringBuilder();
//
//            @Override
//            public void storeData(String data) {
//                storage.append(data).append("\n");
//            }
//
//            public String getStoredData() {
//                return storage.toString();
//            }
//        };
//
//        // Mock WebSocket server
//        MyWebSocketClient mockServer = new MyWebSocketClient(//localhost:8080/ws);
//        mockServer.start();
//
//        // DataReader implementation
//        DataReaderImplementation dataReader = new DataReaderImplementation("dummyDirectory");
//
//        // Start reading real-time data
//        Thread readerThread = new Thread(() -> {
//            try {
//                dataReader.readRealTimeData(mockStorage, 8080);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//        readerThread.start();
//
//        // Wait for connection to establish
//        Thread.sleep(1000);
//
//        // Send test data from WebSocket server
//        mockServer.broadcast("Test message 1");
//        mockServer.broadcast("Test message 2");
//
//        // Wait for data to be processed
//        Thread.sleep(1000);
//
//        // Verify that data is correctly stored in DataStorage
//        String storedData = mockStorage.getDataToProcess();
//        assertTrue(storedData.contains("Test message 1"));
//        assertTrue(storedData.contains("Test message 2"));
//
//        // Clean up
//        mockServer.stop();
//    }
//
//    @Test
//    public void testMyWebSocketClient() throws URISyntaxException, InterruptedException {
//        // Mock DataStorage
//        DataStorage mockDataStorage = new DataStorage();
//
//        // Start a WebSocket server
//        MockWebSocketServer webSocketServerMock = new MockWebSocketServer();
//        webSocketServerMock.start();
//
//        // Create WebSocket client
//        MyWebSocketClient webSocketClient = new MyWebSocketClient(new URI("ws://localhost:8080")) {
//            @Override
//            public void onOpen(ServerHandshake handshakedata) {
//                super.onOpen(handshakedata);
//                // Send test messages when connection is open
//                sendTestMessages();
//            }
//        };
//        webSocketClient.setDataStorage(mockDataStorage);
//        webSocketClient.connectBlocking();
//
//        // Wait for messages to be processed
//        Thread.sleep(1000);
//
//        // Stop WebSocket server
//        webSocketServerMock.stop();
//    }
//
//    private void sendTestMessages() {
//        // Send test messages to WebSocket server
//        // You can customize the messages according to your needs
//        // For example:
//        // webSocketClient.send("1,123456789,HeartRate,80");
//        // webSocketClient.send("2,123456790,BloodPressure,120/80");
//    }
//
//
//
//
//
//
//
//
//    // Mock WebSocket server
//    private static class MockWebSocketServer extends Thread {
//        private volatile boolean running = true;
//
//        public void run() {
//            try {
//                while (running) {
//                    // Simulate receiving messages
//                    // You can replace this with your actual WebSocket server logic
//                    Thread.sleep(500);
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//        public void broadcast(String message) {
//            // Simulate broadcasting a message to connected clients
//            message = "broadcastingMessage";
//        }
//
//        public void stopServer() {
//            running = false;
//            interrupt(); // Interrupt the thread to break out of the loop
//        }
//    }
}
