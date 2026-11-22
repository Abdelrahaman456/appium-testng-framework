package pages;



import io.appium.java_client.pagefactory.AndroidFindBy;

import io.appium.java_client.pagefactory.AppiumFieldDecorator;

import org.openqa.selenium.WebElement;

import org.openqa.selenium.support.PageFactory;



import java.time.Duration;



public class QuoteScreen extends BasePage {



    public QuoteScreen() {

        super();

        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(2)), this);

    }



    // Placeholder locators based on the screenshot provided by the user.

    // Update these with accurate resource-ids once you inspect the XML in Appium.

    

    @AndroidFindBy(xpath = "//*[@content-desc=\"Here's your quote\"]")

    private WebElement pageHeader;



    @AndroidFindBy(xpath = "//*[contains(@content-desc, 'Smart Cover') or contains(@text, 'Smart Cover')]")

    private WebElement smartCoverOption;



    @AndroidFindBy(xpath = "//*[contains(@content-desc, 'Saver Cover') or contains(@text, 'Saver Cover')]")

    private WebElement saverCoverOption;



    @AndroidFindBy(xpath = "//*[contains(@content-desc, 'Comprehensive Cover') or contains(@text, 'Comprehensive Cover')]")

    private WebElement comprehensiveCoverOption;



    @AndroidFindBy(xpath = "//*[contains(@content-desc, 'Politically Exposed Person') or contains(@text, 'Politically Exposed Person')]")
    private WebElement pepQuestionText;

    @AndroidFindBy(xpath = "//*[contains(@content-desc, 'related party') or contains(@text, 'related party') or contains(@content-desc, 'Related') or contains(@text, 'Related')]")
    private WebElement relatedPartyQuestionText;

    @AndroidFindBy(xpath = "(//*[@resource-id='btn_yes_button' or @content-desc='Yes' or @text='Yes'])[1]")
    private WebElement pepYesButton;

    @AndroidFindBy(xpath = "(//*[@resource-id='btn_no_button' or @content-desc='No' or @text='No'])[1]")
    private WebElement pepNoButton;

    @AndroidFindBy(xpath = "(//*[@resource-id='btn_yes_button' or @content-desc='Yes' or @text='Yes'])[2]")
    private WebElement relatedPartyYesButton;

    @AndroidFindBy(xpath = "(//*[@resource-id='btn_no_button' or @content-desc='No' or @text='No'])[2]")
    private WebElement relatedPartyNoButton;

    @AndroidFindBy(xpath = "//android.widget.Button[@clickable='true' and (@content-desc='Buy now' or @text='Buy now')] | //*[@resource-id='btn_quote_page_motor_buy_now' or @resource-id='btn_buy_now' or @content-desc='Buy now' or @text='Buy now']")
    private WebElement buyNowButton;



    @AndroidFindBy(xpath = "//*[@resource-id='btn_upsell_skip' or @content-desc='Skip' or @text='Skip' or contains(@content-desc, 'Skip') or contains(@text, 'Skip')]")
    private WebElement skipUpsellButton;

    // --- Repair Options ---
    @AndroidFindBy(xpath = "//*[contains(@content-desc, 'Agency Repair') or contains(@text, 'Agency Repair') or contains(@resource-id, 'agency_repair')]")
    private WebElement agencyRepairOption;

    @AndroidFindBy(xpath = "//*[contains(@content-desc, 'Workshop') or contains(@text, 'Workshop') or contains(@resource-id, 'workshop_repair')]")
    private WebElement workshopRepairOption;

    // --- Add-on Covers ---
    @AndroidFindBy(xpath = "//*[contains(@content-desc, 'Roadside') or contains(@text, 'Roadside') or contains(@resource-id, 'addon_roadside')]")
    private WebElement roadsideAssistanceAddon;

    @AndroidFindBy(xpath = "//*[contains(@content-desc, 'Replacement') or contains(@text, 'Replacement') or contains(@content-desc, 'Hire Car') or contains(@resource-id, 'addon_replacement')]")
    private WebElement replacementCarAddon;

    @AndroidFindBy(xpath = "//*[contains(@content-desc, 'Personal Accident') or contains(@text, 'Personal Accident') or contains(@resource-id, 'addon_personal_accident')]")
    private WebElement personalAccidentAddon;

    @AndroidFindBy(xpath = "//*[contains(@content-desc, 'Glass') or contains(@text, 'Glass') or contains(@content-desc, 'Windshield') or contains(@resource-id, 'addon_glass')]")
    private WebElement glassCoverageAddon;

    // --- Promo Code ---
    @AndroidFindBy(xpath = "//*[@resource-id='textfield_promo_code' or contains(@hint, 'Promo') or contains(@hint, 'promo') or contains(@resource-id, 'promo')]")
    private WebElement promoCodeField;

    @AndroidFindBy(xpath = "//*[@resource-id='btn_apply_promo' or @content-desc='Apply' or @text='Apply' or contains(@content-desc, 'Apply')]")
    private WebElement applyPromoButton;

    @AndroidFindBy(xpath = "//*[contains(@content-desc, 'Promo applied') or contains(@text, 'Promo applied') or contains(@content-desc, 'Discount') or contains(@text, 'Discount')]")
    private WebElement promoSuccessBadge;

    @AndroidFindBy(xpath = "//*[contains(@content-desc, 'Invalid promo') or contains(@text, 'Invalid promo') or contains(@content-desc, 'Invalid code') or contains(@text, 'Invalid code') or contains(@resource-id, 'promo_error')]")
    private WebElement promoErrorBadge;

    // --- Premium & Deductible ---
    @AndroidFindBy(xpath = "//*[contains(@content-desc, 'SAR') or contains(@text, 'SAR') or contains(@resource-id, 'total_premium')]")
    private WebElement totalPremiumText;

    public void selectAgencyRepair() {
        System.out.println("Selecting Agency Repair option...");
        try {
            waitForVisibility(agencyRepairOption);
            tapElement(agencyRepairOption);
        } catch (Exception e) {
            try {
                WebElement el = driver.findElement(org.openqa.selenium.By.xpath("//*[contains(@content-desc, 'Agency') or contains(@text, 'Agency')]"));
                tapElement(el);
            } catch (Exception ignore) {}
        }
    }

    public void selectWorkshopRepair() {
        System.out.println("Selecting Workshop Repair option...");
        try {
            waitForVisibility(workshopRepairOption);
            tapElement(workshopRepairOption);
        } catch (Exception e) {
            try {
                WebElement el = driver.findElement(org.openqa.selenium.By.xpath("//*[contains(@content-desc, 'Workshop') or contains(@text, 'Workshop')]"));
                tapElement(el);
            } catch (Exception ignore) {}
        }
    }

    public void toggleRoadsideAssistanceAddon() {
        System.out.println("Toggling Roadside Assistance Add-on...");
        try {
            waitForVisibility(roadsideAssistanceAddon);
            tapElement(roadsideAssistanceAddon);
        } catch (Exception ignore) {}
    }

    public void toggleReplacementCarAddon() {
        System.out.println("Toggling Car Replacement Add-on...");
        try {
            waitForVisibility(replacementCarAddon);
            tapElement(replacementCarAddon);
        } catch (Exception ignore) {}
    }

    public void togglePersonalAccidentAddon() {
        System.out.println("Toggling Personal Accident Add-on...");
        try {
            waitForVisibility(personalAccidentAddon);
            tapElement(personalAccidentAddon);
        } catch (Exception ignore) {}
    }

    public void toggleGlassCoverageAddon() {
        System.out.println("Toggling Glass Coverage Add-on...");
        try {
            waitForVisibility(glassCoverageAddon);
            tapElement(glassCoverageAddon);
        } catch (Exception ignore) {}
    }

    public void selectDeductible(String amount) {
        System.out.println("Selecting Deductible amount: " + amount);
        try {
            org.openqa.selenium.By dedBy = org.openqa.selenium.By.xpath("//*[contains(@content-desc, '" + amount + "') or contains(@text, '" + amount + "')]");
            WebElement dedEl = driver.findElement(dedBy);
            tapElement(dedEl);
        } catch (Exception e) {
            System.out.println("Deductible option not found or default retained: " + e.getMessage());
        }
    }

    public void enterPromoCode(String promoCode) {
        System.out.println("Entering promo code: " + promoCode);
        try {
            if (!isElementVisible(promoCodeField)) {
                scrollDown();
            }
            waitForVisibility(promoCodeField);
            sendKeys(promoCodeField, promoCode);
            hideKeyboard();
        } catch (Exception e) {
            try {
                WebElement el = driver.findElement(org.openqa.selenium.By.xpath("//*[@resource-id='textfield_promo_code' or contains(@hint, 'Promo') or contains(@hint, 'promo')]"));
                sendKeys(el, promoCode);
                hideKeyboard();
            } catch (Exception ignore) {}
        }
    }

    public void clickApplyPromoCode() {
        System.out.println("Clicking Apply promo code button...");
        try {
            waitForVisibility(applyPromoButton);
            tapElement(applyPromoButton);
        } catch (Exception e) {
            try {
                WebElement el = driver.findElement(org.openqa.selenium.By.xpath("//*[@content-desc='Apply' or @text='Apply']"));
                tapElement(el);
            } catch (Exception ignore) {}
        }
        try { Thread.sleep(1500); } catch (Exception ignore) {}
    }

    public boolean isPromoCodeApplied() {
        try {
            return isElementVisible(promoSuccessBadge) || !driver.findElements(org.openqa.selenium.By.xpath("//*[contains(@content-desc, 'applied') or contains(@text, 'applied') or contains(@content-desc, 'Discount')]")).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPromoCodeInvalid() {
        try {
            org.openqa.selenium.By invalidPromoBy = org.openqa.selenium.By.xpath(
                "//*[contains(@resource-id, 'promo_error') " +
                "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'invalid') " +
                "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'invalid') " +
                "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'not valid') " +
                "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'not valid') " +
                "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'incorrect') " +
                "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'incorrect') " +
                "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'expired') " +
                "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'expired') " +
                "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'not found') " +
                "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'not found')]"
            );
            return isElementVisible(promoErrorBadge)
                    || waitUntil(() -> !findWithUnimportantViews(invalidPromoBy).isEmpty(), 4);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isQuoteVisibleNow() {
        try {
            org.openqa.selenium.By quoteIndicator = org.openqa.selenium.By.xpath(
                "//*[@content-desc=\"Here's your quote\" or @text=\"Here's your quote\" " +
                "or @resource-id='btn_quote_page_motor_buy_now' " +
                "or contains(@content-desc, 'Smart Cover') or contains(@text, 'Smart Cover') " +
                "or contains(@content-desc, 'Saver Cover') or contains(@text, 'Saver Cover') " +
                "or contains(@content-desc, 'Comprehensive Cover') or contains(@text, 'Comprehensive Cover')]"
            );
            return !driver.findElements(quoteIndicator).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isUnderwritingOrReferralDisplayed() {
        org.openqa.selenium.By referralBy = org.openqa.selenium.By.xpath(
            "//*[contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'referral') " +
            "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'referral') " +
            "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'underwriting') " +
            "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'underwriting') " +
            "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'manual review') " +
            "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'manual review') " +
            "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'not eligible') " +
            "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'not eligible') " +
            "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'cannot') " +
            "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'cannot') " +
            "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'sorry') " +
            "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'sorry') " +
            "or contains(@content-desc, 'Something Went Wrong') or contains(@text, 'Something Went Wrong')]"
        );
        return waitUntil(() -> !findWithUnimportantViews(referralBy).isEmpty(), 5);
    }

    public String getTotalPremiumText() {
        try {
            java.util.List<WebElement> els = driver.findElements(org.openqa.selenium.By.xpath(
                "//*[contains(@content-desc, 'SAR') or contains(@text, 'SAR') or contains(@resource-id, 'total_premium')]"
            ));
            for (WebElement el : els) {
                String t = el.getText();
                if (t != null && !t.trim().isEmpty()) {
                    return t.trim();
                }
                String cd = el.getAttribute("content-desc");
                if (cd != null && !cd.trim().isEmpty()) {
                    return cd.trim();
                }
            }
        } catch (Exception ignore) {}
        return "";
    }



    public boolean isPageLoaded() {
        System.out.println("Polling up to 45s for Quote Screen to load (waiting for quotes)...");
        try {
            org.openqa.selenium.By quoteIndicator = org.openqa.selenium.By.xpath(
                "//*[@resource-id='btn_quote_page_motor_buy_now' " +
                "or contains(@content-desc, 'Smart Cover') or contains(@text, 'Smart Cover') " +
                "or contains(@content-desc, 'Saver Cover') or contains(@text, 'Saver Cover') " +
                "or contains(@content-desc, 'Comprehensive Cover') or contains(@text, 'Comprehensive Cover') " +
                "or @content-desc='Buy now' or @text='Buy now']"
            );
            boolean loaded = waitUntil(() -> isElementVisible(quoteIndicator), 45);
            if (loaded) {
                try { Thread.sleep(700); } catch (Exception ignore) {}
            }
            return loaded;
        } catch (Exception e) {
            System.out.println("Quote screen failed to load: " + e.getMessage());
            return false;
        }
    }



    private boolean isCoverCardExpanded(String coverName) {
        try {
            org.openqa.selenium.By coverHeaderBy = org.openqa.selenium.By.xpath(
                "//*[contains(@content-desc, '" + coverName + "') or contains(@text, '" + coverName + "')]"
            );
            java.util.List<WebElement> headers = findWithUnimportantViews(coverHeaderBy);
            WebElement coverHeader = null;
            for (WebElement h : headers) {
                if (h.isDisplayed()) {
                    coverHeader = h;
                    break;
                }
            }
            if (coverHeader == null) {
                return false;
            }
            int coverTop = coverHeader.getRect().getY();

            // Determine where the next cover card starts below this card so we never match another cover's Buy now/details
            int nextCoverTop = Integer.MAX_VALUE;
            for (String otherCover : new String[]{"Comprehensive Cover", "Smart Cover", "Saver Cover"}) {
                if (otherCover.equalsIgnoreCase(coverName)) {
                    continue;
                }
                org.openqa.selenium.By otherBy = org.openqa.selenium.By.xpath(
                    "//*[contains(@content-desc, '" + otherCover + "') or contains(@text, '" + otherCover + "')]"
                );
                for (WebElement otherEl : findWithUnimportantViews(otherBy)) {
                    try {
                        if (otherEl.isDisplayed()) {
                            int oy = otherEl.getRect().getY();
                            if (oy > coverTop + 20 && oy < nextCoverTop) {
                                nextCoverTop = oy;
                            }
                        }
                    } catch (Exception ignore) {}
                }
            }

            org.openqa.selenium.By buyNowMarker = org.openqa.selenium.By.xpath(
                "//android.widget.Button[@clickable='true' and (@content-desc='Buy now' or @text='Buy now')] " +
                "| //*[@resource-id='btn_quote_page_motor_buy_now' or @resource-id='btn_buy_now' or @content-desc='Buy now' or @text='Buy now']"
            );
            for (WebElement bn : findWithUnimportantViews(buyNowMarker)) {
                try {
                    if (bn.isDisplayed()) {
                        int byY = bn.getRect().getY();
                        if (byY >= coverTop && byY < nextCoverTop) {
                            return true;
                        }
                    }
                } catch (Exception ignore) {}
            }

            org.openqa.selenium.By detailMarker = org.openqa.selenium.By.xpath(
                "//*[contains(@content-desc, 'Agency') or contains(@text, 'Agency') " +
                "or contains(@content-desc, 'Workshop') or contains(@text, 'Workshop') " +
                "or contains(@content-desc, 'Vehicle value') or contains(@text, 'Vehicle value') " +
                "or contains(@content-desc, 'Deductible') or contains(@text, 'Deductible')]"
            );
            for (WebElement det : findWithUnimportantViews(detailMarker)) {
                try {
                    if (det.isDisplayed()) {
                        String desc = det.getAttribute("content-desc");
                        String txt = det.getText();
                        boolean isHeaderItself = (desc != null && desc.contains(coverName)) || (txt != null && txt.contains(coverName));
                        int dy = det.getRect().getY();
                        if (!isHeaderItself && dy > coverTop + 20 && dy < nextCoverTop) {
                            return true;
                        }
                    }
                } catch (Exception ignore) {}
            }
        } catch (Exception ignore) {}
        return false;
    }

    private void selectCoverCard(String coverName, WebElement fallbackOption, boolean skipIfExpanded) {
        org.openqa.selenium.By coverHeaderBy = org.openqa.selenium.By.xpath(
            "//*[contains(@content-desc, '" + coverName + "') or contains(@text, '" + coverName + "')]"
        );
        if (!isElementVisible(coverHeaderBy)) {
            scrollUp();
        }
        waitForVisibility(fallbackOption);
        if (skipIfExpanded && isCoverCardExpanded(coverName)) {
            System.out.println(coverName + " is already expanded (its Buy now / details are visible); skipping toggle tap.");
            return;
        }
        org.openqa.selenium.By clickableCardBy = org.openqa.selenium.By.xpath(
            "//*[@clickable='true' and (contains(@content-desc, '" + coverName + "') or contains(@text, '" + coverName + "'))]"
        );
        java.util.List<WebElement> clickableNodes = findWithUnimportantViews(clickableCardBy);
        WebElement target = clickableNodes.isEmpty() ? fallbackOption : clickableNodes.get(0);
        tapElement(target);
        if (waitUntil(() -> isCoverCardExpanded(coverName), 3)) {
            return;
        }
        System.out.println(coverName + " did not expand on first tap; trying native click / alternate card node...");
        try {
            target.click();
        } catch (Exception ignore) {}
        if (waitUntil(() -> isCoverCardExpanded(coverName), 2)) {
            return;
        }
        try {
            java.util.List<WebElement> allMatches = findWithUnimportantViews(coverHeaderBy);
            for (WebElement node : allMatches) {
                System.out.println("Quote card candidate [" + coverName + "]: class=" + node.getAttribute("className")
                    + " clickable=" + node.getAttribute("clickable") + " bounds=" + node.getAttribute("bounds"));
            }
            if (!allMatches.isEmpty()) {
                tapElement(allMatches.get(allMatches.size() - 1));
                waitUntil(() -> isCoverCardExpanded(coverName), 2);
            }
        } catch (Exception e) {
            System.out.println("Retry expand failed for " + coverName + ": " + e.getMessage());
        }
    }



    public void selectSmartCover() {
        selectCoverCard("Smart Cover", smartCoverOption, false);
    }



    public void selectSaverCover() {
        selectCoverCard("Saver Cover", saverCoverOption, false);
    }



    public void selectComprehensiveCover() {
        // Comprehensive Cover may expand by default on Motor Quote Screen; tapping again collapses it.
        selectCoverCard("Comprehensive Cover", comprehensiveCoverOption, true);
    }



    public void answerPepQuestion(boolean isYes) {
        org.openqa.selenium.By pepLocator = org.openqa.selenium.By.xpath(
            "//*[contains(@content-desc, 'Politically Exposed') or contains(@text, 'Politically Exposed') " +
            "or @resource-id='btn_yes_button' or @content-desc='Yes']"
        );
        for (int i = 0; i < 3; i++) {
            if (!driver.findElements(pepLocator).isEmpty()) {
                break;
            }
            scrollDown();
            try { Thread.sleep(500); } catch (Exception ignore) {}
        }
        if (isYes) {
            click(pepYesButton);
        } else {
            click(pepNoButton);
        }
    }

    public void answerRelatedPartyQuestion(boolean isYes) {
        org.openqa.selenium.By relatedLocator = org.openqa.selenium.By.xpath(
            "//*[contains(@content-desc, 'related party') or contains(@text, 'related party')] " +
            "| (//*[@resource-id='btn_yes_button' or @content-desc='Yes' or @text='Yes'])[2]"
        );
        for (int i = 0; i < 3; i++) {
            if (!driver.findElements(relatedLocator).isEmpty()) {
                break;
            }
            scrollDown();
            try { Thread.sleep(500); } catch (Exception ignore) {}
        }
        if (isYes) {
            click(relatedPartyYesButton);
        } else {
            click(relatedPartyNoButton);
        }
    }



    public void clickBuyNow() {
        System.out.println("Scrolling down to reveal Buy Now button...");
        org.openqa.selenium.By clickableBuyNowBy = org.openqa.selenium.By.xpath(
            "//android.widget.Button[@clickable='true' and (@content-desc='Buy now' or @text='Buy now')]"
        );
        org.openqa.selenium.By buyNowBy = org.openqa.selenium.By.xpath(
            "//android.widget.Button[@clickable='true' and (@content-desc='Buy now' or @text='Buy now')] " +
            "| //*[@resource-id='btn_quote_page_motor_buy_now' or @resource-id='btn_buy_now' or @content-desc='Buy now' or @text='Buy now']"
        );
        org.openqa.selenium.By quoteHeaderBy = org.openqa.selenium.By.xpath(
            "//*[@content-desc=\"Here's your quote\" or @text=\"Here's your quote\"]"
        );
        org.openqa.selenium.By postQuoteBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='btn_upsell_skip' or @content-desc='Skip' or @text='Skip' " +
            "or @resource-id='btn_checkout_page_motor_pay_now' or @resource-id='btn_pay_now' " +
            "or @content-desc='Pay now' or @text='Pay now' " +
            "or @resource-id='textfield_checkout_page_motor_email' or @resource-id='textfield_email']"
        );

        org.openqa.selenium.By referralDialogBy = org.openqa.selenium.By.xpath(
            "//*[contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'referral') " +
            "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'referral') " +
            "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'underwriting') " +
            "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'underwriting') " +
            "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'manual review') " +
            "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'manual review') " +
            "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'not eligible') " +
            "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'not eligible') " +
            "or contains(@content-desc, 'Something Went Wrong') or contains(@text, 'Something Went Wrong')]"
        );

        boolean tappedBuyNow = false;
        for (int i = 0; i < 5; i++) {
            WebElement target = null;
            java.util.List<WebElement> preferred = driver.findElements(clickableBuyNowBy);
            if (!preferred.isEmpty() && preferred.get(0).isDisplayed()) {
                target = preferred.get(0);
            } else {
                java.util.List<WebElement> found = findWithUnimportantViews(buyNowBy);
                if (!found.isEmpty()) {
                    target = found.get(0);
                }
            }
            if (target != null) {
                if ("false".equalsIgnoreCase(target.getAttribute("enabled"))) {
                    System.out.println("Buy Now button is currently disabled.");
                    return;
                }
                System.out.println("Buy Now button located! Tapping...");
                tapElement(target);
                tappedBuyNow = true;
                boolean transitioned = waitUntil(() -> !driver.findElements(postQuoteBy).isEmpty()
                        || driver.findElements(quoteHeaderBy).isEmpty()
                        || !driver.findElements(referralDialogBy).isEmpty(), 4);
                if (transitioned) {
                    return;
                }
                System.out.println("Still on Quote screen after Buy Now tap; retrying tap...");
                java.util.List<WebElement> retry = findWithUnimportantViews(buyNowBy);
                if (!retry.isEmpty()) {
                    tapElement(retry.get(0));
                    if (waitUntil(() -> !driver.findElements(postQuoteBy).isEmpty()
                            || driver.findElements(quoteHeaderBy).isEmpty()
                            || !driver.findElements(referralDialogBy).isEmpty(), 4)) {
                        return;
                    }
                }
            }
            System.out.println("Scrolling down (attempt " + (i + 1) + ")...");
            scrollDown();
        }
        if (tappedBuyNow) {
            return;
        }
        click(buyNowButton);
    }

    public void clickSkipUpsell() {
        System.out.println("Checking briefly for Upsell popup...");
        org.openqa.selenium.By skipBy = org.openqa.selenium.By.xpath(
            "//android.widget.Button[@clickable='true' and (contains(@content-desc, 'Skip') or contains(@text, 'Skip'))] " +
            "| //*[@resource-id='btn_upsell_skip' or @content-desc='Skip' or @text='Skip']"
        );
        org.openqa.selenium.By checkoutReadyBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='btn_checkout_page_motor_pay_now' or @resource-id='btn_pay_now' " +
            "or @content-desc='Pay now' or @text='Pay now' " +
            "or @resource-id='textfield_checkout_page_motor_email' or @resource-id='textfield_email' " +
            "or @resource-id='textfield_checkout_page_motor_iban' or @resource-id='textfield_iban']"
        );
        try {
            long deadline = System.currentTimeMillis() + 4000L;
            while (System.currentTimeMillis() < deadline) {
                if (!driver.findElements(checkoutReadyBy).isEmpty()) {
                    System.out.println("Checkout already visible; no Upsell popup.");
                    return;
                }
                java.util.List<WebElement> skips = findWithUnimportantViews(skipBy);
                if (!skips.isEmpty()) {
                    System.out.println("Upsell popup found. Clicking Skip...");
                    tapElement(skips.get(0));
                    return;
                }
                Thread.sleep(250);
            }
            System.out.println("No Upsell popup appeared. Proceeding...");
        } catch (Exception e) {
            System.out.println("No Upsell popup appeared. Proceeding...");
        }
    }

}

