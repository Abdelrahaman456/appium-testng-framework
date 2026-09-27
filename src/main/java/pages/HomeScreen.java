package pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;

public class HomeScreen extends BasePage {

    public HomeScreen() {
        super();
        // Set decorator timeout to 0 so explicit wait handles polling efficiently without double-waiting
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(0)), this);
    }

    // Using uiAutomator instead of XPath! Native uiAutomator searches the screen in milliseconds.
    @CacheLookup
    @AndroidFindBy(uiAutomator = "new UiSelector().resourceId(\"btn_home_product_motor\")")
    private WebElement motorProductButton;

    @AndroidFindBy(uiAutomator = "new UiSelector().resourceId(\"btn_home_product_individualHealthInsurance\")")
    private WebElement healthInsuranceButton;

    @AndroidFindBy(xpath = "//*[@resource-id='btn_home_product_income']")
    private WebElement incomeButton;

    @AndroidFindBy(xpath = "//*[@resource-id='btn_home_product_pet']")
    private WebElement petButton;

    @AndroidFindBy(xpath = "//android.widget.Button[contains(@content-desc, 'Travel')]")
    private WebElement travelButton;

    @AndroidFindBy(accessibility = "Login")
    private WebElement loginButton;

    public HomeScreen clickMotorProduct() {
        System.out.println("Waiting 2 seconds for Home Page to fully load and settle...");
        try { Thread.sleep(2000); } catch (Exception e) {}
        
        click(motorProductButton);
        return this;
    }

    public HomeScreen clickTravelProduct() {
        System.out.println("Waiting 2 seconds for Home Page to fully load and settle...");
        try { Thread.sleep(2000); } catch (Exception e) {}
        
        System.out.println("Clicking Travel Product icon...");
        click(travelButton);
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
