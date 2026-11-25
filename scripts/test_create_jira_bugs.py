#!/usr/bin/env python3
"""Unit tests for scripts/create_jira_bugs.py."""

import json
import sys
import tempfile
import unittest
from pathlib import Path
from unittest.mock import MagicMock, patch

SCRIPT_DIR = Path(__file__).resolve().parent
if str(SCRIPT_DIR) not in sys.path:
    sys.path.insert(0, str(SCRIPT_DIR))

import create_jira_bugs as jb
from send_teams_report import BugTriage, ReportModel, UnifiedTestResult


def _sample_model(failures):
    return ReportModel(
        environment="UAT",
        channel_name="Mobile Automation",
        overall_status="FAILED",
        card_style="Attention",
        trigger_label="Daily Scheduled Run",
        branch="main",
        commit_short="9f11823",
        commit_message="test commit",
        actor="Abdelrahaman456",
        device_info="sdk_gphone64_x86_64 (Android 14, emulator-5554)",
        app_version="1.2.5",
        run_url="https://github.com/TreeDigitalInsurance/Mobile_Automation_Testing/actions/runs/45585043505",
        commit_url="https://github.com/TreeDigitalInsurance/Mobile_Automation_Testing/commit/9f11823",
        timestamp_ksa="2026-11-25 14:00 KSA (UTC+3)",
        timestamp_utc="2026-11-25 11:00 UTC",
        total=10,
        passed=8,
        failed=len(failures),
        skipped=0,
        flaky=0,
        pass_rate_pct=80.0,
        total_duration_ms=60000,
        suites=[],
        failures=failures,
        flaky_tests=[],
        warnings_list=[],
        config_errors=[],
        perf=None,
    )


class TestCreateJiraBugs(unittest.TestCase):
    def test_field_mappings_and_payload(self):
        failure = UnifiedTestResult(
            suite_name="Income Insurance Product Test",
            class_name="tests.IncomeInsuranceTest",
            method_name="testIncomeInsuranceFlow1_HappyPath",
            signature="tests.IncomeInsuranceTest#testIncomeInsuranceFlow1_HappyPath",
            description="Income Flow 1",
            category="Functional / Assertion Bug",
            status="FAIL",
            flaky=False,
            retries=0,
            duration_ms=167760,
            triage=BugTriage(
                category="Functional / Assertion Bug",
                severity="High",
                clean_message="Policy Confirmation screen was not reached for Income Protection Insurance!",
                failing_location="tests.IncomeInsuranceTest.testIncomeInsuranceFlow1_HappyPath(IncomeInsuranceTest.java:80)",
                last_step="3DS WebView CTA detected ('Submit')!",
                exception_class="AssertionError",
            ),
        )
        payload = jb.build_issue_payload(
            _sample_model([failure]),
            failure,
            project_key="UITEST",
            assignee_account_id=jb.DEFAULT_ASSIGNEE_ACCOUNT_ID,
        )
        fields = payload["fields"]
        self.assertEqual(fields["project"]["key"], "UITEST")
        self.assertEqual(fields["issuetype"]["name"], "Bug")
        self.assertEqual(fields["assignee"]["accountId"], jb.DEFAULT_ASSIGNEE_ACCOUNT_ID)
        self.assertEqual(fields["customfield_10082"], [{"id": "10130"}])  # UAT
        self.assertEqual(fields["customfield_10517"], {"id": "10604"})    # Income
        self.assertEqual(fields["customfield_10582"], {"id": "10634"})    # Functional
        self.assertIn("auto-mobile-testIncomeInsuranceFlow1_HappyPath", fields["labels"])

    def test_skips_infra_and_deduplicates_open_bugs(self):
        real_bug = UnifiedTestResult(
            suite_name="Home Insurance Product Test",
            class_name="tests.HomeInsuranceTest",
            method_name="testHomeInsuranceFlow1_HappyPath",
            signature="tests.HomeInsuranceTest#testHomeInsuranceFlow1_HappyPath",
            description="Home Flow 1",
            category="Functional / Assertion Bug",
            status="FAIL",
            flaky=False,
            retries=0,
            duration_ms=120000,
            triage=BugTriage(
                category="Functional / Assertion Bug",
                severity="High",
                clean_message="Policy Confirmation screen was not reached!",
                failing_location="tests.HomeInsuranceTest.java:80",
                last_step="Pay now",
                exception_class="AssertionError",
            ),
        )
        infra_bug = UnifiedTestResult(
            suite_name="Auth and Profile Test",
            class_name="tests.AuthAndProfileTest",
            method_name="testAuthFlow1_MyPoliciesGuestAuthGateAndLoginNavigation",
            signature="tests.AuthAndProfileTest#testAuthFlow1",
            description="Auth Flow 1",
            category="App Crash / ANR",
            status="FAIL",
            flaky=False,
            retries=1,
            duration_ms=5000,
            triage=BugTriage(
                category="App Crash / ANR",
                severity="Critical",
                clean_message="Command output: cmd: Can't find service: settings",
                failing_location="DriverManager.java:179",
                last_step="setUp",
                exception_class="SessionNotCreatedException",
            ),
        )

        with tempfile.TemporaryDirectory() as tmp:
            out_path = Path(tmp) / "jira-links.json"
            mock_client = MagicMock()
            mock_client.resolve_assignee_account_id.return_value = jb.DEFAULT_ASSIGNEE_ACCOUNT_ID
            mock_client.find_open_issue_by_label.return_value = "UITEST-99"

            with patch.object(jb, "JiraClient", return_value=mock_client):
                created, commented, skipped = jb.sync_failures_to_jira(
                    model=_sample_model([real_bug, infra_bug]),
                    screenshots_dir=Path(tmp),
                    output_path=out_path,
                    base_url="https://treeda.atlassian.net",
                    email="aashraf@tree.com.sa",
                    api_token="dummy",
                    project_key="UITEST",
                    assignee_query="abanaemah@tree.com.sa",
                )

            self.assertEqual((created, commented, skipped), (0, 1, 1))
            mock_client.create_issue.assert_not_called()
            mock_client.add_comment.assert_called_once()
            saved = json.loads(out_path.read_text(encoding="utf-8"))
            self.assertEqual(saved["testHomeInsuranceFlow1_HappyPath"]["key"], "UITEST-99")
            self.assertEqual(saved["testHomeInsuranceFlow1_HappyPath"]["action"], "commented")


if __name__ == "__main__":
    unittest.main()
