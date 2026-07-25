package utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

public class SequenceNumberGenerator {

    private static final String FILE_PATH = "sequence_number_counter.properties";
    private static final String BASE_PREFIX = "704848";
    private static final int DEFAULT_START_COUNTER = 484; // Initial base counter (704848484)

    /**
     * Generates a unique, auto-incrementing Sequence Number that persists across runs.
     * Increments the last 3 digits (+1) for every test case execution.
     * Example: 704848485, 704848486, 704848487...
     */
    public static synchronized String getNextSequenceNumber() {
        int currentCounter = DEFAULT_START_COUNTER;
        File file = new File(FILE_PATH);
        Properties props = new Properties();

        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                props.load(reader);
                String savedValue = props.getProperty("last_counter");
                if (savedValue != null) {
                    currentCounter = Integer.parseInt(savedValue.trim());
                }
            } catch (Exception e) {
                System.out.println("Could not read sequence counter file, using default: " + e.getMessage());
            }
        }

        // Increment for this test execution (+1 per test case)
        currentCounter++;

        // Save the updated counter back to disk
        props.setProperty("last_counter", String.valueOf(currentCounter));
        try (FileWriter writer = new FileWriter(file)) {
            props.store(writer, "Auto-incremented Sequence Number Counter");
        } catch (Exception e) {
            System.out.println("Could not save sequence counter file: " + e.getMessage());
        }

        // Format: 704848 + three digit number (e.g. 485, 486, 487...)
        String sequenceNumber = BASE_PREFIX + String.format("%03d", currentCounter);
        System.out.println("[SequenceNumberGenerator] Generated unique Sequence Number: " + sequenceNumber);
        return sequenceNumber;
    }
}
