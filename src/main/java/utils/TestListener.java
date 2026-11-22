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
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * TestNG Listener for ExtentReports, Performance Benchmarks, failure artifact capture,
 * and structured JSON telemetry export for Microsoft Teams daily reporting.
 */
public class TestListener implements ITestListener, ISuiteListener {

    private static final String SCREENSHOT_DIR = "target/screenshots/";
    private static final String TEAMS_REPORT_DIR = "target/teams-report/";
    private static final String TELEMETRY_JSON_PATH = TEAMS_REPORT_DIR + "test-run-summary.json";
    private static final int MAX_STEPS_CAPTURED = 6;
    private static final int MAX_ERROR_MESSAGE_LENGTH = 600;

    private static final ExtentReports extent = ExtentManager.getInstance();
    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    private static final ThreadLocal<List<String>> currentSteps = ThreadLocal.withInitial(ArrayList::new);
    private static final ThreadLocal<List<String>> currentWarnings = ThreadLocal.withInitial(ArrayList::new);
    private static final List<TestTelemetryRecord> TELEMETRY_RECORDS = Collections.synchronizedList(new ArrayList<>());

    private static volatile String activeSuiteName = "Appium Mobile Test Suite";
    private static volatile long suiteStartMillis = System.currentTimeMillis();

    public static class TestTelemetryRecord {
        public final String suiteName;
        public final String className;
        public final String methodName;
        public final String description;
        public final String category;
        public final String status;
        public final boolean retried;
        public final long durationMs;
        public final List<String> lastSteps;
        public final List<String> warnings;
        public final boolean uatElmBlocked;
        public final String exceptionClass;
        public final String errorMessage;
        public final String failingLocation;
        public final String screenshotFile;
        public final String domXmlFile;
        public final String videoFile;
        public final long recordedAtMillis;

        public TestTelemetryRecord(
                String suiteName,
                String className,
                String methodName,
                String description,
                String category,
                String status,
                boolean retried,
                long durationMs,
                List<String> lastSteps,
                List<String> warnings,
                boolean uatElmBlocked,
                String exceptionClass,
                String errorMessage,
                String failingLocation,
                String screenshotFile,
                String domXmlFile,
                String videoFile) {
            this.suiteName = suiteName;
            this.className = className;
            this.methodName = methodName;
            this.description = description;
            this.category = category;
            this.status = status;
            this.retried = retried;
            this.durationMs = durationMs;
            this.lastSteps = lastSteps;
            this.warnings = warnings;
            this.uatElmBlocked = uatElmBlocked;
            this.exceptionClass = exceptionClass;
            this.errorMessage = errorMessage;
            this.failingLocation = failingLocation;
            this.screenshotFile = screenshotFile;
            this.domXmlFile = domXmlFile;
            this.videoFile = videoFile;
            this.recordedAtMillis = System.currentTimeMillis();
        }
    }

    private static class FailureArtifacts {
        String screenshotFile = "";
        String domXmlFile = "";
        String videoFile = "";
    }

    public static ExtentTest getTest() {
        return extentTest.get();
    }

    /**
     * Logs a step message to ExtentReports and retains recent steps for failure triage.
     */
    public static void logStep(String message) {
        if (message != null && !message.trim().isEmpty()) {
            List<String> steps = currentSteps.get();
            steps.add(message.trim());
            while (steps.size() > MAX_STEPS_CAPTURED) {
                steps.remove(0);
            }
        }
        if (getTest() != null) {
            getTest().log(Status.INFO, "📌 " + message);
        }
    }

    /**
     * Logs a warning / self-healing message to ExtentReports and Teams telemetry.
     */
    public static void logWarning(String message) {
        if (message != null && !message.trim().isEmpty()) {
            currentWarnings.get().add(message.trim());
        }
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
    public void onStart(ISuite suite) {
        if (suite != null && suite.getName() != null && !suite.getName().isBlank()) {
            activeSuiteName = suite.getName();
        }
        suiteStartMillis = System.currentTimeMillis();
        TELEMETRY_RECORDS.clear();
    }

    @Override
    public void onFinish(ISuite suite) {
        if (suite != null && suite.getName() != null && !suite.getName().isBlank()) {
            activeSuiteName = suite.getName();
        }
        writeTelemetryJson();
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
        currentSteps.set(new ArrayList<>());
        currentWarnings.set(new ArrayList<>());

        String methodName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        String description = result.getMethod().getDescription();
        if (description == null || description.isEmpty()) {
            description = formatDescriptionFromMethod(methodName);
        }

        System.out.println("[TEST STARTED] " + methodName);
        PerformanceBenchmark.setTestContext(methodName);
        PerformanceBenchmark.startStage("E2E_Journey");
        ExtentTest test = extent.createTest(methodName, description);

        String category = assignCategory(className, methodName);
        test.assignCategory(category);
        test.assignAuthor("Tree QA Automation");
        test.assignDevice("Android Device / Emulator");

        extentTest.set(test);
        getTest().log(Status.INFO, MarkupHelper.createLabel("STARTED: " + methodName, ExtentColor.BLUE));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        PerformanceBenchmark.stopStage("E2E_Journey", PerformanceBenchmark.SLA_E2E_JOURNEY);
        long durationMs = Math.max(0L, result.getEndMillis() - result.getStartMillis());
        double durationSec = durationMs / 1000.0;
        System.out.println("[TEST PASSED] " + result.getName() + " (Duration: " + durationSec + "s)");

        DriverManager.stopVideoRecording();
        recordTelemetry(result, "PASS", new FailureArtifacts());

        if (getTest() != null) {
            getTest().log(Status.PASS, MarkupHelper.createLabel("PASSED: " + result.getName() + " (" + durationSec + "s)", ExtentColor.GREEN));
        }
        clearThreadLocals();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        PerformanceBenchmark.stopStage("E2E_Journey", PerformanceBenchmark.SLA_E2E_JOURNEY);
        long durationMs = Math.max(0L, result.getEndMillis() - result.getStartMillis());
        double durationSec = durationMs / 1000.0;
        String rawMsg = (result.getThrowable() != null) ? result.getThrowable().getMessage() : "Unknown failure";
        System.out.println("[TEST FAILED] " + result.getName() + " - Reason: " + rawMsg);

        if (getTest() != null) {
            getTest().log(Status.FAIL, MarkupHelper.createLabel("FAILED: " + result.getName() + " (" + durationSec + "s)", ExtentColor.RED));
            if (result.getThrowable() != null) {
                getTest().fail(result.getThrowable());
            }
        }

        FailureArtifacts artifacts = captureAndAttachScreenshots(result);
        recordTelemetry(result, "FAIL", artifacts);
        clearThreadLocals();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("[TEST SKIPPED] " + result.getName());
        DriverManager.stopVideoRecording();
        recordTelemetry(result, "SKIP", new FailureArtifacts());
        if (getTest() != null) {
            getTest().log(Status.SKIP, MarkupHelper.createLabel("SKIPPED: " + result.getName(), ExtentColor.ORANGE));
            if (result.getThrowable() != null) {
                getTest().skip(result.getThrowable());
            }
        }
        clearThreadLocals();
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("=== TEST SUITE RUN FINISHED ===");
        PerformanceBenchmark.saveReportToDisk();
        if (extent != null) {
            extent.flush();
            System.out.println("[EXTENT REPORT] Visual HTML Report generated at: target/extent-reports/ExtentReport.html");
        }
        writeTelemetryJson();
    }

    private void clearThreadLocals() {
        currentSteps.remove();
        currentWarnings.remove();
    }

    private void recordTelemetry(ITestResult result, String status, FailureArtifacts artifacts) {
        String suiteName = (result.getTestContext() != null && result.getTestContext().getName() != null)
                ? result.getTestContext().getName()
                : activeSuiteName;
        String className = (result.getTestClass() != null) ? result.getTestClass().getName() : "";
        String methodName = (result.getMethod() != null) ? result.getMethod().getMethodName() : result.getName();
        String description = (result.getMethod() != null && result.getMethod().getDescription() != null
                && !result.getMethod().getDescription().isEmpty())
                ? result.getMethod().getDescription()
                : formatDescriptionFromMethod(methodName);
        String category = assignCategory(className, methodName);
        long durationMs = Math.max(0L, result.getEndMillis() - result.getStartMillis());

        boolean wasRetried = false;
        try {
            wasRetried = result.wasRetried();
        } catch (Throwable ignored) {}

        Throwable throwable = result.getThrowable();
        String exceptionClass = (throwable != null) ? throwable.getClass().getName() : "";
        String errorMessage = (throwable != null) ? cleanErrorMessage(throwable) : "";
        String failingLocation = (throwable != null) ? extractFailingLocation(throwable) : "";

        List<String> stepsSnapshot = new ArrayList<>(currentSteps.get());
        List<String> warningsSnapshot = new ArrayList<>(currentWarnings.get());

        boolean elmBlocked = false;
        try {
            elmBlocked = pages.BasePage.uatElmDependentBlocked;
        } catch (Throwable ignored) {}

        TELEMETRY_RECORDS.add(new TestTelemetryRecord(
                suiteName,
                className,
                methodName,
                description,
                category,
                status,
                wasRetried,
                durationMs,
                stepsSnapshot,
                warningsSnapshot,
                elmBlocked,
                exceptionClass,
                errorMessage,
                failingLocation,
                artifacts.screenshotFile,
                artifacts.domXmlFile,
                artifacts.videoFile
        ));
    }

    private FailureArtifacts captureAndAttachScreenshots(ITestResult result) {
        FailureArtifacts artifacts = new FailureArtifacts();
        try {
            AppiumDriver driver = DriverManager.getDriver();
            if (driver == null) {
                System.out.println("[TEST LISTENER] Driver instance was null. Skipping screenshot capture.");
                return artifacts;
            }

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = result.getName() + "_" + timeStamp;

            // 1. Save Failure Video Recording (.mp4)
            File videoFile = DriverManager.stopAndSaveVideoRecording(SCREENSHOT_DIR + fileName + ".mp4");
            if (videoFile != null && videoFile.exists()) {
                artifacts.videoFile = videoFile.getName();
                if (getTest() != null) {
                    getTest().info("🎥 <b>Failure Video Replay:</b> <a href='../screenshots/" + videoFile.getName() + "' target='_blank'>" + videoFile.getName() + "</a>");
                }
            }

            // 2. Base64 Screenshot (Embedded directly inside HTML report for portability)
            if (getTest() != null) {
                String base64Screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
                getTest().fail("<b>Failure Screenshot (Embedded Base64):</b>",
                        MediaEntityBuilder.createScreenCaptureFromBase64String("data:image/png;base64," + base64Screenshot).build());
            }

            // 3. High-Res File Screenshot on Disk
            File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destFile = new File(SCREENSHOT_DIR + fileName + ".png");
            Files.copy(scrFile.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            artifacts.screenshotFile = destFile.getName();
            System.out.println("[FAILURE ARTIFACT] Screenshot saved to: " + destFile.getAbsolutePath());

            // 4. Dump Page Source XML (DOM Tree)
            String pageSource = driver.getPageSource();
            File xmlFile = new File(SCREENSHOT_DIR + fileName + "_DOM.xml");
            try (FileWriter writer = new FileWriter(xmlFile, StandardCharsets.UTF_8)) {
                writer.write(pageSource);
            }
            artifacts.domXmlFile = xmlFile.getName();
            System.out.println("[FAILURE ARTIFACT] Page Source XML saved to: " + xmlFile.getAbsolutePath());

            if (getTest() != null) {
                getTest().info("📄 DOM XML File Artifact: " + xmlFile.getName());
            }
        } catch (Exception e) {
            System.out.println("[TEST LISTENER] Error capturing failure artifacts: " + e.getMessage());
        }
        return artifacts;
    }

    private static String extractFailingLocation(Throwable throwable) {
        Throwable cursor = throwable;
        while (cursor != null) {
            for (StackTraceElement el : cursor.getStackTrace()) {
                String cls = el.getClassName();
                if ((cls.startsWith("pages.") || cls.startsWith("tests.") || cls.startsWith("utils."))
                        && !cls.equals("utils.TestListener")
                        && !cls.equals("utils.RetryAnalyzer")) {
                    return el.toString();
                }
            }
            cursor = cursor.getCause();
        }
        return "";
    }

    private static String cleanErrorMessage(Throwable throwable) {
        String msg = throwable.getMessage();
        if (msg == null || msg.isBlank()) {
            msg = throwable.toString();
        }
        int docIdx = msg.indexOf("For documentation on this error");
        if (docIdx > 0) {
            msg = msg.substring(0, docIdx);
        }
        int buildIdx = msg.indexOf("Build info:");
        if (buildIdx > 0) {
            msg = msg.substring(0, buildIdx);
        }
        msg = msg.replaceAll("\\s+", " ").trim();
        if (msg.length() > MAX_ERROR_MESSAGE_LENGTH) {
            msg = msg.substring(0, MAX_ERROR_MESSAGE_LENGTH - 3) + "...";
        }
        return msg;
    }

    private void writeTelemetryJson() {
        try {
            File dir = new File(TEAMS_REPORT_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            StringBuilder sb = new StringBuilder(4096);
            sb.append("{\n");
            sb.append("  \"environment\": ").append(jsonStr(TestConfig.appEnvName())).append(",\n");
            sb.append("  \"appPackage\": ").append(jsonStr(TestConfig.appPackage())).append(",\n");
            sb.append("  \"suiteName\": ").append(jsonStr(activeSuiteName)).append(",\n");
            sb.append("  \"startedAt\": ").append(jsonStr(Instant.ofEpochMilli(suiteStartMillis).toString())).append(",\n");
            sb.append("  \"generatedAt\": ").append(jsonStr(Instant.now().toString())).append(",\n");
            sb.append("  \"tests\": [\n");

            List<TestTelemetryRecord> snapshot;
            synchronized (TELEMETRY_RECORDS) {
                snapshot = new ArrayList<>(TELEMETRY_RECORDS);
            }

            for (int i = 0; i < snapshot.size(); i++) {
                TestTelemetryRecord r = snapshot.get(i);
                sb.append("    {\n");
                sb.append("      \"suiteName\": ").append(jsonStr(r.suiteName)).append(",\n");
                sb.append("      \"className\": ").append(jsonStr(r.className)).append(",\n");
                sb.append("      \"methodName\": ").append(jsonStr(r.methodName)).append(",\n");
                sb.append("      \"description\": ").append(jsonStr(r.description)).append(",\n");
                sb.append("      \"category\": ").append(jsonStr(r.category)).append(",\n");
                sb.append("      \"status\": ").append(jsonStr(r.status)).append(",\n");
                sb.append("      \"retried\": ").append(r.retried).append(",\n");
                sb.append("      \"durationMs\": ").append(r.durationMs).append(",\n");
                sb.append("      \"uatElmBlocked\": ").append(r.uatElmBlocked).append(",\n");
                sb.append("      \"exceptionClass\": ").append(jsonStr(r.exceptionClass)).append(",\n");
                sb.append("      \"errorMessage\": ").append(jsonStr(r.errorMessage)).append(",\n");
                sb.append("      \"failingLocation\": ").append(jsonStr(r.failingLocation)).append(",\n");
                sb.append("      \"screenshotFile\": ").append(jsonStr(r.screenshotFile)).append(",\n");
                sb.append("      \"domXmlFile\": ").append(jsonStr(r.domXmlFile)).append(",\n");
                sb.append("      \"videoFile\": ").append(jsonStr(r.videoFile)).append(",\n");
                sb.append("      \"lastSteps\": ").append(jsonStringArray(r.lastSteps)).append(",\n");
                sb.append("      \"warnings\": ").append(jsonStringArray(r.warnings)).append("\n");
                sb.append("    }");
                if (i < snapshot.size() - 1) {
                    sb.append(",");
                }
                sb.append("\n");
            }
            sb.append("  ]\n");
            sb.append("}\n");

            Files.writeString(new File(TELEMETRY_JSON_PATH).toPath(), sb.toString(), StandardCharsets.UTF_8);
            System.out.println("[TEAMS TELEMETRY] Summary JSON saved to: " + TELEMETRY_JSON_PATH);
        } catch (Exception e) {
            System.out.println("[TEAMS TELEMETRY] Failed to write telemetry JSON: " + e.getMessage());
        }
    }

    private static String jsonStringArray(List<String> items) {
        if (items == null || items.isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(jsonStr(items.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }

    private static String jsonStr(String value) {
        if (value == null) {
            return "\"\"";
        }
        StringBuilder sb = new StringBuilder(value.length() + 4);
        sb.append('"');
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        sb.append('"');
        return sb.toString();
    }

    private String assignCategory(String className, String methodName) {
        if (className != null) {
            if (className.endsWith("MotorTest")) return "Motor Insurance";
            if (className.endsWith("NegativeValidationTest")) return "Motor Negative Validation";
            if (className.endsWith("TravelTest")) return "Travel Insurance";
            if (className.endsWith("HomeInsuranceTest")) return "Home Insurance";
            if (className.endsWith("PetTest")) return "Pet Insurance";
            if (className.endsWith("IncomeInsuranceTest")) return "Income Protection";
            if (className.endsWith("MalpracticeTest")) return "Medical Malpractice";
            if (className.endsWith("DomesticWorkerTest")) return "Domestic Worker";
            if (className.endsWith("IndividualHealthTest")) return "Individual Health";
            if (className.endsWith("FamilyHealthTest")) return "Family Health";
            if (className.endsWith("ClaimsTest")) return "Claims Lifecycle (FNOL)";
            if (className.endsWith("PaymentValidationTest")) return "Payment & Card Validation";
            if (className.endsWith("SupportAndLocalizationTest")) return "Support, Complaints, FAQs & Arabic RTL";
            if (className.endsWith("SmeTest")) return "SMEs Portal & Products";
            if (className.endsWith("AuthAndProfileTest")) return "Authentication, Profile & Policies";
        }
        if (methodName.contains("Travel")) return "Travel Insurance";
        if (methodName.contains("Negative") || methodName.contains("Invalid") || methodName.contains("Incorrect") || methodName.contains("Empty")) {
            return "Negative Validation Suite";
        }
        return "General E2E Suite";
    }

    private String formatDescriptionFromMethod(String methodName) {
        String formatted = methodName.replace("test", "").replaceAll("([A-Z])", " $1").trim();
        return "Automated E2E Test Case: " + formatted;
    }
}
