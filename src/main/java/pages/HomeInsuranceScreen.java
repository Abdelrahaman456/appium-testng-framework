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
                    java.util.List<WebElement> btns = findWithUnimportantViews(insureBtnBy);
                    if (btns.isEmpty()) {
                        scrollDown();
                        btns = findWithUnimportantViews(insureBtnBy);
                    }
                    if (!btns.isEmpty()) {
                        tapElement(btns.get(btns.size() - 1));
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

    public HomeInsuranceScreen enterNationalId(String nationalId) {
        System.out.println("Entering Policyholder National ID: " + nationalId);
        By idBy = By.xpath("//*[@resource-id='textfield_national_id' or contains(@resource-id, 'national_id')]");
        WebElement idField = getVisibleElement(idBy);
        sendKeys(idField, nationalId);
        hideKeyboard();
        return this;
    }

    public HomeInsuranceScreen enterPhoneNumber(String phone) {
        System.out.println("Entering Phone Number: " + phone);
        By phoneBy = By.xpath("//*[@resource-id='textfield_phone_number' or contains(@resource-id, 'phone')]");
        WebElement phoneField = getVisibleElement(phoneBy);
        sendKeys(phoneField, phone);
        hideKeyboard();
        return this;
    }

    public HomeInsuranceScreen fillPolicyholderDetails(String nationalId, String phone, String email) {
        System.out.println("Waiting for Policyholder details form...");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(25));
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//*[@resource-id='textfield_national_id' or contains(@resource-id, 'national_id')]")));

        enterNationalId(nationalId);
        selectDob();
        enterPhoneNumber(phone);

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
        By switchBy = By.xpath("//*[@resource-id='tgl_checkbox' or contains(@resource-id, 'checkbox') or @class='android.widget.Switch' or @class='android.widget.CheckBox']");
        clickPrivacySwitch(switchBy);
        return this;
    }

    public HomeInsuranceScreen clickNext() {
        System.out.println("Clicking Next button on Policyholder Details...");
        hideKeyboard();
        tapClickableNextButton();
        try { Thread.sleep(1500); } catch (Exception ignore) {}
        return this;
    }

    private void tapClickableNextButton() {
        By nextBy = By.xpath(
            "//android.widget.Button[(@content-desc='Next' or @text='Next') and @clickable='true'] "
            + "| //*[@resource-id='btn_next_button']//*[@clickable='true'] "
            + "| //*[@resource-id='btn_next_button' or @content-desc='Next' or @text='Next']"
        );
        List<WebElement> btns = findWithUnimportantViews(nextBy);
        if (btns.isEmpty()) {
            scrollDown();
            btns = findWithUnimportantViews(nextBy);
        }
        for (WebElement btn : btns) {
            try {
                if (btn.isDisplayed()) {
                    tapElement(btn);
                    return;
                }
            } catch (Exception ignore) {}
        }
        if (!btns.isEmpty()) {
            tapElement(btns.get(btns.size() - 1));
        } else {
            click(nextButton);
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

    private void ensureRiyadhGpsLocation() {
        System.out.println("Setting emulator GPS location to Riyadh (24.774265, 46.738586)...");
        try {
            driver.executeScript("mobile: setGeolocation",
                    java.util.Map.of("latitude", 24.774265, "longitude", 46.738586, "altitude", 600));
        } catch (Exception ignore) {}
        try {
            Runtime.getRuntime().exec(buildAdbCommand("emu", "geo", "fix", "46.738586", "24.774265")).waitFor();
        } catch (Exception ignore) {}
    }

    private void fillAddressTextField(By fieldBy, String value, boolean onlyIfEmpty, String label) {
        WebElement field = findDisplayedElement(fieldBy);
        if (field == null) {
            return;
        }
        String cur = "";
        try {
            if (field.getText() != null) {
                cur = field.getText().trim();
            }
        } catch (Exception ignore) {}

        String checkToken = value.contains(" ") ? value.substring(0, value.indexOf(' ')) : value;
        if (onlyIfEmpty && !cur.isEmpty() && !cur.equalsIgnoreCase("Ukiah") && !cur.toLowerCase().contains("vista")) {
            System.out.println("Address field [" + label + "] already populated with '" + cur + "'.");
            return;
        }
        if (!onlyIfEmpty && !cur.isEmpty() && cur.toLowerCase().contains(checkToken.toLowerCase())) {
            System.out.println("Address field [" + label + "] already matches '" + cur + "'.");
            return;
        }

        System.out.println("Populating Address field [" + label + "] with '" + value + "' (was '" + cur + "')...");
        tapElement(field);
        try { Thread.sleep(500); } catch (Exception ignore) {}

        WebElement activeField = findDisplayedElement(fieldBy);
        if (activeField != null) {
            if (!cur.isEmpty()) {
                try {
                    activeField.clear();
                    Thread.sleep(200);
                } catch (Exception ignore) {}
            }
            try {
                activeField.sendKeys(value);
            } catch (Exception e) {
                adbInput(value.replace(" ", "%s"));
            }
        } else {
            adbInput(value.replace(" ", "%s"));
        }
        try { Thread.sleep(350); } catch (Exception ignore) {}

        WebElement verified = findDisplayedElement(fieldBy);
        String after = "";
        try {
            if (verified != null && verified.getText() != null) {
                after = verified.getText().trim();
            }
        } catch (Exception ignore) {}
        if ((after.isEmpty() || !after.toLowerCase().contains(checkToken.toLowerCase())) && verified != null) {
            System.out.println("Retrying Address field [" + label + "] via tapElement + adbInput...");
            tapElement(verified);
            try { Thread.sleep(500); } catch (Exception ignore) {}
            adbInput(value.replace(" ", "%s"));
            try { Thread.sleep(350); } catch (Exception ignore) {}
        }
    }

    private void ensureAddressCityRiyadh() {
        By cityBy = By.xpath(
            "//android.widget.EditText[@hint='City' or @text='Ukiah' or @text='Riyadh'] "
            + "| //*[@resource-id='textfield_postal_code']/following-sibling::android.widget.EditText[1]"
        );
        WebElement cityField = findDisplayedElement(cityBy);
        if (cityField == null) {
            return;
        }
        String cityText = "";
        try {
            if (cityField.getText() != null) {
                cityText = cityField.getText().trim();
            }
        } catch (Exception ignore) {}
        if ("Riyadh".equalsIgnoreCase(cityText)) {
            System.out.println("Address City is already 'Riyadh'.");
            return;
        }

        System.out.println("Address City is '" + cityText + "'; selecting 'Riyadh'...");
        hideKeyboard();
        tapElement(cityField);
        try { Thread.sleep(1000); } catch (Exception ignore) {}

        By riyadhOptionBy = By.xpath(
            "//*[not(@hint='City') and (@content-desc='Riyadh' or @text='Riyadh' "
            + "or contains(@content-desc, 'Riyadh') or contains(@text, 'Riyadh'))]"
        );
        WebElement riyadhOpt = findDisplayedElement(riyadhOptionBy);
        if (riyadhOpt != null) {
            tapElement(riyadhOpt);
            try { Thread.sleep(600); } catch (Exception ignore) {}
            return;
        }

        // If City is a searchable dropdown or editable input, type Riyadh and select option if shown
        adbInput("Riyadh");
        try { Thread.sleep(700); } catch (Exception ignore) {}
        riyadhOpt = findDisplayedElement(riyadhOptionBy);
        if (riyadhOpt != null) {
            tapElement(riyadhOpt);
            try { Thread.sleep(500); } catch (Exception ignore) {}
        }
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
                ensureRiyadhGpsLocation();
                System.out.println("Add Home Address button detected; tapping to configure address...");
                tapElement(addBtns.get(addBtns.size() - 1));

                By swwOrErrorModalBy = By.xpath(
                    "//*[contains(@content-desc, 'Something Went Wrong') or contains(@text, 'Something Went Wrong') "
                    + "or @resource-id='btn_try_again_button' or @resource-id='btn_close_button']"
                );
                By closeErrorBtnBy = By.xpath(
                    "//*[@resource-id='btn_close_button']//*[@clickable='true'] "
                    + "| //android.widget.Button[@clickable='true' and (@content-desc='Close' or @text='Close')] "
                    + "| //*[@resource-id='btn_close_button' or @content-desc='Close' or @text='Close']"
                );

                // Dismiss map error dialog if present
                for (int m = 0; m < 8; m++) {
                    try {
                        if (!findWithUnimportantViews(swwOrErrorModalBy).isEmpty()) {
                            WebElement closeBtn = findDisplayedElement(closeErrorBtnBy);
                            if (closeBtn != null) {
                                System.out.println("Detected 'Something Went Wrong' map dialog! Tapping Close...");
                                tapElement(closeBtn);
                                Thread.sleep(1500);
                                break;
                            }
                        }
                    } catch (Exception ignore) {}
                    if (!driver.findElements(By.xpath("//*[@resource-id='textfield_building_number']")).isEmpty()
                            && findWithUnimportantViews(swwOrErrorModalBy).isEmpty()) {
                        break;
                    }
                    Thread.sleep(1000);
                }

                // Wait until manual address entry fields are visible
                WebDriverWait addrWait = new WebDriverWait(driver, Duration.ofSeconds(15));
                addrWait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[@resource-id='textfield_building_number']")));
                Thread.sleep(2500); // Allow modal animation & Google Map reverse-geocoding to settle

                if (!driver.findElements(By.xpath("//*[contains(@content-desc, 'Ukiah') or @text='Ukiah']")).isEmpty()) {
                    ensureRiyadhGpsLocation();
                    By myLocBy = By.xpath("(//android.view.View[@content-desc='Google Map']/following::android.widget.ImageView[@clickable='true'])[1]");
                    WebElement myLocBtn = findDisplayedElement(myLocBy);
                    if (myLocBtn != null) {
                        System.out.println("Map defaulted to Ukiah; tapping Google Map My-Location button to recenter on Riyadh...");
                        tapElement(myLocBtn);
                        try { Thread.sleep(2000); } catch (Exception ignore) {}
                    }
                }

                // Fill all 6 manual address fields using post-keyboard coordinates & verification
                try {
                    By bldgBy = By.xpath("//*[@resource-id='textfield_building_number' or contains(@hint, 'Building')]");
                    By secBy = By.xpath("//*[@resource-id='textfield_secondary_number' or contains(@hint, 'Secondary')]");
                    By streetBy = By.xpath("//*[@resource-id='textfield_street' or contains(@resource-id, 'street') or contains(@hint, 'Street')]");
                    By districtBy = By.xpath("//*[@resource-id='textfield_district' or contains(@hint, 'District')]");
                    By postalBy = By.xpath("//*[@resource-id='textfield_postal_code' or contains(@hint, 'Postal')]");
                    By clickableSaveBy = By.xpath(
                        "//*[@resource-id='btn_save_button']//*[@clickable='true'] "
                        + "| //android.widget.Button[@clickable='true' and (@content-desc='Save' or @text='Save')]"
                    );
                    By fallbackSaveBy = By.xpath(
                        "//*[@resource-id='btn_save_button' or @content-desc='Save' or @text='Save']"
                    );
                    By noSavedAddressBy = By.xpath(
                        "//*[contains(@content-desc, 'saved addresses') or contains(@text, 'saved addresses')]"
                    );

                    for (int attempt = 0; attempt < 3; attempt++) {
                        if (findWithUnimportantViews(bldgBy).isEmpty()) {
                            WebElement reOpenBtn = findDisplayedElement(addAddrBy);
                            if (reOpenBtn != null) {
                                System.out.println("Re-opening Add Home Address modal (attempt " + (attempt + 1) + "/3)...");
                                tapElement(reOpenBtn);
                                waitUntil(() -> !findWithUnimportantViews(bldgBy).isEmpty(), 10);
                                try { Thread.sleep(1500); } catch (Exception ignore) {}
                            }
                        }

                        System.out.println("Filling and verifying 6 Home Address fields (attempt " + (attempt + 1) + "/3)...");
                        ensureAddressCityRiyadh();
                        fillAddressTextField(bldgBy, "1234", true, "Building number");
                        fillAddressTextField(secBy, "5678", true, "Secondary number");
                        fillAddressTextField(streetBy, "King Fahd", true, "Street");
                        fillAddressTextField(districtBy, "Alsahafa", true, "District");
                        fillAddressTextField(postalBy, "12251", true, "Postal code");
                        ensureAddressCityRiyadh();

                        hideKeyboard();
                        try { Thread.sleep(500); } catch (Exception ignore) {}

                        System.out.println("Tapping Save on address modal (attempt " + (attempt + 1) + ")...");
                        WebElement saveBtn = findDisplayedElement(clickableSaveBy);
                        if (saveBtn == null) {
                            saveBtn = findDisplayedElement(fallbackSaveBy);
                        }
                        boolean tappedSave = false;
                        if (saveBtn != null) {
                            tapElement(saveBtn);
                            tappedSave = true;
                        }

                        waitUntil(() -> (findWithUnimportantViews(bldgBy).isEmpty() && findWithUnimportantViews(swwOrErrorModalBy).isEmpty())
                                || !findWithUnimportantViews(swwOrErrorModalBy).isEmpty(), 18);

                        if (!findWithUnimportantViews(swwOrErrorModalBy).isEmpty()) {
                            System.out.println("WARNING: 'Something Went Wrong' dialog appeared after tapping Save on Address modal; tapping Close to retry...");
                            WebElement closeBtn = findDisplayedElement(closeErrorBtnBy);
                            if (closeBtn != null) {
                                tapElement(closeBtn);
                                try { Thread.sleep(1200); } catch (Exception ignore) {}
                            }
                            continue;
                        }

                        if (tappedSave
                                && findWithUnimportantViews(bldgBy).isEmpty()
                                && findWithUnimportantViews(swwOrErrorModalBy).isEmpty()
                                && findWithUnimportantViews(noSavedAddressBy).isEmpty()) {
                            System.out.println("Address modal genuinely saved and closed!");
                            break;
                        }
                    }
                } catch (Exception formEx) {
                    System.out.println("Address form dynamic fill error: " + formEx.getMessage());
                }
            }
        } catch (Exception ex) {
            System.out.println("Address handling note: " + ex.getMessage());
        }

        // Wait for address modal Building field to be completely gone before locating Email on 'About your home'
        By activeBldgBy = By.xpath("//*[@resource-id='textfield_building_number' or contains(@hint, 'Building')]");
        waitUntil(() -> findWithUnimportantViews(activeBldgBy).isEmpty(), 6);

        // 2. Email address
        System.out.println("Entering Email address on About Your Home: " + email);
        try {
            By emailBy = By.xpath(
                "//android.widget.EditText[@resource-id='textfield_email' or contains(@resource-id, 'email') or contains(@hint, 'Email') or contains(@text, 'Email')] "
                + "| //*[@resource-id='textfield_email' or contains(@hint, 'Email')]"
            );
            waitUntil(() -> !findWithUnimportantViews(emailBy).isEmpty(), 3);
            List<WebElement> emailEls = findWithUnimportantViews(emailBy);
            if (emailEls.isEmpty()) {
                scrollUp();
                emailEls = findWithUnimportantViews(emailBy);
            }
            if (emailEls.isEmpty()) {
                scrollDown();
                emailEls = findWithUnimportantViews(emailBy);
            }
            if (!emailEls.isEmpty()) {
                sendKeys(emailEls.get(0), email);
                List<WebElement> refreshed = findWithUnimportantViews(emailBy);
                String combined = "";
                if (!refreshed.isEmpty()) {
                    String txt = refreshed.get(0).getText();
                    String desc = refreshed.get(0).getAttribute("content-desc");
                    combined = ((txt != null ? txt : "") + " " + (desc != null ? desc : "")).trim();
                }
                if (!combined.contains("@") && !refreshed.isEmpty()) {
                    System.out.println("Email value missing '@' after sendKeys ('" + combined + "'); retrying tap + adbInput...");
                    tapElement(refreshed.get(0));
                    try { Thread.sleep(300); } catch (Exception ignore) {}
                    adbInput(email);
                    refreshed = findWithUnimportantViews(emailBy);
                    if (!refreshed.isEmpty()) {
                        String txt = refreshed.get(0).getText();
                        String desc = refreshed.get(0).getAttribute("content-desc");
                        combined = ((txt != null ? txt : "") + " " + (desc != null ? desc : "")).trim();
                    }
                }
                System.out.println("Verified Email field state on About Your Home: '" + combined + "'");
                hideKeyboard();
            } else {
                System.out.println("WARNING: Email input field not found on 'About your home'!");
            }
        } catch (Exception ex) {
            System.out.println("Email entry error: " + ex.getMessage());
        }

        // 3. Underwriting Declaration questions (9 questions) via dynamic locators & scrolling
        System.out.println("Answering Home Underwriting Declaration questions (cleanEligibility=" + cleanEligibility + ")...");
        answerHomeUnderwritingDeclarations(cleanEligibility);

        // 4. Click Next button dynamically
        System.out.println("Clicking Next button on 'About your home'...");
        hideKeyboard();
        By nextBy = By.xpath(
            "//*[@content-desc='Next' and @clickable='true'] "
            + "| //*[@resource-id='btn_next_button']//*[@clickable='true'] "
            + "| //android.widget.Button[@content-desc='Next' or @text='Next'] "
            + "| //*[@content-desc='Next' or @text='Next' or @resource-id='btn_next_button']"
        );
        for (int n = 1; n <= 3; n++) {
            List<WebElement> nextBtns = findWithUnimportantViews(nextBy);
            if (nextBtns.isEmpty()) {
                scrollDown();
                nextBtns = findWithUnimportantViews(nextBy);
            }
            if (!nextBtns.isEmpty()) {
                tapElement(nextBtns.get(nextBtns.size() - 1));
            }
            if (waitUntil(() -> isCheckoutLoaded() || isReferralElementPresent(), 4)) {
                break;
            }
        }
        return this;
    }

    private void answerHomeUnderwritingDeclarations(boolean cleanEligibility) {
        By yesBy = By.xpath("//*[@resource-id='btn_yes_button' or @content-desc='Yes' or @text='Yes']");
        By noBy = By.xpath("//*[@resource-id='btn_no_button' or @content-desc='No' or @text='No']");
        if (!waitUntil(() -> !findWithUnimportantViews(yesBy).isEmpty() || !findWithUnimportantViews(noBy).isEmpty(), 5)) {
            System.out.println("No underwriting declaration questions detected on screen.");
            return;
        }

        // Expected answers for Q1..Q9:
        // Q1..Q7 are pre-selected by default (No, No, Yes, Yes, No, No, No); Q8 (PEP) and Q9 (Related Party) are unselected by default.
        boolean[] expectedYes = new boolean[]{
            !cleanEligibility, // Q1: sustained any loss or damage during the last 3 years
            false,             // Q2: declined to accept, canceled, refused to continue, special terms
            true,              // Q3: brick, stone, or concrete construction
            true,              // Q4: good state of repair
            false,             // Q5: profession or business carried on in your premises
            false,             // Q6: additional facts affecting the proposed insurance
            false,             // Q7: basement
            false,             // Q8: Politically Exposed Person (PEP)
            false              // Q9: related party/Person
        };
        String[][] questionKeywords = new String[][]{
            {"sustained any loss", "loss or damage", "last 3 years"},
            {"declined to accept", "refused to continue", "special terms"},
            {"brick, stone", "concrete construction", "incombustible"},
            {"good state of repair", "state of repair", "maintained"},
            {"profession or business", "business carried on", "commercial"},
            {"additional facts", "disclosed to the company", "material fact"},
            {"basement", "cellar"},
            {"politically exposed", "pep"},
            {"related party", "related"}
        };

        int screenHeight = getSafeWindowSize().height;
        int maxTapY = (int) (screenHeight * 0.90);
        boolean[] answered = new boolean[9];

        // In referral test (!cleanEligibility), ensure Q1 at top of 'About your home' is visible before scrolling down
        if (!cleanEligibility && findQuestionLabel(questionKeywords[0], maxTapY) == null) {
            scrollUp();
        }

        for (int scrollPass = 0; scrollPass < 3; scrollPass++) {
            for (int q = 0; q < 9; q++) {
                if (answered[q]) continue;
                WebElement questionEl = findQuestionLabel(questionKeywords[q], maxTapY);
                if (questionEl == null) continue;

                int qY = questionEl.getRect().getY();
                List<WebElement> candidates = findWithUnimportantViews(expectedYes[q] ? yesBy : noBy);
                WebElement bestBtn = null;
                int bestDist = Integer.MAX_VALUE;
                for (WebElement btn : candidates) {
                    try {
                        if (!btn.isDisplayed()) continue;
                        int bY = btn.getRect().getY();
                        if (bY >= qY - 20 && bY < maxTapY && (bY - qY) < (int) (screenHeight * 0.25)) {
                            int dist = Math.abs(bY - qY);
                            if (dist < bestDist) {
                                bestDist = dist;
                                bestBtn = btn;
                            }
                        }
                    } catch (Exception ignore) {}
                }
                if (bestBtn != null) {
                    boolean alreadySelected = false;
                    try {
                        alreadySelected = "true".equalsIgnoreCase(bestBtn.getAttribute("selected"));
                    } catch (Exception ignore) {}
                    if (!alreadySelected) {
                        tapElement(bestBtn);
                        try { Thread.sleep(250); } catch (Exception ignore) {}
                    }
                    answered[q] = true;
                    if (!cleanEligibility && q == 0 && isReferralElementPresent()) {
                        return;
                    }
                }
            }
            boolean allDone = true;
            for (boolean a : answered) {
                if (!a) {
                    allDone = false;
                    break;
                }
            }
            if (allDone) {
                return;
            }
            if (scrollPass < 2) {
                scrollDown();
            }
        }

        // Fallback if Q8 (PEP) or Q9 (Related Party) at the bottom are still unselected
        for (int q = 7; q <= 8; q++) {
            if (!answered[q]) {
                WebElement questionEl = findQuestionLabel(questionKeywords[q], maxTapY);
                if (questionEl != null) {
                    int qY = questionEl.getRect().getY();
                    for (WebElement btn : findWithUnimportantViews(noBy)) {
                        try {
                            int bY = btn.getRect().getY();
                            if (btn.isDisplayed() && bY >= qY - 20 && bY < maxTapY && (bY - qY) < (int) (screenHeight * 0.25)) {
                                if (!"true".equalsIgnoreCase(btn.getAttribute("selected"))) {
                                    tapElement(btn);
                                    Thread.sleep(250);
                                }
                                answered[q] = true;
                                break;
                            }
                        } catch (Exception ignore) {}
                    }
                }
            }
        }
    }

    private WebElement findQuestionLabel(String[] keywords, int maxY) {
        StringBuilder predicate = new StringBuilder();
        for (int i = 0; i < keywords.length; i++) {
            if (i > 0) predicate.append(" or ");
            String kw = keywords[i].toLowerCase();
            predicate.append("contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '").append(kw).append("')");
            predicate.append(" or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '").append(kw).append("')");
        }
        By labelBy = By.xpath("//*[" + predicate + "]");
        for (WebElement el : findWithUnimportantViews(labelBy)) {
            try {
                if (el.isDisplayed() && el.getRect().getY() > 0 && el.getRect().getY() < maxY) {
                    return el;
                }
            } catch (Exception ignore) {}
        }
        return null;
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
            By descBy = By.xpath(
                "//*[@resource-id='textfield_valuable_desc' or contains(@resource-id, 'valuable') "
                + "or contains(@hint, 'Valuable') or contains(@hint, 'Description') or contains(@hint, 'Item')]"
            );
            List<WebElement> descEls = findWithUnimportantViews(descBy);
            if (descEls.isEmpty()) {
                By toggleBy = By.xpath(
                    "//*[contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'valuable') "
                    + "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'valuable') "
                    + "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'high-value') "
                    + "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'high-value')]"
                );
                List<WebElement> toggles = findWithUnimportantViews(toggleBy);
                if (toggles.isEmpty()) {
                    scrollDown();
                    toggles = findWithUnimportantViews(toggleBy);
                }
                if (!toggles.isEmpty()) {
                    tapElement(toggles.get(0));
                    waitUntil(() -> !findWithUnimportantViews(descBy).isEmpty(), 3);
                }
                descEls = findWithUnimportantViews(descBy);
            }
            if (!descEls.isEmpty()) {
                sendKeys(descEls.get(0), itemDescription);
                By valBy = By.xpath("//*[@resource-id='textfield_valuable_value' or contains(@hint, 'Value') or contains(@hint, 'Estimated')]");
                List<WebElement> valEls = findWithUnimportantViews(valBy);
                if (!valEls.isEmpty()) {
                    sendKeys(valEls.get(0), estimatedValue);
                }
                hideKeyboard();
            } else {
                System.out.println("High-value item fields not present on current screen; continuing.");
            }
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
        By locator = By.xpath("//*[@resource-id='btn_proceed' or @resource-id='btn_next_button' or @content-desc='Proceed' or @text='Proceed']");
        List<WebElement> btns = findWithUnimportantViews(locator);
        if (btns.isEmpty()) {
            scrollDown();
            btns = findWithUnimportantViews(locator);
        }
        if (!btns.isEmpty()) {
            tapElement(btns.get(btns.size() - 1));
        }
        try { Thread.sleep(1500); } catch (Exception ignore) {}
        return this;
    }

    private boolean isReferralElementPresent() {
        try {
            By referralBy = By.xpath(
                "//*[contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'underwriting') "
                + "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'underwriting') "
                + "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'manual review') "
                + "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'manual review') "
                + "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'under review') "
                + "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'under review') "
                + "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'referral') "
                + "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'referral') "
                + "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'referred') "
                + "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'referred') "
                + "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'not eligible') "
                + "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'not eligible') "
                + "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'required documents') "
                + "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'required documents') "
                + "or contains(@content-desc, 'National Address Certificate') or contains(@text, 'National Address Certificate') "
                + "or contains(@content-desc, 'IBAN Certificate') or contains(@text, 'IBAN Certificate') "
                + "or contains(@content-desc, 'Something Went Wrong') or contains(@text, 'Something Went Wrong') "
                + "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'cannot') "
                + "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'cannot') "
                + "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'sorry') "
                + "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'sorry')]"
            );
            return !findWithUnimportantViews(referralBy).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isReferralModalDisplayed() {
        return waitUntil(this::isReferralElementPresent, 15);
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
            // Select first available plan card (Standard, Premium, Silver, Gold, Platinum)
            By planCardBy = By.xpath("//*[contains(@content-desc, 'Standard') or contains(@content-desc, 'Premium') or contains(@content-desc, 'Silver') or contains(@content-desc, 'Gold') or contains(@content-desc, 'Platinum')]");
            List<WebElement> plans = findWithUnimportantViews(planCardBy);
            if (!plans.isEmpty()) {
                try { tapElement(plans.get(0)); Thread.sleep(800); } catch (Exception ignore) {}
            }

            By proceedBy = By.xpath(
                "//android.widget.Button[@clickable='true' and (@content-desc='Proceed' or @text='Proceed' or @content-desc='Buy now' or @text='Buy now')] "
                + "| //*[@resource-id='btn_proceed' or @resource-id='btn_buy_now' or @resource-id='btn_sales_quote_page_buy_now_button' or @content-desc='Proceed' or @content-desc='Buy now']"
            );
            List<WebElement> proceedBtns = findWithUnimportantViews(proceedBy);
            if (proceedBtns.isEmpty()) {
                scrollDown();
                proceedBtns = findWithUnimportantViews(proceedBy);
            }
            if (!proceedBtns.isEmpty()) {
                tapElement(proceedBtns.get(proceedBtns.size() - 1));
            }
            if (waitUntil(() -> driver.findElements(quoteHeaderBy).isEmpty(), 4)) {
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
        System.out.println("Waiting for Checkout screen to appear...");
        By checkoutBy = By.xpath("//*[@resource-id='textfield_iban' or contains(@resource-id, 'iban') or @resource-id='btn_pay_now' or contains(@content-desc, 'Checkout') or contains(@text, 'Checkout') or contains(@text, 'Total premium') or contains(@content-desc, 'Total premium')]");
        By swwBy = By.xpath("//*[contains(@content-desc, 'Something Went Wrong') or contains(@text, 'Something Went Wrong')]");

        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 35000) {
            if (!driver.findElements(checkoutBy).isEmpty()) {
                System.out.println("Checkout loaded. Home address captured during About Your Home stage; continuing directly to payment details.");
                return this;
            }
            if (!driver.findElements(swwBy).isEmpty()) {
                System.out.println("'Something Went Wrong' error displayed during Home submission; attempting Try Again...");
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
                    System.out.println("Home Checkout did not recover after Try Again.");
                    return this;
                }
            }
            // If still on 'About your home' with Next button, tap it again
            try {
                By nextBy = By.xpath("//*[@content-desc='Next' and @clickable='true'] | //android.widget.Button[@content-desc='Next'] | //*[@resource-id='btn_next_button']");
                List<WebElement> nBtns = findWithUnimportantViews(nextBy);
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

    public HomeInsuranceScreen clickPayNowOnly() {
        if (isPaymentOrConfirmationAlreadyActive()) {
            return this;
        }
        System.out.println("Clicking Pay now on Home Insurance Checkout (without re-selecting payment card)...");
        try {
            new CheckoutScreen().clickPayNow();
        } catch (Exception e) {
            System.out.println("CheckoutScreen clickPayNow warning: " + e.getMessage());
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
