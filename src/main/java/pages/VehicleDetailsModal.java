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

    @AndroidFindBy(xpath = "//android.widget.Button[@content-desc='Proceed' or @text='Proceed']")
    private WebElement proceedButton;

    // Locators used dynamically to avoid stale references after modal re-render
    private static final org.openqa.selenium.By CAR_MAKE_FIELD   = org.openqa.selenium.By.xpath("(//android.widget.EditText)[1]");
    private static final org.openqa.selenium.By CAR_MODEL_FIELD  = org.openqa.selenium.By.xpath("(//android.widget.EditText)[2]");
    private static final org.openqa.selenium.By BMW_RESULT       = org.openqa.selenium.By.xpath(
        "//*[contains(@content-desc,'B.M.W') or contains(@text,'B.M.W') or contains(@content-desc,'BMW') or contains(@text,'BMW')]"
    );
    private static final org.openqa.selenium.By DROPDOWN_ITEM_1 = org.openqa.selenium.By.xpath(
        "(//android.widget.ScrollView//android.view.View[@clickable='true'] | " +
        "//android.widget.ScrollView//android.widget.TextView | " +
        "//android.widget.ListView//android.view.View | " +
        "//android.view.View[@clickable='true' and not(@content-desc='Scrim') and not(contains(@content-desc,'details')) and not(contains(@content-desc,'Proceed'))])[1]"
    );

    public boolean isModalDisplayed() {
        try {
            System.out.println("Checking for 'We need more details about your vehicle' modal...");
            return wait.until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf(modalHeader)) != null;
        } catch (Exception e) {
            System.out.println("Vehicle details modal did not appear: " + e.getMessage());
            return false;
        }
    }

    /**
     * Searches for a car make and clicks the first result.
     * Returns true if the result was clicked successfully.
     */
    private boolean attemptSelectCarMake(String searchKeyword) {
        try {
            WebElement makeField = getVisibleElement(CAR_MAKE_FIELD);
            makeField.click();
            try { Thread.sleep(800); } catch (Exception e) {}
            makeField.clear();
            makeField.sendKeys(searchKeyword);
            try { Thread.sleep(1500); } catch (Exception e) {}

            // Try to click BMW result
            try {
                getVisibleElement(BMW_RESULT).click();
                System.out.println("[CarMake] B.M.W selected via direct locator.");
                return true;
            } catch (Exception e) {
                // Fallback: click first dropdown item
                try {
                    getVisibleElement(DROPDOWN_ITEM_1).click();
                    System.out.println("[CarMake] First dropdown item selected as fallback.");
                    return true;
                } catch (Exception ex) {
                    // Last resort: tap below the make field
                    org.openqa.selenium.Rectangle rect = makeField.getRect();
                    tapCoordinates(rect.getX() + (rect.getWidth() / 2), rect.getY() + rect.getHeight() + 100);
                    System.out.println("[CarMake] Tapped below car make field as last resort.");
                    return false;
                }
            }
        } catch (Exception e) {
            System.out.println("[CarMake] attemptSelectCarMake failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Clicks the car model field and selects the first option.
     * Returns true if model was selected successfully.
     */
    private boolean attemptSelectCarModel() {
        try {
            // Wait up to 5 seconds for Car Model field to appear after make selection
            boolean modelFieldVisible = waitUntil(() -> isElementVisible(CAR_MODEL_FIELD), 5);
            if (!modelFieldVisible) {
                System.out.println("[CarModel] Car Model field not visible after 5s.");
                return false;
            }

            WebElement modelField = getVisibleElement(CAR_MODEL_FIELD);
            modelField.click();
            try { Thread.sleep(1500); } catch (Exception e) {}

            // Try clicking first dropdown item
            try {
                getVisibleElement(DROPDOWN_ITEM_1).click();
                System.out.println("[CarModel] First car model option selected.");
                return true;
            } catch (Exception e) {
                // Fallback: tap below model field
                org.openqa.selenium.Rectangle rect = modelField.getRect();
                tapCoordinates(rect.getX() + (rect.getWidth() / 2), rect.getY() + rect.getHeight() + 120);
                System.out.println("[CarModel] Tapped below car model field as fallback.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("[CarModel] attemptSelectCarModel failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Full retry-enabled car make + model selection.
     * If the popup closes after selecting make, reopens car make and retries up to MAX_RETRIES times.
     */
    public void searchAndSelectCarMakeAndModel(String searchKeyword) {
        final int MAX_RETRIES = 3;

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            System.out.println("\n[VehicleDetails] Attempt " + attempt + "/" + MAX_RETRIES + ": Selecting Car Make '" + searchKeyword + "'...");

            // Step 1: Select car make
            attemptSelectCarMake(searchKeyword);
            try { Thread.sleep(1000); } catch (Exception e) {}

            // Step 2: Check if Car Model field is visible (modal is still open)
            boolean modelVisible = waitUntil(() -> isElementVisible(CAR_MODEL_FIELD), 4);

            if (modelVisible) {
                System.out.println("[VehicleDetails] Modal still open after car make selection. Selecting Car Model...");
                boolean modelSelected = attemptSelectCarModel();
                if (modelSelected) {
                    System.out.println("[VehicleDetails] ✅ Car Make & Model selected successfully on attempt " + attempt + "!");
                    try { Thread.sleep(1000); } catch (Exception e) {}
                    return; // SUCCESS — exit the retry loop
                }
            } else {
                System.out.println("[VehicleDetails] ⚠️ Modal closed after car make click! Retrying...");
                // Wait a moment then check if modal is still on screen
                try { Thread.sleep(1500); } catch (Exception e) {}

                // If modal is still open but model field not found, scroll down to reveal it
                if (isElementVisible(CAR_MAKE_FIELD)) {
                    System.out.println("[VehicleDetails] Modal still open, scrolling to reveal Car Model field...");
                    scrollDown();
                    boolean modelAfterScroll = waitUntil(() -> isElementVisible(CAR_MODEL_FIELD), 3);
                    if (modelAfterScroll) {
                        attemptSelectCarModel();
                        return;
                    }
                }
            }

            if (attempt < MAX_RETRIES) {
                System.out.println("[VehicleDetails] Retrying in 2 seconds...");
                try { Thread.sleep(2000); } catch (Exception e) {}
            }
        }

        System.out.println("[VehicleDetails] ⚠️ All " + MAX_RETRIES + " attempts exhausted. Proceeding anyway...");
    }

    public void clickProceed() {
        System.out.println("Clicking Proceed button on Vehicle Details modal...");
        for (int i = 0; i < 2; i++) {
            try {
                if (proceedButton.isDisplayed()) break;
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
            searchAndSelectCarMakeAndModel("b.m.w");
            clickProceed();
        } else {
            System.out.println("Vehicle details modal not required. Skipping...");
        }
    }
}
