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

    @AndroidFindBy(xpath = "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.widget.FrameLayout/android.view.View/android.view.View/android.view.View/android.view.View/android.widget.ImageView[1]")
    private WebElement closeHeaderButton;

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

    /**
     * Clicks top-right Close (✕) icon on Policy Confirmation screen to route back to Home Landing Page.
     * Keeps driver session alive and enables continuous fast test case execution!
     */
    public void clickCloseToHome() {
        System.out.println("Clicking Close (✕) icon on Policy Confirmation screen to return to Home Landing Page...");
        try {
            click(closeHeaderButton);
        } catch (Exception e) {
            System.out.println("Standard click on Close icon failed, attempting fallback locator/tap...");
            try {
                WebElement fallbackClose = getVisibleElement(org.openqa.selenium.By.xpath("(//android.widget.ImageView)[1]"));
                tapElement(fallbackClose);
            } catch (Exception ex) {
                tapCoordinates(993, 198); // Physical tap coordinates from Inspector screenshot
            }
        }
        try { Thread.sleep(2000); } catch (Exception e) {} // Wait for Home screen to load
    }
}
