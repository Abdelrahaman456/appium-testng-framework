package utils;

import java.io.InputStream;
import java.util.Properties;

/**
 * Centralized config loader for testdata.properties.
 * All test data values are loaded once and cached for the full test run.
 */
public class TestConfig {

    private static final Properties props = new Properties();

    static {
        try (InputStream input = TestConfig.class.getClassLoader()
                .getResourceAsStream("testdata.properties")) {
            if (input != null) {
                props.load(input);
                System.out.println("[TestConfig] testdata.properties loaded successfully.");
            } else {
                System.out.println("[TestConfig] testdata.properties not found! Using hardcoded defaults.");
            }
        } catch (Exception e) {
            System.out.println("[TestConfig] Failed to load testdata.properties: " + e.getMessage());
        }
    }

    public static String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(props.getProperty(key, String.valueOf(defaultValue)).trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    // --- Convenience accessors ---
    public static String phone()              { return get("default.phone",               "500421222"); }
    public static String otp()               { return get("default.otp",                 "1234"); }
    public static String email()             { return get("default.email",               "aashraf@tree.com.sa"); }
    public static String iban()              { return get("default.iban",                "SA6530400108071059170014"); }
    public static String cardNumber()        { return get("default.card.number",         "5123456789012346"); }
    public static String cardExpiry()        { return get("default.card.expiry",         "01/2031"); }
    public static String cardCvv()           { return get("default.card.cvv",            "100"); }
    public static String cardHolder()        { return get("default.card.holder",         "Tree User"); }
    public static String sellerId()          { return get("default.seller.id",           "1313424273"); }
    public static String carYear()           { return get("default.car.year",            "2026"); }
    public static String customCard()        { return get("default.custom.card",         "1254874892"); }
    public static int    elementTimeout()    { return getInt("timeout.element.visible",   10); }
    public static int    paymentTimeout()    { return getInt("timeout.payment.processing",30); }
    public static int    policyTimeout()     { return getInt("timeout.policy.confirmation",30); }
}
