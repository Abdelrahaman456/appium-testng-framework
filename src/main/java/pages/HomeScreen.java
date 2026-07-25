package pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;

public class HomeScreen extends BasePage {

    public HomeScreen() {
        super();
        // Set decorator timeout to 0 so explicit wait handles polling efficiently without double-waiting
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(0)), this);
    }

    // Using uiAutomator instead of XPath! XPath is the #1 cause of slow Appium tests.
    // Native uiAutomator searches the screen in milliseconds instead of seconds.
    @AndroidFindBy(uiAutomator = "new UiSelector().resourceId(\"btn_home_product_motor\")")
    private WebElement motorProductButton;

    @AndroidFindBy(uiAutomator = "new UiSelector().resourceId(\"btn_home_product_individualHealthInsurance\")")
    private WebElement healthInsuranceButton;

    @AndroidFindBy(xpath = "//*[@resource-id='btn_home_product_income']")
    private WebElement incomeButton;

    @AndroidFindBy(xpath = "//*[@resource-id='btn_home_product_pet']")
    private WebElement petButton;

    @AndroidFindBy(accessibility = "Login")
    private WebElement loginButton;

    public HomeScreen clickMotorProduct() {
        click(motorProductButton);
        return this;
    }

    public HomeScreen clickHealthInsuranceProduct() {
        click(healthInsuranceButton);
        return this;
    }

    public HomeScreen clickIncomeProduct() {
        click(incomeButton);
        return this;
    }

    public HomeScreen clickPetProduct() {
        click(petButton);
        return this;
    }

    public HomeScreen clickLoginButton() {
        click(loginButton);
        return this;
    }
}
