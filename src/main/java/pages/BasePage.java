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
        getCompiledLocator("//*[@text='Got it' or @content-desc='Got it' or @resource-id='btn_ok']"),
        getCompiledLocator("//*[contains(@content-desc, \"Got it, let's continue\") or contains(@text, \"Got it, let's continue\")]"),
        getCompiledLocator("//*[@resource-id='btn_try_again_button' or @content-desc='Try Again' or @text='Try Again']")
    ));



    public BasePage() {

        this.driver = DriverManager.getDriver();

        PageFactory.initElements(driver, this);

        this.wait = new WebDriverWait(driver, Duration.ofSeconds(utils.TestConfig.elementTimeout()), Duration.ofMillis(200));

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

            } catch (Exception e) {
                if (DriverManager.isInstrumentationDeadError(e)) {
                    System.out.println("[BasePage] UiAutomator2 instrumentation dead inside waitUntil; aborting wait and forcing session recreation...");
                    DriverManager.forceQuitDeadDriver();
                    if (e instanceof RuntimeException) {
                        throw (RuntimeException) e;
                    }
                    throw new RuntimeException(e);
                }
                /* element not ready yet, keep polling */
            }

            try { Thread.sleep(200); } catch (Exception e) {}

        }

        return false;

    }



    /**

     * Checks if an element is visible on screen RIGHT NOW (no waiting).

     */

    protected boolean isElementVisible(WebElement element) {

        try {
            return element.isDisplayed();
        } catch (Exception e) {
            if (DriverManager.isInstrumentationDeadError(e)) {
                DriverManager.forceQuitDeadDriver();
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new RuntimeException(e);
            }
            return false;
        }

    }



    /**

     * Checks if an element (by locator) is visible RIGHT NOW.

     */

    protected boolean isElementVisible(org.openqa.selenium.By locator) {

        try {

            java.util.List<WebElement> elements = driver.findElements(locator);

            return !elements.isEmpty() && elements.get(0).isDisplayed();

        } catch (Exception e) {
            if (DriverManager.isInstrumentationDeadError(e)) {
                DriverManager.forceQuitDeadDriver();
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new RuntimeException(e);
            }
            return false;
        }

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

            } catch (Exception e) {
                if (DriverManager.isInstrumentationDeadError(e)) {
                    DriverManager.forceQuitDeadDriver();
                    if (e instanceof RuntimeException) {
                        throw (RuntimeException) e;
                    }
                    throw new RuntimeException(e);
                }
            }

        }

        return false;

    }



    protected void click(WebElement element) {

        try {

            waitForVisibility(element);

            element.click();

        } catch (Exception e) {
            if (DriverManager.isInstrumentationDeadError(e)) {
                System.out.println("[BasePage] UiAutomator2 instrumentation dead during click; forcing session recreation...");
                DriverManager.forceQuitDeadDriver();
                throw e;
            }

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
            boolean isPassword = Boolean.parseBoolean(element.getAttribute("password"));
            String afterText = element.getText();
            boolean hasMaskChars = afterText != null && (afterText.contains("\u2022") || afterText.contains("*") || afterText.contains("\u25CF"));
            boolean sameLength = afterText != null && text != null && afterText.trim().length() == text.length();
            if (isPassword || hasMaskChars || sameLength) {
                return;
            }
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
                boolean keyboardShown = false;
                try {
                    keyboardShown = androidDriver.isKeyboardShown();
                } catch (Exception ignore) {}
                if (!keyboardShown) {
                    return;
                }
                // On Android 14 + Flutter, Appium's default hideKeyboard() falls back to
                // `input keyevent 111` (ESC) and `input keyevent 4` (BACK), which can pop
                // Flutter routes back to Home if the IME is already dismissing or unfocused.
                // Use IME editor action "done" first instead of hardware BACK.
                try {
                    java.util.Map<String, Object> args = new java.util.HashMap<>();
                    args.put("action", "done");
                    androidDriver.executeScript("mobile: performEditorAction", args);
                } catch (Exception ignore) {}
                try { Thread.sleep(300); } catch (Exception ignore) {}
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
            if (DriverManager.isInstrumentationDeadError(e)) {
                System.out.println("[BasePage] UiAutomator2 instrumentation dead inside getVisibleElement; forcing session recreation...");
                DriverManager.forceQuitDeadDriver();
                throw e;
            }

            throw new org.openqa.selenium.NoSuchElementException("No visible element found for locator: " + locator + " after " + utils.TestConfig.elementTimeout() + " seconds");

        } catch (org.openqa.selenium.WebDriverException e) {
            if (DriverManager.isInstrumentationDeadError(e)) {
                System.out.println("[BasePage] UiAutomator2 instrumentation dead inside getVisibleElement; forcing session recreation...");
                DriverManager.forceQuitDeadDriver();
            }
            throw e;
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

    private void performVerticalScroll(boolean down) {
        org.openqa.selenium.Dimension size = getSafeWindowSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * (down ? 0.7 : 0.3));
        int endY = (int) (size.height * (down ? 0.3 : 0.7));

        // If a ScrollView is active (including IME-compressed ScrollViews), swipe strictly inside its bounds
        try {
            java.util.List<WebElement> scrollViews = driver.findElements(org.openqa.selenium.By.xpath("//android.widget.ScrollView"));
            for (int i = scrollViews.size() - 1; i >= 0; i--) {
                WebElement sv = scrollViews.get(i);
                if (sv.isDisplayed()) {
                    org.openqa.selenium.Rectangle r = sv.getRect();
                    if (r.getHeight() > 200 && r.getWidth() > 200) {
                        startX = Math.max(1, Math.min(size.width - 1, r.getX() + (r.getWidth() / 2)));
                        int topY = Math.max(1, r.getY() + (int) (r.getHeight() * 0.20));
                        int bottomY = Math.min(size.height - 1, r.getY() + (int) (r.getHeight() * 0.80));
                        if (bottomY > topY + 60) {
                            startY = down ? bottomY : topY;
                            endY = down ? topY : bottomY;
                            break;
                        }
                    }
                }
            }
        } catch (Exception ignore) {}

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence scroll = new Sequence(finger, 1);
        scroll.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), startX, startY));
        scroll.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        scroll.addAction(finger.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), startX, endY));
        scroll.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Arrays.asList(scroll));
        try { Thread.sleep(350); } catch (Exception ignore) {}
    }

    public void scrollDown() {
        performVerticalScroll(true);
    }

    public void scrollUp() {
        performVerticalScroll(false);
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
        java.util.List<WebElement> elements;
        try {
            elements = driver.findElements(locator);
        } catch (org.openqa.selenium.WebDriverException e) {
            if (DriverManager.isInstrumentationDeadError(e)) {
                System.out.println("[BasePage] UiAutomator2 instrumentation dead inside findWithUnimportantViews; forcing session recreation...");
                DriverManager.forceQuitDeadDriver();
            }
            throw e;
        }
        if (!elements.isEmpty()) {
            return elements;
        }
        if (driver instanceof io.appium.java_client.android.AndroidDriver) {
            io.appium.java_client.android.AndroidDriver androidDriver = (io.appium.java_client.android.AndroidDriver) driver;
            try {
                androidDriver.setSetting("ignoreUnimportantViews", false);
                return androidDriver.findElements(locator);
            } catch (Exception e) {
                if (DriverManager.isInstrumentationDeadError(e)) {
                    System.out.println("[BasePage] UiAutomator2 instrumentation dead inside findWithUnimportantViews fallback; forcing session recreation...");
                    DriverManager.forceQuitDeadDriver();
                    if (e instanceof RuntimeException) {
                        throw (RuntimeException) e;
                    }
                    throw new RuntimeException(e);
                }
            } finally {
                if (DriverManager.getDriver() != null) {
                    try {
                        androidDriver.setSetting("ignoreUnimportantViews", false);
                    } catch (Exception ignored) {}
                }
            }
        }
        return elements;
    }

    protected WebElement findDisplayedElement(org.openqa.selenium.By locator) {
        java.util.List<WebElement> elements = findWithUnimportantViews(locator);
        for (WebElement el : elements) {
            try {
                if (el.isDisplayed()) {
                    return el;
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    public boolean clickPrivacySwitch() {
        return clickPrivacySwitch(org.openqa.selenium.By.xpath("//*[@resource-id='tgl_checkbox' or contains(@resource-id, 'checkbox') or @class='android.widget.Switch']"));
    }

    protected boolean clickPrivacySwitch(org.openqa.selenium.By locator) {
        hideKeyboard();
        WebElement sw = findDisplayedElement(locator);
        if (sw == null) {
            System.out.println("Privacy switch not visible in DOM; scrolling down once to locate dynamically...");
            scrollDown();
            try { Thread.sleep(400); } catch (Exception ignored) {}
            sw = findDisplayedElement(locator);
        }
        if (sw == null) {
            System.out.println("Privacy switch not found in DOM after scroll.");
            return false;
        }

        String checkedBefore = null;
        try { checkedBefore = sw.getAttribute("checked"); } catch (Exception ignored) {}
        if ("true".equalsIgnoreCase(checkedBefore)) {
            System.out.println("Privacy checkbox/switch is already checked.");
            return true;
        }

        boolean isClickable = false;
        try { isClickable = "true".equalsIgnoreCase(sw.getAttribute("clickable")); } catch (Exception ignored) {}

        // Perform one primary interaction only (never stack click + tapCoordinates + adbTap on opaque Flutter switches)
        if (isClickable) {
            try {
                sw.click();
            } catch (Exception e) {
                tapElement(sw);
            }
        } else {
            tapElement(sw);
        }
        try { Thread.sleep(600); } catch (Exception ignored) {}

        WebElement afterSw = findDisplayedElement(locator);
        if (afterSw != null) {
            try {
                if ("true".equalsIgnoreCase(afterSw.getAttribute("checked"))) {
                    System.out.println("Privacy switch toggled to checked.");
                    return true;
                }
            } catch (Exception ignored) {}
        }
        System.out.println("Privacy switch tapped once; accessibility checked attribute remains false.");
        return false;
    }

    public void tapElement(WebElement element) {
        org.openqa.selenium.Rectangle rect = element.getRect();
        int centerX = rect.getX() + (rect.getWidth() / 2);
        int centerY = rect.getY() + (rect.getHeight() / 2);
        tapCoordinates(centerX, centerY);
    }

    public static final String ADB_PATH = resolveAdbPath();

    private static String resolveAdbPath() {
        boolean isWindows = System.getProperty("os.name", "").toLowerCase().contains("win");
        String adbName = isWindows ? "adb.exe" : "adb";

        for (String envVar : new String[]{"ANDROID_HOME", "ANDROID_SDK_ROOT"}) {
            String sdkRoot = System.getenv(envVar);
            if (sdkRoot != null && !sdkRoot.isBlank()) {
                java.io.File candidate = new java.io.File(sdkRoot, "platform-tools" + java.io.File.separator + adbName);
                if (candidate.exists()) {
                    return candidate.getAbsolutePath();
                }
            }
        }

        if (isWindows) {
            String localAppData = System.getenv("LOCALAPPDATA");
            if (localAppData != null && !localAppData.isBlank()) {
                java.io.File candidate = new java.io.File(localAppData, "Android\\Sdk\\platform-tools\\adb.exe");
                if (candidate.exists()) {
                    return candidate.getAbsolutePath();
                }
            }
        }
        return adbName;
    }

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

    protected String resolveDeviceUdid() {
        try {
            if (driver != null && driver.getCapabilities() != null) {
                Object udidCap = driver.getCapabilities().getCapability("deviceUDID");
                if (udidCap == null || udidCap.toString().trim().isEmpty()) {
                    udidCap = driver.getCapabilities().getCapability("udid");
                }
                if (udidCap == null || udidCap.toString().trim().isEmpty()) {
                    udidCap = driver.getCapabilities().getCapability("appium:udid");
                }
                if (udidCap != null && !udidCap.toString().trim().isEmpty()) {
                    return udidCap.toString().trim();
                }
            }
        } catch (Exception ignore) {}
        String sysUdid = System.getProperty("udid");
        if (sysUdid != null && !sysUdid.trim().isEmpty()) {
            return sysUdid.trim();
        }
        return null;
    }

    protected String[] buildAdbCommand(String... adbArgs) {
        java.util.List<String> command = new java.util.ArrayList<>();
        command.add(ADB_PATH);
        String udid = resolveDeviceUdid();
        if (udid != null && !udid.isBlank()) {
            command.add("-s");
            command.add(udid);
        }
        if (adbArgs != null) {
            for (String arg : adbArgs) {
                if (arg != null) {
                    command.add(arg);
                }
            }
        }
        return command.toArray(new String[0]);
    }

    public void adbTap(int x, int y) {
        try {
            Runtime.getRuntime().exec(buildAdbCommand("shell", "input", "tap", String.valueOf(x), String.valueOf(y))).waitFor();
        } catch (Exception ignore) {}
    }

    public void adbInput(String text) {
        if (text == null || text.isEmpty()) {
            return;
        }
        try {
            Runtime.getRuntime().exec(buildAdbCommand("shell", "input", "text", text)).waitFor();
        } catch (Exception ignore) {}
    }

    protected void scrollDobWheelAndConfirm(WebElement dobInput) {
        hideKeyboard();
        waitForVisibility(dobInput);
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
            System.out.println("Confirm button on DOB dialog not found: " + e.getMessage());
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
        waitUntil(() -> !driver.findElements(quoteLoadedBy).isEmpty() || !driver.findElements(explicitErrorBy).isEmpty(), 12);
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

    public boolean isErrorDialogDisplayed() {
        try {
            return !driver.findElements(org.openqa.selenium.By.xpath(
                "//*[contains(@text, 'Something Went Wrong') or contains(@content-desc, 'Something Went Wrong')"
                + " or contains(@text, 'This field is required') or contains(@content-desc, 'This field is required')"
                + " or contains(@text, 'Error') or contains(@content-desc, 'Error')"
                + " or contains(@text, 'not found') or contains(@content-desc, 'not found')"
                + " or contains(@text, 'Invalid') or contains(@content-desc, 'Invalid')]")).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public void dismissErrorDialogIfPresent() {
        try {
            WebElement closeBtn = driver.findElement(org.openqa.selenium.By.xpath(
                "//*[@content-desc='Close' or @text='Close' or @content-desc='Try Again' or @text='Try Again' or @content-desc='OK' or @text='OK']"));
            tapElement(closeBtn);
        } catch (Exception e) {
            System.out.println("Dismiss error dialog handled: " + e.getMessage());
        }
    }

}

