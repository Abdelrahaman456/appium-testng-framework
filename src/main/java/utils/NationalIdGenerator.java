package utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

public class NationalIdGenerator {

    private static final String FILE_PATH = "national_id_counter.properties";
    private static final String BASE_PREFIX = "1354545";
    private static final int DEFAULT_START_COUNTER = 120; // Initial base counter (1354545120)

    /**
     * Generates a unique, auto-incrementing National ID that persists across runs.
     * Increments the last 3 digits (+1) for every test case execution.
     * Example: 1354545121, 1354545122, 1354545123...
     */
    public static synchronized String getNextNationalId() {
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
                System.out.println("Could not read counter file, using default: " + e.getMessage());
            }
        }

        // Increment for this execution (+1 per test case)
        currentCounter++;

        // Save the updated counter back to disk so future runs continue from here
        props.setProperty("last_counter", String.valueOf(currentCounter));
        try (FileWriter writer = new FileWriter(file)) {
            props.store(writer, "Auto-incremented National ID Counter");
        } catch (Exception e) {
            System.out.println("Could not save counter file: " + e.getMessage());
        }

        // Format: 1354545 + three digit number (e.g. 121, 122, 123...)
        String nationalId = BASE_PREFIX + String.format("%03d", currentCounter);
        System.out.println("[NationalIdGenerator] Generated unique National ID: " + nationalId);
        return nationalId;
    }
}
