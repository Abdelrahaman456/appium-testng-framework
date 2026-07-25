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

    public void selectCreditDebitCard() {
        System.out.println("Scrolling down to reveal Payment Methods...");
        for (int i = 0; i < 3; i++) {
            try {
                if (creditDebitCardButton.isDisplayed()) {
                    break;
                }
            } catch (Exception e) {}
            scrollDown();
            try { Thread.sleep(1000); } catch (Exception e) {}
        }
        click(creditDebitCardButton);
    }

    public void enterEmail(String email) {
        sendKeys(emailField, email);
    }

    public void enterIban(String iban) {
        sendKeys(ibanField, iban);
    }
    
    public void clickPayNow() {
        click(payNowButton);
    }

    public void autoFillCheckout(utils.TestDataBuilder.CustomerProfile profile) {
        selectCreditDebitCard();
        enterEmail(profile.email);
        enterIban(profile.iban);
        clickPayNow();
    }
}
