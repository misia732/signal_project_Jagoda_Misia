package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;


/* Changed from fileOutputStrategy -> FileOutputStrategy
   Because according to Google Java Style Guide section 5.2.2 class names should be written in UpperCamelCase
*/
public class FileOutputStrategy implements OutputStrategy {

    // changed from BaseDirectory -> baseDirectory, because parameter names are written in lowerCamelCase
    private String baseDirectory;

    // changed to camelCase, from file_map -> fileMap
    public final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();

    // changed from fileOutputStrategy -> FileOutputStrategy, because the constuctor has the same name as the class
    public FileOutputStrategy(String baseDirectory) {

        this.baseDirectory = baseDirectory;
    }

    // changed variable name to camelCase, from timestamp -> timeStamp
    @Override
    public void output(int patientId, long timeStamp, String label, String data) {
        try {
            // Create the directory
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        // Set the FilePath variable

        // changed to lowerCamelCase, from FilePath -> filePath
        String filePath = fileMap.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString());

        // Write the data to the file
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timeStamp, label, data);
        } catch (Exception e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}