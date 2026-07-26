# 🚀 Tree Digital Insurance - Enterprise Appium Mobile Automation Framework

An end-to-end, self-healing mobile test automation framework built for **Tree Digital Insurance (UAT)** Android app using **Appium 2.x**, **Java 17**, **TestNG**, and **GitHub Actions**.

---

## 📋 Table of Contents
1. [Business Overview & E2E Flows](#-business-overview--e2e-flows)
2. [Technical Architecture](#️-technical-architecture)
3. [High-Performance Data Structure Architecture](#-high-performance-data-structure-architecture)
4. [Key Framework Innovations](#-key-framework-innovations)
5. [Advanced Algorithm Upgrades](#-advanced-algorithm-upgrades)
6. [Project Directory Structure](#-project-directory-structure)
7. [Test Data Configuration](#️-test-data-configuration)
8. [Prerequisites & Local Setup](#️-prerequisites--local-setup)
9. [Running the Test Suite](#-running-the-test-suite)
10. [CI/CD Daily Automation (GitHub Actions)](#-cicd-daily-automation-github-actions)

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
    [ Flow 1 & 2: New Insurance ]                               [ Flow 3 & 4: Ownership Transfer ]
   Sequence Number / Custom Card                                 Seller ID / Sequence Number / Custom Card
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
   - Dynamic National ID & Sequence Number via timestamp-based generator.
   - Native OTP verification injection.
   - Comprehensive, Smart, and Saver Cover selection.
   - Checkout auto-filling (Email, IBAN) and Credit Card entry with wheel picker date calibration.
   - Policy confirmation assertion (`"All set! You're ready to roll!"`).

2. **Flow 2 (New Insurance + Custom Card)**:
   - Custom vehicle registration with model year selection.
   - Dynamic vehicle make & model search inside the *"We need more details"* modal.
   - Full checkout, payment, and policy confirmation across all 3 cover types.

3. **Flow 3 (Ownership Transfer + Sequence Number)**:
   - Transfer tab activation, Seller ID entry, and Sequence Number radio button selection.
   - OTP processing, quote selection, payment, and policy confirmation.

4. **Flow 4 (Ownership Transfer + Custom Card)**:
   - Transfer tab, Seller ID, Custom Card, and Model Year.
   - Direct quote selection, payment, and policy confirmation.

### 🧪 Total Test Suite: 17 Automated Test Cases

| Suite | Count | Description |
|---|---|---|
| Flow 1 (New Insurance + Sequence) | 3 | Comprehensive, Smart, Saver |
| Flow 2 (New Insurance + Custom Card) | 3 | Comprehensive, Smart, Saver |
| Flow 3 (Transfer + Sequence) | 3 | Comprehensive, Smart, Saver |
| Flow 4 (Transfer + Custom Card) | 3 | Comprehensive, Smart, Saver |
| Negative Validation Suite | 5 | Edge cases & invalid input tests |
| **Total** | **17** | **Full regression coverage** |

---

## ⚡ High-Performance Data Structure Architecture

The framework incorporates specialized **Data Structures** to guarantee $O(1)$ high-speed execution, minimal memory consumption, and zero thread lock contention:

1. **`ConcurrentHashMap<String, By>` Locator Cache (`BasePage.java`)**:
   - Locators are pre-compiled into memory on first access.
   - Delivers $O(1)$ constant-time lookup for element discovery across all test cases.

2. **`ArrayDeque<By>` High-Speed Self-Healing Queue (`BasePage.java`)**:
   - Double-ended Queue storing pre-compiled XPaths for system popups, banners, and dialogs.
   - Enables $O(1)$ high-speed queue traversal for instant popup auto-dismissal.

3. **Strongly-Typed Enums (`CoverType.java` & `InsuranceFlow.java`)**:
   - Replaces fragile String comparisons with type-safe Enums (`COMPREHENSIVE`, `SMART`, `SAVER`).
   - Provides $O(1)$ direct array index memory access.

4. **`ThreadLocal` Isolation Pattern (`DriverManager.java`)**:
   - Enforces thread-isolated `ThreadLocal<AppiumDriver>` and `ThreadLocal<ExtentTest>` instances.
   - Enables zero-lock parallel test execution without thread contention.

---

## 🛠️ Technical Architecture

- **Core Engine**: Appium 2.x Java Client (`io.appium:java-client 9.2.2`) with `UiAutomator2` driver.
- **Test Runner**: TestNG (`7.9.0`) with parallel execution configuration.
- **Design Pattern**: Page Object Model (POM) with PageFactory lazy element initialization.
- **Build System**: Apache Maven (`pom.xml`).
- **Reporting Engine**: ExtentReports 5.x Dark-Mode Visual Dashboard.
- **CI/CD Engine**: GitHub Actions with daily scheduled runs.

---

## 🔥 Key Framework Innovations

### 1. 📊 ExtentReports 5.x Dark-Mode Visual Dashboard
- Generates interactive dark-mode HTML reports at `target/extent-reports/ExtentReport.html`.
- Displays real-time test execution analytics, pass/fail badges, execution duration, and inline Base64 failure screenshots.

### 2. 🛡️ Self-Healing Popup Interceptor Engine
- Automatically intercepts and dismisses unexpected popups (system permissions, upsell banners, "Skip", "Dismiss", "Not now") whenever an element click fails.
- Once auto-dismissed, retries the primary operation smoothly without failing the test.

### 3. 🔢 Timestamp-Based Zero-Duplicate ID Generators
- `NationalIdGenerator.java` & `SequenceNumberGenerator.java` generate unique IDs using:
```
System.currentTimeMillis() → last 4 digits → appended to prefix
National ID  = 135454 + last 4 digits of timestamp  → e.g. 1354547891
Sequence No  = 70484  + last 4 digits of timestamp  → e.g. 704847892
```
- **Zero file I/O** — no disk reads/writes needed.
- **Zero duplicate risk** across parallel runs, multiple machines, and CI/CD environments.
- **Last 4 digits** guarantee uniqueness every millisecond.

### 4. 📝 Failure Artifacts Collector
- Upon test failure, automatically captures:
  - High-res PNG Screenshot → `target/screenshots/{TestName}_{Timestamp}.png`
  - Complete Appium DOM XML → `target/screenshots/{TestName}_{Timestamp}_DOM.xml`

### 5. 🔄 Flaky Test Retry Engine
- Implements `IRetryAnalyzer` to automatically retry transient failures once before declaring a final failure.

### 6. ⚡ Smart Dynamic Wait (`BasePage.java`)
- Replaces all fixed `Thread.sleep()` with intelligent condition polling:
```java
// OLD — wastes time or fails under load:
Thread.sleep(5000);

// NEW — polls every 500ms, exits immediately when element is ready:
waitUntil(() -> isElementVisible(someElement), 10);
```
- `waitUntil(condition, timeoutSec)` — polls a condition every 500ms.
- `isElementVisible(element)` — instant check with no exceptions thrown.

### 7. ✅ Page Load Validation
- Every screen now exposes an `isLoaded()` method that validates the screen is fully rendered before interacting:
```java
public boolean isLoaded() {
    boolean loaded = waitUntil(
        () -> isElementVisible(readyToRollHeader) || isElementVisible(viewPolicyButton),
        TestConfig.policyTimeout()
    );
    if (!loaded) System.out.println("[PageValidation] Screen did NOT load!");
    return loaded;
}
```
- Produces meaningful error messages: `[PageValidation] PolicyConfirmationScreen did NOT load within 30s!`
- Instead of cryptic `NoSuchElementException` crashes.

### 8. 🗂️ Centralized Test Data Config (`testdata.properties`)
- All test data and timeouts are stored in a single config file:
```properties
default.phone=500421222
default.iban=SA6530400108071059170014
default.card.number=5123456789012346
timeout.payment.processing=30
timeout.policy.confirmation=30
```
- Change any test data or timeout **without recompiling Java code**.
- `TestConfig.java` loads the file once at startup and caches all values.
- `TestDataBuilder.java` reads all values from `TestConfig`.

### 9. 🧠 IBAN Self-Healing Input
- The UAT app pre-renders a `SA` country code label inside the IBAN text field.
- The framework automatically strips `SA` prefix before typing and sends only the 22 numeric digits:
```java
String numericOnly = iban.startsWith("SA") ? iban.substring(2) : iban;
sendKeys(ibanField, numericOnly); // UI combines SA + 22 digits = SA + 6530400108071059170014
```

---

## 📁 Project Directory Structure

```text
appium-testng-framework/
├── .github/workflows/
│   └── daily_tests.yml                  # Daily CI/CD pipeline (no auto push trigger)
├── src/
│   ├── main/java/
│   │   ├── enums/
│   │   │   ├── CoverType.java           # Type-safe cover Enum (COMPREHENSIVE, SMART, SAVER)
│   │   │   └── InsuranceFlow.java       # Insurance flow Enum (FLOW_1 → FLOW_4)
│   │   ├── pages/
│   │   │   ├── BasePage.java            # Core: ConcurrentHashMap cache, ArrayDeque queue,
│   │   │   │                            #       Smart Dynamic Wait, isElementVisible
│   │   │   ├── HomeScreen.java          # Home screen navigation
│   │   │   ├── MotorCoverageSelectionScreen.java
│   │   │   ├── AboutYouScreen.java      # Customer info, tab switching, keyboard dismissal
│   │   │   ├── VehicleDetailsModal.java # Vehicle make/model search modal
│   │   │   ├── OtpModal.java            # OTP native digit injection
│   │   │   ├── QuoteScreen.java         # Cover card selection & Buy Now scroll
│   │   │   ├── CheckoutScreen.java      # Email, IBAN (SA-prefix self-healing)
│   │   │   ├── AddNewCardModal.java     # Credit card entry & wheel pickers
│   │   │   └── PolicyConfirmationScreen.java  # isLoaded() page validation
│   │   └── utils/
│   │       ├── DriverManager.java       # ThreadLocal AppiumDriver + fast session flags
│   │       ├── NationalIdGenerator.java # Timestamp-based 4-digit suffix (zero duplicates)
│   │       ├── SequenceNumberGenerator.java # Timestamp-based 4-digit suffix (zero duplicates)
│   │       ├── TestConfig.java          # testdata.properties loader & cache
│   │       └── TestDataBuilder.java     # Data profiles (reads from TestConfig)
│   └── test/java/
│       ├── base/
│       │   └── BaseTest.java            # TestNG setup/teardown with CI detection
│       ├── tests/
│       │   ├── SampleTest.java          # 12 positive E2E test cases (Flows 1-4)
│       │   └── NegativeValidationTest.java # 5 negative edge-case test cases
│       └── utils/
│           ├── ExtentManager.java       # Dark-mode ExtentReports 5.x manager
│           ├── TestListener.java        # Screenshots, DOM capture & report logger
│           ├── RetryAnalyzer.java       # Automatic test retry logic
│           └── AnnotationTransformer.java # TestNG retry listener injector
├── src/test/resources/
│   ├── testng.xml                       # Suite XML runner configuration
│   └── testdata.properties              # ⭐ Centralized test data config (no recompile needed!)
├── pom.xml                              # Maven project configuration
└── README.md                            # Project documentation
```

---

## 🗂️ Test Data Configuration

All test data is centralized in **[`src/test/resources/testdata.properties`](src/test/resources/testdata.properties)**.

To change any value — **edit the file directly, no Java recompilation needed**:

```properties
# Customer Data
default.phone=500421222
default.email=aashraf@tree.com.sa
default.iban=SA6530400108071059170014

# Credit Card Data
default.card.number=5123456789012346
default.card.expiry=01/2031
default.card.cvv=100
default.card.holder=Tree User

# Timeouts (seconds)
timeout.element.visible=10
timeout.payment.processing=30
timeout.policy.confirmation=30
```

---

## ⚙️ Prerequisites & Local Setup

1. **Java JDK**: Version 17 or higher.
2. **Appium Server**: Appium 2.x installed globally (`npm install -g appium`).
3. **Appium Android Driver**: `appium driver install uiautomator2`.
4. **Android Device / Emulator**: USB Debugging enabled (physical) or active AVD.

---

## 🧪 Running the Test Suite

### Option 1: Run via IntelliJ IDEA
- Open `src/test/resources/testng.xml`.
- Right-click `testng.xml` ➔ **Run '.../testng.xml'**.
- Or click the green ▶️ play button next to any `@Test` method.

### Option 2: Run via Command Line (Maven)
```bash
# Start Appium Server in a separate terminal
appium

# Run the complete test suite
mvn test
```

---

## ⏰ CI/CD Daily Automation (GitHub Actions)

The GitHub Actions workflow (**`daily_tests.yml`**) is configured as:

| Trigger | Details |
|---|---|
| ⏰ **Daily Schedule** | Every day at 8:00 AM UTC (11:00 AM local time) |
| 🖱️ **Manual Trigger** | GitHub Actions tab ➔ **"Run workflow"** button |
| 🚫 **Push Trigger** | **Disabled** — pushes do NOT auto-trigger runs |

- Boots a cloud Android Emulator, installs Appium, runs all 17 test cases.
- Uploads HTML report, screenshots, and server logs as a downloadable ZIP artifact.

---

### 👤 Author & Support
Automated for **Tree Digital Insurance** UAT Environment. Built with ❤️ for robust, scalable QA engineering.
