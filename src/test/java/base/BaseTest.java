package base;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
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

        // 🚀 CONTINUOUS SESSION REUSE: Initialize driver only if session is not already active!
        if (DriverManager.getDriver() == null) {
            System.out.println("╔══════════════════════════════════════════════════════════╗");
            System.out.println("║  [BaseTest] Initializing Driver Session");
            System.out.println("║  Environment  : " + TestConfig.appEnvName());
            System.out.println("║  Platform     : " + platformName);
            System.out.println("║  Device       : " + deviceName + " (UDID: " + (udid.isEmpty() ? "Auto-detect" : udid) + ")");
            System.out.println("║  App Package  : " + appPackage);
            System.out.println("║  App Path     : " + (appPath.isEmpty() ? "(use installed app)" : appPath));
            System.out.println("╚══════════════════════════════════════════════════════════╝");
            DriverManager.initializeDriver(platformName, deviceName, udid, appPath, appPackage, appActivity);
        } else {
            System.out.println("[SESSION REUSE] Appium session active. Reusing current app session for fast execution!");
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        // If test failed, tear down driver session to guarantee next test starts fresh
        if (!result.isSuccess()) {
            System.out.println("[TEST FAILED: " + result.getName() + "] Tearing down Driver session to reset app state for next test...");
            DriverManager.quitDriver();
        } else {
            System.out.println("[TEST PASSED: " + result.getName() + "] Driver session kept active for continuous fast execution!");
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        System.out.println("=== TEST CLASS COMPLETED: Closing Driver Session ===");
        DriverManager.quitDriver();
    }
}
