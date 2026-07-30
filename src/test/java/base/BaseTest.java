package base;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import utils.DriverManager;
import utils.TestConfig;

public class BaseTest {

    @Parameters({"platformName", "deviceName", "udid"})
    @BeforeMethod(alwaysRun = true)
    public void setUp(
            @Optional("Android") String platformName,
            @Optional("Android Device") String deviceName,
            @Optional("") String udid) {

        // ─── Read all App settings directly from the active environment config ──────
        String appPath     = TestConfig.appPath();
        String appPackage  = TestConfig.appPackage();
        String appActivity = TestConfig.appActivity();

        // ─── Allow CLI overrides for appPath and UDID via -DappPath= -Dudid= ────────
        String sysAppPath = System.getProperty("appPath");
        if (sysAppPath != null && !sysAppPath.isEmpty()) appPath = sysAppPath;

        String sysUdid = System.getProperty("udid");
        if (sysUdid != null && !sysUdid.isEmpty()) udid = sysUdid;

        // ─── Smart CI/CD Detection ────────────────────────────────────────────────
        boolean isCI = System.getenv("GITHUB_ACTIONS") != null
                || "true".equalsIgnoreCase(System.getProperty("ci"));
        if (isCI) {
            System.out.println("[CI DETECTED] Clearing UDID (" + udid + ") to target Cloud Emulator...");
            udid = "";
        }

        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║  [BaseTest] Initializing Driver");
        System.out.println("║  Environment  : " + TestConfig.appEnvName());
        System.out.println("║  Platform     : " + platformName);
        System.out.println("║  Device       : " + deviceName + " (UDID: " + udid + ")");
        System.out.println("║  App Package  : " + appPackage);
        System.out.println("║  App Path     : " + (appPath.isEmpty() ? "(use installed app)" : appPath));
        System.out.println("╚══════════════════════════════════════════════════════════╝");

        DriverManager.initializeDriver(platformName, deviceName, udid, appPath, appPackage, appActivity);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        System.out.println("Tearing down Driver");
        DriverManager.quitDriver();
    }
}
