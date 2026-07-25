package pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;

public class VehicleDetailsModal extends BasePage {

    public VehicleDetailsModal() {
        super();
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(10)), this);
    }

    @AndroidFindBy(xpath = "//*[contains(@content-desc, 'We need more details about your vehicle') or contains(@text, 'We need more details about your vehicle')]")
    private WebElement modalHeader;

    // First EditText is Car Make, Second is Car Model
    @AndroidFindBy(xpath = "(//android.widget.EditText)[1]")
    private WebElement carMakeField;

    @AndroidFindBy(xpath = "(//android.widget.EditText)[2]")
    private WebElement carModelField;

    @AndroidFindBy(xpath = "//android.widget.Button[@content-desc='Proceed' or @text='Proceed']")
    private WebElement proceedButton;

    public boolean isModalDisplayed() {
        try {
            System.out.println("Checking for 'We need more details about your vehicle' modal...");
            return wait.until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf(modalHeader)) != null;
        } catch (Exception e) {
            System.out.println("Vehicle details modal did not appear: " + e.getMessage());
            return false;
        }
    }

    public void searchAndSelectCarMake(String searchKeyword) {
        System.out.println("Clicking Car Make field...");
        click(carMakeField);
        try { Thread.sleep(1000); } catch (Exception e) {}
        
        System.out.println("Typing '" + searchKeyword + "' into Car Make field...");
        carMakeField.click();
        carMakeField.clear();
        new org.openqa.selenium.interactions.Actions(driver)
            .sendKeys(searchKeyword)
            .perform();
            
        try { Thread.sleep(1500); } catch (Exception e) {}
        
        System.out.println("Selecting first search result for '" + searchKeyword + "'...");
        try {
            org.openqa.selenium.By resultLocator = org.openqa.selenium.By.xpath(
                "//*[contains(@content-desc, 'B.M.W') or contains(@text, 'B.M.W') or contains(@content-desc, 'BMW') or contains(@text, 'BMW')]"
            );
            getVisibleElement(resultLocator).click();
            System.out.println("Car Make 'B.M.W.' selected!");
        } catch (Exception e) {
            System.out.println("Direct B.M.W search element not clicked via XPath, attempting fallback tap on 1st dropdown item...");
            try {
                org.openqa.selenium.By optionLocator = org.openqa.selenium.By.xpath(
                    "(//android.view.View[@clickable='true' and not(@content-desc='Scrim') and not(contains(@content-desc, 'details')) and not(contains(@content-desc, 'Proceed'))] | //android.widget.TextView)[1]"
                );
                getVisibleElement(optionLocator).click();
            } catch (Exception ex) {
                org.openqa.selenium.Rectangle rect = carMakeField.getRect();
                tapCoordinates(rect.getX() + (rect.getWidth() / 2), rect.getY() + rect.getHeight() + 100);
            }
        }
        try { Thread.sleep(1500); } catch (Exception e) {}
    }

    public void selectCarModel(int optionIndex) {
        System.out.println("Clicking Car Model field...");
        click(carModelField);
        try { Thread.sleep(1500); } catch (Exception e) {}
        
        System.out.println("Selecting Car Model option #" + optionIndex + "...");
        try {
            org.openqa.selenium.By optionLocator = org.openqa.selenium.By.xpath(
                "(//android.widget.ScrollView//android.view.View[@clickable='true'] | " +
                "//android.widget.ScrollView//android.widget.TextView | " +
                "//android.widget.ListView//android.view.View | " +
                "//android.view.View[@clickable='true' and not(@content-desc='Scrim') and not(contains(@content-desc, 'details')) and not(contains(@content-desc, 'Proceed'))])[" + optionIndex + "]"
            );
            getVisibleElement(optionLocator).click();
            System.out.println("Car Model option #" + optionIndex + " selected!");
        } catch (Exception e) {
            System.out.println("Fallback: Tapping dropdown area below Car Model...");
            org.openqa.selenium.Rectangle rect = carModelField.getRect();
            tapCoordinates(rect.getX() + (rect.getWidth() / 2), rect.getY() + rect.getHeight() + 120);
        }
        try { Thread.sleep(1500); } catch (Exception e) {}
    }

    public void clickProceed() {
        System.out.println("Clicking Proceed button on Vehicle Details modal...");
        for (int i = 0; i < 2; i++) {
            try {
                if (proceedButton.isDisplayed()) {
                    break;
                }
            } catch (Exception e) {}
            System.out.println("Scrolling down to reveal Proceed button...");
            scrollDown();
            try { Thread.sleep(800); } catch (Exception e) {}
        }
        try {
            click(proceedButton);
        } catch (Exception e) {
            System.out.println("Standard click on Proceed failed, attempting physical tap...");
            tapElement(proceedButton);
        }
        try { Thread.sleep(3000); } catch (Exception e) {} // Wait for navigation to Quote Screen
    }

    public void handleVehicleDetailsIfPresent() {
        if (isModalDisplayed()) {
            System.out.println("Vehicle details required. Searching Car Make ('b.m.w') & selecting 1st Car Model option...");
            searchAndSelectCarMake("b.m.w");
            selectCarModel(1);
            clickProceed();
        } else {
            System.out.println("Vehicle details modal not required. Skipping...");
        }
    }
}
