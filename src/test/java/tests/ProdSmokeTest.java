package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AboutYouScreen;
import pages.HomeInsuranceScreen;
import pages.HomeScreen;
import pages.IncomeInsuranceScreen;
import pages.MotorCoverageSelectionScreen;
import pages.OtpModal;
import pages.QuoteScreen;
import pages.TravelAboutYouScreen;
import utils.PerformanceBenchmark;
import utils.TestConfig;
import utils.TestDataBuilder;

/**
 * Safe Production Smoke & Quote Verification Suite (sa.com.tree.digital.insurance).
 *
 * Verifies live Production journeys end-to-end up to Quote / Plan Selection
 * using the approved Production QA identity tuple from config/prod.properties,
 * and strictly stops BEFORE Pay now / policy issuance.
 */
public class ProdSmokeTest extends BaseTest {

    @Test(priority = 1, groups = {"prod", "smoke", "motor"}, description = "PROD Smoke: Motor Insurance Quote Generation (Stops Before Payment)")
    public void testProdMotorQuoteSmoke() {
        String nationalId = TestConfig.nationalId();
        String sequenceNumber = TestConfig.sequenceNumber();
        System.out.println("=== [PROD SMOKE] Motor Quote (ID=" + nationalId + ", Seq=" + sequenceNumber + ", Phone=" + TestConfig.phone() + ") ===");

        HomeScreen homeScreen = new HomeScreen();
        homeScreen.clickMotorProduct();

        MotorCoverageSelectionScreen coverageScreen = new MotorCoverageSelectionScreen();
        Assert.assertTrue(coverageScreen.waitForScreenLoaded(12), "PROD Motor Coverage Selection screen did not load!");
        coverageScreen.clickInsureNow();

        PerformanceBenchmark.startStage("Prod_Motor_Quote");
        AboutYouScreen aboutYouScreen = new AboutYouScreen();
        aboutYouScreen.selectNewInsuranceTab();
        aboutYouScreen.selectSequenceNumberRadio();
        aboutYouScreen.enterSequenceNumber(sequenceNumber);
        aboutYouScreen.enterNationalId(nationalId);
        aboutYouScreen.selectDob();
        aboutYouScreen.enterPhoneNumber(TestConfig.phone());
        aboutYouScreen.selectPolicyStartDate();
        aboutYouScreen.clickPrivacyCheckbox();
        aboutYouScreen.clickNext();

        OtpModal otpModal = new OtpModal();
        otpModal.enterOtp(TestConfig.otp());

        QuoteScreen quoteScreen = new QuoteScreen();
        Assert.assertTrue(quoteScreen.isPageLoaded(), "PROD Motor Quote Screen did not load!");
        PerformanceBenchmark.stopStage("Prod_Motor_Quote", PerformanceBenchmark.SLA_QUOTE_RATING);
        System.out.println("[PASS] PROD Motor Quote loaded cleanly (stopped safely before Checkout/Payment).");
    }

    @Test(priority = 2, groups = {"prod", "smoke", "travel"}, description = "PROD Smoke: Travel Insurance Quote Generation (Stops Before Payment)")
    public void testProdTravelQuoteSmoke() {
        String nationalId = TestConfig.nationalId();
        System.out.println("=== [PROD SMOKE] Travel Quote (ID=" + nationalId + ", Phone=" + TestConfig.phone() + ") ===");

        HomeScreen homeScreen = new HomeScreen();
        homeScreen.clickTravelProduct();

        TravelAboutYouScreen travelScreen = new TravelAboutYouScreen();
        travelScreen.clickInsureNowIfPresent();
        travelScreen.handleInitialTripInfoIfPresent(nationalId, TestConfig.phone());
        travelScreen.dismissOtpIfPresent();

        PerformanceBenchmark.startStage("Prod_Travel_Quote");
        travelScreen.selectSingleTrip();
        travelScreen.selectDestination("Worldwide");
        travelScreen.selectDepartureDate();
        travelScreen.selectReturnDate();
        travelScreen.selectTravelerType("Individual");
        travelScreen.enterEmailIfPresent(TestDataBuilder.DEFAULT_EMAIL);
        travelScreen.clickPrivacyCheckbox();
        travelScreen.clickNext();
        travelScreen.answerAllDeclarationQuestions(false);
        travelScreen.clickDeclarationProceed();
        travelScreen.waitForPlanSelectionScreen();

        PerformanceBenchmark.stopStage("Prod_Travel_Quote", PerformanceBenchmark.SLA_QUOTE_RATING);
        System.out.println("[PASS] PROD Travel Quote loaded cleanly (stopped safely before Checkout/Payment).");
    }

    @Test(priority = 3, groups = {"prod", "smoke", "home"}, description = "PROD Smoke: Home Insurance Quote Generation (Stops Before Payment)")
    public void testProdHomeQuoteSmoke() {
        String nationalId = TestConfig.nationalId();
        System.out.println("=== [PROD SMOKE] Home Quote (ID=" + nationalId + ", Phone=" + TestConfig.phone() + ") ===");

        HomeScreen homeScreen = new HomeScreen();
        homeScreen.clickHomeInsuranceProduct();

        HomeInsuranceScreen homeInsScreen = new HomeInsuranceScreen();
        homeInsScreen.clickInsureNowIfPresent();

        PerformanceBenchmark.startStage("Prod_Home_Quote");
        homeInsScreen.fillPolicyholderDetails(nationalId, TestConfig.phone(), TestDataBuilder.generateUniqueEmail());
        homeInsScreen.acceptPrivacyNotice();
        homeInsScreen.clickNext();

        OtpModal otpModal = new OtpModal();
        otpModal.enterOtp(TestConfig.otp());

        homeInsScreen.waitForPlanSelectionScreen();
        PerformanceBenchmark.stopStage("Prod_Home_Quote", PerformanceBenchmark.SLA_QUOTE_RATING);
        System.out.println("[PASS] PROD Home Quote loaded cleanly (stopped safely before Checkout/Payment).");
    }

    @Test(priority = 4, groups = {"prod", "smoke", "income"}, description = "PROD Smoke: Income Protection Quote Generation (Stops Before Payment)")
    public void testProdIncomeQuoteSmoke() {
        String nationalId = TestConfig.nationalId();
        System.out.println("=== [PROD SMOKE] Income Protection Quote (ID=" + nationalId + ", Phone=" + TestConfig.phone() + ") ===");

        HomeScreen homeScreen = new HomeScreen();
        homeScreen.clickIncomeProduct();

        IncomeInsuranceScreen incomeScreen = new IncomeInsuranceScreen();
        incomeScreen.clickInsureNowIfPresent();

        PerformanceBenchmark.startStage("Prod_Income_Quote");
        incomeScreen.fillApplicantDetails(nationalId, TestConfig.phone(), TestDataBuilder.generateUniqueEmail());
        incomeScreen.selectDob();
        incomeScreen.acceptPrivacyNotice();
        incomeScreen.clickNext();

        OtpModal otpModal = new OtpModal();
        otpModal.enterOtp(TestConfig.otp());

        incomeScreen.fillEmploymentDetails("Employed", "15000", "Software Engineer");
        incomeScreen.clickEmploymentProceed();
        incomeScreen.answerAllDeclarationQuestions(false);
        incomeScreen.clickDeclarationProceed();
        incomeScreen.waitForPlanSelectionScreen();

        PerformanceBenchmark.stopStage("Prod_Income_Quote", PerformanceBenchmark.SLA_QUOTE_RATING);
        System.out.println("[PASS] PROD Income Protection Quote loaded cleanly (stopped safely before Checkout/Payment).");
    }
}
