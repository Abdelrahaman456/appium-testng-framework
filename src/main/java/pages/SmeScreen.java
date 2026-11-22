package pages;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Page Object for SMEs Tab, SME Health Insurance External Portal Handoff,
 * All Products Catalog Toggle, and Home Quick Actions.
 * 100% pure Appium locator-based implementation.
 */
public class SmeScreen extends BasePage {

    private static final String TREE_PACKAGE = utils.TestConfig.appPackage();

    // Home Segment Tabs
    private final By individualTabBy = By.xpath("//*[@content-desc='Individual' or @text='Individual']");
    private final By smesTabBy = By.xpath("//*[@content-desc='SMEs' or @text='SMEs' or contains(@content-desc, 'SMEs')]");
    private final By allProductsTabBy = By.xpath("//*[contains(@content-desc, 'All products') or contains(@text, 'All products')]");

    // SME Products
    private final By smeHealthCardBy = By.xpath(
        "//*[@resource-id='btn_home_product_sme'] | " +
        "//*[contains(@content-desc, 'SME') and contains(@content-desc, 'Health')] | " +
        "//*[contains(@text, 'SME') and contains(@text, 'Health')] | " +
        "//*[contains(@content-desc, 'SME\nHealth')]"
    );

    // Quick Actions on SMEs view
    private final By quickActionFaqsBy = By.xpath("//*[@content-desc='FAQs' or @text='FAQs' or contains(@content-desc, 'FAQs')]");
    private final By faqSearchInputBy = By.xpath("//*[@resource-id='textfield_searchkeywords']");
    private final By faqsHeaderBy = By.xpath("//*[@content-desc='FAQs' or @text='FAQs'] | //*[@resource-id='textfield_searchkeywords']");

    // Chrome External Portal Locators
    private final By chromeDismissFreBy = By.xpath(
        "//*[@text='Stay signed out' or @text='Use without an account' or @text='No thanks' or @resource-id='com.android.chrome:id/signin_fre_dismiss_button']"
    );
    private final By chromeUrlBarBy = By.xpath(
        "//*[@resource-id='com.android.chrome:id/url_bar' or @resource-id='com.android.chrome:id/search_box_text' or contains(@package, 'chrome')]"
    );

    // =========================================================================
    // 1. TAB SWITCHING & CATALOG VERIFICATION
    // =========================================================================
    public SmeScreen selectSmesTab() {
        System.out.println("Selecting 'SMEs' tab on Home screen via locator...");
        waitUntil(() -> isElementVisible(smesTabBy), 10);
        tapElement(getVisibleElement(smesTabBy));
        waitUntil(() -> isElementVisible(smeHealthCardBy), 5);
        try { Thread.sleep(500); } catch (Exception ignore) {}
        return this;
    }

    public SmeScreen selectAllProductsTab() {
        System.out.println("Selecting 'All products' tab on Home screen via locator...");
        waitUntil(() -> isElementVisible(allProductsTabBy), 10);
        tapElement(getVisibleElement(allProductsTabBy));
        try { Thread.sleep(300); } catch (Exception ignore) {}
        return this;
    }

    public SmeScreen selectIndividualTab() {
        System.out.println("Switching back to 'Individual' tab on Home screen via locator...");
        if (!isElementVisible(individualTabBy)) {
            scrollUp();
        }
        waitUntil(() -> isElementVisible(individualTabBy), 10);
        tapElement(getVisibleElement(individualTabBy));
        try { Thread.sleep(300); } catch (Exception ignore) {}
        return this;
    }

    public boolean isSmeHealthProductDisplayed() {
        return waitUntil(() -> isElementVisible(smeHealthCardBy), 10);
    }

    public boolean isAllProductsCatalogDisplayed() {
        By individualProductBy = By.xpath("//*[contains(@content-desc, 'Motor') or contains(@content-desc, 'Travel') or contains(@content-desc, 'Pet')]");
        boolean bothVisible = waitUntil(() -> isElementVisible(individualProductBy) && isElementVisible(smeHealthCardBy), 6);
        if (!bothVisible && isElementVisible(individualProductBy)) {
            scrollDown();
            bothVisible = waitUntil(() -> isElementVisible(smeHealthCardBy), 4);
            scrollUp();
        }
        return bothVisible;
    }

    // =========================================================================
    // 2. SME HEALTH INSURANCE EXTERNAL PORTAL HANDOFF
    // =========================================================================
    public SmeScreen openSmeHealthInsurance() {
        System.out.println("Tapping 'SME Health Insurance' card via locator...");
        waitUntil(() -> isElementVisible(smeHealthCardBy), 10);
        try { Thread.sleep(500); } catch (Exception ignore) {}
        tapElement(getVisibleElement(smeHealthCardBy));
        try { Thread.sleep(2500); } catch (Exception ignore) {}
        try {
            if (driver instanceof AndroidDriver
                    && TREE_PACKAGE.equals(((AndroidDriver) driver).getCurrentPackage())
                    && isElementVisible(smeHealthCardBy)) {
                System.out.println("Still on Tree app after first SME Health tap; retrying tap on settled card...");
                tapElement(getVisibleElement(smeHealthCardBy));
                Thread.sleep(2500);
            }
        } catch (Exception ignore) {}
        return this;
    }

    /**
     * Verifies that tapping SME Health Insurance hands off cleanly to Chrome / external SME web portal.
     */
    public boolean isExternalSmePortalOpened() {
        boolean handedOff = waitUntil(() -> {
            try {
                if (driver instanceof AndroidDriver) {
                    String currentPkg = ((AndroidDriver) driver).getCurrentPackage();
                    if (currentPkg != null) {
                        String pkgLower = currentPkg.toLowerCase();
                        if (pkgLower.contains("chrome") || pkgLower.contains("browser")
                                || pkgLower.contains("webview") || pkgLower.contains("firefox")
                                || pkgLower.contains("opera") || pkgLower.contains("edge")) {
                            System.out.println("External browser handoff verified! Active package: " + currentPkg);
                            return true;
                        }
                    }
                }
            } catch (Exception ignore) {}
            return !driver.findElements(chromeUrlBarBy).isEmpty() || !driver.findElements(chromeDismissFreBy).isEmpty();
        }, 12);

        if (handedOff) {
            // Dismiss Chrome First-Run sign-in prompt if shown so the SME portal page loads
            try {
                List<WebElement> freBtns = driver.findElements(chromeDismissFreBy);
                if (!freBtns.isEmpty() && freBtns.get(0).isDisplayed()) {
                    System.out.println("Dismissing Chrome First-Run prompt ('Stay signed out')...");
                    tapElement(freBtns.get(0));
                    Thread.sleep(2000);
                }
            } catch (Exception ignore) {}
        }
        return handedOff;
    }

    /**
     * Brings the Tree UAT app back to foreground cleanly after external portal handoff.
     */
    public boolean returnToTreeApp() {
        System.out.println("Returning from external SME portal back to Tree UAT app...");
        // 1. If Chrome Custom Tab close button exists, tap it
        try {
            By chromeCloseBy = By.xpath("//*[@resource-id='com.android.chrome:id/close_button' or @content-desc='Close tab']");
            List<WebElement> closes = driver.findElements(chromeCloseBy);
            if (!closes.isEmpty() && closes.get(0).isDisplayed()) {
                tapElement(closes.get(0));
                Thread.sleep(1500);
            }
        } catch (Exception ignore) {}

        // 2. Press back up to 2 times if Chrome is still foreground (dismisses Custom Tab / browser overlay)
        for (int i = 0; i < 2; i++) {
            if (isElementVisible(smesTabBy)) {
                return true;
            }
            try {
                driver.navigate().back();
                Thread.sleep(1200);
            } catch (Exception ignore) {}
        }

        // 3. Ensure Tree UAT app is foregrounded
        try {
            if (driver instanceof AndroidDriver) {
                ((AndroidDriver) driver).activateApp(TREE_PACKAGE);
                Thread.sleep(2000);
            }
        } catch (Exception ignore) {}

        return waitUntil(() -> {
            boolean treeActive = !(driver instanceof AndroidDriver) || TREE_PACKAGE.equals(((AndroidDriver) driver).getCurrentPackage());
            return treeActive && (isElementVisible(smesTabBy) || isElementVisible(individualTabBy));
        }, 12);
    }

    // =========================================================================
    // 3. QUICK ACTIONS FROM SME VIEW
    // =========================================================================
    public SmeScreen openQuickActionFaqs() {
        System.out.println("Tapping 'FAQs' Quick Action from SMEs view via locator...");
        if (!isElementVisible(quickActionFaqsBy)) {
            scrollDown();
        }
        waitUntil(() -> isElementVisible(quickActionFaqsBy), 8);
        tapElement(getVisibleElement(quickActionFaqsBy));
        try { Thread.sleep(1500); } catch (Exception ignore) {}
        return this;
    }

    public boolean isFaqsOpened() {
        return waitUntil(() -> isElementVisible(faqSearchInputBy)
                || (isElementVisible(faqsHeaderBy) && !isElementVisible(smesTabBy)), 10);
    }
}
