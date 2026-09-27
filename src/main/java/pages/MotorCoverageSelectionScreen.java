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

    @AndroidFindBy(xpath = "//*[contains(@content-desc, 'Insure') or contains(@text, 'Insure') or contains(@content-desc, 'insure') or contains(@text, 'insure')]")
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
        System.out.println("Waiting 2 seconds for Travel screen to load...");
        try { Thread.sleep(2000); } catch (Exception e) {}
        
        System.out.println("Clicking Insure Now button...");
        try {
            // Wait for it to appear
            waitForVisibility(insureNowButton);
            
            // React Native often ignores standard .click() if the listener is on a parent View.
            // Using tapElement physically taps the exact screen coordinates.
            tapElement(insureNowButton);
        } catch (Exception e) {
            System.out.println("Standard locator failed. Searching entire DOM for 'Insure now'...");
            try {
                org.openqa.selenium.By fallbackLocator = AppiumBy.xpath("//*[@content-desc='Insure now' or @resource-id='btn_next_button_product_landing_page_']");
                WebElement fallbackBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(fallbackLocator));
                System.out.println("Found Insure Now via fallback! Tapping coordinates...");
                tapElement(fallbackBtn);
            } catch (Exception ex) {
                System.out.println("CRITICAL: Could not find Insure Now anywhere on screen!");
                throw ex;
            }
        }
    }
}
