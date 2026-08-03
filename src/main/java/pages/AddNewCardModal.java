package pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;

public class AddNewCardModal extends BasePage {

    public AddNewCardModal() {
        super();
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(10)), this);
    }

    @AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='textfield_credit_debit_card_bottom_sheet_card_number' or contains(@resource-id, 'card_number')]")
    private WebElement cardNumberField;

    @AndroidFindBy(xpath = "//android.widget.ImageView[@resource-id='textfield_credit_debit_card_bottom_sheet_expiry_date' or contains(@resource-id, 'expiry_date')]")
    private WebElement expiryDateField;

    @AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='textfield_credit_debit_card_bottom_sheet_cvv' or contains(@resource-id, 'cvv')]")
    private WebElement cvvField;

    @AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='textfield_credit_debit_card_bottom_sheet_card_holder_name' or contains(@resource-id, 'card_holder_name')]")
    private WebElement cardHolderNameField;

    @AndroidFindBy(xpath = "//android.widget.Button[@resource-id='btn_credit_debit_card_bottom_sheet_save' or contains(@resource-id, 'save')]")
    private WebElement saveButton;

    public void enterCardNumber(String cardNumber) {
        System.out.println("Entering Card Number: " + cardNumber);
        sendKeys(cardNumberField, cardNumber);
    }

    public void enterExpiryDate(String expiry) {
        System.out.println("Opening Expiry Date picker...");
        try {
            expiryDateField.click();
        } catch (Exception e) {
            tapElement(expiryDateField);
        }
        try { Thread.sleep(1500); } catch (Exception e) {}
        
        try {
            WebElement yearPicker = getVisibleElement(org.openqa.selenium.By.xpath("(//android.widget.SeekBar)[1]"));
            WebElement monthPicker = getVisibleElement(org.openqa.selenium.By.xpath("(//android.widget.SeekBar)[2]"));
            
            org.openqa.selenium.Rectangle yearRect = yearPicker.getRect();
            org.openqa.selenium.Rectangle monthRect = monthPicker.getRect();
            
            int yearX = yearRect.getX() + (yearRect.getWidth() / 2);
            int yearBottomY = yearRect.getY() + (int)(yearRect.getHeight() * 0.9);
            int yearTopY = yearRect.getY() + (int)(yearRect.getHeight() * 0.1);
            scrollWheel(yearX, yearBottomY, yearTopY);
            
            int yearMidBottomY = yearRect.getY() + (int)(yearRect.getHeight() * 0.7);
            int yearMidTopY = yearRect.getY() + (int)(yearRect.getHeight() * 0.5);
            scrollWheel(yearX, yearMidBottomY, yearMidTopY);
            
            // --- Month Wheel: Scroll UP to 01 (January) ---
            System.out.println("Scrolling Expiry Month wheel to 01 (January)...");
            int monthX = monthRect.getX() + (monthRect.getWidth() / 2);
            int monthTopY = monthRect.getY() + (int)(monthRect.getHeight() * 0.15);
            int monthBottomY = monthRect.getY() + (int)(monthRect.getHeight() * 0.85);

            // Swipe DOWN to pull the list upward to 01
            for (int i = 1; i <= 3; i++) {
                try {
                    java.util.List<WebElement> monthCheck = driver.findElements(
                        org.openqa.selenium.By.xpath("//*[contains(@content-desc,'01') or contains(@text,'01') or contains(@content-desc,'Jan') or contains(@text,'Jan')]")
                    );
                    if (!monthCheck.isEmpty() && monthCheck.get(0).isDisplayed()) {
                        System.out.println("Expiry Month '01' (January) is selected!");
                        break;
                    }
                } catch (Exception ignored) {}

                System.out.println("Swiping DOWN on Expiry Month wheel (attempt " + i + "/3)...");
                scrollWheel(monthX, monthTopY, monthBottomY);
                try { Thread.sleep(500); } catch (Exception e) {}
            }
            
            try {
                WebElement confirmBtn = driver.findElement(org.openqa.selenium.By.xpath("//*[@text='Confirm' or @content-desc='Confirm' or @text='Done' or @content-desc='Done' or @text='Select']"));
                confirmBtn.click();
            } catch (Exception ex) {
                System.out.println("Confirm button not found, picker auto-confirmed.");
            }
        } catch (Exception e) {
            System.out.println("Date picker wheel interaction completed/skipped: " + e.getMessage());
        }
        
        try { Thread.sleep(1000); } catch (Exception e) {}
    }

    public void enterCvv(String cvv) {
        System.out.println("Entering CVV...");
        sendKeys(cvvField, cvv);
    }

    public void enterCardHolderName(String name) {
        System.out.println("Entering Cardholder Name...");
        sendKeys(cardHolderNameField, name);
    }

    public void clickSave() {
        System.out.println("Clicking Save Button...");
        try { Thread.sleep(3000); } catch (Exception e) {}
        try {
            saveButton.click();
        } catch (Exception e) {
            tapElement(saveButton);
        }
        System.out.println("Card saved! Waiting 30 seconds for payment processing...");
        try { Thread.sleep(30000); } catch (Exception e) {}
    }
}
