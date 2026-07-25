package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;

public class ExtentManager {

    private static ExtentReports extent;
    private static final String REPORT_PATH = "target/extent-reports/ExtentReport.html";

    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            File reportDir = new File("target/extent-reports/");
            if (!reportDir.exists()) {
                reportDir.mkdirs();
            }

            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(REPORT_PATH);
            sparkReporter.config().setDocumentTitle("Tree Digital Insurance - Mobile Test Automation Report");
            sparkReporter.config().setReportName("Appium Mobile E2E Test Execution Summary");
            sparkReporter.config().setTheme(Theme.DARK);
            sparkReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");

            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
            extent.setSystemInfo("Application Name", "Tree Digital Insurance (UAT)");
            extent.setSystemInfo("Platform", "Android");
            extent.setSystemInfo("Automation Engine", "Appium 2.x + UiAutomator2");
            extent.setSystemInfo("Framework", "TestNG + Java 17");
            extent.setSystemInfo("Target Device", "Pixel 10 Pro Fold / Physical Device");
        }
        return extent;
    }
}
