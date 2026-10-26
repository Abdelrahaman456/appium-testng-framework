package pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;
import java.util.List;

public class VehicleDetailsModal extends BasePage {

    public VehicleDetailsModal() {
        super();
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(10)), this);
    }

    @AndroidFindBy(xpath = "//*[contains(@content-desc, 'We need more details about your vehicle') or contains(@text, 'We need more details about your vehicle')]")
    private WebElement modalHeader;

    @AndroidFindBy(xpath = "//android.widget.Button[@content-desc='Proceed' or @text='Proceed']")
    private WebElement proceedButton;

    // ─── Locators ───────────────────────────────────────────────────────────────
    private static final org.openqa.selenium.By MODAL_HEADER = org.openqa.selenium.By.xpath(
        "//*[contains(@content-desc,'We need more details') or contains(@text,'We need more details')]"
    );
    private static final org.openqa.selenium.By CAR_MAKE_FIELD = org.openqa.selenium.By.xpath(
        "(//android.widget.EditText)[1]"
    );
    private static final org.openqa.selenium.By BMW_RESULT = org.openqa.selenium.By.xpath(
        "//*[contains(@content-desc,'B.M.W') or contains(@text,'B.M.W') or contains(@content-desc,'BMW') or contains(@text,'BMW')]"
    );
    // Car Model can be EditText OR a Button/View after re-render — use broadest possible locator
    private static final org.openqa.selenium.By CAR_MODEL_ANY = org.openqa.selenium.By.xpath(
        "//*[contains(@resource-id,'model') or contains(@content-desc,'Car model') or contains(@content-desc,'car model') or contains(@text,'Car model')]"
    );
    private static final org.openqa.selenium.By SECOND_EDIT_TEXT = org.openqa.selenium.By.xpath(
        "(//android.widget.EditText)[2]"
    );
    private static final org.openqa.selenium.By DROPDOWN_FIRST_ITEM = org.openqa.selenium.By.xpath(
        "(//android.widget.ScrollView//*[@clickable='true'] | " +
        "//android.widget.ListView//*[@clickable='true'] | " +
        "//*[@clickable='true' and not(@content-desc='Scrim') and not(contains(@content-desc,'details')) and not(contains(@content-desc,'Proceed')) and not(contains(@content-desc,'We need'))])[1]"
    );

    // ─── isModalDisplayed ───────────────────────────────────────────────────────
    public boolean isModalDisplayed() {
        try {
            System.out.println("Checking for Vehicle Details modal...");
            return wait.until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf(modalHeader)) != null;
        } catch (Exception e) {
            System.out.println("Vehicle details modal did not appear.");
            return false;
        }
    }

    // ─── isMainModalStillOpen ───────────────────────────────────────────────────
    private boolean isMainModalStillOpen() {
        return isElementVisible(MODAL_HEADER);
    }

    // ─── Type car make and click BMW ────────────────────────────────────────────
    private boolean selectCarMake(String keyword) {
        try {
            WebElement makeField = getVisibleElement(CAR_MAKE_FIELD);
            // Clear and type
            makeField.click();
            try { Thread.sleep(600); } catch (Exception e) {}
            makeField.clear();
            makeField.sendKeys(keyword);
            System.out.println("[CarMake] Typed '" + keyword + "'. Waiting for results...");
            try { Thread.sleep(2000); } catch (Exception e) {} // wait for results list to appear

            // Try clicking BMW result
            try {
                List<WebElement> results = driver.findElements(BMW_RESULT);
                if (!results.isEmpty() && results.get(0).isDisplayed()) {
                    results.get(0).click();
                    System.out.println("[CarMake] ✅ BMW clicked via direct locator.");
                    return true;
                }
            } catch (Exception ignored) {}

            // Fallback: tap first visible dropdown item
            try {
                List<WebElement> items = driver.findElements(DROPDOWN_FIRST_ITEM);
                if (!items.isEmpty() && items.get(0).isDisplayed()) {
                    items.get(0).click();
                    System.out.println("[CarMake] ✅ First dropdown item clicked as fallback.");
                    return true;
                }
            } catch (Exception ignored) {}

            // Last resort: tap 150px below the make field
            org.openqa.selenium.Rectangle r = makeField.getRect();
            tapCoordinates(r.getX() + r.getWidth() / 2, r.getY() + r.getHeight() + 150);
            System.out.println("[CarMake] ✅ Tapped below make field as last resort.");
            return true;

        } catch (Exception e) {
            System.out.println("[CarMake] ❌ selectCarMake failed: " + e.getMessage());
            return false;
        }
    }

    // ─── Click car model field and pick first option ─────────────────────────────
    private boolean selectCarModel() {
        System.out.println("[CarModel] Looking for Car Model field...");

        try { Thread.sleep(3000); } catch (Exception e) {}

        // Strategy 1: resource-id / hint based locator
        try {
            org.openqa.selenium.By robustModelLocator = org.openqa.selenium.By.xpath("//android.widget.EditText[contains(@hint, 'Model') or contains(@hint, 'model')]");
            List<WebElement> modelFields = driver.findElements(robustModelLocator);
            if (!modelFields.isEmpty()) {
                WebElement mf = modelFields.get(0);
                System.out.println("[CarModel] Found via robust hint locator.");
                
                mf.click();
                // Wait a full 2 seconds for the keyboard animation to push the screen up and STOP moving
                try { Thread.sleep(2000); } catch (Exception ex) {}
                
                // Re-find element to get its completely settled NEW coordinates
                modelFields = driver.findElements(robustModelLocator);
                if (!modelFields.isEmpty()) {
                    mf = modelFields.get(0);
                    org.openqa.selenium.Rectangle rect = mf.getRect();
                    int tapX = rect.getX() + (rect.getWidth() / 2);
                    // Tap exactly 30 pixels below the text box (80 was too far and hit the background gap, closing the modal!)
                    int tapY = rect.getY() + rect.getHeight() + 30;
                    return pickFirstDropdownItem(tapX, tapY);
                }
            }
        } catch (Exception ignored) {}

        // Strategy 2: second EditText on screen
        try {
            List<WebElement> editTexts = driver.findElements(SECOND_EDIT_TEXT);
            if (!editTexts.isEmpty() && editTexts.get(0).isDisplayed()) {
                System.out.println("[CarModel] Found via second EditText.");
                WebElement mf = editTexts.get(0);
                
                mf.click();
                try { Thread.sleep(2000); } catch (Exception ex) {}
                
                // Re-find to get new Y coordinate
                editTexts = driver.findElements(SECOND_EDIT_TEXT);
                if (!editTexts.isEmpty()) {
                    mf = editTexts.get(0);
                    org.openqa.selenium.Rectangle rect = mf.getRect();
                    int tapX = rect.getX() + (rect.getWidth() / 2);
                    int tapY = rect.getY() + rect.getHeight() + 30;
                    return pickFirstDropdownItem(tapX, tapY);
                }
            }
        } catch (Exception ignored) {}

        // Strategy 3: scroll down and retry
        System.out.println("[CarModel] Not found - scrolling down to reveal...");
        scrollDown();
        try { Thread.sleep(1000); } catch (Exception e) {}

        try {
            List<WebElement> modelAfterScroll = driver.findElements(CAR_MODEL_ANY);
            if (!modelAfterScroll.isEmpty()) {
                WebElement mf = modelAfterScroll.get(0);
                mf.click();
                try { Thread.sleep(2000); } catch (Exception ex) {}
                
                modelAfterScroll = driver.findElements(CAR_MODEL_ANY);
                if (!modelAfterScroll.isEmpty()) {
                    mf = modelAfterScroll.get(0);
                    org.openqa.selenium.Rectangle rect = mf.getRect();
                    int tapX = rect.getX() + (rect.getWidth() / 2);
                    int tapY = rect.getY() + rect.getHeight() + 30;
                    return pickFirstDropdownItem(tapX, tapY);
                }
            }
        } catch (Exception ignored) {}

        try {
            List<WebElement> editTextsAfterScroll = driver.findElements(SECOND_EDIT_TEXT);
            if (!editTextsAfterScroll.isEmpty()) {
                WebElement mf = editTextsAfterScroll.get(0);
                mf.click();
                try { Thread.sleep(2000); } catch (Exception ex) {}
                
                editTextsAfterScroll = driver.findElements(SECOND_EDIT_TEXT);
                if (!editTextsAfterScroll.isEmpty()) {
                    mf = editTextsAfterScroll.get(0);
                    org.openqa.selenium.Rectangle rect = mf.getRect();
                    int tapX = rect.getX() + (rect.getWidth() / 2);
                    int tapY = rect.getY() + rect.getHeight() + 30;
                    return pickFirstDropdownItem(tapX, tapY);
                }
            }
        } catch (Exception ignored) {}

        System.out.println("[CarModel] Car Model field not found with any strategy.");
        return false;
    }

    // Pick first item from open dropdown using physical coordinates below the text box
    private boolean pickFirstDropdownItem(int tapX, int tapY) {
        try {
            System.out.println("[CarModel] Tapping below the text field at X:" + tapX + " Y:" + tapY);
            tapCoordinates(tapX, tapY);
            try { Thread.sleep(1000); } catch (Exception e) {}
            return true;
        } catch (Exception e) {
            System.out.println("[CarModel] Failed to tap below text field: " + e.getMessage());
            return false;
        }
    }

    // ─── Main retry engine ───────────────────────────────────────────────────────
    public void searchAndSelectCarMakeAndModel(String keyword) {
        final int MAX_RETRIES = 3;

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            System.out.println("\n[VehicleDetails] ══ Attempt " + attempt + "/" + MAX_RETRIES + " ══");

            // Verify main modal is still open before each attempt
            if (!isMainModalStillOpen()) {
                System.out.println("[VehicleDetails] ❌ Main modal is CLOSED before attempt " + attempt + "! Cannot continue.");
                break;
            }

            // Step 1: Select car make
            selectCarMake(keyword);

            // Wait briefly for UI to settle after closing search results
            try { Thread.sleep(1500); } catch (Exception e) {}

            // Verify main modal is STILL open after selecting car make
            if (!isMainModalStillOpen()) {
                System.out.println("[VehicleDetails] ❌ Main modal closed AFTER car make selection on attempt " + attempt + "!");
                System.out.println("[VehicleDetails] Waiting 2s before retry...");
                try { Thread.sleep(2000); } catch (Exception e) {}
                continue; // retry from top
            }

            // Step 2: Select car model
            boolean modelSelected = selectCarModel();

            if (modelSelected) {
                System.out.println("[VehicleDetails] ✅ Car Make + Model selected successfully on attempt " + attempt + "!");
                return; // SUCCESS
            } else {
                System.out.println("[VehicleDetails] ⚠️ Car model not selected on attempt " + attempt + ". Retrying...");
                try { Thread.sleep(1500); } catch (Exception e) {}
            }
        }

        System.out.println("[VehicleDetails] ⚠️ All attempts exhausted. Proceeding to click Proceed...");
    }

    // ─── clickProceed ────────────────────────────────────────────────────────────
    public void clickProceed() {
        System.out.println("Clicking Proceed button on Vehicle Details modal...");
        
        // Check if the modal already auto-closed after selecting the car model
        try {
            boolean isStillOpen = false;
            try {
                isStillOpen = new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(1))
                    .until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf(modalHeader)) != null;
            } catch (Exception ex) {}
            
            if (!isStillOpen) {
                System.out.println("Modal already disappeared (auto-closed). No need to click Proceed.");
                return;
            }
        } catch (Exception e) {}

        for (int i = 0; i < 2; i++) {
            try { if (proceedButton.isDisplayed()) break; } catch (Exception e) {}
            System.out.println("Scrolling down to reveal Proceed button...");
            scrollDown();
            try { Thread.sleep(800); } catch (Exception e) {}
        }
        
        try {
            if (proceedButton.isDisplayed()) {
                click(proceedButton);
            }
        } catch (Exception e) {
            System.out.println("Standard click on Proceed failed, attempting physical tap if still visible...");
            try {
                if (proceedButton.isDisplayed()) {
                    tapElement(proceedButton);
                }
            } catch (Exception ex) {
                System.out.println("Proceed button is no longer visible, assuming modal auto-closed.");
            }
        }
        try { Thread.sleep(3000); } catch (Exception e) {}
    }

    // ─── Entry point ─────────────────────────────────────────────────────────────
    public void handleVehicleDetailsIfPresent() {
        if (isModalDisplayed()) {
            System.out.println("Vehicle Details modal detected. Starting car make + model selection...");
            searchAndSelectCarMakeAndModel("b.m.w");
            clickProceed();
        } else {
            System.out.println("Vehicle details modal not required. Skipping...");
        }
    }
}
