#!/usr/bin/env bash
# Run only in a maintainer's isolated checkout of a dependency-update PR.
# This generates candidate trust metadata for review; CI never calls this script.
set -euo pipefail
cd "$(dirname "$0")/.."
printf '%s\n' 'Generating candidate locks/checksums. Independently review new checksums before accepting.'
./gradlew --no-daemon clean build --write-locks --write-verification-metadata sha256
./gradlew --no-daemon --offline clean build
git diff -- gradle.lockfile gradle/verification-metadata.xml
printf '%s\n' 'Review dependency sources, artifact hashes and audit results; no files were staged or committed.'
