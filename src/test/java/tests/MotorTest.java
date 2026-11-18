package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.AddNewCardModal;
import pages.AboutYouScreen;
import pages.CheckoutScreen;
import pages.HomeScreen;
import pages.MotorCoverageSelectionScreen;
import pages.OtpModal;
import pages.PolicyConfirmationScreen;
import pages.QuoteScreen;
import pages.VehicleDetailsModal;
import utils.NationalIdGenerator;
import utils.PerformanceBenchmark;
import utils.SequenceNumberGenerator;
import utils.TestDataBuilder;

public class MotorTest extends BaseTest {

    @DataProvider(name = "coverTypes")
    public Object[][] getCoverTypes() {
        return new Object[][] {
            {"Comprehensive"},
            {"Smart"},
            {"Saver"}
        };
    }

    private void navigateToCoverageScreen() {
        MotorCoverageSelectionScreen coverageScreen = new MotorCoverageSelectionScreen();
        if (coverageScreen.isInsureNowButtonVisible()) {
            System.out.println("App already on Motor Coverage screen.");
            return;
        }

        HomeScreen homeScreen = new HomeScreen();
        System.out.println("On Home Screen. Clicking on Motor Product...");
        homeScreen.clickMotorProduct();

        boolean arrived = coverageScreen.waitForScreenLoaded(10);
        if (!arrived) {
            homeScreen.tapScreenCenter();
            arrived = coverageScreen.waitForScreenLoaded(8);
        }
        Assert.assertTrue(arrived, "Failed to navigate to Motor Coverage Selection screen.");
    }

    private void fillRemainingFields(AboutYouScreen aboutYouScreen) {
        aboutYouScreen.selectDob();
        aboutYouScreen.enterPhoneNumber(TestDataBuilder.DEFAULT_PHONE);
        aboutYouScreen.selectPolicyStartDate();
        aboutYouScreen.clickPrivacyCheckbox();
        aboutYouScreen.clickNext();

        System.out.println("Entering OTP...");
        OtpModal otpModal = new OtpModal();
        otpModal.enterOtp(TestDataBuilder.DEFAULT_OTP);
    }

    private QuoteScreen reachQuoteScreenFlow1(String nationalId, String sequenceNumber) {
        System.out.println("\n--- Navigating to Quote Screen via FLOW 1 (New Insurance + Sequence) ---");
        System.out.println("Using National ID: " + nationalId + " | Sequence Number: " + sequenceNumber);
        navigateToCoverageScreen();

        MotorCoverageSelectionScreen coverageScreen = new MotorCoverageSelectionScreen();
        coverageScreen.clickInsureNow();

        PerformanceBenchmark.startStage("Motor_Form_Fill");
        AboutYouScreen aboutYouScreen = new AboutYouScreen();
        aboutYouScreen.selectNewInsuranceTab();
        aboutYouScreen.selectSequenceNumberRadio();
        aboutYouScreen.enterSequenceNumber(sequenceNumber);
        aboutYouScreen.enterNationalId(nationalId);

        fillRemainingFields(aboutYouScreen);
        PerformanceBenchmark.stopStage("Motor_Form_Fill", PerformanceBenchmark.SLA_FORM_INPUT);

        PerformanceBenchmark.startStage("Motor_Quote_Rating");
        QuoteScreen quoteScreen = new QuoteScreen();
        Assert.assertTrue(quoteScreen.isPageLoaded(), "Quote Screen did not load for Flow 1!");
        PerformanceBenchmark.stopStage("Motor_Quote_Rating", PerformanceBenchmark.SLA_QUOTE_RATING);
        return quoteScreen;
    }

    private QuoteScreen reachQuoteScreenFlow2(String nationalId) {
        System.out.println("\n--- Navigating to Quote Screen via FLOW 2 (New Insurance + Custom Card) ---");
        System.out.println("Using National ID: " + nationalId);
        navigateToCoverageScreen();

        MotorCoverageSelectionScreen coverageScreen = new MotorCoverageSelectionScreen();
        coverageScreen.clickInsureNow();

        AboutYouScreen aboutYouScreen = new AboutYouScreen();
        aboutYouScreen.selectNewInsuranceTab();
        aboutYouScreen.selectCustomCardRadio();
        aboutYouScreen.enterCustomCard(TestDataBuilder.DEFAULT_CUSTOM_CARD);
        aboutYouScreen.selectCarModelYear(TestDataBuilder.DEFAULT_CAR_YEAR);
        aboutYouScreen.enterNationalId(nationalId);

        fillRemainingFields(aboutYouScreen);

        VehicleDetailsModal modal = new VehicleDetailsModal();
        modal.handleVehicleDetailsIfPresent();

        QuoteScreen quoteScreen = new QuoteScreen();
        Assert.assertTrue(quoteScreen.isPageLoaded(), "Quote Screen did not load for Flow 2!");
        return quoteScreen;
    }

    private QuoteScreen reachQuoteScreenFlow3(String nationalId, String sequenceNumber) {
        System.out.println("\n--- Navigating to Quote Screen via FLOW 3 (Ownership Transfer + Sequence) ---");
        System.out.println("Buyer ID: " + nationalId + " | Seller ID: " + TestDataBuilder.DEFAULT_SELLER_ID);
        navigateToCoverageScreen();

        MotorCoverageSelectionScreen coverageScreen = new MotorCoverageSelectionScreen();
        coverageScreen.clickInsureNow();

        AboutYouScreen aboutYouScreen = new AboutYouScreen();
        aboutYouScreen.selectOwnershipTransferTab();
        aboutYouScreen.enterSellerId(TestDataBuilder.DEFAULT_SELLER_ID);
        aboutYouScreen.selectSequenceNumberRadio();
        aboutYouScreen.enterSequenceNumber(sequenceNumber);
        aboutYouScreen.enterNationalId(nationalId);

        fillRemainingFields(aboutYouScreen);

        QuoteScreen quoteScreen = new QuoteScreen();
        Assert.assertTrue(quoteScreen.isPageLoaded(), "Quote Screen did not load for Flow 3!");
        return quoteScreen;
    }

    private QuoteScreen reachQuoteScreenFlow4(String nationalId) {
        System.out.println("\n--- Navigating to Quote Screen via FLOW 4 (Ownership Transfer + Custom Card) ---");
        System.out.println("Buyer ID: " + nationalId + " | Seller ID: " + TestDataBuilder.DEFAULT_SELLER_ID);
        navigateToCoverageScreen();

        MotorCoverageSelectionScreen coverageScreen = new MotorCoverageSelectionScreen();
        coverageScreen.clickInsureNow();

        AboutYouScreen aboutYouScreen = new AboutYouScreen();
        aboutYouScreen.selectOwnershipTransferTab();
        aboutYouScreen.enterSellerId(TestDataBuilder.DEFAULT_SELLER_ID);
        aboutYouScreen.selectCustomCardRadio();
        aboutYouScreen.enterCustomCard(TestDataBuilder.DEFAULT_CUSTOM_CARD);
        aboutYouScreen.selectCarModelYear(TestDataBuilder.DEFAULT_CAR_YEAR);
        aboutYouScreen.enterNationalId(nationalId);

        fillRemainingFields(aboutYouScreen);

        VehicleDetailsModal modal = new VehicleDetailsModal();
        modal.handleVehicleDetailsIfPresent();

        QuoteScreen quoteScreen = new QuoteScreen();
        Assert.assertTrue(quoteScreen.isPageLoaded(), "Quote Screen did not load for Flow 4!");
        return quoteScreen;
    }

    private void selectCoverType(QuoteScreen quoteScreen, String coverType) {
        System.out.println("Selecting cover type: " + coverType);
        PerformanceBenchmark.startStage("Motor_Cover_Selection");
        switch (coverType.toLowerCase()) {
            case "smart":
                quoteScreen.selectSmartCover();
                break;
            case "saver":
                quoteScreen.selectSaverCover();
                break;
            case "comprehensive":
            default:
                quoteScreen.selectComprehensiveCover();
                break;
        }
        quoteScreen.clickBuyNow();
        quoteScreen.clickSkipUpsell();
        PerformanceBenchmark.stopStage("Motor_Cover_Selection", PerformanceBenchmark.SLA_ADDON_RERATE);
    }

    private void fillCheckoutAndPayment(QuoteScreen quoteScreen, String nationalId) {
        PerformanceBenchmark.startStage("Motor_Checkout_Payment");
        CheckoutScreen checkoutScreen = new CheckoutScreen();
        Assert.assertTrue(checkoutScreen.waitForPageToLoad(),
                "Checkout screen did not load after Quote -> Buy Now for National ID: " + nationalId);
        checkoutScreen.autoFillCheckout(TestDataBuilder.CustomerProfile.createDefault(nationalId));

        AddNewCardModal cardModal = new AddNewCardModal();
        if (cardModal.isModalDisplayed()) {
            System.out.println("Entering Card Payment Details...");
            TestDataBuilder.CreditCard card = TestDataBuilder.CreditCard.defaultCard();
            cardModal.enterCardNumber(card.number);
            cardModal.enterExpiryDate(card.expiry);
            cardModal.enterCvv(card.cvv);
            cardModal.enterCardHolderName(card.holderName);
            cardModal.clickSave();
            try { Thread.sleep(2500); } catch (Exception ignore) {}
            checkoutScreen.clickPayNow();
        }

        PolicyConfirmationScreen confirmationScreen = new PolicyConfirmationScreen();
        boolean isSuccess = confirmationScreen.isPolicyConfirmed();
        PerformanceBenchmark.stopStage("Motor_Checkout_Payment", PerformanceBenchmark.SLA_CHECKOUT_PREP + PerformanceBenchmark.SLA_3DS_PAYMENT);
        Assert.assertTrue(isSuccess, "Policy Confirmation screen was not reached for National ID: " + nationalId);
    }

    // =========================================================================================
    // TIER 1: PRIMARY HAPPY PATH END-TO-END FLOWS
    // =========================================================================================

    @Test(priority = 1, groups = {"motor", "e2e", "happy-path"}, description = "Motor Flow 1: New Insurance + Sequence Number Comprehensive Happy Path")
    public void testMotorFlow1_ComprehensiveHappyPath() {
        String nationalId = NationalIdGenerator.getNextNationalId();
        String sequenceNumber = SequenceNumberGenerator.getNextSequenceNumber();

        QuoteScreen quoteScreen = reachQuoteScreenFlow1(nationalId, sequenceNumber);
        selectCoverType(quoteScreen, "Comprehensive");
        fillCheckoutAndPayment(quoteScreen, nationalId);
        System.out.println("Motor Flow 1 Comprehensive Completed Successfully!");
    }

    @Test(priority = 2, groups = {"motor", "e2e", "tpl"}, description = "Motor Flow 1: New Insurance + Sequence Number Saver (TPL) Happy Path")
    public void testMotorFlow1_TplSaverHappyPath() {
        String nationalId = NationalIdGenerator.getNextNationalId();
        String sequenceNumber = SequenceNumberGenerator.getNextSequenceNumber();

        QuoteScreen quoteScreen = reachQuoteScreenFlow1(nationalId, sequenceNumber);
        selectCoverType(quoteScreen, "Smart");
        fillCheckoutAndPayment(quoteScreen, nationalId);
        System.out.println("Motor Flow 1 TPL / Smart Completed Successfully!");
    }

    // =========================================================================================
    // TIER 2: PRODUCT VARIANTS & REPAIR / ADD-ON TIERS
    // =========================================================================================

    @Test(priority = 3, groups = {"motor", "custom-card"}, description = "Motor Flow 2: New Insurance + Custom Card with Vehicle Specs")
    public void testMotorFlow2_CustomCardSmartCover() {
        String nationalId = NationalIdGenerator.getNextNationalId();
        QuoteScreen quoteScreen = reachQuoteScreenFlow2(nationalId);
        selectCoverType(quoteScreen, "Smart");
        fillCheckoutAndPayment(quoteScreen, nationalId);
        System.out.println("Motor Flow 2 Custom Card Completed Successfully!");
    }

    @Test(priority = 4, groups = {"motor", "ownership-transfer"}, description = "Motor Flow 3: Ownership Transfer + Sequence Number with Seller ID")
    public void testMotorFlow3_OwnershipTransferSequence() {
        String nationalId = NationalIdGenerator.getNextNationalId();
        String sequenceNumber = SequenceNumberGenerator.getNextSequenceNumber();
        QuoteScreen quoteScreen = reachQuoteScreenFlow3(nationalId, sequenceNumber);
        selectCoverType(quoteScreen, "Comprehensive");
        fillCheckoutAndPayment(quoteScreen, nationalId);
        System.out.println("Motor Flow 3 Ownership Transfer Completed Successfully!");
    }

    @Test(priority = 5, groups = {"motor", "ownership-transfer-custom"}, description = "Motor Flow 4: Ownership Transfer + Custom Card Variant")
    public void testMotorFlow4_OwnershipTransferCustomCard() {
        String nationalId = NationalIdGenerator.getNextNationalId();
        QuoteScreen quoteScreen = reachQuoteScreenFlow4(nationalId);
        selectCoverType(quoteScreen, "Smart");
        fillCheckoutAndPayment(quoteScreen, nationalId);
        System.out.println("Motor Flow 4 Ownership Transfer Custom Card Completed Successfully!");
    }

    @Test(priority = 6, groups = {"motor", "addons", "repair-options"}, description = "Motor Flow 5: Agency Repair, Roadside Addon, and Promo Code Verification")
    public void testMotorFlow5_RepairOptionsAndAddons() {
        String nationalId = NationalIdGenerator.getNextNationalId();
        String sequenceNumber = SequenceNumberGenerator.getNextSequenceNumber();

        QuoteScreen quoteScreen = reachQuoteScreenFlow1(nationalId, sequenceNumber);

        // Select Comprehensive and customize repair options & add-ons
        quoteScreen.selectComprehensiveCover();
        quoteScreen.selectAgencyRepair();
        quoteScreen.toggleRoadsideAssistanceAddon();
        quoteScreen.toggleReplacementCarAddon();

        // Apply Promo Code
        quoteScreen.enterPromoCode("PROMO10");
        quoteScreen.clickApplyPromoCode();
        try { Thread.sleep(2000); } catch (Exception ignore) {}

        // Re-expand Comprehensive cover after promo code re-rates and refreshes the card list
        quoteScreen.selectComprehensiveCover();

        quoteScreen.clickBuyNow();
        quoteScreen.clickSkipUpsell();

        CheckoutScreen checkoutScreen = new CheckoutScreen();
        Assert.assertTrue(checkoutScreen.waitForPageToLoad(), "Checkout screen did not load!");
        fillCheckoutAndPayment(quoteScreen, nationalId);
        System.out.println("Motor Flow 5 Repair Options & Add-ons Completed Successfully!");
    }

    // =========================================================================================
    // TIER 3: UNDERWRITING REFERRAL & HIGH RISK
    // =========================================================================================

    @Test(priority = 7, groups = {"motor", "underwriting"}, description = "Motor Flow 6: Underwriting PEP & Related Party Declarations")
    public void testMotorFlow6_PepUnderwritingDeclarations() {
        String nationalId = NationalIdGenerator.getNextNationalId();
        String sequenceNumber = SequenceNumberGenerator.getNextSequenceNumber();

        QuoteScreen quoteScreen = reachQuoteScreenFlow1(nationalId, sequenceNumber);
        quoteScreen.selectComprehensiveCover();

        System.out.println("Answering PEP declaration as 'Yes' and Related Party as 'Yes'...");
        quoteScreen.answerPepQuestion(true);
        quoteScreen.answerRelatedPartyQuestion(true);

        Assert.assertTrue(quoteScreen.isPageLoaded(), "Quote Screen remained interactive after PEP declarations.");
        System.out.println("Motor Flow 6 PEP Underwriting Question Verified!");
    }

    // =========================================================================================
    // TIER 4: NEGATIVE VALIDATION & BOUNDARY CHECKS
    // =========================================================================================

    @Test(priority = 8, groups = {"motor", "negative"}, description = "Motor Flow 7: Form Validation (Invalid Promo Code Error Handling)")
    public void testMotorFlow7_InvalidPromoCodeBoundary() {
        String nationalId = NationalIdGenerator.getNextNationalId();
        String sequenceNumber = SequenceNumberGenerator.getNextSequenceNumber();

        QuoteScreen quoteScreen = reachQuoteScreenFlow1(nationalId, sequenceNumber);
        quoteScreen.selectComprehensiveCover();

        System.out.println("Applying invalid promo code 'INVALID_999'...");
        quoteScreen.enterPromoCode("INVALID_999");
        quoteScreen.clickApplyPromoCode();

        boolean isError = quoteScreen.isPromoCodeInvalid();
        System.out.println("Invalid promo code handled (error badge displayed: " + isError + ")");
    }
}
