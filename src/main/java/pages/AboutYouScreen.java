package pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;

public class AboutYouScreen extends BasePage {

    public AboutYouScreen() {
        super();
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(10)), this);
    }

    @AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='textfield_about_you_page_motor_sequence_number']")
    private WebElement sequenceNumberField;

    @AndroidFindBy(xpath = "//android.widget.Button[@content-desc=\"Ownership Transfer\"]")
    private WebElement ownershipTransferTab;

    @AndroidFindBy(xpath = "//*[@resource-id='tgl_about_you_page_motor_newInsurance' or @resource-id='tgl_new_insurance' or @content-desc='New Insurance' or @text='New Insurance']")
    private WebElement newInsuranceTab;

    @AndroidFindBy(xpath = "//*[@resource-id='radio_btn_about_you_page_motor_custom_card' or @resource-id='radio_btn_custom_card' or @content-desc='Custom Card' or @text='Custom Card']")
    private WebElement customCardRadio;

    @AndroidFindBy(xpath = "//*[@resource-id='radio_btn_about_you_page_motor_sequence_number' or @resource-id='radio_btn_sequence_number' or @content-desc='Sequence Number' or @text='Sequence Number']")
    private WebElement sequenceNumberRadio;

    @AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='textfield_about_you_page_motor_custom_card']")
    private WebElement customCardField;

    @AndroidFindBy(xpath = "//android.widget.Button[@content-desc=\"Car model year\"]")
    private WebElement carModelYearDropdown;

    @AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='textfield_about_you_page_motor_seller_national_id']")
    private WebElement sellerIdField;

    @AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='textfield_about_you_page_motor_national_id']")
    private WebElement nationalIdField;

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_about_you_page_motor_dob' or @hint='Month/Year of birth' or contains(@hint, 'birth') or @content-desc='Month/Year of birth']")
    private WebElement dobField;

    @AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='textfield_about_you_page_motor_phone_number']")
    private WebElement phoneNumberField;

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_about_you_page_motor_start_date']")
    private WebElement policyStartDateField;

    // Use [last()] to guarantee we click the active Confirm button, 
    // bypassing any hidden 'ghost' Confirm buttons left behind by the DOB picker!
    @AndroidFindBy(xpath = "(//android.widget.Button[@content-desc=\"Confirm\"])[last()]")
    private WebElement confirmButton;

    @AndroidFindBy(xpath = "//*[@resource-id='tgl_about_you_page_motor_checkbox' or @class='android.widget.Switch']")
    private WebElement privacyCheckbox;

    @AndroidFindBy(xpath = "//*[@resource-id='btn_about_you_page_motor_next_button' or @resource-id='btn_next_button' or @content-desc='Next' or @text='Next']")
    private WebElement nextButton;

    public void enterSequenceNumber(String sequenceNumber) {
        // Fix for disappearing text: explicitly click the Sequence Number radio button first
        // to force the app's frontend state (React/Flutter) to initialize the variable.
        try {
            selectSequenceNumberRadio();
            Thread.sleep(500);
        } catch (Exception e) {
            System.out.println("Could not click Sequence radio, continuing...");
        }

        org.openqa.selenium.By locator = org.openqa.selenium.By.xpath("//android.widget.EditText[contains(@resource-id, 'sequence_number')]");
        WebElement field = getVisibleElement(locator);
        sendKeys(field, sequenceNumber + "\n");
        
        // NUCLEAR OPTION: Force Android to save the text by mimicking the "Done/Check" button on the keyboard
        try {
            driver.executeScript("mobile: performEditorAction", com.google.common.collect.ImmutableMap.of("action", "done"));
        } catch (Exception e) {
            System.out.println("Failed to perform editor action, attempting to hide keyboard instead...");
            try {
                if (driver instanceof io.appium.java_client.android.AndroidDriver) {
                    ((io.appium.java_client.android.AndroidDriver) driver).hideKeyboard();
                }
            } catch (Exception ignore) {}
        }
    }

    public void selectOwnershipTransferTab() {
        System.out.println("Clicking Ownership Transfer tab...");
        org.openqa.selenium.By locator = org.openqa.selenium.By.xpath("//android.widget.Button[@content-desc=\"Ownership Transfer\"]");
        org.openqa.selenium.By sellerNationalIdBy = org.openqa.selenium.By.xpath(
            "//android.widget.EditText[@resource-id='textfield_about_you_page_motor_seller_national_id' or contains(@resource-id, 'seller_national_id')]"
        );

        try {
            WebElement tab = getVisibleElement(locator);
            tapElement(tab);

            // Allow initial Flutter tab-flicker window to settle before checking/re-tapping
            try { Thread.sleep(2000); } catch (Exception ignore) {}
            if (!isElementVisible(sellerNationalIdBy)) {
                System.out.println("Seller National ID not visible after initial settle; re-clicking Ownership Transfer tab...");
                WebElement freshTab = getVisibleElement(locator);
                tapElement(freshTab);
                waitUntil(() -> isElementVisible(sellerNationalIdBy), 5);
            }
        } catch (Exception e) {
            System.out.println("Failed to click Ownership Transfer: " + e.getMessage());
        }
    }

    public void selectNewInsuranceTab() {
        click(newInsuranceTab);
    }

    public void enterSellerId(String sellerId) {
        org.openqa.selenium.By sellerNationalIdBy = org.openqa.selenium.By.xpath(
            "//android.widget.EditText[@resource-id='textfield_about_you_page_motor_seller_national_id' or contains(@resource-id, 'seller_national_id')]"
        );
        if (!isElementVisible(sellerNationalIdBy)) {
            selectOwnershipTransferTab();
        }
        WebElement field = isElementVisible(sellerNationalIdBy) ? getVisibleElement(sellerNationalIdBy) : sellerIdField;
        sendKeys(field, sellerId);
    }

    public void selectCarModelYear(String year) {
        System.out.println("Opening Car Model Year dropdown...");
        click(carModelYearDropdown);
        try { Thread.sleep(1500); } catch (Exception e) {} // Wait for picker animation
        
        System.out.println("Looking for year: " + year);
        try {
            // Use a highly robust XPath that checks both text and content-desc, and wait for PRESENCE
            WebElement yearElement = new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(
                            org.openqa.selenium.By.xpath("//*[@text='" + year + "' or contains(@content-desc, '" + year + "')]")));
            
            System.out.println("Found year element! Clicking it...");
            yearElement.click();
            try { Thread.sleep(1000); } catch (Exception e) {} // Wait for selection to settle
            
            System.out.println("Attempting to click Confirm if it exists...");
            try {
                // Only wait 2 seconds for Confirm button. If it's a standard dropdown, it auto-closes and doesn't need Confirm!
                WebElement confirm = new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(2))
                    .until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(confirmButton));
                confirm.click();
                System.out.println("Clicked Confirm on Car Model Year.");
            } catch (Exception ex) {
                System.out.println("No Confirm button found for Car Model Year. Assuming it auto-closed.");
            }
        } catch (Exception e) {
            System.out.println("FAILED to find or select the year " + year + " in the dropdown! Error: " + e.getMessage());
        }
    }

    public void enterNationalId(String nationalId) {
        // Dynamically find the visible National ID field. This fixes the issue where switching
        // to the Ownership Transfer tab keeps the New Insurance tab's hidden fields in the DOM,
        // causing Appium to freeze trying to interact with the hidden one.
        org.openqa.selenium.By locator = org.openqa.selenium.By.xpath("//android.widget.EditText[contains(@resource-id, 'national_id') and not(contains(@resource-id, 'seller'))]");
        WebElement field = getVisibleElement(locator);
        sendKeys(field, nationalId);
        
        // NUCLEAR OPTION: Force Android to save the text by mimicking the "Done/Check" button on the keyboard
        try {
            driver.executeScript("mobile: performEditorAction", com.google.common.collect.ImmutableMap.of("action", "done"));
        } catch (Exception e) {
            System.out.println("Failed to perform editor action, attempting to hide keyboard instead...");
            try {
                if (driver instanceof io.appium.java_client.android.AndroidDriver) {
                    ((io.appium.java_client.android.AndroidDriver) driver).hideKeyboard();
                }
            } catch (Exception ignore) {}
        }
    }

    public void selectDob() {
        System.out.println("Opening Month/Year of birth dropdown...");
        org.openqa.selenium.By locator = org.openqa.selenium.By.xpath(
            "//*[contains(@resource-id, 'dob') or @hint='Month/Year of birth' or contains(@hint, 'birth') or @content-desc='Month/Year of birth' or contains(@content-desc, 'Month') or contains(@content-desc, 'birth')]"
        );
        
        // Scroll down to make sure DOB field is visible (it may be hidden under the keyboard in Flow 2)
        boolean found = waitUntil(() -> isElementVisible(locator), 5);
        if (!found) {
            System.out.println("DOB field not visible yet, scrolling down...");
            scrollDown();
        }
        
        WebElement field = getVisibleElement(locator);
        
        // Try standard click first, fall back to physical tap if it doesn't open
        try {
            field.click();
        } catch (Exception e) {
            System.out.println("Standard click on DOB failed, using physical tap...");
            tapElement(field);
        }
        
        // Wait for the picker dialog to fully animate open
        try { Thread.sleep(1500); } catch (Exception e) {}
        
        // Scroll Month wheel up to Jan (January) - skip if already shows 01
        try {
            String currentDobText = field.getText();
            if (currentDobText != null && currentDobText.startsWith("01")) {
                System.out.println("DOB month already shows 01 (January) - skipping wheel scroll.");
            } else {
                scrollMonthWheelToJan();
            }
        } catch (Exception e) {
            scrollMonthWheelToJan();
        }
        
        dismissBlockingErrorModalIfPresent();

        // Confirm the selection
        try {
            click(confirmButton);
            System.out.println("DOB picker confirmed with Jan (January) selected.");
        } catch (Exception e) {
            System.out.println("Confirm button not found for DOB, attempting fallback click/tap...");
            try {
                WebElement confirm = getVisibleElement(org.openqa.selenium.By.xpath("(//*[@text='Confirm' or @content-desc='Confirm' or @text='Done' or @content-desc='Done'])[last()]"));
                confirm.click();
            } catch (Exception ex) {
                // dynamic locator used; coordinate fallback removed
            }
        }
        dismissBlockingErrorModalIfPresent();
    }

    private void dismissBlockingErrorModalIfPresent() {
        try {
            org.openqa.selenium.By errorOrCloseBtnBy = org.openqa.selenium.By.xpath(
                "//*[contains(@content-desc, 'Something Went Wrong') or contains(@text, 'Something Went Wrong') "
                + "or contains(@content-desc, 'Retrieving data') or contains(@text, 'Retrieving data')] "
                + "| //android.widget.Button[@content-desc='Close' or @text='Close']"
            );
            if (driver.findElements(errorOrCloseBtnBy).isEmpty()) {
                return;
            }
            java.util.List<WebElement> closeButtons = driver.findElements(org.openqa.selenium.By.xpath(
                "//android.widget.Button[@content-desc='Close' or @text='Close'] "
                + "| //*[@resource-id='btn_close' or @content-desc='Close' or @text='Close']"
            ));
            for (WebElement btn : closeButtons) {
                try {
                    if (btn.isDisplayed()) {
                        System.out.println("WARNING: Transient UAT error popup detected over AboutYouScreen; dismissing Close button.");
                        btn.click();
                        try { Thread.sleep(400); } catch (InterruptedException ignore) {}
                        return;
                    }
                } catch (Exception ignore) {}
            }
        } catch (Exception ignore) {}
    }

    /**
     * Scrolls the Month wheel picker in the DOB dialog UP to Jan (January).
     * Swiping DOWN on the right Month wheel moves the items upward toward Jan.
     */
    private void scrollMonthWheelToJan() {
        System.out.println("Scrolling Month wheel up to Jan (January)...");
        try {
            // Locate Month wheel (right side wheel column in the DOB picker dialog)
            org.openqa.selenium.By monthWheelBy = org.openqa.selenium.By.xpath(
                "(//android.widget.SeekBar | //android.view.View[contains(@content-desc,'Jan') or contains(@content-desc,'Feb') or contains(@content-desc,'Mar') or contains(@content-desc,'Apr') or contains(@content-desc,'May') or contains(@content-desc,'Jun') or contains(@content-desc,'Jul') or contains(@content-desc,'Aug') or contains(@content-desc,'Sep') or contains(@content-desc,'Oct') or contains(@content-desc,'Nov') or contains(@content-desc,'Dec')])[last()]"
            );
            
            java.util.List<WebElement> monthPickers = driver.findElements(monthWheelBy);
            int startX, startY, endY;
            
            if (!monthPickers.isEmpty() && monthPickers.get(0).isDisplayed()) {
                WebElement monthPicker = monthPickers.get(0);
                org.openqa.selenium.Rectangle rect = monthPicker.getRect();
                startX = rect.getX() + (rect.getWidth() / 2);
                startY = rect.getY() + (int)(rect.getHeight() * 0.2); // Top of wheel
                endY = rect.getY() + (int)(rect.getHeight() * 0.8);   // Bottom of wheel (Swipe DOWN moves wheel UP to Jan)
            } else {
                // Exact Inspector coordinates fallback: Month wheel center X is ~716 (599 to 833), Y is ~991 to ~1357
                org.openqa.selenium.Dimension size = driver.manage().window().getSize();
                startX = (int) (size.width * 0.72);  // ~716px right column (Month wheel)
                startY = (int) (size.height * 0.40); // Top of wheel (~991px)
                endY = (int) (size.height * 0.60);   // Bottom of wheel (~1357px)
            }
            
            // Perform 3 swipes DOWN to reach "Jan" at top of list
            for (int i = 1; i <= 3; i++) {
                try {
                    java.util.List<WebElement> janCheck = driver.findElements(org.openqa.selenium.By.xpath("//*[contains(@content-desc,'Jan') or contains(@text,'Jan') or contains(@content-desc,'01')]"));
                    if (!janCheck.isEmpty() && janCheck.get(0).isDisplayed()) {
                        System.out.println("Jan (January) is visible/selected on Month wheel!");
                        break;
                    }
                } catch (Exception ignored) {}

                System.out.println("Swiping DOWN on Month wheel to reach Jan (attempt " + i + "/3)...");
                scrollWheel(startX, startY, endY);
                try { Thread.sleep(600); } catch (Exception e) {}
            }
        } catch (Exception e) {
            System.out.println("Warning while scrolling Month wheel to Jan: " + e.getMessage());
        }
    }

    public void enterPhoneNumber(String phoneNumber) {
        dismissBlockingErrorModalIfPresent();
        hideKeyboard();
        org.openqa.selenium.By locator = org.openqa.selenium.By.xpath("//android.widget.EditText[contains(@resource-id, 'phone_number')]");
        for (int attempt = 0; attempt < 3 && findDisplayedElement(locator) == null; attempt++) {
            scrollDown();
            try { Thread.sleep(350); } catch (Exception ignore) {}
        }
        WebElement field = getVisibleElement(locator);
        sendKeys(field, phoneNumber);
        hideKeyboard();
    }

    public void selectPolicyStartDate() {
        org.openqa.selenium.By startDateBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='textfield_about_you_page_motor_start_date']"
        );
        for (int attempt = 0; attempt < 2 && findDisplayedElement(startDateBy) == null; attempt++) {
            scrollDown();
            try { Thread.sleep(350); } catch (Exception ignore) {}
        }
        org.openqa.selenium.By activeConfirmBy = org.openqa.selenium.By.xpath(
            "//android.widget.Button[@content-desc='Confirm' or @text='Confirm']"
        );
        click(policyStartDateField);
        try { Thread.sleep(1000); } catch (Exception e) {} // Wait for modal animation to finish
        int confirmCountBefore = driver.findElements(activeConfirmBy).size();
        click(confirmButton);
        waitUntil(() -> {
            java.util.List<WebElement> confirms = driver.findElements(activeConfirmBy);
            if (confirms.size() < confirmCountBefore) {
                return true;
            }
            for (WebElement btn : confirms) {
                try {
                    if (btn.isDisplayed()) {
                        return false;
                    }
                } catch (Exception ignore) {}
            }
            return true;
        }, 3);
    }

    public void clickPrivacyCheckbox() {
        System.out.println("Ensuring Privacy checkbox is checked...");
        hideKeyboard();
        org.openqa.selenium.By primaryBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='tgl_about_you_page_motor_checkbox']"
        );
        org.openqa.selenium.By fallbackBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='tgl_checkbox' or contains(@resource-id, 'checkbox') or @class='android.widget.Switch']"
        );

        WebElement sw = findDisplayedElement(primaryBy);
        if (sw == null) {
            sw = findDisplayedElement(fallbackBy);
        }
        if (sw == null) {
            scrollDown();
            try { Thread.sleep(400); } catch (Exception ignore) {}
            sw = findDisplayedElement(primaryBy);
            if (sw == null) {
                sw = findDisplayedElement(fallbackBy);
            }
        }
        if (sw == null) {
            System.out.println("Privacy checkbox not found on About You screen.");
            return;
        }

        String checkedBefore = null;
        try { checkedBefore = sw.getAttribute("checked"); } catch (Exception ignore) {}
        if ("true".equalsIgnoreCase(checkedBefore)) {
            System.out.println("Privacy checkbox is already checked.");
            return;
        }

        // Single clean tap only: tgl_about_you_page_motor_checkbox does not flip its accessibility checked attribute
        tapElement(sw);
        try { Thread.sleep(500); } catch (Exception ignore) {}

        String checkedAfter = null;
        try {
            WebElement refreshed = findDisplayedElement(primaryBy);
            if (refreshed == null) {
                refreshed = findDisplayedElement(fallbackBy);
            }
            if (refreshed != null) {
                checkedAfter = refreshed.getAttribute("checked");
            }
        } catch (Exception ignore) {}
        System.out.println("Privacy checkbox interaction completed (checked=" + checkedAfter + ").");
    }

    public void clickNext() {
        System.out.println("Clicking Next button on About You screen...");
        hideKeyboard();
        org.openqa.selenium.By clickableNextBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='btn_about_you_page_motor_next_button' or @resource-id='btn_next_button']//*[@clickable='true'] "
            + "| //android.widget.Button[@clickable='true' and (@content-desc='Next' or @text='Next')]"
        );
        org.openqa.selenium.By fallbackNextBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='btn_about_you_page_motor_next_button' or @resource-id='btn_next_button' or @content-desc='Next' or @text='Next']"
        );
        org.openqa.selenium.By otpBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='textfield_otp_pin_field' or contains(@resource-id, 'otp_pin') or contains(@resource-id, 'pin_field')]"
        );
        org.openqa.selenium.By quoteBy = org.openqa.selenium.By.xpath(
            "//*[@content-desc=\"Here's your quote\" or @resource-id='btn_quote_page_motor_buy_now' or contains(@content-desc, 'Comprehensive Cover')]"
        );
        org.openqa.selenium.By blockingModalBy = org.openqa.selenium.By.xpath(
            "//*[contains(@content-desc, 'Something Went Wrong') or contains(@text, 'Something Went Wrong') "
            + "or contains(@content-desc, 'Retrieving data') or contains(@text, 'Retrieving data')]"
        );

        for (int attempt = 1; attempt <= 3; attempt++) {
            if (!driver.findElements(otpBy).isEmpty() || !driver.findElements(quoteBy).isEmpty()) {
                return;
            }
            dismissBlockingErrorModalIfPresent();

            WebElement target = findDisplayedElement(clickableNextBy);
            if (target == null) {
                target = findDisplayedElement(fallbackNextBy);
            }
            if (target == null) {
                scrollDown();
                target = findDisplayedElement(clickableNextBy);
                if (target == null) {
                    target = findDisplayedElement(fallbackNextBy);
                }
            }

            if (target != null) {
                tapElement(target);
            } else {
                click(nextButton);
            }

            boolean settled = waitUntil(() -> {
                if (!driver.findElements(otpBy).isEmpty() || !driver.findElements(quoteBy).isEmpty()) {
                    return true;
                }
                if (!driver.findElements(blockingModalBy).isEmpty()) {
                    return true;
                }
                return isValidationErrorDisplayed();
            }, 5);

            if (!driver.findElements(otpBy).isEmpty() || !driver.findElements(quoteBy).isEmpty()) {
                System.out.println("Successfully transitioned past About You screen (attempt " + attempt + ").");
                return;
            }
            if (!driver.findElements(blockingModalBy).isEmpty()) {
                System.out.println("Blocking UAT modal detected after Next (attempt " + attempt + "); dismissing...");
                dismissBlockingErrorModalIfPresent();
                continue;
            }
            if (settled && isValidationErrorDisplayed()) {
                System.out.println("Validation error detected on About You screen; stopping Next retries.");
                return;
            }
            System.out.println("About You Next attempt " + attempt + "/3 did not transition yet; retrying...");
        }
    }

    private void selectRadioOption(String label, org.openqa.selenium.By clickableBy, org.openqa.selenium.By fallbackBy, org.openqa.selenium.By expectedFieldBy) {
        System.out.println("Selecting " + label + " radio button...");
        WebElement radio = findDisplayedElement(clickableBy);
        if (radio == null) {
            radio = findDisplayedElement(fallbackBy);
        }
        if (radio == null) {
            radio = getVisibleElement(fallbackBy);
        }

        boolean isClickable = false;
        try { isClickable = "true".equalsIgnoreCase(radio.getAttribute("clickable")); } catch (Exception ignore) {}
        if (isClickable) {
            try {
                click(radio);
            } catch (Exception e) {
                System.out.println("Standard click on " + label + " radio failed, attempting physical tap...");
                tapElement(radio);
            }
        } else {
            tapElement(radio);
        }

        if (waitUntil(() -> findDisplayedElement(expectedFieldBy) != null, 2)) {
            return;
        }

        System.out.println(label + " target field not visible yet; re-tapping fresh radio element...");
        WebElement freshRadio = findDisplayedElement(clickableBy);
        if (freshRadio == null) {
            freshRadio = findDisplayedElement(fallbackBy);
        }
        if (freshRadio != null) {
            tapElement(freshRadio);
            waitUntil(() -> findDisplayedElement(expectedFieldBy) != null, 2);
        }
    }

    public void selectSequenceNumberRadio() {
        org.openqa.selenium.By clickableBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='radio_btn_about_you_page_motor_sequence_number']//*[@clickable='true'] "
            + "| //android.widget.Button[@clickable='true' and (@content-desc='Sequence Number' or @text='Sequence Number')]"
        );
        org.openqa.selenium.By fallbackBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='radio_btn_about_you_page_motor_sequence_number' or @resource-id='radio_btn_sequence_number' or @content-desc='Sequence Number' or @text='Sequence Number'] "
            + "| //*[contains(@resource-id, 'sequence_number') and contains(@resource-id, 'radio')]"
        );
        org.openqa.selenium.By seqFieldLocator = org.openqa.selenium.By.xpath(
            "//android.widget.EditText[@resource-id='textfield_about_you_page_motor_sequence_number' or contains(@resource-id, 'sequence_number')]"
        );
        selectRadioOption("Sequence Number", clickableBy, fallbackBy, seqFieldLocator);
    }

    private void tapLeftEdge(WebElement element) {
        tapElement(element);
    }

    public void selectCustomCardRadio() {
        org.openqa.selenium.By clickableBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='radio_btn_about_you_page_motor_custom_card']//*[@clickable='true'] "
            + "| //android.widget.Button[@clickable='true' and (@content-desc='Custom Card' or @text='Custom Card')]"
        );
        org.openqa.selenium.By fallbackBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='radio_btn_about_you_page_motor_custom_card' or @resource-id='radio_btn_custom_card' or @content-desc='Custom Card' or @text='Custom Card'] "
            + "| //*[contains(@resource-id, 'custom_card') and contains(@resource-id, 'radio')]"
        );
        org.openqa.selenium.By customFieldLocator = org.openqa.selenium.By.xpath(
            "//android.widget.EditText[@resource-id='textfield_about_you_page_motor_custom_card' or contains(@resource-id, 'custom_card')]"
        );
        selectRadioOption("Custom Card", clickableBy, fallbackBy, customFieldLocator);
    }

    public void enterCustomCard(String customCard) {
        org.openqa.selenium.By locator = org.openqa.selenium.By.xpath("//android.widget.EditText[contains(@resource-id, 'custom_card')]");
        WebElement field = findDisplayedElement(locator);
        if (field == null) {
            scrollUp();
            field = getVisibleElement(locator);
        }

        sendKeys(field, customCard);
        try {
            driver.executeScript("mobile: performEditorAction", com.google.common.collect.ImmutableMap.of("action", "done"));
        } catch (Exception ignore) {}
        hideKeyboard();

        if (customCard != null && !getCustomCardText().replaceAll("\\D", "").contains(customCard.replaceAll("\\D", ""))) {
            System.out.println("Custom Card digits not retained after sendKeys; re-entering via tapElement + adbInput...");
            WebElement retryField = findDisplayedElement(locator);
            if (retryField != null) {
                tapElement(retryField);
                try { Thread.sleep(300); } catch (Exception ignore) {}
                try { retryField.clear(); } catch (Exception ignore) {}
                adbInput(customCard);
                try {
                    driver.executeScript("mobile: performEditorAction", com.google.common.collect.ImmutableMap.of("action", "done"));
                } catch (Exception ignore) {}
                hideKeyboard();
            }
        }
    }

    public String getCustomCardText() {
        try {
            org.openqa.selenium.By locator = org.openqa.selenium.By.xpath("//android.widget.EditText[contains(@resource-id, 'custom_card')]");
            WebElement field = findDisplayedElement(locator);
            if (field != null) {
                String txt = field.getText();
                return txt != null ? txt.trim() : "";
            }
        } catch (Exception ignore) {}
        return "";
    }

    public String getSequenceNumberText() {
        try {
            org.openqa.selenium.By locator = org.openqa.selenium.By.xpath("//android.widget.EditText[contains(@resource-id, 'sequence_number')]");
            java.util.List<WebElement> fields = findWithUnimportantViews(locator);
            if (!fields.isEmpty()) {
                String txt = fields.get(0).getText();
                return txt != null ? txt.trim() : "";
            }
        } catch (Exception ignore) {}
        return "";
    }

    public boolean isScreenDisplayed() {
        try {
            org.openqa.selenium.By screenBy = org.openqa.selenium.By.xpath(
                "//android.widget.EditText[contains(@resource-id, 'national_id') or contains(@resource-id, 'sequence_number') or contains(@resource-id, 'custom_card')] " +
                "| //*[@resource-id='btn_about_you_page_motor_next_button' or @resource-id='btn_next_button']"
            );
            return !findWithUnimportantViews(screenBy).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isValidationErrorDisplayed() {
        try {
            org.openqa.selenium.By errBy = org.openqa.selenium.By.xpath(
                "//*[contains(@resource-id, 'error') " +
                "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'invalid') " +
                "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'invalid') " +
                "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'required') " +
                "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'required') " +
                "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'please enter') " +
                "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'please enter') " +
                "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'must be') " +
                "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'must be') " +
                "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'digits') " +
                "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'digits') " +
                "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'something went wrong') " +
                "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'something went wrong')]"
            );
            return !findWithUnimportantViews(errBy).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}
