package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.TravelAboutYouScreen;

public class TravelTest extends BaseTest {

    @DataProvider(name = "travelerTypes")
    public Object[][] travelerTypes() {
        return new Object[][] {
            {"Individual"},
            {"With Family"},
            {"Group"}
        };
    }

    // =========================================================================================
    // ABSTRACTION HELPERS: Reusable flows for Travel Product
    // =========================================================================================

    private void fillSharedTravelFields(TravelAboutYouScreen travelScreen, String travelerType) {
        travelScreen.selectDestination("Worldwide"); // Example destination, can be updated based on app config
        travelScreen.selectDepartureDate();
        travelScreen.enterMobileNumber(utils.TestDataBuilder.DEFAULT_PHONE);
        travelScreen.selectTravelerType(travelerType);
        travelScreen.clickPrivacyCheckbox();
        travelScreen.clickNext();
    }

    // =========================================================================================
    // TEST FLOWS
    // =========================================================================================

    @Test(dataProvider = "travelerTypes", description = "Travel Flow 1: Single Trip for Individual, Family, and Group")
    public void testTravelFlow1_SingleTrip(String travelerType) {
        System.out.println("\n--- [TRAVEL FLOW 1] Single Trip | Traveler: " + travelerType + " ---");
        
        // 1. Navigate to Travel product
        pages.HomeScreen homeScreen = new pages.HomeScreen();
        homeScreen.clickTravelProduct(); 
        
        pages.MotorCoverageSelectionScreen coverageScreen = new pages.MotorCoverageSelectionScreen();
        coverageScreen.clickInsureNow();
        
        // 2. Fill About You Screen
        TravelAboutYouScreen travelScreen = new TravelAboutYouScreen();
        
        travelScreen.selectSingleTrip();
        travelScreen.selectDestination("Schengen");
        travelScreen.selectDepartureDate();
        travelScreen.selectReturnDate(); // Specific to Single Trip
        
        travelScreen.enterMobileNumber(utils.TestDataBuilder.DEFAULT_PHONE);
        travelScreen.selectTravelerType(travelerType);
        travelScreen.clickPrivacyCheckbox();
        
        System.out.println("Validating next step...");
        travelScreen.clickNext();
        
        // Add validations for the next screen (e.g. OTP, Quote, or Traveler Details)
        // Assert.assertTrue(new NextScreen().isPageLoaded(), "Failed to navigate past Travel About You Screen");
    }

    @Test(dataProvider = "travelerTypes", description = "Travel Flow 2: Multi Trip (1 year) for Individual, Family, and Group")
    public void testTravelFlow2_MultiTrip(String travelerType) {
        System.out.println("\n--- [TRAVEL FLOW 2] Multi Trip (1 year) | Traveler: " + travelerType + " ---");
        
        // 1. Navigate to Travel product
        pages.HomeScreen homeScreen = new pages.HomeScreen();
        homeScreen.clickTravelProduct(); 
        
        pages.MotorCoverageSelectionScreen coverageScreen = new pages.MotorCoverageSelectionScreen();
        coverageScreen.clickInsureNow();
        
        // 2. Fill About You Screen
        TravelAboutYouScreen travelScreen = new TravelAboutYouScreen();
        
        travelScreen.selectMultiTrip();
        travelScreen.selectDestination("Schengen"); 
        travelScreen.selectDepartureDate();
        // NOTE: Multi trip does NOT have a Return Date picker!
        
        travelScreen.enterMobileNumber(utils.TestDataBuilder.DEFAULT_PHONE);
        travelScreen.selectTravelerType(travelerType);
        travelScreen.clickPrivacyCheckbox();
        
        System.out.println("Validating next step...");
        travelScreen.clickNext();
        
        // Add validations for the next screen
    }
}
