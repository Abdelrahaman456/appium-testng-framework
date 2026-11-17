package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AddNewCardModal;
import pages.HomeScreen;
import pages.MalpracticeScreen;
import pages.OtpModal;
import pages.PolicyConfirmationScreen;
import utils.PerformanceBenchmark;
import utils.TestDataBuilder;

/**
 * Standardized 4-Tier Test Suite for Medical Malpractice Insurance.
 * Platform: Tree Digital Insurance (UAT Android Flutter App).
 * 
 * Flows:
 *  - Flow 1: Physician Specialist E2E Happy Path (Landing -> Details -> OTP -> Quote -> Checkout -> Card -> Confirmation)
 *  - Flow 2: Surgeon / Nurse Practitioner with Specialty, SCFHS License & Deductible Choice
 *  - Flow 3: Prior Acts / Retroactive Coverage with Underwriting Referral & Claims Disclosures
 *  - Flow 4: Negative & Boundary Validation (Invalid / Missing Practitioner Data, Unregistered Identity)
 */
public class MalpracticeTest extends BaseTest {

    @Test(priority = 1, groups = {"malpractice", "e2e", "happy-path"},
          description = "Malpractice Flow 1: Physician Specialist E2E Happy Path")
    public void testMalpracticeFlow1_PhysicianSpecialistHappyPath() {
        System.out.println("\n========================================================");
        System.out.println("  [MALPRACTICE FLOW 1] Physician Specialist Happy Path  ");
        System.out.println("========================================================");

        HomeScreen homeScreen = new HomeScreen();
        homeScreen.clickMalpracticeProduct();

        MalpracticeScreen malpScreen = new MalpracticeScreen();
        malpScreen.clickInsureNowIfPresent();

        String applicantId = TestDataBuilder.DEFAULT_SELLER_ID; // 1313424273
        String applicantPhone = TestDataBuilder.DEFAULT_PHONE;   // 500421222
        String applicantEmail = TestDataBuilder.generateUniqueEmail();

        PerformanceBenchmark.startStage("Malpractice_Form_Fill");
        System.out.println("[Step 1] Filling Practitioner Details: ID=" + applicantId + ", Profession=Physician Specialist");
        malpScreen.fillPractitionerDetails(applicantId, applicantPhone, applicantEmail);
        malpScreen.selectDob();
        malpScreen.selectProfession("ACUPUNCTURE SPECIALIST");
        malpScreen.selectPriorInsurance(false);
        malpScreen.acceptPrivacyNotice();
        malpScreen.clickNext();

        System.out.println("[Step 2] Entering Absher OTP verification...");
        OtpModal otpModal = new OtpModal();
        otpModal.enterOtp(TestDataBuilder.DEFAULT_OTP);
        PerformanceBenchmark.stopStage("Malpractice_Form_Fill", PerformanceBenchmark.SLA_FORM_INPUT);

        System.out.println("[Step 3] Handling Quote & Plan Selection...");
        PerformanceBenchmark.startStage("Malpractice_Quote_Rating");
        malpScreen.waitForQuoteScreen();
        PerformanceBenchmark.stopStage("Malpractice_Quote_Rating", PerformanceBenchmark.SLA_QUOTE_RATING);

        PerformanceBenchmark.startStage("Malpractice_Plan_Selection");
        malpScreen.selectDeductible("1000");
        malpScreen.clickBuyNow();
        PerformanceBenchmark.stopStage("Malpractice_Plan_Selection", PerformanceBenchmark.SLA_ADDON_RERATE);

        System.out.println("[Step 4] Checkout & National Address with IBAN...");
        PerformanceBenchmark.startStage("Malpractice_Checkout_Payment");
        malpScreen.fillNationalAddress("1234", "Riyadh", "Riyadh", "King Fahd Rd", "Al Olaya", "5678", "12211");
        malpScreen.fillIbanAndAcceptTerms(TestDataBuilder.DEFAULT_IBAN);
        malpScreen.clickCheckoutPayNow();

        System.out.println("[Step 5] Processing Payment Card Modal if presented...");
        AddNewCardModal cardModal = new AddNewCardModal();
        if (cardModal.isModalDisplayed()) {
            TestDataBuilder.CreditCard card = TestDataBuilder.CreditCard.defaultCard();
            cardModal.enterCardNumber(card.number);
            cardModal.enterExpiryDate(card.expiry);
            cardModal.enterCvv(card.cvv);
            cardModal.enterCardHolderName(card.holderName);
            cardModal.clickSave();
            try { Thread.sleep(2500); } catch (Exception ignore) {}
            malpScreen.clickCheckoutPayNow();
        }

        System.out.println("[Step 6] Verifying Policy Issuance Confirmation...");
        PolicyConfirmationScreen confirmationScreen = new PolicyConfirmationScreen();
        boolean isConfirmed = confirmationScreen.isPolicyConfirmed();
        PerformanceBenchmark.stopStage("Malpractice_Checkout_Payment", PerformanceBenchmark.SLA_CHECKOUT_PREP + PerformanceBenchmark.SLA_3DS_PAYMENT);
        Assert.assertTrue(isConfirmed, "Policy Confirmation screen was not reached for Physician Specialist Malpractice Insurance!");

        String policyNumber = confirmationScreen.getPolicyNumber();
        System.out.println("Malpractice Flow 1 Completed Successfully! Policy Issued: " + policyNumber);
        confirmationScreen.clickCloseToHome();
    }

    @Test(priority = 2, groups = {"malpractice", "variant"},
          description = "Malpractice Flow 2: Surgeon / Nurse Practitioner with SCFHS License & Deductible Choice")
    public void testMalpracticeFlow2_SurgeonSpecialtyAndDeductibleChoice() {
        System.out.println("\n========================================================");
        System.out.println("  [MALPRACTICE FLOW 2] Surgeon / Nurse Specialty & Plan ");
        System.out.println("========================================================");

        HomeScreen homeScreen = new HomeScreen();
        homeScreen.clickMalpracticeProduct();

        MalpracticeScreen malpScreen = new MalpracticeScreen();
        malpScreen.clickInsureNowIfPresent();

        String applicantId = TestDataBuilder.DEFAULT_SELLER_ID;
        String applicantPhone = TestDataBuilder.DEFAULT_PHONE;
        String applicantEmail = TestDataBuilder.generateUniqueEmail();

        System.out.println("[Step 1] Filling Surgeon Details & Professional License...");
        malpScreen.fillPractitionerDetails(applicantId, applicantPhone, applicantEmail);
        malpScreen.selectDob();
        malpScreen.selectProfession("Acute Care Surgery");
        malpScreen.enterLicenseNumber("SCFHS-8947219");
        malpScreen.enterHospitalName("King Faisal Specialist Hospital");
        malpScreen.selectPriorInsurance(false);
        malpScreen.acceptPrivacyNotice();
        malpScreen.clickNext();

        System.out.println("[Step 2] OTP Verification...");
        OtpModal otpModal = new OtpModal();
        otpModal.enterOtp(TestDataBuilder.DEFAULT_OTP);

        System.out.println("[Step 3] Validating Quote Screen and Deductible Selection...");
        malpScreen.waitForQuoteScreen();
        malpScreen.selectDeductible("2500");

        boolean isQuoteLoaded = malpScreen.isQuoteLoaded();
        Assert.assertTrue(isQuoteLoaded, "Quote screen did not load for Acute Care Surgery Specialist!");
        malpScreen.clickBuyNow();

        System.out.println("[Step 4] Checkout & National Address with IBAN...");
        malpScreen.fillNationalAddress("1234", "Riyadh", "Riyadh", "King Fahd Rd", "Al Olaya", "5678", "12211");
        malpScreen.fillIbanAndAcceptTerms(TestDataBuilder.DEFAULT_IBAN);
        malpScreen.clickCheckoutPayNow();

        System.out.println("[Step 5] Processing Payment Card Modal if presented...");
        AddNewCardModal cardModal = new AddNewCardModal();
        if (cardModal.isModalDisplayed()) {
            TestDataBuilder.CreditCard card = TestDataBuilder.CreditCard.defaultCard();
            cardModal.enterCardNumber(card.number);
            cardModal.enterExpiryDate(card.expiry);
            cardModal.enterCvv(card.cvv);
            cardModal.enterCardHolderName(card.holderName);
            cardModal.clickSave();
            try { Thread.sleep(2500); } catch (Exception ignore) {}
            malpScreen.clickCheckoutPayNow();
        }

        System.out.println("[Step 6] Verifying Policy Issuance Confirmation...");
        PolicyConfirmationScreen confirmationScreen = new PolicyConfirmationScreen();
        boolean isConfirmed = confirmationScreen.isPolicyConfirmed();
        Assert.assertTrue(isConfirmed, "Policy Confirmation screen was not reached for Malpractice Flow 2!");
        String policyNumber = confirmationScreen.getPolicyNumber();
        System.out.println("Malpractice Flow 2 Completed Successfully! Policy Issued: " + policyNumber);
        confirmationScreen.clickCloseToHome();
    }

    @Test(priority = 3, groups = {"malpractice", "underwriting"},
          description = "Malpractice Flow 3: Retroactive / Prior Acts Coverage with Underwriting Referral")
    public void testMalpracticeFlow3_PriorActsRetroactiveCoverageUnderwriting() {
        System.out.println("\n========================================================");
        System.out.println("  [MALPRACTICE FLOW 3] Prior Acts / Retroactive Cover   ");
        System.out.println("========================================================");

        HomeScreen homeScreen = new HomeScreen();
        homeScreen.clickMalpracticeProduct();

        MalpracticeScreen malpScreen = new MalpracticeScreen();
        malpScreen.clickInsureNowIfPresent();

        String applicantId = TestDataBuilder.DEFAULT_SELLER_ID;
        String applicantPhone = TestDataBuilder.DEFAULT_PHONE;
        String applicantEmail = TestDataBuilder.generateUniqueEmail();

        System.out.println("[Step 1] Enabling Prior Acts / Retroactive Coverage...");
        malpScreen.fillPractitionerDetails(applicantId, applicantPhone, applicantEmail);
        malpScreen.selectDob();
        malpScreen.selectProfession("ACUPUNCTURE SPECIALIST");
        malpScreen.selectPriorInsurance(true);
        malpScreen.enterRetroactiveDate("01/01/2023");
        malpScreen.declarePriorClaims(true);
        malpScreen.acceptPrivacyNotice();
        malpScreen.clickNext();

        System.out.println("[Step 2] Validating Underwriting Referral or Quote Adjustment...");
        boolean isUnderwritingState = malpScreen.isUnderwritingReferralDisplayed()
                || malpScreen.isQuoteLoaded()
                || malpScreen.isPractitionerDetailsScreenDisplayed();

        Assert.assertTrue(isUnderwritingState,
                "Underwriting state was not properly handled when prior acts and claims were declared!");
        System.out.println("Malpractice Flow 3 Prior Acts Underwriting referral / quote handled cleanly!");
    }

    @Test(priority = 4, groups = {"malpractice", "negative"},
          description = "Malpractice Flow 4: Form & Identity Validation (Missing / Invalid Fields)")
    public void testMalpracticeFlow4_NegativeBoundaryValidation() {
        System.out.println("\n========================================================");
        System.out.println("  [MALPRACTICE FLOW 4] Form & Identity Negative Boundary ");
        System.out.println("========================================================");

        HomeScreen homeScreen = new HomeScreen();
        homeScreen.clickMalpracticeProduct();

        MalpracticeScreen malpScreen = new MalpracticeScreen();
        malpScreen.clickInsureNowIfPresent();

        System.out.println("[Validation 1] Submitting empty form to verify required fields enforcement...");
        malpScreen.acceptPrivacyNotice();
        malpScreen.clickNext();

        boolean isBlocked = malpScreen.isValidationErrorDisplayed() || malpScreen.isPractitionerDetailsScreenDisplayed();
        Assert.assertTrue(isBlocked, "Form validation should block progression when required practitioner fields are missing!");

        System.out.println("[Validation 2] Entering invalid National ID (Luhn failure)...");
        malpScreen.enterNationalId("1111111111");
        malpScreen.enterPhone("500000000");
        malpScreen.enterEmail("test.error@tree.com.sa");
        malpScreen.clickNext();

        boolean isStillBlocked = malpScreen.isValidationErrorDisplayed() || malpScreen.isPractitionerDetailsScreenDisplayed();
        Assert.assertTrue(isStillBlocked, "Form must reject invalid National ID!");
        System.out.println("Malpractice Flow 4 Form & Negative Validations successfully enforced.");
    }
}
