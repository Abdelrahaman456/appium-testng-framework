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

    @AndroidFindBy(xpath = "//*[@resource-id='btn_quote_page_motor_politically_exposed_person_yes'] | (//*[@resource-id='btn_yes_button' or @content-desc='Yes' or @text='Yes'])[1]")
    private WebElement pepYesButton;

    @AndroidFindBy(xpath = "//*[@resource-id='btn_quote_page_motor_politically_exposed_person_no'] | (//*[@resource-id='btn_no_button' or @content-desc='No' or @text='No'])[1]")
    private WebElement pepNoButton;

    @AndroidFindBy(xpath = "//*[@resource-id='btn_quote_page_motor_party_person_yes'] | (//*[@resource-id='btn_yes_button' or @content-desc='Yes' or @text='Yes'])[2]")
    private WebElement relatedPartyYesButton;

    @AndroidFindBy(xpath = "//*[@resource-id='btn_quote_page_motor_party_person_no'] | (//*[@resource-id='btn_no_button' or @content-desc='No' or @text='No'])[2]")
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

    @AndroidFindBy(xpath = "//*[@resource-id='btn_apply_button' or @resource-id='btn_apply_promo' or @content-desc='Apply' or @text='Apply' or contains(@content-desc, 'Apply')]")
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

    public boolean hasPromoCodeField() {
        org.openqa.selenium.By promoBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='textfield_promo_code' or contains(@hint, 'Promo') or contains(@hint, 'promo') or contains(@resource-id, 'promo')]"
        );
        return findDisplayedElement(promoBy) != null;
    }

    public void enterPromoCode(String promoCode) {
        System.out.println("Entering promo code: " + promoCode);
        org.openqa.selenium.By promoBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='textfield_promo_code' or contains(@hint, 'Promo') or contains(@hint, 'promo') or contains(@resource-id, 'promo')]"
        );
        WebElement field = findDisplayedElement(promoBy);
        if (field == null) {
            scrollDown();
            field = findDisplayedElement(promoBy);
        }
        if (field == null) {
            throw new org.openqa.selenium.NoSuchElementException("Promo code field not found on QuoteScreen: " + promoBy);
        }
        sendKeys(field, promoCode);
        hideKeyboard();
    }

    public void clickApplyPromoCode() {
        System.out.println("Clicking Apply promo code button...");
        org.openqa.selenium.By clickableApplyBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='btn_apply_button' or @resource-id='btn_apply_promo']//*[@clickable='true'] "
            + "| //android.widget.Button[@clickable='true' and (@content-desc='Apply' or @text='Apply')]"
        );
        org.openqa.selenium.By fallbackApplyBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='btn_apply_button' or @resource-id='btn_apply_promo' or @content-desc='Apply' or @text='Apply']"
        );
        WebElement btn = findDisplayedElement(clickableApplyBy);
        if (btn == null) {
            btn = findDisplayedElement(fallbackApplyBy);
        }
        if (btn == null) {
            throw new org.openqa.selenium.NoSuchElementException("Apply promo code button not found on QuoteScreen.");
        }
        tapElement(btn);
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
            "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'required documents') " +
            "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'required documents') " +
            "or contains(@content-desc, 'National Address Certificate') or contains(@text, 'National Address Certificate') " +
            "or contains(@content-desc, 'IBAN Certificate') or contains(@text, 'IBAN Certificate') " +
            "or contains(@content-desc, 'Something Went Wrong') or contains(@text, 'Something Went Wrong')]"
        );
        return waitUntil(() -> !findWithUnimportantViews(referralBy).isEmpty(), 20);
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



    private void findAndTapDeclarationButton(org.openqa.selenium.By primaryBy, org.openqa.selenium.By fallbackBy,
            WebElement pageFactoryFallback, String label) {
        org.openqa.selenium.By belowDeclBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='dd_sales_your_details_page_political_exposed' or @resource-id='textfield_quote_page_motor_email']"
        );
        for (int i = 0; i < 3; i++) {
            WebElement btn = findDisplayedElement(primaryBy);
            if (btn == null) {
                btn = findDisplayedElement(fallbackBy);
            }
            if (btn != null) {
                System.out.println("Tapping declaration button: " + label);
                tapElement(btn);
                return;
            }
            if (i < 2 && findDisplayedElement(belowDeclBy) != null) {
                scrollUp();
            } else {
                scrollDown();
            }
            try { Thread.sleep(450); } catch (Exception ignore) {}
        }
        click(pageFactoryFallback);
    }

    private String getElementLabel(WebElement el) {
        if (el == null) {
            return "";
        }
        try {
            String desc = el.getAttribute("content-desc");
            if (desc != null && !desc.trim().isEmpty()) {
                return desc.trim();
            }
            String txt = el.getText();
            if (txt != null && !txt.trim().isEmpty()) {
                return txt.trim();
            }
        } catch (Exception ignore) {}
        return "";
    }

    private boolean isExcludedDropdownLabel(String label) {
        if (label == null || label.trim().isEmpty()) {
            return true;
        }
        String lower = label.trim().toLowerCase();
        if (lower.equals("scrim") || lower.equals("close") || lower.equals("back")
                || lower.equals("cancel") || lower.equals("confirm") || lower.equals("save")
                || lower.equals("done") || lower.equals("ok") || lower.equals("dismiss")
                || lower.equals("yes") || lower.equals("no") || lower.equals("buy now")
                || lower.equals("learn more")) {
            return true;
        }
        return lower.contains("select pep type")
                || lower.contains("politically exposed")
                || lower.contains("related party")
                || lower.contains("comprehensive cover")
                || lower.contains("smart cover")
                || lower.contains("saver cover");
    }

    private java.util.Set<String> collectVisibleClickableLabels() {
        java.util.Set<String> labels = new java.util.HashSet<>();
        try {
            for (WebElement el : findWithUnimportantViews(org.openqa.selenium.By.xpath("//*[@clickable='true']"))) {
                try {
                    if (el.isDisplayed()) {
                        String lbl = getElementLabel(el);
                        if (!lbl.isEmpty()) {
                            labels.add(lbl);
                        }
                    }
                } catch (Exception ignore) {}
            }
        } catch (Exception ignore) {}
        return labels;
    }

    private WebElement findDropdownOptionCandidate(java.util.Set<String> preOpenLabels) {
        org.openqa.selenium.By scrimOptionsBy = org.openqa.selenium.By.xpath(
            "//*[@content-desc='Scrim']/following::*[@clickable='true']"
        );
        for (WebElement el : findWithUnimportantViews(scrimOptionsBy)) {
            try {
                if (el.isDisplayed() && !isExcludedDropdownLabel(getElementLabel(el))) {
                    return el;
                }
            } catch (Exception ignore) {}
        }

        org.openqa.selenium.By anyClickableOptionBy = org.openqa.selenium.By.xpath(
            "//android.view.View[@clickable='true'] | //android.widget.Button[@clickable='true'] "
            + "| //android.widget.CheckedTextView[@clickable='true'] | //android.widget.TextView[@clickable='true']"
        );
        for (WebElement el : findWithUnimportantViews(anyClickableOptionBy)) {
            try {
                if (el.isDisplayed()) {
                    String lbl = getElementLabel(el);
                    if (!isExcludedDropdownLabel(lbl) && !preOpenLabels.contains(lbl)) {
                        return el;
                    }
                }
            } catch (Exception ignore) {}
        }
        return null;
    }

    private WebElement waitForNewDropdownOption(java.util.Set<String> preOpenLabels, int timeoutSec) {
        final WebElement[] found = new WebElement[1];
        waitUntil(() -> {
            found[0] = findDropdownOptionCandidate(preOpenLabels);
            return found[0] != null;
        }, timeoutSec);
        return found[0];
    }

    private void selectDropdownFirstOptionIfPresent(org.openqa.selenium.By clickableDropdownBy,
            org.openqa.selenium.By fallbackDropdownBy, String label) {
        WebElement dropdown = findDisplayedElement(clickableDropdownBy);
        if (dropdown == null) {
            dropdown = findDisplayedElement(fallbackDropdownBy);
        }
        if (dropdown == null) {
            scrollDown();
            try { Thread.sleep(400); } catch (Exception ignore) {}
            dropdown = findDisplayedElement(clickableDropdownBy);
            if (dropdown == null) {
                dropdown = findDisplayedElement(fallbackDropdownBy);
            }
        }
        if (dropdown == null) {
            System.out.println("No conditional dropdown found for " + label + "; continuing.");
            return;
        }

        try {
            int screenH = getSafeWindowSize().height;
            org.openqa.selenium.Rectangle r = dropdown.getRect();
            if (r.getY() + r.getHeight() >= screenH - 140) {
                scrollDown();
                try { Thread.sleep(400); } catch (Exception ignore) {}
                WebElement refreshed = findDisplayedElement(clickableDropdownBy);
                if (refreshed == null) {
                    refreshed = findDisplayedElement(fallbackDropdownBy);
                }
                if (refreshed != null) {
                    dropdown = refreshed;
                }
            }
        } catch (Exception ignore) {}

        java.util.Set<String> preOpenLabels = collectVisibleClickableLabels();

        System.out.println("Opening " + label + " dropdown...");
        tapElement(dropdown);
        try { Thread.sleep(700); } catch (Exception ignore) {}

        WebElement option = waitForNewDropdownOption(preOpenLabels, 3);
        if (option == null) {
            WebElement retryDropdown = findDisplayedElement(clickableDropdownBy);
            if (retryDropdown == null) {
                retryDropdown = findDisplayedElement(fallbackDropdownBy);
            }
            if (retryDropdown != null) {
                tapElement(retryDropdown);
                try { Thread.sleep(700); } catch (Exception ignore) {}
                option = waitForNewDropdownOption(preOpenLabels, 3);
            }
        }

        org.openqa.selenium.By confirmBy = org.openqa.selenium.By.xpath(
            "//android.widget.Button[@clickable='true' and (@content-desc='Confirm' or @text='Confirm' or @content-desc='Save' or @text='Save' or @content-desc='Done' or @text='Done')] "
            + "| //*[@resource-id='btn_confirm' or @content-desc='Confirm' or @text='Confirm']"
        );

        if (option != null) {
            System.out.println("Selecting " + label + " option: " + getElementLabel(option));
            tapElement(option);
            try { Thread.sleep(500); } catch (Exception ignore) {}
            WebElement confirmBtn = findDisplayedElement(confirmBy);
            if (confirmBtn != null) {
                System.out.println("Confirming " + label + " dropdown selection...");
                tapElement(confirmBtn);
                try { Thread.sleep(500); } catch (Exception ignore) {}
            }
        } else if (findDisplayedElement(org.openqa.selenium.By.xpath("//android.widget.SeekBar")) != null) {
            WebElement confirmBtn = findDisplayedElement(confirmBy);
            if (confirmBtn != null) {
                System.out.println("Confirming " + label + " wheel picker selection...");
                tapElement(confirmBtn);
                try { Thread.sleep(500); } catch (Exception ignore) {}
            }
        } else {
            System.out.println("Warning: No selectable option appeared after opening " + label + " dropdown.");
        }
    }

    public void answerPepQuestion(boolean isYes) {
        org.openqa.selenium.By primaryBy = isYes
            ? org.openqa.selenium.By.xpath("//*[@resource-id='btn_quote_page_motor_politically_exposed_person_yes']")
            : org.openqa.selenium.By.xpath("//*[@resource-id='btn_quote_page_motor_politically_exposed_person_no']");
        org.openqa.selenium.By fallbackBy = isYes
            ? org.openqa.selenium.By.xpath("(//*[@resource-id='btn_yes_button' or @content-desc='Yes' or @text='Yes'])[1]")
            : org.openqa.selenium.By.xpath("(//*[@resource-id='btn_no_button' or @content-desc='No' or @text='No'])[1]");
        findAndTapDeclarationButton(primaryBy, fallbackBy, isYes ? pepYesButton : pepNoButton,
                "PEP (" + (isYes ? "Yes" : "No") + ")");

        if (isYes) {
            try { Thread.sleep(600); } catch (Exception ignore) {}
            org.openqa.selenium.By clickablePepDropdownBy = org.openqa.selenium.By.xpath(
                "//*[@resource-id='dd_sales_your_details_page_political_exposed']//*[@clickable='true'] "
                + "| //android.widget.Button[@clickable='true' and (contains(@content-desc, 'Select PEP type') or contains(@text, 'Select PEP type'))]"
            );
            org.openqa.selenium.By fallbackPepDropdownBy = org.openqa.selenium.By.xpath(
                "//*[@resource-id='dd_sales_your_details_page_political_exposed' or contains(@content-desc, 'Select PEP type') or contains(@text, 'Select PEP type')]"
            );
            selectDropdownFirstOptionIfPresent(clickablePepDropdownBy, fallbackPepDropdownBy, "PEP type");

            fillConditionalPepEmailIfPresent();
        }
    }

    private void fillConditionalPepEmailIfPresent() {
        try { Thread.sleep(400); } catch (Exception ignore) {}
        org.openqa.selenium.By pepEmailBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='textfield_quote_page_motor_email' or contains(@hint, 'email ID') or contains(@hint, 'Email ID')]"
        );
        WebElement emailField = findDisplayedElement(pepEmailBy);
        if (emailField == null) {
            scrollDown();
            try { Thread.sleep(350); } catch (Exception ignore) {}
            emailField = findDisplayedElement(pepEmailBy);
        }
        if (emailField == null) {
            return;
        }
        try {
            String existing = emailField.getText();
            if (existing != null && existing.contains("@")) {
                System.out.println("Conditional PEP email field already populated: " + existing.trim());
                return;
            }
        } catch (Exception ignore) {}

        try {
            if (emailField.getRect().getY() > (int) (getSafeWindowSize().height * 0.70)) {
                scrollDown();
                try { Thread.sleep(350); } catch (Exception ignore) {}
                WebElement scrolledField = findDisplayedElement(pepEmailBy);
                if (scrolledField != null) {
                    emailField = scrolledField;
                }
            }
        } catch (Exception ignore) {}

        String email = utils.TestDataBuilder.DEFAULT_EMAIL;
        System.out.println("Filling conditional PEP email field (Attempt 1 via tapElement + adbInput): " + email);
        tapElement(emailField);
        try { Thread.sleep(700); } catch (Exception ignore) {}
        adbInput(email);
        try { Thread.sleep(500); } catch (Exception ignore) {}
        hideKeyboard();

        WebElement check1 = findDisplayedElement(pepEmailBy);
        try {
            if (check1 != null && check1.getText() != null && check1.getText().contains("@")) {
                System.out.println("Verified conditional PEP email on attempt 1: " + check1.getText().trim());
                return;
            }
        } catch (Exception ignore) {}

        System.out.println("Conditional PEP email empty after attempt 1; retrying Attempt 2 via post-keyboard sendKeys...");
        WebElement refreshed = findDisplayedElement(pepEmailBy);
        if (refreshed == null) {
            scrollDown();
            try { Thread.sleep(350); } catch (Exception ignore) {}
            refreshed = findDisplayedElement(pepEmailBy);
        }
        if (refreshed != null) {
            tapElement(refreshed);
            try { Thread.sleep(700); } catch (Exception ignore) {}
            WebElement postKeyboardField = findDisplayedElement(pepEmailBy);
            if (postKeyboardField != null) {
                try {
                    postKeyboardField.sendKeys(email);
                } catch (Exception e) {
                    adbInput(email);
                }
            } else {
                adbInput(email);
            }
            try { Thread.sleep(500); } catch (Exception ignore) {}
            hideKeyboard();

            WebElement check2 = findDisplayedElement(pepEmailBy);
            String verifiedText = "";
            try {
                if (check2 != null && check2.getText() != null) {
                    verifiedText = check2.getText().trim();
                }
            } catch (Exception ignore) {}
            System.out.println("Post-attempt-2 conditional PEP email text: '" + verifiedText + "'");
        }
    }

    public void answerRelatedPartyQuestion(boolean isYes) {
        org.openqa.selenium.By primaryBy = isYes
            ? org.openqa.selenium.By.xpath("//*[@resource-id='btn_quote_page_motor_party_person_yes']")
            : org.openqa.selenium.By.xpath("//*[@resource-id='btn_quote_page_motor_party_person_no']");
        org.openqa.selenium.By fallbackBy = isYes
            ? org.openqa.selenium.By.xpath("(//*[@resource-id='btn_yes_button' or @content-desc='Yes' or @text='Yes'])[2]")
            : org.openqa.selenium.By.xpath("(//*[@resource-id='btn_no_button' or @content-desc='No' or @text='No'])[2]");
        findAndTapDeclarationButton(primaryBy, fallbackBy, isYes ? relatedPartyYesButton : relatedPartyNoButton,
                "Related Party (" + (isYes ? "Yes" : "No") + ")");

        if (isYes) {
            try { Thread.sleep(500); } catch (Exception ignore) {}
            org.openqa.selenium.By secondaryClickableBy = org.openqa.selenium.By.xpath(
                "//*[(contains(@resource-id, 'party_person') or contains(@resource-id, 'related')) and starts-with(@resource-id, 'dd_')]//*[@clickable='true'] "
                + "| //android.widget.Button[@clickable='true' and contains(@content-desc, 'Select') and contains(@content-desc, '*') and not(contains(@content-desc, 'PEP'))]"
            );
            org.openqa.selenium.By secondaryFallbackBy = org.openqa.selenium.By.xpath(
                "//*[(contains(@resource-id, 'party_person') or contains(@resource-id, 'related')) and starts-with(@resource-id, 'dd_')]"
            );
            if (findDisplayedElement(secondaryClickableBy) != null || findDisplayedElement(secondaryFallbackBy) != null) {
                selectDropdownFirstOptionIfPresent(secondaryClickableBy, secondaryFallbackBy, "Related Party detail");
            }
        }
        fillConditionalPepEmailIfPresent();
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
            "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'required documents') " +
            "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'required documents') " +
            "or contains(@content-desc, 'National Address Certificate') or contains(@text, 'National Address Certificate') " +
            "or contains(@content-desc, 'IBAN Certificate') or contains(@text, 'IBAN Certificate') " +
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
            boolean atDeclarationsBottom = !driver.findElements(org.openqa.selenium.By.xpath(
                "//*[contains(@content-desc, 'Politically Exposed') or contains(@content-desc, 'related party') "
                + "or @resource-id='dd_sales_your_details_page_political_exposed' or @resource-id='textfield_quote_page_motor_email']"
            )).isEmpty();
            if (atDeclarationsBottom && i < 2) {
                System.out.println("Scrolling up from declarations to reveal Buy Now (attempt " + (i + 1) + ")...");
                scrollUp();
            } else {
                System.out.println("Scrolling down (attempt " + (i + 1) + ")...");
                scrollDown();
            }
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
            "or @resource-id='textfield_checkout_page_motor_iban' or @resource-id='textfield_iban' " +
            "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'required documents') " +
            "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'required documents') " +
            "or contains(@content-desc, 'National Address Certificate') or contains(@content-desc, 'IBAN Certificate')]"
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

