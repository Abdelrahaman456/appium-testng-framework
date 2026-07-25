package pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;

public class SampleScreen extends BasePage {

    public SampleScreen() {
        super();
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(10)), this);
    }

    @AndroidFindBy(accessibility = "Login Button")
    private WebElement loginButton;

    @AndroidFindBy(xpath = "//android.widget.EditText[@content-desc='Username Input']")
    private WebElement usernameInput;

    public SampleScreen enterUsername(String username) {
        sendKeys(usernameInput, username);
        return this;
    }

    public SampleScreen clickLogin() {
        click(loginButton);
        return this;
    }

    public boolean isLoginButtonDisplayed() {
        try {
            waitForVisibility(loginButton);
            return loginButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
