package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * This interface specifies a method that a patient data generator needs to implement.
 */
public interface PatientDataGenerator {

    /**
     * Generates patient data for the specified patient ID and outputs it using the provided output strategy.
     *
     * @param patientId The unique identifier of the patient. Must be a an integer.
     * @param outputStrategy The strategy used to output the generated data. The generated data will be passed to this strategy for output. Must not be null.
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
