#!/usr/bin/env bash
#
# DevContainer 環境の想定値を検証するスクリプト。
# ローカル（Rebuild Container 後）でも GitHub Actions（devcontainers/ci）でも
# 同じものを実行し、検証ロジックを一箇所に集約する。
#
# 使い方:
#   bash scripts/verify-env.sh
#
set -o pipefail

# ------------------------------------------------------------
# 期待値（Dockerfile / gradle-wrapper.properties と揃えること）
# ------------------------------------------------------------
EXPECTED_JAVA_MAJOR="25"
EXPECTED_GRADLE="9.6.1"
EXPECTED_LANG="ja_JP.UTF-8"
EXPECTED_TZ="Asia/Tokyo"

# SDKMAN で入れた java / gradle を確実に PATH に載せる
if [ -s "$HOME/.sdkman/bin/sdkman-init.sh" ]; then
  # shellcheck disable=SC1091
  source "$HOME/.sdkman/bin/sdkman-init.sh"
fi

fail=0

# assert LABEL EXPECTED_SUBSTRING ACTUAL
assert() {
  local label="$1" expected="$2" actual="$3"
  if printf '%s' "$actual" | grep -qF "$expected"; then
    printf '  \033[32m✅ %-16s\033[0m %s\n' "$label" "$actual"
  else
    printf '  \033[31m❌ %-16s\033[0m expected to contain "%s" but got: %s\n' "$label" "$expected" "$actual"
    fail=1
  fi
}

echo "=== 1. Java ==="
assert "Java major" "\"${EXPECTED_JAVA_MAJOR}" "$(java -version 2>&1 | head -1)"

echo "=== 2. Gradle (SDKMAN) ==="
assert "gradle" "Gradle ${EXPECTED_GRADLE}" "$(gradle --version 2>&1 | grep -E '^Gradle ')"

echo "=== 3. Gradle Wrapper ==="
assert "gradlew" "Gradle ${EXPECTED_GRADLE}" "$(./gradlew --version 2>&1 | grep -E '^Gradle ')"

echo "=== 4. Locale ==="
assert "LANG" "$EXPECTED_LANG" "${LANG:-unset}"

echo "=== 5. Timezone ==="
assert "TZ" "$EXPECTED_TZ" "${TZ:-unset}"

echo "=== 6. Build & Test ==="
if ./gradlew clean build test --console=plain > /tmp/gradle-build.log 2>&1; then
  printf '  \033[32m✅ %-16s\033[0m gradlew clean build test\n' "BUILD"
else
  printf '  \033[31m❌ %-16s\033[0m gradlew clean build test failed\n' "BUILD"
  tail -30 /tmp/gradle-build.log
  fail=1
fi

echo
if [ "$fail" -eq 0 ]; then
  echo "🎉 すべての検証項目に合格しました。"
else
  echo "💥 検証に失敗した項目があります（上記 ❌ を確認してください）。"
fi
exit "$fail"
