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
                    
                    // HIGH-PERFORMANCE OPTIMIZATION (Rec 2): Skip redundant checks & compress accessibility tree
                    options.setSkipUnlock(true);
                    // options.setSkipServerInstallation(true); // Must be disabled so Appium installs UiAutomator2 on fresh emulators
                    options.setCapability("appium:ignoreUnimportantViews", true);
                    options.setNewCommandTimeout(Duration.ofSeconds(120));
                    
                    // CRITICAL FIX: If the app has any background animations, Appium will wait 10 seconds 
                    // on EVERY single findElement attempt because it thinks the app is "busy".
                    // Setting this to 0 forces Appium to search immediately without waiting for the app to be idle.
                    options.setCapability("appium:waitForIdleTimeout", 0);
                    
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
        if (currentDriver instanceof io.appium.java_client.InteractsWithApps) {
            try {
                String appPackage = TestConfig.appPackage();
                System.out.println("[FAST RESET] Restarting app (" + appPackage + ") while keeping Appium session alive...");
                io.appium.java_client.InteractsWithApps appController = (io.appium.java_client.InteractsWithApps) currentDriver;
                appController.terminateApp(appPackage);
                Thread.sleep(500);
                appController.activateApp(appPackage);
                Thread.sleep(2000);
            } catch (Exception e) {
                System.out.println("[FAST RESET] Soft restart failed (" + e.getMessage() + "). Falling back to full session reset...");
                quitDriver();
            }
        } else {
            quitDriver();
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
