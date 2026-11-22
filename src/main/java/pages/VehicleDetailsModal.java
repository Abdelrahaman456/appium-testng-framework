package pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

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

    private static final org.openqa.selenium.By CAR_MODEL_FIELD = org.openqa.selenium.By.xpath(
        "(//android.widget.EditText)[2]"
    );

    private static final org.openqa.selenium.By BMW_RESULT = org.openqa.selenium.By.xpath(
        "//*[contains(@content-desc,'B.M.W') or contains(@text,'B.M.W') or contains(@content-desc,'BMW') or contains(@text,'BMW')]"
    );

    public boolean isModalDisplayed() {
        try {
            System.out.println("Checking for Vehicle Details modal...");
            return wait.until(ExpectedConditions.visibilityOf(modalHeader)) != null;
        } catch (Exception e) {
            System.out.println("Vehicle details modal did not appear.");
            return false;
        }
    }

    private boolean isMainModalStillOpen() {
        return isElementVisible(MODAL_HEADER);
    }

    public boolean isProceedEnabled() {
        try {
            List<WebElement> buttons = driver.findElements(org.openqa.selenium.By.xpath("//android.widget.Button[@content-desc='Proceed' or @text='Proceed']"));
            if (!buttons.isEmpty()) {
                String enabled = buttons.get(0).getAttribute("enabled");
                String clickable = buttons.get(0).getAttribute("clickable");
                System.out.println("[Proceed Check] enabled=" + enabled + ", clickable=" + clickable);
                return "true".equalsIgnoreCase(enabled) && "true".equalsIgnoreCase(clickable);
            }
        } catch (Exception e) {}
        return false;
    }

    private boolean selectCarMake(String keyword) {
        try {
            WebElement makeField = getVisibleElement(CAR_MAKE_FIELD);
            makeField.click();
            try { Thread.sleep(600); } catch (Exception e) {}
            makeField.clear();
            makeField.sendKeys(keyword);
            System.out.println("[CarMake] Typed '" + keyword + "'. Waiting for results...");
            try { Thread.sleep(2000); } catch (Exception e) {}

            // Try clicking BMW result
            int screenHeight = getSafeWindowSize().height;
            int minResultY = (int) (screenHeight * 0.42);
            int maxDropdownY = (int) (screenHeight * 0.88);
            try {
                List<WebElement> results = driver.findElements(BMW_RESULT);
                for (WebElement res : results) {
                    if (res.isDisplayed() && res.getRect().getY() > minResultY) {
                        res.click();
                        System.out.println("[CarMake] ✅ BMW clicked via direct locator.");
                        try { Thread.sleep(1000); } catch (Exception ex) {}
                        return true;
                    }
                }
            } catch (Exception ignored) {}

            // Fallback: find clickable suggestion below make field, or tap proportional offset
            org.openqa.selenium.Rectangle r = makeField.getRect();
            int makeBottom = r.getY() + r.getHeight();
            try {
                List<WebElement> clickables = driver.findElements(org.openqa.selenium.By.xpath("//*[@clickable='true']"));
                for (WebElement cand : clickables) {
                    org.openqa.selenium.Rectangle cr = cand.getRect();
                    String cls = cand.getAttribute("className");
                    String desc = cand.getAttribute("content-desc");
                    if (cand.isDisplayed()
                            && cr.getY() >= makeBottom - (int) (r.getHeight() * 0.15)
                            && cr.getY() < maxDropdownY
                            && (cls == null || !cls.contains("EditText"))
                            && (desc == null || !desc.contains("Proceed"))) {
                        cand.click();
                        System.out.println("[CarMake] ✅ Clicked suggestion below make field.");
                        try { Thread.sleep(1000); } catch (Exception ex) {}
                        return true;
                    }
                }
            } catch (Exception ignored) {}

            tapCoordinates(r.getX() + r.getWidth() / 2, r.getY() + (int) (r.getHeight() * 1.6));
            System.out.println("[CarMake] ✅ Tapped proportionally below make field as fallback.");
            try { Thread.sleep(1000); } catch (Exception ex) {}
            return true;
        } catch (Exception e) {
            System.out.println("[CarMake] ❌ selectCarMake failed: " + e.getMessage());
            return false;
        }
    }

    private boolean selectCarModel() {
        System.out.println("[CarModel] Looking for Car Model field...");
        try { Thread.sleep(1500); } catch (Exception e) {}

        WebElement modelField = null;
        try {
            List<WebElement> fields = driver.findElements(CAR_MODEL_FIELD);
            if (!fields.isEmpty() && fields.get(0).isDisplayed()) {
                modelField = fields.get(0);
            }
        } catch (Exception ignored) {}

        if (modelField == null) {
            try {
                modelField = getVisibleElement(org.openqa.selenium.By.xpath("//android.widget.EditText[contains(@hint, 'Model') or contains(@hint, 'model')]"));
            } catch (Exception ignored) {}
        }

        if (modelField == null) {
            System.out.println("[CarModel] Model field not found.");
            return false;
        }

        System.out.println("[CarModel] Found Model field. Interacting...");
        try {
            modelField.click();
            try { Thread.sleep(1500); } catch (Exception ex) {}

            // Debug: print clickable elements located in the lower half of the screen
            int screenHeight = getSafeWindowSize().height;
            int minLowerHalfY = (int) (screenHeight * 0.58);
            int maxDropdownY = (int) (screenHeight * 0.88);
            List<WebElement> allElements = driver.findElements(org.openqa.selenium.By.xpath("//*[@clickable='true']"));
            System.out.println("[CarModel Debug] Found " + allElements.size() + " clickable elements.");
            for (WebElement el : allElements) {
                try {
                    org.openqa.selenium.Rectangle r = el.getRect();
                    String desc = el.getAttribute("content-desc");
                    String txt = el.getText();
                    String cls = el.getAttribute("className");
                    if (r.getY() > minLowerHalfY) {
                        System.out.println("[CarModel Modal Item] " + cls + " | desc: '" + desc + "' | text: '" + txt + "' | [" + r.getX() + "," + r.getY() + "][" + (r.getX() + r.getWidth()) + "," + (r.getY() + r.getHeight()) + "]");
                    }
                } catch (Exception ignore) {}
            }

            // Find an item below the Model field and above Proceed button using proportional screen bounds
            org.openqa.selenium.Rectangle modelRect = modelField.getRect();
            int modelBottom = modelRect.getY() + modelRect.getHeight();
            int modelTopOverlap = (int) (modelRect.getHeight() * 0.15);

            for (WebElement el : allElements) {
                try {
                    org.openqa.selenium.Rectangle r = el.getRect();
                    String cls = el.getAttribute("className");
                    String desc = el.getAttribute("content-desc");
                    if (r.getY() >= modelBottom - modelTopOverlap
                            && r.getY() < maxDropdownY
                            && (cls == null || !cls.contains("EditText"))
                            && (desc == null || !desc.contains("Proceed"))) {
                        System.out.println("[CarModel] Clicking dropdown candidate: " + cls + " desc: " + desc + " at Y=" + r.getY());
                        el.click();
                        try { Thread.sleep(1000); } catch (Exception ex) {}
                        if (isProceedEnabled()) {
                            System.out.println("[CarModel] ✅ Proceed button is enabled after item click!");
                            return true;
                        }
                    }
                } catch (Exception ignore) {}
            }

            // Fallback: physical tap proportionally below the model field
            int tapX = modelRect.getX() + (modelRect.getWidth() / 2);
            int tapY = modelRect.getY() + (int) (modelRect.getHeight() * 1.3);
            System.out.println("[CarModel] Tapping proportionally below model field at (" + tapX + ", " + tapY + ")...");
            tapCoordinates(tapX, tapY);
            try { Thread.sleep(1000); } catch (Exception ex) {}

            if (isProceedEnabled()) {
                System.out.println("[CarModel] ✅ Proceed button enabled after tap below field!");
                return true;
            }

            // Try dismissing keyboard if open
            try {
                if (driver instanceof io.appium.java_client.android.AndroidDriver) {
                    ((io.appium.java_client.android.AndroidDriver) driver).hideKeyboard();
                    Thread.sleep(500);
                }
            } catch (Exception ignore) {}

            // Try typing into model field to filter
            try {
                modelField.sendKeys("3");
                try { Thread.sleep(1000); } catch (Exception ex) {}
                List<WebElement> filteredItems = driver.findElements(org.openqa.selenium.By.xpath("//*[contains(@content-desc, 'Series') or contains(@content-desc, 'SERIES') or contains(@content-desc, '3')]"));
                for (WebElement fi : filteredItems) {
                    if (fi.isDisplayed() && fi.getRect().getY() > minLowerHalfY) {
                        fi.click();
                        try { Thread.sleep(1000); } catch (Exception ex) {}
                        if (isProceedEnabled()) return true;
                    }
                }
            } catch (Exception ignore) {}

            return isProceedEnabled();

        } catch (Exception e) {
            System.out.println("[CarModel] Error interacting with model field: " + e.getMessage());
            return false;
        }
    }

    public void searchAndSelectCarMakeAndModel(String keyword) {
        final int MAX_RETRIES = 3;

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            System.out.println("\n[VehicleDetails] ══ Attempt " + attempt + "/" + MAX_RETRIES + " ══");

            if (!isMainModalStillOpen()) {
                System.out.println("[VehicleDetails] Main modal is closed.");
                return;
            }

            // Step 1: Select car make
            selectCarMake(keyword);
            try { Thread.sleep(1000); } catch (Exception e) {}

            if (!isMainModalStillOpen()) return;

            // Step 2: Select car model
            selectCarModel();
            try { Thread.sleep(1000); } catch (Exception e) {}

            if (isProceedEnabled()) {
                System.out.println("[VehicleDetails] ✅ Car Make + Model selected successfully on attempt " + attempt + "!");
                return;
            } else {
                System.out.println("[VehicleDetails] ⚠️ Proceed button still not enabled on attempt " + attempt + ".");
            }
        }
    }

    public void clickProceed() {
        System.out.println("Clicking Proceed button on Vehicle Details modal...");
        try { Thread.sleep(1000); } catch (Exception e) {}

        // Dismiss keyboard if open
        try {
            if (driver instanceof io.appium.java_client.android.AndroidDriver) {
                ((io.appium.java_client.android.AndroidDriver) driver).hideKeyboard();
                Thread.sleep(500);
            }
        } catch (Exception ignore) {}

        for (int i = 0; i < 5; i++) {
            try {
                if (!isMainModalStillOpen()) {
                    System.out.println("Vehicle Details modal already dismissed.");
                    return;
                }
                List<WebElement> buttons = driver.findElements(org.openqa.selenium.By.xpath("//android.widget.Button[@content-desc='Proceed' or @text='Proceed']"));
                if (!buttons.isEmpty() && buttons.get(0).isDisplayed()) {
                    System.out.println("Proceed button visible, clicking it...");
                    buttons.get(0).click();
                    try { Thread.sleep(2500); } catch (Exception e) {}
                    if (!isMainModalStillOpen()) {
                        System.out.println("Vehicle Details modal closed successfully!");
                        return;
                    }
                }
            } catch (Exception e) {}

            List<WebElement> hiddenButtons = findWithUnimportantViews(org.openqa.selenium.By.xpath(
                "//android.widget.Button[@content-desc='Proceed' or @text='Proceed'] | //*[@resource-id='btn_proceed']"));
            if (!hiddenButtons.isEmpty()) {
                System.out.println("Proceed fallback via findWithUnimportantViews...");
                tapElement(hiddenButtons.get(0));
            } else {
                scrollDown();
            }
            try { Thread.sleep(2000); } catch (Exception e) {}
            if (!isMainModalStillOpen()) return;
        }
    }

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
