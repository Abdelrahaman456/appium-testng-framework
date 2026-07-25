package utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;
    private static final int MAX_RETRY_COUNT = 1; // Retry failed test 1 time automatically

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY_COUNT) {
            retryCount++;
            System.out.println("[RETRY ANALYZER] Retrying failed test: " + result.getName() + " (Attempt " + (retryCount + 1) + ")");
            return true;
        }
        return false;
    }
}
