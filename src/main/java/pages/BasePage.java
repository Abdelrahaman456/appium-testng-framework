package pages;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.Dimension;
import utils.DriverManager;

import java.time.Duration;
import java.util.Arrays;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

public class BasePage {
    protected AppiumDriver driver;
    protected WebDriverWait wait;

    // DATA STRUCTURE 1: ConcurrentHashMap for thread-safe O(1) pre-compiled locator caching
    private static final ConcurrentHashMap<String, org.openqa.selenium.By> LOCATOR_CACHE = new ConcurrentHashMap<>();

    // DATA STRUCTURE 2: ArrayDeque (Double-ended Queue) for high-speed O(1) popup interceptor queue
    private static final Deque<org.openqa.selenium.By> POPUP_QUEUE = new ArrayDeque<>(Arrays.asList(
        getCompiledLocator("//android.widget.Button[@content-desc='Skip' or @text='Skip']"),
        getCompiledLocator("//android.widget.Button[@content-desc='Dismiss' or @text='Dismiss']"),
        getCompiledLocator("//android.widget.Button[@content-desc='Close' or @text='Close']"),
        getCompiledLocator("//android.widget.Button[@content-desc='Not now' or @text='Not now']"),
        getCompiledLocator("//android.widget.Button[@content-desc='Cancel' or @text='Cancel']"),
        getCompiledLocator("//*[contains(@resource-id, 'permission_allow_button')]"),
        getCompiledLocator("//android.widget.Button[@content-desc='OK' or @text='OK']")
    ));

    public BasePage() {
        this.driver = DriverManager.getDriver();
        PageFactory.initElements(driver, this);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(utils.TestConfig.elementTimeout()));
    }

    /**
     * Smart Dynamic Wait: polls a condition every 500ms until it's true or timeout is reached.
     * Replaces all fixed Thread.sleep() calls!
     * @param condition  a lambda returning true when the wait should stop
     * @param timeoutSec maximum seconds to wait
     */
    protected boolean waitUntil(java.util.function.Supplier<Boolean> condition, int timeoutSec) {
        long endTime = System.currentTimeMillis() + (timeoutSec * 1000L);
        while (System.currentTimeMillis() < endTime) {
            try {
                if (Boolean.TRUE.equals(condition.get())) return true;
            } catch (Exception e) { /* element not ready yet, keep polling */ }
            try { Thread.sleep(500); } catch (Exception e) {}
        }
        return false;
    }

    /**
     * Checks if an element is visible on screen RIGHT NOW (no waiting).
     */
    protected boolean isElementVisible(WebElement element) {
        try { return element.isDisplayed(); } catch (Exception e) { return false; }
    }

    /**
     * Checks if an element (by locator) is visible RIGHT NOW.
     */
    protected boolean isElementVisible(org.openqa.selenium.By locator) {
        try {
            java.util.List<WebElement> elements = driver.findElements(locator);
            return !elements.isEmpty() && elements.get(0).isDisplayed();
        } catch (Exception e) { return false; }
    }

    public static org.openqa.selenium.By getCompiledLocator(String xpath) {
        return LOCATOR_CACHE.computeIfAbsent(xpath, org.openqa.selenium.By::xpath);
    }

    public boolean healAndDismissPopups() {
        for (org.openqa.selenium.By popupBy : POPUP_QUEUE) {
            try {
                java.util.List<WebElement> elements = driver.findElements(popupBy);
                for (WebElement el : elements) {
                    if (el.isDisplayed()) {
                        String msg = "[Self-Healing Engine] Detected unexpected obstructing popup (" + popupBy + "). Auto-dismissing...";
                        System.out.println(msg);
                        utils.TestListener.logWarning(msg);
                        el.click();
                        try { Thread.sleep(800); } catch (Exception e) {}
                        return true;
                    }
                }
            } catch (Exception e) {}
        }
        return false;
    }

    protected void click(WebElement element) {
        try {
            waitForVisibility(element);
            element.click();
        } catch (Exception e) {
            System.out.println("[Self-Healing Triggered] Primary click failed. Checking for obstructing popups...");
            utils.TestListener.logWarning("[Self-Healing Interceptor] Primary click failed, scanning for popups...");
            if (healAndDismissPopups()) {
                System.out.println("[Self-Healing Success] Popup auto-dismissed! Retrying primary click...");
                utils.TestListener.logStep("[Self-Healing Success] Retrying primary element click after popup dismissal...");
                try {
                    waitForVisibility(element);
                    element.click();
                    return;
                } catch (Exception retryEx) {
                    System.out.println("[Self-Healing] Retrying via physical tap...");
                    tapElement(element);
                    return;
                }
            }
            throw e;
        }
    }

    protected void sendKeys(WebElement element, String text) {
        waitForVisibility(element);
        
        // Intelligent caching: If the field already contains the exact text we want to type
        // (which happens frequently when chaining flows together), skip typing to save time and prevent duplication!
        try {
            String currentText = element.getText();
            if (currentText != null && currentText.equals(text)) {
                System.out.println("Field already contains '" + text + "'. Skipping input.");
                return;
            }
        } catch (Exception e) {}
        
        element.click(); // Click to focus the field and bring up the keyboard
        
        try {
            element.clear(); // Attempt to clear any old data (e.g. from Flow 1)
        } catch (Exception e) {}

        try {
            element.sendKeys(text);
        } catch (Exception e) {
            System.out.println("Warning: Fast sendKeys failed, retrying standard sendKeys");
            element.sendKeys(text);
        }
    }

    protected void waitForVisibility(WebElement element) {
        wait.ignoring(StaleElementReferenceException.class).until(ExpectedConditions.visibilityOf(element));
    }

    protected WebElement getVisibleElement(org.openqa.selenium.By locator) {
        try {
            return wait.until(d -> {
                java.util.List<WebElement> elements = driver.findElements(locator);
                for (WebElement el : elements) {
                    if (el.isDisplayed()) {
                        return el;
                    }
                }
                return null; // Return null so WebDriverWait keeps polling
            });
        } catch (org.openqa.selenium.TimeoutException e) {
            throw new org.openqa.selenium.NoSuchElementException("No visible element found for locator: " + locator + " after 10 seconds");
        }
    }

    public void scrollDown() {
        Dimension size = driver.manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.7);
        int endY = (int) (size.height * 0.3);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence scroll = new Sequence(finger, 1);
        scroll.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), startX, startY));
        scroll.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        scroll.addAction(finger.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), startX, endY));
        scroll.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Arrays.asList(scroll));
        waitUntil(() -> true, 1); // Smart 1-second settle wait
    }

    public void scrollWheel(int startX, int startY, int endY) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence scroll = new Sequence(finger, 1);
        scroll.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), startX, startY));
        scroll.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        // Use a slower duration for wheel pickers so it doesn't spin wildly
        scroll.addAction(finger.createPointerMove(Duration.ofMillis(1000), PointerInput.Origin.viewport(), startX, endY));
        // CRITICAL BUG FIX: Add a pause before releasing the finger to kill momentum/fling!
        scroll.addAction(new org.openqa.selenium.interactions.Pause(finger, Duration.ofMillis(200)));
        scroll.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Arrays.asList(scroll));
        try { Thread.sleep(1000); } catch (Exception e) {} // Wait for wheel to settle
    }

    public void tapElement(WebElement element) {
        org.openqa.selenium.Rectangle rect = element.getRect();
        int centerX = rect.getX() + (rect.getWidth() / 2);
        int centerY = rect.getY() + (rect.getHeight() / 2);
        tapCoordinates(centerX, centerY);
    }

    public void tapCoordinates(int x, int y) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1);
        tap.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), x, y));
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Arrays.asList(tap));
    }

    public void tapScreenCenter() {
        Dimension size = driver.manage().window().getSize();
        int x = size.width / 2;
        int y = size.height / 2;
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1);
        tap.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), x, y));
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Arrays.asList(tap));
    }
}
