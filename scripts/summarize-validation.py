"""Print measured CI counts; missing outputs are explicitly reported."""
import collections
import json
from pathlib import Path
import xml.etree.ElementTree as ET



def junit_summary():
    reports = list(Path("build/test-results/test").glob("TEST-*.xml"))
    if not reports:
        return "JUnit: output unavailable"
    totals = collections.Counter()
    for report in reports:
        suite = ET.parse(report).getroot()
        for key in ("tests", "failures", "errors", "skipped"):
            totals[key] += int(suite.attrib[key])
    return "JUnit: " + ", ".join(f"{key}={totals[key]}" for key in ("tests", "failures", "errors", "skipped"))


def audit_summary(name):
    path = Path(name)
    if not path.is_file():
        return f"{name}: output unavailable"
    report = json.loads(path.read_text())
    results = report["Results"] if isinstance(report, dict) else None
    if not isinstance(results, list):
        return f"{name}: scan results unavailable"
    severities = collections.Counter(
        str(finding.get("Severity", "UNKNOWN")) for result in results
        for finding in (result.get("Vulnerabilities") or [])
    )
    packages = sum(len(result["Packages"]) for result in results if isinstance(result.get("Packages"), list))
    return f"{name}: packages={packages}; " + ", ".join(
        f"{key}={severities[key]}" for key in ("CRITICAL", "HIGH", "MEDIUM", "LOW", "UNKNOWN"))


try:
    print(junit_summary())
except Exception as error:  # malformed XML must not hide the other results
    print(f"JUnit: output unavailable ({type(error).__name__})")
for name in ("java-audit.json", "container-audit.json"):
    try:
        print(audit_summary(name))
    except Exception as error:  # malformed or unexpected report shape
        print(f"{name}: scan results unavailable ({type(error).__name__})")
print("Coverage: Gradle lock dependencies and OS packages. Temurin JDK binaries are not audited by this scan.")
