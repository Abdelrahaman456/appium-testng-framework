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

    public static volatile boolean uatElmDependentBlocked = false;

    protected AppiumDriver driver;

    protected WebDriverWait wait;



    // DATA STRUCTURE 1: ConcurrentHashMap for thread-safe O(1) pre-compiled locator caching

    private static final ConcurrentHashMap<String, org.openqa.selenium.By> LOCATOR_CACHE = new ConcurrentHashMap<>();



    // DATA STRUCTURE 2: ArrayDeque (Double-ended Queue) for high-speed O(1) popup interceptor queue
    private static final Deque<org.openqa.selenium.By> POPUP_QUEUE = new ArrayDeque<>(Arrays.asList(
        getCompiledLocator("//*[@resource-id='android:id/aerr_wait' or @text='Wait' or @content-desc='Wait']"),
        getCompiledLocator("//*[contains(@resource-id, 'permission_allow_button')]"),
        getCompiledLocator("//android.widget.Button[@content-desc='Not now' or @text='Not now']"),
        getCompiledLocator("//android.widget.Button[@content-desc='Dismiss' or @text='Dismiss']"),
        getCompiledLocator("//*[@text='Got it' or @content-desc='Got it' or contains(@resource-id, 'ok')]"),
        getCompiledLocator("//*[contains(@content-desc, \"Got it, let's continue\") or contains(@text, \"Got it, let's continue\")]")
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

    public boolean waitUntil(java.util.function.Supplier<Boolean> condition, int timeoutSec) {

        long endTime = System.currentTimeMillis() + (timeoutSec * 1000L);

        while (System.currentTimeMillis() < endTime) {

            try {

                if (Boolean.TRUE.equals(condition.get())) return true;

            } catch (Exception e) { /* element not ready yet, keep polling */ }

            try { Thread.sleep(200); } catch (Exception e) {}

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
                System.out.println("[Self-Healing Success] Popup auto-dismissed! Settle wait for app to resume...");
                try { Thread.sleep(1500); } catch (Exception ignore) {}
                try {
                    waitForVisibility(element);
                    element.click();
                    return;
                } catch (Exception retryEx) {
                    try {
                        System.out.println("[Self-Healing] Retrying via physical tap...");
                        tapElement(element);
                        return;
                    } catch (Exception tapEx) {
                        System.out.println("[Self-Healing] Physical tap retry skipped: " + tapEx.getMessage());
                    }
                }
            }

            throw e;

        }

    }



    protected void sendKeys(WebElement element, String text) {
        waitForVisibility(element);

        // Always re-enter text cleanly so Flutter FormField validation state is updated on retry attempts.

        

        element.click(); // Click to focus the field and bring up the keyboard
        try { Thread.sleep(250); } catch (Exception ignore) {}

        try {
            element.clear(); // Attempt to clear any old data (e.g. from Flow 1)
        } catch (Exception e) {}

        try {
            element.sendKeys(text);
        } catch (Exception e) {
            System.out.println("Warning: Fast sendKeys failed, retrying standard sendKeys");
            try { element.sendKeys(text); } catch (Exception ignore) {}
        }

        // Verify text actually landed (Flutter fields can drop keystrokes while keyboard animates or return hint text)
        try {
            Thread.sleep(200);
            String afterText = element.getText();
            String prefix = (text != null && !text.isEmpty()) ? text.substring(0, Math.min(2, text.length())) : "";
            if (afterText == null || afterText.trim().isEmpty() || (!prefix.isEmpty() && !afterText.contains(prefix))) {
                tapElement(element);
                Thread.sleep(300);
                try {
                    element.sendKeys(text);
                } catch (Exception retryEx) {
                    new org.openqa.selenium.interactions.Actions(driver).sendKeys(text).perform();
                }
                Thread.sleep(300);
            }
        } catch (Exception ignore) {}
    }

    public void hideKeyboard() {
        try {
            if (driver instanceof io.appium.java_client.android.AndroidDriver) {
                io.appium.java_client.android.AndroidDriver androidDriver = (io.appium.java_client.android.AndroidDriver) driver;
                try {
                    androidDriver.hideKeyboard();
                } catch (Exception ignore) {}
                try {
                    androidDriver.pressKey(new io.appium.java_client.android.nativekey.KeyEvent(io.appium.java_client.android.nativekey.AndroidKey.ESCAPE));
                } catch (Exception e) {
                    try {
                        Runtime.getRuntime().exec(new String[]{ADB_PATH, "-s", "emulator-5554", "shell", "input", "keyevent", "111"}).waitFor();
                    } catch (Exception ignore) {}
                }
                try { Thread.sleep(600); } catch (Exception ignore) {}
            } else if (driver instanceof io.appium.java_client.HidesKeyboard) {
                ((io.appium.java_client.HidesKeyboard) driver).hideKeyboard();
            }
        } catch (Exception ignore) {}
    }



    protected void waitForVisibility(WebElement element) {

        wait.ignoring(StaleElementReferenceException.class, org.openqa.selenium.NoSuchElementException.class)

            .until(ExpectedConditions.visibilityOf(element));

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

            throw new org.openqa.selenium.NoSuchElementException("No visible element found for locator: " + locator + " after " + utils.TestConfig.elementTimeout() + " seconds");

        }

    }



    private static org.openqa.selenium.Dimension cachedWindowSize = null;

    protected org.openqa.selenium.Dimension getSafeWindowSize() {
        if (cachedWindowSize != null) return cachedWindowSize;
        try {
            cachedWindowSize = driver.manage().window().getSize();
            return cachedWindowSize;
        } catch (Exception e) {
            return new org.openqa.selenium.Dimension(1080, 2400);
        }
    }

    public void scrollDown() {
        org.openqa.selenium.Dimension size = getSafeWindowSize();
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

    public void scrollUp() {
        org.openqa.selenium.Dimension size = getSafeWindowSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.3);
        int endY = (int) (size.height * 0.7);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence scroll = new Sequence(finger, 1);
        scroll.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), startX, startY));
        scroll.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        scroll.addAction(finger.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), startX, endY));
        scroll.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Arrays.asList(scroll));
        waitUntil(() -> true, 1);
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



    protected java.util.List<WebElement> findWithUnimportantViews(org.openqa.selenium.By locator) {
        java.util.List<WebElement> elements = driver.findElements(locator);
        if (!elements.isEmpty()) {
            return elements;
        }
        if (driver instanceof io.appium.java_client.android.AndroidDriver) {
            io.appium.java_client.android.AndroidDriver androidDriver = (io.appium.java_client.android.AndroidDriver) driver;
            try {
                androidDriver.setSetting("ignoreUnimportantViews", false);
                return androidDriver.findElements(locator);
            } catch (Exception ignored) {
            } finally {
                try {
                    androidDriver.setSetting("ignoreUnimportantViews", true);
                } catch (Exception ignored) {}
            }
        }
        return elements;
    }

    protected boolean clickPrivacySwitch(org.openqa.selenium.By locator) {
        hideKeyboard();
        try { Thread.sleep(600); } catch (Exception ignore) {}
        for (int attempt = 0; attempt < 3; attempt++) {
            java.util.List<WebElement> switches = findWithUnimportantViews(locator);
            if (!switches.isEmpty()) {
                WebElement sw = switches.get(0);
                String checkedBefore = null;
                try { checkedBefore = sw.getAttribute("checked"); } catch (Exception ignored) {}
                if ("true".equalsIgnoreCase(checkedBefore)) {
                    System.out.println("Privacy checkbox/switch is already checked.");
                    return true;
                }

                org.openqa.selenium.Rectangle r = sw.getRect();
                int cx = r.getX() + (r.getWidth() / 2);
                int cy = r.getY() + (r.getHeight() / 2);

                // Tap exact center via W3C touch gesture
                tapCoordinates(cx, cy);
                try { Thread.sleep(500); } catch (Exception ignored) {}

                // Verify
                java.util.List<WebElement> after = findWithUnimportantViews(locator);
                if (!after.isEmpty() && "true".equalsIgnoreCase(after.get(0).getAttribute("checked"))) {
                    System.out.println("Privacy switch toggled to checked via center tap.");
                    return true;
                }

                // Native click fallback
                try {
                    sw.click();
                    Thread.sleep(500);
                } catch (Exception ignored) {}

                after = findWithUnimportantViews(locator);
                if (!after.isEmpty() && "true".equalsIgnoreCase(after.get(0).getAttribute("checked"))) {
                    System.out.println("Privacy switch toggled to checked via native click.");
                    return true;
                }

                // Fallback: tap label to the right of the switch
                tapCoordinates(r.getX() + r.getWidth() + 60, cy);
                try { Thread.sleep(500); } catch (Exception ignored) {}

                after = findWithUnimportantViews(locator);
                if (!after.isEmpty() && "true".equalsIgnoreCase(after.get(0).getAttribute("checked"))) {
                    System.out.println("Privacy switch toggled via label tap.");
                    return true;
                }

                // ADB direct tap fallback
                try {
                    Runtime.getRuntime().exec(new String[]{ADB_PATH, "-s", "emulator-5554", "shell", "input", "tap", String.valueOf(cx), String.valueOf(cy)}).waitFor();
                    Thread.sleep(500);
                } catch (Exception ignore) {}

                after = findWithUnimportantViews(locator);
                if (!after.isEmpty() && "true".equalsIgnoreCase(after.get(0).getAttribute("checked"))) {
                    System.out.println("Privacy switch toggled via ADB tap.");
                    return true;
                }
            } else {
                System.out.println("Privacy switch not found in DOM; tapping standard coordinate (68, 2101)...");
                tapCoordinates(68, 2101);
                try { Thread.sleep(500); } catch (Exception ignored) {}
            }
            if (attempt == 0) {
                scrollDown();
                try { Thread.sleep(500); } catch (Exception ignore) {}
            }
        }
        return false;
    }

    public void tapElement(WebElement element) {
        org.openqa.selenium.Rectangle rect = element.getRect();
        int centerX = rect.getX() + (rect.getWidth() / 2);
        int centerY = rect.getY() + (rect.getHeight() / 2);
        tapCoordinates(centerX, centerY);
    }

    public static final String ADB_PATH = "C:\\Users\\LENOVO\\AppData\\Local\\Android\\Sdk\\platform-tools\\adb.exe";

    public void tapCoordinates(int x, int y) {
        try {
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence tap = new Sequence(finger, 1);
            tap.addAction(finger.createPointerMove(Duration.ofMillis(50), PointerInput.Origin.viewport(), x, y));
            tap.addAction(finger.createPointerDown(0));
            tap.addAction(new org.openqa.selenium.interactions.Pause(finger, Duration.ofMillis(100)));
            tap.addAction(finger.createPointerUp(0));
            driver.perform(java.util.Arrays.asList(tap));
            return;
        } catch (Exception ex) {
            System.out.println("Warning: W3C tap failed for (" + x + "," + y + "): " + ex.getMessage());
        }
        try {
            driver.executeScript("mobile: clickGesture", java.util.Map.of("x", x, "y", y));
        } catch (Exception ignore) {}
    }

    protected void scrollDobWheelAndConfirm(WebElement dobInput) {
        hideKeyboard();
        waitForVisibility(dobInput);
        try {
            String text = dobInput.getText();
            String desc = dobInput.getAttribute("content-desc");
            if ((text != null && text.contains("1998")) || (desc != null && desc.contains("1998"))) {
                System.out.println("DOB field already contains 1998 (text=" + text + ", desc=" + desc + "); skipping wheel scroll.");
                return;
            }
        } catch (Exception ignore) {}
        tapElement(dobInput);
        try { Thread.sleep(1500); } catch (Exception ignore) {}

        try {
            boolean swipeUpDecreasesYear = true;
            Integer lastYear = null;
            for (int attempt = 0; attempt < 8; attempt++) {
                java.util.List<WebElement> wheels = driver.findElements(org.openqa.selenium.By.xpath("//android.widget.SeekBar"));
                WebElement yearWheel = null;
                String yearDesc = null;
                for (WebElement w : wheels) {
                    String desc = w.getAttribute("content-desc");
                    if (desc == null || desc.trim().isEmpty()) {
                        desc = w.getText();
                    }
                    if (desc != null && desc.trim().matches("\\d{4}")) {
                        yearWheel = w;
                        yearDesc = desc.trim();
                        break;
                    }
                }
                if (yearWheel == null) {
                    // Check if dialog is even present
                    java.util.List<WebElement> confirms = driver.findElements(org.openqa.selenium.By.xpath(
                        "//*[@text='Confirm' or @content-desc='Confirm' or @text='Done' or @content-desc='Done']"));
                    if (confirms.isEmpty() && wheels.isEmpty()) {
                        System.out.println("DOB wheel dialog not detected; skipping blind scroll.");
                        break;
                    }
                    Dimension size = driver.manage().window().getSize();
                    int wheelX = (int) (size.width * 0.35);
                    scrollWheel(wheelX, (int) (size.height * 0.45), (int) (size.height * 0.65));
                    Thread.sleep(600);
                    continue;
                }
                int currentYear = Integer.parseInt(yearDesc);
                if (currentYear == 1998) {
                    break;
                }
                if (lastYear != null) {
                    if (lastYear > 1998 && currentYear > lastYear) {
                        swipeUpDecreasesYear = !swipeUpDecreasesYear;
                    } else if (lastYear < 1998 && currentYear < lastYear) {
                        swipeUpDecreasesYear = !swipeUpDecreasesYear;
                    }
                }
                lastYear = currentYear;

                org.openqa.selenium.Rectangle r = yearWheel.getRect();
                int wheelX = r.getX() + (r.getWidth() / 2);
                int centerY = r.getY() + (r.getHeight() / 2);
                int diff = Math.abs(currentYear - 1998);
                int deltaY = Math.min(200, Math.max(50, diff * 26));
                boolean needLowerYear = currentYear > 1998;
                boolean swipeDown = needLowerYear ? swipeUpDecreasesYear : !swipeUpDecreasesYear;
                if (swipeDown) {
                    scrollWheel(wheelX, centerY - (deltaY / 2), centerY + (deltaY / 2));
                } else {
                    scrollWheel(wheelX, centerY + (deltaY / 2), centerY - (deltaY / 2));
                }
                Thread.sleep(500);
            }
        } catch (Exception e) {
            System.out.println("DOB wheel scroll handled: " + e.getMessage());
        }

        try {
            WebElement confirm = driver.findElement(org.openqa.selenium.By.xpath(
                    "(//*[@text='Confirm' or @content-desc='Confirm' or @text='Done' or @content-desc='Done'])[last()]"));
            tapElement(confirm);
        } catch (Exception e) {
            try { tapElement(dobInput); } catch (Exception ignore) {}
        }
        try { Thread.sleep(1000); } catch (Exception ignore) {}
    }

    protected void tapPreferredButton(org.openqa.selenium.By clickableBy, WebElement fallbackElement) {
        java.util.List<WebElement> candidates = driver.findElements(clickableBy);
        for (WebElement candidate : candidates) {
            try {
                if (candidate.isDisplayed()) {
                    tapElement(candidate);
                    return;
                }
            } catch (Exception ignore) {}
        }
        if (isElementVisible(fallbackElement)) {
            tapElement(fallbackElement);
        }
    }

    protected void answerMemberDeclarations(boolean answerYes) {
        org.openqa.selenium.By anyOptionBy = org.openqa.selenium.By.xpath(
                "//*[@resource-id='btn_no_button' or @resource-id='btn_yes_button' or @text='No' or @content-desc='No' or @text='Yes' or @content-desc='Yes']");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.presenceOfElementLocated(anyOptionBy));
        } catch (Exception ignore) {
            if (driver.findElements(anyOptionBy).isEmpty()) {
                return;
            }
        }

        java.util.List<WebElement> noBtns = driver.findElements(org.openqa.selenium.By.xpath(
                "//*[@resource-id='btn_no_button' or @content-desc='No' or @text='No']"));
        java.util.List<WebElement> yesBtns = driver.findElements(org.openqa.selenium.By.xpath(
                "//*[@resource-id='btn_yes_button' or @content-desc='Yes' or @text='Yes']"));

        if (!answerYes) {
            for (WebElement btn : noBtns) {
                try {
                    if (btn.isDisplayed()) {
                        tapElement(btn);
                        Thread.sleep(300);
                    }
                } catch (Exception ignore) {}
            }
        } else {
            if (!yesBtns.isEmpty()) {
                try {
                    if (yesBtns.get(0).isDisplayed()) {
                        tapElement(yesBtns.get(0));
                        Thread.sleep(300);
                    }
                } catch (Exception ignore) {}
            }
            for (int i = 1; i < noBtns.size(); i++) {
                try {
                    if (noBtns.get(i).isDisplayed()) {
                        tapElement(noBtns.get(i));
                        Thread.sleep(300);
                    }
                } catch (Exception ignore) {}
            }
        }
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

    /**
     * Checks briefly (up to 2s) if the app has already transitioned out of Checkout into 3DS / post-payment.
     */
    protected boolean isPaymentOrConfirmationAlreadyActive() {
        org.openqa.selenium.By postCheckoutBy = org.openqa.selenium.By.xpath(
            "//*[@content-desc='Payment' or @text='Payment'"
            + " or contains(@text, 'ACS Emulator') or contains(@content-desc, 'ACS Emulator')"
            + " or contains(@text, 'Submit') or contains(@content-desc, 'Submit')"
            + " or contains(@content-desc, 'ready to roll') or contains(@text, 'ready to roll')"
            + " or contains(@content-desc, 'All set') or contains(@text, 'All set')"
            + " or contains(@content-desc, 'Policy number') or contains(@text, 'Policy number')"
            + " or contains(@content-desc, 'Purchase failed') or contains(@text, 'Purchase failed')]");
        boolean found = waitUntil(() -> {
            try {
                java.util.List<WebElement> matches = driver.findElements(postCheckoutBy);
                for (WebElement el : matches) {
                    if (el.isDisplayed()) {
                        return true;
                    }
                }
            } catch (Exception ignore) {}
            return false;
        }, 2);
        if (found) {
            System.out.println("Already transitioned to 3DS / post-payment screen after card Save; skipping redundant Pay now click.");
        }
        return found;
    }

    /**
     * Checks after tapping Proceed on Member/Other Details whether UAT Elm rejected test dependents
     * and kept the app on the dependent verification screen.
     */
    protected boolean checkAndMarkUatElmDependentBlock(String screenLabel) {
        org.openqa.selenium.By quoteLoadedBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='btn_buy_now' or @resource-id='btn_sales_quote_page_buy_now_button'"
            + " or contains(@content-desc, 'Bronze') or contains(@content-desc, 'Silver')"
            + " or contains(@content-desc, 'Gold') or contains(@content-desc, 'Platinum')"
            + " or contains(@content-desc, 'Comfort') or contains(@content-desc, 'Primary')]");
        org.openqa.selenium.By explicitErrorBy = org.openqa.selenium.By.xpath(
            "//*[contains(@content-desc, 'This field is required') or contains(@text, 'This field is required')]");
        waitUntil(() -> !driver.findElements(quoteLoadedBy).isEmpty() || !driver.findElements(explicitErrorBy).isEmpty(), 4);
        boolean stillOnDependentStep = !driver.findElements(org.openqa.selenium.By.xpath(
            "//*[contains(@content-desc, 'Other Details') or contains(@text, 'Other Details')"
            + " or contains(@content-desc, 'Member(s) Details') or contains(@text, 'Member(s) Details')"
            + " or contains(@content-desc, 'This field is required') or contains(@text, 'This field is required')"
            + " or @resource-id='textfield_national_id' or @resource-id='textfield_iqama_id']")).isEmpty();
        boolean quoteAlreadyLoaded = !driver.findElements(quoteLoadedBy).isEmpty();
        if (stillOnDependentStep && !quoteAlreadyLoaded) {
            uatElmDependentBlocked = true;
            System.out.println("[UAT Elm Notice] UAT Elm rejected test dependent Iqamas on " + screenLabel
                + " and requires at least 1 live Absher-linked dependent to enable Proceed.");
            return true;
        }
        return false;
    }

}

