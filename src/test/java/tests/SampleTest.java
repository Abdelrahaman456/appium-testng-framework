package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomeScreen;
import pages.MotorCoverageSelectionScreen;
import pages.AboutYouScreen;
import pages.OtpModal;
import pages.QuoteScreen;
import pages.CheckoutScreen;
import pages.AddNewCardModal;
import pages.PolicyConfirmationScreen;
import pages.VehicleDetailsModal;

public class SampleTest extends BaseTest {

    private void navigateToCoverageScreen() {
        HomeScreen homeScreen = new HomeScreen();
        System.out.println("On Home Screen. Clicking on Motor Product...");
        homeScreen.clickMotorProduct();
        homeScreen.tapScreenCenter();
        
        MotorCoverageSelectionScreen coverageScreen = new MotorCoverageSelectionScreen();
        Assert.assertTrue(coverageScreen.isInsureNowButtonVisible(), "Failed to navigate to Motor Coverage Selection screen.");
    }

    private void fillRemainingFields(AboutYouScreen aboutYouScreen) {
        aboutYouScreen.selectDob();
        aboutYouScreen.enterPhoneNumber(utils.TestDataBuilder.DEFAULT_PHONE);
        aboutYouScreen.selectPolicyStartDate();
        aboutYouScreen.clickPrivacyCheckbox();
        aboutYouScreen.clickNext();
        
        System.out.println("Waiting 10 seconds for the OTP popup...");
        try { Thread.sleep(10000); } catch (InterruptedException e) {}
        
        System.out.println("Entering OTP...");
        OtpModal otpModal = new OtpModal();
        otpModal.enterOtp(utils.TestDataBuilder.DEFAULT_OTP);
        try { Thread.sleep(3000); } catch (InterruptedException e) {} // Wait for OTP to be processed
    }

    // =========================================================================================
    // ABSTRACTION HELPERS: These methods run a specific flow up to the Quote Screen
    // =========================================================================================

    private QuoteScreen reachQuoteScreenFlow1(String nationalId, String sequenceNumber) {
        System.out.println("\n--- Navigating to Quote Screen via FLOW 1 (New Insurance + Sequence) ---");
        System.out.println("Using National ID: " + nationalId + " | Sequence Number: " + sequenceNumber);
        navigateToCoverageScreen();
        
        MotorCoverageSelectionScreen coverageScreen = new MotorCoverageSelectionScreen();
        coverageScreen.clickInsureNow();
        
        AboutYouScreen aboutYouScreen = new AboutYouScreen();
        aboutYouScreen.enterSequenceNumber(sequenceNumber);
        aboutYouScreen.enterNationalId(nationalId);
        fillRemainingFields(aboutYouScreen);

        QuoteScreen quoteScreen = new QuoteScreen();
        System.out.println("Waiting for Quote Screen to load...");
        Assert.assertTrue(quoteScreen.isPageLoaded(), "Quote screen did not display after OTP in Flow 1.");
        System.out.println("Quote screen displayed! Waiting 5 seconds...");
        try { Thread.sleep(5000); } catch (InterruptedException e) {}
        
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
        aboutYouScreen.enterCustomCard(utils.TestDataBuilder.DEFAULT_CUSTOM_CARD);
        aboutYouScreen.selectCarModelYear(utils.TestDataBuilder.DEFAULT_CAR_YEAR);
        aboutYouScreen.enterNationalId(nationalId);
        fillRemainingFields(aboutYouScreen);

        // Handle Vehicle Details modal ("We need more details about your vehicle") for Flow 2
        VehicleDetailsModal vehicleDetailsModal = new VehicleDetailsModal();
        vehicleDetailsModal.handleVehicleDetailsIfPresent();

        QuoteScreen quoteScreen = new QuoteScreen();
        Assert.assertTrue(quoteScreen.isPageLoaded(), "Quote screen did not display after OTP in Flow 2.");
        try { Thread.sleep(5000); } catch (InterruptedException e) {}
        return quoteScreen;
    }

    private QuoteScreen reachQuoteScreenFlow3(String nationalId, String sequenceNumber) {
        System.out.println("\n--- Navigating to Quote Screen via FLOW 3 (Ownership Transfer + Sequence) ---");
        System.out.println("Using National ID: " + nationalId + " | Sequence Number: " + sequenceNumber);
        navigateToCoverageScreen();
        
        MotorCoverageSelectionScreen coverageScreen = new MotorCoverageSelectionScreen();
        coverageScreen.clickInsureNow();
        
        AboutYouScreen aboutYouScreen = new AboutYouScreen();
        aboutYouScreen.selectOwnershipTransferTab();
        aboutYouScreen.selectSequenceNumberRadio();
        aboutYouScreen.enterSequenceNumber(sequenceNumber);
        aboutYouScreen.enterNationalId(nationalId);
        aboutYouScreen.enterSellerId(utils.TestDataBuilder.DEFAULT_SELLER_ID);
        fillRemainingFields(aboutYouScreen);

        QuoteScreen quoteScreen = new QuoteScreen();
        Assert.assertTrue(quoteScreen.isPageLoaded(), "Quote screen did not display after OTP in Flow 3.");
        try { Thread.sleep(5000); } catch (InterruptedException e) {}
        return quoteScreen;
    }

    private QuoteScreen reachQuoteScreenFlow4(String nationalId) {
        System.out.println("\n--- Navigating to Quote Screen via FLOW 4 (Ownership Transfer + Custom Card) ---");
        System.out.println("Using National ID: " + nationalId);
        navigateToCoverageScreen();
        
        MotorCoverageSelectionScreen coverageScreen = new MotorCoverageSelectionScreen();
        coverageScreen.clickInsureNow();
        
        AboutYouScreen aboutYouScreen = new AboutYouScreen();
        aboutYouScreen.selectOwnershipTransferTab();
        aboutYouScreen.selectCustomCardRadio();
        aboutYouScreen.enterCustomCard(utils.TestDataBuilder.DEFAULT_CUSTOM_CARD);
        aboutYouScreen.selectCarModelYear(utils.TestDataBuilder.DEFAULT_CAR_YEAR);
        aboutYouScreen.enterNationalId(nationalId);
        aboutYouScreen.enterSellerId(utils.TestDataBuilder.DEFAULT_SELLER_ID);
        
        System.out.println("Scrolling down to reveal hidden fields...");
        aboutYouScreen.scrollDown(); 
        
        fillRemainingFields(aboutYouScreen);

        // Handle Vehicle Details modal ("We need more details about your vehicle") for Flow 4
        VehicleDetailsModal vehicleDetailsModal = new VehicleDetailsModal();
        vehicleDetailsModal.handleVehicleDetailsIfPresent();

        QuoteScreen quoteScreen = new QuoteScreen();
        Assert.assertTrue(quoteScreen.isPageLoaded(), "Quote screen did not display after OTP in Flow 4.");
        try { Thread.sleep(5000); } catch (InterruptedException e) {}
        return quoteScreen;
    }

    private void fillCheckoutAndPayment(QuoteScreen quoteScreen, String nationalId) {
        System.out.println("Clicking Buy Now...");
        quoteScreen.clickBuyNow();
        
        System.out.println("Handling potential Upsell popup...");
        quoteScreen.clickSkipUpsell();
        
        System.out.println("Waiting 5 seconds for Checkout Screen to load...");
        try { Thread.sleep(5000); } catch (Exception e) {}
        
        System.out.println("Auto-filling Checkout Screen details...");
        CheckoutScreen checkoutScreen = new CheckoutScreen();
        utils.TestDataBuilder.CustomerProfile profile = utils.TestDataBuilder.CustomerProfile.createDefault(nationalId);
        checkoutScreen.autoFillCheckout(profile);
        
        System.out.println("Waiting for Add New Card modal & Auto-filling Credit Card details...");
        AddNewCardModal cardModal = new AddNewCardModal();
        cardModal.autoFillCardDetails(utils.TestDataBuilder.CreditCard.defaultCard());
        
        System.out.println("Verifying Policy Confirmation Screen...");
        PolicyConfirmationScreen confirmationScreen = new PolicyConfirmationScreen();
        Assert.assertTrue(confirmationScreen.isPolicyConfirmed(), "Policy Confirmation screen ('You're ready to roll!') did not display after payment!");
        System.out.println("SUCCESS! Policy Confirmation Screen displayed! Policy purchase completed successfully.");
    }

    // =========================================================================================
    // ISOLATED TEST CASES
    // =========================================================================================

    // FLOW 1: NEW INSURANCE + SEQUENCE NUMBER
    
    @Test
    public void testFlow1_ComprehensiveCover() {
        String nationalId = utils.NationalIdGenerator.getNextNationalId();
        String sequenceNumber = utils.SequenceNumberGenerator.getNextSequenceNumber();
        QuoteScreen quoteScreen = reachQuoteScreenFlow1(nationalId, sequenceNumber);
        System.out.println("Executing Flow 1 -> Comprehensive Cover scenario...");
        
        // Comprehensive Cover is already open/selected by default when Quote Screen loads!
        // We do not click selectComprehensiveCover() because re-clicking it might collapse the card.
        fillCheckoutAndPayment(quoteScreen, nationalId);
        System.out.println("Flow 1 Comprehensive Cover Completed Successfully!");
    }

    @Test
    public void testFlow1_SmartCover() {
        String nationalId = utils.NationalIdGenerator.getNextNationalId();
        String sequenceNumber = utils.SequenceNumberGenerator.getNextSequenceNumber();
        QuoteScreen quoteScreen = reachQuoteScreenFlow1(nationalId, sequenceNumber);
        System.out.println("Executing Flow 1 -> Smart Cover scenario...");
        
        System.out.println("Clicking Smart Cover card...");
        quoteScreen.selectSmartCover();
        try { Thread.sleep(2000); } catch (Exception e) {} // Wait for card to expand
        
        fillCheckoutAndPayment(quoteScreen, nationalId);
        System.out.println("Flow 1 Smart Cover Completed Successfully!");
    }

    @Test
    public void testFlow1_SaverCover() {
        String nationalId = utils.NationalIdGenerator.getNextNationalId();
        String sequenceNumber = utils.SequenceNumberGenerator.getNextSequenceNumber();
        QuoteScreen quoteScreen = reachQuoteScreenFlow1(nationalId, sequenceNumber);
        System.out.println("Executing Flow 1 -> Saver Cover scenario...");
        
        System.out.println("Clicking Saver Cover card...");
        quoteScreen.selectSaverCover();
        try { Thread.sleep(2000); } catch (Exception e) {} // Wait for card to expand
        
        fillCheckoutAndPayment(quoteScreen, nationalId);
        System.out.println("Flow 1 Saver Cover Completed Successfully!");
    }

    // FLOW 2: NEW INSURANCE + CUSTOM CARD
    
    @Test
    public void testFlow2_ComprehensiveCover() {
        String nationalId = utils.NationalIdGenerator.getNextNationalId();
        QuoteScreen quoteScreen = reachQuoteScreenFlow2(nationalId);
        System.out.println("Executing Flow 2 -> Comprehensive Cover scenario...");
        
        fillCheckoutAndPayment(quoteScreen, nationalId);
        System.out.println("Flow 2 Comprehensive Cover Completed Successfully!");
    }

    @Test
    public void testFlow2_SmartCover() {
        String nationalId = utils.NationalIdGenerator.getNextNationalId();
        QuoteScreen quoteScreen = reachQuoteScreenFlow2(nationalId);
        System.out.println("Executing Flow 2 -> Smart Cover scenario...");
        
        System.out.println("Clicking Smart Cover card...");
        quoteScreen.selectSmartCover();
        try { Thread.sleep(2000); } catch (Exception e) {} // Wait for card to expand
        
        fillCheckoutAndPayment(quoteScreen, nationalId);
        System.out.println("Flow 2 Smart Cover Completed Successfully!");
    }

    @Test
    public void testFlow2_SaverCover() {
        String nationalId = utils.NationalIdGenerator.getNextNationalId();
        QuoteScreen quoteScreen = reachQuoteScreenFlow2(nationalId);
        System.out.println("Executing Flow 2 -> Saver Cover scenario...");
        
        System.out.println("Clicking Saver Cover card...");
        quoteScreen.selectSaverCover();
        try { Thread.sleep(2000); } catch (Exception e) {} // Wait for card to expand
        
        fillCheckoutAndPayment(quoteScreen, nationalId);
        System.out.println("Flow 2 Saver Cover Completed Successfully!");
    }

    // FLOW 3: OWNERSHIP TRANSFER + SEQUENCE NUMBER

    @Test
    public void testFlow3_ComprehensiveCover() {
        String nationalId = utils.NationalIdGenerator.getNextNationalId();
        String sequenceNumber = utils.SequenceNumberGenerator.getNextSequenceNumber();
        QuoteScreen quoteScreen = reachQuoteScreenFlow3(nationalId, sequenceNumber);
        System.out.println("Executing Flow 3 -> Comprehensive Cover scenario...");
        
        fillCheckoutAndPayment(quoteScreen, nationalId);
        System.out.println("Flow 3 Comprehensive Cover Completed Successfully!");
    }

    @Test
    public void testFlow3_SmartCover() {
        String nationalId = utils.NationalIdGenerator.getNextNationalId();
        String sequenceNumber = utils.SequenceNumberGenerator.getNextSequenceNumber();
        QuoteScreen quoteScreen = reachQuoteScreenFlow3(nationalId, sequenceNumber);
        System.out.println("Executing Flow 3 -> Smart Cover scenario...");
        
        System.out.println("Clicking Smart Cover card...");
        quoteScreen.selectSmartCover();
        try { Thread.sleep(2000); } catch (Exception e) {} // Wait for card to expand
        
        fillCheckoutAndPayment(quoteScreen, nationalId);
        System.out.println("Flow 3 Smart Cover Completed Successfully!");
    }

    @Test
    public void testFlow3_SaverCover() {
        String nationalId = utils.NationalIdGenerator.getNextNationalId();
        String sequenceNumber = utils.SequenceNumberGenerator.getNextSequenceNumber();
        QuoteScreen quoteScreen = reachQuoteScreenFlow3(nationalId, sequenceNumber);
        System.out.println("Executing Flow 3 -> Saver Cover scenario...");
        
        System.out.println("Clicking Saver Cover card...");
        quoteScreen.selectSaverCover();
        try { Thread.sleep(2000); } catch (Exception e) {} // Wait for card to expand
        
        fillCheckoutAndPayment(quoteScreen, nationalId);
        System.out.println("Flow 3 Saver Cover Completed Successfully!");
    }

    // FLOW 4: OWNERSHIP TRANSFER + CUSTOM CARD

    @Test
    public void testFlow4_ComprehensiveCover() {
        String nationalId = utils.NationalIdGenerator.getNextNationalId();
        QuoteScreen quoteScreen = reachQuoteScreenFlow4(nationalId);
        System.out.println("Executing Flow 4 -> Comprehensive Cover scenario...");
        
        fillCheckoutAndPayment(quoteScreen, nationalId);
        System.out.println("Flow 4 Comprehensive Cover Completed Successfully!");
    }

    @Test
    public void testFlow4_SmartCover() {
        String nationalId = utils.NationalIdGenerator.getNextNationalId();
        QuoteScreen quoteScreen = reachQuoteScreenFlow4(nationalId);
        System.out.println("Executing Flow 4 -> Smart Cover scenario...");
        
        System.out.println("Clicking Smart Cover card...");
        quoteScreen.selectSmartCover();
        try { Thread.sleep(2000); } catch (Exception e) {} // Wait for card to expand
        
        fillCheckoutAndPayment(quoteScreen, nationalId);
        System.out.println("Flow 4 Smart Cover Completed Successfully!");
    }

    @Test
    public void testFlow4_SaverCover() {
        String nationalId = utils.NationalIdGenerator.getNextNationalId();
        QuoteScreen quoteScreen = reachQuoteScreenFlow4(nationalId);
        System.out.println("Executing Flow 4 -> Saver Cover scenario...");
        
        System.out.println("Clicking Saver Cover card...");
        quoteScreen.selectSaverCover();
        try { Thread.sleep(2000); } catch (Exception e) {} // Wait for card to expand
        
        fillCheckoutAndPayment(quoteScreen, nationalId);
        System.out.println("Flow 4 Saver Cover Completed Successfully!");
    }
}
}
