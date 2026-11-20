import json
import os
import sys
import tempfile
import unittest
from pathlib import Path
from unittest import mock

sys.path.insert(0, str(Path(__file__).resolve().parent))
import send_teams_report as reporter  # noqa: E402


REPO_ROOT = Path(__file__).resolve().parent.parent


class SendTeamsReportTest(unittest.TestCase):
    def test_real_testng_xml_dry_run(self):
        xml_path = REPO_ROOT / "target" / "surefire-reports" / "testng-results.xml"
        if not xml_path.exists():
            self.skipTest("Live testng-results.xml not present")

        model = reporter.build_report_model(
            xml_path=xml_path,
            telemetry_path=REPO_ROOT / "target" / "teams-report" / "nonexistent.json",
            perf_path=REPO_ROOT / "target" / "perf-reports" / "performance_benchmark_report.md",
            screenshots_dir=REPO_ROOT / "target" / "screenshots",
            videos_dir=REPO_ROOT / "target" / "videos",
        )
        self.assertGreater(model.total, 0)
        payload = reporter.build_size_bounded_card(model)
        card = payload["attachments"][0]["content"]
        self.assertEqual(card["type"], "AdaptiveCard")
        self.assertEqual(card["version"], "1.4")
        self.assertEqual(card["msteams"]["width"], "Full")
        self.assertLessEqual(
            len(json.dumps(payload, ensure_ascii=False).encode("utf-8")),
            reporter.MAX_TEAMS_PAYLOAD_BYTES,
        )

    def test_retry_dedup_bug_triage_and_artifacts(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            xml_path = root / "testng-results.xml"
            tel_path = root / "test-run-summary.json"
            perf_path = root / "perf.md"
            scr_dir = root / "screenshots"
            vid_dir = root / "videos"
            scr_dir.mkdir()
            vid_dir.mkdir()

            (scr_dir / "testFlow2_Checkout_20261120_120000.png").write_bytes(b"png")
            (scr_dir / "testFlow2_Checkout_20261120_120000_DOM.xml").write_text("<hierarchy/>", encoding="utf-8")
            (vid_dir / "testFlow2_Checkout_20261120_120000.mp4").write_bytes(b"mp4")

            xml_path.write_text(
                """<?xml version="1.0" encoding="UTF-8"?>
<testng-results ignored="0" total="6" passed="2" failed="3" skipped="1">
  <suite name="Appium Mobile Test Suite" duration-ms="185000">
    <test name="Motor Product Test" duration-ms="60000">
      <class name="tests.MotorTest">
        <test-method name="testFlow1_FlakyPass" signature="testFlow1_FlakyPass()" status="SKIP" retried="true" duration-ms="12000"/>
        <test-method name="testFlow1_FlakyPass" signature="testFlow1_FlakyPass()" status="PASS" duration-ms="14000"/>
        <test-method name="testFlow2_Checkout" signature="testFlow2_Checkout()" description="Motor Checkout Card" status="FAIL" duration-ms="22000">
          <exception class="java.lang.AssertionError">
            <message><![CDATA[Purchase failed on 3DS ACS Emulator!
For documentation on this error, please visit: https://selenium.dev/ignored]]></message>
            <full-stacktrace><![CDATA[java.lang.AssertionError: Purchase failed
\tat pages.CheckoutScreen.submit3ds(CheckoutScreen.java:142)
\tat tests.MotorTest.testFlow2_Checkout(MotorTest.java:55)]]></full-stacktrace>
          </exception>
        </test-method>
      </class>
    </test>
    <test name="Support and Localization Test" duration-ms="45000">
      <class name="tests.SupportAndLocalizationTest">
        <test-method name="testFaqLocatorMissing" signature="testFaqLocatorMissing()" status="FAIL" duration-ms="15000">
          <exception class="org.openqa.selenium.NoSuchElementException">
            <message><![CDATA[No visible element found for locator: //*[@content-desc='Payment' or @content-desc='FAQs'] Build info: version: '4.20']]></message>
            <full-stacktrace><![CDATA[org.openqa.selenium.NoSuchElementException: missing
\tat pages.SupportAndLocalizationScreen.openFaqs(SupportAndLocalizationScreen.java:88)]]></full-stacktrace>
          </exception>
        </test-method>
        <test-method name="testCrashDialog" signature="testCrashDialog()" status="FAIL" duration-ms="8000">
          <exception class="java.lang.RuntimeException">
            <message><![CDATA[App isn't responding (ANR) during screen transition]]></message>
            <full-stacktrace><![CDATA[java.lang.RuntimeException: ANR
\tat pages.BasePage.click(BasePage.java:200)]]></full-stacktrace>
          </exception>
        </test-method>
        <test-method name="testCleanPass" signature="testCleanPass()" status="PASS" duration-ms="5000"/>
      </class>
    </test>
  </suite>
</testng-results>
""",
                encoding="utf-8",
            )

            tel_path.write_text(
                json.dumps({
                    "environment": "UAT",
                    "appPackage": "sa.com.tree.digital.insurance.uat",
                    "tests": [
                        {
                            "className": "tests.MotorTest",
                            "methodName": "testFlow1_FlakyPass",
                            "warnings": ["[Self-Healing Engine] Auto-dismissed Try Again modal"],
                            "uatElmBlocked": False,
                            "lastSteps": ["Entered National ID", "Selected Comprehensive"],
                        },
                        {
                            "className": "tests.MotorTest",
                            "methodName": "testFlow2_Checkout",
                            "warnings": [],
                            "uatElmBlocked": False,
                            "lastSteps": ["Clicked Pay now", "[PERF BENCHMARK] [testFlow2_Checkout] E2E_Journey -> 22.0s"],
                            "failingLocation": "pages.CheckoutScreen.submit3ds(CheckoutScreen.java:142)",
                        },
                    ],
                }),
                encoding="utf-8",
            )

            perf_path.write_text(
                """# ⚡ Tree Digital Insurance - Automated Performance Benchmarks

| Product / Test Journey | Stage / Transaction | Elapsed (s) | SLA Target (s) | SLA Status |
| :--- | :--- | :---: | :---: | :---: |
| testCleanPass | E2E_Journey | 5.00s | 35.0s | 🟢 PASSED |
| testFlow2_Checkout | E2E_Journey | 42.50s | 35.0s | 🔴 EXCEEDED |
""",
                encoding="utf-8",
            )

            model = reporter.build_report_model(xml_path, tel_path, perf_path, scr_dir, vid_dir)
            # 5 unique tests: 1 flaky pass, 1 clean pass, 3 fails (retry SKIP deduplicated!)
            self.assertEqual(model.total, 5)
            self.assertEqual(model.passed, 2)
            self.assertEqual(model.failed, 3)
            self.assertEqual(model.skipped, 0)
            self.assertEqual(model.flaky, 1)
            self.assertEqual(model.card_style, "Attention")
            self.assertEqual(model.suites[0].total, 2)
            self.assertEqual(model.suites[0].passed, 1)
            self.assertEqual(model.suites[0].flaky, 1)

            by_method = {b.method_name: b for b in model.failures}
            self.assertEqual(by_method["testCrashDialog"].triage.category, "App Crash / ANR")
            self.assertEqual(by_method["testCrashDialog"].triage.severity, "Critical")
            self.assertEqual(by_method["testFlow2_Checkout"].triage.category, "Payment / 3DS Gateway Issue")
            self.assertEqual(by_method["testFlow2_Checkout"].triage.last_step, "Clicked Pay now")
            self.assertNotIn("selenium.dev", by_method["testFlow2_Checkout"].triage.clean_message)
            self.assertEqual(by_method["testFlow2_Checkout"].screenshot_file, "testFlow2_Checkout_20261120_120000.png")
            self.assertEqual(by_method["testFlow2_Checkout"].dom_xml_file, "testFlow2_Checkout_20261120_120000_DOM.xml")
            self.assertEqual(by_method["testFlow2_Checkout"].video_file, "testFlow2_Checkout_20261120_120000.mp4")
            self.assertEqual(by_method["testFaqLocatorMissing"].triage.category, "UI / Locator Regression")
            self.assertEqual(model.perf.met_count, 1)
            self.assertEqual(model.perf.breached_count, 1)

            md = reporter.render_markdown_report(model)
            self.assertIn("Detailed Bug & Defect Triage", md)
            self.assertIn("Bug #1", md)
            self.assertIn("42.50s", md)

    def test_28kb_payload_guard_under_heavy_failure_load(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            xml_path = root / "testng-results.xml"
            methods_xml = []
            for i in range(30):
                methods_xml.append(
                    f"""<test-method name="testStressBug_{i}" signature="testStressBug_{i}()" description="Long failure scenario description #{i} testing card size truncation" status="FAIL" duration-ms="30000">
  <exception class="java.lang.AssertionError">
    <message><![CDATA[Expected policy issuance screen on attempt #{i} but API returned unexpected payload error details: {'X' * 300}]]></message>
    <full-stacktrace><![CDATA[java.lang.AssertionError: fail\n\tat pages.QuoteScreen.verify(QuoteScreen.java:{10 + i})]]></full-stacktrace>
  </exception>
</test-method>"""
                )
            xml_path.write_text(
                f"""<?xml version="1.0" encoding="UTF-8"?>
<testng-results ignored="0" total="30" passed="0" failed="30" skipped="0">
  <suite name="Stress Suite" duration-ms="900000">
    <test name="Stress Test" duration-ms="900000">
      <class name="tests.MotorTest">{"".join(methods_xml)}</class>
    </test>
  </suite>
</testng-results>""",
                encoding="utf-8",
            )
            model = reporter.build_report_model(xml_path, root / "missing.json", root / "missing.md", root, root)
            payload = reporter.build_size_bounded_card(model)
            size = len(json.dumps(payload, ensure_ascii=False).encode("utf-8"))
            self.assertLessEqual(size, reporter.MAX_TEAMS_PAYLOAD_BYTES)
            card_str = json.dumps(payload)
            self.assertIn("omitted to stay within Teams card size limits", card_str)

    def test_cancelled_and_zero_test_states(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            missing_xml = root / "none.xml"
            with mock.patch.dict(os.environ, {"MAVEN_OUTCOME": "cancelled", "JOB_STATUS": "cancelled"}):
                cancelled = reporter.build_report_model(missing_xml, root / "t.json", root / "p.md", root, root)
                self.assertIn("CANCELLED", cancelled.overall_status)
                self.assertEqual(cancelled.card_style, "Warning")

            with mock.patch.dict(os.environ, {"MAVEN_OUTCOME": "success", "JOB_STATUS": "success"}):
                empty = reporter.build_report_model(missing_xml, root / "t.json", root / "p.md", root, root)
                self.assertIn("NO TESTS EXECUTED", empty.overall_status)


if __name__ == "__main__":
    unittest.main()
