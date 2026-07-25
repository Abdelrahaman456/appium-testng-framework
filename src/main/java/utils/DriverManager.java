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
                    if (appPath != null && !appPath.isEmpty()) {
                        options.setApp(appPath);
                    } else if (appPackage != null && !appPackage.isEmpty() && appActivity != null && !appActivity.isEmpty()) {
                        options.setAppPackage(appPackage);
                        options.setAppActivity(appActivity);
                    }
                    options.setAutomationName("UiAutomator2");
                    options.setAutoGrantPermissions(true);
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
            getDriver().quit();
            driver.remove();
        }
    }
}
