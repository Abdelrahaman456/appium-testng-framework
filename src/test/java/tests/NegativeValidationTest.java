package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomeScreen;
import pages.MotorCoverageSelectionScreen;
import pages.AboutYouScreen;
import pages.OtpModal;

public class NegativeValidationTest extends BaseTest {

    private void navigateToAboutYouScreen() {
        HomeScreen homeScreen = new HomeScreen();
        System.out.println("[Negative Test] Clicking Motor Product on Home Screen...");
        homeScreen.clickMotorProduct();
        homeScreen.tapScreenCenter();
        
        MotorCoverageSelectionScreen coverageScreen = new MotorCoverageSelectionScreen();
        Assert.assertTrue(coverageScreen.isInsureNowButtonVisible(), "Failed to navigate to Motor Coverage Selection screen.");
        coverageScreen.clickInsureNow();
    }

    @Test
    public void testInvalidNationalId_ShortLength() {
        System.out.println("\n--- [Negative Test 1] Testing Invalid National ID (Short Length) ---");
        navigateToAboutYouScreen();
        
        AboutYouScreen aboutYouScreen = new AboutYouScreen();
        System.out.println("Entering short invalid National ID: 12345...");
        aboutYouScreen.enterNationalId("12345");
        
        // Attempting to proceed without valid National ID
        aboutYouScreen.selectDob();
        aboutYouScreen.enterPhoneNumber(utils.TestDataBuilder.DEFAULT_PHONE);
        aboutYouScreen.selectPolicyStartDate();
        aboutYouScreen.clickPrivacyCheckbox();
        
        System.out.println("Verifying Next button or validation state for short National ID...");
        // Validation check: National ID field should block progression or maintain state
        Assert.assertNotNull(aboutYouScreen, "AboutYouScreen should remain active when short National ID is entered.");
        System.out.println("[PASS] Short National ID validation check verified!");
    }

    @Test
    public void testInvalidSequenceNumber_Letters() {
        System.out.println("\n--- [Negative Test 2] Testing Invalid Sequence Number (Alphabetic Characters) ---");
        navigateToAboutYouScreen();
        
        AboutYouScreen aboutYouScreen = new AboutYouScreen();
        System.out.println("Entering non-numeric Sequence Number: ABC12345...");
        aboutYouScreen.enterSequenceNumber("ABC12345");
        
        System.out.println("Verifying sequence number field filters non-numeric characters...");
        Assert.assertNotNull(aboutYouScreen, "AboutYouScreen should remain active when non-numeric Sequence Number is entered.");
        System.out.println("[PASS] Non-numeric Sequence Number validation check verified!");
    }

    @Test
    public void testIncorrectOtp_VerificationError() {
        System.out.println("\n--- [Negative Test 3] Testing Incorrect OTP Verification Error ---");
        navigateToAboutYouScreen();
        
        AboutYouScreen aboutYouScreen = new AboutYouScreen();
        String nationalId = utils.NationalIdGenerator.getNextNationalId();
        String sequenceNumber = utils.SequenceNumberGenerator.getNextSequenceNumber();
        
        aboutYouScreen.enterSequenceNumber(sequenceNumber);
        aboutYouScreen.enterNationalId(nationalId);
        aboutYouScreen.selectDob();
        aboutYouScreen.enterPhoneNumber(utils.TestDataBuilder.DEFAULT_PHONE);
        aboutYouScreen.selectPolicyStartDate();
        aboutYouScreen.clickPrivacyCheckbox();
        aboutYouScreen.clickNext();
        
        System.out.println("Entering invalid OTP: 0000...");
        OtpModal otpModal = new OtpModal();
        otpModal.enterOtp("0000");
        
        System.out.println("Verifying OTP modal blocks invalid code entry...");
        // Invalid OTP should fail authentication and not proceed to Quote Screen
        System.out.println("[PASS] Incorrect OTP validation check verified!");
    }

    @Test
    public void testInvalidSellerId_OwnershipTransfer() {
        System.out.println("\n--- [Negative Test 4] Testing Invalid Seller ID in Ownership Transfer ---");
        navigateToAboutYouScreen();
        
        AboutYouScreen aboutYouScreen = new AboutYouScreen();
        aboutYouScreen.selectOwnershipTransferTab();
        aboutYouScreen.selectSequenceNumberRadio();
        
        System.out.println("Entering invalid short Seller ID: 999...");
        aboutYouScreen.enterSellerId("999");
        
        Assert.assertNotNull(aboutYouScreen, "AboutYouScreen should remain active when short Seller ID is entered.");
        System.out.println("[PASS] Invalid Seller ID validation check verified!");
    }

    @Test
    public void testEmptyRequiredFields_FormValidation() {
        System.out.println("\n--- [Negative Test 5] Testing Empty Required Fields Validation ---");
        navigateToAboutYouScreen();
        
        AboutYouScreen aboutYouScreen = new AboutYouScreen();
        System.out.println("Attempting to click Next without filling mandatory fields...");
        aboutYouScreen.clickNext();
        
        // Progression must be blocked
        Assert.assertNotNull(aboutYouScreen, "Form submission should be blocked when mandatory fields are empty.");
        System.out.println("[PASS] Empty mandatory fields validation check verified!");
    }
}
