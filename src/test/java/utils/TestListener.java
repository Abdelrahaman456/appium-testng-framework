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

    /**
     * Helper method to log step details into ExtentReports directly from BasePage or Page Classes.
     */
    public static void logStep(String message) {
        if (getTest() != null) {
            getTest().log(Status.INFO, "📌 " + message);
        }
    }

    public static void logWarning(String message) {
        if (getTest() != null) {
            getTest().log(Status.WARNING, "⚠️ " + message);
        }
    }

    public static void logPass(String message) {
        if (getTest() != null) {
            getTest().log(Status.PASS, "✅ " + message);
        }
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
        String methodName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();
        if (description == null || description.isEmpty()) {
            description = formatDescriptionFromMethod(methodName);
        }

        System.out.println("[TEST STARTED] " + methodName);
        ExtentTest test = extent.createTest(methodName, description);
        
        // Assign Category based on method name
        String category = assignCategory(methodName);
        test.assignCategory(category);
        test.assignAuthor("Tree QA Automation");
        test.assignDevice("Android Device / Emulator");

        extentTest.set(test);
        getTest().log(Status.INFO, MarkupHelper.createLabel("STARTED: " + methodName, ExtentColor.BLUE));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        long durationMs = result.getEndMillis() - result.getStartMillis();
        double durationSec = durationMs / 1000.0;
        System.out.println("[TEST PASSED] " + result.getName() + " (Duration: " + durationSec + "s)");
        
        getTest().log(Status.PASS, MarkupHelper.createLabel("PASSED: " + result.getName() + " (" + durationSec + "s)", ExtentColor.GREEN));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        long durationMs = result.getEndMillis() - result.getStartMillis();
        double durationSec = durationMs / 1000.0;
        System.out.println("[TEST FAILED] " + result.getName() + " - Reason: " + result.getThrowable().getMessage());
        
        getTest().log(Status.FAIL, MarkupHelper.createLabel("FAILED: " + result.getName() + " (" + durationSec + "s)", ExtentColor.RED));
        getTest().fail(result.getThrowable());

        // Capture both Base64 inline screenshot & File screenshot for 100% portable reporting
        captureAndAttachScreenshots(result);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("[TEST SKIPPED] " + result.getName());
        getTest().log(Status.SKIP, MarkupHelper.createLabel("SKIPPED: " + result.getName(), ExtentColor.ORANGE));
        if (result.getThrowable() != null) {
            getTest().skip(result.getThrowable());
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("=== TEST SUITE RUN FINISHED ===");
        if (extent != null) {
            extent.flush();
            System.out.println("[EXTENT REPORT] Visual HTML Report generated at: target/extent-reports/ExtentReport.html");
        }
    }

    private void captureAndAttachScreenshots(ITestResult result) {
        try {
            AppiumDriver driver = DriverManager.getDriver();
            if (driver == null) {
                System.out.println("[TEST LISTENER] Driver instance was null. Skipping screenshot capture.");
                return;
            }

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = result.getName() + "_" + timeStamp;

            // 1. Base64 Screenshot (Embedded directly inside HTML report for portability)
            String base64Screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
            getTest().fail("<b>Failure Screenshot (Embedded Base64):</b>",
                    MediaEntityBuilder.createScreenCaptureFromBase64String("data:image/png;base64," + base64Screenshot).build());

            // 2. High-Res File Screenshot on Disk
            File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destFile = new File(SCREENSHOT_DIR + fileName + ".png");
            java.nio.file.Files.copy(scrFile.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            System.out.println("[FAILURE ARTIFACT] Screenshot saved to: " + destFile.getAbsolutePath());

            // 3. Dump Page Source XML (DOM Tree)
            String pageSource = driver.getPageSource();
            File xmlFile = new File(SCREENSHOT_DIR + fileName + "_DOM.xml");
            try (FileWriter writer = new FileWriter(xmlFile)) {
                writer.write(pageSource);
            }
            System.out.println("[FAILURE ARTIFACT] Page Source XML saved to: " + xmlFile.getAbsolutePath());

            getTest().info("📄 DOM XML File Artifact: " + xmlFile.getName());

        } catch (Exception e) {
            System.out.println("[TEST LISTENER] Error capturing failure artifacts: " + e.getMessage());
        }
    }

    private String assignCategory(String methodName) {
        if (methodName.contains("Flow1")) return "Flow 1: New Insurance (Sequence)";
        if (methodName.contains("Flow2")) return "Flow 2: New Insurance (Custom Card)";
        if (methodName.contains("Flow3")) return "Flow 3: Ownership Transfer (Sequence)";
        if (methodName.contains("Flow4")) return "Flow 4: Ownership Transfer (Custom Card)";
        if (methodName.contains("Negative") || methodName.contains("Invalid") || methodName.contains("Incorrect") || methodName.contains("Empty")) return "Negative Validation Suite";
        return "General E2E Suite";
    }

    private String formatDescriptionFromMethod(String methodName) {
        // e.g. testFlow1_ComprehensiveCover -> Flow 1: Comprehensive Cover End-to-End Test
        String formatted = methodName.replace("test", "").replaceAll("([A-Z])", " $1").trim();
        return "Automated E2E Test Case: " + formatted;
    }
}
