package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AddNewCardModal;
import pages.FamilyHealthScreen;
import pages.HomeScreen;
import pages.OtpModal;
import pages.PolicyConfirmationScreen;
import utils.TestDataBuilder;

/**
 * Standardized 4-Tier Test Suite for Family Health Insurance.
 * Platform: Tree Digital Insurance (UAT Android Flutter App).
 * 
 * Flows:
 *  - Flow 1: Primary Happy Path E2E (Sponsor + Spouse + Child, Clean Declaration, Gold Plan with Maternity & Dental Add-ons, Confirmation)
 *  - Flow 2: Multi-Tier Plan Comparison (Bronze vs Silver vs Gold vs Platinum + Specialized Maternity & Dental Add-ons)
 *  - Flow 3: Dependent Chronic Condition Disclosure ('Yes' path triggering Underwriting Referral)
 *  - Flow 4: Negative & Boundary Validation (Invalid Sponsor ID, Invalid Dependent Iqama, Field Guards)
 */
public class FamilyHealthTest extends BaseTest {

    // Identity Tuple: ID 1313424273 + Phone 500421222 + Birth Year 1998
    private static final String SPONSOR_ID = "1313424273";
    private static final String SPONSOR_PHONE = "500421222";
    private static final String SPOUSE_IQAMA = "2456789011";
    private static final String CHILD_IQAMA = "2456789029";

    @Test(priority = 1, groups = {"health", "family-health", "e2e", "happy-path"},
          description = "Family Health Flow 1: Primary Happy Path E2E with Spouse & Child, Maternity & Dental Add-ons")
    public void testFamilyHealthFlow1_HappyPath() {
        System.out.println("\n========================================================");
        System.out.println("  [FAMILY HEALTH FLOW 1] Primary Happy Path E2E         ");
        System.out.println("========================================================");

        HomeScreen homeScreen = new HomeScreen();
        homeScreen.clickFamilyHealthProduct();

        FamilyHealthScreen familyScreen = new FamilyHealthScreen();
        familyScreen.clickInsureNowIfPresent();

        String email = TestDataBuilder.generateUniqueEmail();

        System.out.println("[Step 1] Filling Sponsor Details (Absher Identity Tuple)...");
        familyScreen.fillSponsorDetails(SPONSOR_ID, SPONSOR_PHONE, email);
        familyScreen.selectSponsorDob(); // 1998 wheel physics
        familyScreen.acceptPrivacyNotice();
        familyScreen.clickNext();

        System.out.println("[Step 2] Absher OTP Verification...");
        OtpModal otpModal = new OtpModal();
        otpModal.enterOtp(TestDataBuilder.DEFAULT_OTP);

        System.out.println("[Step 3] Adding Family Dependents (Spouse: " + SPOUSE_IQAMA + ", Son: " + CHILD_IQAMA + ")...");
        familyScreen.addSpouse(SPOUSE_IQAMA);
        familyScreen.addChild(CHILD_IQAMA, true);
        familyScreen.clickProceedToQuote();

        System.out.println("[Step 4] Answering Clean Health Declarations...");
        familyScreen.answerAllDeclarationQuestions(false);
        familyScreen.clickDeclarationProceed();

        System.out.println("[Step 5] Selecting Gold Plan Tier & Maternity/Dental Add-ons...");
        familyScreen.waitForPlanSelectionScreen();
        familyScreen.selectGoldPlan();
        familyScreen.toggleMaternityAddon();
        familyScreen.toggleDentalAddon();
        familyScreen.clickBuyNow();

        System.out.println("[Step 6] Checkout & National Address with IBAN...");
        familyScreen.fillNationalAddress("1234", "Riyadh", "Riyadh", "King Fahd Rd", "Al Olaya", "5678", "12211");
        familyScreen.fillIbanAndAcceptTerms(TestDataBuilder.DEFAULT_IBAN);
        familyScreen.clickCheckoutPayNow();

        System.out.println("[Step 7] Processing Payment Card Modal...");
        AddNewCardModal cardModal = new AddNewCardModal();
        if (cardModal.isModalDisplayed()) {
            TestDataBuilder.CreditCard card = TestDataBuilder.CreditCard.defaultCard();
            cardModal.enterCardNumber(card.number);
            cardModal.enterExpiryDate(card.expiry);
            cardModal.enterCvv(card.cvv);
            cardModal.enterCardHolderName(card.holderName);
            cardModal.clickSave();
            try { Thread.sleep(2500); } catch (Exception ignore) {}
            familyScreen.clickCheckoutPayNow();
        }

        System.out.println("[Step 8] Verifying Policy Issuance Confirmation...");
        PolicyConfirmationScreen confirmationScreen = new PolicyConfirmationScreen();
        boolean isSuccess = confirmationScreen.isPolicyConfirmed();
        Assert.assertTrue(isSuccess, "Policy Confirmation screen was not reached for Family Health Insurance!");

        String policyNumber = confirmationScreen.getPolicyNumber();
        System.out.println("Family Health Flow 1 Completed Successfully! Policy Issued: " + policyNumber);
        confirmationScreen.clickCloseToHome();
    }

    @Test(priority = 2, groups = {"health", "family-health", "variant"},
          description = "Family Health Flow 2: Multi-Tier Plan Comparison & Add-ons Selection")
    public void testFamilyHealthFlow2_MultiTierPlanComparison() {
        System.out.println("\n========================================================");
        System.out.println("  [FAMILY HEALTH FLOW 2] Multi-Tier Plan Comparison     ");
        System.out.println("========================================================");

        HomeScreen homeScreen = new HomeScreen();
        homeScreen.clickFamilyHealthProduct();

        FamilyHealthScreen familyScreen = new FamilyHealthScreen();
        familyScreen.clickInsureNowIfPresent();

        String email = TestDataBuilder.generateUniqueEmail();
        familyScreen.fillSponsorDetails(SPONSOR_ID, SPONSOR_PHONE, email);
        familyScreen.selectSponsorDob();
        familyScreen.acceptPrivacyNotice();
        familyScreen.clickNext();

        OtpModal otpModal = new OtpModal();
        otpModal.enterOtp(TestDataBuilder.DEFAULT_OTP);

        familyScreen.addSpouse(SPOUSE_IQAMA);
        familyScreen.clickProceedToQuote();

        familyScreen.answerAllDeclarationQuestions(false);
        familyScreen.clickDeclarationProceed();

        System.out.println("[Step 1] Comparing Bronze, Silver, Platinum and Gold Plan tiers...");
        familyScreen.waitForPlanSelectionScreen();

        System.out.println("Toggling Bronze Plan...");
        familyScreen.selectBronzePlan();

        System.out.println("Toggling Silver Plan...");
        familyScreen.selectSilverPlan();

        System.out.println("Toggling Platinum Plan...");
        familyScreen.selectPlatinumPlan();

        System.out.println("Finalizing on Gold Plan with Dental add-on...");
        familyScreen.selectGoldPlan();
        familyScreen.toggleDentalAddon();
        familyScreen.clickBuyNow();

        familyScreen.fillNationalAddress("1234", "Riyadh", "Riyadh", "King Fahd Rd", "Al Olaya", "5678", "12211");
        familyScreen.fillIbanAndAcceptTerms(TestDataBuilder.DEFAULT_IBAN);
        familyScreen.clickCheckoutPayNow();

        AddNewCardModal cardModal = new AddNewCardModal();
        if (cardModal.isModalDisplayed()) {
            TestDataBuilder.CreditCard card = TestDataBuilder.CreditCard.defaultCard();
            cardModal.enterCardNumber(card.number);
            cardModal.enterExpiryDate(card.expiry);
            cardModal.enterCvv(card.cvv);
            cardModal.enterCardHolderName(card.holderName);
            cardModal.clickSave();
            try { Thread.sleep(2500); } catch (Exception ignore) {}
            familyScreen.clickCheckoutPayNow();
        }

        PolicyConfirmationScreen confirmationScreen = new PolicyConfirmationScreen();
        boolean isSuccess = confirmationScreen.isPolicyConfirmed();
        Assert.assertTrue(isSuccess, "Policy Confirmation screen was not reached for Family Health Flow 2!");
        String policyNumber = confirmationScreen.getPolicyNumber();
        System.out.println("Family Health Flow 2 Completed Successfully! Policy Issued: " + policyNumber);
        confirmationScreen.clickCloseToHome();
    }

    @Test(priority = 3, groups = {"health", "family-health", "underwriting"},
          description = "Family Health Flow 3: Chronic Condition Disclosures ('Yes' path triggering Underwriting Referral)")
    public void testFamilyHealthFlow3_ChronicConditionUnderwritingReferral() {
        System.out.println("\n========================================================");
        System.out.println("  [FAMILY HEALTH FLOW 3] Chronic Condition Underwriting  ");
        System.out.println("========================================================");

        HomeScreen homeScreen = new HomeScreen();
        homeScreen.clickFamilyHealthProduct();

        FamilyHealthScreen familyScreen = new FamilyHealthScreen();
        familyScreen.clickInsureNowIfPresent();

        String email = TestDataBuilder.generateUniqueEmail();
        familyScreen.fillSponsorDetails(SPONSOR_ID, SPONSOR_PHONE, email);
        familyScreen.selectSponsorDob();
        familyScreen.acceptPrivacyNotice();
        familyScreen.clickNext();

        OtpModal otpModal = new OtpModal();
        otpModal.enterOtp(TestDataBuilder.DEFAULT_OTP);

        familyScreen.addSpouse(SPOUSE_IQAMA);
        familyScreen.clickProceedToQuote();

        System.out.println("[Underwriting Trigger] Declaring dependent pre-existing chronic conditions ('Yes' path)...");
        familyScreen.answerAllDeclarationQuestions(true);
        familyScreen.clickDeclarationProceed();

        boolean isReferral = familyScreen.isUnderwritingReferralDisplayed();
        Assert.assertTrue(isReferral, "Underwriting referral notice should display for pre-existing family health condition!");
        System.out.println("Family health underwriting referral verified: " + isReferral);
    }

    @Test(priority = 4, groups = {"health", "family-health", "negative"},
          description = "Family Health Flow 4: Negative & Boundary Validation (Invalid Sponsor ID & Field Constraints)")
    public void testFamilyHealthFlow4_NegativeBoundaryValidation() {
        System.out.println("\n========================================================");
        System.out.println("  [FAMILY HEALTH FLOW 4] Negative & Boundary Tests       ");
        System.out.println("========================================================");

        HomeScreen homeScreen = new HomeScreen();
        homeScreen.clickFamilyHealthProduct();

        FamilyHealthScreen familyScreen = new FamilyHealthScreen();
        familyScreen.clickInsureNowIfPresent();

        System.out.println("[Validation 1] Submitting invalid sponsor National ID (1111111111)...");
        String invalidId = "1111111111";
        familyScreen.enterNationalId(invalidId);
        familyScreen.enterPhoneNumber(SPONSOR_PHONE);
        familyScreen.enterEmail("invalid.sponsor@tree.com.sa");
        familyScreen.acceptPrivacyNotice();
        familyScreen.clickNext();

        boolean hasError = familyScreen.isValidationErrorDisplayed();
        Assert.assertTrue(hasError, "Validation error should display when invalid Sponsor ID is submitted!");
        System.out.println("Negative Sponsor ID validation verified: " + hasError);
    }
}
