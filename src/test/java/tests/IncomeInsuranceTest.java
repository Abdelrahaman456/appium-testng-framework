package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AddNewCardModal;
import pages.CheckoutScreen;
import pages.HomeScreen;
import pages.IncomeInsuranceScreen;
import pages.OtpModal;
import pages.PolicyConfirmationScreen;
import utils.PerformanceBenchmark;
import utils.TestDataBuilder;

public class IncomeInsuranceTest extends BaseTest {

    // Identity Tuple: ID 1313424273 + Phone 500421222 + Birth Year 1998
    private static final String VALID_ID = "1313424273";
    private static final String VALID_PHONE = "500421222";

    @Test(priority = 1, groups = {"income", "e2e", "happy-path"}, description = "Income Insurance Flow 1: Primary Happy Path End-to-End (Employed, 15k SAR Income, Standard Plan)")
    public void testIncomeInsuranceFlow1_HappyPath() {
        System.out.println("\n=== [INCOME INSURANCE FLOW 1] Primary Happy Path End-to-End ===");

        HomeScreen homeScreen = new HomeScreen();
        homeScreen.clickIncomeProduct();

        IncomeInsuranceScreen incomeScreen = new IncomeInsuranceScreen();
        incomeScreen.clickInsureNowIfPresent();

        PerformanceBenchmark.startStage("Income_Form_Fill");
        String email = TestDataBuilder.generateUniqueEmail();
        incomeScreen.fillApplicantDetails(VALID_ID, VALID_PHONE, email);
        incomeScreen.selectDob(); // 1998 wheel physics
        incomeScreen.acceptPrivacyNotice();
        incomeScreen.clickNext();

        OtpModal otpModal = new OtpModal();
        otpModal.enterOtp(TestDataBuilder.DEFAULT_OTP);

        // Employment details: Employed with SAR 15,000 monthly income
        incomeScreen.fillEmploymentDetails("Employed", "15000", "Software Engineer");
        incomeScreen.clickEmploymentProceed();

        // Disability & Health Declarations: "No" for instant quote
        incomeScreen.answerAllDeclarationQuestions(false);
        incomeScreen.clickDeclarationProceed();
        PerformanceBenchmark.stopStage("Income_Form_Fill", PerformanceBenchmark.SLA_FORM_INPUT);

        // Plan Selection: Standard Plan
        PerformanceBenchmark.startStage("Income_Quote_Rating");
        incomeScreen.waitForPlanSelectionScreen();
        PerformanceBenchmark.stopStage("Income_Quote_Rating", PerformanceBenchmark.SLA_QUOTE_RATING);

        PerformanceBenchmark.startStage("Income_Plan_Selection");
        incomeScreen.selectStandardPlan();
        incomeScreen.clickBuyNow();
        PerformanceBenchmark.stopStage("Income_Plan_Selection", PerformanceBenchmark.SLA_ADDON_RERATE);

        // Checkout & National Address (IBAN stripped of 'SA')
        PerformanceBenchmark.startStage("Income_Checkout_Payment");
        incomeScreen.fillNationalAddress("1234", "Riyadh", "Riyadh", "King Fahd Rd", "Al Olaya", "5678", "12211");
        incomeScreen.fillIbanAndAcceptTerms(TestDataBuilder.DEFAULT_IBAN);
        incomeScreen.clickCheckoutPayNow();

        AddNewCardModal cardModal = new AddNewCardModal();
        Assert.assertTrue(cardModal.isModalDisplayed(), "Add New Card modal should be displayed on Income Insurance Checkout!");
        TestDataBuilder.CreditCard card = TestDataBuilder.CreditCard.defaultCard();
        cardModal.enterCardNumber(card.number);
        cardModal.enterExpiryDate(card.expiry);
        cardModal.enterCvv(card.cvv);
        cardModal.enterCardHolderName(card.holderName);
        cardModal.clickSave();
        try { Thread.sleep(2500); } catch (Exception ignore) {}
        new CheckoutScreen().clickPayNow();

        PolicyConfirmationScreen confirmationScreen = new PolicyConfirmationScreen();
        boolean isSuccess = confirmationScreen.isPolicyConfirmed();
        PerformanceBenchmark.stopStage("Income_Checkout_Payment", PerformanceBenchmark.SLA_CHECKOUT_PREP + PerformanceBenchmark.SLA_3DS_PAYMENT);
        Assert.assertTrue(isSuccess, "Policy Confirmation screen was not reached for Income Protection Insurance!");
        System.out.println("=== Income Insurance Flow 1 Completed Successfully! ===");
        confirmationScreen.clickCloseToHome();
    }

    private void completeCheckoutAndPayment(IncomeInsuranceScreen incomeScreen, String flowLabel) {
        incomeScreen.fillNationalAddress("1234", "Riyadh", "Riyadh", "King Fahd Rd", "Al Olaya", "5678", "12211");
        incomeScreen.fillIbanAndAcceptTerms(TestDataBuilder.DEFAULT_IBAN);
        incomeScreen.clickCheckoutPayNow();

        AddNewCardModal cardModal = new AddNewCardModal();
        Assert.assertTrue(cardModal.isModalDisplayed(), "Add New Card modal should be displayed for " + flowLabel + "!");
        TestDataBuilder.CreditCard card = TestDataBuilder.CreditCard.defaultCard();
        cardModal.enterCardNumber(card.number);
        cardModal.enterExpiryDate(card.expiry);
        cardModal.enterCvv(card.cvv);
        cardModal.enterCardHolderName(card.holderName);
        cardModal.clickSave();
        try { Thread.sleep(2500); } catch (Exception ignore) {}
        new CheckoutScreen().clickPayNow();

        PolicyConfirmationScreen confirmationScreen = new PolicyConfirmationScreen();
        boolean isSuccess = confirmationScreen.isPolicyConfirmed();
        Assert.assertTrue(isSuccess, "Policy Confirmation screen was not reached for " + flowLabel + "!");
        System.out.println("=== " + flowLabel + " Completed Successfully! ===");
        confirmationScreen.clickCloseToHome();
    }

    @Test(priority = 2, groups = {"income", "variant"}, description = "Income Insurance Flow 2: Tier & Employment Comparison (Self-Employed Comprehensive vs Employed Basic)")
    public void testIncomeInsuranceFlow2_TierEmploymentComparison() {
        System.out.println("\n=== [INCOME INSURANCE FLOW 2] Tier & Employment Comparison (Self-Employed) ===");

        HomeScreen homeScreen = new HomeScreen();
        homeScreen.clickIncomeProduct();

        IncomeInsuranceScreen incomeScreen = new IncomeInsuranceScreen();
        incomeScreen.clickInsureNowIfPresent();

        String email = TestDataBuilder.generateUniqueEmail();
        incomeScreen.fillApplicantDetails(VALID_ID, VALID_PHONE, email);
        incomeScreen.selectDob();
        incomeScreen.acceptPrivacyNotice();
        incomeScreen.clickNext();

        OtpModal otpModal = new OtpModal();
        otpModal.enterOtp(TestDataBuilder.DEFAULT_OTP);

        // Self-Employed with SAR 25,000 monthly income
        incomeScreen.fillEmploymentDetails("Self-Employed", "25000", "Business Owner");
        incomeScreen.clickEmploymentProceed();

        incomeScreen.answerAllDeclarationQuestions(false);
        incomeScreen.clickDeclarationProceed();

        incomeScreen.waitForPlanSelectionScreen();

        System.out.println("Toggling Basic Plan...");
        incomeScreen.selectBasicPlan();

        System.out.println("Selecting Comprehensive Plan for Self-Employed coverage...");
        incomeScreen.selectComprehensivePlan();
        incomeScreen.clickBuyNow();

        completeCheckoutAndPayment(incomeScreen, "Income Insurance Flow 2 Tier & Employment Comparison");
    }

    @Test(priority = 3, groups = {"income", "underwriting"}, description = "Income Insurance Flow 3: Pre-Existing Condition Exclusion Gate on Quote")
    public void testIncomeInsuranceFlow3_UnderwritingConditionDeclared() {
        System.out.println("\n=== [INCOME INSURANCE FLOW 3] Pre-Existing Diseases Exclusion Gate ===");

        HomeScreen homeScreen = new HomeScreen();
        homeScreen.clickIncomeProduct();

        IncomeInsuranceScreen incomeScreen = new IncomeInsuranceScreen();
        incomeScreen.clickInsureNowIfPresent();

        String email = TestDataBuilder.generateUniqueEmail();
        incomeScreen.fillApplicantDetails(VALID_ID, VALID_PHONE, email);
        incomeScreen.selectDob();
        incomeScreen.acceptPrivacyNotice();
        incomeScreen.clickNext();

        OtpModal otpModal = new OtpModal();
        otpModal.enterOtp(TestDataBuilder.DEFAULT_OTP);

        incomeScreen.waitForPlanSelectionScreen();

        Assert.assertTrue(incomeScreen.isPreExistingExclusionNoticeDisplayed(),
                "Quote screen should display pre-existing diseases exclusion switch and 30-day waiting notice!");
        Assert.assertFalse(incomeScreen.isPreExistingExclusionChecked(),
                "Pre-existing diseases exclusion checkbox should start unchecked on Quote screen!");

        // Proceed reports enabled='true' in Flutter semantics even while visually disabled; verify click stays on Quote
        incomeScreen.tapQuoteProceed();
        Assert.assertFalse(incomeScreen.isCheckoutActiveNow(),
                "Proceed must NOT advance to Checkout while pre-existing diseases exclusion checkbox is unchecked!");
        Assert.assertTrue(incomeScreen.isPreExistingExclusionNoticeDisplayed(),
                "Quote screen with pre-existing diseases exclusion and 30-day notice should remain visible when unchecked!");

        incomeScreen.acceptPreExistingExclusionNotice();
        Assert.assertTrue(incomeScreen.isQuoteProceedButtonEnabled(),
                "Proceed button should be enabled after accepting pre-existing diseases exclusion checkbox!");
        incomeScreen.tapQuoteProceed();
        Assert.assertTrue(incomeScreen.isCheckoutLoaded(),
                "Checkout screen should load after accepting pre-existing diseases exclusion checkbox and clicking Proceed!");
        System.out.println("=== Income Insurance Flow 3 Completed Successfully! ===");
    }

    @Test(priority = 4, groups = {"income", "negative"}, description = "Income Insurance Flow 4: About You Form Validation (Invalid ID / Missing DOB)")
    public void testIncomeInsuranceFlow4_FormValidation() {
        System.out.println("\n=== [INCOME INSURANCE FLOW 4] About You Form Validation (Invalid ID / Missing DOB) ===");

        HomeScreen homeScreen = new HomeScreen();
        homeScreen.clickIncomeProduct();

        IncomeInsuranceScreen incomeScreen = new IncomeInsuranceScreen();
        incomeScreen.clickInsureNowIfPresent();

        String email = TestDataBuilder.generateUniqueEmail();
        // Enter invalid National ID and omit DOB selection to validate About you form blocking
        incomeScreen.fillApplicantDetails("0000000000", VALID_PHONE, email);
        incomeScreen.acceptPrivacyNotice();
        incomeScreen.clickNext();

        OtpModal otpModal = new OtpModal();
        Assert.assertFalse(otpModal.isModalDisplayed(),
                "OTP modal must NOT open when National ID is invalid and DOB is missing!");
        Assert.assertTrue(incomeScreen.isApplicantDetailsDisplayed(),
                "App must remain on 'About you' screen when form inputs are invalid/incomplete!");

        boolean hasErrorOrDisabledNext = incomeScreen.isValidationErrorDisplayed()
                || !incomeScreen.isAboutYouNextEnabled();
        Assert.assertTrue(hasErrorOrDisabledNext,
                "Validation error should display or Next button should remain disabled on 'About you'!");
        System.out.println("Validation error/disabled gate triggered on About you: " + hasErrorOrDisabledNext);
        System.out.println("=== Income Insurance Flow 4 Completed Successfully! ===");
    }

    @Test(priority = 5, groups = {"income", "unemployment", "disability"}, description = "Income Insurance Flow 5: Involuntary Unemployment vs Permanent Disability with Waiting Period")
    public void testIncomeInsuranceFlow5_InvoluntaryUnemploymentAndDisabilityCover() {
        System.out.println("\n=== [INCOME INSURANCE FLOW 5] Involuntary Unemployment & Disability Cover ===");

        HomeScreen homeScreen = new HomeScreen();
        homeScreen.clickIncomeProduct();

        IncomeInsuranceScreen incomeScreen = new IncomeInsuranceScreen();
        incomeScreen.clickInsureNowIfPresent();

        String email = TestDataBuilder.generateUniqueEmail();
        incomeScreen.fillApplicantDetails(VALID_ID, VALID_PHONE, email);
        incomeScreen.selectDob();
        incomeScreen.acceptPrivacyNotice();
        incomeScreen.clickNext();

        OtpModal otpModal = new OtpModal();
        otpModal.enterOtp(TestDataBuilder.DEFAULT_OTP);

        incomeScreen.fillEmploymentDetails("Employed", "20000", "Project Manager");
        incomeScreen.selectInvoluntaryUnemploymentCover();
        incomeScreen.selectPermanentDisabilityCover();
        incomeScreen.selectWaitingPeriod("60");
        incomeScreen.clickEmploymentProceed();

        incomeScreen.answerAllDeclarationQuestions(false);
        incomeScreen.clickDeclarationProceed();

        incomeScreen.waitForPlanSelectionScreen();
        incomeScreen.selectComprehensivePlan();
        incomeScreen.clickBuyNow();

        completeCheckoutAndPayment(incomeScreen, "Income Insurance Flow 5 Involuntary Unemployment & Disability");
    }
}
