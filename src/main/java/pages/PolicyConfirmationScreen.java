package pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;

public class PolicyConfirmationScreen extends BasePage {

    public PolicyConfirmationScreen() {
        super();
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(10)), this);
    }

    @AndroidFindBy(xpath = "//*[contains(@text, \"ready to roll\") or contains(@content-desc, \"ready to roll\") or contains(@text, \"All set!\") or contains(@content-desc, \"All set!\")]")
    private WebElement readyToRollHeader;

    @AndroidFindBy(xpath = "//*[contains(@text, \"Policy number\") or contains(@content-desc, \"Policy number\")]")
    private WebElement policyNumberLabel;

    @AndroidFindBy(xpath = "//*[contains(@content-desc, 'View Policy') or contains(@text, 'View Policy')]")
    private WebElement viewPolicyButton;

    public boolean isPolicyConfirmed() {
        try {
            System.out.println("Verifying Policy Confirmation Screen elements...");
            org.openqa.selenium.support.ui.WebDriverWait longWait = new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(30));
            return longWait.until(org.openqa.selenium.support.ui.ExpectedConditions.or(
                org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf(readyToRollHeader),
                org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf(viewPolicyButton)
            )) != null;
        } catch (Exception e) {
            System.out.println("Policy confirmation screen element not detected: " + e.getMessage());
            return false;
        }
    }

    public void clickViewPolicy() {
        click(viewPolicyButton);
    }
}
