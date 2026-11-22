package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Page Object for Support Hub (FAQs, File/Track Complaint, Contact Us)
 * and Profile Localization (English LTR <-> Arabic RTL switching).
 * 100% pure Appium locators — zero hardcoded coordinate dependencies.
 */
public class SupportAndLocalizationScreen extends BasePage {

    private static final Pattern ARABIC_PATTERN = Pattern.compile("[\\u0600-\\u06FF]+");

    // Bottom Navigation Tabs (EN + AR)
    private final By homeTabEnBy = By.xpath("//*[@content-desc='Home' or @text='Home']");
    private final By supportTabBy = By.xpath("//*[@content-desc='Support' or @text='Support' or contains(@content-desc, 'الدعم')]");
    private final By profileTabBy = By.xpath("//*[@content-desc='My profile' or @text='My profile' or contains(@content-desc, 'حسابي') or contains(@content-desc, 'ملفي')]");

    // Support Hub Cards
    private final By faqsCardBy = By.xpath("//*[contains(@content-desc, 'FAQs') or contains(@text, 'FAQs')]");
    private final By fileComplaintCardBy = By.xpath("//*[contains(@content-desc, 'File a complaint') or contains(@text, 'File a complaint')]");
    private final By trackComplaintCardBy = By.xpath("//*[contains(@content-desc, 'Track Complaint') or contains(@text, 'Track Complaint')]");
    private final By contactUsCardBy = By.xpath("//*[contains(@content-desc, 'Contact us') or contains(@text, 'Contact us')]");

    // FAQs Screen Locators
    private final By faqsHeaderBy = By.xpath("//*[@content-desc='FAQs' or @text='FAQs']");
    private final By faqSearchInputBy = By.xpath("//*[@resource-id='textfield_searchkeywords' or contains(@hint, 'Search') or contains(@content-desc, 'Search')]");

    // Auth Gate (Login) Locators
    private final By loginScreenBy = By.xpath(
        "//*[@resource-id='btn_guest_login_or_register_part1' or @resource-id='textfield_national_id' " +
        "or contains(@content-desc, 'Welcome to') or @content-desc='Login']"
    );

    // Contact Us Screen & Error Modal Locators
    private final By contactUsHeaderBy = By.xpath("//*[contains(@content-desc, 'Contact us') or contains(@text, 'Contact us') or contains(@hint, 'Full Name')]");
    private final By modalCloseBtnBy = By.xpath(
        "//*[@resource-id='btn_close_button']//android.widget.Button | //android.widget.Button[@content-desc='Close' or @text='Close'] | //*[@resource-id='btn_close']"
    );

    // Profile & Language Toggle Locators
    private final By languageToggleBy = By.xpath(
        "//*[contains(@content-desc, 'EN') and contains(@content-desc, 'AR')]"
    );

    // =========================================================================
    // 1. TAB NAVIGATION
    // =========================================================================
    public SupportAndLocalizationScreen openSupportTab() {
        System.out.println("Navigating to Support tab via locator...");
        waitUntil(() -> isElementVisible(supportTabBy), 10);
        tapElement(getVisibleElement(supportTabBy));
        waitUntil(() -> isElementVisible(contactUsCardBy) || isElementVisible(faqsCardBy), 5);
        return this;
    }

    public SupportAndLocalizationScreen openMyProfileTab() {
        System.out.println("Navigating to My profile tab via locator...");
        waitUntil(() -> isElementVisible(profileTabBy), 10);
        tapElement(getVisibleElement(profileTabBy));
        waitUntil(() -> isElementVisible(languageToggleBy), 5);
        return this;
    }

    // =========================================================================
    // 2. FAQS MODULE
    // =========================================================================
    public SupportAndLocalizationScreen openFaqs() {
        System.out.println("Opening FAQs from Support tab...");
        if (!isElementVisible(faqsCardBy)) {
            scrollDown();
        }
        waitUntil(() -> isElementVisible(faqsCardBy), 8);
        tapElement(getVisibleElement(faqsCardBy));
        waitUntil(() -> isElementVisible(faqsHeaderBy) || isElementVisible(faqSearchInputBy), 6);
        return this;
    }

    public boolean isFaqsScreenDisplayed() {
        return waitUntil(() -> isElementVisible(faqsHeaderBy) || isElementVisible(faqSearchInputBy), 10);
    }

    public SupportAndLocalizationScreen selectFaqCategory(String categoryName) {
        System.out.println("Selecting FAQ category: " + categoryName);
        By catBy = By.xpath("//android.widget.Button[@content-desc='" + categoryName + "' or @text='" + categoryName + "'] | //*[contains(@content-desc, '" + categoryName + "')]");
        waitUntil(() -> isElementVisible(catBy), 8);
        tapElement(getVisibleElement(catBy));
        try { Thread.sleep(1000); } catch (Exception ignore) {}
        return this;
    }

    public SupportAndLocalizationScreen searchFaqs(String keyword) {
        System.out.println("Searching FAQs for keyword: " + keyword);
        waitUntil(() -> isElementVisible(faqSearchInputBy), 8);
        WebElement searchField = getVisibleElement(faqSearchInputBy);
        tapElement(searchField);
        try { Thread.sleep(400); } catch (Exception ignore) {}
        try {
            searchField.sendKeys(keyword);
        } catch (Exception e) {
            adbInput(keyword);
        }
        hideKeyboard();
        try { Thread.sleep(1000); } catch (Exception ignore) {}
        return this;
    }

    public boolean expandFirstFaqQuestion() {
        System.out.println("Expanding first visible FAQ accordion item...");
        By questionBy = By.xpath("//android.widget.ImageView[@clickable='true' and contains(@content-desc, '?')]");
        if (waitUntil(() -> !driver.findElements(questionBy).isEmpty(), 8)) {
            List<WebElement> questions = driver.findElements(questionBy);
            String qText = questions.get(0).getAttribute("content-desc");
            System.out.println("Tapping FAQ question: " + qText);
            tapElement(questions.get(0));
            try { Thread.sleep(1000); } catch (Exception ignore) {}
            return true;
        }
        return false;
    }

    // =========================================================================
    // 3. COMPLAINTS & CONTACT US MODULE
    // =========================================================================
    public SupportAndLocalizationScreen openFileComplaint() {
        System.out.println("Opening 'File a complaint' from Support tab...");
        if (!isElementVisible(fileComplaintCardBy)) {
            scrollDown();
        }
        waitUntil(() -> isElementVisible(fileComplaintCardBy), 8);
        tapElement(getVisibleElement(fileComplaintCardBy));
        try { Thread.sleep(1500); } catch (Exception ignore) {}
        return this;
    }

    public SupportAndLocalizationScreen openTrackComplaint() {
        System.out.println("Opening 'Track Complaint' from Support tab...");
        if (!isElementVisible(trackComplaintCardBy)) {
            scrollDown();
        }
        waitUntil(() -> isElementVisible(trackComplaintCardBy), 8);
        tapElement(getVisibleElement(trackComplaintCardBy));
        try { Thread.sleep(1500); } catch (Exception ignore) {}
        return this;
    }

    public SupportAndLocalizationScreen openContactUs() {
        System.out.println("Opening 'Contact us' from Support tab...");
        if (!isElementVisible(contactUsCardBy)) {
            scrollDown();
        }
        waitUntil(() -> isElementVisible(contactUsCardBy), 8);
        tapElement(getVisibleElement(contactUsCardBy));
        try { Thread.sleep(2000); } catch (Exception ignore) {}
        return this;
    }

    public boolean isAuthGateLoginDisplayed() {
        return waitUntil(() -> isElementVisible(loginScreenBy), 10);
    }

    public boolean isComplaintsTrackingScreenDisplayed() {
        By trackingBy = By.xpath(
            "//*[contains(@content-desc, 'Complaints tracking') or contains(@text, 'Complaints tracking') " +
            "or contains(@hint, 'Complaint Reference Number') or @content-desc='Track Complaint']"
        );
        return waitUntil(() -> isElementVisible(trackingBy)
                && !isElementVisible(faqsCardBy)
                && !isElementVisible(fileComplaintCardBy), 10);
    }

    public SupportAndLocalizationScreen enterComplaintReferenceAndTrack(String referenceNumber) {
        System.out.println("Entering Complaint Reference Number: " + referenceNumber);
        By refInputBy = By.xpath("//android.widget.EditText[contains(@hint, 'Complaint Reference') or contains(@resource-id, 'complaint')] | //android.widget.EditText");
        if (waitUntil(() -> isElementVisible(refInputBy), 6)) {
            WebElement input = getVisibleElement(refInputBy);
            tapElement(input);
            try { Thread.sleep(300); } catch (Exception ignore) {}
            try {
                input.sendKeys(referenceNumber);
            } catch (Exception e) {
                adbInput(referenceNumber);
            }
            hideKeyboard();
            try { Thread.sleep(500); } catch (Exception ignore) {}
        }
        By trackBtnBy = By.xpath("//android.widget.Button[@content-desc='Track Complaint' or @text='Track Complaint'] | //*[@content-desc='Track Complaint']");
        if (isElementVisible(trackBtnBy)) {
            tapElement(getVisibleElement(trackBtnBy));
        }
        // Wait for backend lookup response modal ("Complaint not found...") and dismiss cleanly
        if (waitUntil(() -> isElementVisible(modalCloseBtnBy), 10)) {
            System.out.println("Complaint lookup modal displayed (e.g. 'Complaint not found'); dismissing via Close button...");
            tapElement(getVisibleElement(modalCloseBtnBy));
            try { Thread.sleep(1000); } catch (Exception ignore) {}
        }
        return this;
    }

    public boolean isSupportHubDisplayed() {
        return waitUntil(() -> isElementVisible(contactUsCardBy) || isElementVisible(faqsCardBy), 8);
    }

    public boolean isContactUsLoadedOrHandled() {
        // Wait for either the Contact Us form or the UAT backend modal ('Something Went Wrong')
        boolean modalAppeared = waitUntil(() -> isElementVisible(modalCloseBtnBy), 5);
        if (modalAppeared) {
            System.out.println("Transient UAT modal displayed on Contact Us; dismissing via Close button...");
            tapElement(getVisibleElement(modalCloseBtnBy));
            try { Thread.sleep(1000); } catch (Exception ignore) {}
            return !isElementVisible(modalCloseBtnBy) && (isElementVisible(contactUsHeaderBy) || isSupportHubDisplayed());
        }
        return waitUntil(() -> !isElementVisible(faqsCardBy) && !isElementVisible(fileComplaintCardBy)
                && (isElementVisible(contactUsHeaderBy) || isElementVisible(By.xpath("//*[@content-desc='Submit' or @text='Submit']"))), 8);
    }

    // =========================================================================
    // 4. LOCALIZATION & ARABIC RTL VERIFICATION
    // =========================================================================
    public SupportAndLocalizationScreen toggleLanguagePreference(boolean targetArabic) {
        System.out.println("Switching Language Preference to " + (targetArabic ? "Arabic (AR)" : "English (EN)") + " via segmented toggle locator...");
        waitUntil(() -> isElementVisible(languageToggleBy), 10);
        WebElement toggle = getVisibleElement(languageToggleBy);
        Rectangle r = toggle.getRect();
        int centerY = r.getY() + (r.getHeight() / 2);

        // In EN (LTR), pill is [EN | AR] so AR is on right (78%).
        // In AR (RTL), pill flips or stays [AR | EN]; tap inactive side dynamically and fallback to opposite side if unchanged.
        int primaryX = targetArabic ? (r.getX() + (int) (r.getWidth() * 0.78)) : (r.getX() + (int) (r.getWidth() * 0.22));
        int alternateX = targetArabic ? (r.getX() + (int) (r.getWidth() * 0.22)) : (r.getX() + (int) (r.getWidth() * 0.78));

        tapCoordinates(primaryX, centerY);
        try { Thread.sleep(2500); } catch (Exception ignore) {}

        // If language didn't flip yet (e.g. pill order mirrored in RTL), tap the alternate half of the same element
        boolean switched = targetArabic ? !isElementVisible(homeTabEnBy) : isElementVisible(homeTabEnBy);
        if (!switched) {
            System.out.println("Tapping alternate segment of language toggle...");
            tapCoordinates(alternateX, centerY);
            try { Thread.sleep(2500); } catch (Exception ignore) {}
        }
        return this;
    }

    /**
     * Verifies Arabic localization by checking:
     * 1) Arabic Unicode characters rendered in visible content-desc attributes
     * 2) RTL spatial layout flip (bottom nav Home/الرئيسية tab sits on the right half of the screen)
     */
    public boolean isArabicRtlActive() {
        return waitUntil(() -> {
            List<WebElement> views = driver.findElements(By.xpath("//*[@content-desc!='']"));
            boolean hasArabicText = false;
            boolean homeOnRightSide = false;
            Dimension size = driver.manage().window().getSize();

            for (WebElement el : views) {
                try {
                    String desc = el.getAttribute("content-desc");
                    if (desc != null && ARABIC_PATTERN.matcher(desc).find()) {
                        hasArabicText = true;
                        if (desc.contains("الرئيسية")) {
                            Rectangle rect = el.getRect();
                            int centerX = rect.getX() + (rect.getWidth() / 2);
                            if (centerX > size.width / 2) {
                                homeOnRightSide = true;
                            }
                        }
                    }
                } catch (Exception ignore) {}
            }
            if (hasArabicText) {
                System.out.println("Arabic text verified! RTL Home tab right-aligned: " + homeOnRightSide);
            }
            return hasArabicText && homeOnRightSide;
        }, 12);
    }

    /**
     * Verifies English LTR layout is restored (Home tab visible on the left half of screen).
     */
    public boolean isEnglishLtrActive() {
        return waitUntil(() -> {
            if (!isElementVisible(homeTabEnBy)) {
                return false;
            }
            Rectangle rect = getVisibleElement(homeTabEnBy).getRect();
            Dimension size = driver.manage().window().getSize();
            int centerX = rect.getX() + (rect.getWidth() / 2);
            boolean homeOnLeft = centerX < size.width / 2;
            System.out.println("English LTR verified! Home tab centerX=" + centerX + " (left half: " + homeOnLeft + ")");
            return homeOnLeft;
        }, 12);
    }
}
