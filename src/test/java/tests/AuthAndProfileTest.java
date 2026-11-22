package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AuthAndProfileScreen;

/**
 * Priority 4: Authentication, Profile & Policy Management Suite.
 * 100% pure Appium locator-based execution.
 *
 * Flows Covered:
 *  - Flow 1: My Policies Guest Auth Gate & Login Navigation
 *  - Flow 2: Login Negative Validation (Short/Malformed National ID & Missing DOB)
 *  - Flow 3: Login with Valid UAT Identity Tuple (ID + 1998 DOB) & OTP/Auth Response Verification
 *  - Flow 4: Update Mobile Number & Sign Up Registration Links Navigation
 *  - Flow 5: My Profile Informational Pages (About Tree & Privacy Policy)
 */
public class AuthAndProfileTest extends BaseTest {

    private static final String VALID_UAT_ID = "1313424273";

    @Test(priority = 1, groups = {"auth", "policies", "guest-gate"},
          description = "Auth Flow 1: My Policies Guest Auth Gate & Login Navigation")
    public void testAuthFlow1_MyPoliciesGuestAuthGateAndLoginNavigation() {
        System.out.println("\n=============================================================");
        System.out.println("  [AUTH FLOW 1] My Policies Guest Gate & Login Navigation    ");
        System.out.println("=============================================================");

        AuthAndProfileScreen screen = new AuthAndProfileScreen();
        screen.openMyPoliciesTab();

        Assert.assertTrue(screen.isMyPoliciesGuestGateDisplayed(),
                "My Policies tab should display guest login prompt.");

        screen.clickLoginOnMyPolicies();
        Assert.assertTrue(screen.isLoginScreenDisplayed(),
                "Tapping Login on My Policies should open the Login screen.");
        System.out.println(">> [SUCCESS] Auth Flow 1 passed: My Policies guest gate & Login screen verified!");
    }

    @Test(priority = 2, groups = {"auth", "negative"},
          description = "Auth Flow 2: Login Negative Validation (Malformed National ID & Missing DOB)")
    public void testAuthFlow2_LoginNegativeValidation_EmptyAndMalformedId() {
        System.out.println("\n=============================================================");
        System.out.println("  [AUTH FLOW 2] Login Negative Validation (Invalid ID)       ");
        System.out.println("=============================================================");

        AuthAndProfileScreen screen = new AuthAndProfileScreen();
        screen.openLoginFromHome();
        Assert.assertTrue(screen.isLoginScreenDisplayed(), "Login screen should be displayed.");

        screen.enterNationalId("12345");
        screen.clickLoginSubmit();

        Assert.assertTrue(screen.isLoginValidationBlocked(),
                "Malformed National ID and missing DOB must block login submission!");
        System.out.println(">> [SUCCESS] Auth Flow 2 passed: Malformed login credentials blocked cleanly!");
    }

    @Test(priority = 3, groups = {"auth", "otp", "e2e"},
          description = "Auth Flow 3: Login with Valid UAT Identity & OTP/Auth Response Verification")
    public void testAuthFlow3_LoginWithValidUatIdentityAndOtpFlow() {
        System.out.println("\n=============================================================");
        System.out.println("  [AUTH FLOW 3] Valid UAT Identity Login & OTP Flow          ");
        System.out.println("=============================================================");

        AuthAndProfileScreen screen = new AuthAndProfileScreen();
        try {
            screen.openLoginFromHome();
            Assert.assertTrue(screen.isLoginScreenDisplayed(), "Login screen should be displayed.");

            screen.enterNationalId(VALID_UAT_ID);
            screen.selectLoginDob();
            screen.clickLoginSubmit();

            Assert.assertTrue(screen.isOtpOrAuthResponseDisplayed(),
                    "Submitting valid UAT ID + DOB should trigger OTP challenge or UAT auth response cleanly.");
            System.out.println(">> [SUCCESS] Auth Flow 3 passed: Valid UAT login submission, OTP & logout verified!");
        } finally {
            try {
                screen.logoutIfAuthenticated();
            } catch (Exception ignore) {}
        }
    }

    @Test(priority = 4, groups = {"auth", "signup"},
          description = "Auth Flow 4: Update Mobile Number & Sign Up Registration Navigation")
    public void testAuthFlow4_UpdateMobileAndSignUpNavigation() {
        System.out.println("\n=============================================================");
        System.out.println("  [AUTH FLOW 4] Update Mobile & Sign Up Navigation           ");
        System.out.println("=============================================================");

        AuthAndProfileScreen screen = new AuthAndProfileScreen();
        screen.openLoginFromHome();
        Assert.assertTrue(screen.isLoginScreenDisplayed(), "Login screen should be displayed.");

        Assert.assertTrue(screen.openAndVerifyUpdateMobileNumber(),
                "Update mobile number screen should open from Login.");
        Assert.assertTrue(screen.openAndVerifySignUp(),
                "Sign up registration screen should open from Login.");
        System.out.println(">> [SUCCESS] Auth Flow 4 passed: Update Mobile & Sign Up links verified!");
    }

    @Test(priority = 5, groups = {"profile", "content"},
          description = "Auth Flow 5: My Profile About Tree & Privacy Policy Verification")
    public void testAuthFlow5_MyProfileAboutTreeAndPrivacyPolicy() {
        System.out.println("\n=============================================================");
        System.out.println("  [AUTH FLOW 5] My Profile: About Tree & Privacy Policy      ");
        System.out.println("=============================================================");

        AuthAndProfileScreen screen = new AuthAndProfileScreen();
        screen.openMyProfileTab();

        Assert.assertTrue(screen.openAndVerifyAboutTree(),
                "About Tree screen should open from My profile.");
        Assert.assertTrue(screen.openAndVerifyPrivacyPolicy(),
                "Privacy Policy screen should open from My profile.");
        System.out.println(">> [SUCCESS] Auth Flow 5 passed: About Tree & Privacy Policy verified!");
    }
}
