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

    /**
     * Page Load Validation: Confirms this screen is fully loaded before interacting.
     * Fails fast with a meaningful error instead of cryptic NoSuchElementException.
     */
    public boolean isLoaded() {
        boolean loaded = waitUntil(() -> isElementVisible(readyToRollHeader) || isElementVisible(viewPolicyButton),
                utils.TestConfig.policyTimeout());
        if (!loaded) System.out.println("[PageValidation] PolicyConfirmationScreen did NOT load within " + utils.TestConfig.policyTimeout() + "s!");
        return loaded;
    }

    public boolean isPolicyConfirmed() {
        try {
            System.out.println("Verifying Policy Confirmation Screen elements...");
            return isLoaded();
        } catch (Exception e) {
            System.out.println("Policy confirmation screen element not detected: " + e.getMessage());
            return false;
        }
    }

    public void clickViewPolicy() {
        click(viewPolicyButton);
    }
}
