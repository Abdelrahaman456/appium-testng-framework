package utils;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enterprise Performance Benchmark Utility for Mobile Test Automation.
 * Tracks synthetic transaction latencies (form fill, quote rating, add-on re-rate, checkout, 3DS payment)
 * and evaluates them against defined SLA thresholds.
 */
public class PerformanceBenchmark {

    private static final String PERF_REPORT_DIR = "target/perf-reports/";
    private static final String PERF_REPORT_FILE = PERF_REPORT_DIR + "performance_benchmark_report.md";

    // Structure for storing timing samples
    public static class MetricRecord {
        public final String testName;
        public final String stageName;
        public final double durationSec;
        public final double slaSec;
        public final boolean slaMet;
        public final long timestamp;

        public MetricRecord(String testName, String stageName, double durationSec, double slaSec, boolean slaMet) {
            this.testName = testName;
            this.stageName = stageName;
            this.durationSec = durationSec;
            this.slaSec = slaSec;
            this.slaMet = slaMet;
            this.timestamp = System.currentTimeMillis();
        }
    }

    // ThreadLocal storage for active stage timers
    private static final ThreadLocal<Map<String, Long>> activeTimers = ThreadLocal.withInitial(HashMap::new);
    private static final List<MetricRecord> metricRecords = Collections.synchronizedList(new ArrayList<>());
    private static final ThreadLocal<String> currentTestContext = ThreadLocal.withInitial(() -> "Global");

    // Standard SLA thresholds (seconds)
    public static final double SLA_FORM_INPUT = 3.0;
    public static final double SLA_QUOTE_RATING = 8.0;
    public static final double SLA_ADDON_RERATE = 2.5;
    public static final double SLA_CHECKOUT_PREP = 3.5;
    public static final double SLA_3DS_PAYMENT = 6.0;
    public static final double SLA_E2E_JOURNEY = 35.0;

    /**
     * Sets the active test name context for benchmarking.
     */
    public static void setTestContext(String testName) {
        currentTestContext.set(testName);
    }

    /**
     * Starts a performance stopwatch for a specific stage.
     */
    public static void startStage(String stageName) {
        activeTimers.get().put(stageName, System.nanoTime());
        System.out.println("[PERF START] [" + currentTestContext.get() + "] Stage: " + stageName);
    }

    /**
     * Stops the stopwatch, evaluates SLA, logs to console & TestListener, and persists the record.
     */
    public static double stopStage(String stageName, double slaThresholdSec) {
        Long startNano = activeTimers.get().remove(stageName);
        if (startNano == null) {
            System.out.println("[PERF WARN] Stopwatch was not started for stage: " + stageName);
            return 0.0;
        }

        long elapsedNano = System.nanoTime() - startNano;
        double elapsedSec = Math.round((elapsedNano / 1_000_000_000.0) * 100.0) / 100.0;
        boolean slaMet = elapsedSec <= slaThresholdSec;

        String testName = currentTestContext.get();
        MetricRecord record = new MetricRecord(testName, stageName, elapsedSec, slaThresholdSec, slaMet);
        metricRecords.add(record);

        String statusBadge = slaMet ? "✅ [SLA MET]" : "⚠️ [SLA EXCEEDED]";
        String logMessage = String.format("[PERF BENCHMARK] [%s] %s -> Duration: %.2fs (SLA: %.1fs) %s",
                testName, stageName, elapsedSec, slaThresholdSec, statusBadge);
        System.out.println(logMessage);

        TestListener.logStep(logMessage);
        return elapsedSec;
    }

    /**
     * Stop stage with default SLA fallback (5.0s).
     */
    public static double stopStage(String stageName) {
        return stopStage(stageName, 5.0);
    }

    /**
     * Convenience method to benchmark a runnable block of code.
     */
    public static void benchmarkBlock(String stageName, double slaThresholdSec, Runnable action) {
        startStage(stageName);
        try {
            action.run();
        } finally {
            stopStage(stageName, slaThresholdSec);
        }
    }

    /**
     * Returns all metric records collected so far.
     */
    public static List<MetricRecord> getRecords() {
        return new ArrayList<>(metricRecords);
    }

    /**
     * Generates a comprehensive Markdown Performance Summary Table.
     */
    public static String generateMarkdownSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("# ⚡ Tree Digital Insurance - Automated Performance Benchmarks\n\n");
        sb.append("**Run Timestamp**: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n\n");
        sb.append("| Product / Test Journey | Stage / Transaction | Elapsed (s) | SLA Target (s) | SLA Status |\n");
        sb.append("| :--- | :--- | :---: | :---: | :---: |\n");

        synchronized (metricRecords) {
            for (MetricRecord rec : metricRecords) {
                String badge = rec.slaMet ? "🟢 PASSED" : "🔴 EXCEEDED";
                sb.append(String.format("| %s | %s | %.2fs | %.1fs | %s |\n",
                        rec.testName, rec.stageName, rec.durationSec, rec.slaSec, badge));
            }
        }

        sb.append("\n\n> **Benchmark Methodology**: Nano-second stopwatch timestamps captured directly across page interactions without artificial pauses. Quote rating SLA benchmark reflects third-party aggregator response times.\n");
        return sb.toString();
    }

    /**
     * Writes the performance benchmark summary to disk, cumulatively preserving historical and multi-suite records.
     */
    public static void saveReportToDisk() {
        try {
            File dir = new File(PERF_REPORT_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File reportFile = new File(PERF_REPORT_FILE);
            Map<String, String> existingRows = new LinkedHashMap<>();
            if (reportFile.exists()) {
                List<String> lines = java.nio.file.Files.readAllLines(reportFile.toPath());
                for (String line : lines) {
                    if (line.startsWith("| ") && !line.contains("Product / Test Journey") && !line.contains(":---")) {
                        String[] cols = line.split("\\|");
                        if (cols.length >= 6) {
                            String key = cols[1].trim() + "___" + cols[2].trim();
                            existingRows.put(key, line);
                        }
                    }
                }
            }

            synchronized (metricRecords) {
                for (MetricRecord rec : metricRecords) {
                    String badge = rec.slaMet ? "🟢 PASSED" : "🔴 EXCEEDED";
                    String key = rec.testName + "___" + rec.stageName;
                    String row = String.format("| %s | %s | %.2fs | %.1fs | %s |",
                            rec.testName, rec.stageName, rec.durationSec, rec.slaSec, badge);
                    existingRows.put(key, row);
                }
            }

            StringBuilder sb = new StringBuilder();
            sb.append("# ⚡ Tree Digital Insurance - Automated Performance Benchmarks\n\n");
            sb.append("**Last Run Timestamp**: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n\n");
            sb.append("| Product / Test Journey | Stage / Transaction | Elapsed (s) | SLA Target (s) | SLA Status |\n");
            sb.append("| :--- | :--- | :---: | :---: | :---: |\n");
            for (String row : existingRows.values()) {
                sb.append(row).append("\n");
            }
            sb.append("\n\n> **Benchmark Methodology**: Nano-second stopwatch timestamps captured directly across page interactions without artificial pauses. Quote rating SLA benchmark reflects third-party aggregator response times.\n");

            try (PrintWriter out = new PrintWriter(new FileWriter(reportFile))) {
                out.print(sb.toString());
            }
            System.out.println("[PERF BENCHMARK] Performance report successfully saved to: " + PERF_REPORT_FILE);
        } catch (Exception e) {
            System.out.println("[PERF BENCHMARK] Failed to save performance report: " + e.getMessage());
        }
    }
}
