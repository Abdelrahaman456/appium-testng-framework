package utils;



import io.appium.java_client.AppiumDriver;

import io.appium.java_client.android.AndroidDriver;

import io.appium.java_client.android.options.UiAutomator2Options;

import io.appium.java_client.ios.IOSDriver;

import io.appium.java_client.ios.options.XCUITestOptions;

import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriverException;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Locale;

public class DriverManager {

    // ThreadLocal to support parallel execution
    private static final ThreadLocal<AppiumDriver> driver = new ThreadLocal<>();

    public static AppiumDriver getDriver() {
        return driver.get();
    }

    /**
     * Checks a Throwable and its cause chain for fatal UiAutomator2 instrumentation,
     * dead Appium session, or dropped ADB transport errors that require session recreation.
     */
    public static boolean isInstrumentationDeadError(Throwable t) {
        Throwable curr = t;
        int depth = 0;
        while (curr != null && depth < 10) {
            if (curr instanceof NoSuchSessionException || curr instanceof SessionNotCreatedException) {
                return true;
            }
            String className = curr.getClass().getName();
            String message = curr.getMessage() != null ? curr.getMessage() : "";
            String combined = (className + " " + message).toLowerCase(Locale.ROOT);
            if (combined.contains("instrumentation process is not running")
                    || combined.contains("cannot be proxied to uiautomator2 server")
                    || combined.contains("nosuchsessionexception")
                    || combined.contains("sessionnotcreatedexception")
                    || combined.contains("invalid session id")
                    || combined.contains("session is either terminated or not started")
                    || combined.contains("socket hang up")
                    || combined.contains("econnrefused")
                    || combined.contains("device offline")
                    || (combined.contains("device") && combined.contains("not found"))) {
                return true;
            }
            curr = curr.getCause();
            depth++;
        }
        return false;
    }

    /**
     * Verifies the on-device UiAutomator2 instrumentation server is alive by executing
     * a direct element lookup (`/*`) that proxies to `io.appium.uiautomator2.server`.
     */
    public static boolean isSessionAlive() {
        AppiumDriver currentDriver = getDriver();
        if (currentDriver == null) {
            return false;
        }
        try {
            currentDriver.findElements(org.openqa.selenium.By.xpath("/*"));
            return true;
        } catch (Throwable t) {
            if (isInstrumentationDeadError(t)) {
                return false;
            }
            try {
                String pageSource = currentDriver.getPageSource();
                return pageSource != null && !pageSource.isEmpty();
            } catch (Throwable t2) {
                return false;
            }
        }
    }

    /**
     * Tears down a broken or dead Appium session and clears the ThreadLocal reference
     * so the next test or RetryAnalyzer attempt creates a clean UiAutomator2 session.
     */
    public static void forceQuitDeadDriver() {
        try {
            AppiumDriver currentDriver = getDriver();
            if (currentDriver != null) {
                currentDriver.quit();
            }
        } catch (Exception ignore) {
        } finally {
            driver.remove();
        }
    }



    public static void initializeDriver(String platformName, String deviceName, String udid, String appPath, String appPackage, String appActivity) {

        if (getDriver() == null) {

            try {

                URL appiumServerUrl = new URL(TestConfig.appiumServerUrl());

                AppiumDriver appiumDriver = null;



                if (platformName.equalsIgnoreCase("Android")) {

                    UiAutomator2Options options = new UiAutomator2Options();

                    options.setDeviceName(deviceName);

                    if (udid != null && !udid.isEmpty()) {

                        options.setUdid(udid);

                    }

                    if (appPath != null && !appPath.isEmpty() && new java.io.File(appPath).exists()) {

                        options.setApp(appPath);

                    }

                    if (appPackage != null && !appPackage.isEmpty() && appActivity != null && !appActivity.isEmpty()) {

                        options.setAppPackage(appPackage);

                        options.setAppActivity(appActivity);

                    }

                    options.setAutomationName("UiAutomator2");

                    options.setAutoGrantPermissions(true);

                    options.setNoReset(true);

                    

                    // HIGH-PERFORMANCE OPTIMIZATION: Skip redundant checks (keep animation scale=1 so Flutter splash transitions complete)
                    options.setSkipUnlock(true);
                    if (System.getenv("GITHUB_ACTIONS") == null) {
                        options.setCapability("appium:skipDeviceInitialization", true);
                    }
                    options.setCapability("appium:ignoreUnimportantViews", false);
                    options.setNewCommandTimeout(Duration.ofSeconds(120));

                    

                    // CRITICAL FIX: If the app has any background animations, Appium will wait 10 seconds 

                    // on EVERY single findElement attempt because it thinks the app is "busy".

                    // Setting this to 0 forces Appium to search immediately without waiting for the app to be idle.

                    options.setCapability("appium:waitForIdleTimeout", 0);
                    options.setCapability("appium:enforceXPath1", true);
                    options.setUiautomator2ServerLaunchTimeout(Duration.ofSeconds(90));
                    options.setUiautomator2ServerInstallTimeout(Duration.ofSeconds(90));
                    options.setAdbExecTimeout(Duration.ofSeconds(90));

                    WebDriverException lastCreationException = null;
                    int maxAttempts = 6;
                    for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                        try {
                            appiumDriver = new AndroidDriver(appiumServerUrl, options);
                            lastCreationException = null;
                            break;
                        } catch (WebDriverException e) {
                            lastCreationException = e;
                            String msg = e.getMessage() != null ? e.getMessage().toLowerCase(Locale.ROOT) : "";
                            boolean emulatorRebooting = msg.contains("could not find a connected android device")
                                    || msg.contains("can't find service: settings")
                                    || msg.contains("device offline")
                                    || msg.contains("still being initialized")
                                    || msg.contains("socket hang up")
                                    || msg.contains("instrumentation process is not running");
                            System.out.println("[DriverManager] AndroidDriver creation attempt " + attempt + "/" + maxAttempts
                                    + " failed" + (emulatorRebooting ? " (emulator reboot/booting detected)" : "") + ": " + e.getMessage());
                            if (attempt < maxAttempts) {
                                long backoffMs = emulatorRebooting ? 10000L : 3000L;
                                if (!emulatorRebooting && attempt >= 2) {
                                    break;
                                }
                                try {
                                    Thread.sleep(backoffMs);
                                } catch (InterruptedException ie) {
                                    Thread.currentThread().interrupt();
                                    break;
                                }
                            }
                        }
                    }
                    if (appiumDriver == null && lastCreationException != null) {
                        throw lastCreationException;
                    }

                } else if (platformName.equalsIgnoreCase("iOS")) {

                    XCUITestOptions options = new XCUITestOptions();

                    options.setDeviceName(deviceName);

                    if (udid != null && !udid.isEmpty()) {

                        options.setUdid(udid);

                    }

                    if (appPath != null && !appPath.isEmpty()) {

                        options.setApp(appPath);

                    }

                    options.setAutomationName("XCUITest");

                    

                    appiumDriver = new IOSDriver(appiumServerUrl, options);

                } else {

                    throw new IllegalArgumentException("Unsupported platform: " + platformName);

                }



                // Removed implicitlyWait because mixing it with explicit waits causes massive performance issues

                // driver.manage().timeouts().implicitlyWait(...)

                driver.set(appiumDriver);



            } catch (MalformedURLException | WebDriverException e) {

                e.printStackTrace();

                throw new RuntimeException("Failed to initialize Appium Driver", e);

            }

        }

    }



    public static void quitDriver() {

        if (getDriver() != null) {

            try {

                stopVideoRecording();

            } catch (Exception ignored) {}

            getDriver().quit();

            driver.remove();

        }

    }



    /**

     * Fast App Reset (~2 seconds): Restarts the app to the Home screen without destroying the UiAutomator2 session.

     * Saves ~25 seconds per test compared to quitDriver() + initializeDriver().

     */

    private static void closeChromeCustomTabIfActive(AppiumDriver currentDriver, String pkg) {
        try {
            String currentPkg = (currentDriver instanceof io.appium.java_client.android.AndroidDriver)
                    ? ((io.appium.java_client.android.AndroidDriver) currentDriver).getCurrentPackage()
                    : null;
            boolean chromeActive = "com.android.chrome".equals(currentPkg)
                    || ((currentPkg == null || (pkg != null && !pkg.equals(currentPkg)))
                            && !currentDriver.findElements(org.openqa.selenium.By.xpath("//*[@package='com.android.chrome']")).isEmpty());
            if (!chromeActive) {
                return;
            }
            System.out.println("[FAST RESET] Chrome Custom Tab active (" + currentPkg + "); closing/terminating com.android.chrome...");
            try {
                java.util.List<org.openqa.selenium.WebElement> closeBtns = currentDriver.findElements(
                    org.openqa.selenium.By.xpath("//*[@resource-id='com.android.chrome:id/close_button' or @content-desc='Close tab']"));
                if (!closeBtns.isEmpty()) {
                    closeBtns.get(0).click();
                    Thread.sleep(500);
                }
            } catch (Exception ignore) {}
            if (currentDriver instanceof io.appium.java_client.InteractsWithApps) {
                try {
                    ((io.appium.java_client.InteractsWithApps) currentDriver).terminateApp("com.android.chrome");
                    Thread.sleep(400);
                } catch (Exception ignore) {}
                if (pkg != null && currentDriver instanceof io.appium.java_client.android.AndroidDriver) {
                    String afterPkg = ((io.appium.java_client.android.AndroidDriver) currentDriver).getCurrentPackage();
                    if (!pkg.equals(afterPkg)) {
                        ((io.appium.java_client.InteractsWithApps) currentDriver).activateApp(pkg);
                    }
                }
            }
        } catch (Exception e) {
            if (isInstrumentationDeadError(e)) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new RuntimeException(e);
            }
        }
    }

    public static void resetAppToHome() {
        AppiumDriver currentDriver = getDriver();
        if (currentDriver != null) {
            try {
                // Only hide keyboard if actually shown (avoids ~500ms-1s wasted roundtrip per reset)
                try {
                    if (currentDriver instanceof io.appium.java_client.android.AndroidDriver) {
                        io.appium.java_client.android.AndroidDriver ad = (io.appium.java_client.android.AndroidDriver) currentDriver;
                        if (ad.isKeyboardShown()) {
                            ad.hideKeyboard();
                        }
                    }
                } catch (Exception ignore) {}

                org.openqa.selenium.By motorTileBy = org.openqa.selenium.By.xpath(
                    "//*[@resource-id='btn_home_product_motor' or @content-desc='Motor' or @text='Motor' or @content-desc='المركبات' or @text='المركبات']"
                );
                org.openqa.selenium.By individualTabBy = org.openqa.selenium.By.xpath(
                    "//*[@content-desc='Individual' or @text='Individual' or @content-desc='فردي' or @text='فردي']"
                );
                org.openqa.selenium.By homeTabBy = org.openqa.selenium.By.xpath(
                    "//*[@content-desc='Home' or @text='Home' or @content-desc='الرئيسية' or @text='الرئيسية']"
                );
                org.openqa.selenium.By inAppBackBy = org.openqa.selenium.By.xpath(
                    "//*[@resource-id='btn_back' or @content-desc='Back'] | " +
                    "//android.widget.ImageView[@clickable='true' and (@content-desc='Login' or @content-desc='FAQs' or @content-desc='Contact us' or @content-desc='Complaints tracking' or contains(@content-desc, 'About') or contains(@content-desc, 'Privacy'))]"
                );

                String pkg = TestConfig.appPackage();
                org.openqa.selenium.By appInteractiveBy = org.openqa.selenium.By.xpath(
                    "//*[@package='" + pkg + "' and (@clickable='true' or string-length(@resource-id) > 0)]"
                );
                org.openqa.selenium.By launcherBy = org.openqa.selenium.By.xpath(
                    "//*[@package='com.google.android.apps.nexuslauncher']"
                );

                for (int i = 0; i < 4; i++) {
                    try {
                        closeChromeCustomTabIfActive(currentDriver, pkg);
                        // Guard against pressing Android Back while on nexuslauncher or app splash screen
                        if (!currentDriver.findElements(launcherBy).isEmpty() && currentDriver instanceof io.appium.java_client.InteractsWithApps) {
                            System.out.println("[FAST RESET] Launcher detected; activating " + pkg + " and waiting for splash to finish...");
                            ((io.appium.java_client.InteractsWithApps) currentDriver).activateApp(pkg);
                        }
                        if (currentDriver.findElements(appInteractiveBy).isEmpty()) {
                            long splashWaitEnd = System.currentTimeMillis() + 12000L;
                            while (System.currentTimeMillis() < splashWaitEnd) {
                                if (!currentDriver.findElements(appInteractiveBy).isEmpty()) {
                                    break;
                                }
                                Thread.sleep(300);
                            }
                        }

                        // 1. Already on Home -> Individual tab
                        java.util.List<org.openqa.selenium.WebElement> motorTile = currentDriver.findElements(motorTileBy);
                        if (!motorTile.isEmpty() && motorTile.get(0).isDisplayed()) {
                            System.out.println("[FAST RESET] Successfully on Home screen!");
                            return;
                        }

                        // 2. Already on Home -> SMEs or All products tab: tap Individual tab directly (0.3s instead of app restart!)
                        java.util.List<org.openqa.selenium.WebElement> indTabs = currentDriver.findElements(individualTabBy);
                        if (!indTabs.isEmpty() && indTabs.get(0).isDisplayed()) {
                            indTabs.get(0).click();
                            Thread.sleep(300);
                            if (!currentDriver.findElements(motorTileBy).isEmpty()) {
                                System.out.println("[FAST RESET] Switched to Home -> Individual tab directly!");
                                return;
                            }
                        }

                        // 3. On another bottom-nav tab (Support / My profile / My Policies): tap Home tab directly
                        java.util.List<org.openqa.selenium.WebElement> homeTabs = currentDriver.findElements(homeTabBy);
                        if (!homeTabs.isEmpty() && homeTabs.get(0).isDisplayed()) {
                            homeTabs.get(0).click();
                            Thread.sleep(350);
                            if (!currentDriver.findElements(motorTileBy).isEmpty() || !currentDriver.findElements(individualTabBy).isEmpty()) {
                                java.util.List<org.openqa.selenium.WebElement> indAfter = currentDriver.findElements(individualTabBy);
                                if (currentDriver.findElements(motorTileBy).isEmpty() && !indAfter.isEmpty()) {
                                    indAfter.get(0).click();
                                    Thread.sleep(250);
                                }
                                System.out.println("[FAST RESET] Successfully docked to Home tab via locator!");
                                return;
                            }
                        }

                        // 4. In-app back button present (sub-screens)
                        java.util.List<org.openqa.selenium.WebElement> backBtns = currentDriver.findElements(inAppBackBy);
                        if (!backBtns.isEmpty() && backBtns.get(0).isDisplayed()) {
                            System.out.println("[FAST RESET] Found in-app Back button. Tapping to return towards Home...");
                            backBtns.get(0).click();
                            Thread.sleep(350);
                            continue;
                        }
                    } catch (Exception e) {
                        if (isInstrumentationDeadError(e)) {
                            throw e;
                        }
                    }

                    // Never press Android Back if still on splash or nexuslauncher
                    if (currentDriver.findElements(appInteractiveBy).isEmpty() || !currentDriver.findElements(launcherBy).isEmpty()) {
                        System.out.println("[FAST RESET] Skipping Android Back because app is on splash screen or launcher.");
                        break;
                    }

                    System.out.println("[FAST RESET] Not on Home yet. Pressing Android Back (" + (i + 1) + "/4)...");
                    if (currentDriver instanceof io.appium.java_client.android.AndroidDriver) {
                        ((io.appium.java_client.android.AndroidDriver) currentDriver).pressKey(
                            new io.appium.java_client.android.nativekey.KeyEvent(io.appium.java_client.android.nativekey.AndroidKey.BACK)
                        );
                    }
                    Thread.sleep(300);
                }

                // If navigation did not reach Home, restart the app cleanly and poll dynamically for Home
                if (currentDriver instanceof io.appium.java_client.InteractsWithApps) {
                    System.out.println("[FAST RESET] Navigation did not reach Home. Restarting app cleanly...");
                    closeChromeCustomTabIfActive(currentDriver, pkg);
                    io.appium.java_client.InteractsWithApps appDriver = (io.appium.java_client.InteractsWithApps) currentDriver;
                    try {
                        appDriver.terminateApp(pkg);
                        Thread.sleep(400);
                    } catch (Exception ignore) {}
                    appDriver.activateApp(pkg);
                    // Poll up to 20s for Home (exits immediately once Home is visible; covers slow splash on cold emulators)
                    long deadline = System.currentTimeMillis() + 20000;
                    while (System.currentTimeMillis() < deadline) {
                        if (!currentDriver.findElements(motorTileBy).isEmpty() || !currentDriver.findElements(individualTabBy).isEmpty()) {
                            break;
                        }
                        Thread.sleep(250);
                    }
                }
            } catch (Exception e) {
                if (isInstrumentationDeadError(e)) {
                    System.out.println("[FAST RESET] UiAutomator2 instrumentation dead; forcing session recreation...");
                    forceQuitDeadDriver();
                    if (e instanceof RuntimeException) {
                        throw (RuntimeException) e;
                    }
                    throw new RuntimeException("UiAutomator2 instrumentation dead during resetAppToHome", e);
                }
                System.out.println("[FAST RESET] Back navigation exception: " + e.getMessage());
            }
        }
    }



    /**

     * Starts Appium screen recording for the current test case.

     */

    public static void startVideoRecording() {

        AppiumDriver currentDriver = getDriver();

        if (currentDriver instanceof io.appium.java_client.screenrecording.CanRecordScreen) {

            try {

                ((io.appium.java_client.screenrecording.CanRecordScreen) currentDriver).startRecordingScreen();

            } catch (Exception e) {

                System.out.println("[VIDEO RECORDER] Could not start screen recording: " + e.getMessage());

            }

        }

    }



    /**

     * Stops screen recording and discards the buffer (used when a test passes to save disk space).

     */

    public static void stopVideoRecording() {

        AppiumDriver currentDriver = getDriver();

        if (currentDriver instanceof io.appium.java_client.screenrecording.CanRecordScreen) {

            try {

                ((io.appium.java_client.screenrecording.CanRecordScreen) currentDriver).stopRecordingScreen();

            } catch (Exception ignored) {}

        }

    }



    /**

     * Stops screen recording and writes the .mp4 video file to disk (used when a test fails).

     */

    public static java.io.File stopAndSaveVideoRecording(String targetFilePath) {

        AppiumDriver currentDriver = getDriver();

        if (currentDriver instanceof io.appium.java_client.screenrecording.CanRecordScreen) {

            try {

                String base64Video = ((io.appium.java_client.screenrecording.CanRecordScreen) currentDriver).stopRecordingScreen();

                if (base64Video != null && !base64Video.isEmpty()) {

                    byte[] decodedBytes = java.util.Base64.getDecoder().decode(base64Video);

                    java.io.File videoFile = new java.io.File(targetFilePath);

                    videoFile.getParentFile().mkdirs();

                    java.nio.file.Files.write(videoFile.toPath(), decodedBytes);

                    System.out.println("[FAILURE ARTIFACT] Screen recording video saved to: " + videoFile.getAbsolutePath());

                    return videoFile;

                }

            } catch (Exception e) {

                System.out.println("[VIDEO RECORDER] Could not save failure video: " + e.getMessage());

            }

        }

        return null;

    }

}

