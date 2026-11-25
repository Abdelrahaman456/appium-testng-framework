#!/usr/bin/env python3
"""
Installs or updates the Production Tree app (sa.com.tree.digital.insurance)
directly from Google Play Store on the connected Android emulator via ADB +
UIAutomator XML dump (zero hardcoded coordinates).

Exit codes:
  0 -> App is installed and up to date from Google Play Store
  1 -> Play Store sign-in required or update could not be completed (caller may fall back to prod-apk release)
"""

from __future__ import annotations

import argparse
import re
import subprocess
import sys
import time
import xml.etree.ElementTree as ET
from typing import Optional, Tuple


BOUNDS_RE = re.compile(r"\[(\d+),(\d+)\]\[(\d+),(\d+)\]")


def adb(device: str, *args: str, timeout: int = 30) -> str:
    cmd = ["adb", "-s", device, *args]
    res = subprocess.run(cmd, capture_output=True, text=True, timeout=timeout)
    return (res.stdout or "") + (res.stderr or "")


def get_installed_version(device: str, pkg: str) -> Tuple[str, str]:
    out = adb(device, "shell", "dumpsys", "package", pkg)
    ver = ""
    code = ""
    m_ver = re.search(r"versionName=([^\s\r\n]+)", out)
    m_code = re.search(r"versionCode=(\d+)", out)
    if m_ver:
        ver = m_ver.group(1)
    if m_code:
        code = m_code.group(1)
    return ver, code


def dump_ui_xml(device: str) -> Optional[ET.Element]:
    adb(device, "shell", "uiautomator", "dump", "/sdcard/playstore_ui.xml", timeout=20)
    xml_text = adb(device, "shell", "cat", "/sdcard/playstore_ui.xml", timeout=15)
    start = xml_text.find("<?xml")
    if start == -1:
        start = xml_text.find("<hierarchy")
    if start == -1:
        return None
    try:
        return ET.fromstring(xml_text[start:])
    except ET.ParseError:
        return None


def find_node_center(root: ET.Element, labels: Tuple[str, ...]) -> Optional[Tuple[int, int, str]]:
    want = {lbl.lower() for lbl in labels}
    for node in root.iter("node"):
        text = (node.attrib.get("text") or "").strip()
        desc = (node.attrib.get("content-desc") or "").strip()
        clickable = node.attrib.get("clickable", "false") == "true"
        candidate = text or desc
        if candidate.lower() in want and (clickable or node.attrib.get("class", "").endswith("Button")):
            m = BOUNDS_RE.match(node.attrib.get("bounds", ""))
            if m:
                x1, y1, x2, y2 = map(int, m.groups())
                return ((x1 + x2) // 2, (y1 + y2) // 2, candidate)
    return None


def main() -> int:
    parser = argparse.ArgumentParser(description="Update/Install Tree PROD app from Google Play Store.")
    parser.add_argument("--device", default="emulator-5554")
    parser.add_argument("--package", default="sa.com.tree.digital.insurance")
    parser.add_argument("--timeout", type=int, default=180)
    args = parser.parse_args()

    before_ver, before_code = get_installed_version(args.device, args.package)
    print(f"[PLAYSTORE] Current {args.package}: version={before_ver or 'not-installed'} code={before_code or 'none'}")

    # Open Play Store listing directly
    adb(
        args.device,
        "shell",
        "am",
        "start",
        "-a",
        "android.intent.action.VIEW",
        "-d",
        f"market://details?id={args.package}",
    )
    time.sleep(5)

    deadline = time.time() + args.timeout
    triggered_action = False

    while time.time() < deadline:
        root = dump_ui_xml(args.device)
        if root is None:
            time.sleep(3)
            continue

        # Detect Play Store sign-in wall
        sign_in = find_node_center(root, ("Sign in",))
        if sign_in and not find_node_center(root, ("Install", "Update", "Open", "Play")):
            print("[PLAYSTORE] Google Play Store requires sign-in on this emulator; falling back.")
            return 1

        # Trigger Update or Install if shown
        cta = find_node_center(root, ("Update", "Install"))
        if cta and not triggered_action:
            x, y, label = cta
            print(f"[PLAYSTORE] Found '{label}' button at ({x}, {y}); tapping...")
            adb(args.device, "shell", "input", "tap", str(x), str(y))
            triggered_action = True
            time.sleep(5)
            continue

        # Check if already up to date / finished installing ("Open" visible and no Cancel/Pending)
        open_btn = find_node_center(root, ("Open",))
        cancel_btn = find_node_center(root, ("Cancel",))
        if open_btn and not cancel_btn:
            after_ver, after_code = get_installed_version(args.device, args.package)
            if after_ver:
                print(f"[PLAYSTORE] Ready: {args.package} version={after_ver} code={after_code}")
                return 0

        time.sleep(5)

    after_ver, after_code = get_installed_version(args.device, args.package)
    if after_ver:
        print(f"[PLAYSTORE] Timed out waiting for Play Store UI, but app is installed: version={after_ver} code={after_code}")
        return 0

    print("[PLAYSTORE] Failed to install/update app within timeout.")
    return 1


if __name__ == "__main__":
    raise SystemExit(main())
