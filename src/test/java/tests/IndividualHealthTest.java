package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AddNewCardModal;
import pages.HomeScreen;
import pages.IndividualHealthScreen;
import pages.OtpModal;
import pages.PolicyConfirmationScreen;
import utils.PerformanceBenchmark;
import utils.TestDataBuilder;

/**
 * Standardized 4-Tier Test Suite for Individual & SME Health Insurance.
 * Platform: Tree Digital Insurance (UAT Android Flutter App).
 * 
 * Flows:
 *  - Flow 1: Primary Happy Path E2E (Applicant + Dependent, Clean Health Declaration, Gold Plan with Deductibles, Checkout, Confirmation)
 *  - Flow 2: SME / Individual Tier Comparison (Bronze vs Silver vs Gold vs Platinum with Co-pay Deductibles)
 *  - Flow 3: Pre-existing Conditions Declaration ('Yes' path triggering Underwriting Referral & Review)
 *  - Flow 4: Negative & Boundary Validation (Invalid National ID, Missing Dependent, Validation Errors)
 */
public class IndividualHealthTest extends BaseTest {

    // Identity Tuple: ID 1313424273 + Phone 500421222 + Birth Year 1998
    private static final String VALID_ID = "1313424273";
    private static final String VALID_PHONE = "500421222";
    private static final String DEPENDENT_IQAMA = "2456789011";

    @Test(priority = 1, groups = {"health", "individual-health", "e2e", "happy-path"},
          description = "Individual Health Flow 1: Primary Happy Path E2E with Dependent, Gold Plan & Deductible")
    public void testIndividualHealthFlow1_HappyPath() {
        System.out.println("\n========================================================");
        System.out.println("  [INDIVIDUAL HEALTH FLOW 1] Primary Happy Path E2E     ");
        System.out.println("========================================================");

        HomeScreen homeScreen = new HomeScreen();
        homeScreen.clickHealthInsuranceProduct();

        IndividualHealthScreen healthScreen = new IndividualHealthScreen();
        healthScreen.clickInsureNowIfPresent();

        String email = TestDataBuilder.generateUniqueEmail();

        PerformanceBenchmark.startStage("Health_Form_Fill");
        System.out.println("[Step 1] Filling Applicant Details (Absher Identity Tuple: ID=" + VALID_ID + ", Phone=" + VALID_PHONE + ", Year=1998)...");
        healthScreen.fillApplicantDetails(VALID_ID, VALID_PHONE, email);
        healthScreen.selectDob();
        healthScreen.selectGenderIfPrompted("Male");
        healthScreen.selectMaritalStatusIfPrompted("Married");
        healthScreen.acceptPrivacyNotice();
        healthScreen.clickNext();

        System.out.println("[Step 2] Absher OTP Verification...");
        OtpModal otpModal = new OtpModal();
        otpModal.enterOtp(TestDataBuilder.DEFAULT_OTP);

        System.out.println("[Step 3] Adding Dependent (Spouse: " + DEPENDENT_IQAMA + ")...");
        healthScreen.addDependent(DEPENDENT_IQAMA, "Spouse");
        healthScreen.clickProceedToQuote();

        System.out.println("[Step 4] Health Declaration Questionnaire (Clean 'No' answers for instant quote)...");
        healthScreen.answerAllDeclarationQuestions(false);
        healthScreen.clickDeclarationProceed();
        PerformanceBenchmark.stopStage("Health_Form_Fill", PerformanceBenchmark.SLA_FORM_INPUT);

        System.out.println("[Step 5] Selecting Gold Plan Tier & Deductible Co-pay...");
        PerformanceBenchmark.startStage("Health_Quote_Rating");
        healthScreen.waitForPlanSelectionScreen();
        PerformanceBenchmark.stopStage("Health_Quote_Rating", PerformanceBenchmark.SLA_QUOTE_RATING);

        PerformanceBenchmark.startStage("Health_Plan_Selection");
        healthScreen.selectGoldPlan();
        healthScreen.selectDeductible("10%");
        healthScreen.clickBuyNow();
        PerformanceBenchmark.stopStage("Health_Plan_Selection", PerformanceBenchmark.SLA_ADDON_RERATE);

        System.out.println("[Step 6] Checkout & National Address...");
        PerformanceBenchmark.startStage("Health_Checkout_Payment");
        healthScreen.fillNationalAddress("1234", "Riyadh", "Riyadh", "King Fahd Rd", "Al Olaya", "5678", "12211");
        healthScreen.fillIbanAndAcceptTerms(TestDataBuilder.DEFAULT_IBAN);
        healthScreen.clickCheckoutPayNow();

        System.out.println("[Step 7] Payment Card Processing...");
        AddNewCardModal cardModal = new AddNewCardModal();
        if (cardModal.isModalDisplayed()) {
            TestDataBuilder.CreditCard card = TestDataBuilder.CreditCard.defaultCard();
            cardModal.enterCardNumber(card.number);
            cardModal.enterExpiryDate(card.expiry);
            cardModal.enterCvv(card.cvv);
            cardModal.enterCardHolderName(card.holderName);
            cardModal.clickSave();
            try { Thread.sleep(2500); } catch (Exception ignore) {}
            healthScreen.clickCheckoutPayNow();
        }

        System.out.println("[Step 8] Verifying Policy Issuance Confirmation...");
        PolicyConfirmationScreen confirmationScreen = new PolicyConfirmationScreen();
        boolean isSuccess = confirmationScreen.isPolicyConfirmed();
        PerformanceBenchmark.stopStage("Health_Checkout_Payment", PerformanceBenchmark.SLA_CHECKOUT_PREP + PerformanceBenchmark.SLA_3DS_PAYMENT);
        Assert.assertTrue(isSuccess, "Policy Confirmation screen was not reached for Individual Health Insurance!");

        String policyNumber = confirmationScreen.getPolicyNumber();
        System.out.println("Individual Health Flow 1 Completed Successfully! Policy Issued: " + policyNumber);
        confirmationScreen.clickCloseToHome();
    }

    @Test(priority = 2, groups = {"health", "individual-health", "variant"},
          description = "Individual Health Flow 2: SME / Multi-Tier Plan Comparison (Bronze vs Silver vs Gold vs Platinum)")
    public void testIndividualHealthFlow2_MultiTierPlanComparison() {
        System.out.println("\n========================================================");
        System.out.println("  [INDIVIDUAL HEALTH FLOW 2] Multi-Tier Plan Comparison ");
        System.out.println("========================================================");

        HomeScreen homeScreen = new HomeScreen();
        homeScreen.clickHealthInsuranceProduct();

        IndividualHealthScreen healthScreen = new IndividualHealthScreen();
        healthScreen.clickInsureNowIfPresent();

        String email = TestDataBuilder.generateUniqueEmail();
        healthScreen.fillApplicantDetails(VALID_ID, VALID_PHONE, email);
        healthScreen.selectDob();
        healthScreen.acceptPrivacyNotice();
        healthScreen.clickNext();

        OtpModal otpModal = new OtpModal();
        otpModal.enterOtp(TestDataBuilder.DEFAULT_OTP);

        healthScreen.addDependent(DEPENDENT_IQAMA, "Spouse");
        healthScreen.clickProceedToQuote();

        healthScreen.answerAllDeclarationQuestions(false);
        healthScreen.clickDeclarationProceed();

        System.out.println("[Step 1] Evaluating Bronze, Silver, Platinum and Gold Plan tiers...");
        healthScreen.waitForPlanSelectionScreen();

        System.out.println("Inspecting Bronze Plan tier...");
        healthScreen.selectBronzePlan();

        System.out.println("Inspecting Silver Plan tier...");
        healthScreen.selectSilverPlan();

        System.out.println("Inspecting Platinum Plan tier...");
        healthScreen.selectPlatinumPlan();

        System.out.println("Finalizing on Gold Plan tier with 20% Co-pay Deductible...");
        healthScreen.selectGoldPlan();
        healthScreen.selectDeductible("20%");
        healthScreen.clickBuyNow();

        healthScreen.fillNationalAddress("1234", "Riyadh", "Riyadh", "King Fahd Rd", "Al Olaya", "5678", "12211");
        healthScreen.fillIbanAndAcceptTerms(TestDataBuilder.DEFAULT_IBAN);
        healthScreen.clickCheckoutPayNow();

        AddNewCardModal cardModal = new AddNewCardModal();
        if (cardModal.isModalDisplayed()) {
            TestDataBuilder.CreditCard card = TestDataBuilder.CreditCard.defaultCard();
            cardModal.enterCardNumber(card.number);
            cardModal.enterExpiryDate(card.expiry);
            cardModal.enterCvv(card.cvv);
            cardModal.enterCardHolderName(card.holderName);
            cardModal.clickSave();
            try { Thread.sleep(2500); } catch (Exception ignore) {}
            healthScreen.clickCheckoutPayNow();
        }

        PolicyConfirmationScreen confirmationScreen = new PolicyConfirmationScreen();
        boolean isSuccess = confirmationScreen.isPolicyConfirmed();
        Assert.assertTrue(isSuccess, "Policy Confirmation screen was not reached for Individual Health Flow 2!");
        String policyNumber = confirmationScreen.getPolicyNumber();
        System.out.println("Individual Health Flow 2 Completed Successfully! Policy Issued: " + policyNumber);
        confirmationScreen.clickCloseToHome();
    }

    @Test(priority = 3, groups = {"health", "individual-health", "underwriting"},
          description = "Individual Health Flow 3: Pre-existing Conditions Declaration ('Yes' path triggering Underwriting Referral)")
    public void testIndividualHealthFlow3_PreExistingConditionsUnderwritingReferral() {
        System.out.println("\n========================================================");
        System.out.println("  [INDIVIDUAL HEALTH FLOW 3] Underwriting Referral Flow ");
        System.out.println("========================================================");

        HomeScreen homeScreen = new HomeScreen();
        homeScreen.clickHealthInsuranceProduct();

        IndividualHealthScreen healthScreen = new IndividualHealthScreen();
        healthScreen.clickInsureNowIfPresent();

        String email = TestDataBuilder.generateUniqueEmail();
        healthScreen.fillApplicantDetails(VALID_ID, VALID_PHONE, email);
        healthScreen.selectDob();
        healthScreen.acceptPrivacyNotice();
        healthScreen.clickNext();

        OtpModal otpModal = new OtpModal();
        otpModal.enterOtp(TestDataBuilder.DEFAULT_OTP);

        healthScreen.addDependent(DEPENDENT_IQAMA, "Spouse");
        healthScreen.clickProceedToQuote();

        System.out.println("[Underwriting Trigger] Declaring pre-existing chronic conditions ('Yes' path)...");
        healthScreen.answerAllDeclarationQuestions(true);
        healthScreen.clickDeclarationProceed();

        boolean isReferralTriggered = healthScreen.isUnderwritingReferralDisplayed();
        Assert.assertTrue(isReferralTriggered,
                "Underwriting referral or review notice should be triggered on declared medical conditions!");
        System.out.println("Underwriting referral verified: " + isReferralTriggered);
    }

    @Test(priority = 4, groups = {"health", "individual-health", "negative"},
          description = "Individual Health Flow 4: Negative & Boundary Validation (Invalid National ID & Field Constraints)")
    public void testIndividualHealthFlow4_NegativeBoundaryValidation() {
        System.out.println("\n========================================================");
        System.out.println("  [INDIVIDUAL HEALTH FLOW 4] Negative & Boundary Tests   ");
        System.out.println("========================================================");

        HomeScreen homeScreen = new HomeScreen();
        homeScreen.clickHealthInsuranceProduct();

        IndividualHealthScreen healthScreen = new IndividualHealthScreen();
        healthScreen.clickInsureNowIfPresent();

        System.out.println("[Validation 1] Submitting invalid National ID (1111111111)...");
        String invalidId = "1111111111";
        healthScreen.enterNationalId(invalidId);
        healthScreen.enterPhoneNumber(VALID_PHONE);
        healthScreen.enterEmail("invalid.user@tree.com.sa");
        healthScreen.acceptPrivacyNotice();
        healthScreen.clickNext();

        boolean hasError = healthScreen.isValidationErrorDisplayed();
        Assert.assertTrue(hasError, "Validation error message should display when invalid National ID is submitted!");
        System.out.println("Negative National ID validation verified: " + hasError);
    }
}
