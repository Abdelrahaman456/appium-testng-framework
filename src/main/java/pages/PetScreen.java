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
 * Page Object for Pet Insurance product in Tree Digital Insurance.
 * Supports:
 * - Landing screen ("Insure now" button)
 * - "About your pet" (Pet Name, 'Meow' for Cat / 'Woof' for Dog, Age in Years & Months, Gender Male/Female, City dropdown, Email, Phone, Privacy checkbox, Next)
 * - Medical / Pre-existing condition declarations
 * - Plan selection (Basic / Standard / Premium)
 * - Checkout & National Address (Building, Region, City, Street, District, Additional Number, Zip, IBAN stripped of 'SA', T&C, Pay now)
 */
public class PetScreen extends BasePage {

    public PetScreen() {
        super();
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(2)), this);
    }

    // =========================================================================
    // 1. LANDING SCREEN
    // =========================================================================
    @AndroidFindBy(xpath = "//*[@resource-id='btn_next_button_product_landing_page_' or @resource-id='btn_insure_now' or @content-desc='Insure now' or @text='Insure now']")
    private WebElement insureNowButton;

    // =========================================================================
    // 2. "ABOUT YOUR PET" SCREEN (CRITICAL DOMAIN RULE 3)
    // =========================================================================
    @AndroidFindBy(xpath = "//*[@resource-id='textfield_your_pet_name' or @resource-id='textfield_pet_name' or contains(@resource-id, 'pet_name') or contains(@hint, 'Name') or contains(@hint, 'name') or contains(@hint, 'Pet')]")
    private WebElement petNameInput;

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_microchip_id' or contains(@resource-id, 'microchip') or contains(@hint, 'Microchip') or contains(@text, 'Microchip')]")
    private WebElement microchipInput;

    @AndroidFindBy(xpath = "//*[@resource-id='btn_woof' or @content-desc='Woof' or @text='Woof' or contains(@content-desc, 'Dog') or contains(@text, 'Dog')]")
    private WebElement dogWoofButton;

    @AndroidFindBy(xpath = "//*[@resource-id='btn_meow' or @content-desc='Meow' or @text='Meow' or contains(@content-desc, 'Cat') or contains(@text, 'Cat')]")
    private WebElement catMeowButton;

    @AndroidFindBy(xpath = "//android.widget.EditText[contains(@hint, 'breed') or contains(@hint, 'Breed')] | //*[@resource-id='dropdown_breed' or contains(@resource-id, 'breed') or contains(@content-desc, 'breed') or contains(@content-desc, 'Breed')]")
    private WebElement breedDropdown;

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_pet_age_years' or @resource-id='textfield_years' or contains(@hint, 'Years') or contains(@content-desc, 'Years') or contains(@text, 'Years')]")
    private WebElement ageYearsInput;

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_pet_age_months' or @resource-id='textfield_months' or contains(@hint, 'Months') or contains(@content-desc, 'Months') or contains(@text, 'Months')]")
    private WebElement ageMonthsInput;

    @AndroidFindBy(xpath = "//*[@resource-id='btn_sales_gender_picker_option_male' or @resource-id='btn_pet_gender_male' or @content-desc='Male' or @text='Male']")
    private WebElement genderMaleButton;

    @AndroidFindBy(xpath = "//*[@resource-id='btn_sales_gender_picker_option_female' or @resource-id='btn_pet_gender_female' or @content-desc='Female' or @text='Female']")
    private WebElement genderFemaleButton;

    @AndroidFindBy(xpath = "//*[@resource-id='dropdown_city' or contains(@content-desc, 'City') or contains(@text, 'City') or contains(@hint, 'City')] | //*[@resource-id='textfield_email']/preceding::android.widget.EditText[@resource-id=''][1]")
    private WebElement cityDropdown;

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_email' or contains(@resource-id, 'email')]")
    private WebElement emailInput;

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_phone_number' or contains(@resource-id, 'phone')]")
    private WebElement phoneInput;

    @AndroidFindBy(xpath = "//*[@resource-id='tgl_checkbox' or contains(@resource-id, 'checkbox') or @class='android.widget.Switch']")
    private WebElement privacyNoticeSwitch;

    @AndroidFindBy(xpath = "//*[@resource-id='btn_next_button' or @content-desc='Next' or @text='Next']")
    private WebElement nextButton;

    @AndroidFindBy(xpath = "//*[contains(@resource-id, 'error') or contains(@content-desc, 'invalid') or contains(@text, 'invalid') or contains(@text, 'Invalid') or contains(@content-desc, 'Age') or contains(@text, 'Age limit')]")
    private WebElement validationErrorMessage;

    // =========================================================================
    // 3. MEDICAL / PRE-EXISTING CONDITION DECLARATIONS
    // =========================================================================
    @AndroidFindBy(xpath = "//*[contains(@content-desc, 'Declaration') or contains(@text, 'Declaration') or contains(@content-desc, 'Medical') or contains(@text, 'Medical')]")
    private WebElement healthDeclarationHeader;

    @AndroidFindBy(xpath = "//*[@text='Yes' or @content-desc='Yes' or contains(@resource-id, 'btn_yes')]")
    private List<WebElement> yesButtons;

    @AndroidFindBy(xpath = "//*[@text='No' or @content-desc='No' or contains(@resource-id, 'btn_no')]")
    private List<WebElement> noButtons;

    @AndroidFindBy(xpath = "//*[@resource-id='btn_proceed' or @resource-id='btn_next_button' or @content-desc='Proceed' or @content-desc='Next' or @text='Proceed' or @text='Next']")
    private WebElement declarationProceedButton;

    @AndroidFindBy(xpath = "//*[contains(@content-desc, 'referral') or contains(@text, 'referral') or contains(@content-desc, 'exclusion') or contains(@text, 'exclusion')]")
    private WebElement underwritingNotice;

    // =========================================================================
    // 4. PLAN SELECTION (BASIC / STANDARD / PREMIUM)
    // =========================================================================
    @AndroidFindBy(xpath = "//*[contains(@content-desc, 'FurBasic') or contains(@text, 'FurBasic') or contains(@content-desc, 'Fur Basic') or contains(@content-desc, 'Basic') or contains(@text, 'Basic') or contains(@resource-id, 'basic') or contains(@content-desc, 'Silver')]")
    private WebElement basicPlanCard;

    @AndroidFindBy(xpath = "//*[contains(@content-desc, 'FurDeluxe') or contains(@text, 'FurDeluxe') or contains(@content-desc, 'Fur Deluxe') or contains(@content-desc, 'Standard') or contains(@text, 'Standard') or contains(@resource-id, 'standard') or contains(@content-desc, 'Gold')]")
    private WebElement standardPlanCard;

    @AndroidFindBy(xpath = "//*[contains(@content-desc, 'FurRoyal') or contains(@text, 'FurRoyal') or contains(@content-desc, 'Fur Royal') or contains(@content-desc, 'Premium') or contains(@text, 'Premium') or contains(@resource-id, 'premium') or contains(@content-desc, 'Platinum')]")
    private WebElement premiumPlanCard;

    @AndroidFindBy(xpath = "//*[@clickable='true' and (@resource-id='btn_buy_now' or @resource-id='btn_sales_quote_page_buy_now_button' or @resource-id='btn_quote_page_motor_buy_now' or @resource-id='btn_proceed' or @content-desc='Buy now' or @text='Buy now' or @content-desc='Proceed' or @text='Proceed')] | //*[@resource-id='btn_buy_now' or @resource-id='btn_sales_quote_page_buy_now_button' or @resource-id='btn_quote_page_motor_buy_now' or @resource-id='btn_proceed' or @content-desc='Buy now' or @text='Buy now' or @content-desc='Proceed' or @text='Proceed']")
    private WebElement buyNowButton;

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

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_iban' or @resource-id='textfield_checkout_page_motor_iban' or contains(@resource-id, 'iban')]")
    private WebElement ibanInput;

    @AndroidFindBy(xpath = "//*[@resource-id='tgl_checkbox' or @resource-id='tgl_checkout_page_motor_checkbox' or @class='android.widget.Switch']")
    private WebElement checkoutTermsCheckbox;

    @AndroidFindBy(xpath = "//*[@resource-id='btn_pay_now' or @resource-id='btn_checkout_page_motor_pay_now' or @content-desc='Pay now' or @text='Pay now']")
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

    public PetScreen clickInsureNow() {
        System.out.println("Waiting for Pet Insurance Landing screen to load...");
        try { Thread.sleep(2000); } catch (Exception ignore) {}
        System.out.println("Clicking 'Insure now' button...");
        try {
            waitForVisibility(insureNowButton);
            tapElement(insureNowButton);
        } catch (Exception e) {
            // dynamic locator used; coordinate fallback removed
        }
        try { Thread.sleep(2000); } catch (Exception ignore) {}
        return this;
    }

    public PetScreen clickInsureNowIfPresent() {
        System.out.println("Checking for Pet Insurance Landing 'Insure now' screen...");
        org.openqa.selenium.By insureNowBy = org.openqa.selenium.By.xpath(
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

    // --- "About Your Pet" Actions ---

    public PetScreen enterPetName(String name) {
        System.out.println("Entering Pet Name: " + name);
        WebDriverWait waitName = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement field = waitName.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//*[@resource-id='textfield_your_pet_name' or @resource-id='textfield_pet_name' or contains(@resource-id, 'pet_name') or contains(@hint, 'Name') or contains(@hint, 'Pet')]")));
        sendKeys(field, name);
        hideKeyboard();
        try { Thread.sleep(500); } catch (Exception ignore) {}
        return this;
    }

    public PetScreen enterMicrochipId(String microchipId) {
        System.out.println("Entering Pet 15-digit Microchip ID: " + microchipId);
        try {
            if (isElementVisible(microchipInput)) {
                sendKeys(microchipInput, microchipId);
            } else {
                WebElement fallback = driver.findElement(By.xpath("//*[contains(@resource-id, 'microchip') or contains(@hint, 'Microchip')]"));
                sendKeys(fallback, microchipId);
            }
            hideKeyboard();
        } catch (Exception ignore) {
            System.out.println("Microchip ID field optional or skipped.");
        }
        return this;
    }

    public boolean isMicrochipErrorDisplayed() {
        try {
            return !driver.findElements(By.xpath("//*[contains(@content-desc, 'microchip') or contains(@text, 'microchip') or contains(@text, '15 digits') or contains(@content-desc, '15 digits')]")).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public PetScreen toggleRoutineCareAddon() {
        System.out.println("Toggling Routine Care / Wellness add-on...");
        try {
            By routineBy = By.xpath("//*[contains(@content-desc, 'Routine') or contains(@text, 'Routine') or contains(@content-desc, 'Wellness') or contains(@text, 'Wellness')]");
            WebElement el = driver.findElement(routineBy);
            tapElement(el);
        } catch (Exception ignore) {}
        return this;
    }

    public PetScreen selectAccidentOnlyCover() {
        System.out.println("Selecting Accident Only Cover...");
        try {
            By accBy = By.xpath("//*[contains(@content-desc, 'Accident Only') or contains(@text, 'Accident Only')]");
            WebElement el = driver.findElement(accBy);
            tapElement(el);
        } catch (Exception ignore) {}
        return this;
    }

    public PetScreen selectAccidentAndIllnessCover() {
        System.out.println("Selecting Accident & Illness Cover...");
        try {
            By accIllBy = By.xpath("//*[contains(@content-desc, 'Accident & Illness') or contains(@text, 'Accident & Illness') or contains(@content-desc, 'Comprehensive')]");
            WebElement el = driver.findElement(accIllBy);
            tapElement(el);
        } catch (Exception ignore) {}
        return this;
    }

    public PetScreen selectPetType(String type) {
        System.out.println("Selecting Pet Type: " + type + " ('Woof' for Dog / 'Meow' for Cat)...");
        By breedBy = By.xpath("//*[@resource-id='btn_woof']/following::android.widget.EditText[@resource-id='' and following::*[@resource-id='btn_sales_gender_picker_option_male']]");
        By targetBy = ("cat".equalsIgnoreCase(type) || "meow".equalsIgnoreCase(type))
                ? By.xpath("//*[@resource-id='btn_meow' or @content-desc='Meow' or @text='Meow']")
                : By.xpath("//*[@resource-id='btn_woof' or @content-desc='Woof' or @text='Woof']");
        for (int attempt = 1; attempt <= 2; attempt++) {
            if (!driver.findElements(breedBy).isEmpty()) {
                break;
            }
            try {
                List<WebElement> btns = driver.findElements(targetBy);
                WebElement btn = !btns.isEmpty() ? btns.get(0)
                        : (("cat".equalsIgnoreCase(type) || "meow".equalsIgnoreCase(type)) ? catMeowButton : dogWoofButton);
                if (attempt == 1) {
                    tapElement(btn);
                } else {
                    org.openqa.selenium.Rectangle r = btn.getRect();
                    int cx = r.getX() + r.getWidth() / 2;
                    int cy = r.getY() + r.getHeight() / 2;
                    org.openqa.selenium.interactions.PointerInput finger = new org.openqa.selenium.interactions.PointerInput(
                            org.openqa.selenium.interactions.PointerInput.Kind.TOUCH, "finger");
                    org.openqa.selenium.interactions.Sequence seq = new org.openqa.selenium.interactions.Sequence(finger, 1);
                    seq.addAction(finger.createPointerMove(Duration.ZERO, org.openqa.selenium.interactions.PointerInput.Origin.viewport(), cx, cy));
                    seq.addAction(finger.createPointerDown(org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT.asArg()));
                    seq.addAction(new org.openqa.selenium.interactions.Pause(finger, Duration.ofMillis(120)));
                    seq.addAction(finger.createPointerUp(org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT.asArg()));
                    driver.perform(java.util.Collections.singletonList(seq));
                }
            } catch (Exception ignore) {}
            try { Thread.sleep(1500); } catch (Exception ignore) {}
            if (!driver.findElements(breedBy).isEmpty()) {
                break;
            }
        }
        return this;
    }

    public PetScreen selectPetBreedIfPresent() {
        System.out.println("Selecting Pet Breed from dropdown...");
        try {
            By breedBy = By.xpath("//android.widget.EditText[contains(@hint, 'breed') or contains(@hint, 'Breed') or contains(@text, 'breed')] | //*[@resource-id='dropdown_breed' or contains(@resource-id, 'breed') or contains(@content-desc, 'breed') or contains(@content-desc, 'Breed')]");
            WebElement trigger = null;
            if (isElementVisible(breedDropdown)) {
                trigger = breedDropdown;
            } else {
                List<WebElement> els = driver.findElements(breedBy);
                if (!els.isEmpty()) trigger = els.get(0);
            }

            if (trigger != null) {
                By anyOptBy = By.xpath("//android.view.View[@clickable='true' and string-length(@content-desc) > 2 and not(contains(@content-desc, 'Privacy')) and not(contains(@content-desc, 'About')) and not(contains(@content-desc, 'Woof')) and not(contains(@content-desc, 'Meow'))]");
                for (int attempt = 1; attempt <= 3; attempt++) {
                    try {
                        trigger.click();
                    } catch (Exception e) {
                        tapElement(trigger);
                    }
                    Thread.sleep(1200);
                    List<WebElement> options = driver.findElements(anyOptBy);
                    if (!options.isEmpty()) {
                        System.out.println("Selecting first breed option: " + options.get(0).getAttribute("content-desc"));
                        try {
                            options.get(0).click();
                        } catch (Exception e) {
                            tapElement(options.get(0));
                        }
                        Thread.sleep(800);
                        hideKeyboard();
                        Thread.sleep(500);
                        break;
                    }
                    // Check if search edittext opened
                    List<WebElement> searchFields = driver.findElements(By.xpath("//android.widget.EditText[contains(@hint, 'Search') or contains(@text, 'Search') or contains(@hint, 'search')]"));
                    if (!searchFields.isEmpty()) {
                        searchFields.get(0).sendKeys("Mix");
                        Thread.sleep(1000);
                        List<WebElement> filtered = driver.findElements(anyOptBy);
                        if (!filtered.isEmpty()) {
                            filtered.get(0).click();
                            Thread.sleep(800);
                            hideKeyboard();
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Breed selection notice: " + e.getMessage());
        }
        return this;
    }

    private void selectAgeDropdown(int boxIndex, String unitKeyword, int value, WebElement fallbackElement) throws InterruptedException {
        By hintBoxBy = By.xpath(
            "//*[contains(translate(@hint, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + unitKeyword + "') " +
            "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + unitKeyword + "')]");
        By ageBoxBy = By.xpath("//*[contains(@content-desc, 'age')]/following::android.view.View[@resource-id='']");
        By popupOptionBy = By.xpath("//android.view.View[@clickable='true' and contains(@content-desc, '" + unitKeyword + "')]");
        boolean opened = false;
        for (int attempt = 1; attempt <= 2; attempt++) {
            List<WebElement> hintBoxes = driver.findElements(hintBoxBy);
            List<WebElement> ageBoxes = hintBoxes.isEmpty() ? driver.findElements(ageBoxBy) : java.util.Collections.emptyList();
            WebElement trigger = !hintBoxes.isEmpty() ? hintBoxes.get(0)
                    : ((ageBoxes.size() > boxIndex) ? ageBoxes.get(boxIndex)
                    : (isElementVisible(fallbackElement) ? fallbackElement : null));
            if (trigger == null) {
                return;
            }
            org.openqa.selenium.Rectangle rect = trigger.getRect();
            int tapX = rect.getX() + (rect.getWidth() / 3);
            int tapY = rect.getY() + (rect.getHeight() / 2);
            tapCoordinates(tapX, tapY);
            Thread.sleep(800);
            if (!driver.findElements(popupOptionBy).isEmpty()) {
                opened = true;
                break;
            }
        }
        if (opened) {
            selectAgeOption(value);
            Thread.sleep(500);
        }
    }

    public PetScreen enterPetAge(int years, int months) {
        System.out.println("Selecting Pet Age from dropdowns: " + years + " Years, " + months + " Months...");
        try {
            hideKeyboard();
            Thread.sleep(500);
            selectAgeDropdown(0, "year", years, ageYearsInput);
            selectAgeDropdown(1, "month", months, ageMonthsInput);
        } catch (Exception e) {
            System.out.println("Age dropdown notice: " + e.getMessage());
        }
        return this;
    }

    private void selectAgeOption(int value) {
        By optBy = By.xpath("//android.view.View[@clickable='true' and (starts-with(@content-desc, '" + value + " ') or @content-desc='" + value + "' or starts-with(@text, '" + value + " '))]");
        for (int i = 0; i < 4; i++) {
            List<WebElement> opts = driver.findElements(optBy);
            if (!opts.isEmpty()) {
                tapElement(opts.get(0));
                return;
            }
            List<WebElement> anyOpts = driver.findElements(By.xpath(
                    "//android.view.View[@clickable='true' and (contains(@content-desc, 'year') or contains(@content-desc, 'month'))]"));
            if (!anyOpts.isEmpty()) {
                org.openqa.selenium.Rectangle first = anyOpts.get(0).getRect();
                org.openqa.selenium.Rectangle last = anyOpts.get(anyOpts.size() - 1).getRect();
                int cx = first.getX() + first.getWidth() / 2;
                scrollWheel(cx, last.getY() + last.getHeight() / 2, first.getY());
                try { Thread.sleep(500); } catch (Exception ignore) {}
            } else {
                break;
            }
        }
    }

    public PetScreen selectPetGender(String gender) {
        System.out.println("Selecting Pet Gender: " + gender);
        if ("female".equalsIgnoreCase(gender)) {
            try {
                if (isElementVisible(genderFemaleButton)) tapElement(genderFemaleButton);
                else {
                    WebElement btn = driver.findElement(By.xpath("//*[@content-desc='Female' or @text='Female']"));
                    tapElement(btn);
                }
            } catch (Exception e) {
                // dynamic locator used; coordinate fallback removed
            }
        } else {
            try {
                if (isElementVisible(genderMaleButton)) tapElement(genderMaleButton);
                else {
                    WebElement btn = driver.findElement(By.xpath("//*[@content-desc='Male' or @text='Male']"));
                    tapElement(btn);
                }
            } catch (Exception e) {
                // dynamic locator used; coordinate fallback removed
            }
        }
        try { Thread.sleep(800); } catch (Exception ignore) {}
        return this;
    }

    public PetScreen selectCity(String cityName) {
        System.out.println("Selecting City: " + cityName);
        try {
            if (isElementVisible(cityDropdown)) {
                By anyCityBy = By.xpath(
                        "//android.view.View[@clickable='true' and string-length(@content-desc) > 2 and not(contains(@content-desc, 'Privacy')) and not(contains(@content-desc, 'About'))]");
                for (int openAttempt = 1; openAttempt <= 2; openAttempt++) {
                    tapElement(cityDropdown);
                    Thread.sleep(1000);
                    if (!driver.findElements(anyCityBy).isEmpty()) {
                        break;
                    }
                }
                boolean selected = false;
                for (int i = 0; i < 2; i++) {
                    List<WebElement> cityOpts = driver.findElements(By.xpath(
                            "//android.view.View[@clickable='true' and (contains(@content-desc, '" + cityName + "') or contains(@text, '" + cityName + "'))]"));
                    if (!cityOpts.isEmpty()) {
                        tapElement(cityOpts.get(0));
                        selected = true;
                        break;
                    }
                    List<WebElement> anyCities = driver.findElements(anyCityBy);
                    if (!anyCities.isEmpty()) {
                        org.openqa.selenium.Rectangle first = anyCities.get(0).getRect();
                        org.openqa.selenium.Rectangle last = anyCities.get(anyCities.size() - 1).getRect();
                        int cx = first.getX() + first.getWidth() / 2;
                        scrollWheel(cx, last.getY() + last.getHeight() / 2, first.getY());
                        Thread.sleep(400);
                    } else {
                        break;
                    }
                }
                if (!selected) {
                    List<WebElement> anyCities = driver.findElements(anyCityBy);
                    if (!anyCities.isEmpty()) {
                        tapElement(anyCities.get(0));
                    }
                }
                Thread.sleep(500);
                hideKeyboard();
            }
        } catch (Exception ignore) {}
        return this;
    }

    public PetScreen enterContactInfo(String email, String phone) {
        System.out.println("Entering Contact Info: Email=" + email + ", Phone=" + phone);
        try {
            if (isElementVisible(emailInput)) sendKeys(emailInput, email);
            if (isElementVisible(phoneInput)) {
                sendKeys(phoneInput, phone);
                try {
                    String val = phoneInput.getText();
                    if (val == null || val.trim().isEmpty()) {
                        new ProcessBuilder(ADB_PATH, "-s", "emulator-5554", "shell", "input", "text", phone).start().waitFor();
                    }
                } catch (Exception ignore) {}
            }
        } catch (Exception e) {
            try {
                WebElement eField = driver.findElement(By.xpath("//*[contains(@resource-id, 'email')]"));
                sendKeys(eField, email);
                WebElement pField = driver.findElement(By.xpath("//*[contains(@resource-id, 'phone')]"));
                sendKeys(pField, phone);
            } catch (Exception ex) {}
        }
        hideKeyboard();
        return this;
    }

    public PetScreen acceptPrivacyNotice() {
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

    public PetScreen clickNext() {
        System.out.println("Clicking Next button on 'About your pet'...");
        hideKeyboard();
        for (int retry = 0; retry < 3; retry++) {
            try {
                By nextBtnBy = By.xpath("//android.widget.Button[(@content-desc='Next' or @text='Next') and @clickable='true'] | //*[@resource-id='btn_next_button' and @clickable='true']");
                List<WebElement> btns = driver.findElements(nextBtnBy);
                if (!btns.isEmpty()) {
                    tapElement(btns.get(0));
                } else {
                    tapCoordinates(540, 2261);
                }
            } catch (Exception e) {
                tapCoordinates(540, 2261);
            }
            try {
                Thread.sleep(2500);
                List<WebElement> expandingBtns = driver.findElements(
                        By.xpath("//*[contains(@content-desc, 'Got it') or contains(@text, 'Got it') or contains(@content-desc, 'continue')]"));
                if (!expandingBtns.isEmpty() && expandingBtns.get(0).isDisplayed()) {
                    System.out.println("Dismissing 'We are expanding!' bottom sheet modal...");
                    tapElement(expandingBtns.get(0));
                    Thread.sleep(2500);
                }
                OtpModal otpModal = new OtpModal();
                if (otpModal.isModalDisplayed()) {
                    System.out.println("OTP modal detected after Pet 'Next'; verifying OTP...");
                    otpModal.enterOtp("1234");
                }
            } catch (Exception ignore) {}

            if (driver.findElements(By.xpath("//*[@resource-id='textfield_your_pet_name' or @content-desc='About your pet']")).isEmpty()) {
                System.out.println("Transitioned successfully out of 'About your pet'!");
                break;
            }
            System.out.println("Still on 'About your pet'; re-toggling Privacy notice switch...");
            acceptPrivacyNotice();
        }
        return this;
    }

    /**
     * Fills the complete "About your pet" screen according to CRITICAL DOMAIN RULE 3.
     */
    public PetScreen fillAboutYourPet(String name, String type, int years, int months, String gender, String city, String email, String phone) {
        System.out.println("Filling 'About your pet' screen: Name=" + name + ", Type=" + type + ", Age=" + years + "y " + months + "m");
        enterPetName(name);
        selectPetType(type);
        selectPetBreedIfPresent();
        enterPetAge(years, months);
        selectPetGender(gender);
        selectCity(city);
        enterContactInfo(email, phone);
        try {
            List<WebElement> nameFields = driver.findElements(By.xpath("//*[@resource-id='textfield_your_pet_name' or @resource-id='textfield_pet_name']"));
            if (!nameFields.isEmpty()) {
                String currentName = nameFields.get(0).getText();
                if (currentName == null || currentName.trim().isEmpty()) {
                    System.out.println("Pet name field was empty after contact entry; re-typing: " + name);
                    tapElement(nameFields.get(0));
                    Thread.sleep(400);
                    nameFields.get(0).sendKeys(name);
                    Thread.sleep(400);
                    hideKeyboard();
                }
            }
        } catch (Exception ignore) {}
        acceptPrivacyNotice();
        clickNext();
        return this;
    }

    public boolean isValidationErrorDisplayed() {
        try {
            WebDriverWait waitErr = new WebDriverWait(driver, Duration.ofSeconds(5));
            return waitErr.until(d -> {
                if (isElementVisible(validationErrorMessage)) return true;
                return !driver.findElements(By.xpath("//*[contains(@resource-id, 'error') or contains(@text, 'invalid') or contains(@content-desc, 'invalid') or contains(@text, 'Age') or contains(@content-desc, 'Age')]")).isEmpty();
            });
        } catch (Exception e) {
            return false;
        }
    }

    // --- Medical / Health Declarations ---

    public PetScreen answerAllDeclarationQuestions(boolean answerYes) {
        System.out.println("Answering Pet Declarations (Answer=" + (answerYes ? "Yes" : "No") + ")...");
        By quoteCardsBy = By.xpath("//*[contains(@content-desc, 'FurBasic') or contains(@content-desc, 'FurDeluxe') or contains(@content-desc, 'FurRoyal') or contains(@content-desc, 'Fur basic') or contains(@content-desc, 'Fur deluxe')]");
        By petMoreBy = By.xpath("//*[contains(@content-desc, 'A bit more about') or contains(@text, 'A bit more about')]");
        By yesNoBy = By.xpath("//*[@resource-id='btn_no_button' or @resource-id='btn_yes_button' or @text='No' or @content-desc='No' or @text='Yes' or @content-desc='Yes']");

        if (!answerYes) {
            if (!driver.findElements(quoteCardsBy).isEmpty() && driver.findElements(petMoreBy).isEmpty()) {
                System.out.println("On Pet Quote screen; deferring 'A bit more about' declarations until after plan selection Proceed.");
                return this;
            }
            try {
                new WebDriverWait(driver, Duration.ofSeconds(5))
                        .until(ExpectedConditions.presenceOfElementLocated(yesNoBy));
            } catch (Exception ignore) {
                if (driver.findElements(yesNoBy).isEmpty() || !driver.findElements(quoteCardsBy).isEmpty()) {
                    System.out.println("No pre-quote Yes/No declaration questions present; deferring to after Quote.");
                    return this;
                }
            }
            answerMemberDeclarations(false);
            return this;
        }

        try {
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//*[contains(@content-desc, 'FurBasic') or contains(@content-desc, 'FurDeluxe') or contains(@content-desc, 'FurRoyal') or contains(@content-desc, 'A bit more about') or @resource-id='btn_yes_button' or @content-desc='Yes']")));
        } catch (Exception ignore) {}

        if (!driver.findElements(quoteCardsBy).isEmpty() && driver.findElements(petMoreBy).isEmpty()) {
            System.out.println("Flow 3 Underwriting: Currently on Pet Quote screen; clicking Proceed to reach 'A bit more about'...");
            try {
                scrollDown();
                Thread.sleep(600);
                answerMemberDeclarations(false);
                tapPreferredButton(
                        By.xpath("//*[@clickable='true' and (@resource-id='btn_proceed' or @content-desc='Proceed' or @text='Proceed' or @content-desc='Buy now' or @text='Buy now')]"),
                        buyNowButton);
                new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(ExpectedConditions.presenceOfElementLocated(yesNoBy));
            } catch (Exception ignore) {}
        }

        List<WebElement> yesBtns = driver.findElements(By.xpath("//*[@clickable='true' and (@resource-id='btn_yes_button' or @content-desc='Yes' or @text='Yes')]"));
        if (yesBtns.isEmpty()) {
            yesBtns = driver.findElements(By.xpath("//*[@resource-id='btn_yes_button' or @content-desc='Yes' or @text='Yes']"));
        }
        for (WebElement btn : yesBtns) {
            try {
                if (btn.isDisplayed()) {
                    tapElement(btn);
                    Thread.sleep(300);
                }
            } catch (Exception ignore) {}
        }
        return this;
    }

    public PetScreen clickDeclarationProceed() {
        System.out.println("Checking Pet Medical Declaration before clicking Proceed...");
        hideKeyboard();
        try {
            By quoteCardsBy = By.xpath("//*[contains(@content-desc, 'FurBasic') or contains(@content-desc, 'FurDeluxe') or contains(@content-desc, 'FurRoyal') or contains(@content-desc, 'Fur basic') or contains(@content-desc, 'Fur deluxe')]");
            By petMoreBy = By.xpath("//*[contains(@content-desc, 'A bit more about') or contains(@text, 'A bit more about')]");
            if (!driver.findElements(quoteCardsBy).isEmpty() && driver.findElements(petMoreBy).isEmpty()) {
                System.out.println("On Pet Quote screen; skipping pre-quote declaration Proceed.");
                return this;
            }
            List<WebElement> yesNoOptions = driver.findElements(
                    By.xpath("//*[@resource-id='btn_no_button' or @resource-id='btn_yes_button' or @text='No' or @content-desc='No' or @text='Yes' or @content-desc='Yes']"));
            if (!yesNoOptions.isEmpty()) {
                tapPreferredButton(
                        By.xpath("//*[@clickable='true' and (@content-desc='Proceed' or @text='Proceed' or @content-desc='Next' or @text='Next')]"),
                        declarationProceedButton);
                try { Thread.sleep(2000); } catch (Exception ignore) {}
            } else {
                System.out.println("No declaration questions present; skipping Proceed click.");
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
                return !driver.findElements(By.xpath("//*[contains(@content-desc, 'referral') or contains(@text, 'referral') or contains(@content-desc, 'exclusion') or contains(@text, 'exclusion') or contains(@content-desc, 'not eligible') or contains(@text, 'not eligible') or contains(@content-desc, 'underwriting') or contains(@text, 'underwriting') or contains(@content-desc, 'Something Went Wrong')]")).isEmpty();
            });
        } catch (Exception e) {
            return false;
        }
    }

    // --- Plan Selection Actions (Basic / Standard / Premium) ---

    public PetScreen waitForPlanSelectionScreen() {
        System.out.println("Waiting up to 45s for Pet Insurance Plan selection screen to load...");
        WebDriverWait waitPlans = new WebDriverWait(driver, Duration.ofSeconds(45));
        waitPlans.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[contains(@content-desc, 'quote') or @resource-id='btn_buy_now' or @resource-id='btn_sales_quote_page_buy_now_button' or @resource-id='btn_quote_page_motor_buy_now' or @content-desc='Buy now' or @text='Buy now' or contains(@content-desc, 'FurBasic') or contains(@content-desc, 'FurDeluxe') or contains(@content-desc, 'FurRoyal') or contains(@content-desc, 'Fur basic') or contains(@content-desc, 'Fur deluxe') or contains(@content-desc, 'Basic') or contains(@content-desc, 'Standard') or contains(@content-desc, 'Premium')]")));
        System.out.println("Pet Plan selection screen loaded successfully!");
        return this;
    }

    public PetScreen selectBasicPlan() {
        System.out.println("Selecting Basic Plan tier (FurBasic)...");
        try {
            if (isElementVisible(basicPlanCard)) {
                tapElement(basicPlanCard);
            } else {
                List<WebElement> cards = driver.findElements(By.xpath("//*[contains(@content-desc, 'FurBasic') or contains(@text, 'FurBasic') or contains(@content-desc, 'Fur Basic') or contains(@content-desc, 'Basic') or contains(@text, 'Basic') or contains(@content-desc, 'Silver')]"));
                if (!cards.isEmpty()) tapElement(cards.get(0));
            }
        } catch (Exception ignore) {}
        try { Thread.sleep(1000); } catch (Exception ignore) {}
        return this;
    }

    public PetScreen selectStandardPlan() {
        System.out.println("Selecting Standard Plan tier (FurDeluxe)...");
        try {
            if (isElementVisible(standardPlanCard)) {
                tapElement(standardPlanCard);
            } else {
                List<WebElement> cards = driver.findElements(By.xpath("//*[contains(@content-desc, 'FurDeluxe') or contains(@text, 'FurDeluxe') or contains(@content-desc, 'Fur Deluxe') or contains(@content-desc, 'Standard') or contains(@text, 'Standard') or contains(@content-desc, 'Gold')]"));
                if (!cards.isEmpty()) tapElement(cards.get(0));
            }
        } catch (Exception ignore) {}
        try { Thread.sleep(1000); } catch (Exception ignore) {}
        return this;
    }

    public PetScreen selectPremiumPlan() {
        System.out.println("Selecting Premium Plan tier (FurRoyal)...");
        try {
            if (isElementVisible(premiumPlanCard)) {
                tapElement(premiumPlanCard);
            } else {
                List<WebElement> cards = driver.findElements(By.xpath("//*[contains(@content-desc, 'FurRoyal') or contains(@text, 'FurRoyal') or contains(@content-desc, 'Fur Royal') or contains(@content-desc, 'Premium') or contains(@text, 'Premium') or contains(@content-desc, 'Platinum')]"));
                if (!cards.isEmpty()) tapElement(cards.get(0));
            }
        } catch (Exception ignore) {}
        try { Thread.sleep(1000); } catch (Exception ignore) {}
        return this;
    }

    public PetScreen clickBuyNow() {
        System.out.println("Clicking Proceed / Buy now on Pet Plan selection screen...");
        By ctaBy = By.xpath("//*[@clickable='true' and (@resource-id='btn_buy_now' or @resource-id='btn_sales_quote_page_buy_now_button' or @resource-id='btn_proceed' or @content-desc='Buy now' or @text='Buy now' or @content-desc='Proceed' or @text='Proceed')]");
        boolean quoteClicked = false;
        for (int i = 0; i < 3; i++) {
            try {
                scrollDown();
                Thread.sleep(600);
                if (!driver.findElements(By.xpath("//*[@resource-id='btn_no_button' or @content-desc='No' or @text='No']")).isEmpty()) {
                    answerMemberDeclarations(false);
                }
                if (!driver.findElements(ctaBy).isEmpty() || isElementVisible(buyNowButton)) {
                    tapPreferredButton(ctaBy, buyNowButton);
                    quoteClicked = true;
                    Thread.sleep(2500);
                    break;
                }
            } catch (Exception ignore) {}
        }
        if (!quoteClicked) {
            try {
                tapPreferredButton(ctaBy, buyNowButton);
                Thread.sleep(2500);
            } catch (Exception ignore) {}
        }

        By checkoutBy = By.xpath("//*[@resource-id='textfield_building_number' or @resource-id='textfield_iban' or @resource-id='btn_pay_now' or @content-desc='Pay now' or @text='Pay now']");
        By proceedNextBy = By.xpath("//*[@clickable='true' and (@content-desc='Proceed' or @text='Proceed' or @content-desc='Next' or @text='Next')]");
        for (int step = 0; step < 5; step++) {
            if (!driver.findElements(checkoutBy).isEmpty()) {
                System.out.println("Pet Checkout screen reached after Quote -> Declarations/Owner steps.");
                break;
            }
            try {
                OtpModal otpModal = new OtpModal();
                if (otpModal.isModalDisplayed()) {
                    System.out.println("OTP modal detected after Pet declarations/owner; verifying OTP...");
                    otpModal.enterOtp("1234");
                    Thread.sleep(2500);
                    continue;
                }
            } catch (Exception ignore) {}

            List<WebElement> ownerIdFields = driver.findElements(By.xpath("//*[@resource-id='textfield_national_id' or contains(@resource-id, 'national_id')]"));
            if (!ownerIdFields.isEmpty()) {
                System.out.println("Pet Owner Details screen detected; filling National ID & DOB...");
                try {
                    String currentVal = ownerIdFields.get(0).getText();
                    if (currentVal == null || !currentVal.contains("1313424273")) {
                        sendKeys(ownerIdFields.get(0), "1313424273");
                    }
                    hideKeyboard();
                    List<WebElement> dobFields = driver.findElements(By.xpath("//*[@resource-id='textfield_month_year_of_birth_txt' or @resource-id='textfield_year_monthof_birth' or contains(@resource-id, 'birth') or contains(@resource-id, 'dob') or @hint='Month/Year of birth' or contains(@hint, 'birth')]"));
                    if (!dobFields.isEmpty()) {
                        String dobText = dobFields.get(0).getText();
                        if (dobText == null || !dobText.contains("1998")) {
                            scrollDobWheelAndConfirm(dobFields.get(0));
                        }
                    }
                    // Tap Next button on Owner Details (no privacy switch on this screen)
                    List<WebElement> nextBtns = driver.findElements(By.xpath("//android.widget.Button[(@content-desc='Next' or @text='Next' or @content-desc='Proceed' or @text='Proceed') and @clickable='true'] | //*[@resource-id='btn_next_button' and @clickable='true']"));
                    if (!nextBtns.isEmpty()) {
                        tapElement(nextBtns.get(0));
                    } else {
                        tapCoordinates(540, 2261);
                    }
                    Thread.sleep(2500);
                    OtpModal otpModal = new OtpModal();
                    if (otpModal.isModalDisplayed()) {
                        System.out.println("OTP modal detected after Pet owner Next; entering OTP...");
                        otpModal.enterOtp("1234");
                        Thread.sleep(2500);
                    }
                } catch (Exception ignore) {}
                continue;
            }

            List<WebElement> petMoreOrNo = driver.findElements(By.xpath(
                    "//*[contains(@content-desc, 'A bit more about') or contains(@text, 'A bit more about') or @resource-id='btn_no_button' or @content-desc='No' or @text='No']"));
            if (!petMoreOrNo.isEmpty()) {
                System.out.println("Handling post-Quote 'A bit more about' Pet declaration screen...");
                try {
                    tapAllNoButtons();
                    scrollDown();
                    Thread.sleep(500);
                    tapAllNoButtons();
                    tapPreferredButton(proceedNextBy, declarationProceedButton);
                } catch (Exception ignore) {}
                try { Thread.sleep(2500); } catch (Exception ignore) {}
                continue;
            }

            try { Thread.sleep(2500); } catch (Exception ignore) {}
        }
        return this;
    }

    private void tapAllNoButtons() {
        List<WebElement> noBtns = driver.findElements(By.xpath(
                "//*[@clickable='true' and (@resource-id='btn_no_button' or @content-desc='No' or @text='No')]"));
        if (noBtns.isEmpty()) {
            noBtns = driver.findElements(By.xpath("//*[@resource-id='btn_no_button' or @content-desc='No' or @text='No']"));
        }
        for (WebElement btn : noBtns) {
            try {
                if (btn.isDisplayed()) {
                    tapElement(btn);
                    Thread.sleep(300);
                }
            } catch (Exception ignore) {}
        }
    }

    // --- Checkout & National Address (CRITICAL IBAN RULE) ---

    public boolean isCheckoutLoaded() {
        try {
            WebDriverWait waitCheckout = new WebDriverWait(driver, Duration.ofSeconds(30));
            waitCheckout.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[@resource-id='textfield_building_number' or @resource-id='textfield_iban' or contains(@resource-id, 'iban')]")));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public PetScreen fillNationalAddress(String bldg, String region, String city, String street, String dist, String addNo, String zip) {
        System.out.println("Waiting for Checkout screen...");
        WebDriverWait waitCheckout = new WebDriverWait(driver, Duration.ofSeconds(30));
        waitCheckout.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[@resource-id='textfield_building_number' or @resource-id='textfield_iban' or @resource-id='btn_pay_now']")));

        if (driver.findElements(By.xpath("//*[@resource-id='textfield_building_number']")).isEmpty()) {
            System.out.println("National Address fields not present on Pet Checkout; skipping.");
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
    public PetScreen fillIbanAndAcceptTerms(String iban) {
        System.out.println("Entering IBAN into Checkout: " + iban);
        try {
            scrollDown();
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
        return this;
    }

    public PetScreen clickCheckoutPayNow() {
        if (isPaymentOrConfirmationAlreadyActive()) {
            return this;
        }
        System.out.println("Clicking Pay now on Checkout screen...");
        try {
            if (isElementVisible(checkoutPayNowButton)) {
                tapElement(checkoutPayNowButton);
            } else {
                List<WebElement> payBtns = driver.findElements(By.xpath("//*[@resource-id='btn_pay_now' or @content-desc='Pay now' or @text='Pay now']"));
                if (!payBtns.isEmpty()) {
                    tapElement(payBtns.get(0));
                }
            }
        } catch (Exception e) {
            System.out.println("Pay now click: " + e.getMessage());
        }
        try { Thread.sleep(2000); } catch (Exception ignore) {}
        return this;
    }
}
