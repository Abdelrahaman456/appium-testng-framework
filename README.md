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

4. **Flow 4 (Ownership Transfer + Custom Card)**:
   - Transfer tab activation, Seller ID entry, Custom Card selection, and Model Year (`2026`).
   - Dynamic 3-digit auto-incrementing National ID (`NationalIdGenerator`).
   - Direct quote selection, payment, and policy confirmation across Comprehensive, Smart, and Saver covers.

---

## ⚡ High-Performance Data Structure Architecture

The framework incorporates specialized **Data Structures** to guarantee $O(1)$ high-speed execution, minimal memory consumption, and zero thread lock contention:

1. **`ConcurrentHashMap<String, By>` Locator Cache (`BasePage.java`)**:
   - Locators are pre-compiled into memory on first access.
   - Delivers $O(1)$ constant-time lookup for element discovery across all 12 test cases, eliminating raw XPath string parsing CPU churn.

2. **`ArrayDeque<By>` High-Speed Self-Healing Queue (`BasePage.java`)**:
   - Double-ended Queue (`ArrayDeque`) storing pre-compiled XPaths for system permission banners, upsell dialogs, and popups.
   - Enables $O(1)$ high-speed queue traversal for instant popup auto-dismissal.

3. **Strongly-Typed Enums & `EnumMap` (`CoverType.java` & `InsuranceFlow.java`)**:
   - Replaces fragile String comparisons with type-safe Enums (`COMPREHENSIVE`, `SMART`, `SAVER`).
   - Provides $O(1)$ direct array index memory access and eliminates string allocation overhead.

4. **`ThreadLocal` Isolation Pattern (`DriverManager.java` & `TestListener.java`)**:
   - Enforces thread-isolated `ThreadLocal<AppiumDriver>` and `ThreadLocal<ExtentTest>` instances.
   - Enables zero-lock parallel test execution without thread contention.

---

## 🛠 Technical Architecture

- **Core Engine**: Appium 2.x Java Client (`io.appium:java-client 9.2.2`) with `UiAutomator2` driver.
- **Test Runner**: TestNG (`7.9.0`) with parallel execution configuration.
- **Design Pattern**: Page Object Model (POM) with PageFactory lazy element initialization.
- **Build System**: Apache Maven (`pom.xml`).
- **Reporting Engine**: ExtentReports 5.x (`com.aventstack:extentreports 5.1.1`) Dark-Mode Visual Dashboard.
- **CI/CD Engine**: GitHub Actions running cloud Android Emulators with KVM acceleration.

---

## 🔥 Key Framework Innovations

### 1. 📊 ExtentReports 5.x Dark-Mode Visual Dashboard (`ExtentManager.java` & `TestListener.java`)
- Generates interactive, dark-mode HTML reports at `target/extent-reports/ExtentReport.html`.
- Displays real-time test execution analytics, pass/fail status badges, execution duration, and attaches inline Base64 failure screenshots.

### 2. 🛡️ Self-Healing Popup Interceptor Engine (`BasePage.java`)
- Automatically intercepts and dismisses unexpected obstructing popups (system permissions, upsell banners, promo dialogs, "Skip", "Dismiss", "Not now", "Close") whenever an element click fails.
- Once auto-dismissed, the framework retries the primary user operation smoothly without failing the test execution.

### 3. 🔢 Persisted Dynamic Data Generators (`NationalIdGenerator` & `SequenceNumberGenerator`)
- Maintains disk-persisted counters (`national_id_counter.properties` and `sequence_number_counter.properties`).
- Automatically increments the **last 3 digits (`+1`)** for both National ID (`1354545XXX`) and Sequence Number (`704848XXX`) on every test case run.
- **Business Impact**: Prevents backend API policy duplicate errors (Najm / Tawuniya) across repeated local and CI/CD test runs.

### 4. 📝 Failure Artifacts Collector (`TestListener.java`)
- Automatically listens to test events via TestNG `ITestListener`.
- Upon test failure, it captures:
  - High-res PNG Screenshot saved to `target/screenshots/{TestName}_{Timestamp}.png`.
  - Complete Appium DOM XML Tree saved to `target/screenshots/{TestName}_{Timestamp}_DOM.xml`.

### 5. 🔄 Flaky Test Retry Engine (`RetryAnalyzer.java` & `AnnotationTransformer.java`)
- Implements `IRetryAnalyzer` and `IAnnotationTransformer` to automatically retry transient network glitches or backend SMS delays **once** before declaring a failure.

### 6. ⚡ Smart Form Auto-Filler (`TestDataBuilder.java`)
- Centralized data model (`CustomerProfile` & `CreditCard`) enabling one-line form auto-filling for Checkout (`autoFillCheckout`) and Add New Card (`autoFillCardDetails`).

---

## 📁 Project Directory Structure

```text
appium-testng-framework/
├── .github/workflows/
│   └── daily_tests.yml                  # Daily CI/CD pipeline on Android Cloud Emulator
├── src/
│   ├── main/java/
│   │   ├── enums/
│   │   │   ├── CoverType.java           # Type-safe cover selection Enum
│   │   │   └── InsuranceFlow.java       # Insurance flow classification Enum
│   │   ├── pages/
│   │   │   ├── BasePage.java            # Base page with ConcurrentHashMap cache & ArrayDeque queue
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
│       │   └── BaseTest.java            # TestNG setup/teardown with smart CI detection
│       ├── tests/
│       │   └── SampleTest.java          # 12 E2E test cases (Flows 1, 2, 3, and 4)
│       └── utils/
│           ├── ExtentManager.java       # Dark-mode ExtentReports 5.x manager
│           ├── TestListener.java        # ExtentReports logger, screenshots & DOM collector
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
