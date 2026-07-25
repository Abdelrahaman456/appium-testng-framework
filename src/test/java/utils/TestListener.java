package utils;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestListener implements ITestListener {

    private static final String SCREENSHOT_DIR = "target/screenshots/";

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("[TEST STARTED] " + result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("[TEST PASSED] " + result.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("[TEST FAILED] " + result.getName() + " - Reason: " + result.getThrowable().getMessage());
        captureFailureArtifacts(result);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("[TEST SKIPPED] " + result.getName());
    }

    @Override
    public void onStart(ITestContext context) {
        System.out.println("=== TEST SUITE RUN STARTED ===");
        File dir = new File(SCREENSHOT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("=== TEST SUITE RUN FINISHED ===");
    }

    private void captureFailureArtifacts(ITestResult result) {
        try {
            AppiumDriver driver = DriverManager.getDriver();
            if (driver == null) {
                System.out.println("[TEST LISTENER] Driver instance was null. Skipping screenshot capture.");
                return;
            }

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = result.getName() + "_" + timeStamp;

            // 1. Capture High-Res Screenshot
            File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destFile = new File(SCREENSHOT_DIR + fileName + ".png");
            java.nio.file.Files.copy(scrFile.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            System.out.println("[FAILURE ARTIFACT] Screenshot saved to: " + destFile.getAbsolutePath());

            // 2. Dump Page Source XML (DOM Tree)
            String pageSource = driver.getPageSource();
            File xmlFile = new File(SCREENSHOT_DIR + fileName + "_DOM.xml");
            try (FileWriter writer = new FileWriter(xmlFile)) {
                writer.write(pageSource);
            }
            System.out.println("[FAILURE ARTIFACT] Page Source XML saved to: " + xmlFile.getAbsolutePath());

        } catch (Exception e) {
            System.out.println("[TEST LISTENER] Error capturing failure artifacts: " + e.getMessage());
        }
    }
}
