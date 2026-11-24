package base;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
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

        // 🚀 CONTINUOUS SESSION REUSE: Verify session health first; recreate if UiAutomator2 instrumentation died
        if (DriverManager.getDriver() != null && !DriverManager.isSessionAlive()) {
            System.out.println("[BaseTest] Dead UiAutomator2 session detected! Recreating Appium driver session...");
            DriverManager.forceQuitDeadDriver();
        }

        if (DriverManager.getDriver() == null) {
            initializeNewSession(platformName, deviceName, udid, appPath, appPackage, appActivity);
        } else {
            System.out.println("[SESSION REUSE] Appium session active. Reusing current app session for fast execution!");
        }

        // Ensure target app is in foreground, past splash screen, and docked on Home tab (recover once if instrumentation crashes mid-reset)
        try {
            ensureAppInForegroundAndReset(appPackage);
        } catch (Exception e) {
            if (DriverManager.isInstrumentationDeadError(e) || DriverManager.getDriver() == null) {
                System.out.println("[BaseTest] UiAutomator2 instrumentation died during setUp/reset (" + e.getMessage() + "). Recreating session and retrying reset once...");
                DriverManager.forceQuitDeadDriver();
                initializeNewSession(platformName, deviceName, udid, appPath, appPackage, appActivity);
                ensureAppInForegroundAndReset(appPackage);
            } else {
                System.out.println("[BaseTest] Non-fatal warning during post-init foreground/reset: " + e.getMessage());
            }
        }

        // Start screen recording for failure replay only when enabled (disabled by default for faster local runs)
        if (utils.TestConfig.recordVideo()) {
            DriverManager.startVideoRecording();
        }
    }

    private void initializeNewSession(
            String platformName,
            String deviceName,
            String udid,
            String appPath,
            String appPackage,
            String appActivity) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║  [BaseTest] Initializing Driver Session");
        System.out.println("║  Environment  : " + TestConfig.appEnvName());
        System.out.println("║  Platform     : " + platformName);
        System.out.println("║  Device       : " + deviceName + " (UDID: " + (udid.isEmpty() ? "Auto-detect" : udid) + ")");
        System.out.println("║  App Package  : " + appPackage);
        System.out.println("║  App Path     : " + (appPath.isEmpty() ? "(use installed app)" : appPath));
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        DriverManager.initializeDriver(platformName, deviceName, udid, appPath, appPackage, appActivity);
        // Dismiss any startup ANR dialog ("Wait") immediately if present (no blind 2s sleep)
        try {
            java.util.List<org.openqa.selenium.WebElement> anrButtons = DriverManager.getDriver().findElements(
                org.openqa.selenium.By.xpath("//*[@resource-id='android:id/aerr_wait' or @text='Wait']"));
            if (!anrButtons.isEmpty()) {
                System.out.println("[BaseTest] Detected startup ANR dialog. Tapping 'Wait'...");
                anrButtons.get(0).click();
                Thread.sleep(800);
            }
        } catch (Exception e) {
            if (DriverManager.isInstrumentationDeadError(e)) {
                throw new RuntimeException(e);
            }
        }
    }

    private void ensureAppInForegroundAndReset(String appPackage) {
        try {
            AppiumDriver d = DriverManager.getDriver();
            if (d instanceof io.appium.java_client.android.AndroidDriver && appPackage != null) {
                io.appium.java_client.android.AndroidDriver ad = (io.appium.java_client.android.AndroidDriver) d;
                String currentPkg = ad.getCurrentPackage();
                if ("com.google.android.permissioncontroller".equals(currentPkg)
                        || "com.android.permissioncontroller".equals(currentPkg)) {
                    System.out.println("[BaseTest] Detected permission controller dialog (" + currentPkg + "); granting runtime permission...");
                    try {
                        java.util.List<org.openqa.selenium.WebElement> allowBtns = d.findElements(
                            org.openqa.selenium.By.xpath(
                                "//*[@resource-id='com.android.permissioncontroller:id/permission_allow_button'"
                                + " or @resource-id='com.android.permissioncontroller:id/permission_allow_foreground_only_button'"
                                + " or @text='Allow' or @text='While using the app']"
                            )
                        );
                        if (!allowBtns.isEmpty()) {
                            allowBtns.get(0).click();
                            Thread.sleep(500);
                        }
                    } catch (Exception e) {
                        if (DriverManager.isInstrumentationDeadError(e)) {
                            throw e;
                        }
                    }
                    currentPkg = ad.getCurrentPackage();
                }
                if ("com.android.chrome".equals(currentPkg)
                        || (!appPackage.equals(currentPkg)
                                && !d.findElements(org.openqa.selenium.By.xpath("//*[@package='com.android.chrome']")).isEmpty())) {
                    System.out.println("[BaseTest] Detected leftover Chrome Custom Tab (" + currentPkg + "); closing/terminating com.android.chrome...");
                    try {
                        java.util.List<org.openqa.selenium.WebElement> closeBtns = d.findElements(
                            org.openqa.selenium.By.xpath("//*[@resource-id='com.android.chrome:id/close_button' or @content-desc='Close tab']"));
                        if (!closeBtns.isEmpty()) {
                            closeBtns.get(0).click();
                            Thread.sleep(500);
                        }
                    } catch (Exception e) {
                        if (DriverManager.isInstrumentationDeadError(e)) {
                            throw e;
                        }
                    }
                    try {
                        ((io.appium.java_client.InteractsWithApps) d).terminateApp("com.android.chrome");
                        Thread.sleep(400);
                    } catch (Exception e) {
                        if (DriverManager.isInstrumentationDeadError(e)) {
                            throw e;
                        }
                    }
                    currentPkg = ad.getCurrentPackage();
                }
                if (!appPackage.equals(currentPkg)) {
                    System.out.println("[BaseTest] App not in foreground (current: " + currentPkg + "). Activating " + appPackage + "...");
                    ((io.appium.java_client.InteractsWithApps) d).activateApp(appPackage);
                }
                org.openqa.selenium.By appReadyBy = org.openqa.selenium.By.xpath(
                    "//*[@package='" + appPackage + "' and (@clickable='true' or string-length(@resource-id) > 0)]"
                );
                long splashDeadline = System.currentTimeMillis() + 15000L;
                while (System.currentTimeMillis() < splashDeadline) {
                    if (!d.findElements(appReadyBy).isEmpty()) {
                        break;
                    }
                    Thread.sleep(300);
                }
            }
        } catch (Exception e) {
            if (DriverManager.isInstrumentationDeadError(e)) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new RuntimeException(e);
            }
        }

        // Ensure app is docked on Home tab before starting any test
        pages.BasePage.uatElmDependentBlocked = false;
        DriverManager.resetAppToHome();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (!result.isSuccess()) {
            System.out.println("[TEST FAILED: " + result.getName() + "] Resetting app state to Home for next test...");
            try {
                if (DriverManager.getDriver() != null) {
                    java.io.File scrFile = ((org.openqa.selenium.TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(org.openqa.selenium.OutputType.FILE);
                    java.io.File destFile = new java.io.File("target/screenshots/" + result.getName() + "_" + System.currentTimeMillis() + ".png");
                    destFile.getParentFile().mkdirs();
                    java.nio.file.Files.copy(scrFile.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("[FAILURE SCREENSHOT] Saved to: " + destFile.getAbsolutePath());
                }
            } catch (Exception ex) {
                if (DriverManager.isInstrumentationDeadError(ex)) {
                    System.out.println("[BaseTest] UiAutomator2 session dead during failure screenshot; marking session for recreation...");
                    DriverManager.forceQuitDeadDriver();
                }
            }
            if (utils.TestConfig.recordVideo()) {
                DriverManager.stopAndSaveVideoRecording("target/videos/" + result.getName() + "_" + System.currentTimeMillis() + ".mp4");
            }
        } else {
            System.out.println("[TEST PASSED: " + result.getName() + "]");
            if (utils.TestConfig.recordVideo()) {
                DriverManager.stopVideoRecording();
            }
        }
        // Note: Single fast reset runs in @BeforeMethod before the next test (eliminates double reset overhead)
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() {
        System.out.println("=== TEST SUITE COMPLETED: Closing Reused Driver Session ===");
        DriverManager.quitDriver();
    }
}
