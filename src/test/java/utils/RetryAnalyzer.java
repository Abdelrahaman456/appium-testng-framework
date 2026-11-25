package utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;
    private static final int MAX_RETRY_COUNT = 1; // Retry failed test 1 time automatically

    @Override
    public boolean retry(ITestResult result) {
        if (isDeterministicUatGatewayFailure(result)) {
            System.out.println("[RETRY ANALYZER] Skipping retry for deterministic UAT gateway/payment failure: " + result.getName());
            return false;
        }
        if (retryCount < MAX_RETRY_COUNT) {
            retryCount++;
            System.out.println("[RETRY ANALYZER] Retrying failed test: " + result.getName() + " (Attempt " + (retryCount + 1) + ")");
            return true;
        }
        return false;
    }

    private boolean isDeterministicUatGatewayFailure(ITestResult result) {
        Throwable t = result != null ? result.getThrowable() : null;
        if (t == null || t.getMessage() == null) {
            return false;
        }
        String msg = t.getMessage().toLowerCase();
        return msg.contains("policy confirmation screen was not reached")
                || msg.contains("purchase failed")
                || msg.contains("retry payment with valid card should complete 3ds");
    }
}
