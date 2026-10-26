package utils;

import java.io.InputStream;
import java.util.Properties;

/**
 * ============================================================
 * Centralized Environment-Aware Configuration Loader.
 *
 * USAGE:
 *   mvn test              → loads UAT  (default)
 *   mvn test -Denv=uat    → loads UAT  config
 *   mvn test -Denv=prod   → loads PROD config
 *
 * Config files live in: src/test/resources/config/
 *   ├── uat.properties    → UAT environment data
 *   └── prod.properties   → PROD environment data
 * ============================================================
 */
public class TestConfig {

    // Active environment resolved once at class load
    private static final String ACTIVE_ENV;
    private static final Properties props = new Properties();

    static {
        // 1. Detect active environment from -Denv= system property (default: uat)
        String envArg = System.getProperty("env", "uat").trim().toLowerCase();
        ACTIVE_ENV = envArg;

        // 2. Load the matching env config file
        String configFile = "config/" + ACTIVE_ENV + ".properties";

        try (InputStream input = TestConfig.class.getClassLoader().getResourceAsStream(configFile)) {
            if (input != null) {
                props.load(input);
                System.out.println("╔══════════════════════════════════════════╗");
                System.out.println("║  ENVIRONMENT : " + ACTIVE_ENV.toUpperCase() + " (loaded: " + configFile + ")");
                System.out.println("║  App Package : " + props.getProperty("app.package", "N/A"));
                System.out.println("║  App Path    : " + props.getProperty("app.path", "N/A"));
                System.out.println("╚══════════════════════════════════════════╝");

                // SAFETY GUARD: Block accidental PROD runs unless explicitly confirmed
                if ("prod".equals(ACTIVE_ENV)) {
                    System.out.println("⚠️  WARNING: Running against PRODUCTION environment!");
                    System.out.println("⚠️  Ensure ONLY approved QA test accounts are used!");
                }
            } else {
                System.out.println("[TestConfig] ❌ Config file not found: " + configFile);
                System.out.println("[TestConfig] ⚠️  Falling back to default testdata.properties...");
                try (InputStream fallback = TestConfig.class.getClassLoader()
                        .getResourceAsStream("testdata.properties")) {
                    if (fallback != null) props.load(fallback);
                } catch (Exception fe) {
                    System.out.println("[TestConfig] ❌ Fallback also failed: " + fe.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("[TestConfig] ❌ Failed to load config: " + e.getMessage());
        }
    }

    // ─── Core getters ────────────────────────────────────────────────────────────

    public static String getActiveEnv() { return ACTIVE_ENV; }

    public static boolean isUat()  { return "uat".equals(ACTIVE_ENV); }
    public static boolean isProd() { return "prod".equals(ACTIVE_ENV); }

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

    // ─── App / Server Settings ───────────────────────────────────────────────────
    public static String appEnvName()     { return get("app.env.name",     ACTIVE_ENV.toUpperCase()); }
    public static String appPackage()     { return get("app.package",      "sa.com.tree.digital.insurance.uat"); }
    public static String appActivity()    { return get("app.activity",     "sa.com.tree.digital.insurance.MainActivity"); }
    public static String appPath()        { return get("app.path",         ""); }
    public static String appiumServerUrl(){ return get("appium.server.url","http://127.0.0.1:4723"); }

    // ─── Customer Data ───────────────────────────────────────────────────────────
    public static String phone()   { return get("default.phone",  "500421222"); }
    public static String otp()     { return get("default.otp",    "1234"); }
    public static String email()   { return get("default.email",  "aashraf@tree.com.sa"); }
    public static String iban()    { return get("default.iban",   "SA6530400108071059170014"); }

    // ─── Vehicle Data ────────────────────────────────────────────────────────────
    public static String cardNumber()   { return get("default.card.number",  "5123456789012346"); }
    public static String cardExpiry()   { return get("default.card.expiry",  "01/2031"); }
    public static String cardCvv()      { return get("default.card.cvv",     "100"); }
    public static String cardHolder()   { return get("default.card.holder",  "Tree User"); }
    public static String sellerId()     { return get("default.seller.id",    "1313424273"); }
    public static String carYear()      { return get("default.car.year",     "2026"); }
    public static String customCard()   { return get("default.custom.card",  "1254874892"); }

    // ─── ID Generator Prefixes ───────────────────────────────────────────────────
    public static String nationalIdPrefix()     { return get("national.id.prefix",     "135454"); }
    public static String sequenceNumberPrefix() { return get("sequence.number.prefix", "70484"); }

    // ─── Timeouts ────────────────────────────────────────────────────────────────
    public static int elementTimeout()  { return getInt("timeout.element.visible",    10); }
    public static int paymentTimeout()  { return getInt("timeout.payment.processing", 30); }
    public static int policyTimeout()   { return getInt("timeout.policy.confirmation",30); }

    // ─── Misc ─────────────────────────────────────────────────────────────────────
    public static String targetDevice() { return get("default.target.device", "Android Device"); }
}
