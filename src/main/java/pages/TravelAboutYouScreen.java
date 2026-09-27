package pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;

public class TravelAboutYouScreen extends BasePage {

    public TravelAboutYouScreen() {
        super();
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(10)), this);
    }

    // --- Trip Type Tabs ---
    @AndroidFindBy(xpath = "//*[@text='Single trip' or @content-desc='Single trip']")
    private WebElement singleTripTab;

    @AndroidFindBy(xpath = "//*[@text='Multi trip (1 year)' or @content-desc='Multi trip (1 year)']")
    private WebElement multiTripTab;

    // --- Form Fields ---
    @AndroidFindBy(xpath = "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.widget.FrameLayout/android.view.View/android.view.View/android.view.View/android.view.View[1]/android.widget.EditText[1]")
    private WebElement destinationDropdown;

    @AndroidFindBy(xpath = "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.widget.FrameLayout/android.view.View/android.view.View/android.view.View/android.view.View[1]/android.widget.ImageView[1]")
    private WebElement destinationDropdownArrow;

    @AndroidFindBy(xpath = "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.widget.FrameLayout/android.view.View/android.view.View/android.view.View/android.view.View[1]/android.widget.ImageView[2]")
    private WebElement departureDatepicker;

    @AndroidFindBy(xpath = "//*[contains(@text, 'Return Date') or contains(@content-desc, 'Return Date') or contains(@resource-id, 'return')]")
    private WebElement returnDatepicker;

    @AndroidFindBy(xpath = "//android.widget.EditText[contains(@hint, 'Mobile number') or contains(@text, 'Mobile number') or contains(@resource-id, 'mobile') or contains(@resource-id, 'phone')]")
    private WebElement mobileNumberField;

    // --- Traveler Types ---
    @AndroidFindBy(xpath = "//*[@text='Individual' or @content-desc='Individual']")
    private WebElement individualTravelerBtn;

    @AndroidFindBy(xpath = "//*[@text='With Family' or @content-desc='With Family']")
    private WebElement familyTravelerBtn;

    @AndroidFindBy(xpath = "//*[@text='Group' or @content-desc='Group']")
    private WebElement groupTravelerBtn;

    // --- Footer ---
    @AndroidFindBy(xpath = "//*[contains(@resource-id, 'checkbox') or contains(@resource-id, 'tgl') or @class='android.widget.Switch' or @class='android.widget.CheckBox']")
    private WebElement privacyCheckbox;

    @AndroidFindBy(xpath = "//*[@text='Next' or @content-desc='Next' or contains(@resource-id, 'next')]")
    private WebElement nextButton;

    // ==========================================
    // Interactions
    // ==========================================

    public void selectSingleTrip() {
        System.out.println("Selecting Single Trip tab...");
        click(singleTripTab);
    }

    public void selectMultiTrip() {
        System.out.println("Selecting Multi Trip (1 year) tab...");
        click(multiTripTab);
    }

    public void selectDestination(String destinationName) {
        System.out.println("Selecting Destination: " + destinationName);
        try {
            waitForVisibility(destinationDropdownArrow);
            System.out.println("Tapping the destination dropdown ARROW (ImageView)...");
            tapElement(destinationDropdownArrow);
        } catch (Exception e) {
            System.out.println("Warning: Could not tap arrow. Trying right side of EditText...");
            try {
                // Manually compute the right-side of the EditText box where the dropdown chevron lives
                org.openqa.selenium.Rectangle rect = destinationDropdown.getRect();
                int rightX = rect.getX() + rect.getWidth() - 30; // 30 pixels from the right edge
                int centerY = rect.getY() + (rect.getHeight() / 2);
                System.out.println("Tapping computed coordinates: " + rightX + ", " + centerY);
                tapCoordinates(rightX, centerY);
            } catch (Exception ex) {
                System.out.println("CRITICAL: Choose Destination field could not be found or tapped!");
                throw ex;
            }
        }
        
        try { Thread.sleep(2000); } catch(Exception e) {} // wait for dropdown/modal
        
        // Dynamically find and click the specific destination
        org.openqa.selenium.By destLocator = org.openqa.selenium.By.xpath("//android.view.View[@content-desc='" + destinationName + "']");
        try {
            WebElement destElement = getVisibleElement(destLocator);
            tapElement(destElement);
        } catch (Exception e) {
            System.out.println("Could not find destination text '" + destinationName + "'. Proceeding.");
        }
        
        // Optional confirm button if it's a picker
        try {
            WebElement confirmBtn = driver.findElement(org.openqa.selenium.By.xpath("//*[@text='Confirm' or @content-desc='Confirm']"));
            if (confirmBtn.isDisplayed()) click(confirmBtn);
        } catch (Exception e) { /* Auto-closes */ }
    }

    public void selectDepartureDate() {
        System.out.println("Selecting Departure Date...");
        tapElement(departureDatepicker);
        try { Thread.sleep(2000); } catch(Exception e) {}
        
        System.out.println("Selecting 'Dec' in the Date Picker...");
        try {
            WebElement decValue = driver.findElement(io.appium.java_client.AppiumBy.accessibilityId("Dec"));
            tapElement(decValue);
        } catch (Exception e) {
            System.out.println("Could not explicitly select Dec.");
        }
        
        try {
            WebElement confirmBtn = getVisibleElement(org.openqa.selenium.By.xpath("//android.widget.Button[@content-desc='Confirm']"));
            tapElement(confirmBtn);
        } catch (Exception e) {}
    }

    public void selectReturnDate() {
        System.out.println("Selecting Return Date...");
        // This is only valid for Single Trip
        try {
            if (returnDatepicker.isDisplayed()) {
                click(returnDatepicker);
                try { Thread.sleep(1000); } catch(Exception e) {}
                WebElement confirmBtn = getVisibleElement(org.openqa.selenium.By.xpath("//*[@text='Confirm' or @content-desc='Confirm' or @text='Done']"));
                click(confirmBtn);
            }
        } catch (Exception e) {
            System.out.println("Return date picker not found (Expected if Multi-Trip is selected).");
        }
    }

    public void enterMobileNumber(String mobileNumber) {
        System.out.println("Entering Mobile Number: " + mobileNumber);
        sendKeys(mobileNumberField, mobileNumber);
        try {
            if (driver instanceof io.appium.java_client.android.AndroidDriver) {
                ((io.appium.java_client.android.AndroidDriver) driver).hideKeyboard();
            }
        } catch (Exception e) {}
    }

    public void selectTravelerType(String travelerType) {
        System.out.println("Selecting Traveler Type: " + travelerType);
        scrollDown(); // Ensure traveler buttons are visible
        
        switch (travelerType.toLowerCase()) {
            case "individual":
                click(individualTravelerBtn);
                break;
            case "with family":
                click(familyTravelerBtn);
                break;
            case "group":
                click(groupTravelerBtn);
                break;
            default:
                throw new IllegalArgumentException("Unknown traveler type: " + travelerType);
        }
    }

    public void clickPrivacyCheckbox() {
        System.out.println("Checking Privacy Notice...");
        scrollDown(); // ensure it's on screen
        click(privacyCheckbox);
    }

    public void clickNext() {
        System.out.println("Clicking Next...");
        click(nextButton);
    }
}
