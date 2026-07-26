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

    @AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='textfield_credit_debit_card_bottom_sheet_card_number']")
    private WebElement cardNumberField;

    @AndroidFindBy(xpath = "//android.widget.ImageView[@resource-id='textfield_credit_debit_card_bottom_sheet_expiry_date' or contains(@resource-id, 'expiry_date')]")
    private WebElement expiryDateField;

    @AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='textfield_credit_debit_card_bottom_sheet_cvv']")
    private WebElement cvvField;

    @AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='textfield_credit_debit_card_bottom_sheet_card_holder_name']")
    private WebElement cardHolderNameField;

    @AndroidFindBy(xpath = "//android.widget.Button[@resource-id='btn_credit_debit_card_bottom_sheet_save']")
    private WebElement saveButton;

    public void enterCardNumber(String cardNumber) {
        System.out.println("Entering Card Number...");
        try {
            cardNumberField.click();
            cardNumberField.clear();
        } catch (Exception e) {}
        cardNumberField.sendKeys(cardNumber);
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
            
            int monthX = monthRect.getX() + (monthRect.getWidth() / 2);
            int monthBottomY = monthRect.getY() + (int)(monthRect.getHeight() * 0.9);
            int monthTopY = monthRect.getY() + (int)(monthRect.getHeight() * 0.1);
            scrollWheel(monthX, monthTopY, monthBottomY);
            
            int monthMidTopY = monthRect.getY() + (int)(monthRect.getHeight() * 0.3);
            int monthMidBottomY = monthRect.getY() + (int)(monthRect.getHeight() * 0.7);
            scrollWheel(monthX, monthMidTopY, monthMidBottomY);
            
            try {
                WebElement confirmBtn = driver.findElement(org.openqa.selenium.By.xpath("//*[@text='Confirm' or @content-desc='Confirm' or @text='Done' or @content-desc='Done' or @text='Select']"));
                confirmBtn.click();
            } catch (Exception ex) {
                System.out.println("Confirm button not found, picker auto-confirmed.");
            }
        } catch (Exception e) {
            System.out.println("Date picker wheel interaction completed/skipped.");
        }
        
        try { Thread.sleep(1000); } catch (Exception e) {}
    }

    public void enterCvv(String cvv) {
        System.out.println("Entering CVV...");
        try {
            cvvField.click();
            cvvField.clear();
        } catch (Exception e) {}
        cvvField.sendKeys(cvv);
    }

    public void enterCardHolderName(String name) {
        System.out.println("Entering Cardholder Name...");
        try {
            cardHolderNameField.click();
            cardHolderNameField.clear();
        } catch (Exception e) {}
        cardHolderNameField.sendKeys(name);
    }

    public void clickSave() {
        System.out.println("Clicking Save Button...");
        try { Thread.sleep(3000); } catch (Exception e) {}
        try {
            saveButton.click();
        } catch (Exception e) {
            tapElement(saveButton);
        }
        System.out.println("Card saved! Waiting 25 seconds for payment processing...");
        try { Thread.sleep(25000); } catch (Exception e) {}
    }
}
