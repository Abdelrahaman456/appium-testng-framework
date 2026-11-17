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
            return !driver.findElements(motorLandingBy).isEmpty() && !driver.findElements(insureNowBy).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickInsureNow() {
        System.out.println("Waiting 1 second for coverage landing screen to load...");
        try { Thread.sleep(1000); } catch (Exception e) {}

        System.out.println("Clicking Insure Now button via dynamic locator...");
        try {
            By locator = AppiumBy.xpath(
                "//*[@resource-id='btn_insure_now']//*[@clickable='true'] " +
                "| //android.widget.Button[@clickable='true' and (@content-desc='Insure now' or @text='Insure now')] " +
                "| //*[@resource-id='btn_next_button_product_landing_page_motor' or @resource-id='btn_next_button_product_landing_page_' or @resource-id='btn_insure_now']"
            );
            WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            tapElement(el);
        } catch (Exception e) {
            List<WebElement> buttons = driver.findElements(By.xpath("//*[@content-desc='Insure now' or @text='Insure now' or @resource-id='btn_insure_now']"));
            if (!buttons.isEmpty()) {
                tapElement(buttons.get(0));
            }
        }
        try { Thread.sleep(1000); } catch (Exception e) {}
    }
}
