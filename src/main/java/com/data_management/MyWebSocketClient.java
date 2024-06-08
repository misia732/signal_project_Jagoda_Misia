package com.data_management;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class MyWebSocketClient extends WebSocketClient{

    private DataStorage dataStorage;

    public MyWebSocketClient(URI serverURI, DataStorage dataStorage){
        super(serverURI);
        this.dataStorage = dataStorage;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Client connected to server.");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Message received: " + message);
        // Parse the message and store data
        parseAndStoreMessage(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed with code: " + code + "; reason: " + reason);
    }

    @Override
    public void onError(Exception e) {
        System.err.println("An error occurred:" + e);
    }

    private void parseAndStoreMessage(String message) {
    
        String[] parts = message.split(",");
        if (parts.length == 4) {
            try {
                int patientId = Integer.parseInt(parts[0]);
                long timestamp = Long.parseLong(parts[1]);
                String recordType = parts[2];
                double measurementValue = Double.parseDouble(parts[3]);

                dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);
            } catch (NumberFormatException e) {
                System.err.println("Failed to parse message: " + message);
            }
        } else {
            System.err.println("Invalid message format: " + message);
        }
    }

    public static void main(String[] args) {
        String serverUri = "ws://localhost:8080"; // Replace with your WebSocket server URI
        DataStorage storage = new DataStorage();
        try {
            MyWebSocketClient client = new MyWebSocketClient(new URI(serverUri), storage);
            client.connect();

            // Keep the main thread alive to keep the client running
            while (true) {
                Thread.sleep(1000);
            }
        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
