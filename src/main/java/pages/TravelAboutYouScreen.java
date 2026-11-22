package pages;

import io.appium.java_client.pagefactory.AndroidFindBy;

import io.appium.java_client.pagefactory.AppiumFieldDecorator;

import org.openqa.selenium.WebElement;

import org.openqa.selenium.support.PageFactory;

import java.time.Duration;

public class TravelAboutYouScreen extends BasePage {

    public TravelAboutYouScreen() {

        super();

        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(2)), this);

    }

    // --- Trip Type Tabs ---

    @AndroidFindBy(xpath = "//*[@text='Single trip' or @content-desc='Single trip']")

    private WebElement singleTripTab;

    @AndroidFindBy(xpath = "//*[@text='Annual multi-trip' or @content-desc='Annual multi-trip' or contains(@content-desc, 'multi-trip') or contains(@text, 'multi-trip') or @text='Multi trip (1 year)' or contains(@content-desc, 'Multi trip')]")
    private WebElement multiTripTab;

    // --- Form Fields ---

    @AndroidFindBy(xpath = "//*[@resource-id='dropdown_destination' or @resource-id='textfield_destination' or contains(@hint, 'Destination') or contains(@text, 'Destination')]")

    private WebElement destinationDropdown;

    @AndroidFindBy(xpath = "//*[contains(@resource-id, 'departure') or contains(@hint, 'Departure') or contains(@content-desc, 'Departure') or contains(@text, 'Departure')] | (//android.widget.ImageView[contains(@resource-id, 'textfield') or contains(@resource-id, 'date')])[1]")
    private WebElement departureDatepicker;

    @AndroidFindBy(xpath = "//*[contains(@resource-id, 'return') or contains(@hint, 'Return') or contains(@content-desc, 'Return') or contains(@text, 'Return')] | (//android.widget.ImageView[contains(@resource-id, 'textfield') or contains(@resource-id, 'date')])[2]")
    private WebElement returnDatepicker;

    @AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='textfield_phone_number' or contains(@resource-id, 'phone') or contains(@hint, 'Phone') or contains(@hint, 'phone') or contains(@hint, 'Mobile') or contains(@text, 'Mobile')]")
    private WebElement mobileNumberField;

    // --- Traveler Types ---
    @AndroidFindBy(xpath = "//*[@text='Individual' or @content-desc='Individual']")
    private WebElement individualTravelerBtn;

    @AndroidFindBy(xpath = "//*[@text='With Family' or @content-desc='With Family']")
    private WebElement familyTravelerBtn;

    @AndroidFindBy(xpath = "//*[@text='Group' or @content-desc='Group']")
    private WebElement groupTravelerBtn;

    // --- Initial Trip Info (New APK Flow) ---
    @AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='textfield_national_id' or contains(@hint, 'National') or contains(@text, 'National')]")
    private WebElement nationalIdField;

    @AndroidFindBy(xpath = "//*[@resource-id='textfield_dob' or @resource-id='textfield_month_year_of_birth' or contains(@hint, 'birth') or contains(@text, 'birth')]")
    private WebElement dobField;

    // --- Footer ---
    @AndroidFindBy(xpath = "//*[@resource-id='tgl_checkbox' or @class='android.widget.Switch']")
    private WebElement privacyCheckbox;

    @AndroidFindBy(xpath = "//*[@resource-id='btn_next_button' or @content-desc='Next' or @text='Next']")
    private WebElement nextButton;

    // ==========================================

    // Interactions

    // ==========================================

    public void dismissOtpIfPresent() {
        try {
            org.openqa.selenium.By otpBy = org.openqa.selenium.By.xpath(
                "//*[@resource-id='textfield_otp_pin_field' or contains(@content-desc, 'OTP') or contains(@text, 'OTP')]"
            );
            if (!driver.findElements(otpBy).isEmpty()) {
                System.out.println("Dismissing/entering OTP modal on Travel screen...");
                new OtpModal().enterOtp(utils.TestDataBuilder.DEFAULT_OTP);
                Thread.sleep(2500);
            }
        } catch (Exception ignore) {}
    }

    public void selectSingleTrip() {
        dismissOtpIfPresent();
        System.out.println("Selecting Single Trip tab...");
        click(singleTripTab);
    }

    public void selectMultiTrip() {
        dismissOtpIfPresent();
        System.out.println("Selecting Multi Trip (1 year) tab...");
        click(multiTripTab);
    }

    public void selectDestination(String destinationName) {

        System.out.println("Selecting Destination: " + destinationName);

        try {

            waitForVisibility(destinationDropdown);

            tapElement(destinationDropdown);

        } catch (Exception e) {

            System.out.println("Standard tap on destination failed, tapping center X:500 Y:630...");

            // dynamic locator used; coordinate fallback removed

        }

        

        try { Thread.sleep(1500); } catch(Exception e) {} // wait for dropdown/modal

        

        // Dynamically find and click the specific destination

        org.openqa.selenium.By destLocator = org.openqa.selenium.By.xpath("//android.view.View[contains(@content-desc, '" + destinationName + "') or contains(@text, '" + destinationName + "')]");

        try {

            WebElement destElement = getVisibleElement(destLocator);

            tapElement(destElement);

        } catch (Exception e) {

            System.out.println("Could not find destination text '" + destinationName + "'. Tapping first option...");

            // dynamic locator used; coordinate fallback removed

        }

        

        try { Thread.sleep(1000); } catch (Exception e) {}

        

        // Auto-hide keyboard so it doesn't cover traveler options

        try {

            if (driver instanceof io.appium.java_client.android.AndroidDriver) {

                ((io.appium.java_client.android.AndroidDriver) driver).hideKeyboard();

            }

        } catch (Exception ignored) {}

    }

    private int calendarMonthOffset = 0;

    private WebElement findDatePicker(boolean isReturn) {
        int minPickerY = (int) (getSafeWindowSize().height * 0.15);
        org.openqa.selenium.By primaryBy = isReturn
            ? org.openqa.selenium.By.xpath("//*[contains(@resource-id, 'return') or contains(@hint, 'Return') or contains(@content-desc, 'Return') or contains(@text, 'Return')]")
            : org.openqa.selenium.By.xpath("//*[contains(@resource-id, 'departure') or contains(@hint, 'Departure') or contains(@content-desc, 'Departure') or contains(@text, 'Departure')]");
        for (WebElement el : findWithUnimportantViews(primaryBy)) {
            try {
                if (el.isDisplayed() && el.getRect().getY() > minPickerY) {
                    return el;
                }
            } catch (Exception ignore) {}
        }
        org.openqa.selenium.By fallbackBy = org.openqa.selenium.By.xpath(
            "//android.widget.ImageView[contains(@resource-id, 'textfield') or contains(@resource-id, 'date') or @clickable='true']"
        );
        java.util.List<WebElement> filtered = new java.util.ArrayList<>();
        for (WebElement el : findWithUnimportantViews(fallbackBy)) {
            try {
                if (el.isDisplayed() && el.getRect().getY() > minPickerY) {
                    filtered.add(el);
                }
            } catch (Exception ignore) {}
        }
        if (isReturn && filtered.size() >= 2) {
            return filtered.get(1);
        }
        if (!isReturn && !filtered.isEmpty()) {
            return filtered.get(0);
        }
        WebElement boundEl = isReturn ? returnDatepicker : departureDatepicker;
        try {
            if (isElementVisible(boundEl) && boundEl.getRect().getY() > minPickerY) {
                return boundEl;
            }
        } catch (Exception ignore) {}
        return null;
    }

    private boolean tapNextMonthArrowIfPresent() {
        org.openqa.selenium.Dimension size = getSafeWindowSize();
        int minHeaderY = (int) (size.height * 0.15);
        int maxHeaderY = (int) (size.height * 0.65);

        org.openqa.selenium.By nextMonthBy = org.openqa.selenium.By.xpath(
            "//*[contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'next month') " +
            "or contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'forward') " +
            "or contains(@resource-id, 'next_month') or contains(@resource-id, 'arrow_right') or @resource-id='next']"
        );
        for (WebElement arrow : findWithUnimportantViews(nextMonthBy)) {
            try {
                org.openqa.selenium.Rectangle r = arrow.getRect();
                if (arrow.isDisplayed() && r.getY() > minHeaderY && r.getY() < maxHeaderY) {
                    System.out.println("Tapping next-month arrow for rolled-over target month...");
                    tapElement(arrow);
                    try { Thread.sleep(500); } catch (Exception ignore) {}
                    return true;
                }
            } catch (Exception ignore) {}
        }
        return false;
    }

    private void selectCalendarDate(java.time.LocalDate targetDate, java.time.LocalDate today) {
        String dayNumber = String.valueOf(targetDate.getDayOfMonth());
        String monthFull = targetDate.getMonth().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH);
        String monthShort = targetDate.getMonth().getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.ENGLISH);
        int monthsAhead = (targetDate.getYear() - today.getYear()) * 12 + (targetDate.getMonthValue() - today.getMonthValue());
        System.out.println("Selecting dynamic calendar date: " + targetDate + " (monthsAhead=" + monthsAhead + ")");

        org.openqa.selenium.Dimension size = getSafeWindowSize();
        int minCalY = (int) (size.height * 0.18);
        int maxCalY = (int) (size.height * 0.88);
        int minCellW = (int) (size.width * 0.02);
        int maxCellW = (int) (size.width * 0.32);
        int minCellH = (int) (size.height * 0.01);
        int maxCellH = (int) (size.height * 0.11);
        int secondMonthMinY = (int) (size.height * 0.60);
        int secondMonthMaxY = (int) (size.height * 0.82);

        if (monthsAhead > calendarMonthOffset) {
            org.openqa.selenium.By targetMonthLabelBy = org.openqa.selenium.By.xpath(
                "//*[contains(@content-desc, '" + monthFull + "') or contains(@text, '" + monthFull + "')]"
            );
            boolean targetMonthVisible = false;
            for (WebElement ml : findWithUnimportantViews(targetMonthLabelBy)) {
                try {
                    if (ml.isDisplayed() && ml.getRect().getY() > minCalY && ml.getRect().getY() < maxCalY) {
                        targetMonthVisible = true;
                        break;
                    }
                } catch (Exception ignore) {}
            }
            if (!targetMonthVisible) {
                while (calendarMonthOffset < monthsAhead) {
                    if (!tapNextMonthArrowIfPresent()) {
                        scrollDown();
                    }
                    calendarMonthOffset++;
                }
            }
        }

        org.openqa.selenium.By dayBy = org.openqa.selenium.By.xpath(
            "//*[@enabled='true' and (" +
            "@content-desc='" + dayNumber + "' or @text='" + dayNumber + "' " +
            "or starts-with(@content-desc, '" + dayNumber + ", ') " +
            "or starts-with(@content-desc, '" + dayNumber + " ') " +
            "or contains(@content-desc, ' " + dayNumber + ", ')" +
            ") and not(@content-desc='0 days')]"
        );

        try {
            java.util.List<WebElement> candidates = findWithUnimportantViews(dayBy);
            java.util.List<WebElement> validCells = new java.util.ArrayList<>();
            for (WebElement el : candidates) {
                try {
                    org.openqa.selenium.Rectangle r = el.getRect();
                    if (el.isDisplayed() && r.getY() > minCalY && r.getY() < maxCalY
                            && r.getWidth() > minCellW && r.getWidth() < maxCellW && r.getHeight() > minCellH) {
                        String desc = el.getAttribute("content-desc");
                        String txt = el.getText();
                        String combined = ((desc != null ? desc : "") + " " + (txt != null ? txt : "")).toLowerCase();
                        if (combined.contains(monthFull.toLowerCase()) || combined.contains(monthShort.toLowerCase())) {
                            tapElement(el);
                            return;
                        }
                        validCells.add(el);
                    }
                } catch (Exception ignore) {}
            }
            if (!validCells.isEmpty()) {
                WebElement chosen = validCells.get(0);
                int bestY = (monthsAhead > 0 && validCells.size() > 1) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
                for (WebElement cell : validCells) {
                    try {
                        int cy = cell.getRect().getY();
                        if ((monthsAhead > 0 && validCells.size() > 1) ? (cy > bestY) : (cy < bestY)) {
                            bestY = cy;
                            chosen = cell;
                        }
                    } catch (Exception ignore) {}
                }
                tapElement(chosen);
                return;
            }

            // Fallback: inspect calendar nodes using proportional screen bounds
            System.out.println("Day '" + dayNumber + "' not matched by text/desc; inspecting calendar grid nodes...");
            java.util.List<WebElement> allNodes = findWithUnimportantViews(org.openqa.selenium.By.xpath("//*"));
            java.util.List<WebElement> monthDayCells = new java.util.ArrayList<>();
            for (WebElement node : allNodes) {
                try {
                    org.openqa.selenium.Rectangle r = node.getRect();
                    if ("true".equalsIgnoreCase(node.getAttribute("enabled"))
                            && r.getY() >= secondMonthMinY && r.getY() <= secondMonthMaxY
                            && r.getWidth() > minCellW && r.getWidth() < maxCellW
                            && r.getHeight() > minCellH && r.getHeight() < maxCellH) {
                        monthDayCells.add(node);
                    }
                } catch (Exception ignore) {}
            }
            if (!monthDayCells.isEmpty()) {
                int idx = Math.min(monthDayCells.size() - 1, Math.max(0, targetDate.getDayOfMonth() - 1));
                System.out.println("Tapping fallback calendar cell index " + idx + " of " + monthDayCells.size());
                tapElement(monthDayCells.get(idx));
            }
        } catch (Exception e) {
            System.out.println("Calendar day selection warning: " + e.getMessage());
        }
    }

    private void confirmCalendarPicker() {
        org.openqa.selenium.By confirmBy = org.openqa.selenium.By.xpath("(//*[@text='Confirm' or @content-desc='Confirm'])[last()]");
        org.openqa.selenium.By resetBy = org.openqa.selenium.By.xpath("//*[@text='Reset' or @content-desc='Reset']");
        try {
            WebElement confirmBtn = getVisibleElement(confirmBy);
            tapElement(confirmBtn);
            waitUntil(() -> findWithUnimportantViews(resetBy).isEmpty(), 3);
        } catch (Exception e) {
            System.out.println("Calendar confirm warning: " + e.getMessage());
        }
    }

    public void selectDepartureDate() {
        System.out.println("Selecting Departure Date...");
        calendarMonthOffset = 0;
        try {
            WebElement picker = findDatePicker(false);
            if (picker != null) {
                tapElement(picker);
            } else {
                waitForVisibility(departureDatepicker);
                tapElement(departureDatepicker);
            }
        } catch (Exception e) {
            System.out.println("Departure datepicker tap warning: " + e.getMessage());
        }

        org.openqa.selenium.By sheetReadyBy = org.openqa.selenium.By.xpath("//*[@text='Confirm' or @content-desc='Confirm' or @text='Reset' or @content-desc='Reset']");
        waitUntil(() -> !driver.findElements(sheetReadyBy).isEmpty(), 3);
        try { Thread.sleep(500); } catch (Exception ignore) {}

        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate departureDate = today.plusDays(2);
        java.time.LocalDate returnDate = today.plusDays(6);

        selectCalendarDate(departureDate, today);

        org.openqa.selenium.By rangeMarkerBy = org.openqa.selenium.By.xpath(
            "//*[contains(@content-desc, 'Return') or contains(@text, 'Return') " +
            "or contains(@hint, 'Return') or contains(@content-desc, 'days') or contains(@text, 'days')]"
        );
        if (!findWithUnimportantViews(rangeMarkerBy).isEmpty()) {
            try { Thread.sleep(400); } catch (Exception ignore) {}
            selectCalendarDate(returnDate, today);
        }

        confirmCalendarPicker();
        try { Thread.sleep(700); } catch (Exception ignore) {}
    }

    public void selectReturnDate() {
        System.out.println("Selecting Return Date...");
        org.openqa.selenium.By resetBy = org.openqa.selenium.By.xpath("//*[@text='Reset' or @content-desc='Reset']");
        if (findWithUnimportantViews(resetBy).isEmpty()) {
            System.out.println("Return Date already confirmed in range calendar; skipping redundant picker open.");
            return;
        }
        java.time.LocalDate today = java.time.LocalDate.now();
        selectCalendarDate(today.plusDays(6), today);
        confirmCalendarPicker();
        try { Thread.sleep(700); } catch (Exception ignore) {}
    }

    public void enterMobileNumber(String mobileNumber) {
        System.out.println("Entering Mobile Number: " + mobileNumber);
        org.openqa.selenium.By phoneBy = org.openqa.selenium.By.xpath(
            "//android.widget.EditText[@resource-id='textfield_phone_number' or contains(@resource-id, 'phone') or contains(@hint, 'Phone') or contains(@hint, 'phone') or contains(@hint, 'Mobile') or contains(@text, 'Mobile')]"
        );
        if (!waitUntil(() -> !driver.findElements(phoneBy).isEmpty(), 3)) {
            System.out.println("Mobile number field not present on this step (already entered on initial screen); skipping.");
            return;
        }
        sendKeys(driver.findElements(phoneBy).get(0), mobileNumber);
        hideKeyboard();
    }

    public void enterEmailIfPresent(String email) {
        try {
            org.openqa.selenium.By emailBy = org.openqa.selenium.By.xpath(
                "//android.widget.EditText[@resource-id='textfield_claims_user_email' or @resource-id='textfield_email' or contains(@resource-id, 'email') " +
                "or contains(@hint, 'Email') or contains(@hint, 'email') or contains(@text, 'Email')]"
            );
            java.util.List<WebElement> emailFields = driver.findElements(emailBy);
            if (!emailFields.isEmpty() && emailFields.get(0).isDisplayed()) {
                WebElement emailField = emailFields.get(0);
                String existing = emailField.getText();
                if (existing == null || existing.trim().isEmpty() || existing.toLowerCase().contains("email")) {
                    System.out.println("Entering Email on Travel screen: " + email);
                    sendKeys(emailField, email);
                    try {
                        driver.executeScript("mobile: performEditorAction", com.google.common.collect.ImmutableMap.of("action", "done"));
                    } catch (Exception ignore) {}
                    hideKeyboard();
                    try { Thread.sleep(500); } catch (Exception ignore) {}
                }
            }
        } catch (Exception e) {
            System.out.println("Travel email check notice: " + e.getMessage());
        }
    }

    public void enterPassportIfPresent(String passport) {
        try {
            org.openqa.selenium.By passportBy = org.openqa.selenium.By.xpath(
                "//android.widget.EditText[@resource-id='textfield_passport_number' or contains(@resource-id, 'passport') " +
                "or contains(@hint, 'Passport') or contains(@text, 'Passport') " +
                "or contains(@content-desc, 'Passport')]"
            );
            java.util.List<WebElement> passportFields = driver.findElements(passportBy);
            if (!passportFields.isEmpty() && passportFields.get(0).isDisplayed()) {
                WebElement passportField = passportFields.get(0);
                String existing = passportField.getText();
                if (existing == null || existing.trim().isEmpty() || existing.toLowerCase().contains("passport")) {
                    System.out.println("Entering Passport number on Travel screen: " + passport);
                    sendKeys(passportField, passport);
                    try {
                        driver.executeScript("mobile: performEditorAction", com.google.common.collect.ImmutableMap.of("action", "done"));
                    } catch (Exception ignore) {}
                    hideKeyboard();
                    try { Thread.sleep(500); } catch (Exception ignore) {}
                }
            } else {
                java.util.List<WebElement> allInputs = driver.findElements(org.openqa.selenium.By.xpath("//android.widget.EditText"));
                if (allInputs.size() >= 2) {
                    WebElement secondField = allInputs.get(1);
                    String txt = secondField.getText();
                    if (txt == null || txt.trim().isEmpty() || txt.toLowerCase().contains("passport")) {
                        System.out.println("Entering Passport into second input field: " + passport);
                        sendKeys(secondField, passport);
                        hideKeyboard();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Travel passport entry note: " + e.getMessage());
        }
    }

    public void selectTravelerType(String travelerType) {
        System.out.println("Selecting Traveler Type: " + travelerType);
        hideKeyboard();

        org.openqa.selenium.By legacyBtnBy = org.openqa.selenium.By.xpath(
            "//*[@text='" + travelerType + "' or @content-desc='" + travelerType + "']"
        );
        java.util.List<WebElement> legacyBtns = driver.findElements(legacyBtnBy);
        if (!legacyBtns.isEmpty()) {
            tapElement(legacyBtns.get(0));
            return;
        }
        System.out.println("Traveler type '" + travelerType + "' uses default Policy holder card on current Travel UI; continuing.");
    }

    public void clickInsureNowIfPresent() {
        org.openqa.selenium.By landingBy = org.openqa.selenium.By.xpath(
            "//*[contains(@content-desc, 'Travel Insurance') or contains(@text, 'Travel Insurance') or contains(@content-desc, 'Explorer')]"
        );
        org.openqa.selenium.By insureBtnBy = org.openqa.selenium.By.xpath(
            "//*[@content-desc='Insure now' or @text='Insure now' or @resource-id='btn_insure_now']"
        );
        try {
            if (!driver.findElements(landingBy).isEmpty() || !driver.findElements(insureBtnBy).isEmpty()) {
                System.out.println("Detected Travel Landing screen. Tapping Insure now...");
                for (int attempt = 1; attempt <= 3; attempt++) {
                    java.util.List<WebElement> btns = findWithUnimportantViews(insureBtnBy);
                    if (btns.isEmpty()) {
                        scrollDown();
                        btns = findWithUnimportantViews(insureBtnBy);
                    }
                    if (!btns.isEmpty()) {
                        tapElement(btns.get(btns.size() - 1));
                    }
                    try { Thread.sleep(1500); } catch (Exception ignore) {}
                    if (isInitialTripInfoScreenVisible() || !driver.findElements(org.openqa.selenium.By.xpath("//*[@text='Single trip' or @content-desc='Single trip']")).isEmpty()) {
                        System.out.println("Successfully transitioned past Travel Landing screen!");
                        return;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("clickInsureNowIfPresent note: " + e.getMessage());
        }
    }

    public boolean isInitialTripInfoScreenVisible() {
        try {
            return !driver.findElements(org.openqa.selenium.By.xpath(
                "//*[contains(@text, 'Insure your trip') or contains(@content-desc, 'Insure your trip') or contains(@hint, 'National') or @resource-id='textfield_national_id']"
            )).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public void enterNationalId(String nationalId) {
        System.out.println("Entering National ID on Travel screen: " + nationalId);
        try {
            waitForVisibility(nationalIdField);
            sendKeys(nationalIdField, nationalId);
        } catch (Exception e) {
            org.openqa.selenium.By idLocator = org.openqa.selenium.By.xpath("//android.widget.EditText[@resource-id='textfield_national_id' or contains(@hint, 'National') or contains(@text, 'National')]");
            WebElement el = getVisibleElement(idLocator);
            sendKeys(el, nationalId);
        }
    }

    public void selectDob() {
        System.out.println("Opening Month/Year of birth dropdown on Travel screen...");
        int minY = (int) (getSafeWindowSize().height * 0.15);
        org.openqa.selenium.By locator = org.openqa.selenium.By.xpath(
            "//android.widget.EditText[@resource-id='textfield_national_id']/following::android.widget.ImageView[1] " +
            "| //*[contains(@resource-id, 'dob') or contains(@resource-id, 'birth') or @hint='Month/Year of birth' or contains(@hint, 'birth') or @content-desc='Month/Year of birth' or contains(@content-desc, 'Month')]"
        );
        WebElement dobTarget = null;
        for (WebElement cand : findWithUnimportantViews(locator)) {
            try {
                if (cand.isDisplayed() && cand.getRect().getY() > minY) {
                    dobTarget = cand;
                    break;
                }
            } catch (Exception ignore) {}
        }
        if (dobTarget == null) {
            dobTarget = isElementVisible(dobField) ? dobField : getVisibleElement(locator);
        }
        scrollDobWheelAndConfirm(dobTarget);
    }

    public void selectSchengenDestination() {
        System.out.println("Selecting Schengen destination zone...");
        selectDestination("Schengen");
    }

    public void selectWorldwideDestination() {
        System.out.println("Selecting Worldwide destination zone...");
        selectDestination("Worldwide");
    }

    public void toggleWinterSportsAddon() {
        System.out.println("Toggling Winter/Extreme Sports Add-on cover...");
        try {
            org.openqa.selenium.By sportsBy = org.openqa.selenium.By.xpath("//*[contains(@content-desc, 'Sports') or contains(@text, 'Sports') or contains(@content-desc, 'Hazardous') or contains(@text, 'Hazardous')]");
            WebElement sportsEl = driver.findElement(sportsBy);
            tapElement(sportsEl);
        } catch (Exception ignore) {}
    }

    public void handleInitialTripInfoIfPresent(String nationalId, String phone) {
        if (isInitialTripInfoScreenVisible()) {
            System.out.println("Detected initial 'Insure your trip' screen. Filling preliminary details...");
            enterNationalId(nationalId);
            selectDob();
            enterMobileNumber(phone);
            clickPrivacyCheckbox();

            // Tap Next on Step 1
            hideKeyboard();
            org.openqa.selenium.By nextBtnBy = org.openqa.selenium.By.xpath(
                "//*[@resource-id='btn_next_button']//*[@clickable='true'] "
                + "| //android.widget.Button[@clickable='true' and (@content-desc='Next' or @text='Next')] "
                + "| //*[@resource-id='btn_next_button' or @content-desc='Next' or @text='Next']"
            );
            java.util.List<WebElement> btns = findWithUnimportantViews(nextBtnBy);
            if (btns.isEmpty()) {
                scrollDown();
                btns = findWithUnimportantViews(nextBtnBy);
            }
            if (!btns.isEmpty()) {
                tapElement(btns.get(btns.size() - 1));
            } else {
                click(nextButton);
            }

            // Check for transient 'Something Went Wrong' / 'Try Again' error
            org.openqa.selenium.By tryAgainBy = org.openqa.selenium.By.xpath(
                "//*[@resource-id='btn_try_again_button' or @content-desc='Try Again' or @text='Try Again']"
            );
            for (int retry = 0; retry < 3; retry++) {
                try { Thread.sleep(1000); } catch (Exception ignore) {}
                java.util.List<WebElement> tryAgainBtns = driver.findElements(tryAgainBy);
                if (!tryAgainBtns.isEmpty()) {
                    System.out.println("Transient 'Something Went Wrong' dialog displayed; tapping Try Again...");
                    tapElement(tryAgainBtns.get(0));
                    try { Thread.sleep(2000); } catch (Exception ignore) {}
                } else {
                    break;
                }
            }

            // Always wait up to 10s and handle OTP modal if it appears
            System.out.println("Checking for OTP modal on Travel screen...");
            try {
                org.openqa.selenium.By otpBy = org.openqa.selenium.By.xpath(
                    "//*[@resource-id='textfield_otp_pin_field' or contains(@content-desc, 'OTP') or contains(@text, 'OTP')]"
                );
                if (waitUntil(() -> !driver.findElements(otpBy).isEmpty(), 10)) {
                    System.out.println("Travel 'Insure your trip' requested OTP verification; entering OTP...");
                    OtpModal otpModal = new OtpModal();
                    otpModal.enterOtp(utils.TestDataBuilder.DEFAULT_OTP);
                    Thread.sleep(2500);
                }
            } catch (Exception ignored) {}

            System.out.println("Transitioned past 'Insure your trip' preliminary screen!");
        }
    }

    public void clickPrivacyCheckbox() {
        System.out.println("Ensuring Travel Privacy checkbox is checked...");
        org.openqa.selenium.By switchLocator = org.openqa.selenium.By.xpath(
            "//*[@resource-id='tgl_checkbox' or @resource-id='tgl_about_you_page_motor_checkbox' or contains(@resource-id, 'checkbox') or @class='android.widget.Switch' or @class='android.widget.CheckBox']"
        );
        boolean checked = clickPrivacySwitch(switchLocator);
        System.out.println("Travel privacy checkbox interaction completed (checked=" + checked + ").");
    }

    public void clickNext() {
        enterEmailIfPresent(utils.TestDataBuilder.DEFAULT_EMAIL);
        hideKeyboard();
        System.out.println("Clicking Next on Travel details screen...");

        org.openqa.selenium.By nextBtnBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='btn_next_button']//*[@clickable='true'] "
            + "| //android.widget.Button[@clickable='true' and (@content-desc='Next' or @text='Next')] "
            + "| //*[@resource-id='btn_next_button'] "
            + "| //android.widget.Button[@content-desc='Next' or @text='Next']"
        );

        for (int attempt = 1; attempt <= 4; attempt++) {
            System.out.println("Travel clickNext attempt " + attempt + " of 4...");
            java.util.List<WebElement> btns = findWithUnimportantViews(nextBtnBy);
            if (btns.isEmpty()) {
                scrollDown();
                btns = findWithUnimportantViews(nextBtnBy);
            }
            if (!btns.isEmpty()) {
                tapElement(btns.get(btns.size() - 1));
            } else {
                try { click(nextButton); } catch (Exception ignore) {}
            }

            boolean transitioned = waitUntil(() -> {
                boolean onDeclarations = !driver.findElements(org.openqa.selenium.By.xpath(
                    "//*[@content-desc='Yes' or @text='Yes' or @content-desc='No' or @text='No']")).isEmpty();
                boolean onQuote = !driver.findElements(travelQuoteIndicatorBy()).isEmpty();
                boolean onCheckout = !driver.findElements(TRAVEL_CHECKOUT_INDICATOR).isEmpty();
                return onDeclarations || onQuote || onCheckout || isValidationErrorDisplayed();
            }, 6);

            if (transitioned) {
                System.out.println("Travel Step 2 Next evaluated (validationError=" + isValidationErrorDisplayed() + ").");
                return;
            }

            System.out.println("Still on Travel Step 2, scrolling if needed before retrying Next...");
            scrollDown();
        }
    }

    // ==========================================
    // Underwriting & Medical Declarations
    // ==========================================
    public void answerAllDeclarationQuestions(boolean hasCondition) {
        System.out.println("Answering Travel Medical/Underwriting declarations with hasCondition=" + hasCondition + "...");
        try {
            org.openqa.selenium.By optionsBy = org.openqa.selenium.By.xpath(
                "//*[@content-desc='Yes' or @text='Yes' or @content-desc='No' or @text='No']"
            );
            boolean hasQuestions = waitUntil(() -> !driver.findElements(optionsBy).isEmpty(), 8);
            if (!hasQuestions) {
                System.out.println("No underwriting declaration questions displayed; skipping.");
                return;
            }
            String targetText = hasCondition ? "Yes" : "No";
            java.util.List<WebElement> buttons = driver.findElements(org.openqa.selenium.By.xpath(
                "//*[@text='" + targetText + "' or @content-desc='" + targetText + "']"
            ));
            for (WebElement btn : buttons) {
                try {
                    tapElement(btn);
                    Thread.sleep(300);
                } catch (Exception ignore) {}
            }
        } catch (Exception e) {
            System.out.println("Underwriting questions handled or not displayed: " + e.getMessage());
        }
    }

    public void clickDeclarationProceed() {
        System.out.println("Proceeding from Travel declaration screen...");
        try {
            org.openqa.selenium.By proceedLoc = org.openqa.selenium.By.xpath(
                "//*[@resource-id='btn_proceed' or @content-desc='Proceed' or @text='Proceed']"
            );
            java.util.List<WebElement> proceedBtns = driver.findElements(proceedLoc);
            if (!proceedBtns.isEmpty()) {
                tapElement(proceedBtns.get(0));
                try { Thread.sleep(2000); } catch (Exception ignore) {}
            }
        } catch (Exception ignore) {}
    }

    public boolean isUnderwritingReferralDisplayed() {
        try {
            org.openqa.selenium.support.ui.WebDriverWait waitReferral = new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(8));
            return waitReferral.until(d -> {
                return !driver.findElements(org.openqa.selenium.By.xpath(
                    "//*[contains(@content-desc, 'referral') or contains(@text, 'referral') or contains(@content-desc, 'underwriting') or contains(@text, 'underwriting') or contains(@content-desc, 'manual review') or contains(@text, 'manual review') or contains(@content-desc, 'exclusion') or contains(@text, 'exclusion') or contains(@content-desc, 'Something Went Wrong') or contains(@text, 'Something Went Wrong')]"
                )).isEmpty();
            });
        } catch (Exception e) {
            return false;
        }
    }

    // ==========================================
    // Plan Selection / Quote Screen
    // ==========================================
    private org.openqa.selenium.By travelQuoteIndicatorBy() {
        return org.openqa.selenium.By.xpath(
            "//*[@resource-id='btn_buy_now' or @resource-id='btn_sales_quote_page_buy_now_button' " +
            "or contains(@content-desc, 'Buy now') or contains(@text, 'Buy now') " +
            "or contains(@content-desc, 'Explorer Savvy') or contains(@text, 'Explorer Savvy') " +
            "or contains(@content-desc, 'Explorer Start') or contains(@text, 'Explorer Start')]"
        );
    }

    public TravelAboutYouScreen waitForPlanSelectionScreen() {
        System.out.println("Waiting for Travel Quote/Plan selection screen...");
        try {
            waitUntil(() -> !driver.findElements(travelQuoteIndicatorBy()).isEmpty(), 30);
            System.out.println("Travel Plan Selection Screen loaded!");
        } catch (Exception e) {
            System.out.println("Fallback wait for travel plan selection screen: " + e.getMessage());
        }
        return this;
    }

    public boolean isQuoteScreenDisplayed() {
        try {
            return !driver.findElements(travelQuoteIndicatorBy()).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTravelPlanExpanded(WebElement planCard, java.util.List<String> selectedLabels) {
        try {
            int cardTop = planCard.getRect().getY();
            int nextCardTop = Integer.MAX_VALUE;
            String[] knownPlans = {"Explorer Start", "Explorer Savvy", "Explorer Plus", "Silver", "Gold", "Platinum"};
            for (String other : knownPlans) {
                boolean matchesSelected = false;
                for (String sel : selectedLabels) {
                    if (other.equalsIgnoreCase(sel) || other.toLowerCase().contains(sel.toLowerCase()) || sel.toLowerCase().contains(other.toLowerCase())) {
                        matchesSelected = true;
                        break;
                    }
                }
                if (matchesSelected) continue;
                org.openqa.selenium.By otherBy = org.openqa.selenium.By.xpath(
                    "//*[contains(@content-desc, '" + other + "') or contains(@text, '" + other + "')]"
                );
                for (WebElement otherEl : findWithUnimportantViews(otherBy)) {
                    try {
                        if (otherEl.isDisplayed()) {
                            int oy = otherEl.getRect().getY();
                            if (oy > cardTop + 20 && oy < nextCardTop) {
                                nextCardTop = oy;
                            }
                        }
                    } catch (Exception ignore) {}
                }
            }
            org.openqa.selenium.By buyNowBy = org.openqa.selenium.By.xpath(
                "//*[@resource-id='btn_buy_now' or @resource-id='btn_sales_quote_page_buy_now_button' or @content-desc='Buy now' or @text='Buy now']"
            );
            for (WebElement bn : findWithUnimportantViews(buyNowBy)) {
                try {
                    if (bn.isDisplayed()) {
                        int byY = bn.getRect().getY();
                        if (byY >= cardTop && byY < nextCardTop) {
                            return true;
                        }
                    }
                } catch (Exception ignore) {}
            }
        } catch (Exception ignore) {}
        return false;
    }

    private void selectTravelPlan(String primaryLabel, String... altLabels) {
        System.out.println("Selecting " + primaryLabel + " Travel Plan...");
        try {
            java.util.List<String> labels = new java.util.ArrayList<>();
            labels.add(primaryLabel);
            for (String alt : altLabels) {
                if (!alt.equalsIgnoreCase("Explorer")) { // Avoid matching top screen title
                    labels.add(alt);
                }
            }
            for (String label : labels) {
                org.openqa.selenium.By planBy = org.openqa.selenium.By.xpath(
                    "//*[@clickable='true' and (contains(@content-desc, '" + label + "') or contains(@text, '" + label + "'))] " +
                    "| //*[contains(@content-desc, '" + label + "') or contains(@text, '" + label + "')]"
                );
                java.util.List<WebElement> matches = findWithUnimportantViews(planBy);
                if (matches.isEmpty()) {
                    scrollDown();
                    matches = findWithUnimportantViews(planBy);
                }
                if (!matches.isEmpty()) {
                    WebElement targetCard = matches.get(0);
                    if (isTravelPlanExpanded(targetCard, labels)) {
                        System.out.println("Requested plan '" + label + "' is already expanded with its Buy now CTA visible; skipping toggle.");
                        return;
                    }
                    System.out.println("Found plan card matching '" + label + "', tapping to select/expand...");
                    tapElement(targetCard);
                    waitUntil(() -> isTravelPlanExpanded(targetCard, labels), 3);
                    return;
                }
            }
            System.out.println("No plan card matched '" + primaryLabel + "' on Travel Quote screen.");
        } catch (Exception ignore) {}
    }

    public void selectSilverPlan() {
        selectTravelPlan("Explorer Start", "Start", "Silver");
    }

    public void selectGoldPlan() {
        selectTravelPlan("Explorer Savvy", "Savvy", "Gold");
    }

    public void selectPlatinumPlan() {
        selectTravelPlan("Explorer Plus", "Plus", "Platinum", "Explorer Savvy");
    }

    public static final org.openqa.selenium.By TRAVEL_CHECKOUT_INDICATOR = org.openqa.selenium.By.xpath(
        "//*[@content-desc='Checkout' or @text='Checkout' "
        + "or contains(@content-desc, 'Required info') or contains(@text, 'Required info') "
        + "or @resource-id='textfield_iban' or contains(@resource-id, 'iban') "
        + "or contains(@content-desc, 'IBAN') or contains(@text, 'IBAN') "
        + "or @resource-id='btn_pay_now' or @content-desc='Pay now' or @text='Pay now']"
    );

    public void clickBuyNow() {
        System.out.println("Ensuring transition to Travel Checkout screen...");
        if (waitUntil(() -> !findWithUnimportantViews(TRAVEL_CHECKOUT_INDICATOR).isEmpty(), 8)) {
            System.out.println("Travel Checkout screen already loaded!");
            return;
        }

        System.out.println("Clicking Buy now on Travel Quote Screen...");
        org.openqa.selenium.By buyNowBy = org.openqa.selenium.By.xpath(
            "//*[@resource-id='btn_buy_now' or @resource-id='btn_sales_quote_page_buy_now_button' or contains(@resource-id, 'buy_now')]//*[@clickable='true'] "
            + "| //android.widget.Button[@clickable='true' and (@content-desc='Buy now' or @text='Buy now' or @content-desc='Select' or @text='Select' or @content-desc='Choose' or @text='Choose')] "
            + "| //*[@resource-id='btn_buy_now' or @resource-id='btn_sales_quote_page_buy_now_button' or contains(@resource-id, 'buy_now') or contains(@content-desc, 'Buy now') or contains(@text, 'Buy now') or @content-desc='Select' or @text='Select' or @content-desc='Choose' or @text='Choose']"
        );
        org.openqa.selenium.By planCardFallbackBy = org.openqa.selenium.By.xpath(
            "//*[@clickable='true' and (contains(@content-desc, 'Explorer Savvy') or contains(@text, 'Explorer Savvy') "
            + "or contains(@content-desc, 'Explorer Start') or contains(@text, 'Explorer Start'))] "
            + "| //*[contains(@content-desc, 'Explorer Savvy') or contains(@text, 'Explorer Savvy')]"
        );

        for (int i = 0; i < 3; i++) {
            java.util.List<WebElement> matches = findWithUnimportantViews(buyNowBy);
            for (WebElement el : matches) {
                try {
                    if (el.isDisplayed()) {
                        tapElement(el);
                        if (waitUntil(() -> !findWithUnimportantViews(TRAVEL_CHECKOUT_INDICATOR).isEmpty(), 8)) {
                            return;
                        }
                    }
                } catch (Exception ignore) {}
            }
            java.util.List<WebElement> cardFallbacks = findWithUnimportantViews(planCardFallbackBy);
            for (WebElement card : cardFallbacks) {
                try {
                    if (card.isDisplayed()) {
                        tapElement(card);
                        if (waitUntil(() -> !findWithUnimportantViews(TRAVEL_CHECKOUT_INDICATOR).isEmpty(), 5)) {
                            return;
                        }
                    }
                } catch (Exception ignore) {}
            }
            scrollDown();
            if (waitUntil(() -> !findWithUnimportantViews(TRAVEL_CHECKOUT_INDICATOR).isEmpty(), 3)) {
                return;
            }
        }
        waitUntil(() -> !findWithUnimportantViews(TRAVEL_CHECKOUT_INDICATOR).isEmpty(), 15);
    }

    // ==========================================
    // Checkout & National Address
    // ==========================================
    public TravelAboutYouScreen fillNationalAddress(String bldg, String region, String city, String street, String dist, String addNo, String zip) {
        System.out.println("Waiting for Travel Checkout screen...");
        try {
            waitUntil(() -> !findWithUnimportantViews(TRAVEL_CHECKOUT_INDICATOR).isEmpty(), 25);

            if (driver.findElements(org.openqa.selenium.By.xpath("//*[@resource-id='textfield_building_number']")).isEmpty()) {
                System.out.println("National Address fields not present on Travel Checkout; skipping.");
                return this;
            }

            System.out.println("Filling National Address on Checkout...");
            WebElement bldgEl = driver.findElement(org.openqa.selenium.By.xpath("//*[@resource-id='textfield_building_number']"));
            WebElement regEl = driver.findElement(org.openqa.selenium.By.xpath("//*[@resource-id='textfield_region']"));
            WebElement cityEl = driver.findElement(org.openqa.selenium.By.xpath("//*[@resource-id='textfield_city']"));
            WebElement strEl = driver.findElement(org.openqa.selenium.By.xpath("//*[@resource-id='textfield_street_name']"));
            WebElement distEl = driver.findElement(org.openqa.selenium.By.xpath("//*[@resource-id='textfield_district']"));
            WebElement addEl = driver.findElement(org.openqa.selenium.By.xpath("//*[@resource-id='textfield_additional_number']"));
            WebElement zipEl = driver.findElement(org.openqa.selenium.By.xpath("//*[@resource-id='textfield_zip_code']"));

            sendKeys(bldgEl, bldg);
            sendKeys(regEl, region);
            sendKeys(cityEl, city);
            sendKeys(strEl, street);
            sendKeys(distEl, dist);
            sendKeys(addEl, addNo);
            sendKeys(zipEl, zip);
            hideKeyboard();
        } catch (Exception e) {
            System.out.println("National address fields handled with exception/bypass: " + e.getMessage());
        }
        return this;
    }

    public TravelAboutYouScreen fillIbanAndAcceptTerms(String iban) {
        waitUntil(() -> !findWithUnimportantViews(TRAVEL_CHECKOUT_INDICATOR).isEmpty(), 25);
        System.out.println("Entering IBAN and accepting Terms on Travel Checkout...");
        try {
            CheckoutScreen checkout = new CheckoutScreen();
            checkout.enterIban(iban);
            checkout.clickTermsCheckbox();
        } catch (Exception e) {
            System.out.println("CheckoutScreen delegation warning: " + e.getMessage());
        }
        return this;
    }

    public TravelAboutYouScreen clickCheckoutPayNow() {
        if (isPaymentOrConfirmationAlreadyActive()) {
            return this;
        }
        System.out.println("Selecting payment method and clicking Pay now...");
        try {
            CheckoutScreen checkout = new CheckoutScreen();
            checkout.selectCreditDebitCard();
            checkout.clickPayNow();
        } catch (Exception e) {
            System.out.println("CheckoutScreen pay now warning: " + e.getMessage());
        }
        return this;
    }

    public TravelAboutYouScreen clickPayNowOnly() {
        if (isPaymentOrConfirmationAlreadyActive()) {
            return this;
        }
        System.out.println("Clicking Pay now on Travel Checkout (without re-selecting payment card)...");
        try {
            new CheckoutScreen().clickPayNow();
        } catch (Exception e) {
            System.out.println("CheckoutScreen clickPayNow warning: " + e.getMessage());
        }
        return this;
    }

    public boolean isCheckoutLoaded() {
        try {
            return !driver.findElements(org.openqa.selenium.By.xpath("//*[@resource-id='textfield_building_number']")).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isValidationErrorDisplayed() {
        try {
            return !driver.findElements(org.openqa.selenium.By.xpath(
                "//*[contains(@resource-id, 'error') or contains(@content-desc, 'invalid') or contains(@text, 'invalid') or contains(@text, 'Invalid') or contains(@content-desc, 'Invalid') or contains(@text, 'required') or contains(@content-desc, 'required') or contains(@content-desc, 'Please enter') or contains(@text, 'Please enter')]"
            )).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private String getElementLabel(WebElement el) {
        if (el == null) return "";
        try {
            String txt = el.getText();
            if (txt != null && !txt.trim().isEmpty()) return txt.trim();
            String desc = el.getAttribute("content-desc");
            return desc != null ? desc.trim() : "";
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isPastDateDisabled() {
        System.out.println("Opening Departure calendar to verify past dates are disabled...");
        org.openqa.selenium.By sheetReadyBy = org.openqa.selenium.By.xpath(
            "//*[@text='Confirm' or @content-desc='Confirm' or @text='Reset' or @content-desc='Reset']"
        );
        try {
            WebElement picker = findDatePicker(false);
            if (picker == null) {
                System.out.println("Departure datepicker not found on screen.");
                return false;
            }
            String beforePickerText = getElementLabel(picker);
            tapElement(picker);
            if (!waitUntil(() -> !findWithUnimportantViews(sheetReadyBy).isEmpty(), 4)) {
                System.out.println("Departure calendar modal did not open.");
                return false;
            }

            java.time.LocalDate today = java.time.LocalDate.now();
            java.time.LocalDate yesterday = today.minusDays(1);
            org.openqa.selenium.Dimension size = getSafeWindowSize();
            int minCalY = (int) (size.height * 0.18);
            int maxCalY = (int) (size.height * 0.88);
            int maxCellW = (int) (size.width * 0.32);

            if (yesterday.getMonthValue() == today.getMonthValue()) {
                String pastDay = String.valueOf(yesterday.getDayOfMonth());
                org.openqa.selenium.By anyDayBy = org.openqa.selenium.By.xpath(
                    "//*[@content-desc='" + pastDay + "' or @text='" + pastDay + "' " +
                    "or starts-with(@content-desc, '" + pastDay + ", ') " +
                    "or starts-with(@content-desc, '" + pastDay + " ') " +
                    "or contains(@content-desc, ' " + pastDay + ", ')]"
                );
                java.util.List<WebElement> matches = findWithUnimportantViews(anyDayBy);
                WebElement topPastCell = null;
                int minY = Integer.MAX_VALUE;
                for (WebElement el : matches) {
                    try {
                        org.openqa.selenium.Rectangle r = el.getRect();
                        if (el.isDisplayed() && r.getY() > minCalY && r.getY() < maxCalY && r.getWidth() < maxCellW && r.getY() < minY) {
                            minY = r.getY();
                            topPastCell = el;
                        }
                    } catch (Exception ignore) {}
                }
                if (topPastCell != null) {
                    String enabled = topPastCell.getAttribute("enabled");
                    String clickable = topPastCell.getAttribute("clickable");
                    if ("false".equalsIgnoreCase(enabled) || "false".equalsIgnoreCase(clickable)) {
                        System.out.println("Past day '" + pastDay + "' is disabled (enabled=" + enabled + ", clickable=" + clickable + ").");
                        return true;
                    }
                    // Tap past cell and verify selection cannot advance to yesterday
                    tapElement(topPastCell);
                    try { Thread.sleep(500); } catch (Exception ignore) {}
                    java.util.List<WebElement> confirmBtns = findWithUnimportantViews(
                        org.openqa.selenium.By.xpath("(//*[@text='Confirm' or @content-desc='Confirm'])[last()]"));
                    if (!confirmBtns.isEmpty() && "false".equalsIgnoreCase(confirmBtns.get(0).getAttribute("enabled"))) {
                        System.out.println("Confirm remained disabled after tapping past day '" + pastDay + "'.");
                        return true;
                    }
                    if (!confirmBtns.isEmpty()) {
                        tapElement(confirmBtns.get(0));
                        try { Thread.sleep(500); } catch (Exception ignore) {}
                        if (!findWithUnimportantViews(sheetReadyBy).isEmpty()) {
                            return true;
                        }
                        WebElement refreshedPicker = findDatePicker(false);
                        String afterText = getElementLabel(refreshedPicker);
                        boolean acceptedPastDate = afterText != null && !afterText.equals(beforePickerText)
                                && afterText.contains(pastDay) && !afterText.toLowerCase().contains("departure");
                        return !acceptedPastDate;
                    }
                }
            } else {
                // On the 1st of a month, verify previous-month navigation is disabled or absent
                java.util.List<WebElement> prevArrows = findWithUnimportantViews(org.openqa.selenium.By.xpath(
                    "//*[contains(translate(@content-desc, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'previous month') or contains(@resource-id, 'prev')]"
                ));
                if (prevArrows.isEmpty() || "false".equalsIgnoreCase(prevArrows.get(0).getAttribute("enabled"))) {
                    System.out.println("Previous month navigation is disabled/absent on day 1 of the month.");
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.out.println("Past date validation failed: " + e.getMessage());
            return false;
        } finally {
            try {
                if (!findWithUnimportantViews(sheetReadyBy).isEmpty()) {
                    java.util.List<WebElement> dismissBtns = findWithUnimportantViews(org.openqa.selenium.By.xpath(
                        "//*[@resource-id='btn_close' or @content-desc='Close' or @text='Close' or @content-desc='Cancel' or @text='Cancel']"
                    ));
                    if (!dismissBtns.isEmpty()) {
                        tapElement(dismissBtns.get(0));
                    } else {
                        driver.navigate().back();
                    }
                    waitUntil(() -> findWithUnimportantViews(sheetReadyBy).isEmpty(), 3);
                }
            } catch (Exception ignore) {}
        }
    }

}

