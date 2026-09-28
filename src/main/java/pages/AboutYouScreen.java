package pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;

public class AboutYouScreen extends BasePage {

    public AboutYouScreen() {
        super();
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(0)), this);
    }

    @AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='textfield_about_you_page_motor_sequence_number']")
    private WebElement sequenceNumberField;

    @AndroidFindBy(xpath = "//android.widget.Button[@content-desc=\"Ownership Transfer\"]")
    private WebElement ownershipTransferTab;

    @AndroidFindBy(xpath = "//*[@resource-id='tgl_about_you_page_motor_newInsurance']")
    private WebElement newInsuranceTab;

    @AndroidFindBy(xpath = "//*[@resource-id='radio_btn_about_you_page_motor_custom_card']")
    private WebElement customCardRadio;

    @AndroidFindBy(xpath = "//*[@resource-id='radio_btn_about_you_page_motor_sequence_number']")
    private WebElement sequenceNumberRadio;

    @AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='textfield_about_you_page_motor_custom_card']")
    private WebElement customCardField;

    @AndroidFindBy(xpath = "//android.widget.Button[@content-desc=\"Car model year\"]")
    private WebElement carModelYearDropdown;

    @AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='textfield_about_you_page_motor_seller_national_id']")
    private WebElement sellerIdField;

    @AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='textfield_about_you_page_motor_national_id']")
    private WebElement nationalIdField;

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_about_you_page_motor_dob']")
    private WebElement dobField;

    @AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='textfield_about_you_page_motor_phone_number']")
    private WebElement phoneNumberField;

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_about_you_page_motor_start_date']")
    private WebElement policyStartDateField;

    // Use [last()] to guarantee we click the active Confirm button, 
    // bypassing any hidden 'ghost' Confirm buttons left behind by the DOB picker!
    @AndroidFindBy(xpath = "(//android.widget.Button[@content-desc=\"Confirm\"])[last()]")
    private WebElement confirmButton;

    @AndroidFindBy(xpath = "//*[@resource-id='tgl_about_you_page_motor_checkbox']")
    private WebElement privacyCheckbox;

    @AndroidFindBy(xpath = "//android.widget.Button[@content-desc=\"Next\"]")
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
        
        try {
            // First click
            WebElement tab = getVisibleElement(locator);
            click(tab);
            
            // Bug workaround: The button flickers and returns to New Insurance! 
            // Wait 3 seconds and click it AGAIN.
            System.out.println("Wait 3 seconds for state flicker to settle...");
            try { Thread.sleep(3000); } catch (Exception e) {}
            
            System.out.println("Re-clicking Ownership Transfer tab to bypass flicker bug...");
            WebElement freshTab = driver.findElement(locator);
            click(freshTab);
            
        } catch (Exception e) {
            System.out.println("Failed to click Ownership Transfer: " + e.getMessage());
        }
        
        try { Thread.sleep(1500); } catch (Exception e) {}
    }

    public void selectNewInsuranceTab() {
        click(newInsuranceTab);
    }

    public void enterSellerId(String sellerId) {
        sendKeys(sellerIdField, sellerId);
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
            "//*[contains(@resource-id, 'dob') or @content-desc='Month/Year of birth' or contains(@content-desc, 'Month') or contains(@content-desc, 'birth')]"
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
        
        // Confirm the selection
        try {
            click(confirmButton);
            System.out.println("DOB picker confirmed with Jan (January) selected.");
        } catch (Exception e) {
            System.out.println("Confirm button not found for DOB, attempting fallback click/tap...");
            try {
                WebElement confirm = getVisibleElement(org.openqa.selenium.By.xpath("(//*[@text='Confirm' or @content-desc='Confirm' or @text='Done' or @content-desc='Done'])[last()]"));
                confirm.click();
            } catch (Exception ex) {}
        }
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
        org.openqa.selenium.By locator = org.openqa.selenium.By.xpath("//android.widget.EditText[contains(@resource-id, 'phone_number')]");
        WebElement field = getVisibleElement(locator);
        sendKeys(field, phoneNumber);
        try {
            if (driver instanceof io.appium.java_client.android.AndroidDriver) {
                ((io.appium.java_client.android.AndroidDriver) driver).hideKeyboard();
            }
        } catch (Exception e) {}
    }

    public void selectPolicyStartDate() {
        click(policyStartDateField);
        try { Thread.sleep(1000); } catch (Exception e) {} // Wait for modal animation to finish
        click(confirmButton);
    }

    public void clickPrivacyCheckbox() {
        System.out.println("Scrolling to and clicking Privacy checkbox...");
        try {
            // Scroll the checkbox into view before clicking (it's below the fold)
            driver.executeScript("mobile: scroll", com.google.common.collect.ImmutableMap.of(
                "strategy", "accessibility id",
                "selector", "tgl_about_you_page_motor_checkbox",
                "direction", "down"
            ));
        } catch (Exception e) {
            // Fallback: generic scroll to bottom of page
            try {
                org.openqa.selenium.Dimension size = driver.manage().window().getSize();
                int startX = size.width / 2;
                scrollWheel(startX, (int)(size.height * 0.8), (int)(size.height * 0.2));
                Thread.sleep(500);
            } catch (Exception ignore) {}
        }
        click(privacyCheckbox);
        System.out.println("Privacy checkbox clicked.");
    }

    public void clickNext() {
        click(nextButton);
    }

    public void selectSequenceNumberRadio() {
        System.out.println("Selecting Sequence Number radio button (resource-id: radio_btn_about_you_page_motor_sequence_number)...");
        org.openqa.selenium.By locator = org.openqa.selenium.By.xpath("//*[@resource-id='radio_btn_about_you_page_motor_sequence_number'] | //*[contains(@resource-id, 'sequence_number') and contains(@resource-id, 'radio')]");
        WebElement radio = getVisibleElement(locator);
        
        try {
            click(radio);
        } catch (Exception e) {
            System.out.println("Standard click on Sequence Number radio failed, attempting physical tap...");
            tapElement(radio);
        }
        
        try { Thread.sleep(1000); } catch (Exception e) {}
        
        // Self-verification: check if sequence number text field is visible. If not, re-tap radio button!
        try {
            org.openqa.selenium.By seqFieldLocator = org.openqa.selenium.By.xpath("//android.widget.EditText[contains(@resource-id, 'sequence_number')]");
            WebElement seqField = driver.findElement(seqFieldLocator);
            if (!seqField.isDisplayed()) {
                System.out.println("Sequence number field not visible yet. Tapping radio button again...");
                tapElement(radio);
                try { Thread.sleep(1000); } catch (Exception e) {}
            }
        } catch (Exception e) {
            System.out.println("Re-attempting tap on Sequence Number radio...");
            tapElement(radio);
        }
    }

    private void tapLeftEdge(WebElement element) {
        org.openqa.selenium.Rectangle rect = element.getRect();
        // Tap 20 pixels from the left edge, which hits the physical radio button circle instead of the text
        int leftX = rect.getX() + 20;
        int centerY = rect.getY() + (rect.getHeight() / 2);
        tapCoordinates(leftX, centerY);
    }

    public void selectCustomCardRadio() {
        System.out.println("Selecting Custom Card radio button...");
        org.openqa.selenium.By locator = org.openqa.selenium.By.xpath("//*[@resource-id='radio_btn_about_you_page_motor_custom_card'] | //*[contains(@resource-id, 'custom_card') and contains(@resource-id, 'radio')]");
        WebElement radio = getVisibleElement(locator);
        
        try {
            click(radio);
        } catch (Exception e) {
            System.out.println("Standard click on Custom Card radio failed, attempting physical tap...");
            tapElement(radio);
        }
        
        try { Thread.sleep(1000); } catch (Exception e) {}
        
        // Self-verification: check if custom card text field is visible. If not, re-tap radio button!
        try {
            org.openqa.selenium.By customFieldLocator = org.openqa.selenium.By.xpath("//android.widget.EditText[contains(@resource-id, 'custom_card')]");
            if (driver.findElements(customFieldLocator).isEmpty()) {
                System.out.println("Custom Card text field not visible yet. Re-tapping radio button...");
                
                WebElement freshRadio = driver.findElement(locator);
                try {
                    driver.executeScript("mobile: clickGesture", com.google.common.collect.ImmutableMap.of(
                        "elementId", ((org.openqa.selenium.remote.RemoteWebElement) freshRadio).getId()
                    ));
                } catch (Exception ex) {
                    tapElement(freshRadio);
                }
                try { Thread.sleep(1000); } catch (Exception e) {}
            }
        } catch (Exception e) {
            System.out.println("Re-attempting tap on Custom Card radio...");
            try {
                WebElement freshRadio = driver.findElement(locator);
                try {
                    driver.executeScript("mobile: clickGesture", com.google.common.collect.ImmutableMap.of(
                        "elementId", ((org.openqa.selenium.remote.RemoteWebElement) freshRadio).getId()
                    ));
                } catch (Exception ex) {
                    tapElement(freshRadio);
                }
            } catch (Exception staleEx) {
                System.out.println("Failed to find radio for retry: " + staleEx.getMessage());
            }
        }
    }

    public void enterCustomCard(String customCard) {
        org.openqa.selenium.By locator = org.openqa.selenium.By.xpath("//android.widget.EditText[contains(@resource-id, 'custom_card')]");
        WebElement field = getVisibleElement(locator);
        
        sendKeys(field, customCard);
        
        // Hide keyboard after typing to ensure the state saves
        try {
            if (driver instanceof io.appium.java_client.android.AndroidDriver) {
                ((io.appium.java_client.android.AndroidDriver) driver).hideKeyboard();
            }
            Thread.sleep(500);
        } catch (Exception ignore) {}
    }
}
