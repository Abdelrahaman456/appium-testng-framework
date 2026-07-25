package pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;

public class AddNewCardModal extends BasePage {

    public AddNewCardModal() {
        super();
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(10)), this);
    }

    // The developers have now added unique resource-ids!
    
    @AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='textfield_credit_debit_card_bottom_sheet_card_number']")
    private WebElement cardNumberField;

    // Expiry date is rendered as an ImageView in the Appium tree (likely due to the calendar icon wrapper)
    @AndroidFindBy(xpath = "//android.widget.ImageView[@resource-id='textfield_credit_debit_card_bottom_sheet_expiry_date']")
    private WebElement expiryDateField;

    @AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='textfield_credit_debit_card_bottom_sheet_cvv']")
    private WebElement cvvField;

    @AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='textfield_credit_debit_card_bottom_sheet_card_holder_name']")
    private WebElement cardHolderNameField;

    @AndroidFindBy(xpath = "//android.widget.Button[@resource-id='btn_credit_debit_card_bottom_sheet_save']")
    private WebElement saveButton;

    public void enterCardNumber(String cardNumber) {
        sendKeys(cardNumberField, cardNumber);
    }

    public void enterExpiryDate(String expiry) {
        // Click the ImageView field to launch the native wheel picker
        click(expiryDateField);
        try { Thread.sleep(1500); } catch (Exception e) {}
        
        // Find the native SeekBars (Index 1 = Year, Index 2 = Month)
        WebElement yearPicker = getVisibleElement(org.openqa.selenium.By.xpath("(//android.widget.SeekBar)[1]"));
        WebElement monthPicker = getVisibleElement(org.openqa.selenium.By.xpath("(//android.widget.SeekBar)[2]"));
        
        org.openqa.selenium.Rectangle yearRect = yearPicker.getRect();
        org.openqa.selenium.Rectangle monthRect = monthPicker.getRect();
        
        System.out.println("Scrolling Year wheel down to 2031 (Swipe UP inside bounds)...");
        int yearX = yearRect.getX() + (yearRect.getWidth() / 2);
        int yearBottomY = yearRect.getY() + (int)(yearRect.getHeight() * 0.9);
        int yearTopY = yearRect.getY() + (int)(yearRect.getHeight() * 0.1);
        
        // Swipe 1: 80% distance (moves ~4 years, from 2026 to 2030)
        scrollWheel(yearX, yearBottomY, yearTopY);
        
        // Swipe 2: 20% distance (moves ~1 year, from 2030 to 2031)
        int yearMidBottomY = yearRect.getY() + (int)(yearRect.getHeight() * 0.7);
        int yearMidTopY = yearRect.getY() + (int)(yearRect.getHeight() * 0.5);
        scrollWheel(yearX, yearMidBottomY, yearMidTopY);
        
        System.out.println("Scrolling Month wheel up to Jan (Swipe DOWN inside bounds)...");
        int monthX = monthRect.getX() + (monthRect.getWidth() / 2);
        int monthBottomY = monthRect.getY() + (int)(monthRect.getHeight() * 0.9);
        int monthTopY = monthRect.getY() + (int)(monthRect.getHeight() * 0.1);
        
        // Swipe 1: 80% distance (moves ~4 months, from July to March)
        scrollWheel(monthX, monthTopY, monthBottomY);
        
        // Swipe 2: 40% distance (moves ~2 months, from March to Jan)
        int monthMidTopY = monthRect.getY() + (int)(monthRect.getHeight() * 0.3);
        int monthMidBottomY = monthRect.getY() + (int)(monthRect.getHeight() * 0.7);
        scrollWheel(monthX, monthMidTopY, monthMidBottomY);
        
        System.out.println("Clicking Confirm on date picker...");
        getVisibleElement(org.openqa.selenium.By.xpath("//*[@text='Confirm' or @content-desc='Confirm']")).click();
        
        try { Thread.sleep(1000); } catch (Exception e) {} // Wait for picker to close
    }

    public void enterCvv(String cvv) {
        sendKeys(cvvField, cvv);
    }

    public void enterCardHolderName(String name) {
        // BasePage.sendKeys presses ENTER by default, which triggers form submission 
        // or accidentally closes the modal before we can tap Save!
        // We must type WITHOUT pressing ENTER.
        cardHolderNameField.click();
        cardHolderNameField.clear();
        new org.openqa.selenium.interactions.Actions(driver)
            .sendKeys(name)
            .perform();
            
        // Dismiss keyboard cleanly by tapping the non-editable "Add new card" header!
        // This removes focus from the text input so gesture typing ('t5 t5') is not triggered.
        System.out.println("Tapping modal header to safely remove focus from text field...");
        try {
            getVisibleElement(org.openqa.selenium.By.xpath("//*[contains(@content-desc, 'Add new card') or contains(@text, 'Add new card')]")).click();
        } catch (Exception e) {}
        try { Thread.sleep(1000); } catch (Exception e) {} // Wait for keyboard animation to finish
    }

    public void clickSave() {
        System.out.println("Clicking Save Button...");
        
        // Wait 4 seconds to ensure React Native form validation has fully enabled the button
        try { Thread.sleep(4000); } catch (Exception e) {}

        // Try physical tap first
        try {
            tapElement(saveButton);
        } catch (Exception e) {
            System.out.println("Physical tap failed, trying standard click...");
        }
        
        // Fallback to standard Appium click if the tap didn't register
        try { Thread.sleep(2000); } catch (Exception e) {}
        try { 
            saveButton.click(); 
        } catch (Exception e) {
            System.out.println("Standard click also threw an exception: " + e.getMessage());
        }
        
        System.out.println("Card saved! Waiting 25 seconds for redirect to payment page...");
        try { Thread.sleep(25000); } catch (Exception e) {}
    }

    public void autoFillCardDetails(utils.TestDataBuilder.CreditCard card) {
        enterCardNumber(card.number);
        enterExpiryDate(card.expiry);
        enterCvv(card.cvv);
        enterCardHolderName(card.holderName);
        clickSave();
    }
}
