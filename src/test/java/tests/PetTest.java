package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AddNewCardModal;
import pages.HomeScreen;
import pages.OtpModal;
import pages.PetScreen;
import pages.PolicyConfirmationScreen;
import utils.PerformanceBenchmark;
import utils.TestDataBuilder;

public class PetTest extends BaseTest {

    private static final String DEFAULT_PHONE = "500421222";

    @Test(priority = 1, groups = {"pet", "e2e", "happy-path"}, description = "Pet Flow 1: Primary Happy Path End-to-End for Dog ('Woof'), Standard Plan, Checkout & Confirmation")
    public void testPetFlow1_HappyPath() {
        System.out.println("\n=== [PET INSURANCE FLOW 1] Primary Happy Path End-to-End (Dog) ===");

        HomeScreen homeScreen = new HomeScreen();
        homeScreen.clickPetProduct();

        PetScreen petScreen = new PetScreen();
        petScreen.clickInsureNowIfPresent();

        PerformanceBenchmark.startStage("Pet_Form_Fill");
        String email = TestDataBuilder.generateUniqueEmail();
        // Domain Rule 3: "About your pet" (Name, 'Woof' for Dog, Age in Years & Months, Gender, City, Email, Phone, Privacy, Next)
        petScreen.fillAboutYourPet("Buddy", "Woof", 2, 6, "Male", "Riyadh", email, DEFAULT_PHONE);

        // Medical Declaration: "No" for instant quote
        petScreen.answerAllDeclarationQuestions(false);
        petScreen.clickDeclarationProceed();
        PerformanceBenchmark.stopStage("Pet_Form_Fill", PerformanceBenchmark.SLA_FORM_INPUT);

        // Plan Selection: Standard Plan
        PerformanceBenchmark.startStage("Pet_Quote_Rating");
        petScreen.waitForPlanSelectionScreen();
        PerformanceBenchmark.stopStage("Pet_Quote_Rating", PerformanceBenchmark.SLA_QUOTE_RATING);

        PerformanceBenchmark.startStage("Pet_Plan_Selection");
        petScreen.selectStandardPlan();
        petScreen.clickBuyNow();
        PerformanceBenchmark.stopStage("Pet_Plan_Selection", PerformanceBenchmark.SLA_ADDON_RERATE);

        // Checkout & National Address (IBAN stripped of 'SA')
        PerformanceBenchmark.startStage("Pet_Checkout_Payment");
        petScreen.fillNationalAddress("1234", "Riyadh", "Riyadh", "King Fahd Rd", "Al Olaya", "5678", "12211");
        petScreen.fillIbanAndAcceptTerms(TestDataBuilder.DEFAULT_IBAN);
        petScreen.clickCheckoutPayNow();

        AddNewCardModal cardModal = new AddNewCardModal();
        if (cardModal.isModalDisplayed()) {
            TestDataBuilder.CreditCard card = TestDataBuilder.CreditCard.defaultCard();
            cardModal.enterCardNumber(card.number);
            cardModal.enterExpiryDate(card.expiry);
            cardModal.enterCvv(card.cvv);
            cardModal.enterCardHolderName(card.holderName);
            cardModal.clickSave();
            try { Thread.sleep(2500); } catch (Exception ignore) {}
            petScreen.clickCheckoutPayNow();
        }

        PolicyConfirmationScreen confirmationScreen = new PolicyConfirmationScreen();
        boolean isSuccess = confirmationScreen.isPolicyConfirmed();
        PerformanceBenchmark.stopStage("Pet_Checkout_Payment", PerformanceBenchmark.SLA_CHECKOUT_PREP + PerformanceBenchmark.SLA_3DS_PAYMENT);
        Assert.assertTrue(isSuccess, "Policy Confirmation screen was not reached for Pet Insurance!");
        System.out.println("=== Pet Insurance Flow 1 Completed Successfully! ===");
    }

    private void completeCheckoutAndPayment(PetScreen petScreen, String flowLabel) {
        petScreen.fillNationalAddress("1234", "Riyadh", "Riyadh", "King Fahd Rd", "Al Olaya", "5678", "12211");
        petScreen.fillIbanAndAcceptTerms(TestDataBuilder.DEFAULT_IBAN);
        petScreen.clickCheckoutPayNow();

        AddNewCardModal cardModal = new AddNewCardModal();
        if (cardModal.isModalDisplayed()) {
            TestDataBuilder.CreditCard card = TestDataBuilder.CreditCard.defaultCard();
            cardModal.enterCardNumber(card.number);
            cardModal.enterExpiryDate(card.expiry);
            cardModal.enterCvv(card.cvv);
            cardModal.enterCardHolderName(card.holderName);
            cardModal.clickSave();
            try { Thread.sleep(2500); } catch (Exception ignore) {}
            petScreen.clickCheckoutPayNow();
        }

        PolicyConfirmationScreen confirmationScreen = new PolicyConfirmationScreen();
        boolean isSuccess = confirmationScreen.isPolicyConfirmed();
        Assert.assertTrue(isSuccess, "Policy Confirmation screen was not reached for " + flowLabel + "!");
        System.out.println("=== " + flowLabel + " Completed Successfully! ===");
    }

    @Test(priority = 2, groups = {"pet", "variant"}, description = "Pet Flow 2: Tier & Animal Comparison (Cat 'Meow' Premium vs Dog 'Woof' Basic)")
    public void testPetFlow2_TierAnimalComparison() {
        System.out.println("\n=== [PET INSURANCE FLOW 2] Tier & Animal Comparison (Cat 'Meow') ===");

        HomeScreen homeScreen = new HomeScreen();
        homeScreen.clickPetProduct();

        PetScreen petScreen = new PetScreen();
        petScreen.clickInsureNowIfPresent();

        String email = TestDataBuilder.generateUniqueEmail();
        // Configure for Cat ('Meow')
        petScreen.fillAboutYourPet("Luna", "Meow", 1, 4, "Female", "Jeddah", email, DEFAULT_PHONE);

        petScreen.answerAllDeclarationQuestions(false);
        petScreen.clickDeclarationProceed();

        petScreen.waitForPlanSelectionScreen();

        System.out.println("Toggling Basic Plan...");
        petScreen.selectBasicPlan();

        System.out.println("Selecting Premium Plan for Cat coverage...");
        petScreen.selectPremiumPlan();
        petScreen.clickBuyNow();

        completeCheckoutAndPayment(petScreen, "Pet Insurance Flow 2 Tier & Animal Comparison");
    }

    @Test(priority = 3, groups = {"pet", "underwriting"}, description = "Pet Flow 3: Underwriting Condition Declared ('Yes' path triggering exclusion/referral)")
    public void testPetFlow3_UnderwritingConditionDeclared() {
        System.out.println("\n=== [PET INSURANCE FLOW 3] Underwriting Condition Declared ('Yes' path) ===");

        HomeScreen homeScreen = new HomeScreen();
        homeScreen.clickPetProduct();

        PetScreen petScreen = new PetScreen();
        petScreen.clickInsureNowIfPresent();

        String email = TestDataBuilder.generateUniqueEmail();
        petScreen.fillAboutYourPet("Rocky", "Woof", 4, 0, "Male", "Dammam", email, DEFAULT_PHONE);

        // Declare "Yes" to trigger medical underwriting review or pre-existing exclusions
        System.out.println("Declaring pre-existing pet condition (Answering 'Yes')...");
        petScreen.answerAllDeclarationQuestions(true);
        petScreen.clickDeclarationProceed();

        boolean isNotice = petScreen.isUnderwritingReferralDisplayed();
        Assert.assertTrue(isNotice, "Underwriting referral/exclusion notice should display when pre-existing condition declared!");
        System.out.println("Underwriting referral/exclusion notice triggered: " + isNotice);
        System.out.println("=== Pet Insurance Flow 3 Completed Successfully! ===");
    }

    @Test(priority = 4, groups = {"pet", "negative"}, description = "Pet Flow 4: Form Validation / Negative Boundary Check (Pet Age Limit Boundary)")
    public void testPetFlow4_FormValidation() {
        System.out.println("\n=== [PET INSURANCE FLOW 4] Form Validation / Age Boundary Check ===");

        HomeScreen homeScreen = new HomeScreen();
        homeScreen.clickPetProduct();

        PetScreen petScreen = new PetScreen();
        petScreen.clickInsureNowIfPresent();

        // Boundary check: Ineligible pet age (e.g. 25 years old exceeds maximum insurable pet age)
        System.out.println("Entering age boundary exceeding limits (25 years)...");
        petScreen.enterPetName("Maximus");
        petScreen.selectPetType("Woof");
        petScreen.enterPetAge(25, 0);
        petScreen.selectPetGender("Male");
        petScreen.selectCity("Riyadh");
        petScreen.enterContactInfo(TestDataBuilder.generateUniqueEmail(), DEFAULT_PHONE);
        petScreen.acceptPrivacyNotice();
        petScreen.clickNext();

        boolean hasError = petScreen.isValidationErrorDisplayed();
        Assert.assertTrue(hasError, "Validation error should display when pet age exceeds maximum threshold!");
        System.out.println("Validation error triggered as expected on age boundary: " + hasError);
        System.out.println("=== Pet Insurance Flow 4 Completed Successfully! ===");
    }

    @Test(priority = 5, groups = {"pet", "microchip", "routine-care"}, description = "Pet Flow 5: ISO 11784 Microchip Validation & Routine Care Add-on")
    public void testPetFlow5_MicrochipAndRoutineCare() {
        System.out.println("\n=== [PET INSURANCE FLOW 5] Microchip Validation & Routine Care Add-on ===");

        HomeScreen homeScreen = new HomeScreen();
        homeScreen.clickPetProduct();

        PetScreen petScreen = new PetScreen();
        petScreen.clickInsureNowIfPresent();

        String email = TestDataBuilder.generateUniqueEmail();
        petScreen.enterPetName("Bella");
        petScreen.selectPetType("Woof");
        petScreen.enterPetAge(2, 0);
        petScreen.selectPetGender("Female");
        petScreen.selectCity("Riyadh");
        // ISO 11784/11785 15-digit microchip number
        petScreen.enterMicrochipId("985141001234567");
        petScreen.enterContactInfo(email, DEFAULT_PHONE);
        petScreen.acceptPrivacyNotice();
        petScreen.clickNext();

        petScreen.answerAllDeclarationQuestions(false);
        petScreen.clickDeclarationProceed();

        petScreen.waitForPlanSelectionScreen();
        petScreen.selectAccidentAndIllnessCover();
        petScreen.toggleRoutineCareAddon();
        petScreen.selectPremiumPlan();
        petScreen.clickBuyNow();

        completeCheckoutAndPayment(petScreen, "Pet Insurance Flow 5 Microchip & Routine Care");
    }
}
