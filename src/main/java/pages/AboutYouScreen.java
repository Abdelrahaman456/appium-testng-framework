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

    @AndroidFindBy(xpath = "//*[@resource-id='tgl_about_you_page_motor_ownershipTransfer']")
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
        org.openqa.selenium.By locator = org.openqa.selenium.By.xpath("//android.widget.EditText[contains(@resource-id, 'sequence_number')]");
        WebElement field = getVisibleElement(locator);
        sendKeys(field, sequenceNumber);
    }

    public void selectOwnershipTransferTab() {
        System.out.println("Clicking Ownership Transfer tab (First Click)...");
        click(ownershipTransferTab);
        
        // Wait 2 seconds to let the "flicker" or forced reset happen
        try { Thread.sleep(2000); } catch (Exception e) {}
        
        System.out.println("Clicking Ownership Transfer tab (Second Click to lock it in)...");
        click(ownershipTransferTab);
        
        // Wait 2 seconds to ensure it is stable before moving on
        try { Thread.sleep(2000); } catch (Exception e) {}
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
    }

    public void selectDob() {
        org.openqa.selenium.By locator = org.openqa.selenium.By.xpath("//*[(contains(@resource-id, 'dob') or @content-desc='Month/Year of birth')]");
        WebElement field = getVisibleElement(locator);
        click(field);
        
        // Wait for Picker to fully open
        try { Thread.sleep(1000); } catch (Exception e) {}
        
        click(confirmButton);
    }

    public void enterPhoneNumber(String phoneNumber) {
        org.openqa.selenium.By locator = org.openqa.selenium.By.xpath("//android.widget.EditText[contains(@resource-id, 'phone_number')]");
        WebElement field = getVisibleElement(locator);
        sendKeys(field, phoneNumber);
    }

    public void selectPolicyStartDate() {
        click(policyStartDateField);
        try { Thread.sleep(1000); } catch (Exception e) {} // Wait for modal animation to finish
        click(confirmButton);
    }

    public void clickPrivacyCheckbox() {
        click(privacyCheckbox);
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

    public void selectCustomCardRadio() {
        org.openqa.selenium.By locator = org.openqa.selenium.By.xpath("//*[contains(@resource-id, 'custom_card') and contains(@resource-id, 'radio')]");
        WebElement radio = getVisibleElement(locator);
        click(radio);
    }

    public void enterCustomCard(String customCard) {
        org.openqa.selenium.By locator = org.openqa.selenium.By.xpath("//android.widget.EditText[contains(@resource-id, 'custom_card')]");
        WebElement field = getVisibleElement(locator);
        sendKeys(field, customCard);
    }
}
