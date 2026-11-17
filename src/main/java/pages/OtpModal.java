package pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.By;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * Page Object for Absher / Platform OTP Authentication Modal.
 * Uses dynamic element bounding box tapping with zero hardcoded coordinates.
 */
public class OtpModal extends BasePage {

    public OtpModal() {
        super();
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(2)), this);
    }

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_otp_pin_field' or contains(@resource-id, 'otp_pin') or contains(@resource-id, 'pin_field')]")
    private WebElement otpInputField;

    @AndroidFindBy(xpath = "//android.widget.Button[@clickable='true' and (@content-desc='Verify' or @text='Verify' or @content-desc='Confirm' or @text='Confirm')] | //*[@resource-id='btn_verify_otp' or @resource-id='btn_verify' or @content-desc='Verify' or @text='Verify' or @content-desc='Confirm' or @text='Confirm']")
    private WebElement verifyButton;

    public boolean isModalDisplayed() {
        try {
            return isElementVisible(otpInputField) || !driver.findElements(By.xpath(
                "//*[@resource-id='textfield_otp_pin_field' or contains(@resource-id, 'otp_pin') or contains(@resource-id, 'pin_field')]")).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private void tapPinBox(WebElement otpField, boolean fourth) {
        Rectangle rect = otpField.getRect();
        double ratio = fourth ? 0.875 : 0.15;
        int tapX = rect.getX() + (int) (rect.getWidth() * ratio);
        int tapY = rect.getY() + (rect.getHeight() / 2);
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1);
        tap.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), tapX, tapY));
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(new Pause(finger, Duration.ofMillis(80)));
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(tap));
    }

    private void typeDigits(String digits) {
        if (driver instanceof AndroidDriver) {
            AndroidDriver androidDriver = (AndroidDriver) driver;
            for (char c : digits.toCharArray()) {
                try {
                    if (Character.isDigit(c)) {
                        androidDriver.pressKey(new KeyEvent(AndroidKey.valueOf("DIGIT_" + c)));
                        Thread.sleep(150);
                    }
                } catch (Exception e) {
                    System.out.println("pressKey failed for digit " + c + ": " + e.getMessage());
                }
            }
        }
        try {
            Runtime.getRuntime().exec(new String[]{BasePage.ADB_PATH, "-s", "emulator-5554", "shell", "input", "text", digits}).waitFor();
            Thread.sleep(400);
        } catch (Exception ignore) {}
    }

    public void enterOtp(String otp) {
        System.out.println("Waiting up to 45 seconds for OTP modal to appear...");
        WebDriverWait localWait = new WebDriverWait(driver, Duration.ofSeconds(45));
        By otpBy = By.xpath("//*[@resource-id='textfield_otp_pin_field' or contains(@resource-id, 'otp_pin') or contains(@resource-id, 'pin_field')]");
        WebElement otpField = localWait.until(ExpectedConditions.presenceOfElementLocated(otpBy));

        String initialText = "";
        try {
            String raw = otpField.getText();
            if (raw != null) {
                initialText = raw.trim();
            }
        } catch (Exception ignore) {}
        System.out.println("OTP Field detected! Initial text: '" + initialText + "', fallback OTP: " + otp);

        if (initialText.length() == 4) {
            try {
                tapPinBox(otpField, true);
                Thread.sleep(600);
            } catch (Exception e) {
                System.out.println("Warning focusing 4th OTP PIN box: " + e.getMessage());
            }
            if (driver instanceof AndroidDriver) {
                try {
                    ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.DEL));
                    Thread.sleep(300);
                } catch (Exception e) {
                    System.out.println("DEL key failed on pre-filled OTP: " + e.getMessage());
                }
            }
            String lastDigit = initialText.substring(3, 4);
            System.out.println("Deleted 4th digit of pre-filled OTP; re-typing '" + lastDigit + "' to trigger verification...");
            typeDigits(lastDigit);
        } else {
            System.out.println("No 4-digit pre-filled OTP detected; focusing first pin box and typing OTP: " + otp);
            try {
                tapPinBox(otpField, false);
                Thread.sleep(600);
            } catch (Exception e) {
                System.out.println("Warning focusing 1st OTP PIN box: " + e.getMessage());
            }
            typeDigits(otp);
        }

        // Click verify button if present
        By verifyBy = By.xpath(
            "//android.widget.Button[@clickable='true' and (@content-desc='Verify' or @text='Verify' or @content-desc='Confirm' or @text='Confirm')] " +
            "| //*[@resource-id='btn_verify_otp' or @resource-id='btn_verify' or @content-desc='Verify' or @text='Verify' or @content-desc='Confirm' or @text='Confirm']");
        try {
            List<WebElement> verifyBtns = findWithUnimportantViews(verifyBy);
            if (!verifyBtns.isEmpty()) {
                System.out.println("Clicking Verify OTP button...");
                tapElement(verifyBtns.get(0));
            }
        } catch (Exception ignore) {}

        By retryOrErrorBy = By.xpath(
            "//*[@content-desc='Try Again' or @text='Try Again' " +
            "or contains(@content-desc, 'Incorrect OTP') or contains(@text, 'Incorrect OTP') " +
            "or contains(@content-desc, 'Something Went Wrong') or contains(@text, 'Something Went Wrong')]");
        waitUntil(() -> driver.findElements(otpBy).isEmpty() || !driver.findElements(retryOrErrorBy).isEmpty(), 4);

        // Recover if stale pre-filled OTP caused "Incorrect OTP entered" or Retry modal
        try {
            List<WebElement> incorrectOtpOrRetry = driver.findElements(retryOrErrorBy);
            if (!incorrectOtpOrRetry.isEmpty()) {
                for (WebElement msgEl : incorrectOtpOrRetry) {
                    System.out.println("OTP Retry modal element detected: text='" + msgEl.getText()
                        + "', content-desc='" + msgEl.getAttribute("content-desc") + "'");
                }
                List<WebElement> tryAgainBtns = driver.findElements(By.xpath(
                    "//*[@content-desc='Try Again' or @text='Try Again' or @content-desc='OK' or @text='OK']"));
                if (!tryAgainBtns.isEmpty()) {
                    tapElement(tryAgainBtns.get(0));
                    waitUntil(() -> !driver.findElements(otpBy).isEmpty(), 3);
                }
                otpField = driver.findElement(otpBy);
                tapPinBox(otpField, true);
                Thread.sleep(350);
                if (driver instanceof AndroidDriver) {
                    AndroidDriver androidDriver = (AndroidDriver) driver;
                    for (int i = 0; i < 5; i++) {
                        androidDriver.pressKey(new KeyEvent(AndroidKey.DEL));
                        Thread.sleep(100);
                    }
                }
                String fallbackDigits = (otp != null && !otp.trim().isEmpty()) ? otp.trim() : "1234";
                typeDigits(fallbackDigits);
                try {
                    WebElement refreshedField = driver.findElement(otpBy);
                    System.out.println("Post-recovery OTP field value: '" + refreshedField.getText() + "'");
                } catch (Exception ignore) {}
                List<WebElement> retryVerifyBtns = findWithUnimportantViews(verifyBy);
                if (!retryVerifyBtns.isEmpty()) {
                    System.out.println("Clicking Verify OTP button after recovery...");
                    tapElement(retryVerifyBtns.get(0));
                }
                waitUntil(() -> driver.findElements(otpBy).isEmpty(), 4);
            }
        } catch (Exception e) {
            System.out.println("Incorrect OTP recovery handled: " + e.getMessage());
        }

        if (!driver.findElements(otpBy).isEmpty()) {
            System.out.println("Waiting up to 4s for OTP modal dismissal...");
            waitUntil(() -> driver.findElements(otpBy).isEmpty(), 4);
        }
        System.out.println("OTP step completed.");
    }
}
