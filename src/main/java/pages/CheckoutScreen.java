package pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;
import java.util.List;

public class CheckoutScreen extends BasePage {

    public CheckoutScreen() {
        super();
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(2)), this);
    }

    @AndroidFindBy(xpath = "//*[@resource-id='btn_checkout_page_motor_payment_mode_direct_pay_post_card' or @resource-id='btn_payment_mode_moyasar_direct_pay' or contains(@content-desc, 'Credit') or contains(@text, 'Credit') or contains(@content-desc, 'Debit') or contains(@text, 'Debit') or contains(@content-desc, 'Card') or contains(@text, 'Card')]")
    private WebElement creditDebitCardButton;

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_checkout_page_motor_email' or @resource-id='textfield_email' or contains(@resource-id, 'email') or @hint='Email' or contains(@hint, 'email')]")
    private WebElement emailField;

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_checkout_page_motor_iban' or @resource-id='textfield_iban' or contains(@resource-id, 'iban') or contains(@hint, 'IBAN')]")
    private WebElement ibanField;

    @AndroidFindBy(xpath = "//android.widget.Button[@clickable='true' and (@content-desc='Pay now' or @text='Pay now')] | //*[@resource-id='btn_checkout_page_motor_pay_now' or @resource-id='btn_pay_now' or @content-desc='Pay now' or @text='Pay now']")
    private WebElement payNowButton;

    @AndroidFindBy(xpath = "//*[@resource-id='tgl_checkout_page_motor_checkbox' or @resource-id='tgl_checkbox' or contains(@resource-id, 'checkbox') or @class='android.widget.Switch']")
    private WebElement termsCheckbox;

    @AndroidFindBy(xpath = "//*[contains(@content-desc, 'VAT') or contains(@text, 'VAT') or contains(@content-desc, '15%')]")
    private WebElement vatBreakdownText;

    @AndroidFindBy(xpath = "//*[contains(@content-desc, 'Total') or contains(@text, 'Total')]")
    private WebElement totalAmountText;

    public boolean isVatBreakdownDisplayed() {
        try {
            return isElementVisible(vatBreakdownText) || !driver.findElements(org.openqa.selenium.By.xpath("//*[contains(@content-desc, 'VAT') or contains(@text, 'VAT')]")).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public void enterEmail(String email) {
        System.out.println("Entering Email into Checkout field: " + email);
        org.openqa.selenium.By emailBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='textfield_checkout_page_motor_email' or @resource-id='textfield_email' or contains(@resource-id, 'email') or @hint='Email' or contains(@hint, 'email')]"
        );
        try {
            List<WebElement> els = driver.findElements(emailBy);
            if (els.isEmpty()) {
                scrollUp();
                waitUntil(() -> !driver.findElements(emailBy).isEmpty(), 3);
                els = driver.findElements(emailBy);
            }
            if (!els.isEmpty()) {
                sendKeys(els.get(0), email);
                return;
            }
            List<WebElement> atEls = driver.findElements(org.openqa.selenium.By.xpath("//*[contains(@text, '@')]"));
            if (!atEls.isEmpty()) {
                sendKeys(atEls.get(0), email);
            } else {
                System.out.println("Email field skipped/not present on this checkout screen.");
            }
        } catch (Exception e) {
            System.out.println("Email field skipped/already filled: " + e.getMessage());
        }
    }

    public void enterIban(String iban) {
        System.out.println("Entering IBAN into Checkout field...");
        
        // Strip 'SA' prefix up front so the UI field receives strictly the 22 numeric digits
        String numericOnly = (iban != null && iban.toUpperCase().startsWith("SA")) ? iban.substring(2) : iban;
        
        try {
            waitForVisibility(ibanField);
            sendKeys(ibanField, numericOnly);
        } catch (Exception e) {
            try {
                WebElement el = driver.findElement(org.openqa.selenium.By.xpath("//*[contains(@resource-id, 'iban')]"));
                sendKeys(el, numericOnly);
            } catch (Exception ex) {
                System.out.println("IBAN entry fallback: " + ex.getMessage());
            }
        }
        
        try { Thread.sleep(800); } catch(Exception e) {}
        
        // Hide keyboard
        try {
            if (driver instanceof io.appium.java_client.android.AndroidDriver) {
                ((io.appium.java_client.android.AndroidDriver) driver).hideKeyboard();
            }
            Thread.sleep(500);
        } catch (Exception ignore) {}
    }

    public void selectCreditDebitCard() {
        System.out.println("Checking for Payment Methods on Checkout Screen...");
        org.openqa.selenium.By cardBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='btn_checkout_page_motor_payment_mode_direct_pay_post_card' " +
            "or @resource-id='btn_payment_mode_moyasar_direct_pay' " +
            "or contains(@content-desc, 'Credit') or contains(@text, 'Credit') " +
            "or contains(@content-desc, 'Debit') or contains(@text, 'Debit') " +
            "or contains(@content-desc, 'Card') or contains(@text, 'Card')]"
        );
        if (isElementVisible(cardBy)) {
            System.out.println("Found Credit/Debit card button directly, clicking...");
            tapElement(driver.findElements(cardBy).get(0));
            return;
        }

        // Scroll slightly down to check payment options below the fold
        scrollDown();
        if (waitUntil(() -> isElementVisible(cardBy), 2)) {
            System.out.println("Found Credit/Debit card button after scroll, clicking...");
            tapElement(driver.findElements(cardBy).get(0));
            return;
        }

        try {
            org.openqa.selenium.By anyPaymentBy = org.openqa.selenium.By.xpath(
                "//*[contains(@resource-id, 'payment_mode') or contains(@content-desc, 'Card') or contains(@text, 'Card') or contains(@content-desc, 'Tamara') or contains(@content-desc, 'Tabby')]"
            );
            List<WebElement> payments = driver.findElements(anyPaymentBy);
            if (!payments.isEmpty() && payments.get(0).isDisplayed()) {
                tapElement(payments.get(0));
                System.out.println("Selected payment method: " + payments.get(0).getAttribute("content-desc"));
            }
        } catch (Exception ex) {
            System.out.println("Payment method selection completed/skipped.");
        }
    }

    public boolean waitForPageToLoad() {
        System.out.println("Waiting for Checkout Screen to load...");
        org.openqa.selenium.By checkoutReadyBy = org.openqa.selenium.By.xpath(
            "//*[@content-desc='Checkout' or @text='Checkout' " +
            "or contains(@content-desc, 'Total premium') or contains(@text, 'Total premium') " +
            "or contains(@content-desc, 'Payment Method') or contains(@text, 'Payment Method') " +
            "or contains(@content-desc, 'Credit/Debit') or contains(@text, 'Credit/Debit') " +
            "or contains(@content-desc, 'Required info') or contains(@text, 'Required info') " +
            "or @resource-id='btn_checkout_page_motor_payment_mode_direct_pay_post_card' " +
            "or @resource-id='btn_payment_mode_moyasar_direct_pay' " +
            "or @resource-id='btn_checkout_page_motor_pay_now' or @resource-id='btn_pay_now' " +
            "or @resource-id='tgl_checkout_page_motor_checkbox' or @resource-id='tgl_checkbox' " +
            "or @resource-id='textfield_checkout_page_motor_iban' or @resource-id='textfield_iban' " +
            "or @content-desc='Pay now' or @text='Pay now']"
        );
        boolean loaded = waitUntil(() -> !findWithUnimportantViews(checkoutReadyBy).isEmpty(), 20);
        if (!loaded) {
            System.out.println("Warning: Checkout Screen did not signal load within 20s");
        }
        return loaded;
    }

    public void clickTermsCheckbox() {
        System.out.println("Clicking Terms and Conditions checkbox on Checkout Screen...");
        org.openqa.selenium.By termsBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='tgl_checkout_page_motor_checkbox' or @resource-id='tgl_checkbox' or contains(@resource-id, 'checkbox') " +
            "or @class='android.widget.CheckBox' or @class='android.widget.Switch' " +
            "or contains(@content-desc, 'I accept') or contains(@text, 'I accept') " +
            "or contains(@content-desc, 'Terms') or contains(@text, 'Terms')]"
        );
        java.util.List<WebElement> boxes = findWithUnimportantViews(termsBy);
        if (!boxes.isEmpty()) {
            tapElement(boxes.get(0));
            return;
        }
        if (!clickPrivacySwitch(termsBy)) {
            scrollDown();
            boxes = findWithUnimportantViews(termsBy);
            if (!boxes.isEmpty()) {
                tapElement(boxes.get(0));
                return;
            }
            System.out.println("Could not find Terms checkbox on Checkout screen.");
        }
    }

    public void clickPayNow() {
        if (isPaymentOrConfirmationAlreadyActive()) {
            System.out.println("Payment/confirmation already active; skipping duplicate Pay now click.");
            return;
        }
        System.out.println("Clicking Pay now button...");
        org.openqa.selenium.By clickablePayBy = org.openqa.selenium.By.xpath(
            "//android.widget.Button[@clickable='true' and (@content-desc='Pay now' or @text='Pay now')] " +
            "| //*[@resource-id='btn_checkout_page_motor_pay_now' or @resource-id='btn_pay_now' or @content-desc='Pay now' or @text='Pay now']"
        );
        try {
            List<WebElement> payButtons = findWithUnimportantViews(clickablePayBy);
            if (payButtons.isEmpty()) {
                scrollDown();
                waitUntil(() -> !findWithUnimportantViews(clickablePayBy).isEmpty(), 3);
                payButtons = findWithUnimportantViews(clickablePayBy);
            }
            if (!payButtons.isEmpty()) {
                WebElement targetBtn = payButtons.get(payButtons.size() - 1);
                tapElement(targetBtn);
                return;
            }
        } catch (Exception ignore) {}

        try {
            if (!isElementVisible(payNowButton)) {
                scrollDown();
            }
            tapElement(payNowButton);
        } catch (Exception e) {
            System.out.println("Standard tap on Pay now failed, retrying click with coordinates...");
            tapCoordinates(540, 2260);
        }
    }

    public void autoFillCheckout(utils.TestDataBuilder.CustomerProfile profile) {
        waitForPageToLoad();
        enterEmail(profile.email);
        enterIban(profile.iban);
        selectCreditDebitCard();
        clickTermsCheckbox();
        clickPayNow();
    }
}
