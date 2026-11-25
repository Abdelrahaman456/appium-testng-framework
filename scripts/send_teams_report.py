#!/usr/bin/env python3
"""
Daily Microsoft Teams Reporter & Bug Triage Builder for Tree Mobile Automation.

Parses TestNG XML, JSON telemetry (TestListener), failure artifacts, and
Performance Benchmark Markdown to produce:
  1. Full Markdown daily report: target/teams-report/daily_report.md
  2. Microsoft Teams Adaptive Card v1.4 JSON: target/teams-report/teams_card.json
  3. Webhook delivery to the Teams 'Mobile Automation' channel (unless --dry-run)
"""

from __future__ import annotations

import argparse
import json
import os
import re
import sys
import time
import urllib.error
import urllib.request
import xml.etree.ElementTree as ET
from dataclasses import dataclass, field
from datetime import datetime, timedelta, timezone
from pathlib import Path
from typing import Any, Dict, List, Optional, Tuple


KSA_TZ = timezone(timedelta(hours=3))
MAX_TEAMS_PAYLOAD_BYTES = 24576  # Safe ceiling under Microsoft Teams' ~28 KB webhook limit

SEVERITY_RANK = {
    "Critical": 0,
    "High": 1,
    "Medium-High": 2,
    "Medium": 3,
}


@dataclass
class AttemptRecord:
    suite_name: str
    class_name: str
    method_name: str
    signature: str
    description: str
    status: str  # PASS | FAIL | SKIP
    retried: bool
    duration_ms: int
    exception_class: str = ""
    error_message: str = ""
    stacktrace: str = ""


@dataclass
class BugTriage:
    category: str
    severity: str
    clean_message: str
    failing_location: str
    last_step: str
    exception_class: str


@dataclass
class UnifiedTestResult:
    suite_name: str
    class_name: str
    method_name: str
    signature: str
    description: str
    category: str
    status: str  # PASS | FAIL | SKIP
    flaky: bool
    retries: int
    duration_ms: int
    last_steps: List[str] = field(default_factory=list)
    warnings: List[str] = field(default_factory=list)
    uat_elm_blocked: bool = False
    screenshot_file: str = ""
    dom_xml_file: str = ""
    video_file: str = ""
    triage: Optional[BugTriage] = None


@dataclass
class SuiteSummary:
    name: str
    total: int = 0
    passed: int = 0
    failed: int = 0
    skipped: int = 0
    flaky: int = 0
    duration_ms: int = 0

    @property
    def status_label(self) -> str:
        if self.total == 0:
            return "NO TESTS"
        if self.failed > 0:
            return "FAILED"
        if self.flaky > 0 or self.skipped > 0:
            return "WARN"
        return "PASSED"


@dataclass
class PerfRecord:
    test_name: str
    stage_name: str
    elapsed_sec: float
    sla_sec: float
    sla_met: bool


@dataclass
class PerfSummary:
    records: List[PerfRecord] = field(default_factory=list)
    met_count: int = 0
    breached_count: int = 0

    @property
    def total(self) -> int:
        return len(self.records)

    @property
    def compliance_pct(self) -> float:
        return (self.met_count / self.total * 100.0) if self.total else 0.0

    @property
    def slowest_records(self) -> List[PerfRecord]:
        return sorted(self.records, key=lambda r: r.elapsed_sec, reverse=True)

    @property
    def breached_records(self) -> List[PerfRecord]:
        return sorted(
            [r for r in self.records if not r.sla_met],
            key=lambda r: r.elapsed_sec,
            reverse=True,
        )


@dataclass
class ReportModel:
    environment: str
    channel_name: str
    overall_status: str
    card_style: str  # Good | Warning | Attention
    trigger_label: str
    branch: str
    commit_short: str
    commit_message: str
    actor: str
    device_info: str
    app_version: str
    run_url: str
    commit_url: str
    timestamp_ksa: str
    timestamp_utc: str
    total: int
    passed: int
    failed: int
    skipped: int
    flaky: int
    pass_rate_pct: float
    total_duration_ms: int
    suites: List[SuiteSummary]
    failures: List[UnifiedTestResult]
    flaky_tests: List[UnifiedTestResult]
    warnings_list: List[Tuple[str, List[str], bool]]
    config_errors: List[str]
    perf: Optional[PerfSummary]
    jira_links: Dict[str, Dict[str, str]] = field(default_factory=dict)
    jira_board_url: str = ""


def format_duration(duration_ms: int) -> str:
    if duration_ms <= 0:
        return "0s"
    total_secs = duration_ms / 1000.0
    if total_secs < 60:
        return f"{total_secs:.1f}s" if total_secs < 10 else f"{int(round(total_secs))}s"
    mins = int(total_secs // 60)
    secs = int(round(total_secs % 60))
    if secs == 60:
        mins += 1
        secs = 0
    hours = mins // 60
    mins = mins % 60
    if hours > 0:
        return f"{hours}h {mins}m {secs}s"
    return f"{mins}m {secs}s"


def strip_error_boilerplate(raw_message: str, max_len: int = 240) -> str:
    if not raw_message:
        return "No exception message captured."
    cleaned = raw_message
    cut_markers = [
        "For documentation on this error",
        "Build info:",
        "System info:",
        "Driver info:",
        "Capabilities {",
        "Session ID:",
    ]
    for marker in cut_markers:
        idx = cleaned.find(marker)
        if idx > 0:
            cleaned = cleaned[:idx]
    cleaned = re.sub(r"\s+", " ", cleaned).strip()
    if not cleaned:
        cleaned = re.sub(r"\s+", " ", raw_message).strip()
    if len(cleaned) > max_len:
        cleaned = cleaned[: max_len - 3].rstrip() + "..."
    return cleaned


def extract_failing_location(stacktrace: str, telemetry_location: str = "") -> str:
    if telemetry_location:
        return telemetry_location
    if not stacktrace:
        return ""
    pattern = re.compile(r"at\s+((?:pages|tests|utils)\.[A-Za-z0-9_.$]+\([^)\r\n]+\))")
    for match in pattern.finditer(stacktrace):
        frame = match.group(1).strip()
        if not frame.startswith("utils.TestListener.") and not frame.startswith("utils.RetryAnalyzer."):
            return frame
    return ""


def classify_bug(
    exception_class: str,
    raw_message: str,
    stacktrace: str,
    method_name: str,
    class_name: str,
    uat_elm_blocked: bool = False,
) -> Tuple[str, str]:
    exc_msg_blob = " ".join([exception_class or "", raw_message or ""]).lower()
    full_blob = " ".join([exc_msg_blob, stacktrace or "", method_name or "", class_name or ""]).lower()

    crash_keywords = (
        "application not responding",
        "anr",
        "has stopped",
        "force close",
        "fatal exception",
        "instrumentation process is not running",
        "app isn't responding",
        "process died",
        "crash",
    )
    if any(k in full_blob for k in crash_keywords):
        return "App Crash / ANR", "Critical"

    infra_keywords = (
        "sessionnotcreatedexception",
        "nosuchsessionexception",
        "unreachablebrowserexception",
        "econnrefused",
        "socket hang up",
        "connection refused",
        "uiautomator2",
        "adb server",
        "device offline",
    )
    if any(k in exc_msg_blob or k in (stacktrace or "").lower() for k in infra_keywords):
        return "Infrastructure / Driver Error", "Medium"

    backend_keywords = (
        "something went wrong",
        "absher",
        "elm",
        "complaint not found",
        "internal server error",
        "bad gateway",
        "service unavailable",
        "status code 50",
        "http 500",
        "http 502",
        "http 503",
        "http 504",
        "api error",
    )
    if uat_elm_blocked or any(k in exc_msg_blob for k in backend_keywords):
        return "Backend / UAT API & Elm Error", "High"

    explicit_payment_failures = (
        "purchase failed",
        "3ds",
        "acs emulator",
        "payment failed",
        "payment confirmation",
        "card declined",
        "addnewcardmodal",
    )
    if any(k in exc_msg_blob for k in explicit_payment_failures):
        return "Payment / 3DS Gateway Issue", "High"

    locator_keywords = (
        "nosuchelementexception",
        "no visible element found for locator",
        "staleelementreferenceexception",
        "elementclickinterceptedexception",
        "elementnotinteractableexception",
        "invalidselectorexception",
        "unable to locate element",
    )
    if any(k in exc_msg_blob for k in locator_keywords):
        return "UI / Locator Regression", "Medium-High"

    payment_context_keywords = (
        "payment",
        "checkout",
        "pay now",
        "mada",
        "apple pay",
        "card number",
        "cvv",
    )
    if any(k in exc_msg_blob for k in payment_context_keywords) or any(
        k in full_blob for k in ("checkoutscreen.", "addnewcardmodal.", "paymentvalidation")
    ):
        return "Payment / 3DS Gateway Issue", "High"

    timeout_keywords = (
        "timeoutexception",
        "timed out",
        "waituntil",
        "read timed out",
        "sla exceeded",
    )
    if any(k in exc_msg_blob for k in timeout_keywords):
        return "Timeout / Performance", "Medium"

    assertion_keywords = (
        "assertionerror",
        "expected [",
        "expected <",
        "expected true",
        "should be",
    )
    if any(k in exc_msg_blob for k in assertion_keywords):
        return "Functional / Assertion Bug", "High"

    return "Infrastructure / Driver Error", "Medium"


def friendly_suite_name(test_tag_name: str, class_name: str) -> str:
    if test_tag_name and test_tag_name not in ("Surefire test", "Command line test"):
        return test_tag_name
    simple = class_name.split(".")[-1] if class_name else ""
    mapping = {
        "MotorTest": "Motor Product Test",
        "NegativeValidationTest": "Motor Negative Validation",
        "TravelTest": "Travel Product Test",
        "HomeInsuranceTest": "Home Insurance Product Test",
        "PetTest": "Pet Insurance Product Test",
        "IncomeInsuranceTest": "Income Insurance Product Test",
        "MalpracticeTest": "Malpractice Product Test",
        "DomesticWorkerTest": "Domestic Worker Product Test",
        "IndividualHealthTest": "Individual Health Product Test",
        "FamilyHealthTest": "Family Health Product Test",
        "ClaimsTest": "Claims Lifecycle Test",
        "PaymentValidationTest": "Payment Validation Test",
        "SupportAndLocalizationTest": "Support and Localization Test",
        "SmeTest": "SME Products Test",
        "AuthAndProfileTest": "Auth and Profile Test",
    }
    return mapping.get(simple, test_tag_name or simple or "General Suite")


def find_fallback_artifacts(method_name: str, screenshots_dir: Path, videos_dir: Path) -> Tuple[str, str, str]:
    screenshot_file = ""
    dom_xml_file = ""
    video_file = ""
    prefix = f"{method_name.lower()}_"

    if screenshots_dir.exists():
        matches = sorted(
            [p for p in screenshots_dir.iterdir() if p.is_file() and p.name.lower().startswith(prefix)],
            key=lambda p: p.name,
            reverse=True,
        )
        for p in matches:
            lower = p.name.lower()
            if lower.endswith(".png") and not screenshot_file:
                screenshot_file = p.name
            elif lower.endswith("_dom.xml") and not dom_xml_file:
                dom_xml_file = p.name
            elif lower.endswith(".mp4") and not video_file:
                video_file = p.name

    if not video_file and videos_dir.exists():
        vid_matches = sorted(
            [p for p in videos_dir.iterdir() if p.is_file() and p.name.lower().startswith(prefix) and p.name.lower().endswith(".mp4")],
            key=lambda p: p.name,
            reverse=True,
        )
        if vid_matches:
            video_file = vid_matches[0].name

    return screenshot_file, dom_xml_file, video_file


def load_telemetry(telemetry_path: Path) -> Tuple[Dict[str, Any], Dict[Tuple[str, str], List[Dict[str, Any]]]]:
    if not telemetry_path.exists():
        return {}, {}
    try:
        data = json.loads(telemetry_path.read_text(encoding="utf-8"))
    except Exception:
        return {}, {}

    grouped: Dict[Tuple[str, str], List[Dict[str, Any]]] = {}
    for entry in data.get("tests", []):
        key = (entry.get("className", ""), entry.get("methodName", ""))
        grouped.setdefault(key, []).append(entry)
    return data, grouped


def parse_performance_benchmark(perf_path: Path) -> Optional[PerfSummary]:
    if not perf_path.exists():
        return None
    try:
        lines = perf_path.read_text(encoding="utf-8").splitlines()
    except Exception:
        return None

    records: List[PerfRecord] = []
    for line in lines:
        if not line.startswith("| ") or "Product / Test Journey" in line or ":---" in line:
            continue
        cols = [c.strip() for c in line.strip().strip("|").split("|")]
        if len(cols) < 5:
            continue
        try:
            elapsed = float(cols[2].lower().replace("s", "").strip())
            sla = float(cols[3].lower().replace("s", "").strip())
        except ValueError:
            continue
        met = "PASSED" in cols[4].upper()
        records.append(PerfRecord(cols[0], cols[1], elapsed, sla, met))

    if not records:
        return None

    met_count = sum(1 for r in records if r.sla_met)
    breached_count = len(records) - met_count
    return PerfSummary(records=records, met_count=met_count, breached_count=breached_count)


def parse_testng_xml(xml_path: Path) -> Tuple[int, List[SuiteSummary], Dict[Tuple[str, str, str, str], List[AttemptRecord]], List[str]]:
    root = ET.parse(xml_path).getroot()
    suite_el = root.find("suite")
    suite_duration_ms = 0
    if suite_el is not None:
        try:
            suite_duration_ms = int(suite_el.get("duration-ms", 0) or 0)
        except ValueError:
            suite_duration_ms = 0

    suites_by_name: Dict[str, SuiteSummary] = {}
    grouped_attempts: Dict[Tuple[str, str, str, str], List[AttemptRecord]] = {}
    config_errors: List[str] = []

    for test_el in root.iter("test"):
        raw_test_name = test_el.get("name", "Default Test")
        try:
            test_duration = int(test_el.get("duration-ms", 0) or 0)
        except ValueError:
            test_duration = 0

        for cls_el in test_el.findall("class"):
            class_name = cls_el.get("name", "")
            suite_name = friendly_suite_name(raw_test_name, class_name)
            if suite_name not in suites_by_name:
                suites_by_name[suite_name] = SuiteSummary(name=suite_name, duration_ms=test_duration)

            for m_el in cls_el.findall("test-method"):
                status = (m_el.get("status") or "").upper()
                if m_el.get("is-config") == "true":
                    if status == "FAIL":
                        msg_el = m_el.find("exception/message")
                        err = strip_error_boilerplate(msg_el.text if msg_el is not None else "", max_len=140)
                        config_errors.append(f"{suite_name} :: {m_el.get('name', 'config')} - {err}")
                    continue

                method_name = m_el.get("name", "")
                signature = m_el.get("signature", method_name)
                description = (m_el.get("description") or "").strip()
                retried = (m_el.get("retried") or "").lower() == "true"
                try:
                    duration_ms = int(m_el.get("duration-ms", 0) or 0)
                except ValueError:
                    duration_ms = 0

                exc_el = m_el.find("exception")
                exc_cls = exc_el.get("class", "") if exc_el is not None else ""
                msg_el = exc_el.find("message") if exc_el is not None else None
                st_el = exc_el.find("full-stacktrace") if exc_el is not None else None
                err_msg = (msg_el.text or "").strip() if msg_el is not None and msg_el.text else ""
                stack = (st_el.text or "").strip() if st_el is not None and st_el.text else ""

                key = (suite_name, class_name, method_name, signature)
                grouped_attempts.setdefault(key, []).append(
                    AttemptRecord(
                        suite_name=suite_name,
                        class_name=class_name,
                        method_name=method_name,
                        signature=signature,
                        description=description,
                        status=status,
                        retried=retried,
                        duration_ms=duration_ms,
                        exception_class=exc_cls,
                        error_message=err_msg,
                        stacktrace=stack,
                    )
                )

    if suite_duration_ms == 0:
        suite_duration_ms = sum(s.duration_ms for s in suites_by_name.values())

    return suite_duration_ms, list(suites_by_name.values()), grouped_attempts, config_errors


def resolve_trigger_label() -> str:
    event = os.environ.get("GITHUB_EVENT_NAME", "").strip().lower()
    if event == "schedule":
        return "Daily Schedule (11:00 AM KSA)"
    if event == "workflow_dispatch":
        return "Manual CI Dispatch"
    if event == "push":
        return "Git Push"
    return "Local Execution"


def build_report_model(
    xml_path: Path,
    telemetry_path: Path,
    perf_path: Path,
    screenshots_dir: Path,
    videos_dir: Path,
    env_override: Optional[str] = None,
    channel_override: Optional[str] = None,
) -> ReportModel:
    telemetry_meta, telemetry_by_test = load_telemetry(telemetry_path)
    perf_summary = parse_performance_benchmark(perf_path)

    total_duration_ms = 0
    suites: List[SuiteSummary] = []
    config_errors: List[str] = []
    unified_tests: List[UnifiedTestResult] = []

    if xml_path.exists():
        total_duration_ms, suites, grouped_attempts, config_errors = parse_testng_xml(xml_path)
        suite_map = {s.name: s for s in suites}

        for (suite_name, class_name, method_name, signature), attempts in grouped_attempts.items():
            final_attempt = attempts[-1]
            retries = max(0, len(attempts) - 1)
            had_retry_skip = any(a.retried or a.status == "SKIP" for a in attempts[:-1])
            flaky = final_attempt.status == "PASS" and (had_retry_skip or retries > 0)

            tel_records = telemetry_by_test.get((class_name, method_name), [])
            if not tel_records:
                tel_records = telemetry_by_test.get(("", method_name), [])
            tel = tel_records[-1] if tel_records else {}

            all_warnings: List[str] = []
            for tr in tel_records:
                for w in tr.get("warnings", []):
                    if w not in all_warnings:
                        all_warnings.append(w)

            elm_blocked = any(bool(tr.get("uatElmBlocked", False)) for tr in tel_records)
            last_steps = list(tel.get("lastSteps", []))
            category = tel.get("category") or suite_name
            description = final_attempt.description or tel.get("description") or method_name

            fb_scr, fb_xml, fb_vid = find_fallback_artifacts(method_name, screenshots_dir, videos_dir)
            screenshot_file = tel.get("screenshotFile") or fb_scr
            dom_xml_file = tel.get("domXmlFile") or fb_xml
            video_file = tel.get("videoFile") or fb_vid

            triage: Optional[BugTriage] = None
            if final_attempt.status == "FAIL":
                raw_err = final_attempt.error_message or tel.get("errorMessage") or ""
                exc_cls = final_attempt.exception_class or tel.get("exceptionClass") or "Exception"
                fail_loc = extract_failing_location(final_attempt.stacktrace, tel.get("failingLocation", ""))
                bug_cat, bug_sev = classify_bug(
                    exc_cls,
                    raw_err,
                    final_attempt.stacktrace,
                    method_name,
                    class_name,
                    uat_elm_blocked=elm_blocked,
                )
                non_perf_steps = [s for s in last_steps if not s.startswith("[PERF BENCHMARK]")]
                last_step = non_perf_steps[-1] if non_perf_steps else (last_steps[-1] if last_steps else "")
                triage = BugTriage(
                    category=bug_cat,
                    severity=bug_sev,
                    clean_message=strip_error_boilerplate(raw_err, max_len=240),
                    failing_location=fail_loc,
                    last_step=last_step,
                    exception_class=exc_cls.split(".")[-1] if exc_cls else "Exception",
                )

            unified = UnifiedTestResult(
                suite_name=suite_name,
                class_name=class_name,
                method_name=method_name,
                signature=signature,
                description=description,
                category=category,
                status=final_attempt.status,
                flaky=flaky,
                retries=retries,
                duration_ms=final_attempt.duration_ms,
                last_steps=last_steps,
                warnings=all_warnings,
                uat_elm_blocked=elm_blocked,
                screenshot_file=screenshot_file,
                dom_xml_file=dom_xml_file,
                video_file=video_file,
                triage=triage,
            )
            unified_tests.append(unified)

            s_sum = suite_map.get(suite_name)
            if s_sum is not None:
                s_sum.total += 1
                if final_attempt.status == "PASS":
                    s_sum.passed += 1
                elif final_attempt.status == "FAIL":
                    s_sum.failed += 1
                elif final_attempt.status == "SKIP":
                    s_sum.skipped += 1
                if flaky:
                    s_sum.flaky += 1

    total = len(unified_tests)
    passed = sum(1 for t in unified_tests if t.status == "PASS")
    failed = sum(1 for t in unified_tests if t.status == "FAIL")
    skipped = sum(1 for t in unified_tests if t.status == "SKIP")
    flaky_tests = [t for t in unified_tests if t.flaky]
    flaky_count = len(flaky_tests)
    pass_rate_pct = (passed / total * 100.0) if total > 0 else 0.0

    failures = [t for t in unified_tests if t.status == "FAIL"]
    failures.sort(key=lambda t: (SEVERITY_RANK.get(t.triage.severity if t.triage else "Medium", 9), t.suite_name))

    warnings_list: List[Tuple[str, List[str], bool]] = [
        (t.method_name, t.warnings, t.uat_elm_blocked)
        for t in unified_tests
        if t.warnings or t.uat_elm_blocked
    ]

    maven_outcome = os.environ.get("MAVEN_OUTCOME", "").strip().lower()
    job_status = os.environ.get("JOB_STATUS", "").strip().lower()

    if maven_outcome == "cancelled" or job_status == "cancelled":
        overall_status = "CANCELLED - RUN ABORTED"
        card_style = "Warning"
    elif total == 0:
        overall_status = "WARNING - NO TESTS EXECUTED"
        card_style = "Attention" if config_errors else "Warning"
    elif failed == 0 and skipped == 0 and flaky_count == 0 and not config_errors:
        overall_status = "PASSED - ALL FLOWS GREEN"
        card_style = "Good"
    elif failed == 0:
        overall_status = f"PASSED WITH WARNINGS ({flaky_count} FLAKY / {skipped} SKIPPED)"
        card_style = "Warning"
    else:
        overall_status = f"FAILED - {failed} BUG(S) DETECTED"
        card_style = "Attention"

    now_utc = datetime.now(timezone.utc)
    now_ksa = now_utc.astimezone(KSA_TZ)

    environment = env_override or os.environ.get("APP_ENV") or telemetry_meta.get("environment") or "UAT"
    channel_name = channel_override or os.environ.get("TEAMS_CHANNEL_NAME") or "Mobile Automation"

    raw_sha = os.environ.get("GITHUB_SHA", "").strip()
    commit_short = raw_sha[:7] if raw_sha else "local"
    commit_msg = strip_error_boilerplate(os.environ.get("COMMIT_MESSAGE", ""), max_len=80)
    if commit_msg == "No exception message captured.":
        commit_msg = ""

    return ReportModel(
        environment=environment.upper(),
        channel_name=channel_name,
        overall_status=overall_status,
        card_style=card_style,
        trigger_label=resolve_trigger_label(),
        branch=os.environ.get("GITHUB_REF_NAME", "").strip() or "main",
        commit_short=commit_short,
        commit_message=commit_msg,
        actor=os.environ.get("ACTOR", "").strip() or os.environ.get("USERNAME", "") or os.environ.get("USER", "") or "CI Bot",
        device_info=os.environ.get("DEVICE_INFO", "").strip() or "Android Emulator",
        app_version=os.environ.get("APP_VERSION", "").strip() or telemetry_meta.get("appPackage") or "sa.com.tree.digital.insurance.uat",
        run_url=os.environ.get("RUN_URL", "").strip(),
        commit_url=os.environ.get("COMMIT_URL", "").strip(),
        timestamp_ksa=now_ksa.strftime("%Y-%m-%d %H:%M KSA (UTC+3)"),
        timestamp_utc=now_utc.strftime("%Y-%m-%d %H:%M UTC"),
        total=total,
        passed=passed,
        failed=failed,
        skipped=skipped,
        flaky=flaky_count,
        pass_rate_pct=pass_rate_pct,
        total_duration_ms=total_duration_ms,
        suites=suites,
        failures=failures,
        flaky_tests=flaky_tests,
        warnings_list=warnings_list,
        config_errors=config_errors,
        perf=perf_summary,
        jira_links=_load_jira_links(telemetry_path.parent / "jira-links.json"),
        jira_board_url=os.environ.get("JIRA_BOARD_URL", "https://treeda.atlassian.net/jira/software/projects/UITEST/boards/1076").strip(),
    )


def _load_jira_links(jira_links_path: Path) -> Dict[str, Dict[str, str]]:
    if not jira_links_path.is_file():
        return {}
    try:
        data = json.loads(jira_links_path.read_text(encoding="utf-8"))
        return data if isinstance(data, dict) else {}
    except Exception:
        return {}


def render_markdown_report(model: ReportModel) -> str:
    lines: List[str] = [
        f"# Daily Mobile Automation Report ({model.environment})",
        "",
        f"- **Channel**: {model.channel_name}",
        f"- **Status**: {model.overall_status}",
        f"- **Run Time**: {model.timestamp_ksa} / {model.timestamp_utc}",
        f"- **Trigger**: {model.trigger_label}",
        f"- **Branch / Commit**: `{model.branch}` @ `{model.commit_short}`" + (f" — {model.commit_message}" if model.commit_message else ""),
        f"- **App / Device**: `{model.app_version}` on {model.device_info}",
        f"- **Triggered By**: {model.actor}",
        "",
        "## Execution KPIs",
        "",
        f"| Total | Passed | Failed (Bugs) | Flaky (Retried) | Skipped | Pass Rate | Duration |",
        f"| :---: | :---: | :---: | :---: | :---: | :---: | :---: |",
        f"| {model.total} | {model.passed} | {model.failed} | {model.flaky} | {model.skipped} | {model.pass_rate_pct:.1f}% | {format_duration(model.total_duration_ms)} |",
        "",
    ]

    if model.failures:
        lines.extend(["## Detailed Bug & Defect Triage", ""])
        for idx, bug in enumerate(model.failures, start=1):
            t = bug.triage
            assert t is not None
            lines.append(f"### Bug #{idx}: `{bug.method_name}` — {t.category} ({t.severity})")
            jira_info = model.jira_links.get(bug.method_name)
            if jira_info and jira_info.get("key") and jira_info.get("url"):
                action_tag = "Created" if jira_info.get("action") == "created" else "Updated"
                lines.append(f"- **Jira Ticket**: [{jira_info['key']}]({jira_info['url']}) ({action_tag})")
            lines.append(f"- **Suite / Flow**: {bug.suite_name} — {bug.description}")
            lines.append(f"- **Exception**: `{t.exception_class}`")
            lines.append(f"- **Root Cause**: {t.clean_message}")
            if t.failing_location:
                lines.append(f"- **Failing Location**: `{t.failing_location}`")
            if t.last_step:
                lines.append(f"- **Last Step**: {t.last_step}")
            lines.append(f"- **Retries**: {bug.retries} | **Duration**: {format_duration(bug.duration_ms)}")
            artifacts = [x for x in (bug.screenshot_file, bug.dom_xml_file, bug.video_file) if x]
            if artifacts:
                lines.append(f"- **Artifacts**: `{'`, `'.join(artifacts)}`")
            lines.append("")

    if model.config_errors:
        lines.extend(["## Setup / Configuration Failures", ""])
        for ce in model.config_errors:
            lines.append(f"- {ce}")
        lines.append("")

    lines.extend(["## Suite Breakdown", ""])
    if model.suites:
        lines.append("| Suite | Status | Passed / Total | Failed | Flaky | Duration |")
        lines.append("| :--- | :---: | :---: | :---: | :---: | :---: |")
        for s in model.suites:
            lines.append(f"| {s.name} | {s.status_label} | {s.passed}/{s.total} | {s.failed} | {s.flaky} | {format_duration(s.duration_ms)} |")
    else:
        lines.append("_No test suites executed._")
    lines.append("")

    if model.flaky_tests or model.warnings_list:
        lines.extend(["## Flaky Retries & UAT Warnings", ""])
        for ft in model.flaky_tests:
            lines.append(f"- **Flaky Pass**: `{ft.method_name}` ({ft.suite_name}) passed after {ft.retries} retry")
        for method_name, warns, elm in model.warnings_list:
            prefix = "[UAT Elm Blocker] " if elm else ""
            lines.append(f"- **{method_name}**: {prefix}{'; '.join(warns) if warns else 'UAT Elm dependent data blocked Proceed'}")
        lines.append("")

    if model.perf and model.perf.total > 0:
        lines.extend([
            "## Performance & SLA Summary",
            "",
            f"- **SLA Compliance**: {model.perf.met_count}/{model.perf.total} ({model.perf.compliance_pct:.1f}%) | **SLA Breaches**: {model.perf.breached_count}",
            "",
            "| Test Journey | Stage | Elapsed | SLA | Status |",
            "| :--- | :--- | :---: | :---: | :---: |",
        ])
        for rec in model.perf.slowest_records[:15]:
            lines.append(f"| {rec.test_name} | {rec.stage_name} | {rec.elapsed_sec:.2f}s | {rec.sla_sec:.1f}s | {'PASSED' if rec.sla_met else 'EXCEEDED'} |")
        lines.append("")

    if model.run_url:
        lines.append(f"**CI Run Artifacts**: {model.run_url}")

    return "\n".join(lines).strip() + "\n"


def _kpi_column(label: str, value: str, color: str = "Default") -> Dict[str, Any]:
    return {
        "type": "Column",
        "width": "stretch",
        "items": [
            {"type": "TextBlock", "text": label, "isSubtle": True, "size": "Small", "horizontalAlignment": "Center"},
            {"type": "TextBlock", "text": value, "weight": "Bolder", "size": "Large", "color": color, "horizontalAlignment": "Center", "spacing": "None"},
        ],
    }


def _suite_badge(status_label: str) -> Tuple[str, str]:
    if status_label == "PASSED":
        return "PASS", "Good"
    if status_label == "FAILED":
        return "FAIL", "Attention"
    return status_label, "Warning"


def _severity_color(severity: str) -> str:
    if severity in ("Critical", "High"):
        return "Attention"
    if severity == "Medium-High":
        return "Warning"
    return "Default"


def build_adaptive_card_payload(
    model: ReportModel,
    max_bugs: int = 8,
    max_perf_rows: int = 3,
    compact_suites: bool = False,
) -> Dict[str, Any]:
    body: List[Dict[str, Any]] = []

    # 1. Header Banner (clean & scannable)
    body.append({
        "type": "Container",
        "style": model.card_style,
        "bleed": True,
        "items": [
            {
                "type": "TextBlock",
                "text": f"Tree Mobile Automation | Daily Report ({model.environment})",
                "weight": "Bolder",
                "size": "Large",
                "wrap": True,
            },
            {
                "type": "TextBlock",
                "text": f"**{model.overall_status}**  •  {model.timestamp_ksa}",
                "wrap": True,
                "spacing": "Small",
            },
        ],
    })

    # 2. 4-Column KPI Strip (key numbers at a glance)
    pass_color = "Good" if model.failed == 0 and model.total > 0 else ("Attention" if model.failed > 0 else "Warning")
    fail_color = "Attention" if model.failed > 0 else "Good"
    body.append({
        "type": "ColumnSet",
        "spacing": "Medium",
        "separator": True,
        "columns": [
            _kpi_column("Pass Rate", f"{model.pass_rate_pct:.1f}%", pass_color),
            _kpi_column("Passed", f"{model.passed}/{model.total}", "Good" if model.passed > 0 else "Default"),
            _kpi_column("Failed (Bugs)", str(model.failed), fail_color),
            _kpi_column("Duration", format_duration(model.total_duration_ms), "Default"),
        ],
    })

    # 3. Compact Run Context (3 essential lines only)
    facts = [
        {"title": "App Build", "value": f"{model.app_version} ({model.environment})"},
        {"title": "Device", "value": model.device_info},
        {"title": "Run", "value": f"{model.trigger_label}  •  {model.branch} @ {model.commit_short}"},
    ]
    body.append({"type": "FactSet", "facts": facts, "spacing": "Medium", "separator": True})

    # 4. Configuration / Setup Errors (if any)
    if model.config_errors:
        body.append({
            "type": "Container",
            "style": "Attention",
            "spacing": "Medium",
            "items": [
                {"type": "TextBlock", "text": "Setup / Configuration Failure(s)", "weight": "Bolder", "color": "Attention", "wrap": True},
                {"type": "TextBlock", "text": "\n".join(f"- {c}" for c in model.config_errors[:4]), "wrap": True, "size": "Small"},
            ],
        })

    # 5. Detailed Bug & Defect Triage Section (prominent when failures exist)
    if model.failures:
        cat_counts: Dict[str, int] = {}
        for bug in model.failures:
            cat = bug.triage.category if bug.triage else "Uncategorized"
            cat_counts[cat] = cat_counts.get(cat, 0) + 1
        summary_line = " | ".join(f"{k}: {v}" for k, v in cat_counts.items())

        body.append({
            "type": "TextBlock",
            "text": f"Bugs & Defect Triage ({model.failed})",
            "weight": "Bolder",
            "size": "Medium",
            "color": "Attention",
            "spacing": "Large",
            "separator": True,
            "wrap": True,
        })
        body.append({
            "type": "TextBlock",
            "text": f"**Category Breakdown:** {summary_line}",
            "size": "Small",
            "wrap": True,
            "spacing": "Small",
        })
        if model.jira_links:
            created_cnt = sum(1 for v in model.jira_links.values() if v.get("action") == "created")
            updated_cnt = sum(1 for v in model.jira_links.values() if v.get("action") == "commented")
            jira_parts = []
            if created_cnt:
                jira_parts.append(f"{created_cnt} created")
            if updated_cnt:
                jira_parts.append(f"{updated_cnt} updated")
            if jira_parts:
                body.append({
                    "type": "TextBlock",
                    "text": f"**Jira Sync (UITEST):** {' | '.join(jira_parts)}",
                    "size": "Small",
                    "wrap": True,
                    "spacing": "None",
                })

        shown_bugs = model.failures[:max_bugs] if max_bugs > 0 else []
        for idx, bug in enumerate(shown_bugs, start=1):
            t = bug.triage
            assert t is not None
            issue_text = (t.clean_message or "Test failed").strip()
            if len(issue_text) > 180:
                issue_text = issue_text[:177].rstrip() + "..."
            bug_facts = [
                {"title": "Product", "value": bug.suite_name},
                {"title": "Issue", "value": issue_text},
            ]
            jira_info = model.jira_links.get(bug.method_name)
            if jira_info and jira_info.get("key") and jira_info.get("url"):
                action_tag = "Created" if jira_info.get("action") == "created" else "Updated"
                bug_facts.append({"title": "Jira Ticket", "value": f"[{jira_info['key']}]({jira_info['url']}) ({action_tag})"})
            if t.last_step and not t.last_step.startswith("[PERF"):
                bug_facts.append({"title": "Failed At", "value": t.last_step})

            human_title = bug.description.strip() if bug.description and bug.description.strip() else bug.method_name
            body.append({
                "type": "Container",
                "style": "Emphasis",
                "spacing": "Small",
                "items": [
                    {
                        "type": "TextBlock",
                        "text": f"#{idx} [{t.severity.upper()}] {human_title}",
                        "weight": "Bolder",
                        "color": _severity_color(t.severity),
                        "size": "Small",
                        "wrap": True,
                    },
                    {"type": "FactSet", "facts": bug_facts, "spacing": "Small"},
                ],
            })

        omitted_bugs = len(model.failures) - len(shown_bugs)
        if omitted_bugs > 0:
            body.append({
                "type": "TextBlock",
                "text": f"+ {omitted_bugs} more bug(s) omitted to stay within Teams card size limits. Full triage is available in `daily_report.md` and `ExtentReport.html` artifacts.",
                "isSubtle": True,
                "size": "Small",
                "wrap": True,
                "spacing": "Small",
            })

    # 6. Suite-by-Suite Breakdown
    if model.suites:
        suite_word = "Suite" if len(model.suites) == 1 else "Suites"
        body.append({
            "type": "TextBlock",
            "text": f"Suite Breakdown ({len(model.suites)} {suite_word})",
            "weight": "Bolder",
            "size": "Medium",
            "spacing": "Large",
            "separator": True,
            "wrap": True,
        })
        suites_to_render = model.suites
        if compact_suites:
            failing_or_warn = [s for s in model.suites if s.failed > 0 or s.flaky > 0 or s.skipped > 0]
            suites_to_render = failing_or_warn if failing_or_warn else model.suites[:6]

        for s in suites_to_render:
            badge_text, badge_color = _suite_badge(s.status_label)
            body.append({
                "type": "ColumnSet",
                "spacing": "Small",
                "columns": [
                    {"type": "Column", "width": "stretch", "items": [{"type": "TextBlock", "text": s.name, "size": "Small", "wrap": True}]},
                    {"type": "Column", "width": "auto", "items": [{"type": "TextBlock", "text": badge_text, "weight": "Bolder", "color": badge_color, "size": "Small"}]},
                    {"type": "Column", "width": "auto", "items": [{"type": "TextBlock", "text": f"{s.passed}/{s.total}", "size": "Small", "horizontalAlignment": "Right"}]},
                    {"type": "Column", "width": "auto", "items": [{"type": "TextBlock", "text": format_duration(s.duration_ms), "isSubtle": True, "size": "Small", "horizontalAlignment": "Right"}]},
                ],
            })
        if compact_suites and len(suites_to_render) < len(model.suites):
            body.append({
                "type": "TextBlock",
                "text": f"Showing {len(suites_to_render)} of {len(model.suites)} suites cleanly (remaining suites passed 100%; see `daily_report.md`).",
                "isSubtle": True,
                "size": "Small",
                "wrap": True,
            })

    # 7. Flaky / Retried & UAT Warnings
    if model.flaky_tests or model.warnings_list:
        warn_lines: List[str] = []
        for ft in model.flaky_tests[:5]:
            warn_lines.append(f"- **Flaky Pass**: `{ft.method_name}` passed after {ft.retries} retry")
        for method_name, warns, elm in model.warnings_list[:4]:
            tag = "[UAT Elm Blocker] " if elm else ""
            msg = warns[0] if warns else "Dependent Iqama blocked by UAT Elm"
            warn_lines.append(f"- `{method_name}`: {tag}{strip_error_boilerplate(msg, 120)}")
        body.append({
            "type": "Container",
            "style": "Warning",
            "spacing": "Medium",
            "items": [
                {"type": "TextBlock", "text": "Flaky Retries & UAT Environment Notices", "weight": "Bolder", "color": "Warning", "size": "Small", "wrap": True},
                {"type": "TextBlock", "text": "\n".join(warn_lines), "size": "Small", "wrap": True, "spacing": "Small"},
            ],
        })

    # 8. Performance & SLA Health
    if model.perf and model.perf.total > 0:
        perf_summary_line = (
            f"SLA Compliance: **{model.perf.met_count}/{model.perf.total} ({model.perf.compliance_pct:.1f}%)**  |  "
            f"Breaches: **{model.perf.breached_count}**"
        )
        perf_items: List[Dict[str, Any]] = [
            {"type": "TextBlock", "text": "Performance & SLA Health", "weight": "Bolder", "size": "Medium", "wrap": True},
            {"type": "TextBlock", "text": perf_summary_line, "size": "Small", "wrap": True, "spacing": "Small"},
        ]
        if max_perf_rows > 0:
            focus_rows = model.perf.breached_records[:max_perf_rows] or model.perf.slowest_records[:max_perf_rows]
            if focus_rows:
                prefix = "Top SLA Breaches:" if model.perf.breached_records else "Slowest E2E Flows:"
                rows_text = "\n".join(
                    f"- `{r.test_name}` ({r.stage_name}): **{r.elapsed_sec:.2f}s** / SLA {r.sla_sec:.1f}s"
                    for r in focus_rows
                )
                perf_items.append({"type": "TextBlock", "text": f"{prefix}\n{rows_text}", "size": "Small", "wrap": True, "spacing": "Small"})
        body.append({"type": "Container", "spacing": "Medium", "separator": True, "items": perf_items})

    # 8. Actions (Full Report & Jira Board)
    actions: List[Dict[str, Any]] = []
    if model.run_url:
        actions.append({
            "type": "Action.OpenUrl",
            "title": "View Full Report & Screenshots",
            "url": model.run_url,
        })
    if model.jira_links and model.jira_board_url:
        actions.append({
            "type": "Action.OpenUrl",
            "title": "Open Jira Board (UITEST)",
            "url": model.jira_board_url,
        })

    card: Dict[str, Any] = {
        "$schema": "http://adaptivecards.io/schemas/adaptive-card.json",
        "type": "AdaptiveCard",
        "version": "1.4",
        "msteams": {"width": "Full"},
        "body": body,
    }
    if actions:
        card["actions"] = actions

    return {
        "type": "message",
        "attachments": [
            {
                "contentType": "application/vnd.microsoft.card.adaptive",
                "contentUrl": None,
                "content": card,
            }
        ],
    }


def build_size_bounded_card(model: ReportModel, max_bytes: int = MAX_TEAMS_PAYLOAD_BYTES) -> Dict[str, Any]:
    attempts = [
        (8, 3, False),
        (5, 2, False),
        (3, 1, False),
        (1, 0, False),
        (0, 0, False),
        (0, 0, True),
    ]
    last_payload: Dict[str, Any] = {}
    for max_bugs, max_perf, compact_suites in attempts:
        last_payload = build_adaptive_card_payload(
            model,
            max_bugs=max_bugs,
            max_perf_rows=max_perf,
            compact_suites=compact_suites,
        )
        size = len(json.dumps(last_payload, ensure_ascii=False).encode("utf-8"))
        if size <= max_bytes:
            return last_payload
    return last_payload


def post_to_teams(webhook_url: str, payload: Dict[str, Any], max_attempts: int = 3) -> int:
    data = json.dumps(payload, ensure_ascii=False).encode("utf-8")
    for attempt in range(1, max_attempts + 1):
        req = urllib.request.Request(
            webhook_url,
            data=data,
            headers={"Content-Type": "application/json; charset=utf-8"},
            method="POST",
        )
        try:
            with urllib.request.urlopen(req, timeout=30) as resp:
                code = resp.getcode()
                if code in (200, 202):
                    return code
                raise RuntimeError(f"Unexpected HTTP status {code} from Teams webhook")
        except urllib.error.HTTPError as err:
            retriable = err.code == 429 or 500 <= err.code < 600
            if retriable and attempt < max_attempts:
                retry_after = err.headers.get("Retry-After") if err.headers else None
                delay = int(retry_after) if (retry_after and retry_after.isdigit()) else attempt * 2
                print(f"[TEAMS] HTTP {err.code} on attempt {attempt}/{max_attempts}; retrying in {delay}s...")
                time.sleep(delay)
                continue
            body = ""
            try:
                body = err.read().decode("utf-8", errors="replace")[:300]
            except Exception:
                pass
            raise RuntimeError(f"Teams webhook HTTP {err.code}: {body}") from err
        except Exception as err:
            if attempt < max_attempts:
                delay = attempt * 2
                print(f"[TEAMS] Network error ({err}) on attempt {attempt}/{max_attempts}; retrying in {delay}s...")
                time.sleep(delay)
                continue
            raise
    return 0


def main(argv: Optional[List[str]] = None) -> int:
    parser = argparse.ArgumentParser(description="Build & send daily Mobile Automation Teams report.")
    parser.add_argument("--xml", default="target/surefire-reports/testng-results.xml")
    parser.add_argument("--telemetry", default="target/teams-report/test-run-summary.json")
    parser.add_argument("--perf", default="target/perf-reports/performance_benchmark_report.md")
    parser.add_argument("--screenshots-dir", default="target/screenshots")
    parser.add_argument("--videos-dir", default="target/videos")
    parser.add_argument("--output-dir", default="target/teams-report")
    parser.add_argument("--webhook-url", default="")
    parser.add_argument("--channel", default="")
    parser.add_argument("--env", default="")
    parser.add_argument("--dry-run", action="store_true")
    args = parser.parse_args(argv)

    model = build_report_model(
        xml_path=Path(args.xml),
        telemetry_path=Path(args.telemetry),
        perf_path=Path(args.perf),
        screenshots_dir=Path(args.screenshots_dir),
        videos_dir=Path(args.videos_dir),
        env_override=args.env or None,
        channel_override=args.channel or None,
    )

    out_dir = Path(args.output_dir)
    out_dir.mkdir(parents=True, exist_ok=True)

    md_path = out_dir / "daily_report.md"
    card_path = out_dir / "teams_card.json"

    md_text = render_markdown_report(model)
    md_path.write_text(md_text, encoding="utf-8")

    payload = build_size_bounded_card(model)
    card_json = json.dumps(payload, indent=2, ensure_ascii=False)
    card_path.write_text(card_json, encoding="utf-8")

    payload_bytes = len(json.dumps(payload, ensure_ascii=False).encode("utf-8"))
    print(f"[TEAMS REPORT] Status: {model.overall_status} | Pass={model.passed}/{model.total} Fail={model.failed} Flaky={model.flaky}")
    print(f"[TEAMS REPORT] Wrote {md_path} and {card_path} ({payload_bytes} bytes)")

    webhook_url = (args.webhook_url or os.environ.get("TEAMS_WEBHOOK_URL", "")).strip()
    if args.dry_run:
        print("[TEAMS REPORT] --dry-run active; skipping webhook POST.")
        return 0
    if not webhook_url:
        print("[TEAMS REPORT] TEAMS_WEBHOOK_URL not set; skipping webhook POST.")
        return 0

    try:
        status_code = post_to_teams(webhook_url, payload)
        print(f"[TEAMS REPORT] Teams notification sent successfully (HTTP {status_code})!")
    except Exception as exc:
        print(f"[TEAMS REPORT] Warning: Teams webhook delivery failed: {exc}", file=sys.stderr)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
