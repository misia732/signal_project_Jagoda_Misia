package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Represents a file output strategy for writing patient's cardio data to files in a specified directory.
 */
public class FileOutputStrategy implements OutputStrategy { /* Changed from fileOutputStrategy -> FileOutputStrategy
    Because according to Google Java Style Guide section 5.2.2 class names should be written in UpperCamelCase */

    // changed from BaseDirectory -> baseDirectory, because parameter names are written in lowerCamelCase
    private String baseDirectory;

    // changed to camelCase, from file_map -> fileMap
    public final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();

    /**
     * Creates a new file output strategy with a specified base directory.
     * 
     * @param baseDirectory The path (String) to the base directory, where the files will be stored.
     */
    public FileOutputStrategy(String baseDirectory) { // changed from fileOutputStrategy -> FileOutputStrategy, because the constuctor has the same name as the class

        this.baseDirectory = baseDirectory;
    }

     /**
     * Writes the patient's cardio data to a file in a specified directory.
     *
     * @param patientId The ID of the patient associated with the cardio data.
     * @param timestamp The timestamp of the cardio data.
     * @param label     The label describing the type of cardio data.
     * @param data      The cardio data to be printed.
     */
    @Override
    public void output(int patientId, long timeStamp, String label, String data) { // changed variable name to camelCase, from timestamp -> timeStamp
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