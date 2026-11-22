package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AddNewCardModal;
import pages.HomeInsuranceScreen;
import pages.HomeScreen;
import pages.OtpModal;
import pages.PolicyConfirmationScreen;
import utils.PerformanceBenchmark;
import utils.TestDataBuilder;

public class HomeInsuranceTest extends BaseTest {

    @Test(priority = 1, groups = {"home", "e2e", "happy-path"}, description = "Home Insurance Flow 1: Villa Comprehensive Owner Happy Path")
    public void testHomeInsuranceFlow1_HappyPath() {
        System.out.println("\n=== [HOME INSURANCE FLOW 1] Villa Comprehensive Owner ===");

        HomeScreen homeScreen = new HomeScreen();
        homeScreen.clickHomeInsuranceProduct();

        HomeInsuranceScreen homeInsScreen = new HomeInsuranceScreen();
        homeInsScreen.clickInsureNowIfPresent();

        String applicantId = TestDataBuilder.DEFAULT_SELLER_ID; // 1313424273
        String applicantPhone = TestDataBuilder.DEFAULT_PHONE;   // 500421222
        String applicantEmail = TestDataBuilder.generateUniqueEmail();

        PerformanceBenchmark.startStage("Home_Form_Fill");
        homeInsScreen.fillPolicyholderDetails(applicantId, applicantPhone, applicantEmail);
        homeInsScreen.acceptPrivacyNotice();
        homeInsScreen.clickNext();

        OtpModal otpModal = new OtpModal();
        otpModal.enterOtp(TestDataBuilder.DEFAULT_OTP);

        // Plan Selection / Quote Rating (Home Insurance displays quote first)
        PerformanceBenchmark.startStage("Home_Quote_Rating");
        homeInsScreen.waitForPlanSelectionScreen();
        PerformanceBenchmark.stopStage("Home_Quote_Rating", PerformanceBenchmark.SLA_QUOTE_RATING);

        PerformanceBenchmark.startStage("Home_Plan_Selection");
        homeInsScreen.clickBuyNow();
        PerformanceBenchmark.stopStage("Home_Plan_Selection", PerformanceBenchmark.SLA_ADDON_RERATE);

        // About Your Home & Underwriting Declarations (clean eligibility = true)
        homeInsScreen.fillAboutYourHome(applicantEmail, true);
        PerformanceBenchmark.stopStage("Home_Form_Fill", PerformanceBenchmark.SLA_FORM_INPUT);

        // National Address & Checkout
        PerformanceBenchmark.startStage("Home_Checkout_Payment");
        completeCheckoutAndPayment(homeInsScreen, "Home Insurance Flow 1");
        PerformanceBenchmark.stopStage("Home_Checkout_Payment", PerformanceBenchmark.SLA_CHECKOUT_PREP + PerformanceBenchmark.SLA_3DS_PAYMENT);
    }

    private void completeCheckoutAndPayment(HomeInsuranceScreen homeInsScreen, String flowLabel) {
        homeInsScreen.fillNationalAddress("1234", "Riyadh", "Riyadh", "King Fahd Rd", "Al Olaya", "5678", "12211");
        pages.CheckoutScreen checkoutScreen = new pages.CheckoutScreen();
        Assert.assertTrue(checkoutScreen.waitForPageToLoad(),
                "Checkout screen did not load for " + flowLabel + "!");
        homeInsScreen.fillIbanAndAcceptTerms(TestDataBuilder.DEFAULT_IBAN);
        homeInsScreen.clickCheckoutPayNow();

        AddNewCardModal cardModal = new AddNewCardModal();
        Assert.assertTrue(cardModal.isModalDisplayed(),
                "Add New Card bottom sheet should appear for " + flowLabel + "!");
        System.out.println("AddNewCardModal displayed, entering payment card details...");
        TestDataBuilder.CreditCard card = TestDataBuilder.CreditCard.defaultCard();
        cardModal.enterCardNumber(card.number);
        cardModal.enterExpiryDate(card.expiry);
        cardModal.enterCvv(card.cvv);
        cardModal.enterCardHolderName(card.holderName);
        cardModal.clickSave();
        try { Thread.sleep(2500); } catch (Exception ignore) {}
        new pages.CheckoutScreen().clickPayNow();

        PolicyConfirmationScreen confirmationScreen = new PolicyConfirmationScreen();
        boolean isSuccess = confirmationScreen.isPolicyConfirmed();
        Assert.assertTrue(isSuccess, "Policy Confirmation screen was not reached for " + flowLabel + "!");
        System.out.println("=== " + flowLabel + " Completed Successfully! ===");
    }

    @Test(priority = 2, groups = {"home", "variant"}, description = "Home Insurance Flow 2: Tenant Contents-Only Insurance")
    public void testHomeInsuranceFlow2_TenantContentsOnly() {
        System.out.println("\n=== [HOME INSURANCE FLOW 2] Tenant Contents-Only ===");

        HomeScreen homeScreen = new HomeScreen();
        homeScreen.clickHomeInsuranceProduct();

        HomeInsuranceScreen homeInsScreen = new HomeInsuranceScreen();
        homeInsScreen.clickInsureNowIfPresent();

        String applicantId = TestDataBuilder.DEFAULT_SELLER_ID;
        String applicantPhone = TestDataBuilder.DEFAULT_PHONE;
        String applicantEmail = TestDataBuilder.generateUniqueEmail();

        homeInsScreen.fillPolicyholderDetails(applicantId, applicantPhone, applicantEmail);
        homeInsScreen.acceptPrivacyNotice();
        homeInsScreen.clickNext();

        OtpModal otpModal = new OtpModal();
        otpModal.enterOtp(TestDataBuilder.DEFAULT_OTP);

        homeInsScreen.waitForPlanSelectionScreen();
        homeInsScreen.selectCoverType("Contents");
        boolean isLoaded = homeInsScreen.isPlanSelectionLoaded();
        Assert.assertTrue(isLoaded, "Home insurance plan selection screen did not load for Tenant Contents-Only flow!");
        homeInsScreen.clickBuyNow();

        // About Your Home declarations with clean answers
        homeInsScreen.fillAboutYourHome(applicantEmail, true);
        completeCheckoutAndPayment(homeInsScreen, "Home Insurance Flow 2 Tenant Contents-Only");
    }

    @Test(priority = 3, groups = {"home", "underwriting"}, description = "Home Insurance Flow 3: Underwriting Referral on Prior Claim History")
    public void testHomeInsuranceFlow3_PriorClaimHistoryReferral() {
        System.out.println("\n=== [HOME INSURANCE FLOW 3] Underwriting Claim History Referral ===");

        HomeScreen homeScreen = new HomeScreen();
        homeScreen.clickHomeInsuranceProduct();

        HomeInsuranceScreen homeInsScreen = new HomeInsuranceScreen();
        homeInsScreen.clickInsureNowIfPresent();

        String applicantId = TestDataBuilder.DEFAULT_SELLER_ID;
        String applicantPhone = TestDataBuilder.DEFAULT_PHONE;
        String applicantEmail = TestDataBuilder.generateUniqueEmail();

        homeInsScreen.fillPolicyholderDetails(applicantId, applicantPhone, applicantEmail);
        homeInsScreen.acceptPrivacyNotice();
        homeInsScreen.clickNext();

        OtpModal otpModal = new OtpModal();
        otpModal.enterOtp(TestDataBuilder.DEFAULT_OTP);

        homeInsScreen.waitForPlanSelectionScreen();
        homeInsScreen.clickBuyNow();

        // Answer 'Yes' to prior loss/damage during last 3 years (cleanEligibility = false)
        homeInsScreen.fillAboutYourHome(applicantEmail, false);

        System.out.println("Checking for referral modal or condition handling...");
        boolean referralShown = homeInsScreen.isReferralModalDisplayed();
        Assert.assertTrue(referralShown, "Underwriting referral dialog should display when prior claim history is declared!");
        System.out.println("Referral / Underwriting Dialog Displayed: " + referralShown);
    }

    @Test(priority = 4, groups = {"home", "negative"}, description = "Home Insurance Flow 4: Form Validation Check (Missing Required DOB)")
    public void testHomeInsuranceFlow4_MissingDobValidation() {
        System.out.println("\n=== [HOME INSURANCE FLOW 4] Form Validation (Missing DOB) ===");

        HomeScreen homeScreen = new HomeScreen();
        homeScreen.clickHomeInsuranceProduct();

        HomeInsuranceScreen homeInsScreen = new HomeInsuranceScreen();
        homeInsScreen.clickInsureNowIfPresent();

        // Fill ID and Phone without selecting DOB
        String applicantId = TestDataBuilder.DEFAULT_SELLER_ID;
        String applicantPhone = TestDataBuilder.DEFAULT_PHONE;
        homeInsScreen.enterNationalId(applicantId);
        homeInsScreen.enterPhoneNumber(applicantPhone);

        homeInsScreen.acceptPrivacyNotice();
        homeInsScreen.clickNext();

        // Verify user stays on "About you" page due to required field validation
        OtpModal otpModal = new OtpModal();
        Assert.assertFalse(otpModal.isModalDisplayed(), "OTP modal should NOT open when required DOB is missing!");
        boolean isBlocked = homeInsScreen.isValidationErrorDisplayed() || homeInsScreen.isAboutYouScreenDisplayed();
        Assert.assertTrue(isBlocked, "Form validation should prevent advancing when required DOB is missing!");
        System.out.println("Verifying required field validation prevents advancing: " + isBlocked);
    }

    @Test(priority = 5, groups = {"home", "valuables", "variant"}, description = "Home Insurance Flow 5: Owner-Occupier Villa with High-Value Valuables Add-on")
    public void testHomeInsuranceFlow5_OwnerOccupierWithHighValueValuables() {
        System.out.println("\n=== [HOME INSURANCE FLOW 5] Owner-Occupier with High-Value Valuables ===");

        HomeScreen homeScreen = new HomeScreen();
        homeScreen.clickHomeInsuranceProduct();

        HomeInsuranceScreen homeInsScreen = new HomeInsuranceScreen();
        homeInsScreen.clickInsureNowIfPresent();

        String applicantId = TestDataBuilder.DEFAULT_SELLER_ID;
        String applicantPhone = TestDataBuilder.DEFAULT_PHONE;
        String applicantEmail = TestDataBuilder.generateUniqueEmail();

        homeInsScreen.fillPolicyholderDetails(applicantId, applicantPhone, applicantEmail);
        homeInsScreen.acceptPrivacyNotice();
        homeInsScreen.clickNext();

        OtpModal otpModal = new OtpModal();
        otpModal.enterOtp(TestDataBuilder.DEFAULT_OTP);

        homeInsScreen.waitForPlanSelectionScreen();
        homeInsScreen.selectCoverType("Building & Contents");
        homeInsScreen.enterHighValueItems("Gold Watch", "25000");
        homeInsScreen.clickBuyNow();

        homeInsScreen.fillAboutYourHome(applicantEmail, true);
        completeCheckoutAndPayment(homeInsScreen, "Home Insurance Flow 5 Owner-Occupier with High-Value Valuables");
    }
}
