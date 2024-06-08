package com.data_management;

import java.io.IOException;

public interface DataReader {
    /**
     * Reads data from a specified real-time data source (e.g., WebSocket server)
     * and continuously stores it in the data storage.
     *
     * @param dataStorage the storage where real-time data will be continuously stored
     * @param websocketPort the port of the WebSocket server to connect to
     * @throws IOException if there is an error reading the data
     */
    void readRealTimeData(DataStorage dataStorage, int websocketPort) throws IOException;

    void readData(DataStorage dataStorage) throws IOException;
}

