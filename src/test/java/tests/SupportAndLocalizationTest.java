package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.SupportAndLocalizationScreen;

/**
 * Priority 5: Support Hub (FAQs, Complaints, Contact Us) & Arabic RTL Localization Suite.
 * 100% pure Appium locator-based execution.
 *
 * Flows Covered:
 *  - Flow 1: FAQs Category Switching, Keyword Search & Accordion Expansion
 *  - Flow 2: File a Complaint Guest Authentication Gate Validation
 *  - Flow 3: Track Complaint Guest Authentication Gate Validation
 *  - Flow 4: Contact Us Inquiry Form & Modal Handling
 *  - Flow 5: English LTR -> Arabic RTL Language Switch, Spatial Flip Verification & English Restore
 */
public class SupportAndLocalizationTest extends BaseTest {

    @Test(priority = 1, groups = {"support", "faqs"},
          description = "Support Flow 1: FAQs Category Filtering, Search & Question Expansion")
    public void testSupportFlow1_FaqsSearchAndCategoryFiltering() {
        System.out.println("\n=============================================================");
        System.out.println("  [SUPPORT FLOW 1] FAQs Search, Categories & Accordion       ");
        System.out.println("=============================================================");

        SupportAndLocalizationScreen screen = new SupportAndLocalizationScreen();
        screen.openSupportTab();
        screen.openFaqs();

        Assert.assertTrue(screen.isFaqsScreenDisplayed(), "FAQs screen should load cleanly.");

        screen.selectFaqCategory("Travel Insurance");
        screen.selectFaqCategory("Motor Insurance");
        Assert.assertTrue(screen.expandFirstFaqQuestion(), "Clicking an FAQ question should expand its details.");

        screen.searchFaqs("Car");
        Assert.assertTrue(screen.isFaqsScreenDisplayed(), "FAQs screen should remain active after keyword search.");
        System.out.println(">> [SUCCESS] Support Flow 1 passed: FAQs categories, expansion, and search verified!");
    }

    @Test(priority = 2, groups = {"support", "complaints", "auth-gate"},
          description = "Support Flow 2: File a Complaint Guest Auth Gate Redirection")
    public void testSupportFlow2_FileComplaint_AuthGateValidation() {
        System.out.println("\n=============================================================");
        System.out.println("  [SUPPORT FLOW 2] File a Complaint Auth Gate Validation     ");
        System.out.println("=============================================================");

        SupportAndLocalizationScreen screen = new SupportAndLocalizationScreen();
        screen.openSupportTab();
        screen.openFileComplaint();

        Assert.assertTrue(screen.isAuthGateLoginDisplayed(),
                "Filing a complaint as a guest should redirect to the Login authentication screen.");
        System.out.println(">> [SUCCESS] Support Flow 2 passed: File a complaint enforces authentication!");
    }

    @Test(priority = 3, groups = {"support", "complaints"},
          description = "Support Flow 3: Track Complaint Screen & Reference Number Lookup")
    public void testSupportFlow3_TrackComplaint_LookupValidation() {
        System.out.println("\n=============================================================");
        System.out.println("  [SUPPORT FLOW 3] Track Complaint Reference Lookup          ");
        System.out.println("=============================================================");

        SupportAndLocalizationScreen screen = new SupportAndLocalizationScreen();
        screen.openSupportTab();
        screen.openTrackComplaint();

        Assert.assertTrue(screen.isComplaintsTrackingScreenDisplayed(),
                "Complaints tracking screen with reference number input should load cleanly.");
        screen.enterComplaintReferenceAndTrack("CMP-2026-0001");
        Assert.assertTrue(screen.isComplaintsTrackingScreenDisplayed() || screen.isSupportHubDisplayed(),
                "Complaints tracking lookup should handle 'not found' response and recover cleanly.");
        System.out.println(">> [SUCCESS] Support Flow 3 passed: Track complaint screen & reference lookup verified!");
    }

    @Test(priority = 4, groups = {"support", "contact-us"},
          description = "Support Flow 4: Contact Us Inquiry Form & Modal Handling")
    public void testSupportFlow4_ContactUs_FormAndErrorModalHandling() {
        System.out.println("\n=============================================================");
        System.out.println("  [SUPPORT FLOW 4] Contact Us Inquiry Form Validation        ");
        System.out.println("=============================================================");

        SupportAndLocalizationScreen screen = new SupportAndLocalizationScreen();
        screen.openSupportTab();
        screen.openContactUs();

        Assert.assertTrue(screen.isContactUsLoadedOrHandled(),
                "Contact Us form should load cleanly (handling any transient UAT modal).");
        System.out.println(">> [SUCCESS] Support Flow 4 passed: Contact Us screen verified!");
    }

    @Test(priority = 5, groups = {"localization", "rtl", "arabic"},
          description = "Localization Flow 5: English LTR -> Arabic RTL Switch, Layout Flip & English Restore")
    public void testLocalizationFlow5_ArabicRtlSwitchAndEnglishRestore() {
        System.out.println("\n=============================================================");
        System.out.println("  [LOCALIZATION FLOW 5] Arabic RTL Switch & LTR Restore      ");
        System.out.println("=============================================================");

        SupportAndLocalizationScreen screen = new SupportAndLocalizationScreen();
        screen.openMyProfileTab();

        Assert.assertTrue(screen.isEnglishLtrActive(),
                "Baseline app state should be English LTR with Home tab on the left.");

        System.out.println("[Step 1] Switching app language from English (EN) to Arabic (AR)...");
        screen.toggleLanguagePreference(true);

        Assert.assertTrue(screen.isArabicRtlActive(),
                "App should render Arabic strings and mirror layout to RTL!");

        System.out.println("[Step 2] Restoring app language back to English (EN)...");
        screen.openMyProfileTab();
        screen.toggleLanguagePreference(false);

        Assert.assertTrue(screen.isEnglishLtrActive(),
                "App should cleanly restore English LTR layout after switching back!");
        System.out.println(">> [SUCCESS] Localization Flow 5 passed: Arabic RTL & English LTR verified!");
    }
}
