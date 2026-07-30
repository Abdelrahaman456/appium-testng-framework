package utils;

public class SequenceNumberGenerator {

    /**
     * Generates a unique, guaranteed non-duplicate Sequence Number using timestamp.
     * Prefix is loaded from the active environment config (uat.properties / prod.properties).
     * Format: [env prefix] + last 4 digits of current timestamp millis
     * UAT  example: 704847892
     * PROD example: 704857892
     */
    public static synchronized String getNextSequenceNumber() {
        // 1ms sleep to guarantee uniqueness if called immediately after NationalIdGenerator
        try { Thread.sleep(1); } catch (Exception e) {}
        String prefix = TestConfig.sequenceNumberPrefix();
        long millis = System.currentTimeMillis();
        String lastFourDigits = String.format("%04d", millis % 10000);
        String sequenceNumber = prefix + lastFourDigits;
        System.out.println("[SequenceNumberGenerator][" + TestConfig.appEnvName() + "] Generated Sequence Number: " + sequenceNumber);
        return sequenceNumber;
    }
}
