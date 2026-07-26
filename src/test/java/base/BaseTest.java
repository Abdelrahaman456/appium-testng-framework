package base;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import utils.DriverManager;

public class BaseTest {

    @Parameters({"platformName", "deviceName", "udid", "appPath", "appPackage", "appActivity"})
    @BeforeMethod(alwaysRun = true)
    public void setUp(
            @Optional("Android") String platformName,
            @Optional("Android Device") String deviceName,
            @Optional("") String udid,
            @Optional("") String appPath,
            @Optional("sa.com.tree.digital.insurance.uat") String appPackage,
            @Optional("sa.com.tree.digital.insurance.MainActivity") String appActivity) {
        
        // Read command-line system properties passed via mvn test -DappPath=... -Dudid=...
        String sysAppPath = System.getProperty("appPath");
        if (sysAppPath != null && !sysAppPath.isEmpty()) {
            appPath = sysAppPath;
        }

        String sysUdid = System.getProperty("udid");
        if (sysUdid != null && !sysUdid.isEmpty()) {
            udid = sysUdid;
        }

        // Smart CI/CD Detection: If running inside GitHub Actions, ignore physical device UDID and connect to Cloud Emulator!
        boolean isCI = System.getenv("GITHUB_ACTIONS") != null || "true".equalsIgnoreCase(System.getProperty("ci"));
        if (isCI) {
            System.out.println("[CI ENVIRONMENT DETECTED] Clearing physical UDID (" + udid + ") to target GitHub Actions Cloud Emulator...");
            udid = "";
        }

        System.out.println("Initializing Driver for " + platformName + " on device: " + deviceName + " (UDID: " + udid + ")");
        DriverManager.initializeDriver(platformName, deviceName, udid, appPath, appPackage, appActivity);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        System.out.println("Tearing down Driver");
        DriverManager.quitDriver();
    }
}
