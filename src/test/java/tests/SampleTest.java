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

        MotorCoverageSelectionScreen coverageScreen = new MotorCoverageSelectionScreen();

        if (coverageScreen.isInsureNowButtonVisible()) {

            System.out.println("App already on Motor Coverage screen.");

            return;

        }



        HomeScreen homeScreen = new HomeScreen();

        System.out.println("On Home Screen. Clicking on Motor Product...");

        homeScreen.clickMotorProduct();

        

        boolean arrived = coverageScreen.isInsureNowButtonVisible();

        if (!arrived) {

            homeScreen.tapScreenCenter();

            arrived = coverageScreen.isInsureNowButtonVisible();

        }

        Assert.assertTrue(arrived, "Failed to navigate to Motor Coverage Selection screen.");

    }



    private void fillRemainingFields(AboutYouScreen aboutYouScreen) {







        aboutYouScreen.selectDob();







        aboutYouScreen.enterPhoneNumber(utils.TestDataBuilder.DEFAULT_PHONE);







        aboutYouScreen.selectPolicyStartDate();







        aboutYouScreen.clickPrivacyCheckbox();







        aboutYouScreen.clickNext();







        







        System.out.println("Entering OTP...");







        OtpModal otpModal = new OtpModal();







        otpModal.enterOtp(utils.TestDataBuilder.DEFAULT_OTP);







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







        System.out.println("Quote screen displayed!");







        try { Thread.sleep(1000); } catch (InterruptedException e) {}







        







        return quoteScreen;







    }







    private QuoteScreen reachQuoteScreenFlow2(String nationalId) {







        System.out.println("\n--- Navigating to Quote Screen via FLOW 2 (New Insurance + Custom Card) ---");







        System.out.println("Using National ID: " + nationalId);







        navigateToCoverageScreen();







        







        MotorCoverageSelectionScreen coverageScreen = new MotorCoverageSelectionScreen();







        coverageScreen.clickInsureNow();







        







        AboutYouScreen aboutYouScreen = new AboutYouScreen();







        // Removed selectNewInsuranceTab() because it is already default, and clicking it might trigger an async state reset that wipes the custom card click!







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







        try { Thread.sleep(1000); } catch (InterruptedException e) {}







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







        try { Thread.sleep(1000); } catch (InterruptedException e) {}







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







        try { Thread.sleep(1000); } catch (InterruptedException e) {}







        return quoteScreen;







    }







    private void fillCheckoutAndPayment(QuoteScreen quoteScreen, String nationalId) {







        System.out.println("Clicking Buy Now...");







        quoteScreen.clickBuyNow();







        







        System.out.println("Handling potential Upsell popup...");







        quoteScreen.clickSkipUpsell();







        







        System.out.println("Waiting for Checkout Screen to load...");







        try { Thread.sleep(1500); } catch (Exception e) {}







        







        System.out.println("Auto-filling Checkout Screen details...");







        CheckoutScreen checkoutScreen = new CheckoutScreen();







        utils.TestDataBuilder.CustomerProfile profile = utils.TestDataBuilder.CustomerProfile.createDefault(nationalId);







        checkoutScreen.autoFillCheckout(profile);







        







        System.out.println("Checking for Add New Card modal...");



        AddNewCardModal cardModal = new AddNewCardModal();



        if (cardModal.isModalDisplayed()) {



            System.out.println("AddNewCardModal detected, filling card details...");



            utils.TestDataBuilder.CreditCard card = utils.TestDataBuilder.CreditCard.defaultCard();



            



            System.out.println("Entering Card Number...");



            cardModal.enterCardNumber(card.number);



            try { Thread.sleep(300); } catch (Exception e) {}



            



            System.out.println("Entering Expiry Date...");



            cardModal.enterExpiryDate(card.expiry);



            try { Thread.sleep(300); } catch (Exception e) {}



            



            System.out.println("Entering CVV...");



            cardModal.enterCvv(card.cvv);



            try { Thread.sleep(300); } catch (Exception e) {}



            



            System.out.println("Entering Cardholder Name...");



            cardModal.enterCardHolderName(card.holderName);



            try { Thread.sleep(300); } catch (Exception e) {}



            



            System.out.println("Clicking Save...");



            cardModal.clickSave();
            try { Thread.sleep(2500); } catch (Exception ignore) {}
            System.out.println("Card saved. Submitting final Checkout Pay Now with saved card...");
            checkoutScreen.clickPayNow();



        } else {



            System.out.println("AddNewCardModal not displayed (e.g. BNPL selected or auto-processed). Proceeding to confirmation verification...");



        }







        







        System.out.println("Verifying Policy Confirmation Screen...");







        PolicyConfirmationScreen confirmationScreen = new PolicyConfirmationScreen();







        Assert.assertTrue(confirmationScreen.isPolicyConfirmed(), "Policy Confirmation screen ('You're ready to roll!') did not display after payment!");







        System.out.println("SUCCESS! Policy Confirmation Screen displayed! Policy purchase completed successfully.");







        System.out.println("Closing Policy Confirmation screen (✕) to return to Home Landing Page for next test case...");







        confirmationScreen.clickCloseToHome();







    }







    // =========================================================================================







    // ISOLATED TEST CASES







    // =========================================================================================







    @org.testng.annotations.DataProvider(name = "coverTypes")







    public Object[][] getCoverTypes() {







        return new Object[][] {







            {"Comprehensive"},







            {"Smart"},







            {"Saver"}







        };







    }







    private void selectCoverType(QuoteScreen quoteScreen, String coverType) {







        System.out.println("Executing scenario for -> " + coverType + " Cover...");







        if (coverType.equals("Smart")) {







            System.out.println("Clicking Smart Cover card...");







            quoteScreen.selectSmartCover();







            try { Thread.sleep(2000); } catch (Exception e) {}







        } else if (coverType.equals("Saver")) {







            System.out.println("Clicking Saver Cover card...");







            quoteScreen.selectSaverCover();







            try { Thread.sleep(2000); } catch (Exception e) {}







        }







        // Comprehensive is already selected by default







    }







    // FLOW 1: NEW INSURANCE + SEQUENCE NUMBER







    @Test(dataProvider = "coverTypes", priority = 1, groups = {"motor", "e2e", "happy-path"}, description = "Motor Flow 1: New Insurance + Sequence Number Comprehensive Happy Path")
    public void testFlow1(String coverType) {







        String nationalId = utils.NationalIdGenerator.getNextNationalId();







        String sequenceNumber = utils.SequenceNumberGenerator.getNextSequenceNumber();







        QuoteScreen quoteScreen = reachQuoteScreenFlow1(nationalId, sequenceNumber);







        







        selectCoverType(quoteScreen, coverType);







        fillCheckoutAndPayment(quoteScreen, nationalId);







        System.out.println("Flow 1 " + coverType + " Cover Completed Successfully!");







    }







    // FLOW 2: NEW INSURANCE + CUSTOM CARD







    @Test(dataProvider = "coverTypes", priority = 2, groups = {"motor", "custom-card"}, description = "Motor Flow 2: New Insurance + Custom Card with Vehicle Specs")
    public void testFlow2(String coverType) {







        String nationalId = utils.NationalIdGenerator.getNextNationalId();







        QuoteScreen quoteScreen = reachQuoteScreenFlow2(nationalId);







        







        selectCoverType(quoteScreen, coverType);







        fillCheckoutAndPayment(quoteScreen, nationalId);







        System.out.println("Flow 2 " + coverType + " Cover Completed Successfully!");







    }







    // FLOW 3: OWNERSHIP TRANSFER + SEQUENCE NUMBER







    @Test(dataProvider = "coverTypes", priority = 3, groups = {"motor", "ownership-transfer"}, description = "Motor Flow 3: Ownership Transfer + Sequence Number with Seller ID")
    public void testFlow3(String coverType) {







        String nationalId = utils.NationalIdGenerator.getNextNationalId();







        String sequenceNumber = utils.SequenceNumberGenerator.getNextSequenceNumber();







        QuoteScreen quoteScreen = reachQuoteScreenFlow3(nationalId, sequenceNumber);







        







        selectCoverType(quoteScreen, coverType);







        fillCheckoutAndPayment(quoteScreen, nationalId);







        System.out.println("Flow 3 " + coverType + " Cover Completed Successfully!");







    }







    // FLOW 4: OWNERSHIP TRANSFER + CUSTOM CARD







    @Test(dataProvider = "coverTypes", priority = 4, groups = {"motor", "ownership-transfer-custom"}, description = "Motor Flow 4: Ownership Transfer + Custom Card Variant")
    public void testFlow4(String coverType) {







        String nationalId = utils.NationalIdGenerator.getNextNationalId();







        QuoteScreen quoteScreen = reachQuoteScreenFlow4(nationalId);







        







        selectCoverType(quoteScreen, coverType);







        fillCheckoutAndPayment(quoteScreen, nationalId);







        System.out.println("Flow 4 " + coverType + " Cover Completed Successfully!");







    }







}







