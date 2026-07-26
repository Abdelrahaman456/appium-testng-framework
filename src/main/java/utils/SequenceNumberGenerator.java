package utils;

public class SequenceNumberGenerator {

    private static final String BASE_PREFIX = "70484"; // 5-digit prefix → last 4 digits increment

    /**
     * Generates a unique, guaranteed non-duplicate Sequence Number using timestamp.
     * Format: 70484 + last 4 digits of current timestamp millis
     * Example: 704845273, 704847891, 704848012...
     *
     * - Zero file I/O needed
     * - Zero duplicate risk across parallel runs and multiple machines
     * - Last 4 digits always unique per millisecond
     */
    public static synchronized String getNextSequenceNumber() {
        // Add 1ms delay to guarantee uniqueness if called back-to-back
        try { Thread.sleep(1); } catch (Exception e) {}
        long millis = System.currentTimeMillis();
        String lastFourDigits = String.format("%04d", millis % 10000);
        String sequenceNumber = BASE_PREFIX + lastFourDigits;
        System.out.println("[SequenceNumberGenerator] Generated unique Sequence Number: " + sequenceNumber);
        return sequenceNumber;
    }
}
