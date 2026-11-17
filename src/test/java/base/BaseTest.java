package base;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import io.appium.java_client.AppiumDriver;
import utils.DriverManager;
import utils.TestConfig;
import utils.TestListener;

@Listeners(TestListener.class)
public class BaseTest {

    @Parameters({"platformName", "deviceName", "udid"})
    @BeforeMethod(alwaysRun = true)
    public void setUp(
            @Optional("Android") String platformName,
            @Optional("Android Device") String deviceName,
            @Optional("") String udid) {

        pages.BasePage.uatElmDependentBlocked = false;

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
            // Dismiss any startup ANR dialog ("Wait") if app was slow to load
            try {
                Thread.sleep(2000);
                java.util.List<org.openqa.selenium.WebElement> anrButtons = DriverManager.getDriver().findElements(
                    org.openqa.selenium.By.xpath("//*[@resource-id='android:id/aerr_wait' or @text='Wait']"));
                if (!anrButtons.isEmpty()) {
                    System.out.println("[BaseTest] Detected startup ANR dialog. Tapping 'Wait'...");
                    anrButtons.get(0).click();
                    Thread.sleep(1500);
                }
            } catch (Exception ignore) {}
        } else {
            System.out.println("[SESSION REUSE] Appium session active. Reusing current app session for fast execution!");
        }

        // Ensure target app is in foreground
        try {
            AppiumDriver d = DriverManager.getDriver();
            if (d instanceof io.appium.java_client.android.AndroidDriver) {
                String currentPkg = ((io.appium.java_client.android.AndroidDriver) d).getCurrentPackage();
                if (appPackage != null && !appPackage.equals(currentPkg)) {
                    System.out.println("[BaseTest] App not in foreground (current: " + currentPkg + "). Activating " + appPackage + "...");
                    ((io.appium.java_client.InteractsWithApps) d).activateApp(appPackage);
                    Thread.sleep(1500);
                }
            }
        } catch (Exception ignore) {}

        // Ensure app is docked on Home tab before starting any test
        pages.BasePage.uatElmDependentBlocked = false;
        DriverManager.resetAppToHome();

        // Start screen recording for failure replay only when enabled (disabled by default for faster local runs)
        if (utils.TestConfig.recordVideo()) {
            DriverManager.startVideoRecording();
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (!result.isSuccess()) {
            System.out.println("[TEST FAILED: " + result.getName() + "] Resetting app state to Home for next test...");
            try {
                java.io.File scrFile = ((org.openqa.selenium.TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(org.openqa.selenium.OutputType.FILE);
                java.io.File destFile = new java.io.File("target/screenshots/" + result.getName() + "_" + System.currentTimeMillis() + ".png");
                destFile.getParentFile().mkdirs();
                java.nio.file.Files.copy(scrFile.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                System.out.println("[FAILURE SCREENSHOT] Saved to: " + destFile.getAbsolutePath());
            } catch (Exception ex) {}
            if (utils.TestConfig.recordVideo()) {
                DriverManager.stopAndSaveVideoRecording("target/videos/" + result.getName() + "_" + System.currentTimeMillis() + ".mp4");
            }
        } else {
            System.out.println("[TEST PASSED: " + result.getName() + "] Resetting app state to Home for next test...");
            if (utils.TestConfig.recordVideo()) {
                DriverManager.stopVideoRecording();
            }
        }
        DriverManager.resetAppToHome();
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        System.out.println("=== TEST CLASS COMPLETED: Closing Driver Session ===");
        DriverManager.quitDriver();
    }
}
