package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
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
    private static ExtentReports extent = ExtentManager.getInstance();
    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    public static ExtentTest getTest() {
        return extentTest.get();
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
    public void onTestStart(ITestResult result) {
        System.out.println("[TEST STARTED] " + result.getName());
        ExtentTest test = extent.createTest(result.getMethod().getMethodName(), result.getMethod().getDescription());
        extentTest.set(test);
        getTest().log(Status.INFO, "Test execution started: " + result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("[TEST PASSED] " + result.getName());
        getTest().log(Status.PASS, MarkupHelper.createLabel("TEST PASSED: " + result.getName(), ExtentColor.GREEN));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("[TEST FAILED] " + result.getName() + " - Reason: " + result.getThrowable().getMessage());
        getTest().log(Status.FAIL, MarkupHelper.createLabel("TEST FAILED: " + result.getName(), ExtentColor.RED));
        getTest().fail(result.getThrowable());

        String screenshotPath = captureFailureArtifacts(result);
        if (screenshotPath != null) {
            try {
                getTest().fail("Failure Screenshot",
                        MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
            } catch (Exception e) {
                System.out.println("[TEST LISTENER] Could not attach screenshot to ExtentReport: " + e.getMessage());
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("[TEST SKIPPED] " + result.getName());
        getTest().log(Status.SKIP, MarkupHelper.createLabel("TEST SKIPPED: " + result.getName(), ExtentColor.ORANGE));
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("=== TEST SUITE RUN FINISHED ===");
        if (extent != null) {
            extent.flush();
            System.out.println("[EXTENT REPORT] Visual HTML Report generated at: target/extent-reports/ExtentReport.html");
        }
    }

    private String captureFailureArtifacts(ITestResult result) {
        try {
            AppiumDriver driver = DriverManager.getDriver();
            if (driver == null) {
                System.out.println("[TEST LISTENER] Driver instance was null. Skipping screenshot capture.");
                return null;
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

            return destFile.getAbsolutePath();

        } catch (Exception e) {
            System.out.println("[TEST LISTENER] Error capturing failure artifacts: " + e.getMessage());
            return null;
        }
    }
}
