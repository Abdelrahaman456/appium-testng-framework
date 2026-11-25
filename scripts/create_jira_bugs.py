#!/usr/bin/env python3
"""
Automatic Jira Cloud Bug Creator & Deduplicator for Tree Mobile Automation.

Reads the same triaged failure model used by scripts/send_teams_report.py and,
for every non-infrastructure FAILED test:
  1. Searches Jira Cloud (REST API v3 /search/jql) for an existing open Bug
     matching label `auto-mobile-<method_name>` in the target project (e.g. UITEST).
  2. If an open Bug already exists -> adds a reproduction comment with run/commit
     metadata (no duplicate tickets).
  3. If no open Bug exists -> creates a new Bug assigned to the configured
     owner (default: Abdulaziz Banaemah / abanaemah@tree.com.sa) with required
     UITEST custom fields (Environment, Product, BugType) and attaches the
     failure screenshot (.png) if present.
  4. Writes target/teams-report/jira-links.json for downstream reporting.

Configuration via environment variables (GitHub Actions Secrets):
  JIRA_BASE_URL          e.g. https://treeda.atlassian.net
  JIRA_EMAIL             Atlassian account email
  JIRA_API_TOKEN         Atlassian API token (NEVER commit to git)
  JIRA_PROJECT_KEY       default: UITEST
  JIRA_ASSIGNEE_QUERY    default: abanaemah@tree.com.sa
  JIRA_ASSIGNEE_ID       optional explicit Jira accountId fallback
"""

from __future__ import annotations

import argparse
import base64
import json
import mimetypes
import os
import re
import sys
import urllib.error
import urllib.parse
import urllib.request
import uuid
from pathlib import Path
from typing import Any, Dict, List, Optional, Tuple

SCRIPT_DIR = Path(__file__).resolve().parent
if str(SCRIPT_DIR) not in sys.path:
    sys.path.insert(0, str(SCRIPT_DIR))

from send_teams_report import (  # noqa: E402
    ReportModel,
    UnifiedTestResult,
    build_report_model,
    format_duration,
)

# UITEST Jira Cloud required custom field option IDs (verified via createmeta)
ENV_OPTION_IDS = {
    "dev": "10129",
    "uat": "10130",
    "pre-prod": "10879",
    "preprod": "10879",
    "staging": "10879",
    "prod": "10131",
}

PRODUCT_OPTION_IDS = {
    "motor": "10602",
    "travel": "10607",
    "home": "10605",
    "pet": "10608",
    "income": "10604",
    "malpractice": "11215",
    "domestic": "10606",
    "sme": "10603",
    "general": "10601",
}

BUGTYPE_OPTION_IDS = {
    "ui": "10632",          # UI | UX
    "business": "10633",    # Business
    "functional": "10634",  # Functional
    "translation": "10635", # Translation
}

SEVERITY_TO_PRIORITY = {
    "Critical": "Highest",
    "High": "High",
    "Medium-High": "Medium",
    "Medium": "Medium",
}

DEFAULT_ASSIGNEE_QUERY = "abanaemah@tree.com.sa"
DEFAULT_ASSIGNEE_ACCOUNT_ID = "712020:a4130249-21ae-45e6-a4b4-04781a6940e6"

INFRA_ERROR_SIGNATURES = (
    "could not find a connected android device",
    "can't find service: settings",
    "instrumentation process is not running",
    "cannot be proxied to uiautomator2 server",
    "socket hang up",
    "sessionnotcreatedexception",
    "nosuchsessionexception",
    "invalid session id",
    'because "this.driver" is null',
    "device offline",
    "econnrefused",
)


def make_dedup_label(method_name: str) -> str:
    cleaned = re.sub(r"[^A-Za-z0-9_-]+", "_", (method_name or "unknown").strip())
    cleaned = cleaned.strip("_") or "unknown"
    return f"auto-mobile-{cleaned[:52]}"


def is_infrastructure_failure(failure: UnifiedTestResult) -> bool:
    triage = failure.triage
    if not triage:
        return False
    combined = " ".join(
        [
            triage.exception_class or "",
            triage.clean_message or "",
            triage.failing_location or "",
        ]
    ).lower()
    return any(sig in combined for sig in INFRA_ERROR_SIGNATURES)


def map_environment_option_id(env_name: str) -> str:
    key = (env_name or "uat").strip().lower()
    return ENV_OPTION_IDS.get(key, ENV_OPTION_IDS["uat"])


def map_product_option_id(suite_name: str, class_name: str, method_name: str) -> str:
    blob = f"{suite_name} {class_name} {method_name}".lower()
    if "motor" in blob:
        return PRODUCT_OPTION_IDS["motor"]
    if "travel" in blob:
        return PRODUCT_OPTION_IDS["travel"]
    if "home" in blob:
        return PRODUCT_OPTION_IDS["home"]
    if "pet" in blob:
        return PRODUCT_OPTION_IDS["pet"]
    if "income" in blob:
        return PRODUCT_OPTION_IDS["income"]
    if "malpractice" in blob:
        return PRODUCT_OPTION_IDS["malpractice"]
    if "domestic" in blob:
        return PRODUCT_OPTION_IDS["domestic"]
    if "sme" in blob:
        return PRODUCT_OPTION_IDS["sme"]
    return PRODUCT_OPTION_IDS["general"]


def map_bugtype_option_id(failure: UnifiedTestResult) -> str:
    blob = f"{failure.suite_name} {failure.method_name} {failure.triage.category if failure.triage else ''}".lower()
    if "localization" in blob or "arabic" in blob or "rtl" in blob or "translation" in blob:
        return BUGTYPE_OPTION_IDS["translation"]
    if "locator" in blob or "element not found" in blob or "ui" in blob:
        return BUGTYPE_OPTION_IDS["ui"]
    return BUGTYPE_OPTION_IDS["functional"]


def _adf_paragraph(text: str, bold_prefix: str = "") -> Dict[str, Any]:
    content: List[Dict[str, Any]] = []
    if bold_prefix:
        content.append({"type": "text", "text": bold_prefix, "marks": [{"type": "strong"}]})
    if text:
        content.append({"type": "text", "text": text})
    if not content:
        content.append({"type": "text", "text": " "})
    return {"type": "paragraph", "content": content}


def _adf_link_paragraph(label: str, url: str) -> Dict[str, Any]:
    return {
        "type": "paragraph",
        "content": [
            {"type": "text", "text": f"{label}: ", "marks": [{"type": "strong"}]},
            {"type": "text", "text": url, "marks": [{"type": "link", "attrs": {"href": url}}]},
        ],
    }


def find_failure_screenshot(failure: UnifiedTestResult, screenshots_dir: Path) -> Optional[Path]:
    if failure.screenshot_file:
        direct = Path(failure.screenshot_file)
        if direct.is_file():
            return direct
        in_dir = screenshots_dir / direct.name
        if in_dir.is_file():
            return in_dir
    if screenshots_dir.is_dir() and failure.method_name:
        matches = sorted(
            screenshots_dir.glob(f"{failure.method_name}_*.png"),
            key=lambda p: p.stat().st_mtime,
            reverse=True,
        )
        if matches:
            return matches[0]
    return None


def build_description_adf(
    model: ReportModel,
    failure: UnifiedTestResult,
    screenshot_name: str = "",
) -> Dict[str, Any]:
    triage = failure.triage
    blocks: List[Dict[str, Any]] = [
        {
            "type": "heading",
            "attrs": {"level": 3},
            "content": [{"type": "text", "text": "Bug Details & Environment"}],
        },
        _adf_paragraph(f"{failure.suite_name}", "Suite: "),
        _adf_paragraph(f"{failure.description or failure.method_name} ({failure.method_name})", "Test Scenario: "),
        _adf_paragraph(f"{model.environment} | App Build: {model.app_version} | Device: {model.device_info}", "Environment: "),
        _adf_paragraph(f"{model.branch} @ {model.commit_short} | {model.timestamp_ksa}", "Build / Run Time: "),
        _adf_paragraph(
            f"{triage.category if triage else 'Functional'} | Severity: {triage.severity if triage else 'High'} | "
            f"Duration: {format_duration(failure.duration_ms)} | Retries: {failure.retries}",
            "Classification: ",
        ),
    ]

    steps = [s.strip() for s in (failure.last_steps or []) if s and s.strip()]
    if steps:
        blocks.append(
            {
                "type": "heading",
                "attrs": {"level": 3},
                "content": [{"type": "text", "text": "Steps Executed Before Failure"}],
            }
        )
        blocks.append(
            {
                "type": "orderedList",
                "content": [
                    {
                        "type": "listItem",
                        "content": [_adf_paragraph(step)],
                    }
                    for step in steps[-10:]
                ],
            }
        )
    elif triage and triage.last_step:
        blocks.append(_adf_paragraph(triage.last_step, "Last Executed Step: "))

    blocks.append(
        {
            "type": "heading",
            "attrs": {"level": 3},
            "content": [{"type": "text", "text": "Actual Result / Root Cause"}],
        }
    )
    blocks.append(
        {
            "type": "codeBlock",
            "attrs": {"language": "text"},
            "content": [
                {
                    "type": "text",
                    "text": f"[{triage.exception_class if triage else 'Error'}] {triage.clean_message if triage else 'Test failed'}",
                }
            ],
        }
    )
    if triage and triage.failing_location:
        blocks.append(_adf_paragraph(triage.failing_location, "Failing Code Location: "))
    if screenshot_name:
        blocks.append(_adf_paragraph(f"Attached to this ticket: {screenshot_name}", "Failure Screenshot (SC): "))
    if model.run_url:
        blocks.append(_adf_link_paragraph("GitHub Actions Run", model.run_url))
    if model.commit_url:
        blocks.append(_adf_link_paragraph("Commit", model.commit_url))
    return {"type": "doc", "version": 1, "content": blocks}


def build_comment_adf(model: ReportModel, failure: UnifiedTestResult) -> Dict[str, Any]:
    triage = failure.triage
    blocks: List[Dict[str, Any]] = [
        _adf_paragraph(
            f"Still reproducing on {model.environment} (App {model.app_version}, {model.device_info}) "
            f"at {model.timestamp_ksa} on {model.branch} @ {model.commit_short}.",
            "[Auto-QA Update] ",
        ),
        _adf_paragraph(
            f"[{triage.exception_class if triage else 'Error'}] {triage.clean_message if triage else 'Test failed'}",
            "Root Cause: ",
        ),
    ]
    if model.run_url:
        blocks.append(_adf_link_paragraph("GitHub Actions Run", model.run_url))
    return {"type": "doc", "version": 1, "content": blocks}


def build_issue_payload(
    model: ReportModel,
    failure: UnifiedTestResult,
    project_key: str,
    assignee_account_id: Optional[str],
    screenshot_name: str = "",
) -> Dict[str, Any]:
    triage = failure.triage
    severity = triage.severity if triage else "High"
    priority_name = SEVERITY_TO_PRIORITY.get(severity, "High")
    short_reason = (triage.clean_message if triage else "Test failed").splitlines()[0].strip()
    if len(short_reason) > 95:
        short_reason = short_reason[:92].rstrip() + "..."

    summary = f"[Mobile {model.environment}] {failure.suite_name} - {failure.method_name}: {short_reason}"
    if len(summary) > 250:
        summary = summary[:247].rstrip() + "..."

    dedup_label = make_dedup_label(failure.method_name)
    fields: Dict[str, Any] = {
        "project": {"key": project_key},
        "issuetype": {"name": "Bug"},
        "summary": summary,
        "description": build_description_adf(model, failure, screenshot_name=screenshot_name),
        "priority": {"name": priority_name},
        "labels": [
            "mobile-automation",
            (model.environment or "uat").lower(),
            dedup_label,
        ],
        # UITEST required custom fields
        "customfield_10082": [{"id": map_environment_option_id(model.environment)}],
        "customfield_10517": {"id": map_product_option_id(failure.suite_name, failure.class_name, failure.method_name)},
        "customfield_10582": {"id": map_bugtype_option_id(failure)},
    }
    if assignee_account_id:
        fields["assignee"] = {"accountId": assignee_account_id}
    return {"fields": fields}


class JiraClient:
    def __init__(self, base_url: str, email: str, api_token: str) -> None:
        self.base_url = base_url.rstrip("/")
        raw = f"{email}:{api_token}".encode("utf-8")
        self.auth_header = "Basic " + base64.b64encode(raw).decode("ascii")

    def _request_json(
        self,
        method: str,
        path: str,
        payload: Optional[Dict[str, Any]] = None,
        timeout: int = 30,
    ) -> Dict[str, Any]:
        url = f"{self.base_url}{path}"
        data = json.dumps(payload, ensure_ascii=False).encode("utf-8") if payload is not None else None
        headers = {
            "Authorization": self.auth_header,
            "Accept": "application/json",
        }
        if data is not None:
            headers["Content-Type"] = "application/json; charset=utf-8"
        req = urllib.request.Request(url, data=data, headers=headers, method=method)
        try:
            with urllib.request.urlopen(req, timeout=timeout) as resp:
                body = resp.read().decode("utf-8", errors="replace")
                return json.loads(body) if body.strip() else {}
        except urllib.error.HTTPError as err:
            err_body = ""
            try:
                err_body = err.read().decode("utf-8", errors="replace")[:500]
            except Exception:
                pass
            raise RuntimeError(f"Jira {method} {path} HTTP {err.code}: {err_body}") from err

    def resolve_assignee_account_id(self, project_key: str, query: str, fallback_id: str = "") -> Optional[str]:
        if query:
            qs = urllib.parse.urlencode({"project": project_key, "query": query})
            try:
                users = self._request_json("GET", f"/rest/api/3/user/assignable/search?{qs}")
                if isinstance(users, list) and users:
                    return users[0].get("accountId") or (fallback_id or None)
            except Exception as exc:
                print(f"[JIRA] Warning: assignee lookup for '{query}' failed ({exc}); using fallback.")
        return fallback_id or None

    def find_open_issue_by_label(self, project_key: str, label: str) -> Optional[str]:
        jql = f'project = "{project_key}" AND labels = "{label}" AND statusCategory != Done ORDER BY created DESC'
        resp = self._request_json(
            "POST",
            "/rest/api/3/search/jql",
            {"jql": jql, "maxResults": 1, "fields": ["key", "status"]},
        )
        issues = resp.get("issues") or []
        if issues:
            return issues[0].get("key")
        return None

    def create_issue(self, payload: Dict[str, Any]) -> str:
        resp = self._request_json("POST", "/rest/api/3/issue", payload)
        key = resp.get("key")
        if not key:
            raise RuntimeError(f"Jira create_issue returned no key: {resp}")
        return key

    def add_comment(self, issue_key: str, body_adf: Dict[str, Any]) -> None:
        self._request_json("POST", f"/rest/api/3/issue/{issue_key}/comment", {"body": body_adf})

    def attach_file(self, issue_key: str, file_path: Path) -> None:
        if not file_path.is_file():
            return
        boundary = f"----JiraBoundary{uuid.uuid4().hex}"
        mime_type = mimetypes.guess_type(str(file_path))[0] or "application/octet-stream"
        file_bytes = file_path.read_bytes()
        body = (
            f"--{boundary}\r\n"
            f'Content-Disposition: form-data; name="file"; filename="{file_path.name}"\r\n'
            f"Content-Type: {mime_type}\r\n\r\n"
        ).encode("utf-8") + file_bytes + f"\r\n--{boundary}--\r\n".encode("utf-8")

        url = f"{self.base_url}/rest/api/3/issue/{issue_key}/attachments"
        req = urllib.request.Request(
            url,
            data=body,
            headers={
                "Authorization": self.auth_header,
                "X-Atlassian-Token": "no-check",
                "Accept": "application/json",
                "Content-Type": f"multipart/form-data; boundary={boundary}",
            },
            method="POST",
        )
        try:
            with urllib.request.urlopen(req, timeout=30) as resp:
                resp.read()
        except Exception as exc:
            print(f"[JIRA] Warning: failed to attach {file_path.name} to {issue_key}: {exc}")


def sync_failures_to_jira(
    model: ReportModel,
    screenshots_dir: Path,
    output_path: Path,
    base_url: str,
    email: str,
    api_token: str,
    project_key: str,
    assignee_query: str,
    assignee_fallback_id: str = DEFAULT_ASSIGNEE_ACCOUNT_ID,
    dry_run: bool = False,
    include_infra: bool = False,
    max_tickets: int = 20,
) -> Tuple[int, int, int]:
    """Returns (created_count, commented_count, skipped_infra_count)."""
    created = 0
    commented = 0
    skipped_infra = 0
    links: Dict[str, Dict[str, str]] = {}

    if not model.failures:
        print("[JIRA] No failed tests to report.")
        output_path.parent.mkdir(parents=True, exist_ok=True)
        output_path.write_text(json.dumps(links, indent=2), encoding="utf-8")
        return (0, 0, 0)

    client = None if dry_run else JiraClient(base_url, email, api_token)
    assignee_id = assignee_fallback_id
    if client is not None:
        assignee_id = client.resolve_assignee_account_id(project_key, assignee_query, assignee_fallback_id)

    processed = 0
    for failure in model.failures:
        if not include_infra and is_infrastructure_failure(failure):
            skipped_infra += 1
            print(f"[JIRA] Skipping CI/emulator infra failure: {failure.method_name}")
            continue
        if processed >= max_tickets:
            print(f"[JIRA] Reached max ticket cap ({max_tickets}); skipping remaining failures.")
            break
        processed += 1

        dedup_label = make_dedup_label(failure.method_name)
        shot_path = find_failure_screenshot(failure, screenshots_dir)
        payload = build_issue_payload(
            model,
            failure,
            project_key,
            assignee_id,
            screenshot_name=shot_path.name if shot_path else "",
        )

        if dry_run:
            print(f"[JIRA][DRY-RUN] Would sync {failure.method_name} -> project={project_key} label={dedup_label} assignee={assignee_id} screenshot={shot_path.name if shot_path else 'none'} summary={payload['fields']['summary']}")
            links[failure.method_name] = {
                "key": f"{project_key}-DRYRUN",
                "url": f"{base_url.rstrip('/')}/browse/{project_key}-DRYRUN",
                "action": "dry-run",
            }
            created += 1
            continue

        assert client is not None
        try:
            existing_key = client.find_open_issue_by_label(project_key, dedup_label)
            if existing_key:
                client.add_comment(existing_key, build_comment_adf(model, failure))
                issue_key = existing_key
                action = "commented"
                commented += 1
                print(f"[JIRA] Updated existing open bug {issue_key} for {failure.method_name}")
            else:
                issue_key = client.create_issue(payload)
                action = "created"
                created += 1
                print(f"[JIRA] Created new bug {issue_key} for {failure.method_name} (assigned to {assignee_id})")

            if shot_path is not None and shot_path.is_file():
                client.attach_file(issue_key, shot_path)
                print(f"[JIRA] Attached screenshot {shot_path.name} to {issue_key}")

            links[failure.method_name] = {
                "key": issue_key,
                "url": f"{base_url.rstrip('/')}/browse/{issue_key}",
                "action": action,
            }
        except Exception as exc:
            print(f"[JIRA] Warning: failed to sync {failure.method_name}: {exc}", file=sys.stderr)

    output_path.parent.mkdir(parents=True, exist_ok=True)
    output_path.write_text(json.dumps(links, indent=2), encoding="utf-8")
    print(f"[JIRA] Sync complete: created={created}, commented={commented}, skipped_infra={skipped_infra}")
    return (created, commented, skipped_infra)


def main(argv: Optional[List[str]] = None) -> int:
    parser = argparse.ArgumentParser(description="Create/update Jira Cloud bugs for failed mobile automation tests.")
    parser.add_argument("--xml", default="target/surefire-reports/testng-results.xml")
    parser.add_argument("--telemetry", default="target/teams-report/test-run-summary.json")
    parser.add_argument("--perf", default="target/perf-reports/performance_benchmark_report.md")
    parser.add_argument("--screenshots-dir", default="target/screenshots")
    parser.add_argument("--videos-dir", default="target/videos")
    parser.add_argument("--output", default="target/teams-report/jira-links.json")
    parser.add_argument("--env", default="")
    parser.add_argument("--dry-run", action="store_true")
    parser.add_argument("--include-infra", action="store_true")
    parser.add_argument("--max-tickets", type=int, default=20)
    args = parser.parse_args(argv)

    base_url = os.environ.get("JIRA_BASE_URL", "https://treeda.atlassian.net").strip()
    email = os.environ.get("JIRA_EMAIL", "").strip()
    api_token = os.environ.get("JIRA_API_TOKEN", "").strip()
    project_key = os.environ.get("JIRA_PROJECT_KEY", "UITEST").strip() or "UITEST"
    assignee_query = os.environ.get("JIRA_ASSIGNEE_QUERY", DEFAULT_ASSIGNEE_QUERY).strip()
    assignee_fallback_id = os.environ.get("JIRA_ASSIGNEE_ID", DEFAULT_ASSIGNEE_ACCOUNT_ID).strip()

    if not args.dry_run and (not email or not api_token):
        print("[JIRA] JIRA_EMAIL or JIRA_API_TOKEN not set; skipping Jira bug sync.")
        return 0

    model = build_report_model(
        xml_path=Path(args.xml),
        telemetry_path=Path(args.telemetry),
        perf_path=Path(args.perf),
        screenshots_dir=Path(args.screenshots_dir),
        videos_dir=Path(args.videos_dir),
        env_override=args.env or None,
    )

    sync_failures_to_jira(
        model=model,
        screenshots_dir=Path(args.screenshots_dir),
        output_path=Path(args.output),
        base_url=base_url,
        email=email,
        api_token=api_token,
        project_key=project_key,
        assignee_query=assignee_query,
        assignee_fallback_id=assignee_fallback_id,
        dry_run=args.dry_run,
        include_infra=args.include_infra,
        max_tickets=args.max_tickets,
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
