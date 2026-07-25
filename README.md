# 🚀 Tree Digital Insurance - Enterprise Appium Mobile Automation Framework

An end-to-end, self-healing mobile test automation framework built for **Tree Digital Insurance (UAT)** Android app using **Appium 2.x**, **Java 17**, **TestNG**, and **GitHub Actions**.

---

## 📋 Table of Contents
1. [Business Overview & E2E Flows](#-business-overview--e2e-flows)
2. [Technical Architecture](#-technical-architecture)
3. [Key Framework Innovations](#-key-framework-innovations)
4. [Project Directory Structure](#-project-directory-structure)
5. [Prerequisites & Local Setup](#-prerequisites--local-setup)
6. [Running the Test Suite](#-running-the-test-suite)
7. [CI/CD Daily Automation (GitHub Actions)](#-cicd-daily-automation-github-actions)

---

## 🏢 Business Overview & E2E Flows

This framework automates the full end-to-end customer journey for purchasing motor insurance policies on the Tree Digital Insurance mobile app, covering quote generation, vehicle selection, checkout, payment processing, and policy confirmation.

### Automated Business Flows:

```
                  ┌─────────────────────────────────────────────────────────┐
                  │                 Motor Product Selection                 │
                  └────────────────────────────┬────────────────────────────┘
                                               │
               ┌───────────────────────────────┴───────────────────────────────┐
               ▼                                                               ▼
    [ Flow 1: New Insurance ]                                     [ Flow 3: Ownership Transfer ]
   Sequence Number / Custom Card                                   Seller ID / Sequence Number
               │                                                               │
               ├───────────────────────────────┬───────────────────────────────┤
               ▼                               ▼                               ▼
    ( Comprehensive Cover )             ( Smart Cover )                 ( Saver Cover )
               │                               │                               │
               └───────────────────────────────┼───────────────────────────────┘
                                               ▼
                                   [ Checkout & Payment ]
                                    Email / IBAN / Credit Card
                                               │
                                               ▼
                                 [ Policy Confirmation Page ]
                                "All set! You're ready to roll!"
```

1. **Flow 1 (New Insurance + Sequence Number)**:
   - Automated registration with dynamic National ID & Sequence Number.
   - Native OTP verification injection.
   - Comprehensive, Smart, and Saver Cover card expansion & selection.
   - Checkout auto-filling (Email, IBAN) and 3D-Secure Credit Card entry with wheel picker date calibration.
   - Policy confirmation assertion (`"All set! You're ready to roll!"`).

2. **Flow 2 (New Insurance + Custom Card)**:
   - Custom vehicle registration with model year selection (`2026`).
   - Dynamic search for vehicle make (`"b.m.w"`) and first model selection inside the *"We need more details about your vehicle"* modal.
   - Full checkout, payment, and policy confirmation across Comprehensive, Smart, and Saver covers.

3. **Flow 3 (Ownership Transfer + Sequence Number)**:
   - Transfer tab activation, Seller ID entry, and Sequence Number radio button selection (`radio_btn_about_you_page_motor_sequence_number`).
   - OTP processing, quote selection, payment, and policy confirmation across Comprehensive, Smart, and Saver covers.

---

## 🛠 Technical Architecture

- **Core Engine**: Appium 2.x Java Client (`io.appium:java-client 9.2.2`) with `UiAutomator2` driver.
- **Test Runner**: TestNG (`7.9.0`) with parallel execution configuration.
- **Design Pattern**: Page Object Model (POM) with PageFactory lazy element initialization.
- **Build System**: Apache Maven (`pom.xml`).
- **CI/CD Engine**: GitHub Actions running cloud Android Emulators on macOS runners.

---

## 🔥 Key Framework Innovations

### 1. 🛡️ Self-Healing Popup Interceptor Engine (`BasePage.java`)
- Automatically intercepts and dismisses unexpected obstructing popups (system permissions, upsell banners, promo dialogs, "Skip", "Dismiss", "Not now", "Close") whenever an element click fails.
- Once auto-dismissed, the framework retries the primary user operation smoothly without failing the test execution.

### 2. 🔢 Persisted Dynamic Data Generators (`NationalIdGenerator` & `SequenceNumberGenerator`)
- Maintains disk-persisted counters (`national_id_counter.properties` and `sequence_number_counter.properties`).
- Automatically increments the **last 3 digits (`+1`)** for both National ID (`1354545XXX`) and Sequence Number (`704848XXX`) on every test case run.
- **Business Impact**: Prevents backend API policy duplicate errors (Najm / Tawuniya) across repeated local and CI/CD test runs.

### 3. 📝 Failure Artifacts Collector (`TestListener.java`)
- Automatically listens to test events via TestNG `ITestListener`.
- Upon test failure, it captures:
  - High-res PNG Screenshot saved to `target/screenshots/{TestName}_{Timestamp}.png`.
  - Complete Appium DOM XML Tree saved to `target/screenshots/{TestName}_{Timestamp}_DOM.xml`.

### 4. 🔄 Flaky Test Retry Engine (`RetryAnalyzer.java` & `AnnotationTransformer.java`)
- Implements `IRetryAnalyzer` and `IAnnotationTransformer` to automatically retry transient network glitches or backend SMS delays **once** before declaring a failure.

### 5. ⚡ Smart Form Auto-Filler (`TestDataBuilder.java`)
- Centralized data model (`CustomerProfile` & `CreditCard`) enabling one-line form auto-filling for Checkout (`autoFillCheckout`) and Add New Card (`autoFillCardDetails`).

---

## 📁 Project Directory Structure

```text
appium-testng-framework/
├── .github/workflows/
│   └── daily_tests.yml                  # Daily CI/CD pipeline on Android Cloud Emulator
├── src/
│   ├── main/java/
│   │   ├── pages/
│   │   │   ├── BasePage.java            # Base page with Self-Healing, gestures & waits
│   │   │   ├── HomeScreen.java          # Home screen navigation
│   │   │   ├── MotorCoverageSelectionScreen.java
│   │   │   ├── AboutYouScreen.java      # Customer info & tab switching
│   │   │   ├── VehicleDetailsModal.java # Vehicle details modal & b.m.w search
│   │   │   ├── OtpModal.java            # OTP native digit injection
│   │   │   ├── QuoteScreen.java         # Cover card selection & Buy Now scroll
│   │   │   ├── CheckoutScreen.java      # Payment method selection, Email & IBAN
│   │   │   ├── AddNewCardModal.java     # Credit card entry & wheel pickers
│   │   │   └── PolicyConfirmationScreen.java # Policy verification
│   │   └── utils/
│   │       ├── DriverManager.java       # ThreadLocal AppiumDriver lifecycle
│   │       ├── NationalIdGenerator.java # Auto-incrementing 3-digit National ID
│   │       ├── SequenceNumberGenerator.java # Auto-incrementing 3-digit Sequence Number
│   │       └── TestDataBuilder.java     # Centralized test data profiles & auto-fillers
│   └── test/java/
│       ├── base/
│       │   └── BaseTest.java            # TestNG setup/teardown with device fallbacks
│       ├── tests/
│       │   └── SampleTest.java          # 9 E2E test cases (Flows 1, 2, and 3)
│       └── utils/
│           ├── TestListener.java        # Automatic screenshot & XML DOM logger
│           ├── RetryAnalyzer.java       # Automatic test retry logic
│           └── AnnotationTransformer.java # TestNG retry listener injector
├── src/test/resources/
│   └── testng.xml                       # Suite XML runner configuration
├── national_id_counter.properties       # Persisted National ID counter
├── sequence_number_counter.properties   # Persisted Sequence Number counter
├── pom.xml                              # Maven project configuration
└── README.md                            # Project documentation
```

---

## ⚙️ Prerequisites & Local Setup

1. **Java JDK**: Version 17 or higher.
2. **Appium Server**: Appium 2.x installed globally (`npm install -g appium`).
3. **Appium Android Driver**: `appium driver install uiautomator2`.
4. **Android Device / Emulator**: USB Debugging enabled (for physical devices) or active AVD.

---

## 🧪 Running the Test Suite

### Option 1: Run via IntelliJ IDEA
- Open `src/test/resources/testng.xml`.
- Right-click `testng.xml` ➔ **Run '.../testng.xml'**.
- Alternatively, click the green ▶️ play button next to any `@Test` method in `SampleTest.java`.

### Option 2: Run via Command Line (Maven)
```bash
# Start Appium Server in a separate terminal
appium

# Run the complete test suite
mvn test
```

---

## ⏰ CI/CD Daily Automation (GitHub Actions)

This repository includes a pre-configured GitHub Actions workflow (**`daily_tests.yml`**):

- **Trigger Schedule**: Runs automatically **every day at 8:00 AM UTC (11:00 AM local time)**.
- **Manual Trigger**: Can be manually executed anytime from the **GitHub Actions** tab via the **"Run workflow"** button.
- **Environment**: Boots a macOS runner with an Android 11 (API 30) Pixel Emulator, installs Appium, runs the suite, and attaches all HTML reports, screenshots, and server logs as a downloadable ZIP artifact.

---

### 👤 Author & Support
Automated for **Tree Digital Insurance** UAT Environment. Built with ❤️ for robust, scalable QA engineering.
