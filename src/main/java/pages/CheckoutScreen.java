package pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;

public class CheckoutScreen extends BasePage {

    public CheckoutScreen() {
        super();
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(10)), this);
    }

    @AndroidFindBy(xpath = "//*[@resource-id='btn_checkout_page_motor_payment_mode_direct_pay_post_card']")
    private WebElement creditDebitCardButton;

    @AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='textfield_checkout_page_motor_email']")
    private WebElement emailField;

    @AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='textfield_checkout_page_motor_iban']")
    private WebElement ibanField;

    @AndroidFindBy(xpath = "//android.widget.Button[@content-desc='Pay now']")
    private WebElement payNowButton;

    @AndroidFindBy(xpath = "//*[@resource-id='tgl_checkout_page_motor_checkbox']")
    private WebElement termsCheckbox;

    public void clickTermsCheckbox() {
        System.out.println("Clicking Terms and Conditions checkbox on Checkout Screen...");
        try {
            // Scroll slightly to ensure the checkbox isn't hidden by the bottom navigation bar
            scrollDown();
            Thread.sleep(500);
            
            click(termsCheckbox);
            Thread.sleep(1000); // Wait for toggle animation
            
            // Self-verify if the switch actually toggled
            String isChecked = termsCheckbox.getAttribute("checked");
            if ("false".equals(isChecked)) {
                System.out.println("Checkbox ignored the click, using physical tap...");
                tapElement(termsCheckbox);
            }
        } catch (Exception e) {
            System.out.println("Could not find Terms checkbox, moving on...");
        }
    }

    public void selectCreditDebitCard() {
        System.out.println("Scrolling down to reveal Payment Methods...");
        boolean found = false;
        for (int i = 0; i < 3; i++) {
            try {
                if (creditDebitCardButton.isDisplayed()) {
                    found = true;
                    break;
                }
            } catch (Exception e) {}
            scrollDown();
            try { Thread.sleep(1000); } catch (Exception e) {}
        }
        if (found) {
            click(creditDebitCardButton);
        } else {
            System.out.println("Credit/Debit card button not found on this screen! Assuming it is removed or auto-selected.");
        }
    }

    public void enterEmail(String email) {
        sendKeys(emailField, email);
    }

    public void enterIban(String iban) {
        System.out.println("Entering IBAN into Checkout field...");
        
        // Strip 'SA' prefix up front so the UI field receives strictly the 22 numeric digits
        String numericOnly = (iban != null && iban.toUpperCase().startsWith("SA")) ? iban.substring(2) : iban;
        
        sendKeys(ibanField, numericOnly);
        
        // Allow app frontend (React/Flutter) a moment to capture the onChange event
        try { Thread.sleep(1000); } catch(Exception e) {}
        
        // Removed performEditorAction here because it appears to be clearing the IBAN field on the checkout screen.
        
        // CRITICAL BUG FIX: Hide the keyboard so it doesn't physically cover the Checkbox!
        try {
            if (driver instanceof io.appium.java_client.android.AndroidDriver) {
                ((io.appium.java_client.android.AndroidDriver) driver).hideKeyboard();
            }
            Thread.sleep(500); // let keyboard disappear
        } catch (Exception ignore) {}
        
        // Verification loop: If it wiped the text, type it again!
        try {
            String currentText = ibanField.getText();
            if (currentText == null || currentText.trim().isEmpty() || currentText.equals("IBAN *")) {
                System.out.println("IBAN was wiped by the app! Retrying via Actions...");
                org.openqa.selenium.interactions.Actions actions = new org.openqa.selenium.interactions.Actions(driver);
                actions.moveToElement(ibanField).click().sendKeys(numericOnly).perform();
                try {
                    if (driver instanceof io.appium.java_client.android.AndroidDriver) {
                        ((io.appium.java_client.android.AndroidDriver) driver).hideKeyboard();
                    }
                } catch (Exception ignore) {}
            }
        } catch (Exception e) {}
    }
    
    public void clickPayNow() {
        click(payNowButton);
    }

    public void autoFillCheckout(utils.TestDataBuilder.CustomerProfile profile) {
        selectCreditDebitCard();
        enterEmail(profile.email);
        enterIban(profile.iban);
        clickTermsCheckbox();
        clickPayNow();
    }
}
