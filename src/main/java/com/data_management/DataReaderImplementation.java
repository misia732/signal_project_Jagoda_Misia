package com.data_management;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class DataReaderImplementation implements DataReader{

    private String outputDirectory;

    public DataReaderImplementation(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    @Override
    public void readRealTimeData(DataStorage dataStorage, int websocketPort) throws IOException {

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
