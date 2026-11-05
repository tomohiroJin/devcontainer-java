"""Print measured CI counts; missing outputs are explicitly reported."""
import collections
import json
from pathlib import Path
import xml.etree.ElementTree as ET

reports = list(Path("build/test-results/test").glob("TEST-*.xml"))
if reports:
    totals = collections.Counter()
    for report in reports:
        suite = ET.parse(report).getroot()
        for key in ("tests", "failures", "errors", "skipped"):
            totals[key] += int(suite.attrib[key])
    print("JUnit: " + ", ".join(f"{key}={totals[key]}" for key in ("tests", "failures", "errors", "skipped")))
else:
    print("JUnit: output unavailable")
for name in ("java-audit.json", "container-audit.json"):
    path = Path(name)
    if not path.exists():
        print(f"{name}: output unavailable")
        continue
    report = json.loads(path.read_text())
    if not isinstance(report.get("Results"), list):
        print(f"{name}: scan results unavailable")
        continue
    results = report["Results"]
    severities = collections.Counter(
        finding["Severity"] for result in results
        for finding in result.get("Vulnerabilities") or []
    )
    packages = sum(len(result.get("Packages") or []) for result in results)
    print(f"{name}: packages={packages}; " + ", ".join(
        f"{key}={severities[key]}" for key in ("CRITICAL", "HIGH", "MEDIUM", "LOW", "UNKNOWN")))
print("Coverage: Gradle lock dependencies and OS packages. Temurin JDK binaries are not audited by this scan.")
