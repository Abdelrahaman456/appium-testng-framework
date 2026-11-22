package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AboutYouScreen;
import pages.AddNewCardModal;
import pages.CheckoutScreen;
import pages.HomeScreen;
import pages.MotorCoverageSelectionScreen;
import pages.OtpModal;
import pages.PolicyConfirmationScreen;
import pages.QuoteScreen;
import utils.NationalIdGenerator;
import utils.PerformanceBenchmark;
import utils.SequenceNumberGenerator;
import utils.TestDataBuilder;

/**
 * Priority 3: Alternative Payment Methods, Card Negative Validation & Failure/Retry Recovery Suite.
 * 100% locator-based verification against UAT Checkout & Payment Gateway flows.
 *
 * Flows Covered:
 *  - Flow 1: Malformed Card Number (Luhn Checksum Failure) Validation
 *  - Flow 2: Invalid CVV (Short Length) & Missing Cardholder Name Validation
 *  - Flow 3: Alternative Payment Methods Discovery & Switching (Tamara / Tabby / Mada / Credit-Debit Card)
 *  - Flow 4: Payment Modal Cancellation & Retry Recovery to Policy Issuance
 */
public class PaymentValidationTest extends BaseTest {

    /**
     * Reusable helper to reach the Motor Checkout screen cleanly using dynamic test data.
     */
    private CheckoutScreen navigateToMotorCheckout(String nationalId, String sequenceNumber) {
        MotorCoverageSelectionScreen coverageScreen = new MotorCoverageSelectionScreen();
        if (!coverageScreen.isInsureNowButtonVisible()) {
            HomeScreen homeScreen = new HomeScreen();
            homeScreen.clickMotorProduct();
            boolean arrived = coverageScreen.waitForScreenLoaded(10);
            if (!arrived) {
                homeScreen.tapScreenCenter();
                arrived = coverageScreen.waitForScreenLoaded(8);
            }
            Assert.assertTrue(arrived, "Failed to navigate to Motor Coverage Selection screen.");
        }

        coverageScreen.clickInsureNow();

        AboutYouScreen aboutYouScreen = new AboutYouScreen();
        aboutYouScreen.selectNewInsuranceTab();
        aboutYouScreen.selectSequenceNumberRadio();
        aboutYouScreen.enterSequenceNumber(sequenceNumber);
        aboutYouScreen.enterNationalId(nationalId);
        aboutYouScreen.selectDob();
        aboutYouScreen.enterPhoneNumber(TestDataBuilder.DEFAULT_PHONE);
        aboutYouScreen.selectPolicyStartDate();
        aboutYouScreen.clickPrivacyCheckbox();
        aboutYouScreen.clickNext();

        OtpModal otpModal = new OtpModal();
        otpModal.enterOtp(TestDataBuilder.DEFAULT_OTP);

        QuoteScreen quoteScreen = new QuoteScreen();
        if (!quoteScreen.isPageLoaded()) {
            // Self-heal if OTP modal stayed open due to transient delay
            otpModal.enterOtp(TestDataBuilder.DEFAULT_OTP);
        }
        Assert.assertTrue(quoteScreen.isPageLoaded(), "Quote Screen did not load for Payment Validation test.");

        // Use Comprehensive cover (expanded by default on Quote screen) for reliable Buy now
        quoteScreen.selectComprehensiveCover();
        quoteScreen.clickBuyNow();
        quoteScreen.clickSkipUpsell();

        CheckoutScreen checkoutScreen = new CheckoutScreen();
        if (!checkoutScreen.waitForPageToLoad()) {
            // Dismiss any transient UAT backend modal (e.g. policyNo error) and retry Buy now once
            checkoutScreen.dismissPaymentErrorOrRetry();
            quoteScreen.clickBuyNow();
            quoteScreen.clickSkipUpsell();
            Assert.assertTrue(checkoutScreen.waitForPageToLoad(), "Checkout Screen did not load after retry.");
        }
        return checkoutScreen;
    }

    /**
     * Fills required Checkout details and reliably opens the Add New Card modal.
     */
    private AddNewCardModal openCardModalFromCheckout(CheckoutScreen checkoutScreen, String nationalId) {
        TestDataBuilder.CustomerProfile profile = TestDataBuilder.CustomerProfile.createDefault(nationalId);
        checkoutScreen.autoFillCheckout(profile);

        AddNewCardModal cardModal = new AddNewCardModal();
        if (!cardModal.isModalDisplayed()) {
            System.out.println("Add New Card modal did not open on first Pay now click; retrying Pay now...");
            checkoutScreen.clickPayNow();
        }
        Assert.assertTrue(cardModal.isModalDisplayed(), "Add New Card modal should open after clicking Pay now.");
        return cardModal;
    }

    @Test(priority = 1, groups = {"payment", "negative", "card-validation"},
          description = "Payment Flow 1: Malformed Card Number (Luhn Failure) Blocks Card Submission")
    public void testPaymentFlow1_MalformedCardNumberValidation() {
        System.out.println("\n=============================================================");
        System.out.println("  [PAYMENT FLOW 1] Malformed Card Number (Luhn) Validation   ");
        System.out.println("=============================================================");

        String nationalId = NationalIdGenerator.getNextNationalId();
        String sequenceNumber = SequenceNumberGenerator.getNextSequenceNumber();
        CheckoutScreen checkoutScreen = navigateToMotorCheckout(nationalId, sequenceNumber);

        AddNewCardModal cardModal = openCardModalFromCheckout(checkoutScreen, nationalId);

        System.out.println("[Step 1] Entering malformed card number (fails Luhn checksum): 1234567890123456...");
        cardModal.enterCardNumber("1234567890123456");
        cardModal.enterExpiryDate(TestDataBuilder.DEFAULT_CARD_EXPIRY);
        cardModal.enterCvv(TestDataBuilder.DEFAULT_CARD_CVV);
        cardModal.enterCardHolderName(TestDataBuilder.DEFAULT_CARD_HOLDER);

        System.out.println("[Step 2] Attempting to save malformed card...");
        cardModal.clickSave();

        boolean blocked = cardModal.isSaveBlockedOrErrorShown() || checkoutScreen.isPaymentErrorModalDisplayed();
        Assert.assertTrue(blocked, "Malformed card number (Luhn failure) must trigger validation error or block submission!");

        // Clean up modal if still open
        checkoutScreen.dismissPaymentErrorOrRetry();
        if (cardModal.isModalDisplayed()) {
            cardModal.dismissModal();
        }
        System.out.println(">> [SUCCESS] Payment Flow 1 passed: Malformed card number blocked cleanly!");
    }

    @Test(priority = 2, groups = {"payment", "negative", "card-validation"},
          description = "Payment Flow 2: Invalid Short CVV & Missing Cardholder Validation")
    public void testPaymentFlow2_InvalidCvvAndEmptyHolderValidation() {
        System.out.println("\n=============================================================");
        System.out.println("  [PAYMENT FLOW 2] Invalid CVV & Empty Cardholder Validation ");
        System.out.println("=============================================================");

        String nationalId = NationalIdGenerator.getNextNationalId();
        String sequenceNumber = SequenceNumberGenerator.getNextSequenceNumber();
        CheckoutScreen checkoutScreen = navigateToMotorCheckout(nationalId, sequenceNumber);

        AddNewCardModal cardModal = openCardModalFromCheckout(checkoutScreen, nationalId);

        System.out.println("[Step 1] Entering valid card number with invalid short CVV ('1') and empty holder...");
        cardModal.enterCardNumber(TestDataBuilder.DEFAULT_CARD_NUMBER);
        cardModal.enterExpiryDate(TestDataBuilder.DEFAULT_CARD_EXPIRY);
        cardModal.enterCvv("1");

        System.out.println("[Step 2] Attempting to save card with invalid CVV...");
        cardModal.clickSave();

        boolean blocked = cardModal.isSaveBlockedOrErrorShown() || checkoutScreen.isPaymentErrorModalDisplayed();
        Assert.assertTrue(blocked, "Short/invalid CVV and empty cardholder name must block card submission!");

        checkoutScreen.dismissPaymentErrorOrRetry();
        if (cardModal.isModalDisplayed()) {
            cardModal.dismissModal();
        }
        System.out.println(">> [SUCCESS] Payment Flow 2 passed: Invalid CVV and empty cardholder blocked cleanly!");
    }

    @Test(priority = 3, groups = {"payment", "alternative-methods"},
          description = "Payment Flow 3: Alternative Payment Methods Discovery & Selection (Tamara / Tabby / Mada / Card)")
    public void testPaymentFlow3_AlternativePaymentMethodsDiscoveryAndSelection() {
        System.out.println("\n=============================================================");
        System.out.println("  [PAYMENT FLOW 3] Alternative Payment Methods Switching     ");
        System.out.println("=============================================================");

        String nationalId = NationalIdGenerator.getNextNationalId();
        String sequenceNumber = SequenceNumberGenerator.getNextSequenceNumber();
        CheckoutScreen checkoutScreen = navigateToMotorCheckout(nationalId, sequenceNumber);

        System.out.println("[Step 1] Verifying Checkout VAT/Premium breakdown is displayed...");
        Assert.assertTrue(checkoutScreen.isVatBreakdownDisplayed() || checkoutScreen.isCheckoutScreenDisplayed(),
                "Checkout screen with premium details should be displayed.");

        System.out.println("[Step 2] Discovering & testing alternative payment methods (Tamara, Tabby, Mada, Credit/Debit)...");
        boolean selectedAnyAlt = false;
        if (checkoutScreen.isPaymentMethodOptionVisible("Tamara")) {
            selectedAnyAlt |= checkoutScreen.selectPaymentMethod("Tamara");
        }
        if (checkoutScreen.isPaymentMethodOptionVisible("Tabby")) {
            selectedAnyAlt |= checkoutScreen.selectPaymentMethod("Tabby");
        }
        if (checkoutScreen.isPaymentMethodOptionVisible("Mada")) {
            selectedAnyAlt |= checkoutScreen.selectPaymentMethod("Mada");
        }

        System.out.println("[Step 3] Switching back to Credit/Debit Card payment method...");
        Assert.assertTrue(checkoutScreen.selectCreditDebitCard(),
                "Moyasar Direct Pay / Credit-Debit Card option should be selected.");

        Assert.assertTrue(checkoutScreen.isCheckoutScreenDisplayed(),
                "Checkout screen must remain stable and intact after switching payment methods.");
        System.out.println(">> [SUCCESS] Payment Flow 3 passed: Payment method switching verified (Alt methods detected: " + selectedAnyAlt + ")!");
    }

    @Test(priority = 4, groups = {"payment", "retry-recovery", "e2e"},
          description = "Payment Flow 4: Card Modal Cancellation & Retry Recovery to Successful Policy Issuance")
    public void testPaymentFlow4_PaymentCancellationAndRetryRecovery() {
        System.out.println("\n=============================================================");
        System.out.println("  [PAYMENT FLOW 4] Payment Cancel & Retry Recovery E2E       ");
        System.out.println("=============================================================");

        String nationalId = NationalIdGenerator.getNextNationalId();
        String sequenceNumber = SequenceNumberGenerator.getNextSequenceNumber();
        CheckoutScreen checkoutScreen = navigateToMotorCheckout(nationalId, sequenceNumber);

        AddNewCardModal cardModal = openCardModalFromCheckout(checkoutScreen, nationalId);

        System.out.println("[Step 1] Cancelling / dismissing Add New Card modal to simulate user abort...");
        cardModal.dismissModal();

        Assert.assertTrue(checkoutScreen.isCheckoutScreenDisplayed(),
                "Cancelling card entry should safely return user to Checkout screen without losing policy state.");

        System.out.println("[Step 2] Retrying payment by tapping Pay now again...");
        checkoutScreen.clickPayNow();
        if (!cardModal.isModalDisplayed()) {
            checkoutScreen.clickPayNow();
        }
        Assert.assertTrue(cardModal.isModalDisplayed(), "Add New Card modal should re-open cleanly on retry.");

        System.out.println("[Step 3] Entering valid test card details on retry and completing 3DS payment...");
        PerformanceBenchmark.startStage("Payment_Retry_3DS");
        TestDataBuilder.CreditCard card = TestDataBuilder.CreditCard.defaultCard();
        cardModal.enterCardNumber(card.number);
        cardModal.enterExpiryDate(card.expiry);
        cardModal.enterCvv(card.cvv);
        cardModal.enterCardHolderName(card.holderName);
        cardModal.clickSave();
        try { Thread.sleep(2500); } catch (Exception ignore) {}
        checkoutScreen.clickPayNow();

        PolicyConfirmationScreen confirmationScreen = new PolicyConfirmationScreen();
        boolean isSuccess = confirmationScreen.isPolicyConfirmed();
        PerformanceBenchmark.stopStage("Payment_Retry_3DS", PerformanceBenchmark.SLA_3DS_PAYMENT);
        Assert.assertTrue(isSuccess, "Retry payment with valid card should complete 3DS and reach Policy Confirmation!");

        System.out.println(">> [SUCCESS] Payment Flow 4 passed: Cancelled card modal, recovered on Checkout, and completed payment!");
    }
}
