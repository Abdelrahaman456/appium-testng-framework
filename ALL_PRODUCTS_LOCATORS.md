# Tree Digital Insurance (`sa.com.tree.digital.insurance.uat`) — Complete Locator & Flutter/UiAutomator2 Reference Catalog

Comprehensive, verified locator reference for the **Tree Digital Insurance** Flutter Android application (`sa.com.tree.digital.insurance.uat`) across **Home**, **Shared Auth/Checkout/Payment Modals**, and all **9 Insurance Products**.

> [!IMPORTANT]
> **Source Evidence Distinction & Verification Scope**
> - **XML-Verified**: Attributes, `resource-id`, `content-desc`, `text`, `class`, `clickable`/`focusable`/`checkable`/`NAF`, and screen bounds (`[x1,y1][x2,y2]`) verified directly from UiAutomator2 XML hierarchy dumps (`brain\42a71681-78b0-4890-aba3-ec5cd6a83b46\**\*.xml`).
> - **Page-Object-Derived (`PO`)**: Locators extracted from `src\main\java\pages\*.java` for flows/screens without captured XML dumps (marked clearly in the `Notes / Gotchas` column). Specifically:
>   - **No XML dumps exist** for: **Home Insurance** (all screens), **Individual Health** (all screens — `landing_dump.xml` is Domestic Worker), **Motor** (`VehicleDetailsModal`, `QuoteScreen`, `CheckoutScreen`), **Travel** post-initial steps (`Single trip`/Destination/Dates/TravelerType/Declarations/Quote/Checkout), **Pet** (Declarations/Quote/Checkout), **Family Health** (Members/Declarations/Quote/Checkout), **Income** (Employment/Declarations), **Add-on/Card 3DS WebView**, and **`PolicyConfirmationScreen`**.
>   - **Class Name Mapping Note**: The Motor flow in `src\main\java\pages\` is split across `MotorCoverageSelectionScreen.java`, `AboutYouScreen.java`, `VehicleDetailsModal.java`, `QuoteScreen.java`, and `CheckoutScreen.java` (with `CheckoutScreen.autoFillCheckout` consuming `utils.TestDataBuilder.CustomerProfile`).
>   - **XML File Naming Traps Observed**:
>     - `home_tiles.xml` is the **Login** screen, not Home product tiles.
>     - `landing_dump.xml` is **Domestic Worker Insurance** landing (`Domestic Worker Insurance` / `Protect your helper!`), not Individual Health.
>     - `3ds_ui.xml` (`size=12388`, identical to `members_ui.xml`) is **Domestic Worker Member(s) Details** (`textfield_national_id` text=`'2456789011'`), not a 3DS WebView dump.
>     - `malp_landing_tree.xml` is identical to `home_fixed.xml` (**Motor Insurance** landing).
>     - `scratch/fh_landing2.xml` is identical to `scratch/pet_landing.xml` (**Pet Insurance** landing).

---

## Table of Contents

1. [Global Architecture & Critical Flutter/UiAutomator2 Interaction Rules](#1-global-architecture--critical-flutteruiautomator2-interaction-rules)
2. [Shared System, Auth, Modal & Checkout Screens](#2-shared-system-auth-modal--checkout-screens)
3. [Home Screen (9 Product Tiles + Navigation)](#3-home-screen-9-product-tiles--navigation)
4. [Product 1: Motor Insurance](#4-product-1-motor-insurance)
5. [Product 2: Travel Insurance](#5-product-2-travel-insurance)
6. [Product 3: Home Insurance](#6-product-3-home-insurance)
7. [Product 4: Pet Insurance](#7-product-4-pet-insurance)
8. [Product 5: Income Protection Insurance](#8-product-5-income-protection-insurance)
9. [Product 6: Individual Health Insurance](#9-product-6-individual-health-insurance)
10. [Product 7: Family Health Insurance](#10-product-7-family-health-insurance)
11. [Product 8: Domestic Worker Health Insurance](#11-product-8-domestic-worker-health-insurance)
12. [Product 9: Medical Malpractice Insurance](#12-product-9-medical-malpractice-insurance)

---

## 1. Global Architecture & Critical Flutter/UiAutomator2 Interaction Rules

### 1.1 Animation Scale Rule (Splash Screen Freeze)
- **`animator_duration_scale=1` is mandatory** on the test device/emulator (`adb shell settings put global animator_duration_scale 1`).
- If `animator_duration_scale` is set to `0`, the Flutter splash/intro animation never completes its ticker callback and the app freezes before reaching Home/Login.

### 1.2 Non-Clickable Outer `Button` Wrapper Pattern vs. Inner Clickable Child
Across nearly all primary CTAs, add buttons, payment selectors, and save actions, Flutter semantics emit **two or three nested nodes with identical bounds**:
- **Outer wrapper (`android.widget.Button`)**: Carries the semantic **`@resource-id`**, has **`clickable="false"`**, **`focusable="true"`**, and **`content-desc=""`** (empty).
  - Observed wrapper IDs: `btn_next_button`, `btn_next_button_product_landing_page_`, `btn_next_button_product_landing_page_motor`, `btn_about_you_page_motor_next_button`, `btn_proceed`, `btn_pay_now`, `btn_insure_now`, `btn_buy_now`, `btn_sales_quote_page_buy_now_button`, `btn_add`, `btn_save_button`, `btn_view_medical_network`, `btn_payment_mode_moyasar_direct_pay`, `btn_payment_mode_tabby`, `btn_payment_mode_tamara`, `btn_guest_login_or_register_part1`.
- **Inner semantic button/icon (`android.widget.Button` or `android.widget.ImageView`)**: Has **no `@resource-id`**, carries the visible **`@content-desc`** (`'Insure now'`, `'Next'`, `'Proceed'`, `'Buy now'`, `'Pay now'`, `'Save'`, `'Add'`, `'View Medical Network'`, `'Login'`, `'Credit/Debit Card'`, etc.), and has **`clickable="true"`**, **`focusable="true"`**.
- **Rule**: Never rely on `element.click()` against an outer `clickable="false"` wrapper ID alone if UiAutomator2 drops the click; either:
  1. Target the **inner clickable child** (`//*[@resource-id='btn_insure_now']//*[@clickable='true']` or `//android.widget.Button[@clickable='true' and @content-desc='Insure now']`), OR
  2. Locate by `@resource-id` and call **`BasePage.tapElement(el)`**, which dispatches `mobile: clickGesture` at `(x + width/2, y + height/2)` (with W3C pointer tap fallback: `50ms` move + `100ms` pause).
- Notice the helper `BasePage.tapPreferredClickable` / `tapPreferredButton` prefers the innermost clickable child when interacting with CTAs.

### 1.3 Zero `@hint` Attributes & `NAF="true"` Unlabelled Fields
- **UiAutomator2 on Flutter exports NO `@hint` attribute** across all 72 inspected XML dumps (`hint` is never present/non-empty).
- Several Page Object `@AndroidFindBy` definitions (notably in `TravelAboutYouScreen`, `HomeInsuranceScreen`, `PetScreen`, `IndividualHealthScreen`, `FamilyHealthScreen`, `IncomeInsuranceScreen`) contain legacy `contains(@hint, ...)` clauses inside compound XPaths. Those clauses never match at runtime and only succeed because of their accompanying `@resource-id` or positional `(//android.widget.EditText)[N]` fallback.
- For new locators, **never write `@hint`-only XPaths**. Use `@resource-id`, preceding/following anchor labels (`following::` / `preceding::`), or validated class indices.
- Many Flutter text fields and interactive icons also carry `NAF="true"` (Not Accessibility Friendly) with `text=""` / `content-desc=""` when empty or unlabelled (e.g., Travel DOB `ImageView`, Malpractice DOB `ImageView` & Profession `EditText`, Pet Breed/Age/City controls, Motor `about_you` Back `View`).

### 1.4 `android.widget.Switch` Toggle Behavior (`tgl_checkbox`)
- Terms & Conditions / Privacy Notice switches (`id='tgl_checkbox'`, `id='tgl_about_you_page_motor_checkbox'`, `id='tgl_checkout_page_motor_checkbox'`) and add-on switches (e.g., Family Health Maternity/Dental) always render with **`clickable="false"`**, **`checkable="true"`**, **`focusable="true"`**, **`checked="false"|"true"`**.
- **Rule**: Locate by `@resource-id` (or `//android.widget.Switch`) and tap coordinates via `tapElement(el)` (or click the switch's exact center), verifying `@checked='true'` afterward rather than gating locator discovery on `@clickable='true'`.

### 1.5 Text Input Reliability (`BasePage.sendKeys`)
- Flutter text fields can reject standard `WebElement.sendKeys()` or fail to clear pre-existing values:
  1. Check `element.getText()` first; skip typing if already equal to `text`.
  2. `click()` -> wait `250ms` -> `clear()` -> `sendKeys(text)`.
  3. Verify the first 2 characters appear in `getText()`; on mismatch, fall back to `tapElement(el)` + wait `400ms` + `driver.executeScript("mobile: type", Map.of("text", text))`.
- On Motor `AboutYouScreen`, typing is followed by `mobile: performEditorAction` (`{"action": "done"}`) and keyboard dismissal.

### 1.6 Absher OTP Modal Rule (`OtpModal.java`)
- In UAT, `textfield_otp_pin_field` frequently opens **pre-filled with 4 digits** (e.g. `text="8312"` in `otp_live.xml`), which prevents the form from auto-submitting until the Flutter `onChanged` listener fires on the 4th PIN box.
- **Required Sequence (`OtpModal.enterOtp`)**:
  1. Wait up to `45s` for `//*[@resource-id='textfield_otp_pin_field']` (`EditText`, `[93,1867][987,2025]` without IME; `[93,1163][987,1321]` with IME).
  2. **First Dynamic Tap**: Compute the 4th PIN cell center at `tapX = rect.x + (int)(rect.width * 0.875)` (`87.5%` width), `tapY = rect.y + rect.height / 2` -> execute `mobile: clickGesture` -> wait `600ms`.
  3. Re-locate `textfield_otp_pin_field` (coordinates shift upward when soft keyboard opens) -> **Second Dynamic Tap** at `87.5%` width -> wait `300ms`.
  4. Read `initialText = field.getText()`:
     - If `initialText.length() == 4` (e.g. `'8312'`): press `AndroidKey.DEL` (deletes 4th digit -> `'831'`, observed in `otp_del.xml`) -> re-type the 4th digit `initialText.substring(3, 4)` (`'2'`).
     - Otherwise (empty or fewer than 4 chars): call `typeDigits(otp)` (fallback `"1234"` via `mobile: type` with per-character `AndroidKey.DIGIT_0..9` fallback).
  5. If a `Verify` / `Confirm` button (`btn_verify_otp` / `btn_verify`) is visible, click it; settle `4s`.
- Note on Resend (`btn_otp_resend`): displays countdown `desc="Send again if you didn't receive in (67s)"` with `clickable="false"` while counting down, becoming `clickable="true"` with `desc="Send again if you didn't receive in "` once elapsed (`otp_dump.xml`, `otp_del.xml`).

### 1.7 DOB Month/Year `SeekBar` Picker Rule (`BasePage.scrollDobWheelAndConfirm`)
- Triggering a Month/Year DOB `ImageView` opens a two-column Flutter Cupertino-style wheel dialog containing two `android.widget.SeekBar` wheels (`[221,979][604,1373]` Year & `[604,979][860,1373]` Month in `malp_btn_dump.xml`, `picker.xml`, `income_dob.xml`) behind a full-screen `View[@content-desc='Dismiss']` scrim (`[0,0][1080,2337]`).
- **Required Gesture Rule**:
  1. `hideKeyboard()` -> `tapElement(dobInput)` -> wait `1500ms`.
  2. **`BasePage.scrollWheel(startX, startY, endY)`**: Uses a **`1000ms` W3C `pointerMove`** plus a **`200ms Pause` before `pointerUp`** (prevents fling momentum from overshooting the target year/month) + **`1000ms` settle**.
  3. **Standard Wheel Coordinates (`BasePage`)**: `wheelX = (int)(size.width * 0.35)` (Year wheel), drag **`0.45 * height` -> `0.65 * height`** (scrolls downward to earlier years).
  4. **Product Variant (`AboutYouScreen` Motor)**: Uses **`0.72 * width`** for the Month wheel (`0.54 * height` -> `0.48 * height`) and selects `(//android.widget.Button[@content-desc="Confirm"])[last()]`.
  5. **Card Expiry Variant (`AddNewCardModal`)**: `(//android.widget.SeekBar)[1]` (Year) & `(//android.widget.SeekBar)[2]` (Month, swipes DOWN to `'01'`/`'Jan'`), then clicks `Confirm` / `Done` / `Select`.
  6. Always locate the dialog action via `(//*[@text='Confirm' or @content-desc='Confirm' or @text='Done' or @content-desc='Done'])[last()]` (`[643,1373][860,1499]`, `clickable="true"`).

### 1.8 Shared Checkout, `AddNewCardModal`, `3DS`, `PolicyConfirmationScreen`, `National Address` & IBAN
- **`textfield_iban` Rule**: Always **strip the leading `'SA'` prefix** (`iban.replaceFirst("(?i)^SA", "")`) before typing — the field expects **strictly 22 numeric digits** because `'SA'` is rendered externally by the UI.
- **Exact Typo in Details Button**: In Domestic Worker (`dw_checkout.xml`), Income (`income_checkout.xml`), and Malpractice (`malp_checkout_dump.xml`), the Checkout price/policy details link is literally `id='btn_detials_btn'` (`ImageView`, `desc='Details'`). Always include `@content-desc='Details'` in fallback XPaths.
- **National Address (7 Fields)**: Present on Checkout for Travel, Home, Pet, Individual Health, Family Health, and Domestic Worker. **Absent in captured UAT XML dumps for Income (`income_checkout.xml`) and Malpractice (`malp_checkout_dump.xml`)** (handled conditionally in POs).
- **Popup/ANR Interceptor (`BasePage.POPUP_QUEUE`)**: Background/poll queue dismisses Android System ANR (`android:id/aerr_wait` / `'Wait'`), runtime permissions (`permission_allow_button`), `'Not now'`, and `'Dismiss'` dialogs.

### 1.9 Test Identity Tuples & Data Rules
| Scope | ID / IQAMA | Mobile (9 digits, no leading `0`/`+966`) | DOB / Year | Email | Additional Observed / PO Rules |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Individual / Health / Income Primary Tuple** | `1313424273` | `500421222` | `1998` | `qa@tree.com.sa` (observed in `income_about*.xml`) | Used across Individual Health & Income (`income_about*.xml` also showed `11/2008` in DOB field). |
| **Domestic Worker Sponsor (XML Observed)** | `1000000001` | `501234567` | `11/2008` | `qa.tester@tree.com.sa` | Observed in `sponsor_screen.xml` / `sp_dump.xml`. |
| **Domestic Worker Helper Member (XML Observed)** | `2456789011` | — | — | — | Observed entered in `3ds_ui.xml` / `members_ui.xml`. |
| **Motor Vehicle Inputs (`AboutYouScreen` & `VehicleDetailsModal`)** | — | — | — | — | Vehicle Sequence Number (`textfield_about_you_page_motor_sequence_number`), Custom Card (`textfield_about_you_page_motor_custom_card` + `Car model year`), and Seller ID (`textfield_about_you_page_motor_seller_national_id`) values are supplied externally (e.g. `TestDataBuilder`, unread per scope constraint). In `VehicleDetailsModal`, Make types `"b.m.w"` -> selects `B.M.W` / `BMW` at `Y > 1000`; Model picks `"3"` / `"Series"` at `r.getY() >= modelBottom - 20 && r.getY() < 2100`; verifies `Proceed` (`enabled=true && clickable=true`). |
| **Declaration Helper (`BasePage.answerMemberDeclarations`)** | — | — | — | — | Uses `btn_no_button` / `btn_yes_button` / `No` / `Yes`. When `answerYes == true`, taps `yesBtns.get(0)` for Q1 then taps remaining `noBtns` from index `1..` (PEP / Related Party). |
| **Customer Profile Helper (`CheckoutScreen.autoFillCheckout`)** | — | — | — | — | Consumes `utils.TestDataBuilder.CustomerProfile` (passed from test layer). |

---

## 2. Shared System, Auth, Modal & Checkout Screens

### 2.1 System ANR / Permission Popups & Login Screen (`anr.xml`, `BasePage.java`, `home_tiles.xml`)

| Screen / Step | Element / Field | Exact `resource-id` | Exact `content-desc` / `text` / `class` | `clickable` / `focusable` | Recommended Dynamic Locator (XPath) | Notes / Gotchas |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| System Popup | ANR Dialog Title | `android:id/alertTitle` | `text="Process system isn't responding"` / `TextView` | `false` / `false` | `//android.widget.TextView[@resource-id='android:id/alertTitle']` | XML (`anr.xml`). System dialog blocking Flutter overlay. |
| System Popup | Wait (Dismiss ANR) | `android:id/aerr_wait` | `text="Wait"` / `Button` | `true` / `true` | `//*[@resource-id='android:id/aerr_wait' or @text='Wait' or @content-desc='Wait']` | XML (`anr.xml`) `[70,1332][1010,1458]`. Handled by `BasePage.POPUP_QUEUE`. |
| System Popup | Close App (Avoid) | `android:id/aerr_close` | `text="Close app"` / `Button` | `true` / `true` | `//android.widget.Button[@resource-id='android:id/aerr_close']` | XML (`anr.xml`) `[70,1206][1010,1332]`. Do not click; kills AUT. |
| System Popup | Permission Allow / Dismiss / Not now | `permission_allow_button` (substring) | `Not now`, `Dismiss` / `Button` | `PO` | `//*[contains(@resource-id, 'permission_allow_button')] \| //android.widget.Button[@content-desc='Not now' or @text='Not now' or @content-desc='Dismiss' or @text='Dismiss']` | PO (`BasePage.POPUP_QUEUE`). |
| Login (`home_tiles.xml`) | Header / Back (`Login`) | `""` | `desc="Login"` / `ImageView` | `true` / `true` | `//android.widget.ImageView[@clickable='true' and @content-desc='Login']` | XML (`home_tiles.xml`) `[42,149][296,233]`. Note: file `home_tiles.xml` is Login, not Home. |
| Login (`home_tiles.xml`) | National ID / Iqama | `textfield_national_id` | `text=""`, `desc=""` / `EditText` | `true` / `true` | `//android.widget.EditText[@resource-id='textfield_national_id']` | XML (`home_tiles.xml`) `[76,710][1004,796]`, `NAF="true"`. |
| Login (`home_tiles.xml`) | Month & Year of Birth | `textfield_month_year_of_birth_txt` | `text=""`, `desc=""` / `ImageView` | `true` / `true` | `//*[@resource-id='textfield_month_year_of_birth_txt']` | XML (`home_tiles.xml`) `[76,877][1004,962]`, `NAF="true"`. Opens SeekBar DOB picker. |
| Login (`home_tiles.xml`) | Update Mobile Number Link | `""` | `desc="Update mobile number"` / `View` | `true` / `true` | `//android.view.View[@content-desc='Update mobile number']` | XML (`home_tiles.xml`) `[42,1064][439,1111]`. |
| Login (`home_tiles.xml`) | Login CTA (Outer Wrapper) | `btn_guest_login_or_register_part1` | `desc=""` / `Button` | `false` / `true` | `//*[@resource-id='btn_guest_login_or_register_part1']//android.widget.Button[@clickable='true']` | XML (`home_tiles.xml`) `[42,1274][1038,1389]`. Outer wrapper is `clickable=false`. |
| Login (`home_tiles.xml`) | Login CTA (Inner Clickable) | `""` | `desc="Login"` / `Button` | `true` / `true` | `//android.widget.Button[@clickable='true' and @content-desc='Login']` | XML (`home_tiles.xml`) `[42,1274][1038,1389]`. |
| Login (`home_tiles.xml`) | Sign Up Link | `""` | `desc="Don’t have an account? Sign up"` / `View` | `true` / `true` | `//android.view.View[contains(@content-desc, 'Sign up')]` | XML (`home_tiles.xml`) `[275,1470][805,1518]`. Uses curly apostrophe `’`. |

### 2.2 DOB Month/Year SeekBar Picker & Absher OTP Modal (`malp_btn_dump.xml`, `picker.xml`, `income_dob.xml`, `otp*.xml`, `OtpModal.java`)

| Screen / Step | Element / Field | Exact `resource-id` | Exact `content-desc` / `text` / `class` | `clickable` / `focusable` | Recommended Dynamic Locator (XPath) | Notes / Gotchas |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| DOB Picker Modal | Dismiss Scrim | `""` | `desc="Dismiss"` / `View` | `true` / `true` | `//android.view.View[@content-desc='Dismiss']` | XML `[0,0][1080,2337]`. Tapping outside closes picker. |
| DOB Picker Modal | Year SeekBar Wheel | `""` | `desc="<year>"` (e.g. `2008`) / `SeekBar` | `false` / `true` | `(//android.widget.SeekBar)[1]` | XML `[221,979][604,1373]`. Scroll via `BasePage.scrollWheel` (`1000ms` move + `200ms` pause) at `wheelX = 0.35 * width`, `0.45*h -> 0.65*h`. |
| DOB Picker Modal | Month SeekBar Wheel | `""` | `desc="<Mon>"` (e.g. `Nov`) / `SeekBar` | `false` / `true` | `(//android.widget.SeekBar)[2]` | XML `[604,979][860,1373]`. `AboutYouScreen` (Motor) scrolls month at `0.72 * width`. |
| DOB Picker Modal | Cancel Button | `""` | `desc="Cancel"` / `Button` | `true` / `true` | `(//android.widget.Button[@content-desc='Cancel'])[last()]` | XML `[450,1373][643,1499]`. |
| DOB Picker Modal | Confirm Button | `""` | `desc="Confirm"` / `Button` | `true` / `true` | `(//*[@text='Confirm' or @content-desc='Confirm' or @text='Done' or @content-desc='Done'])[last()]` | XML `[643,1373][860,1499]`. Always use `[last()]` so modal button wins over background screen nodes. |
| OTP Modal | Scrim Backdrop | `""` | `desc="Scrim"` / `View` | `true` / `true` | `//android.view.View[@content-desc='Scrim']` | XML `[0,0][1080,1425]` (no IME) or `[0,0][1080,721]` (with IME). |
| OTP Modal | Modal Title & Subtitle | `""` | `desc="We've sent you a 4-digit OTP"` (`View`) / `desc="Enter the OTP sent to your mobile number registered with Absher"` (`ImageView`) | `false` / `true` | `//android.view.View[contains(@content-desc, '4-digit OTP')]` | XML (`otp_live.xml`, `otp.xml`). Informational wait anchor. |
| OTP Modal | 4-Digit OTP PIN Field | `textfield_otp_pin_field` | `text="8312"` (live pre-fill) / `"831"` (`otp_del.xml`) / `""` (`otp.xml`) / `EditText` | `true` / `true` | `//*[@resource-id='textfield_otp_pin_field']` | XML `[93,1867][987,2025]` (`[93,1163][987,1321]` IME open). **Tap 4th box at `87.5%` width twice dynamically**, send `DEL` if `len==4`, re-type 4th digit (or fallback `1234`). |
| OTP Modal | Resend Countdown / CTA | `btn_otp_resend` | `desc="Send again if you didn't receive in (67s)"` (`clk=false`) or `"Send again if you didn't receive in "` (`clk=true`) / `Button` | `false` -> `true` / `true` | `//*[@resource-id='btn_otp_resend']` | XML (`otp_live.xml`, `otp_dump.xml`, `otp_del.xml`). Becomes `clickable="true"` only after timer reaches 0. |
| OTP Modal | Verify / Confirm Fallback | `btn_verify_otp` / `btn_verify` | `desc`/`text` = `Verify` or `Confirm` / `Button` | `PO` | `//*[@resource-id='btn_verify_otp' or @resource-id='btn_verify' or @content-desc='Verify' or @text='Verify' or @content-desc='Confirm' or @text='Confirm']` | PO (`OtpModal.java`). Usually auto-submits once 4th digit is re-entered; clicked if still visible. |

### 2.3 Shared Checkout, National Address, `AddNewCardModal`, `3DS` & `PolicyConfirmationScreen`

> [!NOTE]
> `AddNewCardModal` differs between XML (`scratch/income_pay_modal.xml`) and PO (`AddNewCardModal.java`): XML uses short IDs (`textfield_card_number`, `btn_save_button`), whereas `AddNewCardModal.java` uses `_credit_debit_card_bottom_sheet_` IDs with substring fallbacks. Recommend compound XPaths covering both.

| Screen / Step | Element / Field | Exact `resource-id` | Exact `content-desc` / `text` / `class` | `clickable` / `focusable` | Recommended Dynamic Locator (XPath) | Notes / Gotchas |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| Shared Checkout | Back Button | `btn_back` | `""` / `Button` | `true` / `true` | `//android.widget.Button[@resource-id='btn_back']` | XML `[50,136][134,262]`, `NAF="true"`. |
| Shared Checkout | Details Link | `btn_detials_btn` | `desc="Details"` / `ImageView` | `true` / `true` | `//*[@resource-id='btn_detials_btn' or @content-desc='Details']` | XML `[794,414][996,477]`. **Exact APK typo**: `detials` (not `details`). |
| Shared Checkout (Nat. Addr) | Building Number | `textfield_building_number` | `EditText` | `true` / `true` | `//android.widget.EditText[@resource-id='textfield_building_number']` | XML (`dw_checkout.xml`) / PO. `NAF="true"`. Scroll into `ScrollView` if off-screen. **Absent in captured Income & Malpractice checkout XMLs.** |
| Shared Checkout (Nat. Addr) | Region | `textfield_region` | `EditText` | `true` / `true` | `//android.widget.EditText[@resource-id='textfield_region']` | XML (`dw_checkout.xml`) / PO. `NAF="true"`. Note: Domestic Worker / Travel / Pet / Home / Health only. |
| Shared Checkout (Nat. Addr) | City | `textfield_city` | `EditText` | `true` / `true` | `//android.widget.EditText[@resource-id='textfield_city']` | XML (`dw_checkout.xml`) / PO. `NAF="true"`. |
| Shared Checkout (Nat. Addr) | Street Name | `textfield_street_name` | `EditText` | `true` / `true` | `//android.widget.EditText[@resource-id='textfield_street_name']` | XML (`dw_checkout.xml`) / PO. `NAF="true"`. |
| Shared Checkout (Nat. Addr) | District | `textfield_district` | `EditText` | `true` / `true` | `//android.widget.EditText[@resource-id='textfield_district']` | XML (`dw_checkout.xml`) / PO. `NAF="true"`. |
| Shared Checkout (Nat. Addr) | Additional Number | `textfield_additional_number` | `EditText` | `true` / `true` | `//android.widget.EditText[@resource-id='textfield_additional_number']` | XML (`dw_checkout.xml`) / PO. `NAF="true"`. |
| Shared Checkout (Nat. Addr) | Postal / ZIP Code | `textfield_zip_code` | `EditText` | `true` / `true` | `//android.widget.EditText[@resource-id='textfield_zip_code']` | XML (`dw_checkout.xml`) / PO. `NAF="true"`. |
| Shared Checkout (Payment) | Credit/Debit Card Option | `btn_payment_mode_moyasar_direct_pay` (outer) | Outer `Button` `desc=""` -> Inner `ImageView` `desc="Credit/Debit Card"` | Outer `false`/Inner `true` / `true` | `//*[@resource-id='btn_payment_mode_moyasar_direct_pay']//*[@clickable='true'] \| //android.widget.ImageView[@content-desc='Credit/Debit Card']` | XML (`income_checkout.xml`, `malp_checkout_dump.xml`). Selecting opens `AddNewCardModal` when paying or clicking card entry. |
| Shared Checkout (Payment) | Tabby Option | `btn_payment_mode_tabby` (outer) | Outer `Button` -> Inner `ImageView` `desc="Tabby\nSplit up to 12"` | Outer `false`/Inner `true` / `true` | `//*[@resource-id='btn_payment_mode_tabby']//*[@clickable='true'] \| //android.widget.ImageView[contains(@content-desc, 'Tabby')]` | XML (`income_checkout.xml`, `malp_checkout_dump.xml`). |
| Shared Checkout (Payment) | Tamara Option | `btn_payment_mode_tamara` (outer) | Outer `Button` -> Inner `ImageView` `desc="Tamara\nSplit up to 4"` | Outer `false`/Inner `true` / `true` | `//*[@resource-id='btn_payment_mode_tamara']//*[@clickable='true'] \| //android.widget.ImageView[contains(@content-desc, 'Tamara')]` | XML (`income_checkout.xml`, `malp_checkout_dump.xml`). |
| Shared Checkout | IBAN Field | `textfield_iban` | `EditText` | `true` / `true` | `//android.widget.EditText[@resource-id='textfield_iban']` | XML (`dw_checkout.xml`, `income_checkout.xml`, `malp_checkout_dump.xml`). `NAF="true"`. **MUST strip `'SA'` prefix** (22 digits only). |
| Shared Checkout | Terms & Conditions Switch | `tgl_checkbox` | `Switch` | `false` / `true` | `//android.widget.Switch[@resource-id='tgl_checkbox']` | XML. `checkable="true"`, `checked="false"`. Tap via `tapElement` (center coordinates). |
| Shared Checkout | T&C & Exclusions Link | `""` | `desc="Terms & conditions and Exclusions"` / `View` | `true` / `true` | `//android.view.View[contains(@content-desc, 'Terms & conditions')]` | XML. Tapping opens policy T&C sheet instead of toggling checkbox. |
| Shared Checkout | Pay Now CTA | `btn_next_button` -> `btn_pay_now` (outers) | Inner `Button` `desc="Pay now"` | Outer `false`/Inner `true` / `true` | `(//*[@resource-id='btn_pay_now']//*[@clickable='true' and @content-desc='Pay now'] \| //android.widget.Button[@clickable='true' and @content-desc='Pay now'])[last()]` | XML. 3-level nested Button hierarchy (`btn_next_button` -> `btn_pay_now` -> `desc='Pay now'`). |
| `AddNewCardModal` | Scrim / Header | `""` | `desc="Scrim"` (`View`), `desc="Add new card"` (`View`) | `true`/`false` / `true` | `//android.view.View[@content-desc='Add new card']` | XML (`income_pay_modal.xml`) Scrim `[0,0][1080,1393]`, Title `[105,1504][513,1585]`. |
| `AddNewCardModal` | Card Number | XML: `textfield_card_number`<br>PO: `textfield_credit_debit_card_bottom_sheet_card_number` | `EditText` | `true` / `true` | `//android.widget.EditText[@resource-id='textfield_card_number' or @resource-id='textfield_credit_debit_card_bottom_sheet_card_number' or contains(@resource-id, 'card_number')]` | XML `[139,1673][941,1759]`, `NAF="true"`. |
| `AddNewCardModal` | Expiry Date Picker | XML: `textfield_expiry_date`<br>PO: `textfield_credit_debit_card_bottom_sheet_expiry_date` | `ImageView` | `true` / `true` | `//*[@resource-id='textfield_expiry_date' or @resource-id='textfield_credit_debit_card_bottom_sheet_expiry_date' or contains(@resource-id, 'expiry_date')]` | XML `[139,1833][495,1919]`, `NAF="true"`. Opens `(//android.widget.SeekBar)[1]` (Year) & `[2]` (Month, swipe DOWN to `'01'`/`'Jan'`) + Confirm/Done/Select. |
| `AddNewCardModal` | CVV | XML: `textfield_cvv`<br>PO: `textfield_credit_debit_card_bottom_sheet_cvv` | `EditText` | `true` / `true` | `//android.widget.EditText[@resource-id='textfield_cvv' or @resource-id='textfield_credit_debit_card_bottom_sheet_cvv' or contains(@resource-id, 'cvv')]` | XML `[585,1833][941,1919]`, `NAF="true"`. |
| `AddNewCardModal` | Card Holder Name | XML: `textfield_card_holder_name`<br>PO: `textfield_credit_debit_card_bottom_sheet_card_holder_name` | `EditText` | `true` / `true` | `//android.widget.EditText[@resource-id='textfield_card_holder_name' or @resource-id='textfield_credit_debit_card_bottom_sheet_card_holder_name' or contains(@resource-id, 'card_holder_name')]` | XML `[139,1993][941,2079]`, `NAF="true"`. |
| `AddNewCardModal` | Save Button | XML: `btn_save_button` (outer) -> `Button[@content-desc='Save']`<br>PO: `btn_credit_debit_card_bottom_sheet_save` | Inner `Button` `desc="Save"` | Outer `false`/Inner `true` / `true` | `//*[@resource-id='btn_save_button']//*[@clickable='true'] \| //*[@resource-id='btn_credit_debit_card_bottom_sheet_save'] \| //android.widget.Button[@clickable='true' and @content-desc='Save']` | XML `[105,2167][975,2282]`. Use inner `clickable="true"` `Save` button or `tapElement` on wrapper. |
| `3DS` (`PolicyConfirmationScreen`) | 3DS / ACS Page Container | — | `ACS Emulator` / `Authentication Result` / `Payment` / `WebView` | `PO` | `//*[contains(@text, 'ACS Emulator') or contains(@content-desc, 'ACS Emulator') or contains(@text, 'Authentication Result') or contains(@text, 'Payment') or contains(@class, 'WebView')]` | PO-derived (`PolicyConfirmationScreen.java`; no 3DS XML exists — `3ds_ui.xml` is DW Members). |
| `3DS` (`PolicyConfirmationScreen`) | 3DS Submit Button | `submit` (fallback) | `Submit` / `Button` | `PO` | `//*[@text='Submit' or @content-desc='Submit' or contains(@text, 'Submit') or contains(@content-desc, 'Submit') or @resource-id='submit']` | PO (`PolicyConfirmationScreen.java`). Fallback checks generic `//android.widget.Button`. |
| `PolicyConfirmationScreen` | Confirmation Header | `*confirmation*` (substring) | `'ready to roll'` / `'All set!'` / `'Policy number'` / `'Payment successful'` / `'Policy'` / `'Success'` | `PO` | `//*[contains(@text, 'ready to roll') or contains(@content-desc, 'ready to roll') or contains(@text, 'All set!') or contains(@content-desc, 'All set!') or contains(@text, 'Policy number') or contains(@content-desc, 'Policy number') or contains(@text, 'Payment successful') or contains(@content-desc, 'Payment successful') or contains(@resource-id, 'confirmation')]` | PO (`PolicyConfirmationScreen.java`). Polls up to `max(90, TestConfig.policyTimeout())` seconds. |
| `PolicyConfirmationScreen` | Policy Number Text | `""` | `contains 'Policy number'` | `PO` | `//*[contains(@text, 'Policy number') or contains(@content-desc, 'Policy number')]` | PO (`PolicyConfirmationScreen.java`). |
| `PolicyConfirmationScreen` | View Policy Button | `""` | `View Policy` | `PO` | `//*[contains(@content-desc, 'View Policy') or contains(@text, 'View Policy')]` | PO (`PolicyConfirmationScreen.java`). |
| `PolicyConfirmationScreen` | Close / Dismiss Button | `btn_close` (fallback) | `Close` or 1st `ImageView` in `android:id/content` | `PO` | `//*[contains(@content-desc, 'Close') or contains(@text, 'Close')] \| //*[@resource-id='btn_close'] \| //android.widget.FrameLayout[@resource-id='android:id/content']//android.widget.ImageView[1]` | PO (`PolicyConfirmationScreen.java`). |

---

## 3. Home Screen (9 Product Tiles + Navigation)

Verified from **`home_dump.xml`**, **`home_now.xml`**, **`fh_dump.xml`**, **`cur.xml`**, **`sponsor_no_anim.xml`**, and **`HomeScreen.java`**.

> [!WARNING]
> **Home Product Tiles Class & `content-desc` Mutation Gotcha**
> Depending on whether promotional/installment badges load dynamically on Home, tiles for **Motor**, **Travel**, and **Pet** switch element class between **`android.widget.ImageView`** (`clickable="true"`) and **`android.widget.Button`** (`clickable="true"`), and append badge strings (e.g., `'Motor\nSplit up to 12'`, `'Travel\nGroup and family\nInsurance'`, `'Pet\nWe cover vaccines!\nInsurance'`). **Always locate Home product tiles by exact `@resource-id`** rather than class or exact `@content-desc`!

| Screen / Step | Element / Field | Exact `resource-id` | Exact `content-desc` / `text` / `class` | `clickable` / `focusable` | Recommended Dynamic Locator (XPath) | Notes / Gotchas |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| Home Header | Login Pill | `""` | `desc="Login"` / `View` | `true` / `true` | `//android.view.View[@content-desc='Login']` | XML (`home_dump.xml`) `[852,149][1038,244]`. `HomeScreen.java`: `accessibility = "Login"`. |
| Home Filter Tabs | Individual Filter | `""` | `desc="Individual"` / `ImageView` | `true` / `true` | `//android.widget.ImageView[@content-desc='Individual']` | XML `[42,407][354,491]`. |
| Home Filter Tabs | SMEs Filter | `""` | `desc="SMEs"` / `ImageView` | `true` / `true` | `//android.widget.ImageView[@content-desc='SMEs']` | XML `[384,407][696,491]`. |
| Home Filter Tabs | All products Filter | `""` | `desc="All products"` / `ImageView` | `true` / `true` | `//android.widget.ImageView[@content-desc='All products']` | XML `[726,407][1038,491]`. Default shows all 9 tiles. |
| Home Product Tile 1 | **Motor Insurance** | `btn_home_product_motor` | `'Motor\nInsurance'` (`ImageView`), `'Motor\nSplit up to 12'` (`Button`), `'Motor\nSplit up to 12\nInsurance'` (`Button`) | `true` / `true` | `//*[@resource-id='btn_home_product_motor']` | XML `[42,545][353,847]`. Class & `desc` change dynamically when promo badge renders. |
| Home Product Tile 2 | **Individual Health** | `btn_home_product_individualHealthInsurance` | `desc="Individual\nHealth Insurance"` / `ImageView` | `true` / `true` | `//*[@resource-id='btn_home_product_individualHealthInsurance']` | XML `[385,545][696,847]`. Note `Insurance` suffix in ID! |
| Home Product Tile 3 | **Family Health** | `btn_home_product_familyHealth` | `desc="Family\nHealth Insurance"` / `ImageView` | `true` / `true` | `//*[@resource-id='btn_home_product_familyHealth']` | XML `[727,545][1038,847]`. |
| Home Product Tile 4 | **Travel Insurance** | `btn_home_product_travel` | `'Travel\nInsurance'` (`ImageView`), `'Travel\nGroup and family'` (`Button`), `'Travel\nGroup and family\nInsurance'` (`Button`) | `true` / `true` | `//*[@resource-id='btn_home_product_travel']` | XML `[42,878][353,1180]`. Class & `desc` mutate with badge. |
| Home Product Tile 5 | **Home Insurance** | `btn_home_product_homeInsurance` | `desc="Home\nInsurance"` / `ImageView` | `true` / `true` | `//*[@resource-id='btn_home_product_homeInsurance']` | XML `[385,878][696,1180]`. |
| Home Product Tile 6 | **Domestic Worker** | `btn_home_product_domesticWorker` | `desc="Domestic Worker\nHealth Insurance"` / `ImageView` | `true` / `true` | `//*[@resource-id='btn_home_product_domesticWorker']` | XML `[727,878][1038,1180]`. |
| Home Product Tile 7 | **Malpractice** | `btn_home_product_medical` | `desc="Malpractice\nMedical Insurance"` / `ImageView` | `true` / `true` | `//*[@resource-id='btn_home_product_medical']` | XML `[42,1212][353,1514]`. ID uses `medical`, not `malpractice`. |
| Home Product Tile 8 | **Income Insurance** | `btn_home_product_income` | `desc="Income\nInsurance"` / `ImageView` | `true` / `true` | `//*[@resource-id='btn_home_product_income']` | XML `[385,1212][696,1514]`. |
| Home Product Tile 9 | **Pet Insurance** | `btn_home_product_pet` | `'Pet\nInsurance'` (`ImageView`), `'Pet\nWe cover vaccines!\nInsurance'` (`Button`) | `true` / `true` | `//*[@resource-id='btn_home_product_pet']` | XML `[727,1212][1038,1514]`. Class & `desc` mutate with badge. |
| Home Carousel | Promotional Carousel Banners | `""` | `ImageView` | `true` / `true` | `//android.widget.ImageView[@clickable='true' and @focusable='true']` | XML (`home_dump.xml`, etc.). Clickable promo `ImageView` carousel items outside grid/bottom nav. |
| Home Quick Actions | New Insurance | `btn_home_quick_action_newInsurance` | `desc="New Insurance"` | `true` / `true` | `//*[@resource-id='btn_home_quick_action_newInsurance']` | XML & `HomeScreen.java`. Requires scroll if below fold. |
| Home Quick Actions | File a claim | `btn_home_quick_action_submitClaim` | `desc="File a claim"` | `true` / `true` | `//*[@resource-id='btn_home_quick_action_submitClaim']` | XML & `HomeScreen.java`. |
| Home Quick Actions | File a complaint | `btn_home_quick_action_fileComplaint` | `desc="File a complaint"` | `true` / `true` | `//*[@resource-id='btn_home_quick_action_fileComplaint']` | XML & `HomeScreen.java`. |
| Home Bottom Nav | Bottom Nav Tabs (5) | `""` | `desc="Home"`, `"My Policies"`, `"Explore"`, `"Support"`, `"My profile"` / `ImageView` | `true` / `true` | `//android.widget.ImageView[@clickable='true' and @content-desc='Home']` | XML (`home_dump.xml`, `HomeScreen.java`). Replace `'Home'` with desired tab `content-desc`. |

---

## 4. Product 1: Motor Insurance

**Sources**: `MotorCoverageSelectionScreen.java`, `AboutYouScreen.java`, `VehicleDetailsModal.java`, `QuoteScreen.java`, `CheckoutScreen.java` + XML dumps `home_fixed.xml` (duplicate `malp_landing_tree.xml`) and `dump.xml`.

> [!WARNING]
> **Motor-Specific Gotchas**
> 1. **Ownership Transfer Tab 3-Second Flicker Bug (`AboutYouScreen`)**: Tapping `tgl_about_you_page_motor_ownershipTransfer` triggers a ~3s Flutter state rebuild/flicker that can drop the selection — **click it twice**.
> 2. **Radio Self-Verification (`AboutYouScreen`)**: Clicking `radio_btn_about_you_page_motor_sequence_number` or `radio_btn_about_you_page_motor_custom_card` requires checking `selected`/`checked` state and **re-tapping** if Flutter drops the first radio transition.
> 3. **Seller National ID DOM Leak**: When switching away from *Ownership Transfer*, `textfield_about_you_page_motor_seller_national_id` can remain in the accessibility tree — always exclude `seller` when locating the buyer's `national_id`.
> 4. **Motor Back Button (`dump.xml`)**: Uses a plain unlabelled `android.view.View[@clickable='true']` at `[50,136][134,262]` with **NO `btn_back` resource-id**.
> 5. **Motor Month Wheel**: `AboutYouScreen` scrolls the Month wheel at **`0.72 * width`** (`0.54*h -> 0.48*h`) and finishes with `(//android.widget.Button[@content-desc="Confirm"])[last()]`.
> 6. **Quote & Checkout Naming**: Motor suffixes nearly every ID with `_page_motor_` (`btn_quote_page_motor_buy_now`, `btn_checkout_page_motor_pay_now`, `textfield_checkout_page_motor_iban`, `tgl_checkout_page_motor_checkbox`).

| Screen / Step | Element / Field | Exact `resource-id` | Exact `content-desc` / `text` / `class` | `clickable` / `focusable` | Recommended Dynamic Locator (XPath) | Notes / Gotchas |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| Landing (`home_fixed.xml`) | Screen Header / Back | `""` | `desc="Motor Insurance"` / `View` | `true` / `true` | `//android.view.View[@content-desc='Motor Insurance']` | XML (`home_fixed.xml`). |
| Landing (`home_fixed.xml`) | Coverage Comparison Tabs | `""` | `desc="Full Coverage"`, `desc="Third-party"` / `Button` | `true` / `true` | `//android.widget.Button[@content-desc='Full Coverage']` / `[@content-desc='Third-party']` | XML (`home_fixed.xml`) `[63,815][540,899]` & `[540,815][1017,899]`. |
| Landing (`home_fixed.xml`) | Learn More Button | `btn_learn_more_button_product_landing_page_motor` | `desc="Learn More"` / `Button` | `true` / `true` | `//android.widget.Button[@resource-id='btn_learn_more_button_product_landing_page_motor']` | XML (`home_fixed.xml`). Clickable directly. |
| Landing (`home_fixed.xml`) | Insure Now CTA | `btn_next_button_product_landing_page_motor` -> `btn_insure_now` (outers) | Inner `Button` `desc="Insure now"` | Outer `false`/Inner `true` / `true` | `//*[@resource-id='btn_insure_now']//*[@clickable='true' and @content-desc='Insure now'] \| //android.widget.Button[@clickable='true' and @content-desc='Insure now']` | XML (`home_fixed.xml`) + `MotorCoverageSelectionScreen.java` (also checks `btn_next_button_product_landing_page_`). Outer wrappers are `clickable=false`. |
| About You (`dump.xml`) | Back Button | `""` | `desc=""`, `text=""` / `View` | `true` / `true` | `(//android.view.View[@clickable='true' and @NAF='true'])[1]` | XML (`dump.xml`) `[50,136][134,262]`. Missing `btn_back` ID. |
| About You (`dump.xml`) | New Insurance Tab | `tgl_about_you_page_motor_newInsurance` | `desc="New Insurance"` / `Button` | `true` / `true` | `//*[@resource-id='tgl_about_you_page_motor_newInsurance']` | XML (`dump.xml`) & `AboutYouScreen.java`. |
| About You (`dump.xml`) | Ownership Transfer Tab | `tgl_about_you_page_motor_ownershipTransfer` | `desc="Ownership Transfer"` / `Button` | `true` / `true` | `//*[@resource-id='tgl_about_you_page_motor_ownershipTransfer']` | XML (`dump.xml`) & `AboutYouScreen.java`. **Click twice** due to 3s flicker bug. |
| About You (`dump.xml`) | Renew Policy Tab | `tgl_about_you_page_motor_renew` | `desc="Renew policy"` / `Button` | `true` / `true` | `//*[@resource-id='tgl_about_you_page_motor_renew']` | XML (`dump.xml`) & `AboutYouScreen.java`. |
| About You (`dump.xml`) | Sequence Number Radio | `radio_btn_about_you_page_motor_sequence_number` | `desc="Sequence Number"` / `Button` | `true` / `true` | `//*[@resource-id='radio_btn_about_you_page_motor_sequence_number']` | XML (`dump.xml`) & `AboutYouScreen.java`. Verify selection and re-tap if dropped. |
| About You (`dump.xml`) | Custom Card Radio | `radio_btn_about_you_page_motor_custom_card` | `desc="Custom Card"` / `Button` | `true` / `true` | `//*[@resource-id='radio_btn_about_you_page_motor_custom_card']` | XML (`dump.xml`) & `AboutYouScreen.java`. Verify selection and re-tap if dropped. |
| About You (`dump.xml`) | Sequence Number Input | `textfield_about_you_page_motor_sequence_number` | `EditText` | `true` / `true` | `//android.widget.EditText[@resource-id='textfield_about_you_page_motor_sequence_number']` | XML (`dump.xml`) & `AboutYouScreen.java`. |
| About You (`AboutYouScreen`) | Custom Card Input | `textfield_about_you_page_motor_custom_card` | `EditText` | `true` / `true` | `//android.widget.EditText[@resource-id='textfield_about_you_page_motor_custom_card']` | PO (`AboutYouScreen.java`). Shown when Custom Card selected. |
| About You (`AboutYouScreen`) | Car Model Year Dropdown | `""` | `desc="Car model year"` / `Button` | `PO` | `//android.widget.Button[@content-desc='Car model year']` | PO (`AboutYouScreen.java`). Shown when Custom Card selected. |
| About You (`AboutYouScreen`) | Seller National ID Input | `textfield_about_you_page_motor_seller_national_id` | `EditText` | `PO` | `//android.widget.EditText[@resource-id='textfield_about_you_page_motor_seller_national_id']` | PO (`AboutYouScreen.java`). Ownership Transfer only; may stay in DOM if switching back. |
| About You (`dump.xml`) | Buyer / Owner National ID | `textfield_about_you_page_motor_national_id` | `EditText` | `true` / `true` | `//android.widget.EditText[@resource-id='textfield_about_you_page_motor_national_id' and not(contains(@resource-id, 'seller'))]` | XML (`dump.xml`) & `AboutYouScreen.java`. Guard against seller ID collision. |
| About You (`dump.xml`) | Date of Birth Picker | `textfield_about_you_page_motor_dob` | `ImageView` | `true` / `true` | `//android.widget.ImageView[@resource-id='textfield_about_you_page_motor_dob']` | XML (`dump.xml`), `NAF="true"`. Scroll Month wheel at `0.72 * width`, finish with `(//android.widget.Button[@content-desc='Confirm'])[last()]`. |
| About You (`dump.xml`) | Mobile Phone Number | `textfield_about_you_page_motor_phone_number` | `EditText` | `true` / `true` | `//android.widget.EditText[@resource-id='textfield_about_you_page_motor_phone_number']` | XML (`dump.xml`) & `AboutYouScreen.java`. Send `"done"` IME action after input. |
| About You (`dump.xml`) | Policy Start Date | `textfield_about_you_page_motor_start_date` | `text="03/11/2026"` / `ImageView` | `true` / `true` | `//android.widget.ImageView[@resource-id='textfield_about_you_page_motor_start_date']` | XML (`dump.xml`). Pre-filled default date. |
| About You (`dump.xml`) | Privacy / Declaration Switch | `tgl_about_you_page_motor_checkbox` | `Switch` | `false` / `true` | `//android.widget.Switch[@resource-id='tgl_about_you_page_motor_checkbox']` | XML (`dump.xml`). `checkable="true"`. Tap via `tapElement`. |
| About You (`dump.xml`) | Privacy Notice Link | `""` | `desc="Privacy Notice"` / `View` | `true` / `true` | `//android.view.View[@content-desc='Privacy Notice']` | XML (`dump.xml`). |
| About You (`dump.xml`) | Next Button | `btn_about_you_page_motor_next_button` (outer) | Inner `Button` `desc="Next"` | Outer `false`/Inner `true` / `true` | `//*[@resource-id='btn_about_you_page_motor_next_button']//*[@clickable='true'] \| //android.widget.Button[@clickable='true' and @content-desc='Next']` | XML (`dump.xml`). Outer wrapper is `clickable=false`. |
| `VehicleDetailsModal` | Modal Header | `""` | `contains 'We need more details about your vehicle'` | `PO` | `//*[contains(@content-desc, 'We need more details about your vehicle') or contains(@text, 'We need more details about your vehicle')]` | PO-derived (`VehicleDetailsModal.java`). Conditional when vehicle lookup needs Make/Model. |
| `VehicleDetailsModal` | Vehicle Make Input | `""` | `EditText` | `PO` | `(//android.widget.EditText)[1]` | PO (`VehicleDetailsModal.java`). Type `"b.m.w"`, tap `B.M.W`/`BMW` result at `Y > 1000`. |
| `VehicleDetailsModal` | Vehicle Model Input | `""` | `EditText` | `PO` | `(//android.widget.EditText)[2]` | PO (`VehicleDetailsModal.java`). Pick result matching `"3"`/`"Series"` with `Y >= modelBottom - 20 && Y < 2100`. |
| `VehicleDetailsModal` | Proceed Button | `""` | `desc`/`text="Proceed"` / `Button` | `PO` | `//android.widget.Button[@content-desc='Proceed' or @text='Proceed']` | PO (`VehicleDetailsModal.java`). Verify `enabled=true && clickable=true`. |
| `QuoteScreen` | Quote Page Header | `""` | `contains "Here's your quote"` | `PO` | `//*[contains(@content-desc, "Here's your quote") or contains(@text, "Here's your quote")]` | PO (`QuoteScreen.java`). |
| `QuoteScreen` | Tier Cards (Smart / Saver / Comprehensive) | `""` | `'Smart Cover'`, `'Saver Cover'`, `'Comprehensive Cover'` | `PO` | `//*[contains(@content-desc, 'Comprehensive Cover') or contains(@content-desc, 'Smart Cover') or contains(@content-desc, 'Saver Cover')]` | PO (`QuoteScreen.java`). |
| `QuoteScreen` | Repair Type Options | `agency_repair` / `workshop_repair` (substring) | `'Agency Repair'` / `'Workshop'` | `PO` | `//*[contains(@resource-id, 'agency_repair') or contains(@content-desc, 'Agency Repair')] \| //*[contains(@resource-id, 'workshop_repair') or contains(@content-desc, 'Workshop')]` | PO (`QuoteScreen.java`). |
| `QuoteScreen` | Optional Add-ons (4) | `addon_roadside`, `addon_replacement`, `addon_personal_accident`, `addon_glass` (substrings) | `'Roadside'`, `'Replacement'`/`'Hire Car'`, `'Personal Accident'`, `'Glass'`/`'Windshield'` | `PO` | `//*[contains(@resource-id, 'addon_') or contains(@content-desc, 'Roadside') or contains(@content-desc, 'Replacement') or contains(@content-desc, 'Personal Accident') or contains(@content-desc, 'Glass')]` | PO (`QuoteScreen.java`). |
| `QuoteScreen` | Promo Code Input & Apply | `textfield_promo_code`, `btn_apply_promo` | `'Apply'` (button) | `PO` | `//*[@resource-id='textfield_promo_code']` / `//*[@resource-id='btn_apply_promo' or @content-desc='Apply']` | PO (`QuoteScreen.java`). Feedback labels: `'Promo applied'`/`'Discount'` vs `'Invalid promo'`/`'Invalid code'`/`promo_error`. |
| `QuoteScreen` | Total Premium | `total_premium` (substring) | `'SAR'` | `PO` | `//*[contains(@resource-id, 'total_premium') or contains(@content-desc, 'SAR')]` | PO (`QuoteScreen.java`). |
| `QuoteScreen` | PEP & Related Questions | `""` | `(//*[@text='Yes'])[1..2]` / `(//*[@text='No'])[1..2]` | `PO` | `(//*[@content-desc='No' or @text='No'])[1]` and `[2]` | PO (`QuoteScreen.java`). Always answer both before Buy Now. |
| `QuoteScreen` | Buy Now CTA | `btn_quote_page_motor_buy_now` | `desc`/`text="Buy now"` | `PO` | `//*[@resource-id='btn_quote_page_motor_buy_now']//*[@clickable='true'] \| //*[@resource-id='btn_quote_page_motor_buy_now']` | PO (`QuoteScreen.java`). |
| `QuoteScreen` | Upsell Skip CTA | `btn_upsell_skip` | `desc`/`text="Skip"` | `PO` | `//*[@resource-id='btn_upsell_skip' or @content-desc='Skip' or @text='Skip']` | PO (`QuoteScreen.java`). Handles optional add-on upsell modal. |
| `CheckoutScreen` | Payment Card Mode | `btn_checkout_page_motor_payment_mode_direct_pay_post_card` | — | `PO` | `//*[@resource-id='btn_checkout_page_motor_payment_mode_direct_pay_post_card']//*[@clickable='true'] \| //*[@resource-id='btn_checkout_page_motor_payment_mode_direct_pay_post_card']` | PO (`CheckoutScreen.java`). Note distinct Motor checkout ID prefix `btn_checkout_page_motor_`. |
| `CheckoutScreen` | Email Field | `textfield_checkout_page_motor_email` | `EditText` | `PO` | `//android.widget.EditText[@resource-id='textfield_checkout_page_motor_email']` | PO (`CheckoutScreen.java`). |
| `CheckoutScreen` | IBAN Field | `textfield_checkout_page_motor_iban` | `EditText` | `PO` | `//android.widget.EditText[@resource-id='textfield_checkout_page_motor_iban']` | PO (`CheckoutScreen.java`). **Strip `'SA'` prefix -> 22 numeric digits only**. |
| `CheckoutScreen` | T&C Switch | `tgl_checkout_page_motor_checkbox` | `Switch` | `PO` | `//android.widget.Switch[@resource-id='tgl_checkout_page_motor_checkbox']` | PO (`CheckoutScreen.java`). Tap via `tapElement`. |
| `CheckoutScreen` | Price Summary Anchors | `""` | `'VAT'` / `'15%'` / `'Total'` | `PO` | `//*[contains(@content-desc, 'VAT') or contains(@content-desc, '15%') or contains(@content-desc, 'Total')]` | PO (`CheckoutScreen.java`). |
| `CheckoutScreen` | Pay Now Button | `btn_checkout_page_motor_pay_now` | — | `PO` | `(//*[@resource-id='btn_checkout_page_motor_pay_now']//*[@clickable='true'] \| //*[@resource-id='btn_checkout_page_motor_pay_now'])[last()]` | PO (`CheckoutScreen.java`). Uses `[last()]` for overlay safety. |

---

## 5. Product 2: Travel Insurance

**Sources**: `TravelAboutYouScreen.java` + XML dumps `insure_trip_dump.xml`, `now_travel_form_dump.xml`.

> [!WARNING]
> **Travel-Specific Gotchas**
> 1. **Two Distinct Initial Screens**: Landing (`insure_trip_dump.xml`) opens an `'Insure your trip'` Policyholder form (`now_travel_form_dump.xml`) with **National ID, DOB, Mobile, Switch, Next** *before* the Trip Details screen (Trip Type, Destination, Departure/Return Dates, Traveler Type).
> 2. **Missing DOB `resource-id` (`now_travel_form_dump.xml`)**: Unlike Login/Income/Home/DW/Family Health, the DOB `ImageView` at `[76,582][1004,667]` has **`resource-id=""`** and **`NAF="true"`**. Locate it relative to `textfield_national_id` (`following::android.widget.ImageView[1]`).
> 3. **PO `@hint` Fallback Warning**: `TravelAboutYouScreen.java` uses `@AndroidFindBy` compounds containing `@hint` (never exported on Android Flutter) alongside positional indices `(//android.widget.EditText)[1..2]` and `(//android.widget.ImageView[contains(@resource-id, 'textfield')])[1..2]`.

| Screen / Step | Element / Field | Exact `resource-id` | Exact `content-desc` / `text` / `class` | `clickable` / `focusable` | Recommended Dynamic Locator (XPath) | Notes / Gotchas |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| Landing (`insure_trip_dump.xml`) | Header / Back | `""` | `desc="Travel Insurance"` / `View` | `true` / `true` | `//android.view.View[@content-desc='Travel Insurance']` | XML (`insure_trip_dump.xml`). |
| Landing (`insure_trip_dump.xml`) | Plan Preview Cards | `""` | `desc="Explorer Savvy"`, `desc="Explorer Start"` | `true` / `true` | `//*[contains(@content-desc, 'Explorer Savvy') or contains(@content-desc, 'Explorer Start')]` | XML (`insure_trip_dump.xml`). Landing preview cards. |
| Landing (`insure_trip_dump.xml`) | Insure Now CTA | `btn_next_button_product_landing_page_` -> `btn_insure_now` (outers) | Inner `Button` `desc="Insure now"` | Outer `false`/Inner `true` / `true` | `//*[@resource-id='btn_insure_now']//*[@clickable='true' and @content-desc='Insure now'] \| //android.widget.Button[@clickable='true' and @content-desc='Insure now']` | XML (`insure_trip_dump.xml`). Outer wrappers are `clickable=false`. |
| Step 1: Insure Your Trip (`now_travel_form_dump.xml`) | Back Button & Title | `btn_back` | `""` (`Button`) / `desc="Insure your trip"` (`View`) | `true` / `true` | `//android.widget.Button[@resource-id='btn_back']` | XML (`now_travel_form_dump.xml`), `NAF="true"`. |
| Step 1: Insure Your Trip (`now_travel_form_dump.xml`) | National ID / Iqama | `textfield_national_id` | `EditText` | `true` / `true` | `//android.widget.EditText[@resource-id='textfield_national_id']` | XML (`now_travel_form_dump.xml`), `NAF="true"`. |
| Step 1: Insure Your Trip (`now_travel_form_dump.xml`) | Date of Birth Picker | `""` (EMPTY!) | `text=""`, `desc=""` / `ImageView` | `true` / `true` | `//android.widget.EditText[@resource-id='textfield_national_id']/following::android.widget.ImageView[1]` | XML (`now_travel_form_dump.xml`) `[76,582][1004,667]`, `NAF="true"`. Scroll `SeekBar` at `wheelX = 0.35 * width`. |
| Step 1: Insure Your Trip (`now_travel_form_dump.xml`) | Mobile Phone Number | `textfield_phone_number` | `EditText` | `true` / `true` | `//android.widget.EditText[@resource-id='textfield_phone_number']` | XML (`now_travel_form_dump.xml`), `NAF="true"`. |
| Step 1: Insure Your Trip (`now_travel_form_dump.xml`) | Privacy Switch & Link | `tgl_checkbox` | `Switch` / `View` `desc="Privacy Notice"` | Switch `false` / `true` | `//android.widget.Switch[@resource-id='tgl_checkbox']` | XML (`now_travel_form_dump.xml`). `checkable="true"`. Tap via `tapElement`. |
| Step 1: Insure Your Trip (`now_travel_form_dump.xml`) | Next Button | `btn_next_button` (x2 outers) | Inner `Button` `desc="Next"` | Outer `false`/Inner `true` / `true` | `(//*[@resource-id='btn_next_button']//*[@clickable='true' and @content-desc='Next'] \| //android.widget.Button[@clickable='true' and @content-desc='Next'])[last()]` | XML (`now_travel_form_dump.xml`). Double nested `btn_next_button` wrappers. |
| Step 2: Trip Details (`TravelAboutYouScreen`) | Trip Type Tabs | `""` | `'Single trip'`, `'Multi trip (1 year)'` | `PO` | `//*[@content-desc='Single trip' or @content-desc='Multi trip (1 year)']` | PO-derived (`TravelAboutYouScreen.java`). |
| Step 2: Trip Details (`TravelAboutYouScreen`) | Destination Input & Option | `""` | `EditText` / `'Schengen'`, `'Worldwide'` (`android.view.View`) | `PO` | `(//android.widget.EditText)[1]` / `//android.view.View[contains(@content-desc, 'Worldwide') or contains(@content-desc, 'Schengen')]` | PO-derived (`TravelAboutYouScreen.java`). |
| Step 2: Trip Details (`TravelAboutYouScreen`) | Departure & Return Dates | `*textfield*` (substring) | `ImageView` | `PO` | `(//android.widget.ImageView[contains(@resource-id, 'textfield')])[1]` and `[2]` | PO-derived (`TravelAboutYouScreen.java`). |
| Step 2: Trip Details (`TravelAboutYouScreen`) | Mobile Number (Trip Screen) | `textfield_phone_number` (if same) | `EditText` | `PO` | `(//android.widget.EditText)[last()]` | PO-derived (`TravelAboutYouScreen.java`). |
| Step 2: Trip Details (`TravelAboutYouScreen`) | Traveler Type | `""` | `'Individual'`, `'With Family'`, `'Group'` | `PO` | `//*[@content-desc='Individual' or @content-desc='With Family' or @content-desc='Group']` | PO-derived (`TravelAboutYouScreen.java`). |
| Step 3: Declarations (`TravelAboutYouScreen`) | Medical / Sports / Hazardous Qs | `btn_yes_button` / `btn_no_button` | `'Yes'` / `'No'`, `'Sports'`, `'Hazardous'` | `PO` | `//android.widget.Button[@content-desc='No' or @text='No']` | PO-derived (`TravelAboutYouScreen.java`). |
| Step 3: Declarations (`TravelAboutYouScreen`) | Proceed Button | `btn_proceed` / `btn_next_button` | `'Proceed'` / `'Next'` | `PO` | `//*[@resource-id='btn_proceed' or @resource-id='btn_next_button']//*[@clickable='true'] \| //android.widget.Button[@clickable='true' and (@content-desc='Proceed' or @content-desc='Next')]` | PO-derived (`TravelAboutYouScreen.java`). |
| Step 4: Quote (`TravelAboutYouScreen`) | Plan Tiers | `""` | `'Silver'`, `'Gold'`, `'Platinum'` | `PO` | `//*[contains(@content-desc, 'Silver') or contains(@content-desc, 'Gold') or contains(@content-desc, 'Platinum')]` | PO-derived (`TravelAboutYouScreen.java`). |
| Step 4: Quote (`TravelAboutYouScreen`) | Buy Now CTA | `btn_buy_now` / `btn_sales_quote_page_buy_now_button` | `'Buy now'` | `PO` | `(//*[@resource-id='btn_buy_now' or @resource-id='btn_sales_quote_page_buy_now_button']//*[@clickable='true'] \| //android.widget.Button[@clickable='true' and @content-desc='Buy now'])[last()]` | PO-derived (`TravelAboutYouScreen.java`). |
| Step 5: Checkout (`TravelAboutYouScreen`) | National Address (7) + IBAN + T&C + Pay Now | See [Section 2.3](#23-shared-checkout-national-address-addnewcardmodal-3ds--policyconfirmationscreen) | See [Section 2.3](#23-shared-checkout-national-address-addnewcardmodal-3ds--policyconfirmationscreen) | PO | Use `textfield_building_number` .. `textfield_zip_code`, `textfield_iban` (no `'SA'`), `tgl_checkbox`, `btn_pay_now` | PO-derived (`TravelAboutYouScreen.java`). Includes all 7 National Address fields. |

---

## 6. Product 3: Home Insurance

**Sources**: **100% Page-Object-derived** from **`HomeInsuranceScreen.java`** (no Home Insurance XML dumps were captured in the dump set). Uses shared Flutter patterns verified across sibling products.

| Screen / Step | Element / Field | Exact `resource-id` | Exact `content-desc` / `text` / `class` | `clickable` / `focusable` | Recommended Dynamic Locator (XPath) | Notes / Gotchas |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| Landing (`HomeInsuranceScreen`) | Insure Now CTA | `btn_next_button_product_landing_page_` -> `btn_insure_now` | Inner `Button` `desc="Insure now"` | PO | `//*[@resource-id='btn_insure_now']//*[@clickable='true' and @content-desc='Insure now'] \| //android.widget.Button[@clickable='true' and @content-desc='Insure now']` | PO-derived (`HomeInsuranceScreen.java`). Outer wrapper pattern matches other products. |
| Policyholder Details | National ID / Iqama | `textfield_national_id` | `EditText` | PO | `//android.widget.EditText[@resource-id='textfield_national_id']` | PO-derived (`HomeInsuranceScreen.java`). |
| Policyholder Details | Month & Year of Birth | `textfield_month_year_of_birth_txt` | `ImageView` | PO | `//*[@resource-id='textfield_month_year_of_birth_txt']` | PO-derived (`HomeInsuranceScreen.java`). Opens `SeekBar` picker (`wheelX = 0.35 * width`). |
| Policyholder Details | Mobile Phone Number | `textfield_phone_number` | `EditText` | PO | `//android.widget.EditText[@resource-id='textfield_phone_number']` | PO-derived (`HomeInsuranceScreen.java`). |
| Policyholder Details | Email | `textfield_email` | `EditText` | PO | `//android.widget.EditText[@resource-id='textfield_email']` | PO-derived (`HomeInsuranceScreen.java`). |
| Policyholder Details | Privacy / Declaration Switch | `tgl_checkbox` | `Switch` | PO | `//android.widget.Switch[@resource-id='tgl_checkbox']` | PO-derived (`HomeInsuranceScreen.java`). Tap via `tapElement`. |
| Policyholder Details | Next CTA | `btn_next_button` | `desc="Next"` / `Button` | PO | `//*[@resource-id='btn_next_button']//*[@clickable='true'] \| //android.widget.Button[@clickable='true' and @content-desc='Next']` | PO-derived (`HomeInsuranceScreen.java`). |
| About Your Home / Property | Email (Confirm/Step 2) | `textfield_email` | `EditText` | PO | `//android.widget.EditText[@resource-id='textfield_email'] \| (//android.widget.EditText)[1]` | PO-derived (`HomeInsuranceScreen.java`). |
| About Your Home / Property | 5 Property Declarations | `btn_no` / `btn_yes` (or `No` / `Yes`) | `'No'` / `'Yes'` | PO | `//*[@resource-id='btn_no' or @resource-id='btn_no_button' or @content-desc='No' or @text='No']` | PO-derived (`HomeInsuranceScreen.java`). Answer all 5 declaration questions. |
| About Your Home / Property | Cover Type & Property Type | — | Cover / Property selectors | PO | `//*[contains(@content-desc, 'Building') or contains(@content-desc, 'Contents') or contains(@content-desc, 'Owner') or contains(@content-desc, 'Tenant')]` | PO-derived (`HomeInsuranceScreen.java`). Verify exact runtime `content-desc` on live step if failing. |
| About Your Home / Property | Building & Contents Values | `textfield_building_value`, `textfield_contents_value` | `EditText` | PO | `//android.widget.EditText[@resource-id='textfield_building_value']` / `[@resource-id='textfield_contents_value']` | PO-derived (`HomeInsuranceScreen.java`). |
| About Your Home / Property | Valuables Description & Value | `textfield_valuable_desc`, `textfield_valuable_value` | `EditText` | PO | `//android.widget.EditText[@resource-id='textfield_valuable_desc']` / `[@resource-id='textfield_valuable_value']` | PO-derived (`HomeInsuranceScreen.java`). Shown when valuables coverage/items are added. |
| About Your Home / Property | Proceed CTA | `btn_proceed` | `'Proceed'` / `Button` | PO | `//*[@resource-id='btn_proceed']//*[@clickable='true'] \| //android.widget.Button[@clickable='true' and @content-desc='Proceed']` | PO-derived (`HomeInsuranceScreen.java`). |
| Plans / Quote | Plan Buy Now CTA | `btn_buy_now` / `btn_sales_quote_page_buy_now_button` | `'Buy now'` / `Button` | PO | `(//*[@resource-id='btn_buy_now' or @resource-id='btn_sales_quote_page_buy_now_button']//*[@clickable='true'] \| //android.widget.Button[@clickable='true' and @content-desc='Buy now'])[last()]` | PO-derived (`HomeInsuranceScreen.java`). |
| Checkout | National Address (7) + IBAN + T&C + Pay Now | See [Section 2.3](#23-shared-checkout-national-address-addnewcardmodal-3ds--policyconfirmationscreen) | See [Section 2.3](#23-shared-checkout-national-address-addnewcardmodal-3ds--policyconfirmationscreen) | PO | Use `textfield_building_number` .. `textfield_zip_code`, `textfield_iban` (no `'SA'`), `tgl_checkbox`, `btn_pay_now` | PO-derived (`HomeInsuranceScreen.java`). Includes all 7 National Address fields. |

---

## 7. Product 4: Pet Insurance

**Sources**: `PetScreen.java` + XML dumps `scratch/pet_landing.xml` (duplicate `scratch/fh_landing2.xml`), `pet_about.xml`, `pet_breed_open.xml`, `pet_years_popup2.xml`, `pet_months_popup.xml`, `pet_city_popup.xml`, `pet_city_typed.xml`.

> [!WARNING]
> **Pet-Specific Gotchas**
> 1. **Resource-ID Mismatch on Pet Name**: Live XML (`pet_about.xml`) uses **`textfield_your_pet_name`**, whereas `PetScreen.java` also checks `textfield_pet_name`.
> 2. **Unlabelled Dynamic Controls**:
>    - **Breed (`pet_breed_open.xml`)**: Dynamically inserted **after** tapping `btn_meow` or `btn_woof`. It is an unlabelled `EditText` (`resource-id=""`, `NAF="true"`).
>    - **Age Years & Months (`pet_about.xml`)**: Two unlabelled `android.view.View` boxes under `Your pet’s age` with **`clickable="false"`, `focusable="true"`**, whose Y-bounds shift downward from `[813..934]` to `[971..1092]` once Breed opens! `PetScreen.java` taps at **`rect.x + width/3, rect.y + height/2`**.
>    - **City (`pet_city_popup.xml`)**: An unlabelled `EditText` (`resource-id=""`, `NAF="true"`) placed immediately *before* `textfield_email`. Anchor via `//android.widget.EditText[@resource-id='textfield_email']/preceding::android.widget.EditText[1]` so Breed/Microchip changes never shift the index.
> 3. **Gender IDs (`pet_about.xml` vs `PetScreen.java`)**: XML uses `btn_sales_gender_picker_option_male` / `btn_sales_gender_picker_option_female`, while `PetScreen.java` references `btn_pet_gender_male` / `btn_pet_gender_female`.
> 4. **Plan Names**: Landing XML (`pet_landing.xml`) displays **`Fur basic`** and **`Fur deluxe`**, while `PetScreen.java` quote matching checks **`Basic`**, **`Standard`**, and **`Premium`**.

| Screen / Step | Element / Field | Exact `resource-id` | Exact `content-desc` / `text` / `class` | `clickable` / `focusable` | Recommended Dynamic Locator (XPath) | Notes / Gotchas |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| Landing (`pet_landing.xml`) | Screen Header & Vetree Banner | `""` | `desc="Pet Insurance"` (`View`), `desc="Terms & Conditions applies"` (`View`), `desc="Play now"` (`ImageView`) | `true` / `true` | `//android.view.View[@content-desc='Pet Insurance']` | XML (`pet_landing.xml`). `Play now` `[806,843][996,896]`. |
| Landing (`pet_landing.xml`) | Landing Plan Preview Cards | `""` | `desc="Fur basic"`, `desc="Fur deluxe"` / `View` | `true` / `true` | `//android.view.View[@content-desc='Fur basic' or @content-desc='Fur deluxe']` | XML (`pet_landing.xml`). Note `Fur basic` / `Fur deluxe` capitalization. |
| Landing (`pet_landing.xml`) | Insure Now CTA | `btn_next_button_product_landing_page_` -> `btn_insure_now` (outers) | Inner `Button` `desc="Insure now"` | Outer `false`/Inner `true` / `true` | `//*[@resource-id='btn_insure_now']//*[@clickable='true' and @content-desc='Insure now'] \| //android.widget.Button[@clickable='true' and @content-desc='Insure now']` | XML (`pet_landing.xml`). Outer wrappers are `clickable=false`. |
| About Your Pet (`pet_about.xml`) | Back Button | `btn_back` | `""` / `Button` | `true` / `true` | `//android.widget.Button[@resource-id='btn_back']` | XML (`pet_about.xml`), `NAF="true"`. |
| About Your Pet (`pet_about.xml`) | Pet Name Input | XML: `textfield_your_pet_name`<br>PO fallback: `textfield_pet_name` | `EditText` | `true` / `true` | `//android.widget.EditText[@resource-id='textfield_your_pet_name' or @resource-id='textfield_pet_name']` | XML (`pet_about.xml`) & PO mismatch — always include `textfield_your_pet_name`. |
| About Your Pet (`pet_about.xml`) | Species: Cat (Meow) / Dog (Woof) | `btn_meow` / `btn_woof` | `desc="Meow"`, `desc="Woof"` / `ImageView` | `true` / `true` | `//android.widget.ImageView[@resource-id='btn_meow']` / `[@resource-id='btn_woof']` | XML (`pet_about.xml`). Selecting reveals Breed dropdown below it. |
| About Your Pet (`pet_breed_open.xml`) | Breed Autocomplete Input | `""` (EMPTY!) | `EditText` | `true` / `true` | `//android.widget.EditText[@resource-id='textfield_your_pet_name']/following::android.widget.EditText[not(@resource-id) or @resource-id=''][1]` | XML (`pet_breed_open.xml`) `[45,745][1035,866]`, `NAF="true"`. Appears only after `btn_woof`/`btn_meow`. |
| About Your Pet (`pet_breed_open.xml`) | Breed Popup Options | `""` | `desc="American Eskimo Dog Toy"`, `'Jack Chi'`, `'Jack tzu'`, `'Jack-A-Bee'`, etc. / `View` | `true` / `true` | `//android.view.View[@clickable='true' and contains(@content-desc, '<Breed>')]` | XML (`pet_breed_open.xml`). Scrollable `android.view.View` list of `View[@clickable='true']` options. |
| About Your Pet (`pet_about.xml`) | Age Anchor Header | `""` | `desc="Your pet’s age"` / `View` | `false` / `true` | `//android.view.View[@content-desc='Your pet’s age']` | XML (`pet_about.xml`). Uses curly apostrophe `’`. Reliable anchor for Age dropdowns. |
| About Your Pet (`pet_about.xml`, `pet_years_popup2.xml`) | Pet Age — Years Trigger & Popup | `""` (EMPTY!) | Trigger: unlabelled `View` (`clk=false`)<br>Options: `desc="0 year"`, `"1 year"`, `"2 years"`, `"3 years"` / `View` (`clk=true`) | Trigger `false` / `true`<br>Options `true` / `true` | `(//android.view.View[@content-desc='Your pet’s age']/following::android.view.View[@focusable='true'])[1]`<br>Option: `//android.view.View[@clickable='true' and contains(@content-desc, 'year')]` | XML `[45,813][514,934]` (shifts to `[45,971][514,1092]` when Breed open). **`clk=false` trigger** — tap via `tapElement` at `(x + w/3, y + h/2)`. |
| About Your Pet (`pet_about.xml`, `pet_months_popup.xml`) | Pet Age — Months Trigger & Popup | `""` (EMPTY!) | Trigger: unlabelled `View` (`clk=false`)<br>Options: `desc="0 month"`, `"1 month"`, `"2 months"`, `"3 months"` / `View` (`clk=true`) | Trigger `false` / `true`<br>Options `true` / `true` | `(//android.view.View[@content-desc='Your pet’s age']/following::android.view.View[@focusable='true'])[2]`<br>Option: `//android.view.View[@clickable='true' and contains(@content-desc, 'month')]` | XML `[566,813][1035,934]` (shifts to `[566,971][1035,1092]` when Breed open). **`clk=false` trigger** — tap via `tapElement`. |
| About Your Pet (`pet_about.xml`) | Gender: Male / Female | XML: `btn_sales_gender_picker_option_male` / `_female`<br>PO: `btn_pet_gender_male` / `_female` | `desc="Male"`, `desc="Female"` / `ImageView` | `true` / `true` | `//android.widget.ImageView[@resource-id='btn_sales_gender_picker_option_male' or @resource-id='btn_pet_gender_male' or @content-desc='Male']` | XML (`pet_about.xml`) vs PO mismatch. Prefer XML ID + `@content-desc` fallback. |
| About Your Pet (`PetScreen`) | Microchip ID (Optional) | `textfield_microchip_id` | `EditText` | `PO` | `//android.widget.EditText[@resource-id='textfield_microchip_id']` | PO (`PetScreen.java`). Absent in `pet_about.xml`; may be optional/scrolled. |
| About Your Pet (`pet_city_popup.xml`, `pet_city_typed.xml`) | City Input & Options | `""` (EMPTY!) | Input: `EditText`<br>Options: `'Abha'`, `'Abu Areesh'`, `'Afif'`, `'Riyadh'` / `View` | `true` / `true` | `//android.widget.EditText[@resource-id='textfield_email']/preceding::android.widget.EditText[1]`<br>Option: `//android.view.View[@clickable='true' and contains(@content-desc, 'Riyadh')]` | XML `[45,1246][1035,1367]`, `NAF="true"`. Anchoring before `textfield_email` avoids index drift. |
| About Your Pet (`pet_about.xml`) | Owner Email & Mobile | `textfield_email`, `textfield_phone_number` | `EditText` | `true` / `true` | `//android.widget.EditText[@resource-id='textfield_email']` & `[@resource-id='textfield_phone_number']` | XML (`pet_about.xml`), `NAF="true"`. |
| About Your Pet (`pet_about.xml`) | Privacy Switch & Link | `tgl_checkbox` | `Switch` / `View` `desc="Privacy Notice"` | Switch `false` / `true` | `//android.widget.Switch[@resource-id='tgl_checkbox']` | XML (`pet_about.xml`). `checkable="true"`. Tap via `tapElement`. |
| About Your Pet (`pet_about.xml`) | Next Button | `btn_next_button` (x2 outers) | Inner `Button` `desc="Next"` | Outer `false`/Inner `true` / `true` | `(//*[@resource-id='btn_next_button']//*[@clickable='true' and @content-desc='Next'] \| //android.widget.Button[@clickable='true' and @content-desc='Next'])[last()]` | XML (`pet_about.xml`). Double nested `btn_next_button` wrappers. |
| Pet Declarations (`PetScreen`) | Health Yes / No & Proceed | `btn_yes_button`, `btn_no_button`, `btn_proceed` | `'Yes'`, `'No'`, `'Proceed'` | `PO` | `//*[@resource-id='btn_no_button']` / `//*[@resource-id='btn_proceed']//*[@clickable='true']` | PO-derived (`PetScreen.java`). |
| Pet Plans / Quote (`PetScreen`) | Plan Tiers & Buy Now | `btn_buy_now` / `btn_sales_quote_page_buy_now_button` | `'Basic'`, `'Standard'`, `'Premium'` / `'Buy now'` | `PO` | `(//*[@resource-id='btn_buy_now' or @resource-id='btn_sales_quote_page_buy_now_button']//*[@clickable='true'] \| //android.widget.Button[@clickable='true' and @content-desc='Buy now'])[last()]` | PO-derived (`PetScreen.java`). |
| Pet Checkout (`PetScreen`) | National Address (7) + IBAN + T&C + Pay Now | See [Section 2.3](#23-shared-checkout-national-address-addnewcardmodal-3ds--policyconfirmationscreen) | See [Section 2.3](#23-shared-checkout-national-address-addnewcardmodal-3ds--policyconfirmationscreen) | PO | Use `textfield_building_number` .. `textfield_zip_code`, `textfield_iban` (no `'SA'`), `tgl_checkbox`, `btn_pay_now` | PO-derived (`PetScreen.java`). Includes all 7 National Address fields. |

---

## 8. Product 5: Income Protection Insurance

**Sources**: `IncomeInsuranceScreen.java` + XML dumps `scratch/income_about2.xml`, `income_about3.xml`, `income_about4.xml`, `income_dob.xml`, `income_quote.xml`, `income_checkout.xml`, `income_pay_modal.xml`.

> [!WARNING]
> **Income-Specific Gotchas**
> 1. **Quote Page Uses `Proceed` (`btn_proceed`), NOT `Buy now`**: Unlike Motor, Travel, Home, Pet, Health, DW, and Malpractice, the Income Quote screen CTA (`income_quote.xml`) is `btn_next_button` -> `btn_proceed` -> `Button[@content-desc='Proceed']`, **and it has a mandatory hospitalization acknowledgment `Switch` (`id='tgl_checkbox'`) directly on the Quote page** that must be toggled before proceeding!
> 2. **No National Address Fields in `income_checkout.xml`**: The captured Income Checkout dump (`income_checkout.xml`) contains **Payment Mode (`Credit/Debit Card`, `Tabby`, `Tamara`), `textfield_iban`, and `tgl_checkbox`**, with **zero National Address fields** rendered.

| Screen / Step | Element / Field | Exact `resource-id` | Exact `content-desc` / `text` / `class` | `clickable` / `focusable` | Recommended Dynamic Locator (XPath) | Notes / Gotchas |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| Landing (`IncomeInsuranceScreen`) | Insure Now CTA | `btn_next_button_product_landing_page_` -> `btn_insure_now` | Inner `Button` `desc="Insure now"` | PO | `//*[@resource-id='btn_insure_now']//*[@clickable='true' and @content-desc='Insure now'] \| //android.widget.Button[@clickable='true' and @content-desc='Insure now']` | PO-derived (`IncomeInsuranceScreen.java`). Uses `tapPreferredClickable`. |
| About You (`income_about2..4.xml`) | Back Button | `btn_back` | `""` / `Button` | `true` / `true` | `//android.widget.Button[@resource-id='btn_back']` | XML (`income_about*.xml`), `NAF="true"`. |
| About You (`income_about2..4.xml`) | National ID / Iqama | `textfield_national_id` | `text="1313424273"` (when filled) / `EditText` | `true` / `true` | `//android.widget.EditText[@resource-id='textfield_national_id']` | XML (`income_about*.xml`). |
| About You (`income_about2..4.xml`) | Month & Year of Birth | `textfield_month_year_of_birth_txt` | `text="11/2008"` (when picked) / `ImageView` | `true` / `true` | `//*[@resource-id='textfield_month_year_of_birth_txt']` | XML (`income_about*.xml`) + `income_dob.xml` SeekBar picker (`wheelX = 0.35 * width`). |
| About You (`income_about2..4.xml`) | Mobile Phone & Email | `textfield_phone_number`, `textfield_email` | `text="500421222"`, `text="qa@tree.com.sa"` / `EditText` | `true` / `true` | `//android.widget.EditText[@resource-id='textfield_phone_number']` & `[@resource-id='textfield_email']` | XML (`income_about*.xml`). |
| About You (`income_about2..4.xml`) | Policy Start Date | `textfield_policy_start_date` | `text="16/11/2026"` / `ImageView` | `true` / `true` | `//android.widget.ImageView[@resource-id='textfield_policy_start_date']` | XML (`income_about*.xml`). Pre-filled default date. |
| About You (`income_about2..4.xml`) | Privacy Switch & Link | `tgl_checkbox` | `Switch` / `View` `desc="Privacy Notice"` | Switch `false` / `true` | `//android.widget.Switch[@resource-id='tgl_checkbox']` | XML (`income_about*.xml`). Tap via `tapElement`. |
| About You (`income_about2..4.xml`) | Next Button | `btn_next_button` (outers) | Inner `Button` `desc="Next"` | Outer `false`/Inner `true` / `true` | `(//*[@resource-id='btn_next_button']//*[@clickable='true' and @content-desc='Next'] \| //android.widget.Button[@clickable='true' and @content-desc='Next'])[last()]` | XML (`income_about*.xml`). |
| Employment & Declarations (`IncomeInsuranceScreen`) | Employment Status | `btn_employed`, `btn_self_employed` | `'Employed'`, `'Self Employed'` | `PO` | `//*[@resource-id='btn_employed' or @content-desc='Employed']` | PO-derived (`IncomeInsuranceScreen.java`). No XML captured for intermediate step. |
| Employment & Declarations (`IncomeInsuranceScreen`) | Salary / Occupation / Declarations | `textfield_monthly_income`, `textfield_salary`, `dropdown_occupation`, `textfield_occupation`, `btn_proceed` | `'Yes'` / `'No'` declarations (3-pass scroll) | `PO` | `//*[@resource-id='textfield_monthly_income' or @resource-id='textfield_salary']` / `//*[@resource-id='dropdown_occupation' or @resource-id='textfield_occupation']` | PO-derived (`IncomeInsuranceScreen.java`). Scrolls declarations in 3 passes. |
| Quote (`income_quote.xml`) | Back Button & Title | `btn_back` | `View` `desc="Here's your quote"` | `true` / `true` | `//android.view.View[contains(@content-desc, "Here's your quote")]` | XML (`income_quote.xml`). |
| Quote (`income_quote.xml`) | Coverage Duration Tabs | `""` | `desc="3 months"`, `"6 months"`, `"12 months"` / `Button` | `true` / `true` | `//android.widget.Button[@content-desc='12 months']` | XML (`income_quote.xml`). Switch duration `'3 months'`, `'6 months'`, or `'12 months'`. |
| Quote (`income_quote.xml`) | Plan Cards (`Basic` / `Silver` / `Gold Cover`) | `""` | `desc="Basic Cover\n75\n.00\n...\n500.00\nThe monthly claim amount"`, `'Silver Cover\n135\n.00...'`, `'Gold Cover\n225\n.00...'` / `View` | `true` / `true` | `//android.view.View[@clickable='true' and contains(@content-desc, 'Silver Cover')]` | XML (`income_quote.xml`). Multi-line `content-desc`; always use `contains(@content-desc, ...)`. |
| Quote (`income_quote.xml`) | Mandatory Quote Switch | `tgl_checkbox` | `Switch` (`I understand that this policy is only covers hospitalization...`) | `false` / `true` | `//android.widget.Switch[@resource-id='tgl_checkbox']` | XML (`income_quote.xml`). **Must toggle before `Proceed`**. |
| Quote (`income_quote.xml`) | Proceed CTA (Quote Exit) | `btn_next_button` -> `btn_proceed` (outers) | Inner `Button` `desc="Proceed"` | Outer `false`/Inner `true` / `true` | `(//*[@resource-id='btn_proceed']//*[@clickable='true' and @content-desc='Proceed'] \| //android.widget.Button[@clickable='true' and @content-desc='Proceed'])[last()]` | XML (`income_quote.xml`). Uses **`Proceed`**, not `Buy now`. |
| Checkout (`income_checkout.xml`) | Payment Modes, Details, IBAN, T&C, Pay Now | `btn_back`, `btn_detials_btn`, `btn_payment_mode_moyasar_direct_pay`, `btn_payment_mode_tabby`, `btn_payment_mode_tamara`, `textfield_iban`, `tgl_checkbox`, `btn_next_button` -> `btn_pay_now` | `Credit/Debit Card`, `Tabby`, `Tamara`, `Details`, `Pay now` | See [Section 2.3](#23-shared-checkout-national-address-addnewcardmodal-3ds--policyconfirmationscreen) | See [Section 2.3](#23-shared-checkout-national-address-addnewcardmodal-3ds--policyconfirmationscreen) | XML (`income_checkout.xml`) + `income_pay_modal.xml`. **No National Address fields present in `income_checkout.xml`.** |

---

## 9. Product 6: Individual Health Insurance

**Sources**: **100% Page-Object-derived** from **`IndividualHealthScreen.java`** (no Individual Health XML dumps exist — note `landing_dump.xml` is Domestic Worker Insurance!).

| Screen / Step | Element / Field | Exact `resource-id` | Exact `content-desc` / `text` / `class` | `clickable` / `focusable` | Recommended Dynamic Locator (XPath) | Notes / Gotchas |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| Landing (`IndividualHealthScreen`) | Insure Now CTA | `btn_next_button_product_landing_page_` -> `btn_insure_now` | Inner `Button` `desc="Insure now"` | PO | `//*[@resource-id='btn_insure_now']//*[@clickable='true' and @content-desc='Insure now'] \| //android.widget.Button[@clickable='true' and @content-desc='Insure now']` | PO-derived (`IndividualHealthScreen.java`). |
| Applicant Details (`IndividualHealthScreen`) | National ID, DOB, Mobile, Email, Start Date | `textfield_national_id`, `textfield_month_year_of_birth_txt`, `textfield_phone_number`, `textfield_email`, `textfield_policy_start_date` | `EditText` / `ImageView` | PO | `//*[@resource-id='textfield_national_id']`, `[@resource-id='textfield_month_year_of_birth_txt']`, etc. | PO-derived (`IndividualHealthScreen.java`). Uses `1313424273` + `500421222` + `1998` identity tuple. |
| Applicant Details (`IndividualHealthScreen`) | Optional Gender | `btn_gender_male`, `btn_gender_female` | `'Male'`, `'Female'` | PO | `//*[@resource-id='btn_gender_male' or @content-desc='Male']` / `[@resource-id='btn_gender_female' or @content-desc='Female']` | PO-derived (`IndividualHealthScreen.java`). Verify if IDs match Pet/Health (`btn_sales_gender_picker_option_*`) on live screen. |
| Applicant Details (`IndividualHealthScreen`) | Optional Marital Status | `btn_marital_status_single`, `btn_marital_status_married` | `'Single'`, `'Married'` | PO | `//*[@resource-id='btn_marital_status_single' or @content-desc='Single']` | PO-derived (`IndividualHealthScreen.java`). |
| Applicant Details (`IndividualHealthScreen`) | Privacy Switch & Next | `tgl_checkbox`, `btn_next_button` | `Switch` / `Next` | PO | `//android.widget.Switch[@resource-id='tgl_checkbox']` -> `(//*[@resource-id='btn_next_button']//*[@clickable='true'])[last()]` | PO-derived (`IndividualHealthScreen.java`). |
| Dependents (`IndividualHealthScreen`) | Add Member & Relationship | `btn_add`, `btn_add_member`, `dropdown_relationship`, `btn_proceed`, `btn_proceed_to_quote` | `'Add'`, Relationship selector, `'Proceed'` | PO | `//*[@resource-id='btn_add' or @resource-id='btn_add_member']//*[@clickable='true'] \| //*[@resource-id='dropdown_relationship']` | PO-derived (`IndividualHealthScreen.java`). `btn_add` outer is `clickable=false` on Flutter add buttons. |
| Declarations (`IndividualHealthScreen`) | Health Declarations | — | `'Yes'` / `'No'` | PO | `//*[@resource-id='btn_no_button' or @content-desc='No' or @text='No']` | PO-derived (`IndividualHealthScreen.java`). |
| Quote / Plans (`IndividualHealthScreen`) | Plan Tiers | — | `'Bronze'`/`'Primary'`, `'Silver'`/`'Comfort'`, `'Gold'`/`'Beyond'`/`'Diamond'`, `'Platinum'`/`'Beyond'`/`'Diamond'` | PO | `//*[contains(@content-desc, 'Comfort') or contains(@content-desc, 'Silver') or contains(@content-desc, 'Primary') or contains(@content-desc, 'Bronze') or contains(@content-desc, 'Beyond') or contains(@content-desc, 'Gold') or contains(@content-desc, 'Diamond') or contains(@content-desc, 'Platinum')]` | PO-derived (`IndividualHealthScreen.java`). Supports both legacy and SME/Retail tier naming. |
| Quote / Plans (`IndividualHealthScreen`) | Deductible / Copay | `deductible` / `copay` (substrings) | — | PO | `//*[contains(@resource-id, 'deductible') or contains(@resource-id, 'copay') or contains(@content-desc, 'Deductible')]` | PO-derived (`IndividualHealthScreen.java`). |
| Quote / Plans (`IndividualHealthScreen`) | Buy Now CTA | `btn_buy_now` / `btn_sales_quote_page_buy_now_button` | `'Buy now'` | PO | `(//*[@resource-id='btn_buy_now' or @resource-id='btn_sales_quote_page_buy_now_button']//*[@clickable='true'] \| //android.widget.Button[@clickable='true' and @content-desc='Buy now'])[last()]` | PO-derived (`IndividualHealthScreen.java`). |
| Checkout (`IndividualHealthScreen`) | National Address (7) + IBAN + T&C + Pay Now | See [Section 2.3](#23-shared-checkout-national-address-addnewcardmodal-3ds--policyconfirmationscreen) | See [Section 2.3](#23-shared-checkout-national-address-addnewcardmodal-3ds--policyconfirmationscreen) | PO | Use `textfield_building_number` .. `textfield_zip_code`, `textfield_iban` (no `'SA'`), `tgl_checkbox`, `btn_pay_now` | PO-derived (`IndividualHealthScreen.java`). Includes all 7 National Address fields. |

---

## 10. Product 7: Family Health Insurance

**Sources**: `FamilyHealthScreen.java` + XML dumps `scratch/fh_landing.xml`, `fh_live.xml`, `fh_after_insure.xml`, `fh_tap_test2.xml` (note `scratch/fh_landing2.xml` is a Pet Insurance dump).

> [!WARNING]
> **Family Health Landing Render Timing**
> On initial navigation to Family Health (`fh_landing.xml`), `FamilyHealthScreen.java` explicitly waits for `'Annual Limit' | 'Diamond' | 'Gold' | 'Silver'` before tapping `Insure now` (using up to a 3-attempt tap loop) so Flutter finishes hydrating the landing carousel before intercepting the tap.

| Screen / Step | Element / Field | Exact `resource-id` | Exact `content-desc` / `text` / `class` | `clickable` / `focusable` | Recommended Dynamic Locator (XPath) | Notes / Gotchas |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| Landing (`fh_landing.xml`, `fh_live.xml`) | Screen Header & Hero Copy | `""` | `desc="Family Health Insurance"`, `desc="Your family’s perfect\nhealth plan"` / `View` | `true` (header) / `true` | `//android.view.View[@content-desc='Family Health Insurance']` | XML (`fh_landing.xml`). Wait for `'Annual Limit'\|'Diamond'\|'Gold'\|'Silver'` before CTA click. |
| Landing (`fh_landing.xml`, `fh_live.xml`) | Insure Now CTA | `btn_next_button_product_landing_page_` -> `btn_insure_now` (outers) | Inner `Button` `desc="Insure now"` | Outer `false`/Inner `true` / `true` | `//*[@resource-id='btn_insure_now']//*[@clickable='true' and @content-desc='Insure now'] \| //android.widget.Button[@clickable='true' and @content-desc='Insure now']` | XML (`fh_landing.xml`) + `FamilyHealthScreen.java` (3-attempt tap loop). |
| Step 1: Sponsor About You (`fh_after_insure.xml`) | Back & Header | `btn_back` | `""` (`Button`) / `desc="About you"` (`View`) | `true` / `true` | `//android.widget.Button[@resource-id='btn_back']` | XML (`fh_after_insure.xml`), `NAF="true"`. |
| Step 1: Sponsor About You (`fh_after_insure.xml`) | Sponsor National ID / Iqama | `textfield_national_id` | `EditText` | `true` / `true` | `//android.widget.EditText[@resource-id='textfield_national_id']` | XML (`fh_after_insure.xml`), `NAF="true"`. |
| Step 1: Sponsor About You (`fh_after_insure.xml`) | Sponsor Mobile & Email | `textfield_phone_number`, `textfield_email` | `EditText` | `true` / `true` | `//android.widget.EditText[@resource-id='textfield_phone_number']` & `[@resource-id='textfield_email']` | XML (`fh_after_insure.xml`), `NAF="true"`. |
| Step 1: Sponsor About You (`fh_after_insure.xml`) | Sponsor DOB Picker | `textfield_month_year_of_birth_txt` | `ImageView` | `true` / `true` | `//*[@resource-id='textfield_month_year_of_birth_txt']` | XML (`fh_after_insure.xml`), `NAF="true"`. Opens `SeekBar` picker (`wheelX = 0.35 * width`). |
| Step 1: Sponsor About You (`fh_after_insure.xml`) | Privacy Switch & Link | `tgl_checkbox` | `Switch` / `View` `desc="Privacy Notice"` | Switch `false` / `true` | `//android.widget.Switch[@resource-id='tgl_checkbox']` | XML (`fh_after_insure.xml`). Tap via `tapElement`. |
| Step 1: Sponsor About You (`fh_after_insure.xml`) | Next Button | `btn_next_button` (outers) | Inner `Button` `desc="Next"` | Outer `false`/Inner `true` / `true` | `(//*[@resource-id='btn_next_button']//*[@clickable='true' and @content-desc='Next'] \| //android.widget.Button[@clickable='true' and @content-desc='Next'])[last()]` | XML (`fh_after_insure.xml`). |
| Step 2: Family Members (`FamilyHealthScreen`) | Add Member & Relationship | `btn_add`, `btn_add_member`, `dropdown_relationship`, `btn_proceed`, `btn_proceed_to_quote` | `'Add'`, `'Spouse'`, `'Son'`, `'Daughter'`, `'Proceed'` | PO | `//*[@resource-id='btn_add' or @resource-id='btn_add_member']//*[@clickable='true']` / `//*[@resource-id='dropdown_relationship']` | PO-derived (`FamilyHealthScreen.java`). |
| Declarations (`FamilyHealthScreen`) | Medical Declarations | `btn_no_button` / `btn_yes_button` | `'No'` / `'Yes'` | PO | `//*[@resource-id='btn_no_button' or @content-desc='No' or @text='No']` | PO-derived (`FamilyHealthScreen.java`). |
| Plans / Quote (`FamilyHealthScreen`) | Tiers & Maternity/Dental Switches | `btn_buy_now`, `btn_sales_quote_page_buy_now_button` | `'Bronze'`, `'Silver'`, `'Gold'`, `'Platinum'`, Maternity/Dental `Switch`, `'Buy now'` | PO | `//android.widget.Switch[contains(@content-desc, 'Maternity') or contains(@content-desc, 'Dental')]` / `(//*[@resource-id='btn_buy_now' or @resource-id='btn_sales_quote_page_buy_now_button']//*[@clickable='true'])[last()]` | PO-derived (`FamilyHealthScreen.java`). Note Maternity & Dental add-ons render as `Switch` controls. |
| Checkout (`FamilyHealthScreen`) | National Address (7) + IBAN + T&C + Pay Now | See [Section 2.3](#23-shared-checkout-national-address-addnewcardmodal-3ds--policyconfirmationscreen) | See [Section 2.3](#23-shared-checkout-national-address-addnewcardmodal-3ds--policyconfirmationscreen) | PO | Use `textfield_building_number` .. `textfield_zip_code`, `textfield_iban` (no `'SA'`), `tgl_checkbox`, `btn_pay_now` | PO-derived (`FamilyHealthScreen.java`). Includes all 7 National Address fields. |

---

## 11. Product 8: Domestic Worker Health Insurance

**Sources**: `DomesticWorkerScreen.java` + XML dumps `landing_dump.xml`, `sponsor_screen.xml`, `sp_dump.xml`, `dw_step2_dump.xml`, `3ds_ui.xml`, `members.xml`, `members_ui.xml`, `dw_quotes.xml`, `dw_quotes_dump.xml`, `dw_checkout.xml`, `dw_checkout_dump.xml`.

> [!WARNING]
> **Domestic Worker Critical Gotchas**
> 1. **Two Different `Buy now` Outer IDs Observed Across Dumps**: `dw_quotes.xml` wraps `'Buy now'` in **`btn_buy_now`**, whereas `dw_quotes_dump.xml` wraps it in **`btn_sales_quote_page_buy_now_button`**! Both have `clickable="false"` on the outer wrapper and wrap an inner `Button[@content-desc='Buy now' and @clickable='true']`.
> 2. **Duplicate `btn_yes_button` / `btn_no_button` on Member(s) Details (`dw_step2_dump.xml`)**: Health Declaration, PEP, and Related Party **all reuse `btn_yes_button` and `btn_no_button`** — index with `(//android.widget.Button[@resource-id='btn_no_button'])[1..3]`!

| Screen / Step | Element / Field | Exact `resource-id` | Exact `content-desc` / `text` / `class` | `clickable` / `focusable` | Recommended Dynamic Locator (XPath) | Notes / Gotchas |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| Landing (`landing_dump.xml`) | Header & Preview Plan Cards | `""` | `desc="Domestic Worker Insurance"` (`View`), `'Primary'` (`50,000`), `'Beyond'` (`100,000`) | `true` / `true` | `//android.view.View[@content-desc='Domestic Worker Insurance']` | XML (`landing_dump.xml`). Mistakenly named `landing_dump`, this is Domestic Worker. |
| Landing (`landing_dump.xml`) | Insure Now CTA | `btn_next_button_product_landing_page_` -> `btn_insure_now` (outers) | Inner `Button` `desc="Insure now"` | Outer `false`/Inner `true` / `true` | `//*[@resource-id='btn_insure_now']//*[@clickable='true' and @content-desc='Insure now'] \| //android.widget.Button[@clickable='true' and @content-desc='Insure now']` | XML (`landing_dump.xml`). |
| Step 1: Sponsor Details (`sponsor_screen.xml`, `sp_dump.xml`) | Back & Header | `btn_back` | `""` (`Button`) / `desc="Sponsor details"` (`View`) | `true` / `true` | `//android.widget.Button[@resource-id='btn_back']` | XML (`sponsor_screen.xml`). |
| Step 1: Sponsor Details (`sp_dump.xml`) | Sponsor National ID | `textfield_national_id` | `text="1000000001"` / `EditText` | `true` / `true` | `//android.widget.EditText[@resource-id='textfield_national_id']` | XML (`sp_dump.xml`). |
| Step 1: Sponsor Details (`sp_dump.xml`) | Sponsor Mobile & Email | `textfield_phone_number`, `textfield_email` | `text="501234567"`, `text="qa.tester@tree.com.sa"` / `EditText` | `true` / `true` | `//android.widget.EditText[@resource-id='textfield_phone_number']` & `[@resource-id='textfield_email']` | XML (`sp_dump.xml`). |
| Step 1: Sponsor Details (`sp_dump.xml`) | Sponsor DOB Picker | `textfield_month_year_of_birth_txt` | `text="11/2008"` / `ImageView` | `true` / `true` | `//*[@resource-id='textfield_month_year_of_birth_txt']` | XML (`sp_dump.xml`). Opens `SeekBar` picker (`wheelX = 0.35 * width`). |
| Step 1: Sponsor Details (`sp_dump.xml`) | Privacy Switch, Link & Next | `tgl_checkbox`, `btn_next_button` | `Switch` / `Privacy Notice` / `Next` | Switch `false`/Btn inner `true` | `//android.widget.Switch[@resource-id='tgl_checkbox']` -> `(//*[@resource-id='btn_next_button']//*[@clickable='true'])[last()]` | XML (`sp_dump.xml`). |
| Step 2: Member(s) Details (`dw_step2_dump.xml`, `3ds_ui.xml`) | Header & Back | `btn_back` | `""` (`Button`) / `desc="Member(s) Details"` (`View`) | `true` / `true` | `//android.view.View[@content-desc='Member(s) Details']` | XML (`dw_step2_dump.xml`). |
| Step 2: Member(s) Details (`3ds_ui.xml`) | Helper IQAMA / National ID | `textfield_national_id` | `text="2456789011"` / `EditText` | `true` / `true` | `//android.widget.EditText[@resource-id='textfield_national_id']` | XML (`3ds_ui.xml` / `members_ui.xml`). |
| Step 2: Member(s) Details (`dw_step2_dump.xml`) | Add Member CTA | `btn_add` (outer) | Outer `Button` -> Inner `ImageView` `desc="Add"` | Outer `false`/Inner `true` / `true` | `//*[@resource-id='btn_add']//*[@clickable='true' and @content-desc='Add'] \| //android.widget.ImageView[@clickable='true' and @content-desc='Add']` | XML (`dw_step2_dump.xml`). Inner element is `ImageView`, not `Button`! |
| Step 2: Member(s) Details (`dw_step2_dump.xml`) | Policy Start Date | `textfield_policy_start_date` | `text="12/11/2026"` / `ImageView` | `true` / `true` | `//android.widget.ImageView[@resource-id='textfield_policy_start_date']` | XML (`dw_step2_dump.xml`). |
| Step 2: Member(s) Details (`dw_step2_dump.xml`) | Q1: Health Declaration Yes/No | `btn_yes_button`, `btn_no_button` | `desc="Yes"`, `desc="No"` / `Button` (under `'Does this member/members have any conditions...'`) | `true` / `true` | `(//android.widget.Button[@resource-id='btn_no_button'])[1]` | XML (`dw_step2_dump.xml`). **Duplicate IDs** across Q1, Q2, Q3 — use `[1]`, `[2]`, `[3]`. |
| Step 2: Member(s) Details (`dw_step2_dump.xml`) | Q2: PEP Yes/No (+ Info Icon) | `btn_yes_button`, `btn_no_button` | `desc="Yes"`, `desc="No"` / `Button` (`Are you a Politically Exposed Person?` + `ImageView` info) | `true` / `true` | `(//android.widget.Button[@resource-id='btn_no_button'])[2]` | XML (`dw_step2_dump.xml`). |
| Step 2: Member(s) Details (`dw_step2_dump.xml`) | Q3: Related Party Yes/No | `btn_yes_button`, `btn_no_button` | `desc="Yes"`, `desc="No"` / `Button` (`Are you a related party/Person?` + `ImageView` info) | `true` / `true` | `(//android.widget.Button[@resource-id='btn_no_button'])[3]` | XML (`dw_step2_dump.xml`). |
| Step 2: Member(s) Details (`dw_step2_dump.xml`) | Proceed CTA | `btn_next_button` -> `btn_proceed` (outers) | Inner `Button` `desc="Proceed"` | Outer `false`/Inner `true` / `true` | `(//*[@resource-id='btn_proceed']//*[@clickable='true' and @content-desc='Proceed'] \| //android.widget.Button[@clickable='true' and @content-desc='Proceed'])[last()]` | XML (`dw_step2_dump.xml`). |
| Step 3: Quote (`dw_quotes.xml`, `dw_quotes_dump.xml`) | Back & Quote Title | `btn_back` | `View` `desc="Here's your quote"` | `true` / `true` | `//android.view.View[contains(@content-desc, "Here's your quote")]` | XML (`dw_quotes.xml`). |
| Step 3: Quote (`dw_quotes.xml`) | Plan Cards (`Primary` / `Beyond` / `Comfort`) | `""` | `desc="Primary..."` (`547\n.40`), `'Beyond...'` (`1,026\n.95`), `'Comfort...'` (`683\n.10`, `'Absher Mandatory coverage'`) / `View` | `true` / `true` | `//android.view.View[@clickable='true' and contains(@content-desc, 'Comfort')]` | XML (`dw_quotes.xml`). |
| Step 3: Quote (`dw_quotes.xml`) | View Medical Network CTA | `btn_view_medical_network` (outer) | Outer `Button` -> Inner `ImageView` `desc="View Medical Network"` | Outer `false`/Inner `true` / `true` | `//*[@resource-id='btn_view_medical_network']//*[@clickable='true'] \| //android.widget.ImageView[@content-desc='View Medical Network']` | XML (`dw_quotes.xml`). |
| Step 3: Quote (`dw_quotes.xml`, `dw_quotes_dump.xml`) | Buy Now CTA | `btn_next_button_product_landing_page_` -> `btn_buy_now` OR `btn_sales_quote_page_buy_now_button` | Inner `Button` `desc="Buy now"` | Outer `false`/Inner `true` / `true` | `(//*[@resource-id='btn_buy_now' or @resource-id='btn_sales_quote_page_buy_now_button']//*[@clickable='true' and @content-desc='Buy now'] \| //android.widget.Button[@clickable='true' and @content-desc='Buy now'])[last()]` | XML (`dw_quotes.xml` uses `btn_buy_now`; `dw_quotes_dump.xml` uses `btn_sales_quote_page_buy_now_button`). |
| Step 4: Checkout (`dw_checkout.xml`) | National Address (7) + Details + IBAN + T&C + Pay Now | See [Section 2.3](#23-shared-checkout-national-address-addnewcardmodal-3ds--policyconfirmationscreen) | All 7 Nat. Addr `EditText`s (`ScrollView`), `btn_detials_btn`, `textfield_iban`, `tgl_checkbox`, `btn_pay_now` | XML-verified | Use [Section 2.3](#23-shared-checkout-national-address-addnewcardmodal-3ds--policyconfirmationscreen) exact IDs | XML (`dw_checkout.xml`). All 7 National Address fields are present in DW Checkout. |

---

## 12. Product 9: Medical Malpractice Insurance

**Sources**: `MalpracticeScreen.java` + XML dumps `malp_exact_landing.xml`, `malp_dump.xml`, `malp_form_dump.xml`, `malp_practitioner_tree.xml`, `prof_dump.xml`, `malp_btn_dump.xml`, `malp_quote_dump.xml`, `after_type_2.xml`, `malp_checkout_dump.xml`.

> [!WARNING]
> **Malpractice Critical Gotchas**
> 1. **Unlabelled DOB & Profession Controls (`malp_form_dump.xml`)**: Neither the Practitioner DOB `ImageView` (`[76,998][1004,1084]`) nor the Profession `EditText` (`[45,1138][1035,1259]`) has a `@resource-id`! Anchor both immediately after `textfield_email`.
> 2. **Prior Insurance Branching**: Answering **`Yes`** (`btn_yes_button`) to `'Do you have prior-Medical Malpractice insurance?'` reveals conditional PO fields (`textfield_retroactive_date`, `btn_claims_yes`/`no`, `textfield_license_number`, `textfield_hospital_name`). Standard UAT XML dumps capture the **`No`** path.
> 3. **Quote Layout Differences (`malp_quote_dump.xml`)**:
>    - Policy duration (`'1 Year'`) is a `clickable="false"` `View` at `[87,438][993,559]`.
>    - Limit of indemnity (`100,000`..`500,000`) renders as **4 clickable `Button` cards** (`424.64`..`828.49`), paired with `ImageView` aggregated limit (`'500,000'`), **two duplicate PEP/Related Party `btn_yes_button`/`btn_no_button` pairs**, and **`btn_buy_now`**.
> 4. **No National Address Fields in `malp_checkout_dump.xml`**: Like Income, the captured Malpractice Checkout XML has **`btn_detials_btn`**, **Payment Modes (`Credit/Debit Card`, `Tamara`, `Tabby`)**, **`textfield_iban`**, **`tgl_checkbox`**, and **`btn_pay_now`** — zero National Address inputs rendered.

| Screen / Step | Element / Field | Exact `resource-id` | Exact `content-desc` / `text` / `class` | `clickable` / `focusable` | Recommended Dynamic Locator (XPath) | Notes / Gotchas |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| Landing (`malp_exact_landing.xml`) | Header & Insure Now CTA | `btn_next_button_product_landing_page_` -> `btn_insure_now` (outers) | `desc="Malpractice Insurance"` (`View`) / Inner `Button` `desc="Insure now"` | Outer `false`/Inner `true` / `true` | `//*[@resource-id='btn_insure_now']//*[@clickable='true' and @content-desc='Insure now'] \| //android.widget.Button[@clickable='true' and @content-desc='Insure now']` | XML (`malp_exact_landing.xml`). Note: `malp_landing_tree.xml` is Motor landing. |
| Practitioner About You (`malp_form_dump.xml`) | Header & Back Button | `btn_back` | `""` (`Button`) / `desc="About you"` (`View`) | `true` / `true` | `//android.widget.Button[@resource-id='btn_back']` | XML (`malp_form_dump.xml`), `NAF="true"`. |
| Practitioner About You (`malp_form_dump.xml`) | National ID / Iqama | `textfield_national_id` | `EditText` | `true` / `true` | `//android.widget.EditText[@resource-id='textfield_national_id']` | XML (`malp_form_dump.xml`), `NAF="true"`. |
| Practitioner About You (`malp_form_dump.xml`) | Mobile Phone & Email | `textfield_phone_number`, `textfield_email` | `EditText` | `true` / `true` | `//android.widget.EditText[@resource-id='textfield_phone_number']` & `[@resource-id='textfield_email']` | XML (`malp_form_dump.xml`), `NAF="true"`. |
| Practitioner About You (`malp_form_dump.xml`, `prof_dump.xml`) | Date of Birth Picker | `""` (EMPTY!) | `text=""` (`malp_form_dump.xml`) / `text="11/2008"` (`prof_dump.xml`) / `ImageView` | `true` / `true` | `//android.widget.EditText[@resource-id='textfield_email']/following::android.widget.ImageView[1]` | XML `[76,998][1004,1084]`, `NAF="true"`. Anchored right after `textfield_email`. |
| Practitioner About You (`malp_form_dump.xml`, `prof_dump.xml`) | Medical Profession Input | `""` (EMPTY!) | `EditText` | `true` / `true` | `//android.widget.EditText[@resource-id='textfield_email']/following::android.widget.EditText[1]` | XML `[45,1138][1035,1259]`, `NAF="true"`. Opens scrollable option list. |
| Practitioner About You (`prof_dump.xml`) | Profession List Options | `""` | `'ACUPUNCTURE SPECIALIST'`, `'Acute Care Surgery'`, `'Addiction Medicine'` / `View` | `true` / `true` | `//android.view.View[@clickable='true' and contains(@content-desc, '<Specialty>')]` | XML (`prof_dump.xml`). Scrollable `android.view.View` list. |
| Practitioner About You (`malp_form_dump.xml`) | Policy Start Date | `textfield_policy_start_date` | `text="13/11/2026"` / `ImageView` | `true` / `true` | `//android.widget.ImageView[@resource-id='textfield_policy_start_date']` | XML (`malp_form_dump.xml`). Pre-filled default date. |
| Practitioner About You (`malp_form_dump.xml`) | Prior Malpractice Yes / No | `btn_yes_button`, `btn_no_button` | `desc="Yes"`, `desc="No"` / `Button` (`Do you have prior-Medical Malpractice insurance?`) | `true` / `true` | `//android.widget.Button[@resource-id='btn_no_button']` / `[@resource-id='btn_yes_button']` | XML (`malp_form_dump.xml`). Selecting `Yes` unlocks conditional PO fields below. |
| Practitioner About You (`MalpracticeScreen`) | Conditional Prior-Cover / License Fields | `textfield_retroactive_date`, `btn_claims_yes`, `btn_claims_no`, `textfield_license_number`, `textfield_hospital_name`, `btn_proceed` | `EditText` / `Button` | `PO` | `//*[@resource-id='textfield_retroactive_date' or @resource-id='textfield_license_number' or @resource-id='textfield_hospital_name']` | PO-derived (`MalpracticeScreen.java`). Only appears when prior insurance = `Yes` or specialty requires license. |
| Practitioner About You (`malp_form_dump.xml`) | Privacy Switch & Next | `tgl_checkbox`, `btn_next_button` (x2 outers) | `Switch` / `Privacy Notice` / Inner `Button` `desc="Next"` | Switch `false`/Btn inner `true` | `//android.widget.Switch[@resource-id='tgl_checkbox']` -> `(//*[@resource-id='btn_next_button']//*[@clickable='true' and @content-desc='Next'])[last()]` | XML (`malp_form_dump.xml`). |
| Quote (`malp_quote_dump.xml`) | Back, Title & Duration | `btn_back` | `View` `desc="Here's your quote"`, `View` `text="1 Year"` (`clk=false`) | Back `true` / Dur `false` (`foc=true`) | `//android.view.View[@text='1 Year']` | XML (`malp_quote_dump.xml`). Duration box at `[87,438][993,559]`. |
| Quote (`malp_quote_dump.xml`) | Indemnity Limit Cards (4) | `""` | `'100,000\nLimit of indemnity\n424\n.64'`, `'250,000...\n531\n.76'`, `'300,000...\n556\n.94'`, `'500,000...\n828\n.49'` / `Button` | `true` / `true` | `//android.widget.Button[@clickable='true' and contains(@content-desc, '250,000')]` | XML (`malp_quote_dump.xml`). Use `contains(@content-desc, '<amount>')`. |
| Quote (`malp_quote_dump.xml`) | Annual Claim Aggregated Limit | `""` | `desc="500,000"` / `ImageView` | `true` / `true` | `//android.widget.ImageView[@clickable='true' and @content-desc='500,000']` | XML (`malp_quote_dump.xml`). |
| Quote (`malp_quote_dump.xml`) | PEP & Related Party Yes / No | `btn_yes_button`, `btn_no_button` | `desc="Yes"`, `desc="No"` / `Button` (+ `ImageView` info icons) | `true` / `true` | `(//android.widget.Button[@resource-id='btn_no_button'])[1]` and `[2]` | XML (`malp_quote_dump.xml`). **Duplicate IDs** across PEP `[1]` & Related Party `[2]`. |
| Quote (`malp_quote_dump.xml`) | Buy Now CTA | `btn_next_button` -> `btn_buy_now` (outers) | Inner `Button` `desc="Buy now"` | Outer `false`/Inner `true` / `true` | `(//*[@resource-id='btn_buy_now']//*[@clickable='true' and @content-desc='Buy now'] \| //android.widget.Button[@clickable='true' and @content-desc='Buy now'])[last()]` | XML (`malp_quote_dump.xml`). |
| Checkout (`malp_checkout_dump.xml`) | Payment Modes, Details, IBAN, T&C, Pay Now | `btn_back`, `btn_detials_btn`, `btn_payment_mode_moyasar_direct_pay`, `btn_payment_mode_tamara`, `btn_payment_mode_tabby`, `textfield_iban`, `tgl_checkbox`, `btn_next_button` -> `btn_pay_now` | `Credit/Debit Card`, `Tamara`, `Tabby`, `Details`, `Pay now` | See [Section 2.3](#23-shared-checkout-national-address-addnewcardmodal-3ds--policyconfirmationscreen) | See [Section 2.3](#23-shared-checkout-national-address-addnewcardmodal-3ds--policyconfirmationscreen) | XML (`malp_checkout_dump.xml`). **No National Address fields present in `malp_checkout_dump.xml`** (handled conditionally in PO). |

---

## Missing Locator Fixes & Performance Optimization Catalog

*Audit snapshot verified against `src/main/java/pages/*.java`, `src/test/java/tests/*.java`, `src/main/java/utils/DriverManager.java`, `src/test/java/base/BaseTest.java`, `src/test/resources/testng.xml`, `src/test/resources/config/uat.properties`, and failure DOM dumps under `target/screenshots/*_DOM.xml`.*

### A. Cross-Cutting Locator Root Causes

1. **`appium:ignoreUnimportantViews=true` (`DriverManager.java:97`) strips outer wrappers & `Switch` controls:**
   - Live failure DOMs (`testMotorFlow7_InvalidPromoCodeBoundary_20261115_152224_DOM.xml`, `testPetFlow4_FormValidation_20261113_160940_DOM.xml`, `testMalpracticeFlow2_SurgeonSpecialtyAndDeductibleChoice_20261113_163745_DOM.xml`, `testDomesticWorkerFlow2_MultipleWorkersBatch_20261113_164452_DOM.xml`) confirm Flutter exports `@resource-id` values (`btn_next_button`, `btn_buy_now`, `btn_pay_now`, `btn_insure_now`, `btn_next_button_product_landing_page_`, `btn_about_you_page_motor_next_button`, `radio_btn_about_you_page_motor_sequence_number`, `radio_btn_about_you_page_motor_custom_card`, `tgl_checkbox`, `tgl_about_you_page_motor_checkbox`) on nodes marked `a11y-important="false"` and `clickable="false"`.
   - Action CTAs wrap an inner `android.widget.Button[@a11y-important="true" and @clickable="true" and @content-desc="<Label>" and @resource-id=""]`. When `ignoreUnimportantViews=true`, UiAutomator2 drops all `a11y-important="false"` nodes from the query tree, breaking pure `@resource-id='btn_...'` locators.
   - `android.widget.Switch` toggles (`tgl_checkbox`, `tgl_about_you_page_motor_checkbox`) have `a11y-important="false"`, `clickable="false"`, and **zero clickable children**, making them completely invisible while `ignoreUnimportantViews=true`. Only `AboutYouScreen` (`299`) and `TravelAboutYouScreen` (`373`) currently call `BasePage.clickPrivacySwitch(...)` (`BasePage.java:432`), which uses `findWithUnimportantViews(...)` (`BasePage.java:412`) to toggle `ignoreUnimportantViews` temporarily to `false`; the remaining 7 product Page Objects and all Checkout Terms checkboxes do raw `driver.findElement` / PageFactory lookups that fail when hidden.

2. **XPath 1.0 Predicate Truthiness Bug (`[... or (//...)[n]]` matches root `<FrameLayout>`):**
   - `appium:enforceXPath1=true` (`DriverManager.java:111`) evaluates any node-set inside `[...]` as boolean `true` when non-empty. Writing `//*[cond or (//*[@text='Yes'])[last()]]` turns into `//*[true()]`, matching `<android.widget.FrameLayout>` (`bounds="[0,0][1080,2400]"`) as index 0 and tapping screen center instead of the target button/input.
   - Still live in `MalpracticeScreen.java:239-240` (`declarePriorClaims`); ensure all union fallbacks use top-level `|` unions (`locA | locB`), never nested `(//...)[n]` inside predicates.

3. **`@hint` vs `@text` vs `@content-desc` on Flutter Android UAT:**
   - Contradicting earlier assumptions, live DOM dumps show UAT Flutter consistently exports `@hint` on inputs and picker triggers (`Month/Year of birth`, `Phone number`, `National ID/Iqama`, `Email address`, `Sequence number`, `Your Pet’s name`, `Your Pet’s breed`, `City of residence`, `Years`, `Months`), while buttons, labels, and currency totals expose text via `@content-desc` (`@text` is empty `""`).

4. **Sticky Bottom CTAs Beyond Nominal Hierarchy Height & Unhandled System Modals:**
   - Primary bottom CTAs render at `bounds="[42,2201][1038,2321]"` inside `<hierarchy height="2201">` (e.g., `Insure now`, `Next`, `Buy now`). Always prefer `tapElement(...)` (`getRect()` W3C center tap) on the inner `clickable="true"` `Button`.
   - **Undocumented Modals:** Android ANR dialog (`android:id/aerr_wait`, `android:id/aerr_close`, captured in `testTravelFlow1_SingleTripHappyPath_20261113_145935_DOM.xml`) and `"Something Went Wrong"` modal (`btn_try_again_button` -> `Try Again`, `btn_close_button` -> `Close`).

---

### B. Missing & Brittle Locators Across Shared Components & All 9 Products

#### 0. Shared Screens & Modals (`HomeScreen`, `CheckoutScreen`, `QuoteScreen`, `AddNewCardModal`, `OtpModal`, `PolicyConfirmationScreen`)

| File & Line(s) | Current Broken / Brittle Locator | Root Cause | Recommended Robust XPath / Fix |
| :--- | :--- | :--- | :--- |
| `pages/HomeScreen.java:30, 105` | `contains(@content-desc, 'Home')` | Ambiguously matches the bottom-nav `"Home"` tab (`@content-desc='Home'`) before scrolling reaches the Home Insurance tile. | `//*[@resource-id='btn_home_product_homeInsurance' or (contains(@content-desc, 'Home') and contains(@content-desc, 'Insurance'))]` |
| `pages/CheckoutScreen.java:18-30` | Motor-only primary `@resource-id`s (`btn_checkout_page_motor_*`, `textfield_checkout_page_motor_*`, `tgl_checkout_page_motor_checkbox`) | Shared Checkout (Travel, Home, Malpractice, Domestic Worker, Health, Income, Pet) uses generic IDs (`btn_payment_mode_moyasar_direct_pay`, `textfield_email`, `textfield_iban`, `tgl_checkbox`, `btn_pay_now`), plus National Address fields (`textfield_building_number`, etc.) are absent from `CheckoutScreen`. | `//*[@resource-id='btn_payment_mode_moyasar_direct_pay' or @resource-id='btn_checkout_page_motor_payment_mode_direct_pay_post_card' or contains(@content-desc, 'Credit') or contains(@content-desc, 'Debit')]` ; for Pay Now: `//android.widget.Button[@clickable='true' and @content-desc='Pay now'] \| //*[@clickable='true' and (@resource-id='btn_pay_now' or @resource-id='btn_checkout_page_motor_pay_now')]` ; route T&C through `clickPrivacySwitch(...)`. |
| `pages/CheckoutScreen.java:54, 59` | `waitForVisibility(emailField)` (`54`) + `contains(@hint, 'email')` (`59`) | Line `54` stalls **20s** when Email is absent on Checkout (e.g., Malpractice); line `59` is lowercase `email` which fails XPath 1.0 case-sensitive match against `hint="Email address"`. | Guard with `findElements(...)` (`0s` check) and match `//*[@resource-id='textfield_email' or @resource-id='textfield_checkout_page_motor_email' or contains(@hint, 'Email') or contains(@hint, 'email')]`. |
| `pages/AddNewCardModal.java:33-45` | Card fields omit `@hint`; `saveButton` (`45`) targets outer wrapper `btn_credit_debit_card_bottom_sheet_save`; line `177` calls `isElementVisible(saveButton)` | Missing `@hint` fallbacks on Card number / Expiry / CVV / Holder name; line `177` triggers **10s PageFactory proxy timeout** if `a11y-important="false"` hides `saveButton` under `ignoreUnimportantViews=true`. | Include `contains(@hint, 'Card number')`, `contains(@hint, 'Expiry')`, `contains(@hint, 'CVV')`, `contains(@hint, 'Card holder')` on inputs; replace `isElementVisible(saveButton)` with `isElementVisible(By.xpath("//android.widget.Button[@clickable='true' and @content-desc='Save']"))`. |
| `pages/AddNewCardModal.java:63-64` | `(//android.widget.SeekBar)[1]`, `[2]` | Positional SeekBars (mitigated at runtime by `\d{4}` year check, brittle if 3rd SeekBar appears behind sheet). | Keep dynamic year detection and restrict to visible modal bounds (`y > windowHeight * 0.45`). |
| `pages/OtpModal.java:36, 127` | `//*[@resource-id='btn_verify_otp' or @resource-id='btn_verify' or @content-desc='Verify' or ...]` | Does not prefer clickable inner `Button` when outer `btn_verify_otp` wrapper has `a11y-important="false"`. | `//android.widget.Button[@clickable='true' and (@content-desc='Verify' or @content-desc='Confirm')] \| //*[@clickable='true' and (@resource-id='btn_verify_otp' or @resource-id='btn_verify')]` |
| `pages/PolicyConfirmationScreen.java:35, 184` | `//android.widget.FrameLayout[@resource-id='android:id/content']//android.widget.ImageView[1]` (`35`); `(//android.widget.ImageView)[1]` (`184`) | First `ImageView` on confirmation/receipt screens can be SAR icon, badge, or mascot illustration rather than the Close (`X`) icon. | Prefer `//*[@resource-id='btn_close' or @resource-id='btn_close_button' or @content-desc='Close' or @text='Close']` and only fall back to top-right/top-left header bounds `ImageView[@clickable='true']`. |

#### 1. Motor (`AboutYouScreen`, `MotorCoverageSelectionScreen`, `VehicleDetailsModal`, `QuoteScreen`)

| File & Line(s) | Current Broken / Brittle Locator | Root Cause | Recommended Robust XPath / Fix |
| :--- | :--- | :--- | :--- |
| `pages/AboutYouScreen.java:23` | `//*[@resource-id='tgl_about_you_page_motor_newInsurance' or @content-desc='New Insurance' or @text='New Insurance']` | `tgl_about_you_page_motor_newInsurance` does **not** exist in DOM (`testMotorFlow1_20261115`, `testMotorFlow7_20261115`); tab is `Button[@content-desc="New Insurance" and @resource-id=""]`. | `//android.widget.Button[@clickable='true' and @content-desc='New Insurance']` |
| `pages/AboutYouScreen.java:26, 29, 331, 364` | `@resource-id='radio_btn_about_you_page_motor_custom_card'` / `..._sequence_number` | `radio_btn_about_you_page_motor_*` are `clickable="false" a11y-important="false"` wrappers (`testMotorFlow7_DOM.xml:14-18`); PageFactory/XPath matches wrapper instead of clickable inner `Button`. | `//android.widget.Button[@clickable='true' and @content-desc='Custom Card'] \| //*[@resource-id='radio_btn_about_you_page_motor_custom_card']//*[@clickable='true']` (and matching `'Sequence Number'`). |
| `pages/AboutYouScreen.java:44, 179` | `@resource-id='textfield_about_you_page_motor_dob'` / `@content-desc='Month/Year of birth'` | Missing `@hint='Month/Year of birth'` (DOM `testMotorFlow7_DOM.xml:22` stores label in `hint="Month/Year of birth"`, empty `@content-desc` & `@text`). | `//*[@resource-id='textfield_about_you_page_motor_dob' or @hint='Month/Year of birth' or contains(@hint, 'birth') or @content-desc='Month/Year of birth']` |
| `pages/VehicleDetailsModal.java:31, 35` | `CAR_MAKE_FIELD = "(//android.widget.EditText)[1]"`, `CAR_MODEL_FIELD = "(//android.widget.EditText)[2]"` | Pure positional XPaths can target background Motor About You `EditText` nodes if Flutter tree exposes underlying views. | `//android.widget.EditText[contains(@hint, 'Make') or contains(@hint, 'make') or contains(@resource-id, 'make')]` and `//android.widget.EditText[contains(@hint, 'Model') or contains(@hint, 'model') or contains(@resource-id, 'model')]` |
| `pages/QuoteScreen.java:63, 69, 79, 85, 91, 97` | `//*[contains(@text, 'Politically Exposed Person')]`, `(//*[@text='Yes'])[1]`, `(//*[@text='No'])[1]`, `[2]` | Flutter exports PEP / Related Party text & buttons in `@content-desc` and `btn_yes_button` / `btn_no_button` (`@text` is `""`). | `(//android.widget.Button[@resource-id='btn_yes_button' or @content-desc='Yes'])[1]` / `(//android.widget.Button[@resource-id='btn_no_button' or @content-desc='No'])[1]` (and `[2]`); label `//*[contains(@content-desc, 'Politically Exposed')]`. |
| `pages/QuoteScreen.java:269, 282-285` | `totalPremiumText.getText()` (`269`); `'Cover'`/`'SAR'` marker (`282-285`) | `getText()` returns empty on Flutter `@content-desc` views; `'Cover'` matches non-quote cards if Buy now hasn't loaded yet. | Read `content-desc` fallback when `.getText()` is empty; gate `isPageLoaded()` on `//android.widget.Button[@clickable='true' and @content-desc='Buy now'] \| //*[@resource-id='btn_quote_page_motor_buy_now']`. |

#### 2. Travel (`TravelAboutYouScreen`)

| File & Line(s) | Current Broken / Brittle Locator | Root Cause | Recommended Robust XPath / Fix |
| :--- | :--- | :--- | :--- |
| `pages/TravelAboutYouScreen.java:39, 43` | `(//android.widget.ImageView[contains(@resource-id, 'textfield')])[1]` / `[2]` | Positional index shifts whenever `textfield_dob` (`ImageView` containing `textfield`) is visible on initial Trip screen or Multi-Trip layout. | `//android.widget.ImageView[contains(@resource-id, 'start_date') or contains(@hint, 'Start') or contains(@hint, 'start')]` and `...['end_date' / 'End']` |
| `pages/TravelAboutYouScreen.java:319` | `//android.widget.ImageView[not(@resource-id='btn_back') and not(contains(@content-desc, 'back'))] \| //*[contains(@resource-id, 'dob') or @content-desc='Month/Year of birth']` | First union branch matches **any** non-back `ImageView` on screen (often Calendar/Destination dropdown icon) BEFORE reaching `dob`. | `//*[@resource-id='textfield_dob' or @resource-id='textfield_month_year_of_birth' or @resource-id='textfield_month_year_of_birth_txt' or @hint='Month/Year of birth' or contains(@hint, 'birth') or @content-desc='Month/Year of birth']` |
| `pages/TravelAboutYouScreen.java:47, 61, 64, 555` | Phone/NID fields lack all hint variants (`Phone number`); line `555` raw `tgl_checkbox` click without `clickPrivacySwitch(...)` | Travel UAT Checkout `tgl_checkbox` is `a11y-important="false"` (`ignoreUnimportantViews=true` hides line `555`); `phoneNumber` DOM hint can be `'Phone number'`. | Include `contains(@hint, 'Phone')` on phone field; replace line `555-556` with `clickPrivacySwitch(By.xpath("//*[@resource-id='tgl_checkbox' or @class='android.widget.Switch']"))`. |

#### 3. Home Insurance (`HomeInsuranceScreen`)

| File & Line(s) | Current Broken / Brittle Locator | Root Cause | Recommended Robust XPath / Fix |
| :--- | :--- | :--- | :--- |
| `pages/HomeInsuranceScreen.java:83, 127, 138-140` | `@resource-id='textfield_month_year_of_birth_txt'` / `@content-desc='Month/Year of birth'`; single blind swipe `138-140` | Missing `@hint='Month/Year of birth'` / `contains(@hint, 'birth')`; single wheel swipe often fails to land on valid DOB year (`1998`). | Add `contains(@hint, 'birth')` / `@hint='Month/Year of birth'` and replace lines `122-149` with `scrollDobWheelAndConfirm(...)`. |
| `pages/HomeInsuranceScreen.java:155-160, 423` | Raw `driver.findElement(By.xpath("//*[@resource-id='tgl_checkbox'..."))` | `tgl_checkbox` (`a11y-important="false"`) is stripped when `ignoreUnimportantViews=true`. | Call `BasePage.clickPrivacySwitch(By.xpath("//*[@resource-id='tgl_checkbox' or @class='android.widget.Switch']"))` in both `acceptPrivacyNotice()` & Checkout. |
| `pages/HomeInsuranceScreen.java:192, 197` | `@class='android.widget.EditText'` (`192`); `(//android.widget.EditText)[1]` (`197`) | Step 1 About You has no `textfield_email`; on Step 2 About Your Home, matching the 1st `EditText` can overwrite Building/National ID if email is conditional. | `//android.widget.EditText[@resource-id='textfield_email' or contains(@hint, 'Email') or contains(@hint, 'email')]` |
| `pages/HomeInsuranceScreen.java:210-218` | `if (cleanEligibility) { for (WebElement noBtn : noButtons) ... }` | Logic bug vs comment (`line 210`): answers `"No"` to **all 5** eligibility questions even though Q3 (concrete walls/roof) and Q4 (good repair) must be answered `"Yes"` for clean eligibility. | Answer `[No, No, Yes, Yes, No]` selectively by visible button index / question text. |
| `pages/HomeInsuranceScreen.java:29, 89, 334, 385, 446` | Wrapper IDs (`btn_next_button_product_landing_page_`, `btn_next_button`, `btn_buy_now`, `btn_pay_now`) without clickable child filter | `a11y-important="false"` wrappers are stripped under `ignoreUnimportantViews=true`; `isElementVisible` on `@AndroidFindBy` fails cleanly only via fallback. | Prefer `//android.widget.Button[@clickable='true' and @content-desc='<Label>']` via `tapPreferredClickable(...)`. |

#### 4. Pet Insurance (`PetScreen`)

| File & Line(s) | Current Broken / Brittle Locator | Root Cause | Recommended Robust XPath / Fix |
| :--- | :--- | :--- | :--- |
| `pages/PetScreen.java:39, 198` | Line `39` has no `@hint`; fallback line `198` uses `contains(@hint, 'Name')` | Live DOM (`testPetFlow4_20261113_DOM.xml:11`) shows `hint="Your Pet’s name"` (lowercase `name` + curly apostrophe `’`). Case-sensitive XPath 1.0 `contains(@hint, 'Name')` misses it! | `//*[@resource-id='textfield_your_pet_name' or @resource-id='textfield_pet_name' or contains(@hint, 'name') or contains(@hint, 'Pet')]` |
| `pages/PetScreen.java:328-334` | `//*[contains(@content-desc, 'age')]/following::android.view.View[@resource-id='']` (`boxIndex=0/1`) | Relies on sibling index after `Your pet’s age`; validation errors (`testPetFlow4_DOM.xml:23`) and empty container Views (`24-25`) risk wrong index selection while `@hint="Years"` and `@hint="Months"` exist directly on the trigger Views (`lines 20, 22`). | `//android.view.View[@hint='Years' or @content-desc='Years']` and `//android.view.View[@hint='Months' or @content-desc='Months']` |
| `pages/PetScreen.java:767` | `//*[@resource-id='textfield_month_year_of_birth_txt' or contains(@resource-id, 'birth')]` | Owner Details DOB check misses `@hint='Month/Year of birth'` / `@content-desc='Month/Year of birth'`. | Add `or @hint='Month/Year of birth' or contains(@hint, 'birth') or @content-desc='Month/Year of birth'`. |
| `pages/PetScreen.java:482-485, 771, 878` | Raw `@AndroidFindBy` / `driver.findElements` on `tgl_checkbox` | `tgl_checkbox` (`a11y-important="false"`, `testPetFlow4_DOM.xml:39`) is invisible under `ignoreUnimportantViews=true`. | Use `clickPrivacySwitch(...)` / `findWithUnimportantViews(...)` in `acceptPrivacyNotice()`, Owner Details, and Checkout T&C. |

#### 5. Income Insurance (`IncomeInsuranceScreen`)

| File & Line(s) | Current Broken / Brittle Locator | Root Cause | Recommended Robust XPath / Fix |
| :--- | :--- | :--- | :--- |
| `pages/IncomeInsuranceScreen.java:44, 229-234` | `@resource-id='textfield_month_year_of_birth_txt' or contains(@resource-id, 'dob') or contains(@resource-id, 'birth')`; single swipe `232` | Missing `@hint='Month/Year of birth'` / `@content-desc='Month/Year of birth'`; single blind wheel swipe misses target birth year `1998`. | Add `@hint` / `@content-desc` attributes and delegate `selectDob()` to `scrollDobWheelAndConfirm(dobInput)`. |
| `pages/IncomeInsuranceScreen.java:283-288, 563, 664, 670` | Raw `tgl_checkbox` queries without `findWithUnimportantViews` | Under `ignoreUnimportantViews=true`, privacy (`283`), declaration (`563`), and checkout T&C (`664, 670`) `Switch` elements are hidden (`a11y-important="false"`). | Use `BasePage.clickPrivacySwitch(...)` (`findWithUnimportantViews`). |
| `pages/IncomeInsuranceScreen.java:50, 71, 86, 300-304` | `clickNext()` (`300-304`) taps `@AndroidFindBy` wrapper `nextButton` with no fallback click if invisible | Outer `btn_next_button` is `a11y-important="false"`; if `isElementVisible(nextButton)` returns `false` (after burning 10s), `else` is an empty comment! | Use `tapPreferredButton(By.xpath("//android.widget.Button[@clickable='true' and @content-desc='Next']"), nextButton)`. |

#### 6. Medical Malpractice (`MalpracticeScreen`)

| File & Line(s) | Current Broken / Brittle Locator | Root Cause | Recommended Robust XPath / Fix |
| :--- | :--- | :--- | :--- |
| `pages/MalpracticeScreen.java:239-240` | `//*[@resource-id='btn_claims_yes' or (//*[@text='Yes' or @content-desc='Yes'])[last()]]` and `btn_claims_no` | **Critical XPath 1.0 Truthiness Bug:** nested `(//*[@text='Yes' or @content-desc='Yes'])[last()]` evaluates `true` for every element in the tree, matching root `<FrameLayout>` (`bounds="[0,0][1080,2400]"`). | `(//*[@resource-id='btn_claims_yes' or @resource-id='btn_yes_button' or @content-desc='Yes' or @text='Yes'])[last()]` (and `'No'` / `btn_no_button`). |
| `pages/MalpracticeScreen.java:90` | `@resource-id='textfield_month_year_of_birth_txt' or contains(@resource-id, 'dob') or contains(@resource-id, 'birth')` | Missing `@hint='Month/Year of birth'` / `@content-desc='Month/Year of birth'`. | Add `or contains(@hint, 'birth') or @content-desc='Month/Year of birth'`. |
| `pages/MalpracticeScreen.java:251, 483` | Raw `driver.findElement(By.xpath("//*[@resource-id='tgl_checkbox'..."))` | Stripped when `ignoreUnimportantViews=true`. | Call `clickPrivacySwitch(By.xpath("//*[@resource-id='tgl_checkbox' or @class='android.widget.Switch']"))`. |
| `pages/MalpracticeScreen.java:93, 296, 308` | `contains(@hint, 'Profession')`, `'License'`, `'Hospital'` (Title Case only) | XPath 1.0 `contains()` is case-sensitive; lowercase hints like `'Enter license number'` miss `'License'`. | Add `contains(@hint, 'profession')` / `contains(@hint, 'license')` / `contains(@hint, 'hospital')` fallbacks. |

#### 7. Domestic Worker (`DomesticWorkerScreen`)

| File & Line(s) | Current Broken / Brittle Locator | Root Cause | Recommended Robust XPath / Fix |
| :--- | :--- | :--- | :--- |
| `pages/DomesticWorkerScreen.java:78` | `@resource-id='textfield_month_year_of_birth_txt' or contains(@resource-id, 'dob') or contains(@resource-id, 'birth')` | Missing `@hint='Month/Year of birth'` / `@content-desc='Month/Year of birth'`. | Add `or contains(@hint, 'birth') or @content-desc='Month/Year of birth'`. |
| `pages/DomesticWorkerScreen.java:137-140, 311, 406` | Raw `tgl_checkbox` lookups in `acceptPrivacyNotice()`, Emergency addon (`311`), and Checkout T&C (`406`) | `Switch` (`a11y-important="false"`) is stripped when `ignoreUnimportantViews=true`. | Route all 3 through `clickPrivacySwitch(...)` / `findWithUnimportantViews(...)`. |
| `pages/DomesticWorkerScreen.java:173, 188, 258, 366` | Worker Iqama lacks `'National ID/Iqama'` hint; `buyNowButton`/`payNowButton` lack inner clickable `Button` targeting | Wrapper CTAs (`btn_buy_now`, `btn_pay_now`) can fail under `ignoreUnimportantViews=true`. | Add `@hint='National ID/Iqama'` to worker ID and use `//android.widget.Button[@clickable='true' and @content-desc='Buy now']` / `'Pay now'`. |

#### 8. Individual Health (`IndividualHealthScreen`)

| File & Line(s) | Current Broken / Brittle Locator | Root Cause | Recommended Robust XPath / Fix |
| :--- | :--- | :--- | :--- |
| `pages/IndividualHealthScreen.java:398, 401, 415` | `goldPlanCard` & `platinumPlanCard` both check `contains(@content-desc, 'Beyond') or contains(@content-desc, 'Diamond')`; `waitForPlanSelectionScreen` (`415`) matches `Primary`/`Diamond`/`Beyond`/`Silver` | Overlapping keywords click the wrong tier; `waitForPlanSelectionScreen` matches plan cards **already rendered on the Landing screen**, giving false-positive readiness! | Differentiate tiers (`Diamond` vs `Diamond Plus` / `Beyond`); require quote CTA (`@content-desc='Buy now'` or `'SAR'`) in `waitForPlanSelectionScreen`. |
| `pages/IndividualHealthScreen.java:87` | `dobInput` misses `@hint` / `@content-desc` | Fails if `textfield_month_year_of_birth_txt` is omitted in another build variant. | Add `or contains(@hint, 'birth') or @content-desc='Month/Year of birth'`. |
| `pages/IndividualHealthScreen.java:34-40, 183-186, 647` | `isInsureNowButtonVisible()` calls **20s** `waitForVisibility(insureNowButton)`; Privacy (`183-186`) & Checkout (`647`) skip `clickPrivacySwitch(...)` | Checking visibility via `waitForVisibility` burns 20s whenever Landing already skipped/dismissed; raw `Switch` lookup fails under `ignoreUnimportantViews=true`. | Change `isInsureNowButtonVisible()` to instant `isElementVisible(By)` and switch `tgl_checkbox` calls to `clickPrivacySwitch(...)`. |

#### 9. Family Health (`FamilyHealthScreen`)

| File & Line(s) | Current Broken / Brittle Locator | Root Cause | Recommended Robust XPath / Fix |
| :--- | :--- | :--- | :--- |
| `pages/FamilyHealthScreen.java:393, 396, 410` | Same `Gold`/`Platinum` overlap (`Beyond` & `Diamond` in both) + Landing-screen false positive in `waitForPlanSelectionScreen` (`410`) | Live DOM (`testFamilyHealthFlow2_20261113_DOM.xml:13, 39`) renders **`Diamond Plus`** and **`Diamond`** directly on the Landing screen! | Match distinct `'Diamond Plus'` vs `'Diamond'` carefully (`and not(contains(@content-desc, 'Plus'))`) and require `'Buy now'` / `'SAR'` on the Quote screen. |
| `pages/FamilyHealthScreen.java:34-40, 80, 175-178, 645` | `clickInsureNow()` line `80` checks `if (!isInsureNowButtonVisible())` **after** tapping `Insure now`, where `isInsureNowButtonVisible()` calls **20s `waitForVisibility(insureNowButton)`**! Privacy & Checkout T&C skip `clickPrivacySwitch(...)`. | Every successful `clickInsureNow()` triggers a **20-second forced wait** waiting for the already-navigated `Insure now` button to reappear before exiting the `for` loop! | Replace `isInsureNowButtonVisible()` with `isElementVisible(By.xpath("//android.widget.Button[@clickable='true' and @content-desc='Insure now']"))` (`0s` check); add `@hint` to `sponsorDobInput` (`116`) & use `clickPrivacySwitch(...)`. |

---

### C. Performance Bottleneck Audit & Ranked Speedups

#### 1. Verified `Thread.sleep(...)` Inventory Across `src/` (287 calls / 392,671 ms = ~6.54 min static per single pass)

| File | Calls | Total Fixed Sleep (ms) | Primary Multipliers / Hotspots |
| :--- | ---: | ---: | :--- |
| `src/main/java/pages/PetScreen.java` | 40 | 49,100 | Dropdown/age/breed/step transitions |
| `src/main/java/pages/IncomeInsuranceScreen.java` | 25 | 33,100 | Landing, DOB, employment, declaration, quote |
| `src/main/java/pages/FamilyHealthScreen.java` | 23 | 32,200 | Landing (`4000+2000+1500+2000`), dependents, quote |
| `src/main/java/pages/IndividualHealthScreen.java` | 21 | 28,600 | Landing, dependents, declaration, quote |
| `src/main/java/pages/HomeInsuranceScreen.java` | 17 | 23,500 | Landing, About Home (`2500`), declarations (`5 x 300`) |
| `src/main/java/pages/MalpracticeScreen.java` | 14 | 21,900 | Landing (`2000+2000+2500+2500`), Next (`3000`) |
| `src/main/java/pages/VehicleDetailsModal.java` | 17 | 20,600 | Make/Model dropdowns, Proceed retries (`2500 x 2`) |
| `src/main/java/pages/TravelAboutYouScreen.java` | 14 | 18,800 | Dropdown/calendar (`1500+1000 x 3`), OTP/Nav waits |
| `src/main/java/pages/AboutYouScreen.java` | 14 | 15,600 | Model year picker, DOB, keyboard settle |
| `src/main/java/pages/OtpModal.java` | 11 | 14,770 | Unconditional `3000` (`134`) + `4000` (`166`); recovery `4000+1000+500+5*120` |
| `src/main/java/pages/BasePage.java` | 17 | 13,800 | `checkAndMarkUatElmDependentBlock` unconditional `4000` (`686`); `waitUntil` `500` poll (`103`) |
| `src/test/java/tests/SampleTest.java` | 12 | 13,200 | Multiplied across **12 DataProvider executions** |
| `src/main/java/pages/PolicyConfirmationScreen.java` | 4 | 13,000 | `5000` (`55`) + `5000` (`87`) + `1000` (`123`) in up to 90s loop |
| `src/main/java/pages/DomesticWorkerScreen.java` | 9 | 12,800 | Landing (`1500+2000+2000`), Buy/Proceed (`2500`) |
| `src/test/java/tests/TravelTest.java` | 4 | 9,500 | Manual post-save/navigation sleeps (`2000`, `2500 x 3`) |
| `src/main/java/pages/AddNewCardModal.java` | 8 | 7,500 | **Loop-multiplied**: up to `8 x 500` year + `4 x 500` month SeekBar wheel loops |
| `src/main/java/pages/HomeScreen.java` | 4 | 6,800 | App settle & scroll pauses (`2000 x 3`, `800`) |
| `src/main/java/utils/DriverManager.java` | 4 | 6,300 | `1500` (`129`), `1000` (`252`), `800 x <=5` (`266`), `3000` (`275`) |
| 7 Product Test Classes (`Malpractice`, `HomeInsurance`, `IncomeInsurance`, `IndividualHealth`, `FamilyHealth`, `Pet`, `DomesticWorker`) | 14 (2 each) | 35,000 (5,000 each) | Duplicated `Thread.sleep(2500)` after `cardModal.clickSave()` |
| `src/main/java/pages/CheckoutScreen.java` | 8 | 4,600 | Field/scroll settles (`500` / `800`) |
| `src/main/java/pages/MotorCoverageSelectionScreen.java` | 2 | 4,500 | Landing waits (`2500`, `2000`) |
| `src/test/java/base/BaseTest.java` | 2 | 3,500 | Pre/post reset pauses (`2000`, `1500`) |
| `src/test/java/tests/MotorTest.java` | 1 | 2,500 | Post-save `2500` |
| `src/main/java/pages/QuoteScreen.java` | 1 | 1,500 | Promo Apply (`247`) |
| `src/main/java/utils/SequenceNumberGenerator.java` | 1 | 1 | `Thread.sleep(1)` (`14`) |
| **Totals by Layer** | **287** | **392,671** | **`pages`: 249 (322,670 ms) · `tests`: 31 (60,200 ms) · `base/utils`: 7 (9,801 ms)** |

#### 2. Ranked Performance Optimizations (Highest ROI First)

1. **Rank 1 — Fix PageFactory Proxy (`AppiumFieldDecorator(..., 10s)`) & `waitForVisibility` (20s) Traps in Polling/Branching Checks** *(Estimated savings: 20–60s on payment/landing transitions)*
   - `PolicyConfirmationScreen.java:110, 115, 130` calls `isElementVisible(readyToRollHeader)` and `isElementVisible(viewPolicyButton)` inside the `isLoaded()` loop (`line 109`). Because both are `@AndroidFindBy` proxies initialized with `new AppiumFieldDecorator(driver, Duration.ofSeconds(10))` (`line 23`), each absent element blocks **10 seconds** — turning every `1,000 ms` poll iteration into **20–40 seconds** while 3DS/confirmation loads. **Fix:** Query `isElementVisible(By)` (`0s` implicit wait since `implicitlyWait` is disabled at `DriverManager.java:173`) and lower `AppiumFieldDecorator` timeout to `Duration.ofSeconds(0)`–`2`.
   - `FamilyHealthScreen.java:34-40, 80` (`isInsureNowButtonVisible` called after tapping `Insure now`) and `CheckoutScreen.java:54` (`enterEmail` calling `waitForVisibility(emailField)` when optional) trigger **20-second** `TestConfig.elementTimeout()` (`uat.properties`: `timeout.element.visible=20`) waits on happy paths. **Fix:** Replace presence/absence checks with `isElementVisible(By)` and reduce `timeout.element.visible` from `20` to `10`–`12` (retaining `30s`–`45s` explicit `WebDriverWait` only for async quote/OTP/policy issuance).

2. **Rank 2 — Disable / Exclude Redundant `SampleTest` in `testng.xml`** *(Eliminates 12 redundant full Motor E2E invocations)*
   - `src/test/resources/testng.xml` includes `<class name="tests.SampleTest"/>` inside `"Motor Product Test"`, executing 4 `@Test` methods (`SampleTest.java:1490-1726`) x 3 `@DataProvider(name="coverTypes")` rows (`1330-1362`: `Comprehensive`, `Smart`, `Saver`) = **12 full Motor E2E runs** (with `12` static `Thread.sleep` calls totaling `13,200 ms` in `SampleTest`) that overlap `MotorTest`. **Fix:** Comment out or remove `tests.SampleTest` from `testng.xml` when `MotorTest` covers all 7 Motor flows.

3. **Rank 3 — Eliminate Double `DriverManager.resetAppToHome()` Calls & Replace 5x `BACK` Loop With Direct Reset** *(Estimated savings: ~4–8s per reset cycle, plus eliminating duplicate resets)*
   - `BaseTest.java:95` (`@AfterMethod`) resets after every test, yet `TravelTest.java:242` (`@AfterMethod`) AND `NegativeValidationTest.java:63, 80, 109, 128, 145` call `DriverManager.resetAppToHome()` a second time. **Fix:** Remove the duplicate `resetAppToHome()` calls from `TravelTest` and `NegativeValidationTest`.
   - In `DriverManager.resetAppToHome()` (`221-281`), looping up to 5 `BACK` presses x `800 ms` sleep + `1000 ms` Home-tab check before calling `terminateApp`/`activateApp` + `3000 ms` sleep consumes ~8s when deep in Checkout/3DS/Quote. **Fix:** If `btn_home_product_motor` isn't visible after 1 quick Back/Home-tab check, immediately execute `terminateApp(pkg)` + `activateApp(pkg)` (or `adb shell am force-stop <pkg> && adb shell am start -n <pkg>/<activity>`) and replace the fixed `Thread.sleep(3000)` (`275`) with dynamic `waitUntil(() -> isMotorTileVisible, 10)`.

4. **Rank 4 — Replace Unconditional Multi-Second Post-Action Sleeps (`OtpModal`, `BasePage`, `PolicyConfirmationScreen`, Product Tests) With Dynamic `waitUntil`** *(Estimated savings: ~10–30s per E2E flow)*
   - `OtpModal.java:134, 166`: `3,000 ms` + `4,000 ms` unconditional sleeps even when OTP passes immediately; replace with `waitUntil` checking OTP modal dismissal, error retry, or next screen.
   - `BasePage.java:686` (`checkAndMarkUatElmDependentBlock`): unconditional `4,000 ms` sleep even when Quote loads quickly; poll `waitUntil` for dependent error OR quote card.
   - `PolicyConfirmationScreen.java:55, 87`: two `5,000 ms` sleeps after 3DS Submit; poll immediately for `readyToRollHeader` / `Purchase failed` instead of sleeping 5s first.
   - Product Test Classes (`DomesticWorkerTest`, `FamilyHealthTest`, `HomeInsuranceTest`, `IncomeInsuranceTest`, `IndividualHealthTest`, `MalpracticeTest`, `PetTest`, `MotorTest`, `TravelTest`): remove the repeated post-`clickSave()` `Thread.sleep(2500)` calls (already handled dynamically by `BasePage.isPaymentOrConfirmationAlreadyActive()` and `PolicyConfirmationScreen.isLoaded()`).

5. **Rank 5 — Replace Remaining Page Object Sleeps (249 calls / 322,670 ms in `pages/*.java`) With `BasePage.waitUntil(...)` & `200ms` Polling** *(Estimated savings: ~15–40s per multi-step E2E flow)*
   - Replace fixed `1500 ms`–`3000 ms` screen transition sleeps (`PetScreen`, `IncomeInsuranceScreen`, `FamilyHealthScreen`, `IndividualHealthScreen`, `HomeInsuranceScreen`, `MalpracticeScreen`, `VehicleDetailsModal`) with `waitUntil(targetLocatorVisible, timeout)` and reduce `BasePage.waitUntil` (`line 103`) poll interval from `500 ms` to `200 ms`.
   - In `AddNewCardModal.enterExpiryDate()` (`86-135`), reduce unnecessary `8 x 500 ms` year + `4 x 500 ms` month wheel sleep loops via calibrated single-drag deltas where wheel bounds allow.

6. **Rank 6 — Appium Capabilities & Settings Tuning (`DriverManager.java:87-118`)** *(Preserves fast XPath & avoids animation hangs)*
   - Retain `appium:waitForIdleTimeout=0` (`109`), `appium:enforceXPath1=true` (`111`), `noReset=true` (`87`), and `skipUnlock=true` (`93`) — these eliminate Flutter animation-idle stalls during `findElement`.
   - Retain `appium:ignoreUnimportantViews=true` (`97`) ONLY alongside explicit `findWithUnimportantViews(...)` for `Switch` toggles (`tgl_checkbox`, `tgl_about_you_page_motor_checkbox`) and inner `clickable="true"` `Button` XPaths — or toggle `ignoreUnimportantViews=false` cleanly when switch/wrapper reliability outweighs XML payload reduction.

