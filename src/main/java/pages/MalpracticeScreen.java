package pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import utils.TestDataBuilder;

/**
 * Page Object for Medical Malpractice Insurance on Tree Digital Insurance (Flutter UAT App).
 * Supports Physician Specialist, Nurse Practitioner, Surgeon, Retroactive / Prior Acts coverage,
 * Deductible & Limit selection, Underwriting referrals, and National Address Checkout.
 * ZERO hardcoded coordinates - 100% dynamic bounding-box tapping and accessibility locators.
 */
public class MalpracticeScreen extends BasePage {

    public MalpracticeScreen() {
        super();
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(2)), this);
    }

    // =========================================================================
    // 1. LANDING SCREEN
    // =========================================================================
    @AndroidFindBy(xpath = "//*[@content-desc='Insure now' or @text='Insure now' or @resource-id='btn_insure_now' or @resource-id='btn_next_button_product_landing_page_']")
    private WebElement insureNowButton;

    public boolean isInsureNowButtonVisible() {
        try {
            waitForVisibility(insureNowButton);
            return insureNowButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public MalpracticeScreen clickInsureNow() {
        System.out.println("Waiting for Malpractice Insurance landing screen to settle...");
        try { Thread.sleep(2000); } catch (Exception ignore) {}
        System.out.println("Clicking Insure now button via dynamic locator...");
        try {
            waitForVisibility(insureNowButton);
            tapElement(insureNowButton);
        } catch (Exception e) {
            List<WebElement> buttons = driver.findElements(By.xpath("//*[@content-desc='Insure now' or @text='Insure now']"));
            if (!buttons.isEmpty()) {
                tapElement(buttons.get(0));
            }
        }
        try { Thread.sleep(2000); } catch (Exception ignore) {}
        return this;
    }

    public MalpracticeScreen clickInsureNowIfPresent() {
        System.out.println("Checking / Clicking Malpractice Landing 'Insure now' button...");
        try { Thread.sleep(1500); } catch (Exception ignore) {}
        org.openqa.selenium.By landingBy = org.openqa.selenium.By.xpath(
            "//*[contains(@content-desc, 'Malpractice') or contains(@text, 'Malpractice')]"
        );
        org.openqa.selenium.By insureBtnBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='btn_next_button_product_landing_page_' or @resource-id='btn_insure_now' or @content-desc='Insure now' or @text='Insure now']"
        );
        org.openqa.selenium.By formTargetBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='textfield_national_id' or contains(@resource-id, 'national_id')]"
        );
        try {
            if (!driver.findElements(landingBy).isEmpty() || !driver.findElements(insureBtnBy).isEmpty()) {
                System.out.println("Detected Malpractice Landing screen. Tapping Insure now...");
                for (int attempt = 1; attempt <= 3; attempt++) {
                    java.util.List<WebElement> btns = findWithUnimportantViews(insureBtnBy);
                    if (btns.isEmpty()) {
                        scrollDown();
                        btns = findWithUnimportantViews(insureBtnBy);
                    }
                    if (!btns.isEmpty()) {
                        WebElement target = btns.get(btns.size() - 1);
                        for (WebElement b : btns) {
                            if ("true".equals(b.getAttribute("clickable"))) {
                                target = b;
                                break;
                            }
                        }
                        tapElement(target);
                    } else if (isElementVisible(insureNowButton)) {
                        tapElement(insureNowButton);
                    }
                    try { Thread.sleep(1500); } catch (Exception ignore) {}
                    if (!driver.findElements(formTargetBy).isEmpty()) {
                        System.out.println("Successfully transitioned to Practitioner Details form!");
                        return this;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("clickInsureNowIfPresent note: " + e.getMessage());
        }
        return this;
    }

    // =========================================================================
    // 2. PRACTITIONER DETAILS
    // =========================================================================
    @AndroidFindBy(xpath = "//*[@resource-id='textfield_national_id' or contains(@resource-id, 'national_id')]")
    private WebElement nationalIdInput;

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_phone_number' or contains(@resource-id, 'phone')]")
    private WebElement phoneNumberInput;

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_email' or contains(@resource-id, 'email')]")
    private WebElement emailInput;

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_month_year_of_birth_txt' or contains(@resource-id, 'dob') or contains(@resource-id, 'birth') or @hint='Month/Year of birth' or contains(@hint, 'birth')]")
    private WebElement dobInput;

    @AndroidFindBy(xpath = "//*[contains(@text, 'Profession') or contains(@content-desc, 'Profession') or contains(@hint, 'Profession') or contains(@hint, 'profession') or @resource-id='dropdown_profession']")
    private WebElement professionInput;

    @AndroidFindBy(xpath = "//*[@resource-id='tgl_checkbox' or contains(@resource-id, 'checkbox') or @class='android.widget.Switch']")
    private WebElement privacyNoticeSwitch;

    @AndroidFindBy(xpath = "//*[@resource-id='btn_next_button' or @content-desc='Next' or @text='Next']")
    private WebElement nextButton;

    private String savedNationalId = "1313424273";
    private String savedPhone = "500421222";
    private String savedEmail = "qa.malp@tree.com.sa";

    public MalpracticeScreen fillPractitionerDetails(String nationalId, String phone, String email) {
        this.savedNationalId = nationalId;
        this.savedPhone = phone;
        this.savedEmail = email;
        System.out.println("Waiting for Practitioner details form...");
        try {
            List<WebElement> idCheck = driver.findElements(By.xpath("//*[@resource-id='textfield_national_id' or contains(@resource-id, 'national_id')]"));
            if (idCheck.isEmpty()) {
                scrollUp();
                Thread.sleep(600);
            }
        } catch (Exception ignore) {}

        WebDriverWait waitLocal = new WebDriverWait(driver, Duration.ofSeconds(25));
        waitLocal.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//*[@resource-id='textfield_national_id' or contains(@resource-id, 'national_id')]")));

        System.out.println("Entering Practitioner National ID: " + nationalId);
        try {
            WebElement idField = driver.findElement(By.xpath("//*[@resource-id='textfield_national_id' or contains(@resource-id, 'national_id')]"));
            sendKeys(idField, nationalId);
        } catch (Exception e) {
            System.out.println("National ID sendKeys note: " + e.getMessage());
        }

        System.out.println("Entering Phone Number: " + phone);
        try {
            WebElement phoneField = driver.findElement(By.xpath("//*[@resource-id='textfield_phone_number' or contains(@resource-id, 'phone')]"));
            sendKeys(phoneField, phone);
        } catch (Exception e) {
            System.out.println("Phone sendKeys note: " + e.getMessage());
        }

        System.out.println("Entering Email: " + email);
        try {
            WebElement emailField = driver.findElement(By.xpath("//*[@resource-id='textfield_email' or contains(@resource-id, 'email')]"));
            sendKeys(emailField, email);
        } catch (Exception e) {
            System.out.println("Email sendKeys note: " + e.getMessage());
        }
        hideKeyboard();
        return this;
    }

    public MalpracticeScreen enterNationalId(String nationalId) {
        waitForVisibility(nationalIdInput);
        tapElement(nationalIdInput);
        sendKeys(nationalIdInput, nationalId);
        return this;
    }

    public MalpracticeScreen enterPhone(String phone) {
        waitForVisibility(phoneNumberInput);
        tapElement(phoneNumberInput);
        sendKeys(phoneNumberInput, phone);
        return this;
    }

    public MalpracticeScreen enterEmail(String email) {
        waitForVisibility(emailInput);
        tapElement(emailInput);
        sendKeys(emailInput, email);
        return this;
    }

    public MalpracticeScreen selectProfession(String profession) {
        System.out.println("Selecting Profession: " + profession);
        hideKeyboard();
        By professionBy = By.xpath("//android.widget.EditText[contains(@hint, 'Profession') or contains(@hint, 'profession')]");
        By optionBy = By.xpath("//android.view.View[contains(@content-desc, '" + profession + "') or contains(@text, '" + profession + "')]");
        String prefix = profession.contains(" ") ? profession.split(" ")[0] : profession;

        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                WebElement input = findDisplayedElement(professionBy);
                if (input == null) {
                    waitForVisibility(professionInput);
                    input = professionInput;
                }
                tapElement(input);
                try { Thread.sleep(500); } catch (Exception ignore) {}

                input = findDisplayedElement(professionBy);
                if (input == null) {
                    input = professionInput;
                }
                try { input.clear(); } catch (Exception ignored) {}
                String query = (attempt == 2) ? profession : prefix;
                input.sendKeys(query);

                boolean foundInDom = waitUntil(() -> !findWithUnimportantViews(optionBy).isEmpty(), 6);
                if (!foundInDom) {
                    hideKeyboard();
                    foundInDom = waitUntil(() -> !findWithUnimportantViews(optionBy).isEmpty(), 4);
                }

                boolean clicked = false;
                if (foundInDom) {
                    List<WebElement> options = findWithUnimportantViews(optionBy);
                    for (WebElement opt : options) {
                        try {
                            if (opt.isDisplayed()) {
                                System.out.println("Found profession suggestion in DOM! Tapping: "
                                        + opt.getText() + " / " + opt.getAttribute("content-desc"));
                                tapElement(opt);
                                clicked = true;
                                break;
                            }
                        } catch (Exception ignore) {}
                    }
                }

                if (!clicked) {
                    WebElement refreshed = findDisplayedElement(professionBy);
                    org.openqa.selenium.Rectangle r = (refreshed != null ? refreshed : input).getRect();
                    int suggX = r.getX() + (int) (r.getWidth() * 0.25);
                    int suggY = r.getY() + r.getHeight() + Math.max(85, (int) (r.getHeight() * 0.75));
                    System.out.println("Tapping suggestion overlay dynamically at (" + suggX + ", " + suggY + ")...");
                    tapCoordinates(suggX, suggY);
                }

                boolean updated = waitUntil(() -> {
                    WebElement checkEl = findDisplayedElement(professionBy);
                    if (checkEl == null) {
                        return false;
                    }
                    String txt = "";
                    try { txt = checkEl.getText(); } catch (Exception ignore) {}
                    String low = (txt != null ? txt.trim().toLowerCase() : "");
                    return !low.isEmpty() && !low.equals(prefix.toLowerCase())
                            && (low.contains(profession.toLowerCase()) || low.length() > prefix.length() + 3)
                            && findWithUnimportantViews(optionBy).isEmpty();
                }, 3);

                hideKeyboard();
                if (updated) {
                    WebElement verified = findDisplayedElement(professionBy);
                    System.out.println("Verified Profession selected (attempt " + (attempt + 1) + "): '"
                            + (verified != null ? verified.getText() : profession) + "'");
                    try { Thread.sleep(400); } catch (Exception ignore) {}
                    return this;
                }

                System.out.println("Profession selection attempt " + (attempt + 1)
                        + " did not lock full value / close overlay; retrying...");
                try { Thread.sleep(400); } catch (Exception ignore) {}
            } catch (Exception e) {
                System.out.println("Profession selection attempt " + (attempt + 1) + " error: " + e.getMessage());
            }
        }
        hideKeyboard();
        try { Thread.sleep(500); } catch (Exception ignore) {}
        return this;
    }

    /**
     * Dynamically handles Date of Birth selection wheel using viewport percentage calculations.
     * Zero hardcoded pixel coordinates.
     */
    public MalpracticeScreen selectDob() {
        System.out.println("Selecting Month/Year of birth (Target Year: 1998)...");
        By dobBy = By.xpath("//*[@resource-id='textfield_month_year_of_birth_txt' or contains(@resource-id, 'dob') or contains(@resource-id, 'birth') or @hint='Month/Year of birth' or contains(@hint, 'birth') or contains(@content-desc, 'birth')]");
        List<WebElement> dobs = driver.findElements(dobBy);
        if (dobs.isEmpty()) {
            scrollUp();
            try { Thread.sleep(600); } catch (Exception ignore) {}
            dobs = driver.findElements(dobBy);
        }
        if (!dobs.isEmpty()) {
            scrollDobWheelAndConfirm(dobs.get(0));
        } else {
            scrollDobWheelAndConfirm(dobInput);
        }
        return this;
    }

    public MalpracticeScreen selectPriorInsurance(boolean hasPrior) {
        System.out.println("Selecting Prior Medical Malpractice Insurance: " + (hasPrior ? "Yes" : "No") + " via dynamic locator...");
        hideKeyboard();
        By targetLocator = hasPrior
            ? By.xpath("//*[@resource-id='btn_yes_button' or @content-desc='Yes' or @text='Yes']")
            : By.xpath("//*[@resource-id='btn_no_button' or @content-desc='No' or @text='No']");
        By priorConditionalBy = By.xpath(
            "//*[contains(@content-desc, 'prior insurance provider') or contains(@text, 'prior insurance provider') "
            + "or @resource-id='dd_select_your_prior_insurance_provider' "
            + "or @resource-id='textfield_retroactive_date' "
            + "or @resource-id='btn_claims_yes' "
            + "or contains(@resource-id, 'retroactive')]");
        By providerDdBy = By.xpath(
            "//*[@resource-id='dd_select_your_prior_insurance_provider' "
            + "or contains(@hint, 'prior insurance provider') or contains(@content-desc, 'prior insurance provider') or contains(@text, 'prior insurance provider')]");

        for (int attempt = 0; attempt < 3; attempt++) {
            hideKeyboard();
            List<WebElement> btns = findWithUnimportantViews(targetLocator);
            if (btns.isEmpty()) {
                scrollDown();
                btns = findWithUnimportantViews(targetLocator);
            }
            if (!btns.isEmpty()) {
                WebElement btn = btns.get(0);
                tapElement(btn);

                if (!hasPrior) {
                    if (waitUntil(() -> findWithUnimportantViews(priorConditionalBy).isEmpty(), 3)) {
                        System.out.println("Successfully confirmed 'No' selected for Prior Insurance!");
                        return this;
                    }
                } else {
                    if (waitUntil(() -> !findWithUnimportantViews(priorConditionalBy).isEmpty(), 3)) {
                        System.out.println("Successfully confirmed 'Yes' selected for Prior Insurance (conditional fields visible)!");
                        handlePriorInsuranceProviderDropdownIfPresent(providerDdBy);
                        return this;
                    }
                    System.out.println("Prior Insurance 'Yes' attempt " + (attempt + 1)
                            + " did not expand conditional fields yet; retrying...");
                }
            } else {
                scrollDown();
            }
        }
        if (hasPrior) {
            handlePriorInsuranceProviderDropdownIfPresent(providerDdBy);
        }
        return this;
    }

    private void handlePriorInsuranceProviderDropdownIfPresent(By providerDdBy) {
        try {
            List<WebElement> dds = findWithUnimportantViews(providerDdBy);
            if (dds.isEmpty() || !dds.get(0).isDisplayed()) {
                return;
            }
            System.out.println("Prior insurance provider dropdown detected; selecting an option...");
            WebElement dd = dds.get(0);
            org.openqa.selenium.Rectangle ddRect = dd.getRect();
            tapElement(dd);

            By optionItemsBy = By.xpath(
                "//android.view.View[@scrollable='true']//android.view.View[@clickable='true' and @content-desc and string-length(@content-desc) > 2] "
                + "| //*[@role='menuitem' or @role='option']");
            waitUntil(() -> !findWithUnimportantViews(optionItemsBy).isEmpty(), 3);
            List<WebElement> opts = findWithUnimportantViews(optionItemsBy);
            boolean selected = false;
            for (WebElement opt : opts) {
                try {
                    String desc = opt.getAttribute("content-desc");
                    String low = (desc != null ? desc.trim().toLowerCase() : "");
                    if (opt.isDisplayed() && !low.isEmpty()
                            && !"yes".equals(low) && !"no".equals(low)
                            && !"next".equals(low) && !"proceed".equals(low)
                            && !"close".equals(low) && !"back".equals(low)
                            && !low.contains("prior insurance provider")) {
                        System.out.println("Selecting prior insurance provider option: " + desc);
                        tapElement(opt);
                        selected = true;
                        break;
                    }
                } catch (Exception ignore) {}
            }
            if (!selected) {
                int optX = ddRect.getX() + (ddRect.getWidth() / 2);
                int optY = ddRect.getY() + ddRect.getHeight() + Math.max(85, (int) (ddRect.getHeight() * 0.75));
                System.out.println("Tapping prior provider dropdown option dynamically at (" + optX + ", " + optY + ")...");
                tapCoordinates(optX, optY);
            }
            try { Thread.sleep(500); } catch (Exception ignore) {}
        } catch (Exception e) {
            System.out.println("Prior provider dropdown selection note: " + e.getMessage());
        }
    }

    private MalpracticeScreen selectMonthYearPicker(By fieldBy, int targetYear, String fieldLabel) {
        System.out.println("Selecting " + fieldLabel + " (Target Year: " + targetYear + ")...");
        try {
            hideKeyboard();
            List<WebElement> fields = findWithUnimportantViews(fieldBy);
            for (int s = 0; s < 2 && fields.isEmpty(); s++) {
                scrollDown();
                waitUntil(() -> !findWithUnimportantViews(fieldBy).isEmpty(), 2);
                fields = findWithUnimportantViews(fieldBy);
            }
            if (fields.isEmpty()) {
                System.out.println("WARNING: " + fieldLabel + " field not found!");
                return this;
            }

            WebElement field = fields.get(0);
            String cls = "";
            try { cls = field.getAttribute("className"); } catch (Exception ignore) {}
            if (cls != null && cls.contains("EditText")) {
                sendKeys(field, "01/" + targetYear);
                hideKeyboard();
                return this;
            }

            tapElement(field);
            By confirmBy = By.xpath(
                "(//*[@text='Confirm' or @content-desc='Confirm' or @text='Done' or @content-desc='Done' or @text='OK' or @content-desc='OK'])[last()]");
            By seekBarBy = By.xpath("//android.widget.SeekBar");
            waitUntil(() -> !findWithUnimportantViews(seekBarBy).isEmpty()
                    || !findWithUnimportantViews(confirmBy).isEmpty(), 4);

            boolean swipeUpDecreasesYear = true;
            Integer lastYear = null;
            for (int attempt = 0; attempt < 8; attempt++) {
                List<WebElement> wheels = findWithUnimportantViews(seekBarBy);
                WebElement yearWheel = null;
                String yearDesc = null;
                for (WebElement w : wheels) {
                    String desc = "";
                    try { desc = w.getAttribute("content-desc"); } catch (Exception ignore) {}
                    if (desc == null || desc.trim().isEmpty()) {
                        try { desc = w.getText(); } catch (Exception ignore) {}
                    }
                    if (desc != null && desc.trim().matches("\\d{4}")) {
                        yearWheel = w;
                        yearDesc = desc.trim();
                        break;
                    }
                }
                if (yearWheel == null) {
                    List<WebElement> yearNodes = findWithUnimportantViews(By.xpath(
                        "//android.view.View[@content-desc='" + targetYear + "' or @text='" + targetYear + "']"));
                    if (!yearNodes.isEmpty()) {
                        tapElement(yearNodes.get(0));
                        break;
                    }
                    if (wheels.isEmpty() && findWithUnimportantViews(confirmBy).isEmpty()) {
                        System.out.println(fieldLabel + " wheel picker not detected; skipping scroll.");
                        break;
                    }
                    Dimension size = getSafeWindowSize();
                    int wheelX = (int) (size.width * 0.35);
                    scrollWheel(wheelX, (int) (size.height * 0.45), (int) (size.height * 0.65));
                    try { Thread.sleep(500); } catch (Exception ignore) {}
                    continue;
                }

                int currentYear = Integer.parseInt(yearDesc);
                if (currentYear == targetYear) {
                    System.out.println(fieldLabel + " year wheel aligned at " + targetYear + "!");
                    break;
                }
                if (lastYear != null) {
                    if (lastYear > targetYear && currentYear > lastYear) {
                        swipeUpDecreasesYear = !swipeUpDecreasesYear;
                    } else if (lastYear < targetYear && currentYear < lastYear) {
                        swipeUpDecreasesYear = !swipeUpDecreasesYear;
                    }
                }
                lastYear = currentYear;

                org.openqa.selenium.Rectangle r = yearWheel.getRect();
                int wheelX = r.getX() + (r.getWidth() / 2);
                int centerY = r.getY() + (r.getHeight() / 2);
                int diff = Math.abs(currentYear - targetYear);
                int deltaY = Math.min(200, Math.max(50, diff * 26));
                boolean needLowerYear = currentYear > targetYear;
                boolean swipeDown = needLowerYear ? swipeUpDecreasesYear : !swipeUpDecreasesYear;
                if (swipeDown) {
                    scrollWheel(wheelX, centerY - (deltaY / 2), centerY + (deltaY / 2));
                } else {
                    scrollWheel(wheelX, centerY + (deltaY / 2), centerY - (deltaY / 2));
                }
                try { Thread.sleep(500); } catch (Exception ignore) {}
            }

            List<WebElement> confirms = findWithUnimportantViews(confirmBy);
            if (!confirms.isEmpty()) {
                tapElement(confirms.get(confirms.size() - 1));
                waitUntil(() -> findWithUnimportantViews(confirmBy).isEmpty(), 3);
            }
            List<WebElement> updated = findWithUnimportantViews(fieldBy);
            String val = !updated.isEmpty() ? updated.get(0).getText() : null;
            System.out.println("Verified " + fieldLabel + " text after picker confirm: '" + val + "'");
        } catch (Exception e) {
            System.out.println(fieldLabel + " picker handling note: " + e.getMessage());
        }
        return this;
    }

    public MalpracticeScreen enterPriorPolicyNumber(String policyNumber) {
        System.out.println("Entering Prior Policy Number: " + policyNumber);
        try {
            hideKeyboard();
            By policyBy = By.xpath("//*[@resource-id='textfield_policy_number' or contains(@hint, 'Policy number')]");
            List<WebElement> fields = findWithUnimportantViews(policyBy);
            if (fields.isEmpty()) {
                scrollDown();
                waitUntil(() -> !findWithUnimportantViews(policyBy).isEmpty(), 2);
                fields = findWithUnimportantViews(policyBy);
            }
            if (fields.isEmpty()) {
                scrollUp();
                waitUntil(() -> !findWithUnimportantViews(policyBy).isEmpty(), 2);
                fields = findWithUnimportantViews(policyBy);
            }
            if (!fields.isEmpty()) {
                sendKeys(fields.get(0), policyNumber);
                List<WebElement> refreshed = findWithUnimportantViews(policyBy);
                String val = !refreshed.isEmpty() ? refreshed.get(0).getText() : null;
                if (val == null || val.trim().isEmpty()) {
                    tapElement(fields.get(0));
                    try { Thread.sleep(300); } catch (Exception ignore) {}
                    adbInput(policyNumber);
                }
                hideKeyboard();
            } else {
                System.out.println("WARNING: Prior Policy Number field (textfield_policy_number) not found!");
            }
        } catch (Exception e) {
            System.out.println("Prior Policy Number entry note: " + e.getMessage());
        }
        return this;
    }

    public MalpracticeScreen enterPriorPolicyExpiryDate(int targetYear) {
        By expiryBy = By.xpath(
            "//*[@resource-id='textfield_expiry_date' or contains(@hint, 'Expiry date') or contains(@hint, 'expiry date')]");
        return selectMonthYearPicker(expiryBy, targetYear, "Prior Policy Expiry Date");
    }

    public MalpracticeScreen enterPriorPolicyExpiryDate() {
        return enterPriorPolicyExpiryDate(2027);
    }

    public MalpracticeScreen enterRetroactiveDate(String date) {
        int targetYear = 2023;
        try {
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\b(19\\d{2}|20\\d{2})\\b").matcher(date);
            if (m.find()) {
                targetYear = Integer.parseInt(m.group(1));
            }
        } catch (Exception ignore) {}
        By retroBy = By.xpath(
            "//*[@resource-id='textfield_retrospective_date' or @resource-id='textfield_retroactive_date' "
            + "or contains(@resource-id, 'retrospective') or contains(@resource-id, 'retroactive') "
            + "or contains(@hint, 'Retrospective') or contains(@hint, 'Retroactive')]");
        return selectMonthYearPicker(retroBy, targetYear, "Retrospective / Prior Acts Date");
    }

    public MalpracticeScreen declarePriorClaims(boolean hasClaims) {
        System.out.println("Declaring Prior Malpractice Claims: " + (hasClaims ? "Yes" : "No"));
        try {
            hideKeyboard();
            By claimsSwitchBy = By.xpath(
                "//android.view.View[contains(@content-desc, 'previous claims')]/preceding::android.widget.Switch[1]");
            List<WebElement> switches = findWithUnimportantViews(claimsSwitchBy);
            if (switches.isEmpty()) {
                By policyBy = By.xpath("//*[@resource-id='textfield_policy_number' or @resource-id='textfield_expiry_date']");
                if (!findWithUnimportantViews(policyBy).isEmpty()) {
                    scrollUp();
                } else {
                    scrollDown();
                }
                waitUntil(() -> !findWithUnimportantViews(claimsSwitchBy).isEmpty(), 2);
                switches = findWithUnimportantViews(claimsSwitchBy);
            }
            if (!switches.isEmpty()) {
                WebElement sw = switches.get(0);
                String checked = null;
                try { checked = sw.getAttribute("checked"); } catch (Exception ignore) {}
                boolean currentlyChecked = "true".equalsIgnoreCase(checked);
                if (hasClaims && !currentlyChecked) {
                    clickPrivacySwitch(claimsSwitchBy);
                } else if (!hasClaims && currentlyChecked) {
                    tapElement(sw);
                } else {
                    System.out.println("'I have previous claims' switch already in target state: checked=" + currentlyChecked);
                }
                try { Thread.sleep(400); } catch (Exception ignore) {}
                return this;
            }

            By specificClaimsBy = hasClaims
                ? By.xpath("//*[@resource-id='btn_claims_yes']")
                : By.xpath("//*[@resource-id='btn_claims_no']");
            List<WebElement> specificBtns = findWithUnimportantViews(specificClaimsBy);
            if (!specificBtns.isEmpty()) {
                tapElement(specificBtns.get(specificBtns.size() - 1));
            } else {
                System.out.println("WARNING: 'I have previous claims' switch / Yes-No button not found!");
            }
            try { Thread.sleep(500); } catch (Exception ignore) {}
        } catch (Exception e) {
            System.out.println("Prior claims declaration note: " + e.getMessage());
        }
        return this;
    }

    public MalpracticeScreen acceptPrivacyNotice() {
        System.out.println("Toggling Privacy notice switch via dynamic locator...");
        hideKeyboard();
        scrollDown();
        try { Thread.sleep(500); } catch (Exception ignore) {}
        By switchBy = By.xpath("(//*[@resource-id='tgl_checkbox' or contains(@resource-id, 'checkbox') or @class='android.widget.Switch'])[last()]");
        clickPrivacySwitch(switchBy);
        return this;
    }

    public MalpracticeScreen clickNext() {
        System.out.println("Clicking Next button on Practitioner Details via dynamic locator...");
        hideKeyboard();
        By nextBtnBy = By.xpath(
            "//*[@resource-id='btn_next_button']//*[@clickable='true'] "
            + "| //android.widget.Button[@clickable='true' and (@content-desc='Next' or @text='Next')] "
            + "| //*[@resource-id='btn_next_button'] "
            + "| //android.widget.Button[@content-desc='Next' or @text='Next']"
        );

        for (int attempt = 1; attempt <= 3; attempt++) {
            System.out.println("Malpractice clickNext attempt " + attempt + " of 3...");
            List<WebElement> btns = driver.findElements(nextBtnBy);
            if (btns.isEmpty()) btns = findWithUnimportantViews(nextBtnBy);
            if (btns.isEmpty()) {
                scrollDown();
                btns = findWithUnimportantViews(nextBtnBy);
            }
            if (!btns.isEmpty()) {
                WebElement targetBtn = btns.get(btns.size() - 1);
                for (WebElement b : btns) {
                    if ("true".equals(b.getAttribute("clickable"))) {
                        targetBtn = b;
                        break;
                    }
                }
                tapElement(targetBtn);
            } else if (isElementVisible(nextButton)) {
                tapElement(nextButton);
            }

            boolean transitioned = waitUntil(() -> {
                boolean onOtp = !driver.findElements(By.xpath(
                    "//*[contains(@content-desc, 'OTP') or contains(@text, 'OTP') or contains(@content-desc, 'Verification') or contains(@text, 'Verification') or @resource-id='textfield_otp_code' or contains(@resource-id, 'otp_pin') or contains(@resource-id, 'pin_field')]")).isEmpty();
                boolean onQuote = !driver.findElements(By.xpath(
                    "//*[contains(@content-desc, 'quote') or contains(@content-desc, 'Plan') or @resource-id='btn_buy_now']")).isEmpty();
                boolean onReferral = isUnderwritingReferralDisplayed();
                return onOtp || onQuote || onReferral;
            }, 10);

            if (transitioned) {
                System.out.println("Successfully transitioned past Practitioner Details to OTP / Quote / Underwriting!");
                return this;
            }

            if (isErrorDialogDisplayed() || isValidationErrorDisplayed()) {
                System.out.println("Still on Practitioner Details with validation errors/incomplete fields.");
                break;
            }
        }
        return this;
    }

    // =========================================================================
    // 3. PROFESSIONAL DETAILS & SPECIALTY
    // =========================================================================
    public MalpracticeScreen selectSpecialty(String specialty) {
        System.out.println("Selecting Specialty: " + specialty);
        try {
            By locator = By.xpath("//*[contains(@content-desc, '" + specialty + "') or contains(@text, '" + specialty + "')]");
            WebElement el = driver.findElement(locator);
            tapElement(el);
        } catch (Exception e) {
            System.out.println("Specialty option not found directly: " + e.getMessage());
        }
        return this;
    }

    public MalpracticeScreen enterLicenseNumber(String license) {
        System.out.println("Entering SCFHS License Number: " + license);
        try {
            By locator = By.xpath("//*[@resource-id='textfield_license_number' or contains(@resource-id, 'license') or contains(@hint, 'License') or contains(@hint, 'license')]");
            WebElement el = driver.findElement(locator);
            tapElement(el);
            sendKeys(el, license);
            hideKeyboard();
        } catch (Exception ignore) {}
        return this;
    }

    public MalpracticeScreen enterHospitalName(String hospital) {
        System.out.println("Entering Workplace / Hospital: " + hospital);
        try {
            By locator = By.xpath("//*[@resource-id='textfield_hospital_name' or contains(@resource-id, 'hospital') or contains(@hint, 'Hospital') or contains(@hint, 'hospital') or contains(@hint, 'Workplace')]");
            WebElement el = driver.findElement(locator);
            tapElement(el);
            sendKeys(el, hospital);
            hideKeyboard();
        } catch (Exception ignore) {}
        return this;
    }

    public MalpracticeScreen clickProfessionalProceed() {
        System.out.println("Clicking Proceed button on Professional Details via dynamic locator...");
        hideKeyboard();
        By locator = By.xpath("//*[@resource-id='btn_proceed' or @resource-id='btn_next_button' or @content-desc='Proceed' or @text='Proceed']");
        try {
            WebElement btn = driver.findElement(locator);
            tapElement(btn);
        } catch (Exception e) {
            System.out.println("Proceed button click: " + e.getMessage());
        }
        try { Thread.sleep(2000); } catch (Exception ignore) {}
        return this;
    }

    public MalpracticeScreen clickSpecialtyProceed() {
        return clickProfessionalProceed();
    }

    // =========================================================================
    // 4. PLAN & DEDUCTIBLE SELECTION
    // =========================================================================
    @AndroidFindBy(xpath = "//*[@resource-id='btn_buy_now' or @resource-id='btn_sales_quote_page_buy_now_button' or @content-desc='Buy now' or @text='Buy now']")
    private WebElement buyNowButton;

    public MalpracticeScreen waitForPlanSelectionScreen() {
        System.out.println("Waiting for Malpractice Insurance Quote/Plan screen...");
        WebDriverWait localWait = new WebDriverWait(driver, Duration.ofSeconds(30));
        localWait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//*[@resource-id='btn_buy_now' or contains(@content-desc, 'Buy now') or contains(@text, 'Buy now') or contains(@content-desc, 'quote') or contains(@text, 'quote')]")));
        System.out.println("Malpractice Plan Selection Screen loaded!");
        return this;
    }

    public MalpracticeScreen waitForQuoteScreen() {
        return waitForPlanSelectionScreen();
    }

    public MalpracticeScreen selectDeductible(String deductibleAmount) {
        System.out.println("Selecting Deductible Option: " + deductibleAmount);
        try {
            By deductibleBy = By.xpath(
                "//*[contains(@content-desc, '" + deductibleAmount + "') or contains(@text, '" + deductibleAmount + "')]");
            List<WebElement> options = driver.findElements(deductibleBy);
            if (!options.isEmpty()) {
                tapElement(options.get(0));
            } else {
                System.out.println("Deductible option not directly visible, attempting scroll...");
                scrollDown();
                options = driver.findElements(deductibleBy);
                if (!options.isEmpty()) tapElement(options.get(0));
            }
        } catch (Exception e) {
            System.out.println("Deductible selection handled: " + e.getMessage());
        }
        return this;
    }

    public MalpracticeScreen selectCoverageLimit(String limitAmount) {
        System.out.println("Selecting Coverage Limit: " + limitAmount);
        try {
            By limitBy = By.xpath(
                "//*[contains(@content-desc, '" + limitAmount + "') or contains(@text, '" + limitAmount + "')]");
            List<WebElement> options = driver.findElements(limitBy);
            if (!options.isEmpty()) {
                tapElement(options.get(0));
            }
        } catch (Exception ignore) {}
        return this;
    }

    public MalpracticeScreen selectPlan(String planName) {
        System.out.println("Selecting Malpractice Plan: " + planName);
        try {
            By planBy = By.xpath("//*[contains(@content-desc, '" + planName + "') or contains(@text, '" + planName + "')]");
            WebElement plan = driver.findElement(planBy);
            tapElement(plan);
        } catch (Exception ignore) {}
        return this;
    }

    public MalpracticeScreen clickBuyNow() {
        System.out.println("Answering any Quote PEP / Related Party declarations before clicking Buy now...");
        try {
            if (!driver.findElements(By.xpath("//*[@resource-id='btn_no_button']")).isEmpty()) {
                answerMemberDeclarations(false);
            }
        } catch (Exception ignore) {}

        System.out.println("Clicking Buy now on Quote Screen via dynamic locator...");
        try {
            waitForVisibility(buyNowButton);
            tapElement(buyNowButton);
        } catch (Exception e) {
            WebElement btn = driver.findElement(By.xpath(
                "//*[@resource-id='btn_buy_now' or @resource-id='btn_sales_quote_page_buy_now_button' or @content-desc='Buy now' or @text='Buy now']"));
            tapElement(btn);
        }
        try { Thread.sleep(2500); } catch (Exception ignore) {}
        return this;
    }

    // =========================================================================
    // 5. CHECKOUT & NATIONAL ADDRESS
    // =========================================================================
    @AndroidFindBy(xpath = "//*[@resource-id='textfield_building_number']")
    private WebElement buildingNumberInput;

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_region']")
    private WebElement regionInput;

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_city']")
    private WebElement cityInput;

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_street_name']")
    private WebElement streetNameInput;

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_district']")
    private WebElement districtInput;

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_additional_number']")
    private WebElement additionalNumberInput;

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_zip_code']")
    private WebElement zipCodeInput;

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_iban' or contains(@resource-id, 'iban')]")
    private WebElement ibanInput;

    @AndroidFindBy(xpath = "//*[@resource-id='btn_pay_now' or @content-desc='Pay now' or @text='Pay now']")
    private WebElement checkoutPayNowButton;

    public MalpracticeScreen fillNationalAddress(String bldg, String region, String city, String street, String dist, String addNo, String zip) {
        System.out.println("Waiting for Checkout screen...");
        WebDriverWait waitLocal = new WebDriverWait(driver, Duration.ofSeconds(30));
        waitLocal.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//*[@resource-id='textfield_building_number' or @resource-id='textfield_iban' or @resource-id='btn_pay_now' or @content-desc='Pay now']")));

        if (driver.findElements(By.xpath("//*[@resource-id='textfield_building_number']")).isEmpty()) {
            System.out.println("National Address fields not present on Malpractice Checkout; skipping.");
            return this;
        }

        System.out.println("Filling National Address on Checkout...");
        sendKeys(buildingNumberInput, bldg);
        sendKeys(regionInput, region);
        sendKeys(cityInput, city);
        sendKeys(streetNameInput, street);
        sendKeys(districtInput, dist);
        sendKeys(additionalNumberInput, addNo);
        sendKeys(zipCodeInput, zip);
        hideKeyboard();
        return this;
    }

    public MalpracticeScreen fillIbanAndAcceptTerms(String iban) {
        System.out.println("Entering IBAN and accepting Terms on Medical Malpractice Checkout...");
        try {
            CheckoutScreen checkout = new CheckoutScreen();
            checkout.enterIban(iban);
            checkout.clickTermsCheckbox();
        } catch (Exception e) {
            System.out.println("CheckoutScreen delegation warning: " + e.getMessage());
        }
        return this;
    }

    public MalpracticeScreen clickCheckoutPayNow() {
        if (isPaymentOrConfirmationAlreadyActive()) {
            return this;
        }
        System.out.println("Selecting payment method and clicking Pay now on Medical Malpractice Checkout...");
        try {
            CheckoutScreen checkout = new CheckoutScreen();
            checkout.selectCreditDebitCard();
            checkout.clickPayNow();
        } catch (Exception e) {
            System.out.println("CheckoutScreen pay now warning: " + e.getMessage());
        }
        return this;
    }

    // =========================================================================
    // 6. VALIDATION & ASSERTION HELPERS
    // =========================================================================
    public boolean isQuoteLoaded() {
        try {
            return isElementVisible(buyNowButton) || !findWithUnimportantViews(By.xpath(
                "//*[@resource-id='btn_buy_now' or @resource-id='btn_sales_quote_page_buy_now_button' or contains(@content-desc, 'Buy now') or contains(@text, 'Buy now') or contains(@content-desc, 'quote') or contains(@text, 'quote')]")).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isCheckoutLoaded() {
        try {
            return !findWithUnimportantViews(By.xpath("//*[@resource-id='textfield_building_number']")).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isValidationErrorDisplayed() {
        try {
            return !findWithUnimportantViews(By.xpath(
                "//*[contains(@resource-id, 'error') or contains(@content-desc, 'invalid') or contains(@text, 'invalid') or contains(@text, 'Invalid') or contains(@content-desc, 'Invalid') or contains(@text, 'required') or contains(@content-desc, 'required') or contains(@content-desc, 'Please enter') or contains(@text, 'Please enter')]")).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPractitionerDetailsScreenDisplayed() {
        try {
            return isElementVisible(nationalIdInput) || isElementVisible(nextButton);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isUnderwritingReferralDisplayed() {
        try {
            return !findWithUnimportantViews(By.xpath(
                "//*[contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'underwriting') "
                + "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'underwriting') "
                + "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'review') "
                + "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'review') "
                + "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'referral') "
                + "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'referral') "
                + "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'excluded') "
                + "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'excluded')]")).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean waitForUnderwritingOrQuoteState(int timeoutSeconds) {
        return waitUntil(() -> isUnderwritingReferralDisplayed() || isQuoteLoaded(), timeoutSeconds);
    }
}
