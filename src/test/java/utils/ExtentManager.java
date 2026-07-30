package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;

/**
 * Enterprise ExtentReports 5.x Manager.
 * Features:
 * - Dynamic Light / Dark Mode Toggle Switcher
 * - Executive Dashboards & Analytics
 * - Custom CSS/JS styling for mobile automation suites
 * - Embedded Base64 screenshot support
 */
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
            sparkReporter.config().setReportName("📱 Tree Mobile Appium E2E Execution Dashboard");
            sparkReporter.config().setTheme(Theme.DARK);
            sparkReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss.SSS");

            // 🎨 Inject Custom CSS for Theme Polish & High-Contrast Light/Dark Styling
            sparkReporter.config().setCss("""
                /* Custom ExtentReports Styling & Theme Switcher Enhancements */
                .badge-success { background-color: #10b981 !important; font-size: 90%; }
                .badge-danger  { background-color: #ef4444 !important; font-size: 90%; }
                .badge-warning { background-color: #f59e0b !important; font-size: 90%; }
                .badge-info    { background-color: #3b82f6 !important; font-size: 90%; }
                
                /* Custom Floating Theme Switcher Button */
                #theme-toggle-btn {
                    position: fixed;
                    top: 10px;
                    right: 220px;
                    z-index: 99999;
                    padding: 7px 16px;
                    background: linear-gradient(135deg, #3b82f6, #8b5cf6);
                    color: #ffffff;
                    border: none;
                    border-radius: 20px;
                    font-size: 13px;
                    font-weight: 600;
                    cursor: pointer;
                    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
                    transition: all 0.3s ease;
                }
                #theme-toggle-btn:hover {
                    transform: translateY(-2px);
                    box-shadow: 0 6px 16px rgba(59, 130, 246, 0.5);
                }

                /* Light Mode Override Enhancements */
                body.spark-light {
                    background-color: #f8fafc !important;
                    color: #0f172a !important;
                }
                body.spark-light .card {
                    background-color: #ffffff !important;
                    border: 1px solid #e2e8f0 !important;
                    box-shadow: 0 2px 8px rgba(0,0,0,0.05) !important;
                }
                body.spark-light .table {
                    color: #1e293b !important;
                }
                body.spark-light .nav-left, body.spark-light .nav-right {
                    background-color: #ffffff !important;
                    border-bottom: 1px solid #e2e8f0 !important;
                }
            """);

            // ⚡ Inject JavaScript for Interactive Light/Dark Theme Switcher & Auto-Preference Saving
            sparkReporter.config().setJs("""
                document.addEventListener('DOMContentLoaded', function() {
                    // Create floating theme toggle button
                    var btn = document.createElement('button');
                    btn.id = 'theme-toggle-btn';
                    btn.innerHTML = '🌓 Switch Light / Dark Mode';
                    document.body.appendChild(btn);

                    // Restore saved theme preference if available
                    var savedTheme = localStorage.getItem('extent_report_theme') || 'dark';
                    applyTheme(savedTheme);

                    btn.addEventListener('click', function() {
                        var currentTheme = document.body.classList.contains('spark-light') ? 'light' : 'dark';
                        var targetTheme = (currentTheme === 'dark') ? 'light' : 'dark';
                        applyTheme(targetTheme);
                    });

                    function applyTheme(theme) {
                        if (theme === 'light') {
                            document.body.classList.remove('spark-dark');
                            document.body.classList.add('spark-light');
                            document.documentElement.setAttribute('data-theme', 'light');
                            btn.innerHTML = '☀️ Light Mode (Click for Dark)';
                            btn.style.background = 'linear-gradient(135deg, #0284c7, #2563eb)';
                        } else {
                            document.body.classList.remove('spark-light');
                            document.body.classList.add('spark-dark');
                            document.documentElement.setAttribute('data-theme', 'dark');
                            btn.innerHTML = '🌙 Dark Mode (Click for Light)';
                            btn.style.background = 'linear-gradient(135deg, #3b82f6, #8b5cf6)';
                        }
                        localStorage.setItem('extent_report_theme', theme);
                    }
                });
            """);

            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);

            // 📋 Rich System & Environment Metadata
            extent.setSystemInfo("Application Name", "Tree Digital Insurance (UAT)");
            extent.setSystemInfo("App Package / Target", "sa.com.tree.insurance.uat");
            extent.setSystemInfo("Automation Engine", "Appium 2.x + UiAutomator2");
            extent.setSystemInfo("Test Framework", "Java 17 + TestNG + Page Object Model");
            extent.setSystemInfo("Operating System", System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ")");
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("User / Tester", System.getProperty("user.name"));
            extent.setSystemInfo("Target Device", TestConfig.get("default.target.device", "Physical Android Device / Emulator"));
        }
        return extent;
    }
}
