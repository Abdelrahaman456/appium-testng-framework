package utils;



import io.appium.java_client.AppiumDriver;

import io.appium.java_client.android.AndroidDriver;

import io.appium.java_client.android.options.UiAutomator2Options;

import io.appium.java_client.ios.IOSDriver;

import io.appium.java_client.ios.options.XCUITestOptions;

import org.openqa.selenium.SessionNotCreatedException;



import java.net.MalformedURLException;

import java.net.URL;

import java.time.Duration;



public class DriverManager {



    // ThreadLocal to support parallel execution

    private static final ThreadLocal<AppiumDriver> driver = new ThreadLocal<>();



    public static AppiumDriver getDriver() {

        return driver.get();

    }



    public static void initializeDriver(String platformName, String deviceName, String udid, String appPath, String appPackage, String appActivity) {

        if (getDriver() == null) {

            try {

                URL appiumServerUrl = new URL("http://127.0.0.1:4723");

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

                    

                    // HIGH-PERFORMANCE OPTIMIZATION: Skip redundant checks & disable window animations
                    options.setSkipUnlock(true);
                    options.setCapability("appium:disableWindowAnimation", true);
                    options.setCapability("appium:skipDeviceInitialization", true);
                    options.setCapability("appium:ignoreUnimportantViews", false);
                    options.setNewCommandTimeout(Duration.ofSeconds(120));

                    

                    // CRITICAL FIX: If the app has any background animations, Appium will wait 10 seconds 

                    // on EVERY single findElement attempt because it thinks the app is "busy".

                    // Setting this to 0 forces Appium to search immediately without waiting for the app to be idle.

                    options.setCapability("appium:waitForIdleTimeout", 0);

                    options.setCapability("appium:enforceXPath1", true);

                    options.setUiautomator2ServerLaunchTimeout(Duration.ofSeconds(60));

                    options.setUiautomator2ServerInstallTimeout(Duration.ofSeconds(60));

                    options.setAdbExecTimeout(Duration.ofSeconds(60));

                    

                    appiumDriver = new AndroidDriver(appiumServerUrl, options);

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



            } catch (MalformedURLException | SessionNotCreatedException e) {

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
                    "//*[@resource-id='btn_home_product_motor' or @content-desc='Motor']"
                );
                org.openqa.selenium.By individualTabBy = org.openqa.selenium.By.xpath(
                    "//*[contains(@content-desc, 'Individual') or contains(@text, 'Individual')]"
                );
                org.openqa.selenium.By homeTabBy = org.openqa.selenium.By.xpath(
                    "//*[@content-desc='Home' or @text='Home']"
                );
                org.openqa.selenium.By inAppBackBy = org.openqa.selenium.By.xpath(
                    "//*[@resource-id='btn_back' or @content-desc='Back']"
                );

                for (int i = 0; i < 4; i++) {
                    try {
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
                    } catch (Exception ignore) {}

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
                    String pkg = TestConfig.appPackage();
                    io.appium.java_client.InteractsWithApps appDriver = (io.appium.java_client.InteractsWithApps) currentDriver;
                    try {
                        appDriver.terminateApp(pkg);
                        Thread.sleep(400);
                    } catch (Exception ignore) {}
                    appDriver.activateApp(pkg);
                    long deadline = System.currentTimeMillis() + 6000;
                    while (System.currentTimeMillis() < deadline) {
                        if (!currentDriver.findElements(motorTileBy).isEmpty() || !currentDriver.findElements(individualTabBy).isEmpty()) {
                            break;
                        }
                        Thread.sleep(250);
                    }
                }
            } catch (Exception e) {
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

