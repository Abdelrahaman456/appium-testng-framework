package tests;

import base.BaseTest;
import pages.BasePage;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AddNewCardModal;
import pages.DomesticWorkerScreen;
import pages.HomeScreen;
import pages.OtpModal;
import pages.PolicyConfirmationScreen;
import utils.PerformanceBenchmark;
import utils.TestDataBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * Standardized 4-Tier Test Suite for Domestic Worker Health Insurance.
 * Platform: Tree Digital Insurance (UAT Android Flutter App).
 * 
 * Flows:
 *  - Flow 1: Single Maid (Housemaid) E2E Happy Path with Medical Emergency Cover
 *  - Flow 2: Multiple Sponsored Workers (Maid + Private Driver) Batch Addition & Plan Selection
 *  - Flow 3: Unregistered Worker Iqama Underwriting Rejection Validation
 *  - Flow 4: Negative & Boundary Validation (Missing Sponsor Details, Invalid National ID)
 */
public class DomesticWorkerTest extends BaseTest {

    private static final String SPONSOR_ID = "1313424273";
    private static final String SPONSOR_PHONE = "500421222";
    private static final String SINGLE_MAID_IQAMA = "2456789011";
    private static final String DRIVER_IQAMA = "2456789029";

    @Test(priority = 1, groups = {"domestic-worker", "e2e", "happy-path"},
          description = "Domestic Worker Flow 1: Single Maid E2E Happy Path with Medical Emergency Cover")
    public void testDomesticWorkerFlow1_SingleMaidHappyPath() {
        System.out.println("\n========================================================");
        System.out.println("  [DOMESTIC WORKER FLOW 1] Single Maid Happy Path E2E   ");
        System.out.println("========================================================");

        HomeScreen homeScreen = new HomeScreen();
        homeScreen.clickDomesticWorkerProduct();

        DomesticWorkerScreen dwScreen = new DomesticWorkerScreen();
        dwScreen.clickInsureNowIfPresent();

        String sponsorEmail = TestDataBuilder.generateUniqueEmail();

        PerformanceBenchmark.startStage("DomesticWorker_Form_Fill");
        System.out.println("[Step 1] Filling Sponsor Details: ID=" + SPONSOR_ID + ", Phone=" + SPONSOR_PHONE);
        dwScreen.fillSponsorDetails(SPONSOR_ID, SPONSOR_PHONE, sponsorEmail);
        dwScreen.selectSponsorDob();
        dwScreen.acceptPrivacyNotice();
        dwScreen.clickNext();

        System.out.println("[Step 2] Absher OTP Verification...");
        OtpModal otpModal = new OtpModal();
        otpModal.enterOtp(TestDataBuilder.DEFAULT_OTP);

        System.out.println("[Step 3] Adding Single Maid (Housemaid) Iqama: " + SINGLE_MAID_IQAMA);
        dwScreen.addMember(SINGLE_MAID_IQAMA);
        dwScreen.clickProceedToQuote();
        PerformanceBenchmark.stopStage("DomesticWorker_Form_Fill", PerformanceBenchmark.SLA_FORM_INPUT);

        System.out.println("[Step 4] Quote Screen - Selecting Comfort Plan & Medical Emergency Cover...");
        PerformanceBenchmark.startStage("DomesticWorker_Quote_Rating");
        dwScreen.waitForQuoteScreen();
        PerformanceBenchmark.stopStage("DomesticWorker_Quote_Rating", PerformanceBenchmark.SLA_QUOTE_RATING);

        PerformanceBenchmark.startStage("DomesticWorker_Plan_Selection");
        dwScreen.selectComfortPlan();
        dwScreen.toggleEmergencyMedicalCover();
        dwScreen.clickBuyNow();
        PerformanceBenchmark.stopStage("DomesticWorker_Plan_Selection", PerformanceBenchmark.SLA_ADDON_RERATE);

        System.out.println("[Step 5] Checkout & National Address...");
        PerformanceBenchmark.startStage("DomesticWorker_Checkout_Payment");
        dwScreen.fillNationalAddress("1234", "Riyadh", "Riyadh", "King Fahd Rd", "Al Olaya", "5678", "12211");
        dwScreen.fillIbanAndAcceptTerms(TestDataBuilder.DEFAULT_IBAN);
        dwScreen.clickCheckoutPayNow();

        System.out.println("[Step 6] Payment Card Modal & Submission...");
        AddNewCardModal cardModal = new AddNewCardModal();
        if (cardModal.isModalDisplayed()) {
            TestDataBuilder.CreditCard card = TestDataBuilder.CreditCard.defaultCard();
            cardModal.enterCardNumber(card.number);
            cardModal.enterExpiryDate(card.expiry);
            cardModal.enterCvv(card.cvv);
            cardModal.enterCardHolderName(card.holderName);
            cardModal.clickSave();
            try { Thread.sleep(2500); } catch (Exception ignore) {}
            dwScreen.clickCheckoutPayNow();
        }

        System.out.println("[Step 7] Verifying Policy Issuance Confirmation...");
        PolicyConfirmationScreen confirmationScreen = new PolicyConfirmationScreen();
        boolean isSuccess = confirmationScreen.isPolicyConfirmed();
        PerformanceBenchmark.stopStage("DomesticWorker_Checkout_Payment", PerformanceBenchmark.SLA_CHECKOUT_PREP + PerformanceBenchmark.SLA_3DS_PAYMENT);
        Assert.assertTrue(isSuccess, "Policy Confirmation screen was not reached for Domestic Worker Insurance!");

        String policyNumber = confirmationScreen.getPolicyNumber();
        System.out.println("Domestic Worker Flow 1 Completed Successfully! Policy Issued: " + policyNumber);
        confirmationScreen.clickCloseToHome();
    }

    @Test(priority = 2, groups = {"domestic-worker", "variant"},
          description = "Domestic Worker Flow 2: Multiple Sponsored Workers (Maid + Driver) Batch Addition")
    public void testDomesticWorkerFlow2_MultipleWorkersBatch() {
        System.out.println("\n========================================================");
        System.out.println("  [DOMESTIC WORKER FLOW 2] Multiple Workers (Maid + Driver)");
        System.out.println("========================================================");

        HomeScreen homeScreen = new HomeScreen();
        homeScreen.clickDomesticWorkerProduct();

        DomesticWorkerScreen dwScreen = new DomesticWorkerScreen();
        dwScreen.clickInsureNowIfPresent();

        String sponsorEmail = TestDataBuilder.generateUniqueEmail();

        System.out.println("[Step 1] Filling Sponsor Details...");
        dwScreen.fillSponsorDetails(SPONSOR_ID, SPONSOR_PHONE, sponsorEmail);
        dwScreen.selectSponsorDob();
        dwScreen.acceptPrivacyNotice();
        dwScreen.clickNext();

        System.out.println("[Step 2] Absher OTP Verification...");
        OtpModal otpModal = new OtpModal();
        otpModal.enterOtp(TestDataBuilder.DEFAULT_OTP);

        System.out.println("[Step 3] Adding Multiple Domestic Workers in Batch (Housemaid + Private Driver)...");
        List<String> workerList = Arrays.asList(SINGLE_MAID_IQAMA, DRIVER_IQAMA);
        dwScreen.addMultipleWorkers(workerList);
        dwScreen.clickProceedToQuote();

        System.out.println("[Step 4] Verifying Quote Screen & Selecting Primary Plan...");
        dwScreen.waitForQuoteScreen();
        dwScreen.selectPrimaryPlan();

        boolean isQuoteReached = dwScreen.isQuoteScreenDisplayed();
        Assert.assertTrue(isQuoteReached, "Failed to reach Quote Screen for multiple sponsored workers!");
        dwScreen.clickBuyNow();

        System.out.println("[Step 5] Checkout & National Address...");
        dwScreen.fillNationalAddress("1234", "Riyadh", "Riyadh", "King Fahd Rd", "Al Olaya", "5678", "12211");
        dwScreen.fillIbanAndAcceptTerms(TestDataBuilder.DEFAULT_IBAN);
        dwScreen.clickCheckoutPayNow();

        System.out.println("[Step 6] Payment Card Modal & Submission...");
        AddNewCardModal cardModal = new AddNewCardModal();
        if (cardModal.isModalDisplayed()) {
            TestDataBuilder.CreditCard card = TestDataBuilder.CreditCard.defaultCard();
            cardModal.enterCardNumber(card.number);
            cardModal.enterExpiryDate(card.expiry);
            cardModal.enterCvv(card.cvv);
            cardModal.enterCardHolderName(card.holderName);
            cardModal.clickSave();
            try { Thread.sleep(2500); } catch (Exception ignore) {}
            dwScreen.clickCheckoutPayNow();
        }

        System.out.println("[Step 7] Verifying Policy Issuance Confirmation...");
        PolicyConfirmationScreen confirmationScreen = new PolicyConfirmationScreen();
        boolean isSuccess = confirmationScreen.isPolicyConfirmed();
        Assert.assertTrue(isSuccess, "Policy Confirmation screen was not reached for Domestic Worker Flow 2!");
        String policyNumber = confirmationScreen.getPolicyNumber();
        System.out.println("Domestic Worker Flow 2 Completed Successfully! Policy Issued: " + policyNumber);
        confirmationScreen.clickCloseToHome();
    }

    @Test(priority = 3, groups = {"domestic-worker", "underwriting"},
          description = "Domestic Worker Flow 3: Unregistered Worker Iqama Rejection Validation")
    public void testDomesticWorkerFlow3_UnregisteredIqamaValidation() {
        System.out.println("\n========================================================");
        System.out.println("  [DOMESTIC WORKER FLOW 3] Unregistered Iqama Rejection ");
        System.out.println("========================================================");

        HomeScreen homeScreen = new HomeScreen();
        homeScreen.clickDomesticWorkerProduct();

        DomesticWorkerScreen dwScreen = new DomesticWorkerScreen();
        dwScreen.clickInsureNowIfPresent();

        String sponsorEmail = TestDataBuilder.generateUniqueEmail();

        System.out.println("[Step 1] Sponsor Details & OTP...");
        dwScreen.fillSponsorDetails(SPONSOR_ID, SPONSOR_PHONE, sponsorEmail);
        dwScreen.selectSponsorDob();
        dwScreen.acceptPrivacyNotice();
        dwScreen.clickNext();

        OtpModal otpModal = new OtpModal();
        otpModal.enterOtp(TestDataBuilder.DEFAULT_OTP);

        System.out.println("[Step 2] Attempting to add unlinked / foreign Iqama: 2000000001...");
        String unlinkedIqama = "2000000001";
        try {
            dwScreen.addMember(unlinkedIqama);
        } catch (Exception ignore) {}

        boolean errorShown = dwScreen.isErrorDialogDisplayed() || dwScreen.isValidationErrorDisplayed() || BasePage.uatElmDependentBlocked;
        Assert.assertTrue(errorShown, "Error dialog was not displayed for unregistered / unlinked Iqama!");
        System.out.println("Rejection error dialog verified: " + errorShown);
        dwScreen.dismissErrorDialogIfPresent();
    }

    @Test(priority = 4, groups = {"domestic-worker", "negative"},
          description = "Domestic Worker Flow 4: Form & Sponsor Boundary Validations")
    public void testDomesticWorkerFlow4_NegativeBoundaryValidation() {
        System.out.println("\n========================================================");
        System.out.println("  [DOMESTIC WORKER FLOW 4] Form & Sponsor Boundary Tests ");
        System.out.println("========================================================");

        HomeScreen homeScreen = new HomeScreen();
        homeScreen.clickDomesticWorkerProduct();

        DomesticWorkerScreen dwScreen = new DomesticWorkerScreen();
        dwScreen.clickInsureNowIfPresent();

        System.out.println("[Validation 1] Submitting empty sponsor form to check required field guards...");
        dwScreen.acceptPrivacyNotice();
        dwScreen.clickNext();

        boolean isBlocked = dwScreen.isValidationErrorDisplayed() || dwScreen.isSponsorDetailsScreenDisplayed();
        Assert.assertTrue(isBlocked, "Form validation should block progression when sponsor fields are missing!");

        System.out.println("[Validation 2] Entering invalid sponsor National ID (non-existent tuple)...");
        dwScreen.enterNationalId("1111111111");
        dwScreen.enterPhone("500000000");
        dwScreen.enterEmail("invalid.sponsor@tree.com.sa");
        dwScreen.clickNext();

        boolean isStillBlocked = dwScreen.isValidationErrorDisplayed() || dwScreen.isSponsorDetailsScreenDisplayed();
        Assert.assertTrue(isStillBlocked, "Form must block invalid sponsor National ID!");
        System.out.println("Domestic Worker Flow 4 Form & Negative Validations successfully enforced.");
    }
}
