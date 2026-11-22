package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.SmeScreen;

/**
 * Priority 6: SMEs Tab Products & External SME Health Portal Suite.
 * 100% pure Appium locator-based execution.
 *
 * Flows Covered:
 *  - Flow 1: SMEs Tab & All Products Catalog Navigation Verification
 *  - Flow 2: SME Health Insurance External Web Portal Handoff & Clean App Return
 *  - Flow 3: SMEs Quick Actions Hub Navigation (FAQs)
 */
public class SmeTest extends BaseTest {

    @Test(priority = 1, groups = {"sme", "catalog"},
          description = "SME Flow 1: SMEs Tab Catalog & All Products Switch Verification")
    public void testSmeFlow1_SmeTabNavigationAndCatalogVerification() {
        System.out.println("\n=============================================================");
        System.out.println("  [SME FLOW 1] SMEs Tab & All Products Catalog Verification  ");
        System.out.println("=============================================================");

        SmeScreen smeScreen = new SmeScreen();
        smeScreen.selectSmesTab();

        Assert.assertTrue(smeScreen.isSmeHealthProductDisplayed(),
                "SME Health Insurance product card should be visible under the SMEs tab.");

        smeScreen.selectAllProductsTab();
        Assert.assertTrue(smeScreen.isAllProductsCatalogDisplayed(),
                "All products tab should display unified Individual + SME product catalog.");

        smeScreen.selectIndividualTab();
        System.out.println(">> [SUCCESS] SME Flow 1 passed: SMEs tab and All products catalog verified!");
    }

    @Test(priority = 2, groups = {"sme", "sme-health", "e2e"},
          description = "SME Flow 2: SME Health Insurance External Web Portal Handoff & Clean App Return")
    public void testSmeFlow2_SmeHealthInsurance_ExternalPortalHandoffAndReturn() {
        System.out.println("\n=============================================================");
        System.out.println("  [SME FLOW 2] SME Health External Portal Handoff & Return   ");
        System.out.println("=============================================================");

        SmeScreen smeScreen = new SmeScreen();
        smeScreen.selectSmesTab();
        Assert.assertTrue(smeScreen.isSmeHealthProductDisplayed(),
                "SME Health Insurance card should be displayed.");

        boolean returned = false;
        try {
            smeScreen.openSmeHealthInsurance();
            Assert.assertTrue(smeScreen.isExternalSmePortalOpened(),
                    "Tapping SME Health Insurance should launch the external Chrome SME web portal.");

            returned = smeScreen.returnToTreeApp();
            Assert.assertTrue(returned,
                    "Returning from external SME portal should cleanly restore Tree UAT app Home.");
            System.out.println(">> [SUCCESS] SME Flow 2 passed: SME Health external portal handoff and clean return verified!");
        } finally {
            if (!returned) {
                try {
                    smeScreen.returnToTreeApp();
                } catch (Exception ignore) {}
            }
        }
    }

    @Test(priority = 3, groups = {"sme", "quick-actions"},
          description = "SME Flow 3: Quick Actions Navigation from SMEs View")
    public void testSmeFlow3_SmeQuickActionsNavigation() {
        System.out.println("\n=============================================================");
        System.out.println("  [SME FLOW 3] SMEs Quick Actions Navigation (FAQs)          ");
        System.out.println("=============================================================");

        SmeScreen smeScreen = new SmeScreen();
        smeScreen.selectSmesTab();
        smeScreen.openQuickActionFaqs();

        Assert.assertTrue(smeScreen.isFaqsOpened(),
                "Tapping FAQs from SMEs Quick Actions should open the FAQs screen.");
        System.out.println(">> [SUCCESS] SME Flow 3 passed: SMEs Quick Actions navigation verified!");
    }
}
