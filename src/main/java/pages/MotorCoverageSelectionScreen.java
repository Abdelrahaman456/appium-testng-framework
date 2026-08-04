package pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

public class MotorCoverageSelectionScreen extends BasePage {

    public MotorCoverageSelectionScreen() {
        super();
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(0)), this);
    }

    @AndroidFindBy(accessibility = "Insure now")
    private WebElement insureNowButton;

    public boolean isInsureNowButtonVisible() {
        try {
            waitForVisibility(insureNowButton);
            return insureNowButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickInsureNow() {
        click(insureNowButton);
    }
}
