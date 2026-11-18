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
            org.openqa.selenium.By vatBy = org.openqa.selenium.By.xpath("//*[contains(@content-desc, 'VAT') or contains(@text, 'VAT') or contains(@content-desc, '15%')]");
            if (!driver.findElements(vatBy).isEmpty()) {
                return true;
            }
            // If collapsed under 'Details', tap Details to expand the premium breakdown
            org.openqa.selenium.By detailsBy = org.openqa.selenium.By.xpath("//*[contains(@content-desc, 'Details') or contains(@text, 'Details')]");
            java.util.List<WebElement> details = driver.findElements(detailsBy);
            if (!details.isEmpty()) {
                tapElement(details.get(0));
                Thread.sleep(800);
            }
            return !driver.findElements(vatBy).isEmpty();
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
        System.out.println("Entering IBAN into Checkout field: " + iban);
        
        // Strip 'SA' prefix up front so the UI field receives strictly the 22 numeric digits
        String numericOnly = (iban != null && iban.toUpperCase().startsWith("SA")) ? iban.substring(2) : iban;
        
        org.openqa.selenium.By ibanBy = org.openqa.selenium.By.xpath(
            "//android.widget.EditText[@resource-id='textfield_iban' or @resource-id='textfield_checkout_page_motor_iban' " +
            "or contains(@resource-id, 'iban') or contains(@hint, 'IBAN')] | //*[@resource-id='textfield_iban' or contains(@resource-id, 'iban')]"
        );
        
        try {
            waitUntil(() -> !findWithUnimportantViews(ibanBy).isEmpty(), 8);
            java.util.List<WebElement> list = findWithUnimportantViews(ibanBy);
            if (!list.isEmpty()) {
                WebElement el = list.get(list.size() - 1);
                try {
                    tapElement(el);
                    try { Thread.sleep(300); } catch (Exception ignore) {}
                    el.clear();
                    el.sendKeys(numericOnly);
                    System.out.println("Entered IBAN digits via direct sendKeys: " + numericOnly);
                    hideKeyboard();
                    return;
                } catch (Exception ex) {
                    System.out.println("IBAN direct sendKeys note: " + ex.getMessage());
                }
                try {
                    org.openqa.selenium.Rectangle r = el.getRect();
                    int cx = r.getX() + (r.getWidth() / 2);
                    int cy = r.getY() + (r.getHeight() / 2);
                    System.out.println("Focusing IBAN field at dynamic center: (" + cx + ", " + cy + ")");
                    adbTap(cx, cy);
                    try { Thread.sleep(300); } catch (Exception ignore) {}
                    adbInput(numericOnly);
                    System.out.println("Entered IBAN digits via dynamic adb: " + numericOnly);
                    hideKeyboard();
                    return;
                } catch (Exception ignore) {}
            }
        } catch (Exception ex) {
            System.out.println("IBAN find error: " + ex.getMessage());
        }
        
        System.out.println("Tapping IBAN field at standard coordinates (540, 1925)...");
        adbTap(540, 1925);
        try { Thread.sleep(400); } catch (Exception ignore) {}
        adbInput(numericOnly);
        hideKeyboard();
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
        java.util.List<WebElement> cards = findWithUnimportantViews(cardBy);
        if (!cards.isEmpty()) {
            System.out.println("Found Credit/Debit card button directly, clicking...");
            try {
                tapElement(cards.get(0));
            } catch (Exception ignore) {}
            return;
        }

        // Only scroll if card button was not visible
        scrollDown();
        cards = findWithUnimportantViews(cardBy);
        if (!cards.isEmpty()) {
            System.out.println("Found Credit/Debit card button after scroll, clicking...");
            try {
                tapElement(cards.get(0));
            } catch (Exception ignore) {}
            return;
        }

        try {
            org.openqa.selenium.By anyPaymentBy = org.openqa.selenium.By.xpath(
                "//*[contains(@resource-id, 'payment_mode') or contains(@content-desc, 'Card') or contains(@text, 'Card') or contains(@content-desc, 'Tamara') or contains(@content-desc, 'Tabby')]"
            );
            List<WebElement> payments = findWithUnimportantViews(anyPaymentBy);
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
            "//*[@resource-id='tgl_checkout_page_motor_checkbox' or @resource-id='tgl_checkbox' " +
            "or contains(@resource-id, 'tgl') or contains(@resource-id, 'checkbox') or @class='android.widget.Switch']"
        );
        clickPrivacySwitch(termsBy);
    }

    public void clickPayNow() {
        if (isPaymentOrConfirmationAlreadyActive()) {
            System.out.println("Payment/confirmation already active; skipping duplicate Pay now click.");
            return;
        }
        System.out.println("Clicking Pay now button on Checkout Screen...");
        org.openqa.selenium.By clickablePayBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='btn_pay_now' or @content-desc='Pay now' or @text='Pay now']"
        );
        java.util.List<WebElement> payButtons = findWithUnimportantViews(clickablePayBy);
        if (!payButtons.isEmpty()) {
            WebElement btn = payButtons.get(payButtons.size() - 1);
            org.openqa.selenium.Rectangle r = btn.getRect();
            int cx = r.getX() + (r.getWidth() / 2);
            int cy = r.getY() + (r.getHeight() / 2);
            System.out.println("Dynamic center of Pay now button: (" + cx + ", " + cy + ")");
            tapCoordinates(cx, cy);
            adbTap(cx, cy);
            try { Thread.sleep(1500); } catch (Exception ignore) {}
            return;
        }
        tapCoordinates(540, 2261);
        adbTap(540, 2261);
        try { Thread.sleep(1500); } catch (Exception ignore) {}
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
