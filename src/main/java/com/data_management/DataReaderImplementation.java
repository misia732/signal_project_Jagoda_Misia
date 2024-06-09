package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class DataReaderImplementation implements DataReader{

    private String outputDirectory;

    public DataReaderImplementation(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    @Override
    public void readRealTimeData(DataStorage dataStorage, int websocketPort) throws IOException {
        String websocketUrl = "ws://localhost:" + websocketPort;
        try {
            MyWebSocketClient client = new MyWebSocketClient(new URI(websocketUrl)) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    System.out.println("Connected to WebSocket server");
                }

                @Override
                public void onMessage(String message) {
                    // Store the received message in dataStorage
                    dataStorage.storeData(message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("Disconnected from WebSocket server");
                }

                @Override
                public void onError(Exception ex) {
                    ex.printStackTrace();
                }
            };
            client.connect();
        } catch (URISyntaxException e) {
            throw new IOException("Invalid WebSocket URI", e);
        }
    }

    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        File dir = new File(outputDirectory);
        if (!dir.isDirectory()) {
            throw new IOException("Specified path is not a directory");
        }

        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt")); // assuming only text files
        if (files == null || files.length == 0) {
            throw new IOException("No files found in the directory");
        }

        for (File file : files) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    dataStorage.storeData(line);
                }
            }
        }
    }


}
