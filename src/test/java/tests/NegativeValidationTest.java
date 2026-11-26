package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomeScreen;
import pages.MotorCoverageSelectionScreen;
import pages.AboutYouScreen;
import pages.OtpModal;
import pages.QuoteScreen;

public class NegativeValidationTest extends BaseTest {

    private void navigateToAboutYouScreen() {
        // 1. If already on About you screen, proceed directly
        try {
            if (!utils.DriverManager.getDriver().findElements(io.appium.java_client.AppiumBy.id("textfield_about_you_page_motor_sequence_number")).isEmpty()) {
                System.out.println("[Negative Test] App already on About You screen!");
                return;
            }
        } catch (Exception e) {}

        // 2. If on Motor Coverage screen, click Insure Now
        MotorCoverageSelectionScreen coverageScreen = new MotorCoverageSelectionScreen();
        if (coverageScreen.isInsureNowButtonVisible()) {
            System.out.println("[Negative Test] App already on Motor Coverage screen. Clicking Insure Now directly...");
            coverageScreen.clickInsureNow();
            return;
        }

        // 3. Otherwise navigate from HomeScreen
        HomeScreen homeScreen = new HomeScreen();
        System.out.println("[Negative Test] Clicking Motor Product on Home Screen...");
        homeScreen.clickMotorProduct();
        if (!coverageScreen.waitForScreenLoaded(10)) {
            homeScreen.tapScreenCenter();
            coverageScreen.waitForScreenLoaded(8);
        }

        Assert.assertTrue(coverageScreen.isInsureNowButtonVisible(), "Failed to navigate to Motor Coverage Selection screen.");
        coverageScreen.clickInsureNow();
    }

    @Test
    public void testInvalidNationalId_ShortLength() {
        System.out.println("\n--- [Negative Test 1] Testing Invalid National ID (Short Length) ---");
        navigateToAboutYouScreen();

        AboutYouScreen aboutYouScreen = new AboutYouScreen();
        aboutYouScreen.selectNewInsuranceTab();
        aboutYouScreen.selectSequenceNumberRadio();
        aboutYouScreen.enterSequenceNumber(utils.SequenceNumberGenerator.getNextSequenceNumber());

        System.out.println("Entering short invalid National ID: 12345...");
        aboutYouScreen.enterNationalId("12345");
        aboutYouScreen.selectDob();
        aboutYouScreen.enterPhoneNumber(utils.TestDataBuilder.DEFAULT_PHONE);
        aboutYouScreen.selectPolicyStartDate();
        aboutYouScreen.clickPrivacyCheckbox();
        aboutYouScreen.clickNext();

        try { Thread.sleep(1500); } catch (Exception ignore) {}
        OtpModal otpModal = new OtpModal();
        QuoteScreen quoteScreen = new QuoteScreen();
        Assert.assertFalse(otpModal.isModalDisplayed(), "OTP modal must NOT open when National ID is too short.");
        Assert.assertFalse(quoteScreen.isQuoteVisibleNow(), "Quote screen must NOT load when National ID is too short.");
        Assert.assertTrue(aboutYouScreen.isValidationErrorDisplayed() || aboutYouScreen.isScreenDisplayed(),
                "AboutYouScreen or validation error should remain displayed when short National ID is submitted.");
        System.out.println("[PASS] Short National ID validation check verified!");
    }

    @Test
    public void testInvalidSequenceNumber_Letters() {
        System.out.println("\n--- [Negative Test 2] Testing Invalid Sequence Number (Alphabetic Characters) ---");
        navigateToAboutYouScreen();

        AboutYouScreen aboutYouScreen = new AboutYouScreen();
        aboutYouScreen.selectNewInsuranceTab();
        aboutYouScreen.selectSequenceNumberRadio();
        System.out.println("Entering non-numeric Sequence Number: ABC12345...");
        aboutYouScreen.enterSequenceNumber("ABC12345");

        String enteredSeq = aboutYouScreen.getSequenceNumberText();
        boolean lettersStripped = enteredSeq != null && !enteredSeq.toUpperCase().contains("ABC");

        aboutYouScreen.enterNationalId(utils.NationalIdGenerator.getNextNationalId());
        aboutYouScreen.selectDob();
        aboutYouScreen.enterPhoneNumber(utils.TestDataBuilder.DEFAULT_PHONE);
        aboutYouScreen.selectPolicyStartDate();
        aboutYouScreen.clickPrivacyCheckbox();
        aboutYouScreen.clickNext();

        try { Thread.sleep(1500); } catch (Exception ignore) {}
        OtpModal otpModal = new OtpModal();
        QuoteScreen quoteScreen = new QuoteScreen();
        Assert.assertFalse(quoteScreen.isQuoteVisibleNow(), "Quote screen must NOT load for invalid Sequence Number.");
        boolean rejected = !otpModal.isModalDisplayed() || aboutYouScreen.isValidationErrorDisplayed();
        Assert.assertTrue(lettersStripped || rejected,
                "Non-numeric Sequence Number characters should be stripped or rejected on Next.");
        if (!lettersStripped) {
            Assert.assertFalse(otpModal.isModalDisplayed(),
                    "OTP modal must NOT open when alphabetic characters remained in Sequence Number.");
        }
        System.out.println("[PASS] Non-numeric Sequence Number validation check verified (lettersStripped=" + lettersStripped + ")!");
    }

    @Test
    public void testIncorrectOtp_VerificationError() {
        System.out.println("\n--- [Negative Test 3] Testing Incorrect OTP Verification Error ---");
        navigateToAboutYouScreen();

        AboutYouScreen aboutYouScreen = new AboutYouScreen();
        String nationalId = utils.NationalIdGenerator.getNextNationalId();
        String sequenceNumber = utils.SequenceNumberGenerator.getNextSequenceNumber();

        aboutYouScreen.selectNewInsuranceTab();
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

        QuoteScreen quoteScreen = new QuoteScreen();
        Assert.assertFalse(quoteScreen.isQuoteVisibleNow(), "Quote Screen must NOT load when incorrect OTP '0000' is entered.");
        Assert.assertTrue(
                otpModal.isModalDisplayed()
                        || aboutYouScreen.isErrorDialogDisplayed()
                        || aboutYouScreen.isValidationErrorDisplayed()
                        || aboutYouScreen.isScreenDisplayed(),
                "Incorrect OTP should keep OTP modal / error dialog open or block progression.");
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
        aboutYouScreen.enterSequenceNumber(utils.SequenceNumberGenerator.getNextSequenceNumber());
        aboutYouScreen.enterNationalId(utils.NationalIdGenerator.getNextNationalId());
        aboutYouScreen.selectDob();
        aboutYouScreen.enterPhoneNumber(utils.TestDataBuilder.DEFAULT_PHONE);
        aboutYouScreen.selectPolicyStartDate();
        aboutYouScreen.clickPrivacyCheckbox();
        aboutYouScreen.clickNext();

        try { Thread.sleep(1500); } catch (Exception ignore) {}
        OtpModal otpModal = new OtpModal();
        QuoteScreen quoteScreen = new QuoteScreen();
        Assert.assertFalse(otpModal.isModalDisplayed(), "OTP modal must NOT open when Seller ID is invalid.");
        Assert.assertFalse(quoteScreen.isQuoteVisibleNow(), "Quote screen must NOT load when Seller ID is invalid.");
        Assert.assertTrue(aboutYouScreen.isValidationErrorDisplayed() || aboutYouScreen.isScreenDisplayed(),
                "AboutYouScreen or validation error should remain displayed when Seller ID is invalid.");
        System.out.println("[PASS] Invalid Seller ID validation check verified!");
    }

    @Test
    public void testEmptyRequiredFields_FormValidation() {
        System.out.println("\n--- [Negative Test 5] Testing Empty Required Fields Validation ---");
        navigateToAboutYouScreen();

        AboutYouScreen aboutYouScreen = new AboutYouScreen();
        System.out.println("Attempting to click Next without filling mandatory fields...");
        aboutYouScreen.clickNext();

        try { Thread.sleep(1500); } catch (Exception ignore) {}
        OtpModal otpModal = new OtpModal();
        QuoteScreen quoteScreen = new QuoteScreen();
        Assert.assertFalse(otpModal.isModalDisplayed(), "OTP modal must NOT appear when mandatory fields are empty.");
        Assert.assertFalse(quoteScreen.isQuoteVisibleNow(), "Quote screen must NOT load when mandatory fields are empty.");
        Assert.assertTrue(aboutYouScreen.isValidationErrorDisplayed() || aboutYouScreen.isScreenDisplayed(),
                "Form submission should remain blocked on AboutYouScreen when mandatory fields are empty.");
        System.out.println("[PASS] Empty mandatory fields validation check verified!");
    }
}
