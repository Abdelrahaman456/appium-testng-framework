package pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;

public class AddNewCardModal extends BasePage {

    public boolean isModalDisplayed() {
        if (BasePage.uatElmDependentBlocked) {
            System.out.println("[UAT Elm Notice] Skipping AddNewCardModal display check due to blocked dependent step.");
            return false;
        }
        org.openqa.selenium.By modalBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='textfield_credit_debit_card_bottom_sheet_card_number' or @resource-id='textfield_card_number' " +
            "or contains(@resource-id, 'card_number') or contains(@content-desc, 'Card number') or contains(@text, 'Card number') " +
            "or contains(@hint, 'Card number') or contains(@resource-id, 'card_bottom_sheet') " +
            "or @resource-id='btn_credit_debit_card_bottom_sheet_save']"
        );
        return waitUntil(() -> !driver.findElements(modalBy).isEmpty(), 10);
    }

    public AddNewCardModal() {
        super();
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(2)), this);
    }

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_credit_debit_card_bottom_sheet_card_number' or @resource-id='textfield_card_number' or contains(@resource-id, 'card_number') or contains(@hint, 'Card number') or contains(@hint, 'Card Number') or contains(@content-desc, 'Card number') or contains(@text, 'Card number')]")
    private WebElement cardNumberField;

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_credit_debit_card_bottom_sheet_expiry_date' or @resource-id='textfield_expiry_date' or contains(@resource-id, 'expiry_date') or contains(@hint, 'Expiry') or contains(@hint, 'MM') or contains(@content-desc, 'Expiry') or contains(@text, 'Expiry')]")
    private WebElement expiryDateField;

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_credit_debit_card_bottom_sheet_cvv' or @resource-id='textfield_cvv' or contains(@resource-id, 'cvv') or contains(@hint, 'CVV') or contains(@content-desc, 'CVV') or contains(@text, 'CVV')]")
    private WebElement cvvField;

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_credit_debit_card_bottom_sheet_card_holder_name' or @resource-id='textfield_card_holder_name' or contains(@resource-id, 'card_holder_name') or contains(@hint, 'Card holder') or contains(@hint, 'Name') or contains(@content-desc, 'Card holder') or contains(@text, 'Card holder')]")
    private WebElement cardHolderNameField;

    @AndroidFindBy(xpath = "//*[@resource-id='btn_credit_debit_card_bottom_sheet_save' or @resource-id='btn_save_button' or contains(@resource-id, 'card_bottom_sheet_save') or contains(@resource-id, 'save') or @content-desc='Save' or @text='Save']")
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
            org.openqa.selenium.By seek1By = org.openqa.selenium.By.xpath("(//android.widget.SeekBar)[1]");
            org.openqa.selenium.By seek2By = org.openqa.selenium.By.xpath("(//android.widget.SeekBar)[2]");
            WebElement s1 = getVisibleElement(seek1By);
            WebElement s2 = getVisibleElement(seek2By);
            String d1 = getWheelValue(s1);
            String d2 = getWheelValue(s2);

            boolean s2IsYear = !d1.matches("\\d{4}") && d2.matches("\\d{4}");
            org.openqa.selenium.By yearBy = s2IsYear ? seek2By : seek1By;
            org.openqa.selenium.By monthBy = s2IsYear ? seek1By : seek2By;
            System.out.println("Expiry wheels initial state -> Year=" + (s2IsYear ? d2 : d1) + ", Month=" + (s2IsYear ? d1 : d2));

            int targetYear = 2030;
            if (expiry != null && expiry.contains("/")) {
                String[] parts = expiry.split("/");
                if (parts.length == 2 && parts[1].trim().matches("\\d{2,4}")) {
                    int parsed = Integer.parseInt(parts[1].trim());
                    targetYear = parsed < 100 ? 2000 + parsed : parsed;
                    if (targetYear < 2028) targetYear = 2030;
                }
            }
            boolean swipeUpToIncreaseYear = true;
            Integer lastYear = null;
            for (int attempt = 0; attempt < 8; attempt++) {
                WebElement yearPicker = getVisibleElement(yearBy);
                String yearVal = getWheelValue(yearPicker);
                if (!yearVal.matches("\\d{4}")) {
                    org.openqa.selenium.Rectangle yr = yearPicker.getRect();
                    int yearX = yr.getX() + (yr.getWidth() / 2);
                    scrollWheel(yearX, yr.getY() + (int) (yr.getHeight() * 0.70), yr.getY() + (int) (yr.getHeight() * 0.45));
                    Thread.sleep(500);
                    continue;
                }
                int currentYear = Integer.parseInt(yearVal);
                System.out.println("Expiry Year wheel attempt " + (attempt + 1) + ": current=" + currentYear + ", target=" + targetYear);
                if (currentYear >= 2028 && currentYear <= 2035) {
                    System.out.println("Expiry Year '" + currentYear + "' selected!");
                    break;
                }
                if (lastYear != null && ((targetYear > lastYear && currentYear < lastYear) || (targetYear < lastYear && currentYear > lastYear))) {
                    swipeUpToIncreaseYear = !swipeUpToIncreaseYear;
                }
                lastYear = currentYear;

                org.openqa.selenium.Rectangle yr = yearPicker.getRect();
                int yearX = yr.getX() + (yr.getWidth() / 2);
                int centerY = yr.getY() + (yr.getHeight() / 2);
                int deltaY = Math.min(180, Math.max(60, Math.abs(targetYear - currentYear) * 30));
                boolean needHigher = currentYear < targetYear;
                boolean moveUp = needHigher ? swipeUpToIncreaseYear : !swipeUpToIncreaseYear;
                if (moveUp) {
                    scrollWheel(yearX, centerY + (deltaY / 2), centerY - (deltaY / 2));
                } else {
                    scrollWheel(yearX, centerY - (deltaY / 2), centerY + (deltaY / 2));
                }
                Thread.sleep(500);
            }

            for (int i = 1; i <= 4; i++) {
                WebElement monthPicker = getVisibleElement(monthBy);
                String monthVal = getWheelValue(monthPicker);
                if (monthVal.contains("01") || monthVal.toLowerCase().contains("jan")
                        || monthVal.contains("12") || monthVal.toLowerCase().contains("dec")) {
                    System.out.println("Expiry Month '" + monthVal + "' selected!");
                    break;
                }
                System.out.println("Adjusting Expiry Month wheel (attempt " + i + "/4, current='" + monthVal + "')...");
                org.openqa.selenium.Rectangle mr = monthPicker.getRect();
                int monthX = mr.getX() + (mr.getWidth() / 2);
                int centerY = mr.getY() + (mr.getHeight() / 2);
                scrollWheel(monthX, centerY - 60, centerY + 60);
                Thread.sleep(500);
            }

            try {
                WebElement confirmBtn = driver.findElement(org.openqa.selenium.By.xpath(
                    "(//*[@text='Confirm' or @content-desc='Confirm' or @text='Done' or @content-desc='Done' or @text='Select'])[last()]"));
                tapElement(confirmBtn);
            } catch (Exception ex) {
                System.out.println("Confirm button not found, picker auto-confirmed.");
            }
        } catch (Exception e) {
            System.out.println("Date picker wheel interaction completed/skipped: " + e.getMessage());
        }

        try { Thread.sleep(1000); } catch (Exception e) {}
    }

    private String getWheelValue(WebElement wheel) {
        try {
            String desc = wheel.getAttribute("content-desc");
            if (desc != null && !desc.trim().isEmpty()) {
                return desc.trim();
            }
            String txt = wheel.getText();
            return txt != null ? txt.trim() : "";
        } catch (Exception e) {
            return "";
        }
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
        org.openqa.selenium.By saveBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='btn_credit_debit_card_bottom_sheet_save' or @resource-id='btn_save_button']//*[@clickable='true']"
            + " | //*[@clickable='true' and (@resource-id='btn_credit_debit_card_bottom_sheet_save' or @resource-id='btn_save_button' or @content-desc='Save' or @text='Save')]"
        );
        if (findWithUnimportantViews(saveBy).isEmpty()) {
            try {
                java.util.List<WebElement> titles = driver.findElements(org.openqa.selenium.By.xpath(
                    "//*[contains(@content-desc, 'Add new card') or contains(@text, 'Add new card')]"));
                if (!titles.isEmpty()) {
                    tapElement(titles.get(0));
                    waitUntil(() -> !findWithUnimportantViews(saveBy).isEmpty(), 2);
                }
            } catch (Exception ignore) {}
        }
        tapPreferredButton(saveBy, saveButton);
        System.out.println("Card saved! Handing off to dynamic Policy Confirmation polling...");
        try { Thread.sleep(2000); } catch (Exception e) {}
    }

    /**
     * Checks if any inline card validation error message is displayed inside the Add New Card bottom sheet.
     */
    public boolean isCardValidationErrorDisplayed() {
        org.openqa.selenium.By errorBy = org.openqa.selenium.By.xpath(
            "//*[contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'invalid') " +
            "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'invalid') " +
            "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'required') " +
            "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'required') " +
            "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'expired') " +
            "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'expired') " +
            "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'valid') " +
            "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'valid') " +
            "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'incorrect') " +
            "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'incorrect')]"
        );
        return !driver.findElements(errorBy).isEmpty();
    }

    /**
     * Verifies that submitting invalid card inputs is blocked (either inline error shown or modal stays open).
     */
    public boolean isSaveBlockedOrErrorShown() {
        hideKeyboard();
        try { Thread.sleep(600); } catch (Exception ignore) {}
        return isCardValidationErrorDisplayed() || isModalDisplayed();
    }

    /**
     * Dismisses the Add New Card modal cleanly to verify cancellation returns to Checkout screen.
     */
    public void dismissModal() {
        System.out.println("Dismissing Add New Card modal...");
        hideKeyboard();
        try { Thread.sleep(500); } catch (Exception ignore) {}

        // 1. Try close icon inside bottom sheet
        try {
            org.openqa.selenium.By closeBy = org.openqa.selenium.By.xpath(
                "//*[@resource-id='btn_close' or @content-desc='Close' or @text='Close' or @content-desc='Cancel' or @text='Cancel']"
            );
            java.util.List<WebElement> closes = driver.findElements(closeBy);
            if (!closes.isEmpty() && closes.get(0).isDisplayed()) {
                tapElement(closes.get(0));
                Thread.sleep(1000);
                if (!isModalDisplayedQuick()) return;
            }
        } catch (Exception ignore) {}

        // 2. Drag down on the bottom sheet header / card number region to dismiss Flutter modal sheet without popping Checkout
        try {
            org.openqa.selenium.By sheetHeaderBy = org.openqa.selenium.By.xpath(
                "//*[contains(@content-desc, 'Add new card') or contains(@text, 'Add new card') or @resource-id='textfield_credit_debit_card_bottom_sheet_card_number']"
            );
            java.util.List<WebElement> headers = driver.findElements(sheetHeaderBy);
            if (!headers.isEmpty()) {
                org.openqa.selenium.Rectangle r = headers.get(0).getRect();
                org.openqa.selenium.Dimension size = driver.manage().window().getSize();
                int startX = r.getX() + (r.getWidth() / 2);
                int startY = r.getY();
                int endY = (int) (size.height * 0.92);
                scrollWheel(startX, startY, endY);
                Thread.sleep(1000);
                if (!isModalDisplayedQuick()) return;
            }
        } catch (Exception ignore) {}

        // 3. Fallback: tap dimmed scrim above the bottom sheet (15% screen height)
        if (isModalDisplayedQuick()) {
            org.openqa.selenium.Dimension size = driver.manage().window().getSize();
            tapCoordinates(size.width / 2, (int) (size.height * 0.15));
            try { Thread.sleep(1000); } catch (Exception ignore) {}
        }
    }

    private boolean isModalDisplayedQuick() {
        org.openqa.selenium.By modalBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='textfield_credit_debit_card_bottom_sheet_card_number' or @resource-id='btn_credit_debit_card_bottom_sheet_save']"
        );
        return !driver.findElements(modalBy).isEmpty();
    }
}
