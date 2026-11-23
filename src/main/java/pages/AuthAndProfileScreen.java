package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Page Object for Authentication (Login, Sign Up, Update Mobile Number),
 * My Policies Guest/Authenticated Access, and My Profile (About Tree, Privacy Policy).
 * 100% pure Appium locator-based implementation with dynamic waits.
 */
public class AuthAndProfileScreen extends BasePage {

    // Bottom Nav & Home Entry Points
    private final By myPoliciesTabBy = By.xpath("//*[@content-desc='My Policies' or @text='My Policies']");
    private final By myProfileTabBy = By.xpath("//*[@content-desc='My profile' or @text='My profile']");
    private final By homeTopLoginBtnBy = By.xpath("//android.widget.Button[@content-desc='Login' or @text='Login'] | //*[@content-desc='Login' or @text='Login']");

    // My Policies Guest Gate
    private final By myPoliciesPromptBy = By.xpath("//*[contains(@content-desc, 'Login to access your policies') or contains(@text, 'Login to access your policies') or contains(@content-desc, 'My policies')]");
    private final By myPoliciesLoginCtaBy = By.xpath("(//android.widget.Button[@content-desc='Login' or @text='Login'])[last()] | (//*[@content-desc='Login'])[last()]");

    // Login Screen Elements
    private final By nationalIdInputBy = By.xpath("//*[@resource-id='textfield_national_id' or contains(@hint, 'National ID')]");
    private final By dobInputBy = By.xpath("//*[@resource-id='textfield_month_year_of_birth_txt' or contains(@hint, 'Month/Year of birth')]");
    private final By loginSubmitBtnBy = By.xpath("//*[@resource-id='btn_guest_login_or_register_part1']//android.widget.Button | //*[@resource-id='btn_guest_login_or_register_part1'] | //android.widget.Button[@content-desc='Login' or @text='Login']");
    private final By updateMobileLinkBy = By.xpath("//*[contains(@content-desc, 'Update mobile number') or contains(@text, 'Update mobile number')]");
    private final By signUpLinkBy = By.xpath("//*[contains(@content-desc, 'Sign up') or contains(@text, 'Sign up')]");
    private final By validationErrorBy = By.xpath("//*[contains(@content-desc, 'required') or contains(@text, 'required') or contains(@content-desc, 'valid') or contains(@text, 'valid') or contains(@content-desc, 'Invalid') or contains(@text, 'Invalid')]");

    // Modal / OTP / Auth Response
    private final By otpModalBy = By.xpath("//*[contains(@content-desc, 'OTP') or contains(@text, 'OTP') or contains(@content-desc, '4-digit')]");
    private final By modalCloseBtnBy = By.xpath("//*[@resource-id='btn_close_button']//android.widget.Button | //android.widget.Button[@content-desc='Close' or @text='Close'] | //*[@resource-id='btn_close']");

    // My Profile Items
    private final By aboutTreeCardBy = By.xpath("//*[contains(@content-desc, 'About Tree') or contains(@text, 'About Tree')]");
    private final By privacyPolicyCardBy = By.xpath("//*[contains(@content-desc, 'Privacy Policy') or contains(@text, 'Privacy Policy')]");

    // Authenticated State & Logout Locators
    private final By authenticatedGreetingBy = By.xpath("//*[contains(@content-desc, 'Hi,') or @content-desc='Saeed Abdulrahman Almalki' or contains(@content-desc, 'Personal information')]");
    private final By profileLogoutTriggerBy = By.xpath("//*[@resource-id='btn_logout_bottom_sheet']//android.widget.ImageView[@content-desc='Logout'] | //android.widget.ImageView[@content-desc='Logout']");
    private final By logoutConfirmPromptBy = By.xpath("//*[contains(@content-desc, 'Are you sure you want to logout')]");
    private final By logoutConfirmBtnBy = By.xpath("//*[@resource-id='btn_logout_bottom_sheet']//android.widget.Button[@content-desc='Logout'] | //android.widget.Button[@content-desc='Logout']");
    private final By chromeCloseBtnBy = By.xpath("//*[@resource-id='com.android.chrome:id/close_button' or @content-desc='Close tab']");

    // =========================================================================
    // 1. NAVIGATION
    // =========================================================================
    public AuthAndProfileScreen openMyPoliciesTab() {
        System.out.println("Opening 'My Policies' tab via locator...");
        waitUntil(() -> isElementVisible(myPoliciesTabBy), 10);
        tapElement(getVisibleElement(myPoliciesTabBy));
        waitUntil(() -> isElementVisible(myPoliciesPromptBy), 6);
        return this;
    }

    public AuthAndProfileScreen openMyProfileTab() {
        System.out.println("Opening 'My profile' tab via locator...");
        waitUntil(() -> isElementVisible(myProfileTabBy), 20);
        tapElement(getVisibleElement(myProfileTabBy));
        waitUntil(() -> isElementVisible(aboutTreeCardBy), 6);
        return this;
    }

    public AuthAndProfileScreen openLoginFromHome() {
        if (isElementVisible(nationalIdInputBy)) {
            System.out.println("Already on Login screen; skipping navigation.");
            return this;
        }
        if (isElementVisible(authenticatedGreetingBy)) {
            System.out.println("Authenticated session detected on Home; logging out to restore guest Login entry...");
            logoutIfAuthenticated();
        }
        if (!isElementVisible(nationalIdInputBy)) {
            System.out.println("Opening Login screen from Home header button...");
            waitUntil(() -> isElementVisible(homeTopLoginBtnBy), 20);
            tapElement(getVisibleElement(homeTopLoginBtnBy));
            waitUntil(() -> isElementVisible(nationalIdInputBy), 8);
        }
        return this;
    }

    public AuthAndProfileScreen clickLoginOnMyPolicies() {
        System.out.println("Tapping 'Login' CTA on My Policies guest screen...");
        waitUntil(() -> isElementVisible(myPoliciesLoginCtaBy), 8);
        try { Thread.sleep(600); } catch (Exception ignore) {}
        for (int attempt = 1; attempt <= 2; attempt++) {
            if (isElementVisible(myPoliciesLoginCtaBy)) {
                tapElement(getVisibleElement(myPoliciesLoginCtaBy));
            }
            if (waitUntil(() -> isElementVisible(nationalIdInputBy), 6)) {
                return this;
            }
            System.out.println("My Policies Login CTA tap attempt " + attempt + " did not open Login yet; retrying...");
        }
        return this;
    }

    public boolean isMyPoliciesGuestGateDisplayed() {
        return waitUntil(() -> isElementVisible(myPoliciesPromptBy), 8);
    }

    public boolean isLoginScreenDisplayed() {
        return waitUntil(() -> isElementVisible(nationalIdInputBy) && isElementVisible(dobInputBy), 10);
    }

    // =========================================================================
    // 2. LOGIN FORM & AUTH FLOW
    // =========================================================================
    public AuthAndProfileScreen enterNationalId(String nationalId) {
        System.out.println("Entering National ID on Login screen: " + nationalId);
        waitUntil(() -> isElementVisible(nationalIdInputBy), 8);
        WebElement field = getVisibleElement(nationalIdInputBy);
        tapElement(field);
        try { Thread.sleep(250); } catch (Exception ignore) {}
        try {
            field.sendKeys(nationalId);
        } catch (Exception e) {
            adbInput(nationalId);
        }
        hideKeyboard();
        return this;
    }

    public AuthAndProfileScreen selectLoginDob() {
        System.out.println("Selecting Month/Year of birth (1998) on Login screen...");
        waitUntil(() -> isElementVisible(dobInputBy), 8);
        scrollDobWheelAndConfirm(getVisibleElement(dobInputBy));
        return this;
    }

    public AuthAndProfileScreen clickLoginSubmit() {
        System.out.println("Tapping Login submit button...");
        hideKeyboard();
        waitUntil(() -> isElementVisible(loginSubmitBtnBy), 8);
        tapElement(getVisibleElement(loginSubmitBtnBy));
        try { Thread.sleep(1000); } catch (Exception ignore) {}
        return this;
    }

    public boolean isLoginValidationBlocked() {
        return waitUntil(() -> isElementVisible(validationErrorBy), 6);
    }

    public boolean isOtpOrAuthResponseDisplayed() {
        boolean triggered = waitUntil(() -> isElementVisible(otpModalBy) || isElementVisible(modalCloseBtnBy), 10);
        if (isElementVisible(otpModalBy)) {
            System.out.println("OTP verification modal detected after Login! Entering UAT OTP...");
            OtpModal otpModal = new OtpModal();
            otpModal.enterOtp(utils.TestDataBuilder.DEFAULT_OTP);
            waitUntil(() -> isElementVisible(authenticatedGreetingBy) || isElementVisible(myProfileTabBy), 8);
            return true;
        }
        if (isElementVisible(modalCloseBtnBy)) {
            System.out.println("UAT auth response modal detected; dismissing cleanly...");
            tapElement(getVisibleElement(modalCloseBtnBy));
            return true;
        }
        return triggered;
    }

    public AuthAndProfileScreen logoutIfAuthenticated() {
        if (isElementVisible(nationalIdInputBy) || isElementVisible(homeTopLoginBtnBy)) {
            return this;
        }
        System.out.println("Logging out authenticated user to restore clean guest state...");
        openMyProfileTab();
        int logoutMaxY = (int) (getSafeWindowSize().height * 0.85);
        for (int attempt = 0; attempt < 2; attempt++) {
            if (!isElementVisible(profileLogoutTriggerBy)
                    || getVisibleElement(profileLogoutTriggerBy).getRect().getY() > logoutMaxY) {
                scrollDown();
            } else {
                break;
            }
        }
        if (isElementVisible(profileLogoutTriggerBy)) {
            tapElement(getVisibleElement(profileLogoutTriggerBy));
            if (waitUntil(() -> isElementVisible(logoutConfirmPromptBy) || isElementVisible(logoutConfirmBtnBy), 6)) {
                tapElement(getVisibleElement(logoutConfirmBtnBy));
                waitUntil(() -> isElementVisible(nationalIdInputBy) || isElementVisible(homeTopLoginBtnBy), 10);
            }
        }
        return this;
    }

    private void navigateBackToParent(By parentElementBy) {
        if (isElementVisible(chromeCloseBtnBy)) {
            tapElement(getVisibleElement(chromeCloseBtnBy));
            if (waitUntil(() -> isElementVisible(parentElementBy), 4)) {
                return;
            }
        }
        driver.navigate().back();
        if (!waitUntil(() -> isElementVisible(parentElementBy), 3)) {
            By backImgBy = By.xpath("(//android.widget.ImageView[@clickable='true'])[1]");
            if (isElementVisible(backImgBy)) {
                tapElement(getVisibleElement(backImgBy));
            }
            waitUntil(() -> isElementVisible(parentElementBy), 6);
        }
    }

    // =========================================================================
    // 3. UPDATE MOBILE NUMBER & SIGN UP SUB-FLOWS
    // =========================================================================
    public boolean openAndVerifyUpdateMobileNumber() {
        System.out.println("Tapping 'Update mobile number' link on Login screen...");
        waitUntil(() -> isElementVisible(updateMobileLinkBy), 8);
        tapElement(getVisibleElement(updateMobileLinkBy));
        By updateHeaderBy = By.xpath("//*[contains(@content-desc, 'Update mobile') or contains(@text, 'Update mobile') or contains(@content-desc, 'mobile number')]");
        boolean opened = waitUntil(() -> isElementVisible(updateHeaderBy) && !isElementVisible(signUpLinkBy), 8);
        navigateBackToParent(nationalIdInputBy);
        return opened;
    }

    public boolean openAndVerifySignUp() {
        System.out.println("Tapping 'Sign up' link on Login screen...");
        waitUntil(() -> isElementVisible(signUpLinkBy), 8);
        WebElement link = getVisibleElement(signUpLinkBy);
        // "Don't have an account? Sign up" — tap the right side where "Sign up" text sits
        org.openqa.selenium.Rectangle r = link.getRect();
        tapCoordinates(r.getX() + (int) (r.getWidth() * 0.85), r.getY() + (r.getHeight() / 2));
        By signUpUniqueBy = By.xpath("//*[@resource-id='textfield_phone_number' or contains(@hint, 'Mobile number') or contains(@content-desc, 'Already have an account') or contains(@text, 'Already have an account') or contains(@content-desc, 'referral code') or contains(@text, 'referral code')]");
        return waitUntil(() -> isElementVisible(signUpUniqueBy), 8);
    }

    // =========================================================================
    // 4. MY PROFILE INFORMATIONAL SCREENS (ABOUT TREE & PRIVACY POLICY)
    // =========================================================================
    public boolean openAndVerifyAboutTree() {
        System.out.println("Opening 'About Tree' from My profile...");
        waitUntil(() -> isElementVisible(aboutTreeCardBy), 8);
        tapElement(getVisibleElement(aboutTreeCardBy));
        By aboutContentBy = By.xpath("//*[contains(@content-desc, 'About') or contains(@text, 'About') or contains(@content-desc, 'Tree') or contains(@class, 'WebView')]");
        boolean loaded = waitUntil(() -> isElementVisible(aboutContentBy) && !isElementVisible(privacyPolicyCardBy), 8);
        navigateBackToParent(privacyPolicyCardBy);
        return loaded;
    }

    public boolean openAndVerifyPrivacyPolicy() {
        System.out.println("Opening 'Privacy Policy' from My profile...");
        waitUntil(() -> isElementVisible(privacyPolicyCardBy) && isElementVisible(aboutTreeCardBy), 8);
        try { Thread.sleep(500); } catch (Exception ignore) {}

        By privacyContentBy = By.xpath(
            "//*[contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'privacy') "
            + "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'privacy') "
            + "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'policy') "
            + "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'policy') "
            + "or contains(@class, 'WebView') "
            + "or @resource-id='com.android.chrome:id/close_button' "
            + "or @resource-id='com.android.chrome:id/url_bar' "
            + "or @package='com.android.chrome']");

        for (int attempt = 1; attempt <= 2; attempt++) {
            if (isElementVisible(privacyPolicyCardBy)) {
                tapElement(getVisibleElement(privacyPolicyCardBy));
            }
            if (waitUntil(() -> !isElementVisible(aboutTreeCardBy), 3)) {
                break;
            }
            System.out.println("Privacy Policy tap attempt " + attempt + " left My profile visible; retrying...");
        }

        boolean loaded = waitUntil(() -> !isElementVisible(aboutTreeCardBy)
                && !findWithUnimportantViews(privacyContentBy).isEmpty(), 15);
        if (!isElementVisible(aboutTreeCardBy)) {
            navigateBackToParent(aboutTreeCardBy);
        }
        return loaded;
    }
}
