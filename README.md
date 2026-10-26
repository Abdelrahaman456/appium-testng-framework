# 🚀 Tree Digital Insurance — Appium Mobile Automation Framework

An end-to-end, self-healing mobile test automation framework built for the **Tree Digital Insurance (UAT / PROD)** Android app using **Appium 2.x**, **Java 17**, **TestNG**, **ExtentReports 5**, and **GitHub Actions** with **Microsoft Teams** reporting.

---

## 📋 Table of Contents
1. [Business Overview & E2E Flows](#-business-overview--e2e-flows)
2. [High-Performance Data Structure Architecture](#-high-performance-data-structure-architecture)
3. [Technical Architecture](#️-technical-architecture)
4. [Key Framework Innovations](#-key-framework-innovations)
5. [Project Directory Structure](#-project-directory-structure)
6. [Test Data & Web Config Dashboard](#️-test-data--web-config-dashboard)
7. [Prerequisites & Local Setup](#️-prerequisites--local-setup)
8. [Running the Test Suite](#-running-the-test-suite)
9. [CI/CD Daily Automation & Teams Reporting](#-cicd-daily-automation--teams-reporting)

---

## 🏢 Business Overview & E2E Flows

This framework automates the full end-to-end customer journey for purchasing **Motor** and **Travel** insurance policies on the Tree Digital Insurance mobile app, covering quote generation, vehicle/trip selection, checkout, payment processing, and policy confirmation.

### Automated Motor Business Flows:

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

1. **Motor Flow 1 (New Insurance + Sequence Number)**:
   - Dynamic National ID & Sequence Number via timestamp-based generator.
   - Native OTP verification injection.
   - Comprehensive, Smart, and Saver Cover selection.
   - Checkout auto-filling (Email, IBAN) and Credit Card entry with wheel picker date calibration.
   - Policy confirmation assertion (`"All set! You're ready to roll!"`).

2. **Motor Flow 2 (New Insurance + Custom Card)**:
   - Custom vehicle registration with model year selection.
   - Dynamic vehicle make & model search inside the *"We need more details"* modal.
   - Full checkout, payment, and policy confirmation across all 3 cover types.

3. **Motor Flow 3 (Ownership Transfer + Sequence Number)**:
   - Transfer tab activation (with anti-flicker stabilization), Seller ID entry, and Sequence Number radio button selection.
   - OTP processing, quote selection, payment, and policy confirmation.

4. **Motor Flow 4 (Ownership Transfer + Custom Card)**:
   - Transfer tab, Seller ID, Custom Card, and Model Year.
   - Direct quote selection, payment, and policy confirmation.

5. **Travel Insurance Flows (Single Trip & Multi-Trip)**:
   - Covers **Single Trip** and **Multi-Trip (1 Year)** journeys across **Individual**, **With Family**, and **Group** traveler types (`TravelTest.java` & `TravelAboutYouScreen.java`).

### 🧪 Automated Test Coverage

| Suite | Count | Description |
|---|---|---|
| Motor Flow 1 (New Insurance + Sequence) | 3 | Comprehensive, Smart, Saver |
| Motor Flow 2 (New Insurance + Custom Card) | 3 | Comprehensive, Smart, Saver |
| Motor Flow 3 (Transfer + Sequence) | 3 | Comprehensive, Smart, Saver |
| Motor Flow 4 (Transfer + Custom Card) | 3 | Comprehensive, Smart, Saver |
| Motor Negative Validation Suite | 5 | Edge cases & invalid input tests (`Motor_Error_Messages_Sheet.csv`) |
| Travel Product Suite | 6 | Single Trip & Multi-Trip × Individual, Family, Group |
| **Total** | **23** | **Full E2E + Negative Regression Coverage** |

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

4. **`ThreadLocal` Isolation Pattern (`DriverManager.java`)**:
   - Enforces thread-isolated `ThreadLocal<AppiumDriver>` and `ThreadLocal<ExtentTest>` instances.

---

## 🛠️ Technical Architecture

- **Core Engine**: Appium 2.x Java Client (`io.appium:java-client 9.2.2`) with `UiAutomator2` driver.
- **Test Runner**: TestNG (`7.9.0`).
- **Design Pattern**: Page Object Model (POM) with PageFactory lazy element initialization.
- **Build System**: Apache Maven (`pom.xml`).
- **Reporting Engine**: ExtentReports 5.x Dark-Mode Visual Dashboard + Microsoft Teams Adaptive Cards.
- **CI/CD Engine**: GitHub Actions (`.github/workflows/daily_tests.yml`).

---

## 🔥 Key Framework Innovations

### 1. 📊 ExtentReports 5.x Dark-Mode Visual Dashboard
- Generates interactive dark-mode HTML reports at `target/extent-reports/ExtentReport.html`.
- Displays real-time test execution analytics, pass/fail badges, execution duration, inline Base64 failure screenshots, and clickable failure video replays.

### 2. 🎥 Automatic Failure Video Recording & Artifacts Collector
- Records the device screen during every test execution via Appium's `CanRecordScreen`.
- **When a test passes:** Discards the video buffer automatically so disk space is never wasted.
- **When a test fails:** Saves and attaches:
  - **MP4 Video Replay** → `target/screenshots/{TestName}_{Timestamp}.mp4`
  - **High-res PNG Screenshot** → `target/screenshots/{TestName}_{Timestamp}.png`
  - **Complete Appium DOM XML** → `target/screenshots/{TestName}_{Timestamp}_DOM.xml`

### 3. ⚡ Continuous Session Reuse & Fast App Reset
- Passing E2E tests click the top-right Close (`✕`) button on the Policy Confirmation screen to return directly to the Home landing page, reusing the active Appium session without reinstalling the app.
- Negative tests use `DriverManager.resetAppToHome()` (`terminateApp` + `activateApp`) to reset the app in `~2.5 seconds` instead of a full `~25-second` session teardown.

### 4. 🛡️ Self-Healing Popup Interceptor Engine
- Automatically intercepts and dismisses unexpected popups (system permissions, upsell banners, "Skip", "Dismiss", "Not now") whenever an element click fails, then retries the primary operation seamlessly.

### 5. 🔢 Timestamp-Based Zero-Duplicate ID Generators
- `NationalIdGenerator.java` & `SequenceNumberGenerator.java` generate unique IDs using `System.currentTimeMillis()` suffixes (`135454XXXX` / `70484XXXX`) with zero duplicate risk.

### 6. 🌐 Web Configuration Portal (`ConfigServer.java`)
- Built-in web UI at `http://localhost:8080` for viewing/editing `config/uat.properties` and `config/prod.properties` and triggering test runs from a browser.

---

## 📁 Project Directory Structure

```text
Mobile_Automation_Testing/
├── .github/workflows/
│   └── daily_tests.yml                  # Daily CI/CD pipeline + Emulator Auto-Boot + Teams Report
├── src/
│   ├── main/java/
│   │   ├── enums/
│   │   │   ├── CoverType.java           # Type-safe cover Enum (COMPREHENSIVE, SMART, SAVER)
│   │   │   └── InsuranceFlow.java       # Insurance flow Enum (FLOW_1 → FLOW_4)
│   │   ├── pages/
│   │   │   ├── BasePage.java            # Core wait engine, self-healing queue, W3C gestures
│   │   │   ├── HomeScreen.java          # Home screen navigation (Motor & Travel)
│   │   │   ├── MotorCoverageSelectionScreen.java
│   │   │   ├── AboutYouScreen.java      # Motor form, DOB/Year pickers, privacy scroll
│   │   │   ├── TravelAboutYouScreen.java# Travel form (Single/Multi Trip, Destination, Dates)
│   │   │   ├── VehicleDetailsModal.java # Vehicle make/model search modal
│   │   │   ├── OtpModal.java            # OTP native digit injection
│   │   │   ├── QuoteScreen.java         # Cover card selection & Buy Now scroll
│   │   │   ├── CheckoutScreen.java      # Email, IBAN (SA-prefix self-healing)
│   │   │   ├── AddNewCardModal.java     # Credit card entry & wheel pickers
│   │   │   └── PolicyConfirmationScreen.java  # Dynamic polling confirmation check
│   │   └── utils/
│   │       ├── ConfigServer.java        # Web Dashboard at http://localhost:8080
│   │       ├── DriverManager.java       # ThreadLocal AppiumDriver, video recorder, fast reset
│   │       ├── ExtentManager.java       # Dark-mode ExtentReports 5.x manager
│   │       ├── NationalIdGenerator.java # Timestamp-based unique National ID generator
│   │       ├── SequenceNumberGenerator.java # Timestamp-based unique Sequence generator
│   │       ├── TestConfig.java          # Multi-env (uat/prod) config loader & cache
│   │       ├── TestDataBuilder.java     # Data profiles (reads from TestConfig)
│   │       └── TestListener.java        # Screenshots, MP4 video, DOM capture & report logger
│   └── test/java/
│       ├── base/
│       │   └── BaseTest.java            # TestNG setup/teardown with continuous session reuse
│       ├── tests/
│       │   ├── SampleTest.java          # 12 positive Motor E2E test cases (Flows 1-4)
│       │   ├── NegativeValidationTest.java # 5 negative Motor edge-case test cases
│       │   └── TravelTest.java          # 6 Travel E2E test cases (Single & Multi-Trip)
│       └── utils/
│           ├── RetryAnalyzer.java       # Automatic test retry logic
│           └── AnnotationTransformer.java # TestNG retry listener injector
├── src/test/resources/
│   ├── config/
│   │   ├── uat.properties               # UAT environment configuration
│   │   └── prod.properties              # PROD environment configuration
│   ├── testng.xml                       # Suite XML runner configuration (Motor & Travel)
│   └── testdata.properties              # Default fallback test data properties
├── Motor_Error_Messages_Sheet.csv       # Comprehensive Motor negative & validation error matrix
├── Test_Flows_Matrix.csv                # E2E test flows tracking matrix
├── pom.xml                              # Maven project configuration
└── README.md                            # Project documentation
```

---

## 🗂️ Test Data & Web Config Dashboard

Environment-specific configurations live in:
- `src/test/resources/config/uat.properties` (Default)
- `src/test/resources/config/prod.properties`

You can also launch the interactive **Web Config Portal** at `http://localhost:8080`:
```bash
mvn compile exec:java -Dexec.mainClass="utils.ConfigServer"
```

---

## ⚙️ Prerequisites & Local Setup

1. **Java JDK**: Version 17 (`JAVA_HOME` configured).
2. **Apache Maven**: Version 3.9+.
3. **Node.js & Appium 2.x**:
   ```bash
   npm install -g appium
   appium driver install uiautomator2
   ```
4. **Android Emulator / Device**: Google Play Store system image (API 33 or 34 recommended to pass RootBeer integrity checks).

---

## 🧪 Running the Test Suite

```bash
# Run UAT suite (default)
mvn clean test "-DappPath=app/app-uat-release.apk"

# Switch environment via -Denv
mvn clean test -Denv=uat "-DappPath=app/app-uat-release.apk"
```

---

## ⏰ CI/CD Daily Automation & Teams Reporting

The GitHub Actions workflow (**`.github/workflows/daily_tests.yml`**) runs automatically every morning at **11:00 AM KSA (8:00 AM UTC)** and supports one-click manual triggers (`workflow_dispatch`):

1. **Auto-Boot & Health-Check Emulator**: Checks `adb devices`; if no emulator is running, automatically boots the Google Play AVD, waits for `sys.boot_completed=1`, and unlocks the screen.
2. **Execute TestNG Suites**: Runs the configured suites in `testng.xml` and captures HTML reports, screenshots, DOM trees, and failure `.mp4` videos.
3. **Microsoft Teams Adaptive Card**: Parses `testng-results.xml` and posts a summary card (with per-suite breakdown, execution duration, failed test details, and artifact link) to the configured Teams channel via `TEAMS_WEBHOOK_URL`.

---

### 👤 Author & Support
Automated for **Tree Digital Insurance** QA Engineering.
