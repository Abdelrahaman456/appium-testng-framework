package pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;
import java.util.List;

public class ClaimsScreen extends BasePage {

    public ClaimsScreen() {
        super();
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(2)), this);
    }

    // =========================================================================
    // LOCATORS
    // =========================================================================
    private final By supportTabBy = By.xpath("//*[@content-desc='Support' or contains(@content-desc, 'Support')]");
    private final By submitClaimCardBy = By.xpath("//*[contains(@content-desc, 'Submit claim')]");
    private final By trackClaimCardBy = By.xpath("//*[contains(@content-desc, 'Track claim')]");
    private final By againstTreeCustomerBy = By.xpath("//*[@content-desc='Against a Tree customer']");
    private final By againstOwnPolicyBy = By.xpath("//*[@content-desc='Against my own policy']");

    private final By nationalIdBy = By.xpath("//*[@resource-id='textfield_national_id' or @hint='Your Id/Iqama number']");
    private final By dobPickerBy = By.xpath("//*[@hint='Date of birth' or contains(@hint, 'Date of birth') or @resource-id='textfield_national_id']/following-sibling::android.widget.ImageView[1]");
    private final By phoneBy = By.xpath("//*[@resource-id='textfield_claims_user_phone' or @hint='Mobile phone']");
    private final By sequenceBy = By.xpath("//*[@resource-id='textfield_sequence_number' or @hint='Sequence Number']");
    private final By ownCarBy = By.xpath("//*[@content-desc='I own this car']");
    private final By notOwnCarBy = By.xpath("//*[@content-desc=\"I don't own this car\"]");
    private final By privacySwitchBy = By.xpath("//*[@resource-id='tgl_checkbox' or contains(@resource-id, 'checkbox')]");
    private final By nextBtnBy = By.xpath("//android.widget.Button[@content-desc='Next' or @text='Next'] | //*[@resource-id='btn_next_button' or @content-desc='Next' or @text='Next']");

    private final By accidentNumberBy = By.xpath("//*[@resource-id='textfield_claims_accident_number' or contains(@hint, 'Accident Number') or contains(@hint, 'Accident number')]");
    private final By accidentDateTimeBy = By.xpath("//*[@resource-id='textfield_date_and_time_of_accident' or contains(@hint, 'Date & time of accident') or contains(@hint, 'Date and time of accident')]");

    // =========================================================================
    // 1. NAVIGATION ACTIONS
    // =========================================================================
    public ClaimsScreen navigateToClaimsLanding() {
        System.out.println("Navigating to Support -> Claims Landing via locators...");
        try {
            waitUntil(() -> isElementVisible(supportTabBy), 8);
            WebElement supportEl = getVisibleElement(supportTabBy);
            tapElement(supportEl);
            Thread.sleep(1200);

            waitUntil(() -> isElementVisible(submitClaimCardBy), 8);
            WebElement submitEl = getVisibleElement(submitClaimCardBy);
            tapElement(submitEl);
            Thread.sleep(1500);
        } catch (Exception e) {
            System.out.println("Claims landing navigation fallback: " + e.getMessage());
            adbTap(720, 2260);
            try { Thread.sleep(1200); } catch (Exception ignore) {}
            adbTap(500, 840);
        }
        return this;
    }

    public ClaimsScreen navigateToTrackClaim() {
        System.out.println("Navigating to Support -> Track Claim via locators...");
        try {
            waitUntil(() -> isElementVisible(supportTabBy), 8);
            WebElement supportEl = getVisibleElement(supportTabBy);
            tapElement(supportEl);
            Thread.sleep(1200);

            waitUntil(() -> isElementVisible(trackClaimCardBy), 8);
            WebElement trackEl = getVisibleElement(trackClaimCardBy);
            tapElement(trackEl);
            Thread.sleep(1500);
        } catch (Exception e) {
            System.out.println("Track claim navigation fallback: " + e.getMessage());
            adbTap(720, 2260);
            try { Thread.sleep(1200); } catch (Exception ignore) {}
            adbTap(500, 1080);
        }
        return this;
    }

    // =========================================================================
    // 2. CLAIM ROUTING & INCIDENT SITUATION
    // =========================================================================
    public ClaimsScreen selectClaimAgainstTreeCustomer() {
        System.out.println("Selecting 'Against a Tree customer' claim type...");
        try {
            waitUntil(() -> isElementVisible(againstTreeCustomerBy), 8);
            WebElement btn = getVisibleElement(againstTreeCustomerBy);
            tapElement(btn);
            Thread.sleep(2000);
        } catch (Exception e) {
            adbTap(500, 2200);
        }
        return this;
    }

    public ClaimsScreen selectClaimAgainstOwnPolicy() {
        System.out.println("Selecting 'Against my own policy' claim type...");
        try {
            waitUntil(() -> isElementVisible(againstOwnPolicyBy), 8);
            WebElement btn = getVisibleElement(againstOwnPolicyBy);
            tapElement(btn);
            Thread.sleep(2000);
        } catch (Exception e) {
            adbTap(500, 2050);
        }
        return this;
    }

    public ClaimsScreen selectSituation(String situationKeyword) {
        System.out.println("Selecting claim situation matching keyword: " + situationKeyword);
        try {
            By locator = By.xpath("//*[contains(@content-desc, '" + situationKeyword + "')]");
            waitUntil(() -> isElementVisible(locator), 10);
            WebElement el = getVisibleElement(locator);
            tapElement(el);
            Thread.sleep(1500);

            // If an accident date/time field appears on the situation screen (e.g. for property damage)
            List<WebElement> inlineDateFields = driver.findElements(accidentDateTimeBy);
            if (!inlineDateFields.isEmpty() && inlineDateFields.get(0).isDisplayed()) {
                System.out.println("Confirming inline accident date/time on situation selector...");
                tapElement(inlineDateFields.get(0));
                Thread.sleep(1500);

                // Confirm Date picker, then sequential Time picker
                By confirmBy = By.xpath("//*[@content-desc='Confirm' or @text='Confirm']");
                for (int d = 0; d < 3; d++) {
                    List<WebElement> confirms = driver.findElements(confirmBy);
                    if (!confirms.isEmpty() && confirms.get(0).isDisplayed()) {
                        tapElement(confirms.get(0));
                        Thread.sleep(1200);
                    }
                }
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            System.out.println("Situation element tap error, attempting fallback: " + e.getMessage());
            adbTap(300, 1210);
        }
        clickNext();
        return this;
    }

    // =========================================================================
    // 3. CLAIMANT DETAILS (STEP 2)
    // =========================================================================
    public ClaimsScreen fillClaimantDetails(String nationalId, String phone, String sequenceNumber, boolean ownCar) {
        System.out.println("Entering Claimant details - National ID: " + nationalId + ", Phone: " + phone + ", Sequence: " + sequenceNumber);
        try {
            // National ID
            enterRobustText(nationalIdBy, nationalId);

            // Date of birth
            System.out.println("Selecting Date of Birth via calendar trigger...");
            List<WebElement> dobTriggers = driver.findElements(dobPickerBy);
            if (!dobTriggers.isEmpty()) {
                tapElement(dobTriggers.get(0));
            } else {
                adbTap(910, 791);
            }
            Thread.sleep(1200);

            // Smooth swipe year column downwards to reach eligible adult birth year (~1986-1998)
            adbSwipe(670, 1000, 670, 1600, 350);
            Thread.sleep(600);

            // Confirm DOB picker
            By confirmBy = By.xpath("//*[@content-desc='Confirm' or @text='Confirm']");
            List<WebElement> confirmBtns = driver.findElements(confirmBy);
            if (!confirmBtns.isEmpty()) {
                tapElement(confirmBtns.get(0));
            } else {
                adbTap(695, 1435);
            }
            Thread.sleep(1000);

            // Mobile Phone
            enterRobustText(phoneBy, phone);

            // Sequence Number (if present)
            if (sequenceNumber != null && !sequenceNumber.isEmpty()) {
                enterRobustText(sequenceBy, sequenceNumber);
            }

            // Car Ownership
            By ownBy = ownCar ? ownCarBy : notOwnCarBy;
            List<WebElement> ownBtns = driver.findElements(ownBy);
            if (!ownBtns.isEmpty()) {
                tapElement(ownBtns.get(0));
            } else {
                adbTap(180, 1260);
            }
            Thread.sleep(500);

            // Hide keyboard before checkbox
            hideKeyboard();
            Thread.sleep(600);

            // Privacy Notice Checkbox
            clickPrivacySwitch(privacySwitchBy);
            Thread.sleep(600);

        } catch (Exception e) {
            System.out.println("Claimant details input error: " + e.getMessage());
        }

        clickNext();

        // Dismiss backend ELM mismatch modal if returned in mock environment
        try {
            By modalClose = By.xpath("//android.widget.Button[@content-desc='Close' or @text='Close'] | //*[@resource-id='btn_close']");
            if (isElementVisible(modalClose)) {
                System.out.println("Notice: UAT ELM validation modal displayed; dismissing modal.");
                tapElement(getVisibleElement(modalClose));
                Thread.sleep(1000);
            }
        } catch (Exception ignore) {}

        return this;
    }

    // =========================================================================
    // 4. ACCIDENT & NAJM DETAILS (STEP 3)
    // =========================================================================
    public ClaimsScreen fillAccidentDetails(String accidentNumber) {
        System.out.println("Entering Accident Details - Accident Number: " + accidentNumber);
        try {
            waitUntil(() -> isElementVisible(accidentNumberBy), 12);
            enterRobustText(accidentNumberBy, accidentNumber);

            // Date & Time picker
            System.out.println("Setting accident date & time to current default...");
            List<WebElement> dtEls = driver.findElements(accidentDateTimeBy);
            if (!dtEls.isEmpty()) {
                tapElement(dtEls.get(0));
                Thread.sleep(1200);
                By confirmBy = By.xpath("//*[@content-desc='Confirm' or @text='Confirm']");
                for (int d = 0; d < 3; d++) {
                    List<WebElement> confirmBtns = driver.findElements(confirmBy);
                    if (!confirmBtns.isEmpty() && confirmBtns.get(0).isDisplayed()) {
                        tapElement(confirmBtns.get(0));
                        Thread.sleep(1200);
                    }
                }
                Thread.sleep(800);
            }

            hideKeyboard();
            Thread.sleep(500);
        } catch (Exception e) {
            System.out.println("Accident details input error: " + e.getMessage());
        }

        clickNext();
        return this;
    }

    // =========================================================================
    // 5. NEXT BUTTON & VALIDATIONS
    // =========================================================================
    public ClaimsScreen clickNext() {
        System.out.println("Clicking Next button on Claims flow...");
        try {
            hideKeyboard();
            Thread.sleep(500);
            for (int i = 0; i < 2; i++) {
                List<WebElement> btns = driver.findElements(nextBtnBy);
                if (!btns.isEmpty() && btns.get(0).isDisplayed()) {
                    tapElement(btns.get(0));
                    Thread.sleep(1500);
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            adbTap(540, 2261);
        }
        return this;
    }

    public boolean isClaimsLandingDisplayed() {
        return waitUntil(() -> isElementVisible(By.xpath("//*[contains(@content-desc, 'Submit claim') or contains(@content-desc, 'Tell us what happened')]")), 8);
    }

    public boolean isSituationSelectorDisplayed() {
        return waitUntil(() -> isElementVisible(By.xpath("//*[contains(@content-desc, 'What best describes the situation')]")), 8);
    }

    public boolean isClaimantFormDisplayed() {
        return waitUntil(() -> isElementVisible(nationalIdBy) || isElementVisible(By.xpath("//*[contains(@content-desc, 'Now, tell us about you')]")), 10);
    }

    public boolean isAccidentDetailsDisplayed() {
        return waitUntil(() -> isElementVisible(accidentNumberBy) || isElementVisible(By.xpath("//*[contains(@content-desc, 'accident') and contains(@content-desc, 'Najm')]")), 10);
    }

    public boolean isDocumentUploadDisplayed() {
        return waitUntil(() -> isElementVisible(By.xpath("//*[contains(@content-desc, 'Upload Documents') or contains(@content-desc, 'Taqdeer Report') or contains(@content-desc, 'Upload')]")), 10);
    }

    public boolean isLoginScreenDisplayed() {
        return waitUntil(() -> isElementVisible(By.xpath("//*[contains(@content-desc, 'Welcome to') or contains(@content-desc, 'Login') or @resource-id='btn_guest_login_or_register_part1']")), 10);
    }

    public boolean isTrackClaimDisplayed() {
        // Track claim requires authentication for guest users
        return waitUntil(() -> isElementVisible(By.xpath("//*[contains(@content-desc, 'Welcome to') or contains(@content-desc, 'Login') or contains(@content-desc, 'Track')]")), 10);
    }

    public void adbSwipe(int x1, int y1, int x2, int y2, int durationMs) {
        try {
            Runtime.getRuntime().exec(new String[]{ADB_PATH, "-s", "emulator-5554", "shell", "input", "swipe",
                    String.valueOf(x1), String.valueOf(y1), String.valueOf(x2), String.valueOf(y2), String.valueOf(durationMs)}).waitFor();
        } catch (Exception ignore) {}
    }

    /**
     * Robust text entry for Flutter input widgets:
     * 1. Finds visible element via locator.
     * 2. Focuses via standard tap.
     * 3. Sends keys via Appium.
     * 4. Triggers Android IME 'Done' action to update Flutter reactive state.
     * 5. Self-heals via ADB input if Flutter dropped keystrokes.
     */
    private void enterRobustText(By locator, String text) {
        try {
            waitUntil(() -> isElementVisible(locator), 10);
            WebElement field = getVisibleElement(locator);
            tapElement(field);
            Thread.sleep(300);

            try {
                field.sendKeys(text);
            } catch (Exception e) {
                new org.openqa.selenium.interactions.Actions(driver).sendKeys(text).perform();
            }

            try {
                driver.executeScript("mobile: performEditorAction", com.google.common.collect.ImmutableMap.of("action", "done"));
            } catch (Exception ignore) {}

            Thread.sleep(300);

            // Self-healing check: verify if text landed
            String currentVal = null;
            try { currentVal = field.getText(); } catch (Exception ignore) {}
            if (currentVal == null || currentVal.trim().isEmpty() || !currentVal.contains(text.substring(0, Math.min(2, text.length())))) {
                System.out.println("[Self-Healing] Keystrokes not detected in Flutter widget for " + locator + ". Applying native input injection...");
                tapElement(field);
                Thread.sleep(250);
                Runtime.getRuntime().exec(new String[]{ADB_PATH, "-s", "emulator-5554", "shell", "input", "text", text}).waitFor();
                Thread.sleep(250);
            }
            hideKeyboard();
            Thread.sleep(300);
        } catch (Exception e) {
            System.out.println("enterRobustText failed for " + locator + ": " + e.getMessage());
        }
    }
}
