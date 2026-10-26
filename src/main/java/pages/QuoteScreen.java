package pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;

public class QuoteScreen extends BasePage {

    public QuoteScreen() {
        super();
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(10)), this);
    }

    // Placeholder locators based on the screenshot provided by the user.
    // Update these with accurate resource-ids once you inspect the XML in Appium.
    
    @AndroidFindBy(xpath = "//*[@content-desc=\"Here's your quote\"]")
    private WebElement pageHeader;

    @AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, 'Smart Cover')]")
    private WebElement smartCoverOption;

    @AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, 'Saver Cover')]")
    private WebElement saverCoverOption;

    @AndroidFindBy(xpath = "//android.view.View[contains(@content-desc, 'Comprehensive Cover')]")
    private WebElement comprehensiveCoverOption;

    @AndroidFindBy(xpath = "//*[contains(@text, 'Politically Exposed Person')]")
    private WebElement pepQuestionText;

    @AndroidFindBy(xpath = "//*[contains(@text, 'related party')]")
    private WebElement relatedPartyQuestionText;

    // These buttons likely have a standard ID we can map once we inspect them
    // E.g., Yes/No toggle for PEP
    @AndroidFindBy(xpath = "(//*[@text='Yes'])[1]")
    private WebElement pepYesButton;

    @AndroidFindBy(xpath = "(//*[@text='No'])[1]")
    private WebElement pepNoButton;

    @AndroidFindBy(xpath = "(//*[@text='Yes'])[2]")
    private WebElement relatedPartyYesButton;

    @AndroidFindBy(xpath = "(//*[@text='No'])[2]")
    private WebElement relatedPartyNoButton;

    @AndroidFindBy(xpath = "//android.widget.Button[@content-desc='Buy now']")
    private WebElement buyNowButton;

    @AndroidFindBy(xpath = "//android.widget.Button[@content-desc='Skip']")
    private WebElement skipUpsellButton;

    public boolean isPageLoaded() {
        try {
            org.openqa.selenium.support.ui.WebDriverWait longWait = new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(30));
            return longWait.until(org.openqa.selenium.support.ui.ExpectedConditions.or(
                org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf(pageHeader),
                org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf(smartCoverOption),
                org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf(comprehensiveCoverOption)
            )) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public void selectSmartCover() {
        // Use physical tapElement instead of standard click to guarantee React Native registers the touch
        waitForVisibility(smartCoverOption);
        tapElement(smartCoverOption);
    }

    public void selectSaverCover() {
        waitForVisibility(saverCoverOption);
        tapElement(saverCoverOption);
    }

    public void selectComprehensiveCover() {
        waitForVisibility(comprehensiveCoverOption);
        tapElement(comprehensiveCoverOption);
    }

    public void answerPepQuestion(boolean isYes) {
        if (isYes) {
            click(pepYesButton);
        } else {
            click(pepNoButton);
        }
    }

    public void answerRelatedPartyQuestion(boolean isYes) {
        if (isYes) {
            click(relatedPartyYesButton);
        } else {
            click(relatedPartyNoButton);
        }
    }

    public void clickBuyNow() {
        System.out.println("Scrolling down to reveal Buy Now button...");
        // Comprehensive Cover is open by default and very long, so scroll down until Buy Now button is visible
        for (int i = 0; i < 3; i++) {
            try {
                if (buyNowButton.isDisplayed()) {
                    System.out.println("Buy Now button is visible!");
                    break;
                }
            } catch (Exception e) {}
            System.out.println("Scrolling down (attempt " + (i + 1) + ")...");
            scrollDown();
            try { Thread.sleep(1000); } catch (Exception e) {}
        }
        click(buyNowButton);
    }

    public void clickSkipUpsell() {
        System.out.println("Waiting for Upsell popup...");
        try {
            wait.until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf(skipUpsellButton));
            System.out.println("Upsell popup found. Clicking Skip...");
            click(skipUpsellButton);
        } catch (Exception e) {
            System.out.println("No Upsell popup appeared. Proceeding...");
        }
    }
}
