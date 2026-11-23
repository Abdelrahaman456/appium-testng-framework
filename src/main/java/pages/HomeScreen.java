package pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;

public class HomeScreen extends BasePage {

    public HomeScreen() {
        super();
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(2)), this);
    }

    @AndroidFindBy(xpath = "//*[@resource-id='btn_home_product_motor' or contains(@content-desc, 'Motor')]")
    private WebElement motorProductButton;

    @AndroidFindBy(xpath = "//*[@resource-id='btn_home_product_individualHealthInsurance' or contains(@content-desc, 'Individual\nHealth Insurance')]")
    private WebElement healthInsuranceButton;

    @AndroidFindBy(xpath = "//*[@resource-id='btn_home_product_familyHealth' or contains(@content-desc, 'Family\nHealth Insurance')]")
    private WebElement familyHealthButton;

    @AndroidFindBy(xpath = "//*[@resource-id='btn_home_product_travel' or contains(@content-desc, 'Travel')]")
    private WebElement travelButton;

    @AndroidFindBy(xpath = "//*[@resource-id='btn_home_product_homeInsurance' or (contains(@content-desc, 'Home') and contains(@content-desc, 'Insurance'))]")
    private WebElement homeInsuranceButton;

    @AndroidFindBy(xpath = "//*[@resource-id='btn_home_product_domesticWorker' or contains(@content-desc, 'Domestic Worker')]")
    private WebElement domesticWorkerButton;

    @AndroidFindBy(xpath = "//*[@resource-id='btn_home_product_medical' or contains(@content-desc, 'Malpractice')]")
    private WebElement malpracticeButton;

    @AndroidFindBy(xpath = "//*[@resource-id='btn_home_product_income' or contains(@content-desc, 'Income')]")
    private WebElement incomeButton;

    @AndroidFindBy(xpath = "//*[@resource-id='btn_home_product_pet' or contains(@content-desc, 'Pet')]")
    private WebElement petButton;

    @AndroidFindBy(accessibility = "Login")
    private WebElement loginButton;

    private void clickElReliably(WebElement el) {
        try {
            tapElement(el);
        } catch (Exception e) {
            try {
                el.click();
            } catch (Exception ignore) {}
        }
    }

    private void clickProductWithScroll(WebElement element, String productName, By fallbackLocator) {
        System.out.println("Navigating to " + productName + " Product via dynamic locator...");
        By homeSignals = By.xpath("//*[@resource-id='btn_home_product_motor']");
        try {
            if (!driver.findElements(By.xpath("//*[@package='com.google.android.apps.nexuslauncher']")).isEmpty()
                    && driver instanceof io.appium.java_client.InteractsWithApps) {
                String pkg = utils.TestConfig.appPackage();
                System.out.println("[HomeScreen] Launcher detected; activating " + pkg + " and waiting for HomeScreen...");
                ((io.appium.java_client.InteractsWithApps) driver).activateApp(pkg);
                waitUntil(() -> !driver.findElements(By.xpath(
                    "//*[@resource-id='btn_home_product_motor' or @content-desc='Home' or @content-desc='All products' or @content-desc='Individual']"
                )).isEmpty(), 15);
            }
            if (isElementVisible(element) || isElementVisible(fallbackLocator)) {
                WebElement el = isElementVisible(element) ? element : getVisibleElement(fallbackLocator);
                clickElReliably(el);
                try { Thread.sleep(2000); } catch (Exception ignore) {}
                if (driver.findElements(homeSignals).isEmpty()) {
                    return;
                }
            }
            By allProductsTabBy = By.xpath("//*[@content-desc='All products' or @text='All products']");
            java.util.List<WebElement> allTabs = driver.findElements(allProductsTabBy);
            if (!allTabs.isEmpty() && allTabs.get(0).isDisplayed()) {
                clickElReliably(allTabs.get(0));
                try { Thread.sleep(1000); } catch (Exception ignore) {}
            }
            if (isElementVisible(element) || isElementVisible(fallbackLocator)) {
                WebElement el = isElementVisible(element) ? element : getVisibleElement(fallbackLocator);
                clickElReliably(el);
                try { Thread.sleep(2000); } catch (Exception ignore) {}
                if (driver.findElements(homeSignals).isEmpty()) {
                    return;
                }
            }
            By homeTabBy = By.xpath("//*[@content-desc='Home' or @text='Home']");
            java.util.List<WebElement> homeTabs = driver.findElements(homeTabBy);
            if (!homeTabs.isEmpty() && homeTabs.get(0).isDisplayed()) {
                clickElReliably(homeTabs.get(0));
                try { Thread.sleep(1000); } catch (Exception ignore) {}
            }
            scrollUp();
            if (isElementVisible(element) || isElementVisible(fallbackLocator)) {
                WebElement el = isElementVisible(element) ? element : getVisibleElement(fallbackLocator);
                clickElReliably(el);
                try { Thread.sleep(2000); } catch (Exception ignore) {}
                if (driver.findElements(homeSignals).isEmpty()) {
                    return;
                }
            }
        } catch (Exception ignore) {}
        for (int i = 0; i < 3; i++) {
            try {
                if (isElementVisible(element)) {
                    clickElReliably(element);
                    try { Thread.sleep(2000); } catch (Exception ignore) {}
                    if (driver.findElements(homeSignals).isEmpty()) {
                        return;
                    }
                }
            } catch (Exception ignore) {}
            try {
                java.util.List<WebElement> els = driver.findElements(fallbackLocator);
                if (!els.isEmpty() && els.get(0).isDisplayed()) {
                    clickElReliably(els.get(0));
                    try { Thread.sleep(2000); } catch (Exception ignore) {}
                    if (driver.findElements(homeSignals).isEmpty()) {
                        return;
                    }
                }
            } catch (Exception ignore) {}
            scrollDown();
            try { Thread.sleep(800); } catch (Exception ignore) {}
        }
        try {
            waitForVisibility(element);
            clickElReliably(element);
        } catch (Exception e) {
            WebElement fallback = getVisibleElement(fallbackLocator);
            clickElReliably(fallback);
        }
        try { Thread.sleep(2000); } catch (Exception ignore) {}
    }

    public HomeScreen clickMotorProduct() {
        clickProductWithScroll(motorProductButton, "Motor",
                By.xpath("//*[@resource-id='btn_home_product_motor' or contains(@content-desc, 'Motor')]"));
        return this;
    }

    public HomeScreen clickHealthInsuranceProduct() {
        clickProductWithScroll(healthInsuranceButton, "Individual Health",
                By.xpath("//*[@resource-id='btn_home_product_individualHealthInsurance' or contains(@content-desc, 'Individual')]"));
        return this;
    }

    public HomeScreen clickFamilyHealthProduct() {
        clickProductWithScroll(familyHealthButton, "Family Health",
                By.xpath("//*[@resource-id='btn_home_product_familyHealth' or contains(@content-desc, 'Family')]"));
        return this;
    }

    public HomeScreen clickTravelProduct() {
        clickProductWithScroll(travelButton, "Travel",
                By.xpath("//*[@resource-id='btn_home_product_travel' or contains(@content-desc, 'Travel')]"));
        return this;
    }

    public HomeScreen clickHomeInsuranceProduct() {
        clickProductWithScroll(homeInsuranceButton, "Home Insurance",
                By.xpath("//*[@resource-id='btn_home_product_homeInsurance' or (contains(@content-desc, 'Home') and contains(@content-desc, 'Insurance'))]"));
        return this;
    }

    public HomeScreen clickDomesticWorkerProduct() {
        clickProductWithScroll(domesticWorkerButton, "Domestic Worker",
                By.xpath("//*[@resource-id='btn_home_product_domesticWorker' or contains(@content-desc, 'Domestic')]"));
        return this;
    }

    public HomeScreen clickMalpracticeProduct() {
        clickProductWithScroll(malpracticeButton, "Malpractice",
                By.xpath("//*[@resource-id='btn_home_product_medical' or contains(@content-desc, 'Malpractice')]"));
        return this;
    }

    public HomeScreen clickIncomeProduct() {
        clickProductWithScroll(incomeButton, "Income Protection",
                By.xpath("//*[@resource-id='btn_home_product_income' or contains(@content-desc, 'Income')]"));
        return this;
    }

    public HomeScreen clickPetProduct() {
        clickProductWithScroll(petButton, "Pet",
                By.xpath("//*[@resource-id='btn_home_product_pet' or contains(@content-desc, 'Pet')]"));
        return this;
    }

    public HomeScreen clickLoginButton() {
        click(loginButton);
        return this;
    }
}
