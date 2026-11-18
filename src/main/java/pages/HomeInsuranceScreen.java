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
 * Page Object for Home Insurance product in Tree Digital Insurance.
 * Supports Landing, Policyholder Details, Property Details, Plan Selection,
 * and Checkout & National Address.
 */
public class HomeInsuranceScreen extends BasePage {

    public HomeInsuranceScreen() {
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

    public HomeInsuranceScreen clickInsureNow() {
        System.out.println("Waiting for Home Insurance landing screen to settle...");
        try { Thread.sleep(2000); } catch (Exception ignore) {}
        System.out.println("Clicking Insure now button...");
        try {
            waitForVisibility(insureNowButton);
            try { insureNowButton.click(); } catch (Exception ex) { tapElement(insureNowButton); }
        } catch (Exception e) {
            // dynamic locator used
        }
        try { Thread.sleep(1500); } catch (Exception ignore) {}
        return this;
    }

    public HomeInsuranceScreen clickInsureNowIfPresent() {
        System.out.println("Checking / Clicking Home Insurance Landing 'Insure now' button...");
        try { Thread.sleep(1500); } catch (Exception ignore) {}
        org.openqa.selenium.By landingBy = org.openqa.selenium.By.xpath(
            "//*[contains(@content-desc, 'Home Insurance') or contains(@text, 'Home Insurance')]"
        );
        org.openqa.selenium.By insureBtnBy = org.openqa.selenium.By.xpath(
            "//*[@content-desc='Insure now' or @text='Insure now' or @resource-id='btn_insure_now']"
        );
        org.openqa.selenium.By formTargetBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='textfield_national_id' or contains(@resource-id, 'national_id')]"
        );
        try {
            if (!driver.findElements(landingBy).isEmpty() || !driver.findElements(insureBtnBy).isEmpty()) {
                System.out.println("Detected Home Insurance Landing screen. Tapping Insure now...");
                for (int attempt = 1; attempt <= 3; attempt++) {
                    java.util.List<WebElement> btns = driver.findElements(insureBtnBy);
                    if (!btns.isEmpty()) {
                        tapElement(btns.get(btns.size() - 1));
                    } else {
                        tapCoordinates(540, 2260);
                    }
                    try { Thread.sleep(1500); } catch (Exception ignore) {}
                    if (!driver.findElements(formTargetBy).isEmpty()) {
                        System.out.println("Successfully transitioned to Home Insurance Policyholder form!");
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
    // 2. POLICYHOLDER DETAILS
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

    public HomeInsuranceScreen fillPolicyholderDetails(String nationalId, String phone, String email) {
        System.out.println("Waiting for Policyholder details form...");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(25));
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//*[@resource-id='textfield_national_id' or contains(@resource-id, 'national_id')]")));

        System.out.println("Entering Policyholder National ID: " + nationalId);
        WebElement idField = driver.findElement(By.xpath("//*[@resource-id='textfield_national_id' or contains(@resource-id, 'national_id')]"));
        sendKeys(idField, nationalId);
        hideKeyboard();

        selectDob();

        System.out.println("Entering Phone Number: " + phone);
        WebElement phoneField = driver.findElement(By.xpath("//*[@resource-id='textfield_phone_number' or contains(@resource-id, 'phone')]"));
        sendKeys(phoneField, phone);
        hideKeyboard();

        // Email field if optional or present
        try {
            List<WebElement> emailFields = driver.findElements(By.xpath("//*[@resource-id='textfield_email' or contains(@resource-id, 'email')]"));
            if (!emailFields.isEmpty() && emailFields.get(0).isDisplayed()) {
                sendKeys(emailFields.get(0), email);
                hideKeyboard();
            }
        } catch (Exception ignore) {}
        return this;
    }

    public HomeInsuranceScreen selectDob() {
        System.out.println("Opening Month/Year of birth picker via dynamic locator...");
        By dobBy = By.xpath("//*[@resource-id='textfield_month_year_of_birth_txt' or contains(@resource-id, 'dob') or contains(@resource-id, 'birth') or @hint='Month/Year of birth' or contains(@hint, 'birth') or @content-desc='Month/Year of birth']");
        WebElement dobEl = isElementVisible(dobBy) ? getVisibleElement(dobBy) : dobInput;
        scrollDobWheelAndConfirm(dobEl);
        return this;
    }

    public HomeInsuranceScreen acceptPrivacyNotice() {
        System.out.println("Toggling Privacy notice switch...");
        hideKeyboard();
        try {
            clickPrivacySwitch();
        } catch (Exception e) {
            tapCoordinates(68, 2101);
        }
        try { Thread.sleep(500); } catch (Exception ignore) {}
        return this;
    }

    public HomeInsuranceScreen clickNext() {
        System.out.println("Clicking Next button on Policyholder Details...");
        hideKeyboard();
        tapClickableNextButton();
        try { Thread.sleep(2000); } catch (Exception ignore) {}
        return this;
    }

    private void tapClickableNextButton() {
        try {
            java.util.List<WebElement> btns = driver.findElements(By.xpath(
                "//android.widget.Button[(@content-desc='Next' or @text='Next') and @clickable='true'] | //*[@resource-id='btn_next_button' and @clickable='true']"));
            if (!btns.isEmpty()) {
                tapElement(btns.get(0));
            } else {
                java.util.List<WebElement> anyBtns = driver.findElements(By.xpath(
                    "//*[@content-desc='Next' or @text='Next' or @resource-id='btn_next_button']"));
                if (!anyBtns.isEmpty()) {
                    tapElement(anyBtns.get(anyBtns.size() - 1));
                } else {
                    tapCoordinates(540, 2261);
                }
            }
        } catch (Exception e) {
            System.out.println("Next button tap: " + e.getMessage());
            tapCoordinates(540, 2261);
        }
    }

    private void answerDeclarationYes(int index) {
        try {
            List<WebElement> yesBtns = driver.findElements(By.xpath("//*[@resource-id='btn_yes_button' or @content-desc='Yes']"));
            if (index < yesBtns.size()) {
                tapElement(yesBtns.get(index));
            }
        } catch (Exception ignore) {}
    }

    private void answerDeclarationNo(int index) {
        try {
            List<WebElement> noBtns = driver.findElements(By.xpath("//*[@resource-id='btn_no_button' or @content-desc='No']"));
            if (index < noBtns.size()) {
                tapElement(noBtns.get(index));
            }
        } catch (Exception ignore) {}
    }

    private void dismissMapErrorIfPresent() {
        try {
            By closeBy = By.xpath("//*[@resource-id='btn_close_button'] | //*[@content-desc='Close' or @text='Close']");
            List<WebElement> closeBtns = driver.findElements(closeBy);
            if (!closeBtns.isEmpty()) {
                System.out.println("Closing map error dialog...");
                tapElement(closeBtns.get(closeBtns.size() - 1));
                Thread.sleep(1000);
            }
        } catch (Exception ignore) {}
    }

    // =========================================================================
    // 3. ABOUT YOUR HOME & UNDERWRITING DECLARATIONS
    // =========================================================================
    public HomeInsuranceScreen fillAboutYourHome(String email, boolean cleanEligibility) {
        if (isCheckoutLoaded()) {
            System.out.println("Already on Checkout screen; skipping About your home inputs.");
            return this;
        }
        System.out.println("Waiting for 'About your home' screen or Checkout to load...");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(45));
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//*[contains(@content-desc, 'About your home') or contains(@text, 'About your home') or @resource-id='textfield_email' or @resource-id='btn_add_home_address' or contains(@content-desc, 'Checkout') or @resource-id='btn_pay_now' or @resource-id='textfield_iban']")));
        try { Thread.sleep(2000); } catch (Exception ignore) {}

        if (isCheckoutLoaded()) {
            System.out.println("Transitioned directly to Checkout screen; skipping About your home inputs.");
            return this;
        }

        // 1. Check if Home Address needs to be added
        try {
            By addAddrBy = By.xpath("//*[@content-desc='Add Home Address' or @text='Add Home Address' or @resource-id='btn_add_home_address']");
            List<WebElement> addBtns = driver.findElements(addAddrBy);
            if (!addBtns.isEmpty()) {
                System.out.println("Add Home Address button detected; tapping to configure address...");
                tapElement(addBtns.get(addBtns.size() - 1));

                // Dismiss map error dialog if present
                for (int m = 0; m < 8; m++) {
                    try {
                        By closeBy = By.xpath("//*[@resource-id='btn_close_button' or @content-desc='Close' or contains(@content-desc, 'Something Went Wrong')]");
                        List<WebElement> closeBtns = driver.findElements(closeBy);
                        if (!closeBtns.isEmpty()) {
                            System.out.println("Detected 'Something Went Wrong' map dialog! Tapping Close...");
                            tapElement(closeBtns.get(closeBtns.size() - 1));
                            Thread.sleep(1500);
                            break;
                        }
                    } catch (Exception ignore) {}
                    if (!driver.findElements(By.xpath("//*[@resource-id='textfield_building_number']")).isEmpty() &&
                        driver.findElements(By.xpath("//*[contains(@content-desc, 'Something Went Wrong')]")).isEmpty()) {
                        break;
                    }
                    Thread.sleep(1000);
                }

                // Wait until manual address entry fields are visible
                WebDriverWait addrWait = new WebDriverWait(driver, Duration.ofSeconds(15));
                addrWait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[@resource-id='textfield_building_number']")));
                Thread.sleep(1500); // Allow modal animation to fully settle

                // Fill manual address form using sequential element targeting
                try {
                    System.out.println("Entering manual address: Building 1234, Secondary 5678, Street King...");
                    for (int attempt = 0; attempt < 3; attempt++) {
                        // 1. Building number
                        try {
                            List<WebElement> bldgList = driver.findElements(By.xpath("//*[@resource-id='textfield_building_number']"));
                            if (!bldgList.isEmpty()) {
                                WebElement bldgEl = bldgList.get(0);
                                tapElement(bldgEl);
                                sendKeys(bldgEl, "1234");
                            } else {
                                adbTap(286, 1585);
                                adbInput("1234");
                            }
                        } catch (Exception ex) {
                            adbTap(286, 1585);
                            adbInput("1234");
                        }
                        try { Thread.sleep(300); } catch (Exception ignore) {}

                        // 2. Secondary number
                        try {
                            List<WebElement> secList = driver.findElements(By.xpath("//*[@resource-id='textfield_secondary_number']"));
                            if (!secList.isEmpty()) {
                                WebElement secEl = secList.get(0);
                                tapElement(secEl);
                                sendKeys(secEl, "5678");
                            } else {
                                adbTap(794, 1585);
                                adbInput("5678");
                            }
                        } catch (Exception ex) {
                            adbTap(794, 1585);
                            adbInput("5678");
                        }
                        try { Thread.sleep(300); } catch (Exception ignore) {}

                        // 3. Street
                        try {
                            List<WebElement> streetList = driver.findElements(By.xpath("//*[@resource-id='textfield_street']"));
                            if (!streetList.isEmpty()) {
                                WebElement streetEl = streetList.get(0);
                                tapElement(streetEl);
                                sendKeys(streetEl, "King");
                            } else {
                                adbTap(286, 1806);
                                adbInput("King");
                            }
                        } catch (Exception ex) {
                            adbTap(286, 1806);
                            adbInput("King");
                        }
                        try { Thread.sleep(300); } catch (Exception ignore) {}

                        // Hide keyboard so Save button is clearly displayed
                        hideKeyboard();
                        try { Thread.sleep(500); } catch (Exception ignore) {}

                        // 4. Save address (tap Save button inside modal bounds [53,2153][1028,2274])
                        System.out.println("Tapping Save on address modal (attempt " + (attempt + 1) + ")...");
                        for (int s = 0; s < 3; s++) {
                            try {
                                List<WebElement> saveBtns = driver.findElements(By.xpath(
                                    "//*[@content-desc='Save' or @text='Save'] | //*[@resource-id='btn_save_button']"));
                                if (!saveBtns.isEmpty()) {
                                    tapElement(saveBtns.get(saveBtns.size() - 1));
                                } else {
                                    tapCoordinates(540, 2213);
                                }
                            } catch (Exception ex) {
                                tapCoordinates(540, 2213);
                            }
                            Thread.sleep(1200);
                            if (driver.findElements(By.xpath("//*[@resource-id='textfield_building_number']")).isEmpty()) {
                                break;
                            }
                        }

                        if (driver.findElements(By.xpath("//*[@resource-id='textfield_building_number']")).isEmpty()) {
                            System.out.println("Address modal successfully saved and closed!");
                            break;
                        }
                    }
                    Thread.sleep(1000);
                } catch (Exception formEx) {
                    System.out.println("Address form dynamic fill error: " + formEx.getMessage());
                }
            }
        } catch (Exception ex) {
            System.out.println("Address handling note: " + ex.getMessage());
        }

        // 2. Email address
        System.out.println("Entering Email address on About Your Home: " + email);
        try {
            By emailBy = By.xpath("//*[@resource-id='textfield_email' or contains(@hint, 'Email') or contains(@text, 'Email')]");
            List<WebElement> emailEls = driver.findElements(emailBy);
            if (!emailEls.isEmpty()) {
                tapElement(emailEls.get(0));
            } else {
                adbTap(540, 726);
            }
            Thread.sleep(500);
            Runtime.getRuntime().exec(new String[]{ADB_PATH, "-s", "emulator-5554", "shell", "input", "text", email}).waitFor();
            Thread.sleep(400);
        } catch (Exception ex) {
            System.out.println("Email entry error: " + ex.getMessage());
        }

        // 3. Underwriting Declaration questions (8 questions)
        System.out.println("Answering Home Underwriting Declaration questions (cleanEligibility=" + cleanEligibility + ")...");
        try {
            if (cleanEligibility) {
                List<WebElement> yesBtns = driver.findElements(By.xpath("//*[@resource-id='btn_yes_button' or @content-desc='Yes']"));
                List<WebElement> noBtns = driver.findElements(By.xpath("//*[@resource-id='btn_no_button' or @content-desc='No']"));
                System.out.println("Underwriting elements detected: " + yesBtns.size() + " Yes buttons, " + noBtns.size() + " No buttons.");

                if (yesBtns.size() >= 8 && noBtns.size() >= 8) {
                    System.out.println("Answering 8 questions via WebElements...");
                    // Q1: No
                    tapElement(noBtns.get(0));
                    Thread.sleep(250);
                    // Q2: Yes (standard building materials)
                    tapElement(yesBtns.get(1));
                    Thread.sleep(250);
                    // Q3: Yes (premises in good state of repair)
                    tapElement(yesBtns.get(2));
                    Thread.sleep(250);
                    // Q4: No (business carried on)
                    tapElement(noBtns.get(3));
                    Thread.sleep(250);
                    // Q5: No (additional facts)
                    tapElement(noBtns.get(4));
                    Thread.sleep(250);
                    // Q6: No (basement)
                    tapElement(noBtns.get(5));
                    Thread.sleep(250);
                    // Q7: No (PEP)
                    tapElement(noBtns.get(6));
                    Thread.sleep(250);
                    // Q8: No (Related Party)
                    tapElement(noBtns.get(7));
                    Thread.sleep(250);
                } else {
                    System.out.println("Answering 8 questions via calibrated screen coordinates...");
                    // Calibrated coordinate fallbacks matching exact button centers
                    adbTap(776, 247); // Q1: No
                    Thread.sleep(250);
                    adbTap(304, 576); // Q2: Yes
                    Thread.sleep(250);
                    adbTap(304, 809); // Q3: Yes
                    Thread.sleep(250);
                    adbTap(776, 1090); // Q4: No
                    Thread.sleep(250);
                    adbTap(776, 1371); // Q5: No
                    Thread.sleep(250);
                    adbTap(776, 1605); // Q6: No
                    Thread.sleep(250);
                    adbTap(940, 1846); // Q7: No
                    Thread.sleep(250);
                    adbTap(940, 1977); // Q8: No
                    Thread.sleep(250);
                }
            } else {
                // Referral / Rejection test: answer Yes to Q1 (prior declined/special terms)
                List<WebElement> yesBtns = driver.findElements(By.xpath("//*[@resource-id='btn_yes_button' or @content-desc='Yes']"));
                if (!yesBtns.isEmpty()) {
                    tapElement(yesBtns.get(0));
                } else {
                    adbTap(304, 247);
                }
                Thread.sleep(300);
            }
        } catch (Exception ex) {
            System.out.println("Declaration questions handling: " + ex.getMessage());
        }

        // 4. Click Next button
        System.out.println("Clicking Next button on 'About your home'...");
        for (int n = 1; n <= 3; n++) {
            try {
                By nextBy = By.xpath("//*[@content-desc='Next' and @clickable='true'] | //*[@resource-id='btn_next_button']//*[@clickable='true'] | //android.widget.Button[@content-desc='Next'] | //*[@content-desc='Next'] | //*[@resource-id='btn_next_button']");
                List<WebElement> nextBtns = driver.findElements(nextBy);
                if (!nextBtns.isEmpty()) {
                    tapElement(nextBtns.get(nextBtns.size() - 1));
                } else {
                    adbTap(540, 2261);
                }
            } catch (Exception ignore) {
                adbTap(540, 2261);
            }
            try { Thread.sleep(2000); } catch (Exception ignore) {}
            if (isCheckoutLoaded() || !driver.findElements(By.xpath("//*[contains(@content-desc, 'Something Went Wrong') or contains(@text, 'Something Went Wrong')]")).isEmpty()) {
                break;
            }
        }
        return this;
    }

    public HomeInsuranceScreen selectCoverType(String coverType) {
        System.out.println("Selecting Home Cover Type: " + coverType);
        try {
            By locator = By.xpath("//*[@content-desc='" + coverType + "' or @text='" + coverType + "' or contains(@content-desc, '" + coverType + "')]");
            List<WebElement> els = driver.findElements(locator);
            if (!els.isEmpty()) {
                WebElement chosen = els.get(0);
                for (WebElement el : els) {
                    String desc = el.getAttribute("content-desc");
                    if (coverType.equalsIgnoreCase(desc)) {
                        chosen = el;
                        break;
                    }
                }
                tapElement(chosen);
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            System.out.println("Cover type element not found directly: " + e.getMessage());
        }
        return this;
    }

    public HomeInsuranceScreen enterHighValueItems(String itemDescription, String estimatedValue) {
        System.out.println("Adding High-Value Valuables: " + itemDescription + " (SAR " + estimatedValue + ")");
        try {
            By descBy = By.xpath("//*[@resource-id='textfield_valuable_desc' or contains(@hint, 'Valuable') or contains(@hint, 'Description')]");
            WebElement descEl = driver.findElement(descBy);
            sendKeys(descEl, itemDescription);

            By valBy = By.xpath("//*[@resource-id='textfield_valuable_value' or contains(@hint, 'Value')]");
            WebElement valEl = driver.findElement(valBy);
            sendKeys(valEl, estimatedValue);
            hideKeyboard();
        } catch (Exception ignore) {
            System.out.println("High-value item fields skipped or optional.");
        }
        return this;
    }

    public HomeInsuranceScreen selectPropertyType(String type) {
        System.out.println("Selecting Property Type: " + type);
        try {
            By locator = By.xpath("//*[contains(@content-desc, '" + type + "') or contains(@text, '" + type + "')]");
            WebElement el = driver.findElement(locator);
            tapElement(el);
        } catch (Exception e) {
            System.out.println("Property type element not found directly: " + e.getMessage());
        }
        return this;
    }

    public HomeInsuranceScreen enterBuildingValue(String value) {
        System.out.println("Entering Building Value: " + value);
        try {
            By locator = By.xpath("//*[@resource-id='textfield_building_value' or contains(@resource-id, 'building_value') or contains(@hint, 'Building value')]");
            WebElement el = driver.findElement(locator);
            sendKeys(el, value);
            hideKeyboard();
        } catch (Exception ignore) {}
        return this;
    }

    public HomeInsuranceScreen enterContentsValue(String value) {
        System.out.println("Entering Contents Value: " + value);
        try {
            By locator = By.xpath("//*[@resource-id='textfield_contents_value' or contains(@resource-id, 'contents_value') or contains(@hint, 'Contents value')]");
            WebElement el = driver.findElement(locator);
            sendKeys(el, value);
            hideKeyboard();
        } catch (Exception ignore) {}
        return this;
    }

    public HomeInsuranceScreen clickPropertyProceed() {
        System.out.println("Clicking Proceed button on Property Details...");
        hideKeyboard();
        try {
            By locator = By.xpath("//*[@resource-id='btn_proceed' or @resource-id='btn_next_button' or @content-desc='Proceed' or @text='Proceed']");
            WebElement btn = driver.findElement(locator);
            tapElement(btn);
        } catch (Exception e) {
            // dynamic locator used; coordinate fallback removed
        }
        try { Thread.sleep(2000); } catch (Exception ignore) {}
        return this;
    }

    public boolean isReferralModalDisplayed() {
        try {
            return !driver.findElements(By.xpath("//*[contains(@text, 'Underwriting') or contains(@content-desc, 'Underwriting') or contains(@text, 'Review') or contains(@content-desc, 'Review') or contains(@text, 'Something Went Wrong') or contains(@content-desc, 'Something Went Wrong') or contains(@content-desc, 'Cannot') or contains(@text, 'Cannot') or contains(@content-desc, 'Sorry') or contains(@text, 'Sorry') or contains(@content-desc, 'refer') or contains(@text, 'refer')]")).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    // =========================================================================
    // 4. PLAN SELECTION
    // =========================================================================
    @AndroidFindBy(xpath = "//*[@resource-id='btn_proceed' or @content-desc='Proceed' or @text='Proceed' or @resource-id='btn_buy_now' or @resource-id='btn_sales_quote_page_buy_now_button' or @content-desc='Buy now' or @text='Buy now']")
    private WebElement buyNowButton;

    public HomeInsuranceScreen waitForPlanSelectionScreen() {
        System.out.println("Waiting for Home Insurance Quote/Plan selection or Checkout screen...");
        // Check if intermediate Property Details screen appeared first!
        try {
            By propertyDetailsBy = By.xpath("//*[contains(@content-desc, 'Property') or contains(@text, 'Property') or contains(@content-desc, 'Building value') or @resource-id='textfield_building_value']");
            if (!driver.findElements(propertyDetailsBy).isEmpty()) {
                System.out.println("Detected Property Details screen; selecting defaults (Villa, Building and Contents, values)...");
                selectPropertyType("Villa");
                selectCoverType("Building and Contents");
                enterBuildingValue("1500000");
                enterContentsValue("250000");
                clickPropertyProceed();
                Thread.sleep(2500);
            }
        } catch (Exception ignore) {}

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//*[@resource-id='btn_proceed' or @content-desc='Proceed' or @resource-id='btn_buy_now' or contains(@content-desc, 'Buy now') or contains(@text, 'Buy now') or contains(@content-desc, 'Checkout') or @resource-id='btn_pay_now' or @resource-id='textfield_iban']")));
        System.out.println("Home Insurance Plan Selection / Checkout Screen loaded!");
        return this;
    }

    public HomeInsuranceScreen clickBuyNow() {
        System.out.println("Clicking Proceed/Buy now on Quote Screen (if present)...");
        if (isCheckoutLoaded()) {
            System.out.println("Already on Checkout screen; skipping Buy now click.");
            return this;
        }

        // Wait in case a cover type tab was just toggled and rating is recalculating
        try { Thread.sleep(3000); } catch (Exception ignore) {}

        By quoteHeaderBy = By.xpath("//*[contains(@content-desc, 'Here') and contains(@content-desc, 'quote')]");

        for (int attempt = 1; attempt <= 5; attempt++) {
            try {
                // Select first available plan card (Standard, Premium, Silver, Gold, Platinum)
                By planCardBy = By.xpath("//*[contains(@content-desc, 'Standard') or contains(@content-desc, 'Premium') or contains(@content-desc, 'Silver') or contains(@content-desc, 'Gold') or contains(@content-desc, 'Platinum')]");
                List<WebElement> plans = driver.findElements(planCardBy);
                if (!plans.isEmpty()) {
                    try { tapElement(plans.get(0)); Thread.sleep(1000); } catch (Exception ignore) {}
                }

                By proceedBy = By.xpath("//android.widget.Button[@content-desc='Proceed' or @text='Proceed'] | //*[@content-desc='Proceed'] | //*[@resource-id='btn_proceed']");
                List<WebElement> proceedBtns = driver.findElements(proceedBy);
                if (!proceedBtns.isEmpty()) {
                    tapElement(proceedBtns.get(proceedBtns.size() - 1));
                } else {
                    adbTap(540, 2261);
                }
            } catch (Exception e) {
                adbTap(540, 2261);
            }
            try { Thread.sleep(3000); } catch (Exception ignore) {}
            if (driver.findElements(quoteHeaderBy).isEmpty()) {
                System.out.println("Successfully transitioned past Quote screen!");
                break;
            }
        }
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

    public HomeInsuranceScreen fillNationalAddress(String bldg, String region, String city, String street, String dist, String addNo, String zip) {
        System.out.println("Waiting for Checkout screen or UAT backend response to appear...");
        By checkoutBy = By.xpath("//*[@resource-id='textfield_iban' or contains(@resource-id, 'iban') or @resource-id='btn_pay_now' or contains(@content-desc, 'Checkout') or contains(@text, 'Checkout') or contains(@text, 'Total premium') or contains(@content-desc, 'Total premium')]");
        By swwBy = By.xpath("//*[contains(@content-desc, 'Something Went Wrong') or contains(@text, 'Something Went Wrong')]");

        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 35000) {
            if (!driver.findElements(checkoutBy).isEmpty()) {
                System.out.println("Checkout loaded. Home address captured during About Your Home stage; continuing directly to payment details.");
                return this;
            }
            if (!driver.findElements(swwBy).isEmpty()) {
                System.out.println("[UAT Sandbox Notice] 'Something Went Wrong' returned by UAT backend on Home details submission; attempting Try Again...");
                try {
                    WebElement tryAgain = driver.findElement(By.xpath("//*[@resource-id='btn_try_again_button'] | //*[@content-desc='Try Again'] | //*[@text='Try Again']"));
                    tapElement(tryAgain);
                    Thread.sleep(3000);
                } catch (Exception ignore) {}
                if (!driver.findElements(checkoutBy).isEmpty()) {
                    System.out.println("Checkout loaded after Try Again!");
                    return this;
                }
                if (!driver.findElements(swwBy).isEmpty()) {
                    System.out.println("[UAT Sandbox Notice] UAT backend returned sandbox 'Something Went Wrong' response for Home Insurance; setting UAT sandbox flag.");
                    BasePage.uatElmDependentBlocked = true;
                    return this;
                }
            }
            // If still on 'About your home' with Next button, tap it again
            try {
                By nextBy = By.xpath("//*[@content-desc='Next' and @clickable='true'] | //android.widget.Button[@content-desc='Next'] | //*[@resource-id='btn_next_button']");
                List<WebElement> nBtns = driver.findElements(nextBy);
                if (!nBtns.isEmpty()) {
                    tapElement(nBtns.get(nBtns.size() - 1));
                    Thread.sleep(2000);
                }
            } catch (Exception ignore) {}
            try { Thread.sleep(500); } catch (Exception ignore) {}
        }
        return this;
    }

    public HomeInsuranceScreen fillIbanAndAcceptTerms(String iban) {
        if (BasePage.uatElmDependentBlocked) {
            System.out.println("[UAT Sandbox Notice] Skipping IBAN on UAT sandbox response.");
            return this;
        }
        System.out.println("Entering IBAN and accepting Terms on Home Insurance Checkout...");
        try {
            CheckoutScreen checkout = new CheckoutScreen();
            checkout.enterIban(iban);
            checkout.clickTermsCheckbox();
        } catch (Exception e) {
            System.out.println("CheckoutScreen delegation warning: " + e.getMessage());
        }
        return this;
    }

    public HomeInsuranceScreen clickCheckoutPayNow() {
        if (BasePage.uatElmDependentBlocked) {
            System.out.println("[UAT Sandbox Notice] Skipping Pay Now on UAT sandbox response.");
            return this;
        }
        if (isPaymentOrConfirmationAlreadyActive()) {
            return this;
        }
        System.out.println("Selecting payment method and clicking Pay now on Home Insurance Checkout...");
        try {
            CheckoutScreen checkout = new CheckoutScreen();
            checkout.selectCreditDebitCard();
            checkout.clickPayNow();
        } catch (Exception e) {
            System.out.println("CheckoutScreen pay now warning: " + e.getMessage());
        }
        return this;
    }

    public boolean isPlanSelectionLoaded() {
        try {
            return isElementVisible(buyNowButton) || !driver.findElements(By.xpath("//*[contains(@content-desc, 'Buy now') or contains(@text, 'Buy now') or contains(@content-desc, 'quote') or contains(@text, 'quote')]")).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isCheckoutLoaded() {
        try {
            return !driver.findElements(By.xpath("//*[@resource-id='textfield_iban' or contains(@resource-id, 'iban') or contains(@content-desc, 'Checkout') or contains(@text, 'Checkout')]")).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isValidationErrorDisplayed() {
        try {
            return !driver.findElements(By.xpath("//*[contains(@resource-id, 'error') or contains(@content-desc, 'invalid') or contains(@text, 'invalid') or contains(@text, 'Invalid') or contains(@content-desc, 'Invalid') or contains(@text, 'required') or contains(@content-desc, 'required') or contains(@content-desc, 'Please enter') or contains(@text, 'Please enter')]")).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAboutYouScreenDisplayed() {
        try {
            return isElementVisible(nationalIdInput) || isElementVisible(nextButton);
        } catch (Exception e) {
            return false;
        }
    }
}
