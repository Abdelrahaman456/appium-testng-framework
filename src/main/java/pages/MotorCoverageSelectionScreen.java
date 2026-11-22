package pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;

public class MotorCoverageSelectionScreen extends BasePage {

    public MotorCoverageSelectionScreen() {
        super();
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(2)), this);
    }

    @AndroidFindBy(xpath = "//*[@resource-id='btn_next_button_product_landing_page_motor' or @resource-id='btn_next_button_product_landing_page_' or @resource-id='btn_insure_now' or @content-desc='Insure now' or @text='Insure now']")
    private WebElement insureNowButton;

    public boolean waitForScreenLoaded(int timeoutSec) {
        return waitUntil(this::isInsureNowButtonVisible, timeoutSec);
    }

    public boolean isInsureNowButtonVisible() {
        try {
            By motorLandingBy = By.xpath(
                "//*[@content-desc='Motor Insurance' or @text='Motor Insurance' " +
                "or @resource-id='btn_learn_more_button_product_landing_page_motor' " +
                "or @resource-id='btn_next_button_product_landing_page_motor' " +
                "or @content-desc='Full Coverage' or @content-desc='Third-party']"
            );
            By insureNowBy = By.xpath(
                "//*[@resource-id='btn_next_button_product_landing_page_motor' " +
                "or @resource-id='btn_next_button_product_landing_page_' " +
                "or @resource-id='btn_insure_now' or @content-desc='Insure now' or @text='Insure now']"
            );
            return !findWithUnimportantViews(motorLandingBy).isEmpty() && !findWithUnimportantViews(insureNowBy).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickInsureNow() {
        System.out.println("Clicking Insure Now button via dynamic locator...");
        By locator = AppiumBy.xpath(
            "//*[@resource-id='btn_insure_now']//*[@clickable='true'] " +
            "| //android.widget.Button[@clickable='true' and (@content-desc='Insure now' or @text='Insure now')] " +
            "| //*[@resource-id='btn_next_button_product_landing_page_motor' or @resource-id='btn_next_button_product_landing_page_' or @resource-id='btn_insure_now']"
        );
        By aboutYouBy = By.xpath(
            "//*[contains(@resource-id, 'about_you') or contains(@resource-id, 'sequence_number') or @content-desc='Ownership Transfer']"
        );
        waitUntil(() -> !findWithUnimportantViews(locator).isEmpty(), 5);
        List<WebElement> candidates = findWithUnimportantViews(locator);
        if (candidates.isEmpty()) {
            scrollDown();
            candidates = findWithUnimportantViews(locator);
        }
        if (!candidates.isEmpty()) {
            tapElement(candidates.get(0));
            waitUntil(() -> !driver.findElements(aboutYouBy).isEmpty(), 5);
            return;
        }
        try {
            WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            tapElement(el);
            waitUntil(() -> !driver.findElements(aboutYouBy).isEmpty(), 5);
        } catch (Exception e) {
            List<WebElement> buttons = findWithUnimportantViews(By.xpath("//*[@content-desc='Insure now' or @text='Insure now' or @resource-id='btn_insure_now']"));
            if (!buttons.isEmpty()) {
                tapElement(buttons.get(0));
                waitUntil(() -> !driver.findElements(aboutYouBy).isEmpty(), 5);
            }
        }
    }
}
