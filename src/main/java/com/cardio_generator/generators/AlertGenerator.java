package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Generates alerts for patients based on a randomized process.
 */
public class AlertGenerator implements PatientDataGenerator {


    public static final Random randomGenerator = new Random();

    //Changed variable name to camelCase; AlertStates -> alertStates
    private boolean[] alertStates; // false = resolved, true = pressed

    /**
     * Creates new alert generator for a specified number of patients.
     * 
     * @param patientCount The number (int) of patients for which alert data will be generated.
     */
    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1];
    }

    /**
     * Generates alerts for specific patients identified by an ID using a specified output strategy.
     * 
     * @param patientId The ID of a patient (int) for which alert will be generated.
     * @param outputStrategy The strategy (OutputStrategy) followed to output alerts.
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertStates[patientId]) {
                if (randomGenerator.nextDouble() < 0.9) { // 90% chance to resolve
                    alertStates[patientId] = false;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                //Changed variable name to camelCase; Lambda - > lambda
                double lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency
                //Changed variable name, because one-character parameter names in public methods should be avoided; p -> probability
                double probability = -Math.expm1(-lambda); // Probability of at least one alert in the period
                boolean alertTriggered = randomGenerator.nextDouble() < probability;

                if (alertTriggered) {
                    alertStates[patientId] = true;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();
        }
    }
}
