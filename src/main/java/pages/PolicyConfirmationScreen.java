package pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;
import java.util.List;

/**
 * Page Object for Policy Confirmation Screen across all insurance products.
 * Confirms issuance, handles 3DS ACS emulators dynamically, and routes back to Home.
 * ZERO hardcoded coordinates.
 */
public class PolicyConfirmationScreen extends BasePage {

    private boolean threeDsSubmitted = false;
    private long last3dsClickTimeMs = 0L;

    private static final String[] THREE_DS_POSITIVE_KEYWORDS = {
        "Submit", "Authenticate", "Approve", "Continue", "Complete", "Proceed", "Verify"
    };

    private static final String[] THREE_DS_EXCLUDED_KEYWORDS = {
        "cancel", "reject", "decline", "fail", "back"
    };

    public PolicyConfirmationScreen() {
        super();
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(2)), this);
    }

    @AndroidFindBy(xpath = "//*[contains(@text, 'ready to roll') or contains(@content-desc, 'ready to roll') or contains(@text, 'All set!') or contains(@content-desc, 'All set!') or contains(@text, 'Policy number') or contains(@content-desc, 'Policy number') or contains(@text, 'Payment successful') or contains(@content-desc, 'Payment successful') or contains(@resource-id, 'confirmation')]")
    private WebElement readyToRollHeader;

    @AndroidFindBy(xpath = "//*[contains(@text, 'Policy number') or contains(@content-desc, 'Policy number')]")
    private WebElement policyNumberLabel;

    @AndroidFindBy(xpath = "//*[contains(@content-desc, 'View Policy') or contains(@text, 'View Policy')]")
    private WebElement viewPolicyButton;

    @AndroidFindBy(xpath = "//android.widget.Button[@content-desc='Close' or @text='Close'] | //*[@resource-id='btn_close' or @content-desc='Close' or @text='Close'] | //android.widget.FrameLayout[@resource-id='android:id/content']//android.widget.ImageView[1]")
    private WebElement closeHeaderButton;

    private boolean isExcluded3dsButton(WebElement el) {
        try {
            String text = el.getText();
            String desc = el.getAttribute("content-desc");
            String combined = ((text != null ? text : "") + " " + (desc != null ? desc : "")).toLowerCase();
            for (String excluded : THREE_DS_EXCLUDED_KEYWORDS) {
                if (combined.contains(excluded)) {
                    return true;
                }
            }
        } catch (Exception ignore) {}
        return false;
    }

    private boolean isAnyDisplayed(By locator) {
        try {
            for (WebElement el : driver.findElements(locator)) {
                if (el.isDisplayed()) {
                    return true;
                }
            }
        } catch (Exception ignore) {}
        return false;
    }

    private boolean is3dsScreenOpen() {
        By threeDsBy = By.xpath(
            "//*[@content-desc='Payment' or @text='Payment'"
            + " or contains(@text, 'ACS Emulator') or contains(@content-desc, 'ACS Emulator')]"
            + " | //android.webkit.WebView");
        By cardModalOrCheckoutBy = By.xpath(
            "//*[@content-desc='Save' or @text='Save' or @resource-id='btn_pay_now'"
            + " or @resource-id='btn_checkout_page_motor_pay_now' or @resource-id='btn_credit_debit_card_bottom_sheet_save']");
        return isAnyDisplayed(threeDsBy) && driver.findElements(cardModalOrCheckoutBy).isEmpty();
    }

    /**
     * Dynamically handles 3DS payment authentication webviews or emulators.
     */
    public void handle3dsIfPresent() {
        try {
            driver.setSetting("ignoreUnimportantViews", false);
        } catch (Exception ignore) {}

        try {
            By cardModalOrCheckoutBy = By.xpath(
                "//*[@content-desc='Save' or @text='Save' or @resource-id='btn_pay_now'"
                + " or @resource-id='btn_checkout_page_motor_pay_now' or @resource-id='btn_credit_debit_card_bottom_sheet_save']");
            if (!driver.findElements(cardModalOrCheckoutBy).isEmpty()) {
                return;
            }

            // 1. Search explicit positive 3DS CTAs in priority order (preferring Buttons / clickable elements first)
            for (String keyword : THREE_DS_POSITIVE_KEYWORDS) {
                String kwLower = keyword.toLowerCase();
                String idClause = "submit".equals(kwLower) ? " or @resource-id='submit'" : "";
                By priorityBtnBy = By.xpath(
                    "//android.widget.Button[contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + kwLower + "')"
                    + " or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + kwLower + "')" + idClause + "]"
                    + " | //*[@clickable='true' and (contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + kwLower + "')"
                    + " or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + kwLower + "')" + idClause + ")]");
                for (WebElement el : driver.findElements(priorityBtnBy)) {
                    try {
                        if (el.isDisplayed() && !isExcluded3dsButton(el)) {
                            System.out.println("3DS CTA detected ('" + keyword + "')! Clicking to complete payment authentication...");
                            tapElement(el);
                            threeDsSubmitted = true;
                            last3dsClickTimeMs = System.currentTimeMillis();
                            Thread.sleep(1500);
                            return;
                        }
                    } catch (Exception ignore) {}
                }
            }

            // 2. Fallback to exact positive CTA match inside WebView (avoids matching instructional prose), then single positive WebView button
            if (is3dsScreenOpen()) {
                for (String keyword : THREE_DS_POSITIVE_KEYWORDS) {
                    String kwLower = keyword.toLowerCase();
                    String idClause = "submit".equals(kwLower) ? " or @resource-id='submit'" : "";
                    By webViewKeywordBy = By.xpath(
                        "//android.webkit.WebView//*[translate(normalize-space(@text), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='" + kwLower + "'"
                        + " or translate(normalize-space(@content-desc), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='" + kwLower + "'" + idClause + "]");
                    for (WebElement el : driver.findElements(webViewKeywordBy)) {
                        try {
                            if (el.isDisplayed() && !isExcluded3dsButton(el)) {
                                System.out.println("3DS WebView CTA detected ('" + keyword + "')! Clicking to complete payment authentication...");
                                tapElement(el);
                                threeDsSubmitted = true;
                                last3dsClickTimeMs = System.currentTimeMillis();
                                Thread.sleep(1500);
                                return;
                            }
                        } catch (Exception ignore) {}
                    }
                }

                List<WebElement> webButtons = driver.findElements(By.xpath("//android.webkit.WebView//android.widget.Button"));
                WebElement singlePositiveBtn = null;
                int positiveCount = 0;
                for (WebElement btn : webButtons) {
                    try {
                        if (btn.isDisplayed() && !isExcluded3dsButton(btn)) {
                            singlePositiveBtn = btn;
                            positiveCount++;
                        }
                    } catch (Exception ignore) {}
                }
                if (positiveCount == 1 && singlePositiveBtn != null) {
                    System.out.println("Found single positive button on 3DS WebView: " + singlePositiveBtn.getText());
                    tapElement(singlePositiveBtn);
                    threeDsSubmitted = true;
                    last3dsClickTimeMs = System.currentTimeMillis();
                    Thread.sleep(1500);
                }
            }
        } catch (Exception ignore) {
        } finally {
            try {
                driver.setSetting("ignoreUnimportantViews", false);
            } catch (Exception ignore) {}
        }
    }

    public boolean isLoaded() {
        if (BasePage.uatElmDependentBlocked) {
            System.out.println("[UAT Data Notice] Flow was blocked at UAT Elm dependent verification; Policy Confirmation Screen was not reached.");
            return false;
        }

        By readyBy = By.xpath(
            "//*[contains(@text, 'ready to roll') or contains(@content-desc, 'ready to roll')" +
            " or contains(@text, 'All set!') or contains(@content-desc, 'All set!')" +
            " or contains(@text, 'Policy number') or contains(@content-desc, 'Policy number')" +
            " or contains(@text, 'Payment successful') or contains(@content-desc, 'Payment successful')" +
            " or contains(@resource-id, 'confirmation')" +
            " or contains(@content-desc, 'View Policy') or contains(@text, 'View Policy')]");
        By purchaseFailedBy = By.xpath(
            "//*[contains(@content-desc, 'Purchase failed') or contains(@text, 'Purchase failed')" +
            " or contains(@content-desc, 'Purchase Failed') or contains(@text, 'Purchase Failed')]");
        By swwBy = By.xpath(
            "//*[contains(@content-desc, 'Something Went Wrong') or contains(@text, 'Something Went Wrong')" +
            " or contains(@content-desc, 'Something went wrong') or contains(@text, 'Something went wrong')]");

        int maxWaitSec = Math.max(90, utils.TestConfig.policyTimeout());
        System.out.println("Polling dynamically (up to " + maxWaitSec + "s) for Policy Confirmation Screen...");

        long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - startTime) < (maxWaitSec * 1000L)) {
            if (isAnyDisplayed(readyBy)) {
                return true;
            }
            if (isAnyDisplayed(purchaseFailedBy)) {
                System.out.println("[PolicyConfirmationScreen] Failure detected: 'Purchase failed' displayed after payment/3DS; failing fast.");
                return false;
            }
            if (isAnyDisplayed(swwBy)) {
                System.out.println("[PolicyConfirmationScreen] Error detected: 'Something Went Wrong' displayed; failing fast.");
                return false;
            }

            boolean shouldRetry3ds = threeDsSubmitted
                    && (System.currentTimeMillis() - last3dsClickTimeMs) >= 4000L
                    && is3dsScreenOpen();
            if (!threeDsSubmitted || shouldRetry3ds) {
                if (shouldRetry3ds) {
                    System.out.println("3DS screen still open after 4s with no confirmation/error; retrying 3DS CTA...");
                    threeDsSubmitted = false;
                }
                handle3dsIfPresent();
                if (isAnyDisplayed(readyBy)) {
                    return true;
                }
            }
            try { Thread.sleep(500); } catch (Exception ignore) {}
        }

        System.out.println("[PageValidation] PolicyConfirmationScreen did NOT load within " + maxWaitSec + "s!");
        return false;
    }

    public boolean isPolicyConfirmed() {
        try {
            System.out.println("Verifying Policy Confirmation Screen elements...");
            return isLoaded();
        } catch (Exception e) {
            System.out.println("Policy confirmation screen element not detected: " + e.getMessage());
            return false;
        }
    }

    public String getPolicyNumber() {
        try {
            if (isElementVisible(policyNumberLabel)) {
                String text = policyNumberLabel.getText();
                if (text == null || text.isEmpty()) {
                    text = policyNumberLabel.getAttribute("content-desc");
                }
                return text;
            }
        } catch (Exception ignore) {}
        return "";
    }

    public void clickViewPolicy() {
        click(viewPolicyButton);
    }

    /**
     * Clicks top-right Close (X) icon on Policy Confirmation screen to route back to Home Landing Page.
     * Uses dynamic element resolution with zero coordinate taps.
     */
    public void clickCloseToHome() {
        if (BasePage.uatElmDependentBlocked) {
            System.out.println("[UAT Data Notice] Skipping Close-to-Home tap after blocked UAT Elm dependent step; BaseTest will reset to Home.");
            return;
        }
        if (!driver.findElements(By.xpath("//*[contains(@content-desc, 'Purchase failed') or contains(@text, 'Purchase failed')]")).isEmpty()) {
            System.out.println("[UAT Payment Gateway Notice] Post-3DS sandbox bottom sheet active; BaseTest will reset to Home.");
            return;
        }
        System.out.println("Clicking Close (X) icon on Policy Confirmation screen to return to Home...");
        try {
            click(closeHeaderButton);
        } catch (Exception e) {
            System.out.println("Standard click on Close icon failed, attempting fallback locator...");
            try {
                WebElement fallbackClose = getVisibleElement(By.xpath(
                    "//android.widget.Button[@content-desc='Close' or @text='Close'] | //*[@resource-id='btn_close' or @content-desc='Close' or @text='Close'] | (//android.widget.ImageView)[1]"));
                tapElement(fallbackClose);
            } catch (Exception ex) {
                System.out.println("Fallback close failed: " + ex.getMessage());
            }
        }
        try { Thread.sleep(2000); } catch (Exception ignore) {}
    }
}
