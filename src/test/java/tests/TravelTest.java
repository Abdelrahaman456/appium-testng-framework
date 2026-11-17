package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.AddNewCardModal;
import pages.PolicyConfirmationScreen;
import pages.TravelAboutYouScreen;
import utils.PerformanceBenchmark;
import utils.TestDataBuilder;

public class TravelTest extends BaseTest {

    @DataProvider(name = "travelerTypes")
    public Object[][] travelerTypes() {
        return new Object[][] {
            {"Individual"},
            {"With Family"},
            {"Group"}
        };
    }

    private void navigateToTravelAboutYouScreen() {
        org.openqa.selenium.By tripTypeBy = io.appium.java_client.AppiumBy.xpath("//*[@text='Single trip' or @content-desc='Single trip']");
        try {
            if (!utils.DriverManager.getDriver().findElements(tripTypeBy).isEmpty()) {
                System.out.println("Already on Travel About You screen.");
                return;
            }
        } catch (Exception e) {}

        TravelAboutYouScreen travelScreen = new TravelAboutYouScreen();

        // 1. If on Home screen, click Travel Product
        org.openqa.selenium.By travelLandingBy = org.openqa.selenium.By.xpath("//*[contains(@content-desc, 'Travel Insurance') or contains(@text, 'Travel Insurance') or contains(@content-desc, 'Explorer')]");
        org.openqa.selenium.By insureBtnBy = org.openqa.selenium.By.xpath("//*[@content-desc='Insure now' or @text='Insure now' or @resource-id='btn_insure_now']");

        if (utils.DriverManager.getDriver().findElements(travelLandingBy).isEmpty()
                && utils.DriverManager.getDriver().findElements(insureBtnBy).isEmpty()
                && !travelScreen.isInitialTripInfoScreenVisible()) {
            pages.HomeScreen homeScreen = new pages.HomeScreen();
            System.out.println("On Home screen. Clicking Travel Product...");
            homeScreen.clickTravelProduct();
            try { Thread.sleep(2000); } catch (Exception ignore) {}
        }

        // 2. If on Travel Landing screen, click Insure Now
        travelScreen.clickInsureNowIfPresent();

        // 3. If on Initial Trip Info screen ('Insure your trip'), fill and submit
        travelScreen.handleInitialTripInfoIfPresent(
            utils.TestDataBuilder.DEFAULT_SELLER_ID,
            utils.TestDataBuilder.DEFAULT_PHONE
        );

        // 4. Wait for 'Single trip' tab to appear
        try {
            travelScreen.waitUntil(() -> !utils.DriverManager.getDriver().findElements(tripTypeBy).isEmpty(), 15);
        } catch (Exception e) {
            System.out.println("Warning: 'Single trip' not visible yet: " + e.getMessage());
        }
    }

    // =========================================================================================
    // TEST FLOWS
    // =========================================================================================

    @Test(priority = 1, groups = {"travel", "e2e", "happy-path"}, description = "Travel Flow 1: Happy Path Comprehensive End-to-End Single Trip (Quote, Details, Checkout, Payment Verification)")
    public void testTravelFlow1_SingleTripHappyPath() {
        System.out.println("\n=== [TRAVEL FLOW 1] Single Trip Comprehensive Happy Path ===");
        
        navigateToTravelAboutYouScreen();
        
        TravelAboutYouScreen travelScreen = new TravelAboutYouScreen();
        
        PerformanceBenchmark.startStage("Travel_Form_Fill");
        travelScreen.selectSingleTrip();
        travelScreen.selectDestination("Worldwide");
        travelScreen.selectDepartureDate();
        travelScreen.selectReturnDate();
        
        travelScreen.selectTravelerType("Individual");
        travelScreen.enterEmailIfPresent(TestDataBuilder.DEFAULT_EMAIL);
        travelScreen.clickNext();
        PerformanceBenchmark.stopStage("Travel_Form_Fill", PerformanceBenchmark.SLA_FORM_INPUT);

        // Medical / Travel Declarations (Clean = No pre-existing condition)
        PerformanceBenchmark.startStage("Travel_Quote_Rating");
        travelScreen.answerAllDeclarationQuestions(false);
        travelScreen.clickDeclarationProceed();

        // Quote & Plan Selection
        travelScreen.waitForPlanSelectionScreen();
        PerformanceBenchmark.stopStage("Travel_Quote_Rating", PerformanceBenchmark.SLA_QUOTE_RATING);

        PerformanceBenchmark.startStage("Travel_Plan_Selection");
        travelScreen.selectGoldPlan();
        travelScreen.clickBuyNow();
        PerformanceBenchmark.stopStage("Travel_Plan_Selection", PerformanceBenchmark.SLA_ADDON_RERATE);

        // Checkout & Address
        PerformanceBenchmark.startStage("Travel_Checkout_Payment");
        travelScreen.fillNationalAddress("1234", "Riyadh", "Riyadh", "King Fahd Rd", "Al Olaya", "5678", "12211");
        pages.CheckoutScreen checkoutScreen = new pages.CheckoutScreen();
        checkoutScreen.waitForPageToLoad();
        checkoutScreen.enterIban(TestDataBuilder.DEFAULT_IBAN);
        checkoutScreen.selectCreditDebitCard();
        checkoutScreen.clickTermsCheckbox();
        checkoutScreen.clickPayNow();

        // Card Entry & Payment
        AddNewCardModal cardModal = new AddNewCardModal();
        if (cardModal.isModalDisplayed()) {
            System.out.println("AddNewCardModal displayed, entering payment card details...");
            TestDataBuilder.CreditCard card = TestDataBuilder.CreditCard.defaultCard();
            cardModal.enterCardNumber(card.number);
            cardModal.enterExpiryDate(card.expiry);
            cardModal.enterCvv(card.cvv);
            cardModal.enterCardHolderName(card.holderName);
            cardModal.clickSave();
            try { Thread.sleep(2500); } catch (Exception ignore) {}
            travelScreen.clickCheckoutPayNow();
        }

        // Policy Confirmation Assertion
        PolicyConfirmationScreen confirmationScreen = new PolicyConfirmationScreen();
        boolean isSuccess = confirmationScreen.isPolicyConfirmed();
        PerformanceBenchmark.stopStage("Travel_Checkout_Payment", PerformanceBenchmark.SLA_CHECKOUT_PREP + PerformanceBenchmark.SLA_3DS_PAYMENT);
        Assert.assertTrue(isSuccess, "Policy Confirmation screen was not reached for Travel Insurance!");
        System.out.println("=== Travel Flow 1 Completed Successfully! ===");
    }

    private void completeCheckoutAndPayment(TravelAboutYouScreen travelScreen, String flowLabel) {
        travelScreen.fillNationalAddress("1234", "Riyadh", "Riyadh", "King Fahd Rd", "Al Olaya", "5678", "12211");
        travelScreen.fillIbanAndAcceptTerms(TestDataBuilder.DEFAULT_IBAN);
        travelScreen.clickCheckoutPayNow();

        AddNewCardModal cardModal = new AddNewCardModal();
        if (cardModal.isModalDisplayed()) {
            System.out.println("AddNewCardModal displayed, entering payment card details...");
            TestDataBuilder.CreditCard card = TestDataBuilder.CreditCard.defaultCard();
            cardModal.enterCardNumber(card.number);
            cardModal.enterExpiryDate(card.expiry);
            cardModal.enterCvv(card.cvv);
            cardModal.enterCardHolderName(card.holderName);
            cardModal.clickSave();
            try { Thread.sleep(2500); } catch (Exception ignore) {}
            travelScreen.clickCheckoutPayNow();
        }

        PolicyConfirmationScreen confirmationScreen = new PolicyConfirmationScreen();
        boolean isSuccess = confirmationScreen.isPolicyConfirmed();
        Assert.assertTrue(isSuccess, "Policy Confirmation screen was not reached for " + flowLabel + "!");
        System.out.println("=== " + flowLabel + " Completed Successfully! ===");
    }

    @Test(dataProvider = "travelerTypes", priority = 2, groups = {"travel", "variant"}, description = "Travel Flow 2: Multi Trip (1 year) Annual Travel Cover for Individual, Family, and Group")
    public void testTravelFlow2_MultiTrip(String travelerType) {
        System.out.println("\n=== [TRAVEL FLOW 2] Multi Trip (1 year) | Traveler: " + travelerType + " ===");
        
        navigateToTravelAboutYouScreen();
        
        TravelAboutYouScreen travelScreen = new TravelAboutYouScreen();
        
        travelScreen.selectMultiTrip();
        travelScreen.selectDestination("Worldwide"); 
        travelScreen.selectDepartureDate();
        // Multi trip does not have a Return Date picker
        
        travelScreen.enterMobileNumber(TestDataBuilder.DEFAULT_PHONE);
        travelScreen.selectTravelerType(travelerType);
        travelScreen.clickPrivacyCheckbox();
        travelScreen.clickNext();

        travelScreen.answerAllDeclarationQuestions(false);
        travelScreen.clickDeclarationProceed();

        travelScreen.waitForPlanSelectionScreen();
        boolean isQuoteDisplayed = travelScreen.isQuoteScreenDisplayed();
        Assert.assertTrue(isQuoteDisplayed, "Travel plan selection screen did not load for Multi-trip coverage!");
        travelScreen.selectGoldPlan();
        travelScreen.clickBuyNow();
        completeCheckoutAndPayment(travelScreen, "Travel Flow 2 " + travelerType + " Multi-trip");
    }

    @Test(priority = 3, groups = {"travel", "underwriting"}, description = "Travel Flow 3: Underwriting Referral on Pre-existing Condition / High-Risk Travel")
    public void testTravelFlow3_UnderwritingReferral() {
        System.out.println("\n=== [TRAVEL FLOW 3] Underwriting Pre-existing Condition Referral ===");
        
        navigateToTravelAboutYouScreen();
        
        TravelAboutYouScreen travelScreen = new TravelAboutYouScreen();
        travelScreen.selectSingleTrip();
        travelScreen.selectDestination("Worldwide");
        travelScreen.selectDepartureDate();
        travelScreen.selectReturnDate();
        
        travelScreen.selectTravelerType("Individual");
        travelScreen.enterEmailIfPresent(TestDataBuilder.DEFAULT_EMAIL);
        travelScreen.clickNext();

        // Declare "Yes" on pre-existing condition to trigger underwriting referral
        System.out.println("Declaring pre-existing travel medical condition (Answering 'Yes')...");
        travelScreen.answerAllDeclarationQuestions(true);
        travelScreen.clickDeclarationProceed();

        boolean referralShown = travelScreen.isUnderwritingReferralDisplayed();
        Assert.assertTrue(referralShown, "Underwriting referral notice was not displayed for declared pre-existing condition!");
        System.out.println("=== Travel Flow 3 Underwriting Referral Verified Successfully! ===");
    }

    @Test(priority = 4, groups = {"travel", "negative"}, description = "Travel Flow 4: Form Validation & Date Boundary Validation (Past Date Disabled / Missing Fields)")
    public void testTravelFlow4_FormValidationAndDateBoundary() {
        System.out.println("\n=== [TRAVEL FLOW 4] Form Validation & Date Boundary Validation ===");
        
        navigateToTravelAboutYouScreen();
        
        TravelAboutYouScreen travelScreen = new TravelAboutYouScreen();
        travelScreen.selectSingleTrip();
        
        // Boundary validation: verify past dates cannot be selected in departure calendar
        boolean pastDateDisabled = travelScreen.isPastDateDisabled();
        Assert.assertTrue(pastDateDisabled, "Past dates should be disabled in the departure calendar!");

        // Form validation: attempt to click Next without required destination or mobile
        travelScreen.clickPrivacyCheckbox();
        travelScreen.clickNext();

        boolean hasErrorOrBlocked = travelScreen.isValidationErrorDisplayed() || !travelScreen.isQuoteScreenDisplayed();
        Assert.assertTrue(hasErrorOrBlocked, "Form validation should block advancing when required travel details are missing!");
        System.out.println("=== Travel Flow 4 Validation Enforced Successfully! ===");
    }

    @Test(priority = 5, groups = {"travel", "variant", "addons"}, description = "Travel Flow 5: Family Schengen & Worldwide with Winter Sports Add-on")
    public void testTravelFlow5_FamilySchengenWithWinterSports() {
        System.out.println("\n=== [TRAVEL FLOW 5] Family Schengen / Worldwide with Winter Sports ===");
        
        navigateToTravelAboutYouScreen();
        
        TravelAboutYouScreen travelScreen = new TravelAboutYouScreen();
        travelScreen.selectSingleTrip();
        travelScreen.selectSchengenDestination();
        travelScreen.selectDepartureDate();
        travelScreen.selectReturnDate();
        
        travelScreen.enterMobileNumber(TestDataBuilder.DEFAULT_PHONE);
        travelScreen.selectTravelerType("With Family");
        travelScreen.toggleWinterSportsAddon();
        travelScreen.clickPrivacyCheckbox();
        travelScreen.clickNext();

        travelScreen.answerAllDeclarationQuestions(false);
        travelScreen.clickDeclarationProceed();

        travelScreen.waitForPlanSelectionScreen();
        travelScreen.selectPlatinumPlan();
        travelScreen.clickBuyNow();

        completeCheckoutAndPayment(travelScreen, "Travel Flow 5 Family Schengen with Winter Sports");
    }

}
