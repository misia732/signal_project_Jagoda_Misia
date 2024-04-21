package com.cardio_generator.outputs;

/**
 * Serves as an interface for FileOutputStrategy Class.
 * Defines the parameters that have to be declared to output patient's cardio data.
 * 
 */
public interface OutputStrategy {

    /**
     * Defines parameters that have to be specified to print cardio data.
     *
     * @param patientId The ID of the patient associated with the cardio data.
     * @param timestamp The timestamp of the cardio data.
     * @param label     The label describing the type of cardio data.
     * @param data      The cardio data to be printed.
     */
    void output(int patientId, long timestamp, String label, String data);
}
