#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
java -version 2>&1 | grep -E 'version "25[.]'
./gradlew --version | grep -Fx 'Gradle 9.8.2'
test "${LANG:-}" = ja_JP.UTF-8
test "${TZ:-}" = Asia/Tokyo
./gradlew --no-daemon clean build
