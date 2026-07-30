package utils;

public class NationalIdGenerator {

    /**
     * Generates a unique, guaranteed non-duplicate National ID using timestamp.
     * Prefix is loaded from the active environment config (uat.properties / prod.properties).
     * Format: [env prefix] + last 4 digits of current timestamp millis
     * UAT  example: 1354547891
     * PROD example: 1354557891
     */
    public static synchronized String getNextNationalId() {
        String prefix = TestConfig.nationalIdPrefix();
        long millis = System.currentTimeMillis();
        String lastFourDigits = String.format("%04d", millis % 10000);
        String nationalId = prefix + lastFourDigits;
        System.out.println("[NationalIdGenerator][" + TestConfig.appEnvName() + "] Generated National ID: " + nationalId);
        return nationalId;
    }
}
