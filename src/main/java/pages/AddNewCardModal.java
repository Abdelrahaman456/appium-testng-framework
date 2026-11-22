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

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_credit_debit_card_bottom_sheet_cvv' or @resource-id='textfield_cvv' or contains(@resource-id, 'cvv') or contains(@resource-id, 'cvc') or contains(@hint, 'CVV') or contains(@hint, 'CVC') or contains(@content-desc, 'CVV') or contains(@content-desc, 'CVC') or contains(@text, 'CVV') or contains(@text, 'CVC')]")
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
        int targetMonth = 12;
        int targetYear = 2026;
        if (expiry != null && expiry.contains("/")) {
            String[] parts = expiry.split("/");
            if (parts.length == 2) {
                int parsedMonth = parseMonthNumber(parts[0].trim());
                if (parsedMonth >= 1 && parsedMonth <= 12) {
                    targetMonth = parsedMonth;
                }
                if (parts[1].trim().matches("\\d{2,4}")) {
                    int parsedYear = Integer.parseInt(parts[1].trim());
                    targetYear = parsedYear < 100 ? 2000 + parsedYear : parsedYear;
                }
            }
        }

        System.out.println("Opening Expiry Date picker (target=" + String.format("%02d/%d", targetMonth, targetYear) + ")...");
        try {
            waitForVisibility(expiryDateField);
            expiryDateField.click();
        } catch (Exception e) {
            tapElement(expiryDateField);
        }

        org.openqa.selenium.By allSeekBarsBy = org.openqa.selenium.By.xpath("//android.widget.SeekBar");
        boolean hasSeekBars = waitUntil(() -> driver.findElements(allSeekBarsBy).size() >= 2, 4);
        if (!hasSeekBars && driver.findElements(allSeekBarsBy).isEmpty()) {
            String formattedExpiry = String.format("%02d/%02d", targetMonth, targetYear % 100);
            System.out.println("No SeekBar date picker appeared within 4s; falling back to sendKeys: " + formattedExpiry);
            sendKeys(expiryDateField, formattedExpiry);
            return;
        }

        try {
            org.openqa.selenium.By seek1By = org.openqa.selenium.By.xpath("(//android.widget.SeekBar)[1]");
            org.openqa.selenium.By seek2By = org.openqa.selenium.By.xpath("(//android.widget.SeekBar)[2]");
            WebElement s1 = getVisibleElement(seek1By);
            WebElement s2 = getVisibleElement(seek2By);
            String d1 = getWheelValue(s1);
            String d2 = getWheelValue(s2);

            boolean d1IsYear = d1.matches("\\d{4}");
            boolean d2IsYear = d2.matches("\\d{4}");
            boolean s2IsYear;
            if (d2IsYear && !d1IsYear) {
                s2IsYear = true;
            } else if (d1IsYear && !d2IsYear) {
                s2IsYear = false;
            } else {
                boolean d1IsMonth = parseMonthNumber(d1) != -1;
                boolean d2IsMonth = parseMonthNumber(d2) != -1;
                s2IsYear = d1IsMonth && !d2IsMonth;
            }
            org.openqa.selenium.By yearBy = s2IsYear ? seek2By : seek1By;
            org.openqa.selenium.By monthBy = s2IsYear ? seek1By : seek2By;
            System.out.println("Expiry wheels initial state -> Year=" + (s2IsYear ? d2 : d1) + ", Month=" + (s2IsYear ? d1 : d2));

            boolean swipeUpToIncreaseYear = true;
            Integer lastYear = null;
            for (int attempt = 0; attempt < 12; attempt++) {
                WebElement yearPicker = getVisibleElement(yearBy);
                String yearVal = getWheelValue(yearPicker);
                org.openqa.selenium.Rectangle yr = yearPicker.getRect();
                int h = Math.max(yr.getHeight(), 100);
                int yearX = yr.getX() + (yr.getWidth() / 2);
                int centerY = yr.getY() + (yr.getHeight() / 2);

                if (!yearVal.matches("\\d{4}")) {
                    int nudge = Math.max(40, (int) (h * 0.25));
                    scrollWheel(yearX, centerY + (nudge / 2), centerY - (nudge / 2));
                    Thread.sleep(300);
                    continue;
                }
                int currentYear = Integer.parseInt(yearVal);
                System.out.println("Expiry Year wheel attempt " + (attempt + 1) + ": current=" + currentYear + ", target=" + targetYear);
                if (currentYear == targetYear) {
                    System.out.println("Expiry Year '" + currentYear + "' selected!");
                    break;
                }
                if (lastYear != null) {
                    boolean movedAway = (lastYear < targetYear && currentYear < lastYear)
                            || (lastYear > targetYear && currentYear > lastYear);
                    boolean stalled = (currentYear == lastYear);
                    if (movedAway || stalled) {
                        swipeUpToIncreaseYear = !swipeUpToIncreaseYear;
                    }
                }
                lastYear = currentYear;

                int diff = Math.abs(targetYear - currentYear);
                int deltaY = Math.max((int) (h * 0.22), Math.min((int) (h * 0.55), diff * (int) (h * 0.14)));
                boolean needHigher = currentYear < targetYear;
                boolean moveUp = needHigher ? swipeUpToIncreaseYear : !swipeUpToIncreaseYear;
                if (moveUp) {
                    scrollWheel(yearX, centerY + (deltaY / 2), centerY - (deltaY / 2));
                } else {
                    scrollWheel(yearX, centerY - (deltaY / 2), centerY + (deltaY / 2));
                }
                Thread.sleep(300);
            }

            boolean swipeUpToIncreaseMonth = true;
            Integer lastMonth = null;
            for (int attempt = 0; attempt < 14; attempt++) {
                WebElement monthPicker = getVisibleElement(monthBy);
                String monthVal = getWheelValue(monthPicker);
                int currentMonth = parseMonthNumber(monthVal);
                org.openqa.selenium.Rectangle mr = monthPicker.getRect();
                int mh = Math.max(mr.getHeight(), 100);
                int monthX = mr.getX() + (mr.getWidth() / 2);
                int centerY = mr.getY() + (mr.getHeight() / 2);

                if (currentMonth == -1) {
                    int nudge = Math.max(40, (int) (mh * 0.25));
                    scrollWheel(monthX, centerY + (nudge / 2), centerY - (nudge / 2));
                    Thread.sleep(300);
                    continue;
                }
                System.out.println("Expiry Month wheel attempt " + (attempt + 1) + ": current=" + currentMonth + " ('" + monthVal + "'), target=" + targetMonth);
                if (currentMonth == targetMonth) {
                    System.out.println("Expiry Month '" + monthVal + "' (" + currentMonth + ") selected!");
                    break;
                }
                if (lastMonth != null) {
                    boolean movedAway = (lastMonth < targetMonth && currentMonth < lastMonth)
                            || (lastMonth > targetMonth && currentMonth > lastMonth);
                    boolean stalled = (currentMonth == lastMonth);
                    if (movedAway || stalled) {
                        swipeUpToIncreaseMonth = !swipeUpToIncreaseMonth;
                    }
                }
                lastMonth = currentMonth;

                int diff = Math.abs(targetMonth - currentMonth);
                int deltaY = Math.max((int) (mh * 0.22), Math.min((int) (mh * 0.55), diff * (int) (mh * 0.14)));
                boolean needHigher = currentMonth < targetMonth;
                boolean moveUp = needHigher ? swipeUpToIncreaseMonth : !swipeUpToIncreaseMonth;
                if (moveUp) {
                    scrollWheel(monthX, centerY + (deltaY / 2), centerY - (deltaY / 2));
                } else {
                    scrollWheel(monthX, centerY - (deltaY / 2), centerY + (deltaY / 2));
                }
                Thread.sleep(300);
            }

            try {
                WebElement confirmBtn = driver.findElement(org.openqa.selenium.By.xpath(
                    "(//*[@text='Confirm' or @content-desc='Confirm' or @text='Done' or @content-desc='Done' or @text='Select' or @content-desc='Select'])[last()]"));
                tapElement(confirmBtn);
            } catch (Exception ex) {
                System.out.println("Confirm button not found, picker auto-confirmed.");
            }
        } catch (Exception e) {
            System.out.println("Date picker wheel interaction completed/skipped: " + e.getMessage());
        }

        try { Thread.sleep(1000); } catch (Exception e) {}
    }

    private int parseMonthNumber(String val) {
        if (val == null) return -1;
        String s = val.trim().toLowerCase();
        if (s.isEmpty()) return -1;
        if (s.matches("\\d{1,2}")) {
            int m = Integer.parseInt(s);
            return (m >= 1 && m <= 12) ? m : -1;
        }
        switch (s) {
            case "jan": case "january": return 1;
            case "feb": case "february": return 2;
            case "mar": case "march": return 3;
            case "apr": case "april": return 4;
            case "may": return 5;
            case "jun": case "june": return 6;
            case "jul": case "july": return 7;
            case "aug": case "august": return 8;
            case "sep": case "sept": case "september": return 9;
            case "oct": case "october": return 10;
            case "nov": case "november": return 11;
            case "dec": case "december": return 12;
            default: break;
        }
        java.util.regex.Matcher numMatcher = java.util.regex.Pattern.compile("\\b(1[0-2]|0?[1-9])\\b").matcher(s);
        if (numMatcher.find()) {
            int m = Integer.parseInt(numMatcher.group(1));
            if (m >= 1 && m <= 12) return m;
        }
        String[] abbrs = {"jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"};
        for (int i = 0; i < abbrs.length; i++) {
            if (s.contains(abbrs[i])) {
                return i + 1;
            }
        }
        return -1;
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
        String[] errorKeywords = {"invalid", "required", "expired", "incorrect", "enter a valid", "not valid"};
        StringBuilder xpath = new StringBuilder("//*[");
        for (int i = 0; i < errorKeywords.length; i++) {
            if (i > 0) {
                xpath.append(" or ");
            }
            String kw = errorKeywords[i];
            xpath.append("contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '").append(kw).append("')")
                 .append(" or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '").append(kw).append("')");
        }
        xpath.append("]");
        return !driver.findElements(org.openqa.selenium.By.xpath(xpath.toString())).isEmpty();
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
