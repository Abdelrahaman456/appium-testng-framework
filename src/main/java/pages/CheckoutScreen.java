package pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;
import java.util.List;

public class CheckoutScreen extends BasePage {

    public CheckoutScreen() {
        super();
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(2)), this);
    }

    @AndroidFindBy(xpath = "//*[@resource-id='btn_checkout_page_motor_payment_mode_direct_pay_post_card' or @resource-id='btn_payment_mode_moyasar_direct_pay' or contains(@content-desc, 'Credit') or contains(@text, 'Credit') or contains(@content-desc, 'Debit') or contains(@text, 'Debit') or contains(@content-desc, 'Card') or contains(@text, 'Card')]")
    private WebElement creditDebitCardButton;

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_checkout_page_motor_email' or @resource-id='textfield_email' or contains(@resource-id, 'email') or @hint='Email' or contains(@hint, 'email')]")
    private WebElement emailField;

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_checkout_page_motor_iban' or @resource-id='textfield_iban' or contains(@resource-id, 'iban') or contains(@hint, 'IBAN')]")
    private WebElement ibanField;

    @AndroidFindBy(xpath = "//android.widget.Button[@clickable='true' and (@content-desc='Pay now' or @text='Pay now')] | //*[@resource-id='btn_checkout_page_motor_pay_now' or @resource-id='btn_pay_now' or @content-desc='Pay now' or @text='Pay now']")
    private WebElement payNowButton;

    @AndroidFindBy(xpath = "//*[@resource-id='tgl_checkout_page_motor_checkbox' or @resource-id='tgl_checkbox' or contains(@resource-id, 'checkbox') or @class='android.widget.Switch']")
    private WebElement termsCheckbox;

    @AndroidFindBy(xpath = "//*[contains(@content-desc, 'VAT') or contains(@text, 'VAT') or contains(@content-desc, '15%')]")
    private WebElement vatBreakdownText;

    @AndroidFindBy(xpath = "//*[contains(@content-desc, 'Total') or contains(@text, 'Total')]")
    private WebElement totalAmountText;

    public boolean isVatBreakdownDisplayed() {
        try {
            org.openqa.selenium.By vatBy = org.openqa.selenium.By.xpath("//*[contains(@content-desc, 'VAT') or contains(@text, 'VAT') or contains(@content-desc, '15%')]");
            if (!driver.findElements(vatBy).isEmpty()) {
                return true;
            }
            // If collapsed under 'Details', tap Details to expand the premium breakdown
            org.openqa.selenium.By detailsBy = org.openqa.selenium.By.xpath("//*[contains(@content-desc, 'Details') or contains(@text, 'Details')]");
            java.util.List<WebElement> details = driver.findElements(detailsBy);
            if (!details.isEmpty()) {
                tapElement(details.get(0));
                Thread.sleep(800);
            }
            return !driver.findElements(vatBy).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public void enterEmail(String email) {
        System.out.println("Entering Email into Checkout field: " + email);
        org.openqa.selenium.By emailBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='textfield_checkout_page_motor_email' or @resource-id='textfield_email' or contains(@resource-id, 'email') or @hint='Email' or contains(@hint, 'email')]"
        );
        try {
            WebElement field = findDisplayedElement(emailBy);
            if (field == null) {
                scrollUp();
                waitUntil(() -> findDisplayedElement(emailBy) != null, 3);
                field = findDisplayedElement(emailBy);
            }
            if (field != null) {
                sendKeys(field, email);
                return;
            }
            List<WebElement> atEls = driver.findElements(org.openqa.selenium.By.xpath("//*[contains(@text, '@')]"));
            if (!atEls.isEmpty()) {
                sendKeys(atEls.get(0), email);
            } else {
                System.out.println("Email field skipped/not present on this checkout screen.");
            }
        } catch (Exception e) {
            System.out.println("Email field skipped/already filled: " + e.getMessage());
        } finally {
            hideKeyboard();
        }
    }

    private boolean ibanFieldContains(WebElement el, String expectedDigits) {
        if (el == null || expectedDigits == null || expectedDigits.isEmpty()) {
            return false;
        }
        try {
            String text = el.getText();
            if (text == null) {
                return false;
            }
            String actualDigits = text.replaceAll("\\D", "");
            return actualDigits.contains(expectedDigits);
        } catch (Exception e) {
            return false;
        }
    }

    public void enterIban(String iban) {
        System.out.println("Entering IBAN into Checkout field: " + iban);
        hideKeyboard();

        // Strip 'SA' prefix up front so the UI field receives strictly the 22 numeric digits
        String cleaned = iban == null ? "" : iban.trim().replaceAll("\\s+", "");
        String numericOnly = cleaned.toUpperCase().startsWith("SA") ? cleaned.substring(2) : cleaned;
        String expectedDigits = numericOnly.replaceAll("\\D", "");

        org.openqa.selenium.By ibanBy = org.openqa.selenium.By.xpath(
            "//android.widget.EditText[@resource-id='textfield_iban' or @resource-id='textfield_checkout_page_motor_iban' " +
            "or contains(@resource-id, 'iban') or contains(@hint, 'IBAN')] | //*[@resource-id='textfield_iban' or contains(@resource-id, 'iban')]"
        );

        try {
            waitUntil(() -> findDisplayedElement(ibanBy) != null, 4);
            WebElement el = findDisplayedElement(ibanBy);
            if (el == null) {
                System.out.println("IBAN field not visible yet; scrolling down and retrying dynamic locator...");
                scrollDown();
                waitUntil(() -> findDisplayedElement(ibanBy) != null, 4);
                el = findDisplayedElement(ibanBy);
            }
            if (el == null) {
                System.out.println("IBAN field not found on Checkout screen after scrollDown retry.");
                return;
            }

            for (int attempt = 1; attempt <= 2; attempt++) {
                hideKeyboard();
                el = findDisplayedElement(ibanBy);
                if (el == null) {
                    scrollDown();
                    el = findDisplayedElement(ibanBy);
                }
                if (el == null) {
                    break;
                }

                if (attempt == 1) {
                    try {
                        tapElement(el);
                        try { Thread.sleep(300); } catch (Exception ignore) {}
                        try { el.clear(); } catch (Exception ignore) {}
                        el.sendKeys(numericOnly);
                        System.out.println("Attempt 1: sent IBAN digits via direct sendKeys: " + numericOnly);
                    } catch (Exception ex) {
                        System.out.println("IBAN direct sendKeys note: " + ex.getMessage());
                    }
                } else {
                    System.out.println("Attempt 2: IBAN value missing/mismatched; re-focusing via tapElement and adbInput...");
                    try {
                        tapElement(el);
                        try { Thread.sleep(350); } catch (Exception ignore) {}
                        try { el.clear(); } catch (Exception ignore) {}
                        adbInput(numericOnly);
                        try { Thread.sleep(350); } catch (Exception ignore) {}
                    } catch (Exception ex) {
                        System.out.println("IBAN adbInput retry note: " + ex.getMessage());
                    }
                }

                hideKeyboard();
                WebElement verifiedEl = findDisplayedElement(ibanBy);
                if (ibanFieldContains(verifiedEl, expectedDigits)) {
                    System.out.println("IBAN verified in Checkout field on attempt " + attempt + ".");
                    return;
                }
                try {
                    String currentVal = verifiedEl != null ? verifiedEl.getText() : "null";
                    System.out.println("IBAN verification failed after attempt " + attempt + " (observed text: '" + currentVal + "').");
                } catch (Exception ignore) {}
            }
        } catch (Exception ex) {
            System.out.println("IBAN find error: " + ex.getMessage());
        }
    }

    public boolean selectCreditDebitCard() {
        System.out.println("Checking for Moyasar / Credit-Debit Card payment method on Checkout Screen...");
        org.openqa.selenium.By exactMoyasarBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='btn_payment_mode_moyasar_direct_pay' or @resource-id='btn_checkout_page_motor_payment_mode_direct_pay_post_card']//*[@clickable='true']"
            + " | //*[@resource-id='btn_payment_mode_moyasar_direct_pay' or @resource-id='btn_checkout_page_motor_payment_mode_direct_pay_post_card']"
        );
        org.openqa.selenium.By clickableCardBy = org.openqa.selenium.By.xpath(
            "//*[@clickable='true' and (contains(@content-desc, 'Credit') or contains(@text, 'Credit')"
            + " or contains(@content-desc, 'Debit') or contains(@text, 'Debit')"
            + " or contains(@content-desc, 'Direct') or contains(@text, 'Direct'))]"
        );
        org.openqa.selenium.By broadCardBy = org.openqa.selenium.By.xpath(
            "//*[contains(@content-desc, 'Credit') or contains(@text, 'Credit')"
            + " or contains(@content-desc, 'Debit') or contains(@text, 'Debit')"
            + " or ((contains(@content-desc, 'Card') or contains(@text, 'Card')) and not(contains(@content-desc, 'Mada') or contains(@text, 'Mada')))]"
        );

        for (int attempt = 0; attempt <= 3; attempt++) {
            if (attempt > 0) {
                scrollDown();
            }
            if (tryTapVisiblePaymentOption(exactMoyasarBy, "Exact Moyasar ID")) {
                return true;
            }
            if (tryTapVisiblePaymentOption(clickableCardBy, "Clickable Credit/Debit/Direct Pay")) {
                return true;
            }
            if (tryTapVisiblePaymentOption(broadCardBy, "Broad Credit/Debit Card container")) {
                return true;
            }
        }
        System.out.println("Credit/Debit Card (Moyasar Direct Pay) option was NOT found after scrolling.");
        return false;
    }

    private boolean tryTapVisiblePaymentOption(org.openqa.selenium.By locator, String label) {
        java.util.List<WebElement> matches = findWithUnimportantViews(locator);
        for (WebElement el : matches) {
            try {
                if (el.isDisplayed()) {
                    System.out.println("Found payment option (" + label + "), tapping...");
                    tapElement(el);
                    return true;
                }
            } catch (Exception ignore) {}
        }
        return false;
    }

    public boolean waitForPageToLoad() {
        System.out.println("Waiting for Checkout Screen to load...");
        org.openqa.selenium.By checkoutReadyBy = org.openqa.selenium.By.xpath(
            "//*[@content-desc='Checkout' or @text='Checkout' " +
            "or contains(@content-desc, 'Total premium') or contains(@text, 'Total premium') " +
            "or contains(@content-desc, 'Payment Method') or contains(@text, 'Payment Method') " +
            "or contains(@content-desc, 'Credit/Debit') or contains(@text, 'Credit/Debit') " +
            "or contains(@content-desc, 'Required info') or contains(@text, 'Required info') " +
            "or @resource-id='btn_checkout_page_motor_payment_mode_direct_pay_post_card' " +
            "or @resource-id='btn_payment_mode_moyasar_direct_pay' " +
            "or @resource-id='btn_checkout_page_motor_pay_now' or @resource-id='btn_pay_now' " +
            "or @resource-id='tgl_checkout_page_motor_checkbox' or @resource-id='tgl_checkbox' " +
            "or @resource-id='textfield_checkout_page_motor_iban' or @resource-id='textfield_iban' " +
            "or @content-desc='Pay now' or @text='Pay now']"
        );
        boolean loaded = waitUntil(() -> !findWithUnimportantViews(checkoutReadyBy).isEmpty(), 20);
        if (!loaded) {
            System.out.println("Warning: Checkout Screen did not signal load within 20s");
        }
        return loaded;
    }

    public void clickTermsCheckbox() {
        System.out.println("Clicking Terms and Conditions checkbox on Checkout Screen...");
        org.openqa.selenium.By termsBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='tgl_checkout_page_motor_checkbox' or @resource-id='tgl_checkbox' " +
            "or contains(@resource-id, 'tgl') or contains(@resource-id, 'checkbox') or @class='android.widget.Switch']"
        );
        clickPrivacySwitch(termsBy);
    }

    public void clickPayNow() {
        if (isPaymentOrConfirmationAlreadyActive()) {
            System.out.println("Payment/confirmation already active; skipping duplicate Pay now click.");
            return;
        }
        System.out.println("Clicking Pay now button on Checkout Screen...");
        hideKeyboard();
        org.openqa.selenium.By clickablePayBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='btn_checkout_page_motor_pay_now' or @resource-id='btn_pay_now' or @content-desc='Pay now' or @text='Pay now']"
        );
        java.util.List<WebElement> payButtons = findWithUnimportantViews(clickablePayBy);
        if (payButtons.isEmpty()) {
            scrollDown();
            payButtons = findWithUnimportantViews(clickablePayBy);
        }
        if (!payButtons.isEmpty()) {
            for (int i = payButtons.size() - 1; i >= 0; i--) {
                WebElement btn = payButtons.get(i);
                try {
                    if (btn.isDisplayed()) {
                        System.out.println("Clicking Pay now button via locator...");
                        tapElement(btn);
                        try { Thread.sleep(2000); } catch (Exception ignore) {}
                        return;
                    }
                } catch (Exception ignore) {}
            }
            System.out.println("Clicking Pay now button via fallback locator match...");
            tapElement(payButtons.get(payButtons.size() - 1));
            try { Thread.sleep(2000); } catch (Exception ignore) {}
            return;
        }
        System.out.println("Pay now button not found on Checkout Screen after scrollDown.");
    }

    public void autoFillCheckout(utils.TestDataBuilder.CustomerProfile profile) {
        waitForPageToLoad();
        enterEmail(profile.email);
        enterIban(profile.iban);
        selectCreditDebitCard();
        clickTermsCheckbox();
        clickPayNow();
    }

    /**
     * Checks whether a specific payment method option (e.g., Tamara, Tabby, Credit, Mada) is visible on Checkout.
     */
    public boolean isPaymentMethodOptionVisible(String methodKeyword) {
        org.openqa.selenium.By methodBy = org.openqa.selenium.By.xpath(
            "//*[contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + methodKeyword.toLowerCase() + "') " +
            "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + methodKeyword.toLowerCase() + "') " +
            "or contains(translate(@resource-id, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + methodKeyword.toLowerCase() + "')]"
        );
        if (!findWithUnimportantViews(methodBy).isEmpty()) {
            return true;
        }
        scrollDown();
        return !findWithUnimportantViews(methodBy).isEmpty();
    }

    /**
     * Dynamically selects an alternative payment method (e.g. Tamara, Tabby, Credit/Debit Card, Mada) via locators.
     */
    public boolean selectPaymentMethod(String methodKeyword) {
        System.out.println("Selecting Payment Method matching: " + methodKeyword);
        org.openqa.selenium.By methodBy = org.openqa.selenium.By.xpath(
            "//*[contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + methodKeyword.toLowerCase() + "') " +
            "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + methodKeyword.toLowerCase() + "') " +
            "or contains(translate(@resource-id, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + methodKeyword.toLowerCase() + "')]"
        );
        List<WebElement> matches = findWithUnimportantViews(methodBy);
        if (matches.isEmpty()) {
            scrollDown();
            matches = findWithUnimportantViews(methodBy);
        }
        if (!matches.isEmpty()) {
            tapElement(matches.get(0));
            try { Thread.sleep(800); } catch (Exception ignore) {}
            System.out.println("Selected payment method: " + methodKeyword);
            return true;
        }
        System.out.println("Payment method option not available on this product/checkout: " + methodKeyword);
        return false;
    }

    /**
     * Checks if a payment failure, decline, or generic error modal is currently displayed.
     */
    public boolean isPaymentErrorModalDisplayed() {
        org.openqa.selenium.By errorBy = org.openqa.selenium.By.xpath(
            "//*[contains(@content-desc, 'Something Went Wrong') or contains(@text, 'Something Went Wrong') " +
            "or contains(@content-desc, 'Payment Failed') or contains(@text, 'Payment Failed') " +
            "or contains(@content-desc, 'Payment failed') or contains(@text, 'Payment failed') " +
            "or contains(@content-desc, 'Declined') or contains(@text, 'Declined') " +
            "or contains(@content-desc, 'declined') or contains(@text, 'declined') " +
            "or contains(@content-desc, 'Try again') or contains(@text, 'Try again') " +
            "or contains(@content-desc, 'Retry') or contains(@text, 'Retry') " +
            "or contains(@content-desc, 'Invalid') or contains(@text, 'Invalid')]"
        );
        return waitUntil(() -> !driver.findElements(errorBy).isEmpty(), 8);
    }

    /**
     * Dismisses a payment failure / error modal or taps Try Again / Retry to recover back to Checkout.
     */
    public boolean dismissPaymentErrorOrRetry() {
        org.openqa.selenium.By actionBy = org.openqa.selenium.By.xpath(
            "//android.widget.Button[@content-desc='Try again' or @text='Try again' or @content-desc='Retry' or @text='Retry' or @content-desc='Close' or @text='Close' or @content-desc='OK' or @text='OK'] " +
            "| //*[@resource-id='btn_close' or @content-desc='Try again' or @text='Try again' or @content-desc='Close' or @text='Close']"
        );
        List<WebElement> buttons = driver.findElements(actionBy);
        if (!buttons.isEmpty()) {
            tapElement(buttons.get(0));
            try { Thread.sleep(1000); } catch (Exception ignore) {}
            return true;
        }
        return false;
    }

    /**
     * Verifies whether the Checkout screen is currently active.
     */
    public boolean isCheckoutScreenDisplayed() {
        org.openqa.selenium.By checkoutBy = org.openqa.selenium.By.xpath(
            "//*[@content-desc='Checkout' or @text='Checkout' or @resource-id='btn_pay_now' or @content-desc='Pay now' or @text='Pay now' or contains(@content-desc, 'Payment Method')]"
        );
        return waitUntil(() -> !findWithUnimportantViews(checkoutBy).isEmpty(), 8);
    }

    public void expandOrderSummaryDetails() {
        org.openqa.selenium.By promoBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='textfield_checkout_page_motor_promo_code' or @resource-id='textfield_promo_code']"
        );
        if (findDisplayedElement(promoBy) != null) {
            System.out.println("Order Summary Details already expanded (promo field visible).");
            return;
        }
        org.openqa.selenium.By detailsBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='btn_checkout_page_motor_detials_btn' or @resource-id='btn_detials_btn' or @content-desc='Details' or @text='Details']"
        );
        waitUntil(() -> findDisplayedElement(promoBy) != null || findDisplayedElement(detailsBy) != null, 3);
        if (findDisplayedElement(promoBy) != null) {
            System.out.println("Order Summary Details already expanded (promo field visible).");
            return;
        }
        WebElement detailsBtn = findDisplayedElement(detailsBy);
        if (detailsBtn == null) {
            scrollUp();
            detailsBtn = findDisplayedElement(detailsBy);
        }
        if (detailsBtn != null) {
            System.out.println("Expanding Order Summary Details on Checkout screen...");
            tapElement(detailsBtn);
            waitUntil(() -> findDisplayedElement(promoBy) != null, 4);
        } else {
            System.out.println("Warning: Order Summary Details button not found on Checkout screen.");
        }
    }

    public void enterPromoCode(String promoCode) {
        System.out.println("Entering promo code on Checkout: " + promoCode);
        org.openqa.selenium.By promoBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='textfield_checkout_page_motor_promo_code' or @resource-id='textfield_promo_code' "
            + "or contains(@hint, 'Promo') or contains(@hint, 'promo')]"
        );
        WebElement field = findDisplayedElement(promoBy);
        if (field == null) {
            expandOrderSummaryDetails();
            field = findDisplayedElement(promoBy);
        }
        if (field == null) {
            scrollUp();
            field = findDisplayedElement(promoBy);
        }
        if (field == null) {
            throw new org.openqa.selenium.NoSuchElementException("Promo code field not found on CheckoutScreen.");
        }
        sendKeys(field, promoCode);
        hideKeyboard();
    }

    public void clickApplyPromoCode() {
        System.out.println("Clicking Apply promo code button on Checkout...");
        hideKeyboard();
        org.openqa.selenium.By clickableApplyBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='btn_apply_button' or @resource-id='btn_apply_promo']//*[@clickable='true'] "
            + "| //android.widget.Button[@clickable='true' and (@content-desc='Apply' or @text='Apply')]"
        );
        org.openqa.selenium.By fallbackApplyBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='btn_apply_button' or @resource-id='btn_apply_promo' or @content-desc='Apply' or @text='Apply']"
        );
        org.openqa.selenium.By compositePromoBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='textfield_checkout_page_motor_promo_code' or (contains(@hint, 'promo') and contains(@hint, 'Apply'))]"
        );

        WebElement applyBtn = findDisplayedElement(clickableApplyBy);
        if (applyBtn == null) {
            applyBtn = findDisplayedElement(fallbackApplyBy);
        }
        if (applyBtn != null) {
            tapElement(applyBtn);
            try { Thread.sleep(1500); } catch (Exception ignore) {}
            return;
        }

        WebElement compositePromo = findDisplayedElement(compositePromoBy);
        if (compositePromo == null) {
            scrollUp();
            applyBtn = findDisplayedElement(clickableApplyBy);
            if (applyBtn == null) {
                applyBtn = findDisplayedElement(fallbackApplyBy);
            }
            if (applyBtn != null) {
                tapElement(applyBtn);
                try { Thread.sleep(1500); } catch (Exception ignore) {}
                return;
            }
            compositePromo = findDisplayedElement(compositePromoBy);
        }

        if (compositePromo != null) {
            org.openqa.selenium.Rectangle r = compositePromo.getRect();
            int applyX = r.getX() + (int) (r.getWidth() * 0.88);
            int applyY = r.getY() + (r.getHeight() / 2);
            System.out.println("Tapping composite Checkout promo Apply region at dynamic rect (" + applyX + ", " + applyY + ")...");
            tapCoordinates(applyX, applyY);
            try { Thread.sleep(1500); } catch (Exception ignore) {}
            return;
        }

        throw new org.openqa.selenium.NoSuchElementException("Apply promo button (btn_apply_button / composite promo Apply) not found on CheckoutScreen.");
    }

    public boolean isPromoCodeInvalid() {
        try {
            org.openqa.selenium.By invalidPromoBy = org.openqa.selenium.By.xpath(
                "//*[not(@resource-id='textfield_checkout_page_motor_promo_code' or @resource-id='textfield_promo_code') "
                + "and (contains(@resource-id, 'promo_error') "
                + "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'invalid') "
                + "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'invalid') "
                + "or contains(translate(@hint, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'invalid') "
                + "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'not valid') "
                + "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'not valid') "
                + "or contains(translate(@hint, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'not valid') "
                + "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'incorrect') "
                + "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'incorrect') "
                + "or contains(translate(@hint, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'incorrect') "
                + "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'expired') "
                + "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'expired') "
                + "or contains(translate(@hint, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'expired') "
                + "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'not found') "
                + "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'not found') "
                + "or contains(translate(@hint, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'not found') "
                + "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'does not exist') "
                + "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'does not exist') "
                + "or contains(translate(@hint, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'does not exist') "
                + "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'something went wrong') "
                + "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'something went wrong'))] "
                + "| //*[@resource-id='textfield_checkout_page_motor_promo_code' or @resource-id='textfield_promo_code']"
                + "[contains(translate(@hint, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'invalid') "
                + "or contains(translate(@hint, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'not valid') "
                + "or contains(translate(@hint, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'incorrect') "
                + "or contains(translate(@hint, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'expired') "
                + "or contains(translate(@hint, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'not found') "
                + "or contains(translate(@hint, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'does not exist')]"
            );
            return waitUntil(() -> findDisplayedElement(invalidPromoBy) != null, 6);
        } catch (Exception e) {
            return false;
        }
    }
}
