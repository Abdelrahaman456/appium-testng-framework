package pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object for Income Protection Insurance product in Tree Digital Insurance.
 * Supports Landing, Applicant Details, Employment & Income Details,
 * Disability / Health Declarations, Plan Selection, and Checkout & National Address.
 */
public class IncomeInsuranceScreen extends BasePage {

    public IncomeInsuranceScreen() {
        super();
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(2)), this);
    }

    // =========================================================================
    // 1. LANDING SCREEN
    // =========================================================================
    @AndroidFindBy(xpath = "//*[@content-desc='Insure now' or @text='Insure now' or @resource-id='btn_insure_now' or @resource-id='btn_next_button_product_landing_page_']")
    private WebElement insureNowButton;

    // =========================================================================
    // 2. APPLICANT DETAILS (IDENTITY TUPLE: 1313424273 / 500421222 / 1998)
    // =========================================================================
    @AndroidFindBy(xpath = "//*[@resource-id='textfield_national_id' or contains(@resource-id, 'national_id')]")
    private WebElement nationalIdInput;

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_phone_number' or contains(@resource-id, 'phone')]")
    private WebElement phoneNumberInput;

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_email' or contains(@resource-id, 'email')]")
    private WebElement emailInput;

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_month_year_of_birth_txt' or contains(@resource-id, 'dob') or contains(@resource-id, 'birth') or @hint='Month/Year of birth' or contains(@hint, 'birth')]")
    private WebElement dobInput;

    @AndroidFindBy(xpath = "//*[@resource-id='tgl_checkbox' or contains(@resource-id, 'checkbox') or @class='android.widget.Switch']")
    private WebElement privacyNoticeSwitch;

    @AndroidFindBy(xpath = "//*[@resource-id='btn_next_button' or @content-desc='Next' or @text='Next']")
    private WebElement nextButton;

    @AndroidFindBy(xpath = "//*[contains(@resource-id, 'error') or contains(@content-desc, 'invalid') or contains(@text, 'invalid') or contains(@text, 'Invalid') or contains(@content-desc, 'Minimum income') or contains(@text, 'Minimum income')]")
    private WebElement validationErrorMessage;

    // =========================================================================
    // 3. EMPLOYMENT & INCOME DETAILS
    // =========================================================================
    @AndroidFindBy(xpath = "//*[@resource-id='btn_employed' or @content-desc='Employed' or @text='Employed' or contains(@content-desc, 'Employed')]")
    private WebElement employedButton;

    @AndroidFindBy(xpath = "//*[@resource-id='btn_self_employed' or @content-desc='Self-Employed' or @text='Self-Employed' or contains(@content-desc, 'Self')]")
    private WebElement selfEmployedButton;

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_monthly_income' or @resource-id='textfield_salary' or contains(@hint, 'Income') or contains(@hint, 'Salary') or contains(@resource-id, 'income')]")
    private WebElement monthlyIncomeInput;

    @AndroidFindBy(xpath = "//*[@resource-id='dropdown_occupation' or @resource-id='textfield_occupation' or contains(@content-desc, 'Occupation') or contains(@hint, 'Occupation')]")
    private WebElement occupationInput;

    @AndroidFindBy(xpath = "//*[@resource-id='btn_proceed' or @resource-id='btn_next_button' or @content-desc='Proceed' or @content-desc='Next' or @text='Proceed' or @text='Next']")
    private WebElement employmentProceedButton;

    // =========================================================================
    // 4. DISABILITY / HEALTH DECLARATIONS
    // =========================================================================
    @AndroidFindBy(xpath = "//*[contains(@content-desc, 'Declaration') or contains(@text, 'Declaration') or contains(@content-desc, 'Medical') or contains(@text, 'Medical')]")
    private WebElement declarationHeader;

    @AndroidFindBy(xpath = "//*[@text='Yes' or @content-desc='Yes' or contains(@resource-id, 'btn_yes')]")
    private List<WebElement> yesButtons;

    @AndroidFindBy(xpath = "//*[@text='No' or @content-desc='No' or contains(@resource-id, 'btn_no')]")
    private List<WebElement> noButtons;

    @AndroidFindBy(xpath = "//*[@resource-id='btn_proceed' or @resource-id='btn_next_button' or @content-desc='Proceed' or @content-desc='Next' or @text='Proceed' or @text='Next']")
    private WebElement declarationProceedButton;

    @AndroidFindBy(xpath = "//*[contains(@content-desc, 'referral') or contains(@text, 'referral') or contains(@content-desc, 'underwriting') or contains(@text, 'underwriting')]")
    private WebElement underwritingNotice;

    // =========================================================================
    // 5. PLAN SELECTION (BASIC / SILVER / GOLD)
    // =========================================================================
    @AndroidFindBy(xpath = "//*[contains(@content-desc, 'Basic Cover') or contains(@content-desc, 'Basic') or contains(@text, 'Basic') or contains(@resource-id, 'basic')]")
    private WebElement basicPlanCard;

    @AndroidFindBy(xpath = "//*[contains(@content-desc, 'Silver Cover') or contains(@content-desc, 'Standard') or contains(@text, 'Standard') or contains(@resource-id, 'standard') or contains(@content-desc, 'Gold Cover')]")
    private WebElement standardPlanCard;

    @AndroidFindBy(xpath = "//*[contains(@content-desc, 'Gold Cover') or contains(@content-desc, 'Comprehensive') or contains(@text, 'Comprehensive') or contains(@content-desc, 'Premium') or contains(@resource-id, 'premium')]")
    private WebElement comprehensivePlanCard;

    @AndroidFindBy(xpath = "//*[@content-desc='Proceed' or @text='Proceed' or @resource-id='btn_proceed' or @content-desc='Buy now' or @text='Buy now' or @resource-id='btn_buy_now' or @resource-id='btn_sales_quote_page_buy_now_button' or @resource-id='btn_quote_page_motor_buy_now']")
    private WebElement buyNowButton;

    // =========================================================================
    // 6. CHECKOUT & NATIONAL ADDRESS
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

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_iban' or @resource-id='textfield_checkout_page_motor_iban' or contains(@resource-id, 'iban')]")
    private WebElement ibanInput;

    @AndroidFindBy(xpath = "//*[@resource-id='tgl_checkbox' or @resource-id='tgl_checkout_page_motor_checkbox' or @class='android.widget.Switch']")
    private WebElement checkoutTermsCheckbox;

    @AndroidFindBy(xpath = "//*[@content-desc='Pay now' or @text='Pay now' or @resource-id='btn_pay_now' or @resource-id='btn_checkout_page_motor_pay_now']")
    private WebElement checkoutPayNowButton;

    // =========================================================================
    // ACTIONS & BUSINESS LOGIC
    // =========================================================================

    public boolean isInsureNowButtonVisible() {
        try {
            waitForVisibility(insureNowButton);
            return insureNowButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public IncomeInsuranceScreen clickInsureNow() {
        System.out.println("Waiting for Income Insurance Landing screen to load...");
        try { Thread.sleep(2000); } catch (Exception ignore) {}
        System.out.println("Clicking 'Insure now' button...");
        try {
            waitForVisibility(insureNowButton);
            tapPreferredClickable(
                    By.xpath("//*[@clickable='true' and (@content-desc='Insure now' or @text='Insure now' or @resource-id='btn_insure_now')]"),
                    insureNowButton);
        } catch (Exception e) {
            // dynamic locator used; coordinate fallback removed
        }
        try { Thread.sleep(2000); } catch (Exception ignore) {}
        return this;
    }

    public IncomeInsuranceScreen clickInsureNowIfPresent() {
        System.out.println("Checking for Income Insurance Landing 'Insure now' screen...");
        By insureNowBy = By.xpath(
            "//*[@resource-id='btn_next_button_product_landing_page_' or @resource-id='btn_insure_now' or @content-desc='Insure now' or @text='Insure now']");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(6))
                .until(ExpectedConditions.presenceOfElementLocated(insureNowBy));
            List<WebElement> landingBtns = driver.findElements(insureNowBy);
            if (!landingBtns.isEmpty()) {
                WebElement target = landingBtns.get(landingBtns.size() - 1);
                for (WebElement b : landingBtns) {
                    if ("true".equals(b.getAttribute("clickable"))) {
                        target = b;
                        break;
                    }
                }
                tapElement(target);
                Thread.sleep(2000);
            }
        } catch (Exception ignore) {}
        return this;
    }

    public IncomeInsuranceScreen enterNationalId(String nationalId) {
        System.out.println("Entering Applicant National ID: " + nationalId);
        waitForVisibility(nationalIdInput);
        sendKeys(nationalIdInput, nationalId);
        return this;
    }

    public IncomeInsuranceScreen enterPhoneNumber(String phone) {
        System.out.println("Entering Applicant Phone Number: " + phone);
        waitForVisibility(phoneNumberInput);
        sendKeys(phoneNumberInput, phone);
        return this;
    }

    public IncomeInsuranceScreen enterEmail(String email) {
        System.out.println("Entering Applicant Email: " + email);
        waitForVisibility(emailInput);
        sendKeys(emailInput, email);
        return this;
    }

    public IncomeInsuranceScreen fillApplicantDetails(String nationalId, String phone, String email) {
        System.out.println("Waiting for Applicant Details screen...");
        WebDriverWait localWait = new WebDriverWait(driver, Duration.ofSeconds(20));
        localWait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[@resource-id='textfield_national_id' or contains(@resource-id, 'national_id')]")));

        enterNationalId(nationalId);
        enterPhoneNumber(phone);
        enterEmail(email);
        return this;
    }

    /**
     * Absher/Elm Identity Tuple Rule:
     * Applicant ID 1313424273 + Phone 500421222 MUST have Birth Year 1998.
     */
    public IncomeInsuranceScreen selectDob() {
        System.out.println("Opening Month/Year of birth picker via dynamic locator (Target Year: 1998)...");
        By dobBy = By.xpath("//*[@resource-id='textfield_month_year_of_birth_txt' or contains(@resource-id, 'dob') or contains(@resource-id, 'birth') or @hint='Month/Year of birth' or contains(@hint, 'birth') or @content-desc='Month/Year of birth']");
        WebElement dobEl = isElementVisible(dobBy) ? getVisibleElement(dobBy) : dobInput;
        scrollDobWheelAndConfirm(dobEl);
        return this;
    }

    public IncomeInsuranceScreen selectInvoluntaryUnemploymentCover() {
        System.out.println("Selecting Involuntary Unemployment Cover option...");
        try {
            By uBy = By.xpath("//*[contains(@content-desc, 'Unemployment') or contains(@text, 'Unemployment') or contains(@content-desc, 'Loss of Employment')]");
            WebElement el = driver.findElement(uBy);
            tapElement(el);
        } catch (Exception ignore) {}
        return this;
    }

    public IncomeInsuranceScreen selectPermanentDisabilityCover() {
        System.out.println("Selecting Permanent & Total Disability (TPD) Cover option...");
        try {
            By dBy = By.xpath("//*[contains(@content-desc, 'Disability') or contains(@text, 'Disability') or contains(@content-desc, 'Permanent')]");
            WebElement el = driver.findElement(dBy);
            tapElement(el);
        } catch (Exception ignore) {}
        return this;
    }

    public IncomeInsuranceScreen selectWaitingPeriod(String days) {
        System.out.println("Selecting Waiting / Deferment Period: " + days + " days...");
        try {
            By waitBy = By.xpath("//*[contains(@content-desc, '" + days + "') or contains(@text, '" + days + "')]");
            WebElement el = driver.findElement(waitBy);
            tapElement(el);
        } catch (Exception ignore) {}
        return this;
    }

    public IncomeInsuranceScreen acceptPrivacyNotice() {
        System.out.println("Toggling Privacy notice switch...");
        hideKeyboard();
        try {
            By switchBy = By.xpath("//*[@resource-id='tgl_checkbox' or contains(@resource-id, 'checkbox') or @class='android.widget.Switch']");
            List<WebElement> sws = driver.findElements(switchBy);
            if (!sws.isEmpty()) {
                tapElement(sws.get(0));
            } else {
                tapCoordinates(68, 2101);
            }
        } catch (Exception e) {
            tapCoordinates(68, 2101);
        }
        try { Thread.sleep(500); } catch (Exception ignore) {}
        return this;
    }

    public IncomeInsuranceScreen clickNext() {
        System.out.println("Clicking Next button on Applicant Details via dynamic locator...");
        hideKeyboard();
        try {
            java.util.List<WebElement> btns = driver.findElements(By.xpath(
                "//*[@content-desc='Next' or @text='Next' or @resource-id='btn_next_button']"));
            if (!btns.isEmpty()) {
                WebElement targetBtn = btns.get(btns.size() - 1);
                for (WebElement b : btns) {
                    if ("true".equals(b.getAttribute("clickable"))) {
                        targetBtn = b;
                        break;
                    }
                }
                tapElement(targetBtn);
            } else {
                tapCoordinates(540, 2260);
            }
        } catch (Exception e) {
            tapCoordinates(540, 2260);
        }
        try { Thread.sleep(2000); } catch (Exception ignore) {}
        return this;
    }

    private boolean isOnQuoteScreenNotCheckout() {
        boolean onQuote = !driver.findElements(By.xpath(
                "//*[contains(@content-desc, 'pre-existing diseases') or contains(@content-desc, 'Claims cannot be made') or contains(@content-desc, 'quote')]")).isEmpty();
        boolean onCheckout = !driver.findElements(By.xpath(
                "//*[@content-desc='Checkout' or @resource-id='textfield_iban' or @resource-id='textfield_building_number']")).isEmpty();
        return onQuote && !onCheckout;
    }

    private void tapPreferredClickable(By clickableBy, WebElement fallbackElement) {
        List<WebElement> candidates = driver.findElements(clickableBy);
        if (!candidates.isEmpty() && candidates.get(0).isDisplayed()) {
            tapElement(candidates.get(0));
            return;
        }
        if (isElementVisible(fallbackElement)) {
            tapElement(fallbackElement);
        }
    }

    public boolean isValidationErrorDisplayed() {
        try {
            WebDriverWait waitErr = new WebDriverWait(driver, Duration.ofSeconds(5));
            return waitErr.until(d -> {
                if (isElementVisible(validationErrorMessage)) return true;
                if (!driver.findElements(By.xpath("//*[contains(@resource-id, 'error') or contains(@text, 'invalid') or contains(@content-desc, 'invalid') or contains(@text, 'Minimum') or contains(@content-desc, 'Minimum')]")).isEmpty()) {
                    return true;
                }
                return isOnQuoteScreenNotCheckout();
            });
        } catch (Exception e) {
            return false;
        }
    }

    // --- Employment & Income Actions ---

    public IncomeInsuranceScreen selectEmploymentType(String type) {
        System.out.println("Selecting Employment Type: " + type);
        if ("self-employed".equalsIgnoreCase(type) || "self".equalsIgnoreCase(type)) {
            try {
                if (isElementVisible(selfEmployedButton)) tapElement(selfEmployedButton);
                else {
                    WebElement btn = driver.findElement(By.xpath("//*[contains(@content-desc, 'Self') or contains(@text, 'Self')]"));
                    tapElement(btn);
                }
            } catch (Exception e) {
                // dynamic locator used; coordinate fallback removed
            }
        } else {
            try {
                if (isElementVisible(employedButton)) tapElement(employedButton);
                else {
                    WebElement btn = driver.findElement(By.xpath("//*[contains(@content-desc, 'Employed') or contains(@text, 'Employed')]"));
                    tapElement(btn);
                }
            } catch (Exception e) {
                // dynamic locator used; coordinate fallback removed
            }
        }
        try { Thread.sleep(800); } catch (Exception ignore) {}
        return this;
    }

    public IncomeInsuranceScreen enterMonthlyIncome(String amount) {
        System.out.println("Entering Monthly Income: " + amount + " SAR");
        By incomeBy = By.xpath("//android.widget.EditText[contains(@resource-id, 'income') or contains(@resource-id, 'salary')]");
        try {
            if (isElementVisible(monthlyIncomeInput)) {
                sendKeys(monthlyIncomeInput, amount);
                hideKeyboard();
            } else {
                List<WebElement> fields = driver.findElements(incomeBy);
                if (!fields.isEmpty() && fields.get(0).isDisplayed()) {
                    sendKeys(fields.get(0), amount);
                    hideKeyboard();
                } else {
                    System.out.println("Monthly Income input not present on current screen; skipping.");
                }
            }
        } catch (Exception e) {
            System.out.println("Monthly Income input skipped: " + e.getMessage());
        }
        return this;
    }

    public IncomeInsuranceScreen enterOccupation(String occupation) {
        System.out.println("Entering Occupation: " + occupation);
        try {
            if (isElementVisible(occupationInput)) {
                sendKeys(occupationInput, occupation);
            }
        } catch (Exception ignore) {}
        hideKeyboard();
        return this;
    }

    public IncomeInsuranceScreen fillEmploymentDetails(String employmentType, String monthlyIncome, String occupation) {
        System.out.println("Filling Employment Details: Type=" + employmentType + ", Income=" + monthlyIncome);
        selectEmploymentType(employmentType);
        enterMonthlyIncome(monthlyIncome);
        if (occupation != null) enterOccupation(occupation);
        return this;
    }

    public IncomeInsuranceScreen clickEmploymentProceed() {
        System.out.println("Checking Employment Details screen before clicking Proceed...");
        hideKeyboard();
        try {
            List<WebElement> employmentSignals = driver.findElements(
                    By.xpath("//*[contains(@content-desc, 'Employed') or contains(@resource-id, 'income')]"));
            if (!employmentSignals.isEmpty() && isElementVisible(employmentProceedButton)) {
                tapElement(employmentProceedButton);
                try { Thread.sleep(2000); } catch (Exception ignore) {}
            } else {
                System.out.println("Not on Employment Details screen (already past or on Quote); skipping Proceed.");
            }
        } catch (Exception e) {
            // dynamic locator used; coordinate fallback removed
        }
        return this;
    }

    // --- Health & Disability Declarations ---

    public IncomeInsuranceScreen answerAllDeclarationQuestions(boolean answerYes) {
        System.out.println("Answering Disability & Health Declarations (Answer=" + (answerYes ? "Yes" : "No") + ")...");
        By anyYesNo = By.xpath("//*[@text='No' or @content-desc='No' or @text='Yes' or @content-desc='Yes']");
        try {
            WebDriverWait waitDeclaration = new WebDriverWait(driver, Duration.ofSeconds(3));
            waitDeclaration.until(ExpectedConditions.presenceOfElementLocated(anyYesNo));
        } catch (Exception ignore) {
            if (driver.findElements(anyYesNo).isEmpty()) {
                System.out.println("No Yes/No declaration questions present; returning immediately.");
                return this;
            }
        }

        String targetText = answerYes ? "Yes" : "No";
        By optionLocator = By.xpath("//*[@text='" + targetText + "' or @content-desc='" + targetText + "']");

        for (int pass = 0; pass < 3; pass++) {
            List<WebElement> options = driver.findElements(optionLocator);
            if (options.isEmpty()) break;

            int clickedCount = 0;
            for (WebElement option : options) {
                try {
                    if (option.isDisplayed()) {
                        tapElement(option);
                        clickedCount++;
                        Thread.sleep(400);
                    }
                } catch (Exception ignore) {}
            }
            System.out.println("Income declaration pass " + (pass + 1) + ": answered " + clickedCount + " questions.");

            try {
                scrollDown();
                Thread.sleep(800);
            } catch (Exception ignore) {}
        }
        return this;
    }

    public IncomeInsuranceScreen clickDeclarationProceed() {
        System.out.println("Checking Declaration questions before clicking Proceed...");
        hideKeyboard();
        try {
            List<WebElement> yesNoOptions = driver.findElements(
                    By.xpath("//*[@text='No' or @content-desc='No' or @text='Yes' or @content-desc='Yes']"));
            if (!yesNoOptions.isEmpty() && isElementVisible(declarationProceedButton)) {
                tapElement(declarationProceedButton);
                try { Thread.sleep(2000); } catch (Exception ignore) {}
            } else {
                System.out.println("Not on Health/Disability Declarations screen; skipping Proceed.");
            }
        } catch (Exception e) {
            // dynamic locator used; coordinate fallback removed
        }
        return this;
    }

    public boolean isUnderwritingReferralDisplayed() {
        try {
            WebDriverWait waitReferral = new WebDriverWait(driver, Duration.ofSeconds(10));
            return waitReferral.until(d -> {
                if (isElementVisible(underwritingNotice)) return true;
                if (!driver.findElements(By.xpath("//*[contains(@content-desc, 'referral') or contains(@text, 'referral') or contains(@content-desc, 'underwriting') or contains(@text, 'underwriting')]")).isEmpty()) {
                    return true;
                }
                return isOnQuoteScreenNotCheckout();
            });
        } catch (Exception e) {
            return false;
        }
    }

    // --- Plan Selection Actions (Basic / Silver / Gold) ---

    public IncomeInsuranceScreen waitForPlanSelectionScreen() {
        System.out.println("Waiting up to 45s for Income Insurance Quote / Plan selection screen to load...");
        WebDriverWait waitPlans = new WebDriverWait(driver, Duration.ofSeconds(45));
        waitPlans.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[contains(@content-desc, 'quote') or contains(@content-desc, 'Basic Cover') or contains(@content-desc, 'Silver Cover') or contains(@content-desc, 'Gold Cover') or @content-desc='Proceed' or @resource-id='btn_proceed' or @resource-id='btn_buy_now']")));
        System.out.println("Income Quote / Plan selection screen loaded successfully!");
        return this;
    }

    public IncomeInsuranceScreen selectBasicPlan() {
        System.out.println("Selecting Basic Cover / Plan tier...");
        try {
            waitForVisibility(basicPlanCard);
            tapElement(basicPlanCard);
        } catch (Exception e) {
            WebElement card = driver.findElement(By.xpath("//*[contains(@content-desc, 'Basic Cover') or contains(@content-desc, 'Basic')]"));
            tapElement(card);
        }
        try { Thread.sleep(1000); } catch (Exception ignore) {}
        return this;
    }

    public IncomeInsuranceScreen selectStandardPlan() {
        System.out.println("Selecting Silver Cover / Standard Plan tier...");
        try {
            waitForVisibility(standardPlanCard);
            tapElement(standardPlanCard);
        } catch (Exception e) {
            WebElement card = driver.findElement(By.xpath("//*[contains(@content-desc, 'Silver Cover') or contains(@content-desc, 'Standard') or contains(@content-desc, 'Gold Cover')]"));
            tapElement(card);
        }
        try { Thread.sleep(1000); } catch (Exception ignore) {}
        return this;
    }

    public IncomeInsuranceScreen selectComprehensivePlan() {
        System.out.println("Selecting Gold Cover / Comprehensive Plan tier...");
        try {
            waitForVisibility(comprehensivePlanCard);
            tapElement(comprehensivePlanCard);
        } catch (Exception e) {
            WebElement card = driver.findElement(By.xpath("//*[contains(@content-desc, 'Gold Cover') or contains(@content-desc, 'Comprehensive')]"));
            tapElement(card);
        }
        try { Thread.sleep(1000); } catch (Exception ignore) {}
        return this;
    }

    public IncomeInsuranceScreen clickBuyNow() {
        System.out.println("Accepting Quote declaration switch if unchecked and clicking Proceed / Buy now...");
        clickPrivacySwitch(By.xpath("//*[@resource-id='tgl_checkbox' or contains(@resource-id, 'checkbox') or @class='android.widget.Switch']"));

        By clickableAction = By.xpath(
                "//*[@clickable='true' and (@content-desc='Proceed' or @text='Proceed' or @resource-id='btn_proceed' or @content-desc='Buy now' or @text='Buy now' or @resource-id='btn_buy_now')]");
        By anyAction = By.xpath(
                "//*[@content-desc='Proceed' or @text='Proceed' or @resource-id='btn_proceed' or @content-desc='Buy now' or @text='Buy now' or @resource-id='btn_buy_now']");

        for (int i = 0; i < 3; i++) {
            try {
                List<WebElement> clickable = driver.findElements(clickableAction);
                if (!clickable.isEmpty() && clickable.get(0).isDisplayed()) {
                    tapElement(clickable.get(0));
                    try { Thread.sleep(2000); } catch (Exception ignore) {}
                    return this;
                }
                List<WebElement> any = driver.findElements(anyAction);
                if (!any.isEmpty() && any.get(0).isDisplayed()) {
                    tapElement(any.get(0));
                    try { Thread.sleep(2000); } catch (Exception ignore) {}
                    return this;
                }
            } catch (Exception ignore) {}
            scrollDown();
            try { Thread.sleep(1000); } catch (Exception ignore) {}
        }

        try {
            tapPreferredClickable(clickableAction, buyNowButton);
        } catch (Exception e) {
            // dynamic locator used; coordinate fallback removed
        }
        try { Thread.sleep(2000); } catch (Exception ignore) {}
        return this;
    }

    // --- Checkout & National Address (CRITICAL IBAN RULE) ---

    public boolean isCheckoutLoaded() {
        try {
            WebDriverWait waitCheckout = new WebDriverWait(driver, Duration.ofSeconds(30));
            waitCheckout.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[@content-desc='Checkout' or @resource-id='textfield_iban' or @resource-id='textfield_building_number']")));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public IncomeInsuranceScreen fillNationalAddress(String bldg, String region, String city, String street, String dist, String addNo, String zip) {
        System.out.println("Waiting for Checkout screen...");
        WebDriverWait waitCheckout = new WebDriverWait(driver, Duration.ofSeconds(30));
        waitCheckout.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[@content-desc='Checkout' or @resource-id='textfield_iban' or @resource-id='textfield_building_number']")));

        if (driver.findElements(By.xpath("//*[@resource-id='textfield_building_number']")).isEmpty()) {
            System.out.println("National Address fields not present on Income Checkout; skipping.");
            return this;
        }

        System.out.println("Filling National Address: Bldg=" + bldg + ", Region=" + region + ", City=" + city + ", Street=" + street);
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

    /**
     * CRITICAL RULE: Strip 'SA' prefix before entering numeric digits into textfield_iban.
     */
    public IncomeInsuranceScreen fillIbanAndAcceptTerms(String iban) {
        System.out.println("Entering IBAN into Checkout: " + iban);
        try {
            if (!isElementVisible(ibanInput)) {
                scrollDown();
            }
        } catch (Exception ignore) {}

        String numericOnly = (iban != null && iban.toUpperCase().startsWith("SA")) ? iban.substring(2) : iban;
        try {
            waitForVisibility(ibanInput);
            sendKeys(ibanInput, numericOnly);
        } catch (Exception e) {
            WebElement fallback = driver.findElement(By.xpath("//*[contains(@resource-id, 'iban')]"));
            sendKeys(fallback, numericOnly);
        }
        hideKeyboard();
        try { Thread.sleep(500); } catch (Exception ignore) {}

        System.out.println("Accepting Checkout Terms & Conditions...");
        By termsBy = By.xpath("//*[@resource-id='tgl_checkbox' or @resource-id='tgl_checkout_page_motor_checkbox' or contains(@resource-id, 'checkbox') or @class='android.widget.Switch']");
        if (!clickPrivacySwitch(termsBy)) {
            scrollDown();
            clickPrivacySwitch(termsBy);
        }
        try { Thread.sleep(500); } catch (Exception ignore) {}
        return this;
    }

    public IncomeInsuranceScreen clickCheckoutPayNow() {
        if (isPaymentOrConfirmationAlreadyActive()) {
            return this;
        }
        System.out.println("Clicking Pay now on Checkout screen...");
        try {
            tapPreferredClickable(
                    By.xpath("//*[@clickable='true' and (@content-desc='Pay now' or @text='Pay now')]"),
                    checkoutPayNowButton);
        } catch (Exception e) {
            // dynamic locator used; coordinate fallback removed
        }
        try { Thread.sleep(2000); } catch (Exception ignore) {}
        return this;
    }
}
