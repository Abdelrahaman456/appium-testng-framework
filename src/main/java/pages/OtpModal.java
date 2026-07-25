package pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class OtpModal extends BasePage {

    public OtpModal() {
        super();
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(0)), this);
    }

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_otp_pin_field']")
    private WebElement otpInputField;

    public void enterOtp(String otp) {
        System.out.println("Waiting up to 30 seconds for OTP modal to appear...");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        
        // CRITICAL FIX: We must wait for PRESENCE, not VISIBILITY.
        // Because the OTP field is drawn as 4 separate squares, the actual <EditText> is technically 
        // invisible (opacity 0) and overlaid on top. Appium's visibility check will fail and timeout!
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@resource-id='textfield_otp_pin_field']")));
        
        System.out.println("OTP Field detected! Tapping the first square...");
        
        try {
            // BUG FIX: Appium's default click() hits the exact center of the field.
            // For a 4-square OTP component, the center is the gap between square 2 and 3!
            // We use W3C Actions to click the far left side (the first square).
            int width = otpInputField.getSize().getWidth();
            int offsetToFirstSquare = -(width / 2) + 100; // Move from center to the left edge, then in by 100px
            
            new org.openqa.selenium.interactions.Actions(driver)
                .moveToElement(otpInputField, offsetToFirstSquare, 0)
                .click()
                .perform();
                
            Thread.sleep(1000); // Give keyboard time to rise
            
            System.out.println("First square focused! Typing OTP digits natively...");
            // Use physical AndroidKey hardware events to ensure the React Native listeners fire
            if (driver instanceof AndroidDriver) {
                AndroidDriver androidDriver = (AndroidDriver) driver;
                for (char c : otp.toCharArray()) {
                    AndroidKey key = AndroidKey.valueOf("DIGIT_" + c);
                    androidDriver.pressKey(new KeyEvent(key));
                    Thread.sleep(200); // Small pause between keystrokes
                }
            } else {
                new org.openqa.selenium.interactions.Actions(driver).sendKeys(otp).perform();
            }
            
            System.out.println("OTP injected!");
            
        } catch (Exception e) {
            System.out.println("Error typing OTP: " + e.getMessage());
        }
    }
}
