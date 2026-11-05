# Issue #7 検証記録

実行日: 2026-11-05。対象: `b416edd242b127e8dcbde1d9bb970b3b57d95eb4` に対する本作業ツリー。開始時のローカル HEAD は origin HEAD と一致。

## 確認済み

- Adoptium API の現行 Java 25 LTS は Temurin 25.0.5+8、Gradle current API は 9.8.2。Maven Central の release メタデータから Commons Lang 3.21.0 / JUnit 6.1.3 を確認。
- Gradle Wrapper JAR SHA-256 `3b8a25775a69158b5ad2b1d17a80a88dc7b40a352a73eb27d06c612a7ce68e98` を公式 API の wrapperChecksum と照合。
- Gradle 配布 ZIP の公式 SHA-256 `21bd5da2de49b642904077f38c0efdc7778e92b3ed0889392dca61b97e389607` を Wrapper に設定。
- Temurin 25.0.5+8 / Gradle 9.8.2 で `clean build --write-locks --write-verification-metadata sha256` 成功。JUnit: 1 test、0 failures/errors。
- `docker build -t devcontainer-java:issue-7 .devcontainer` 成功。ベースの manifest digest は `sha256:95e350ad1eb738f943e141640f7e3d797e483359a3b33c3864255bfd1ad71f8a`。生成イメージ ID は `sha256:7cdc8d431e6ab6d151022148000a6026ba6977c3115dc6e52ef0ee0dbc659b4b`。
- 完成イメージ内で `bash scripts/verify-env.sh` 成功。Java 25.0.5、Gradle 9.8.2、ja_JP.UTF-8、Asia/Tokyo の検証と Wrapper による `clean build` がすべて成功（JUnit 1件）。一時 Gradle キャッシュへの配布取得から開始し、ZIP チェックサムと依存チェックサムも検証。
- Trivy **0.76.2**、2026-11-05 取得の DB、`trivy fs --include-dev-deps --scanners vuln --format json ... .`: `gradle.lockfile` の12依存、検出0件。
- 同ツールで `trivy image --scanners vuln --format json ... devcontainer-java:issue-7`: Ubuntu 24.04 パッケージ、CRITICAL 0 / HIGH 0 / MEDIUM 76 / LOW 20。修正版が DB に記載された残件は0件。言語依存はコンテナに同梱せずビルド時に解決するため、上記 lockfile 監査で別途カバー。
- Bash 構文、JSON/YAML 読み込み、`git diff --check` 成功。

## 残件の追跡

MEDIUM/LOW のパッケージ別 CVE・導入版・状態は [security-findings.tsv](security-findings.tsv) に記録。現行の Ubuntu 更新をすべて適用済みで、DB に修正版の記載はありません。開発コンテナを外部公開サーバーとして使用せず、信頼できないコードは実行しない運用とし、既存 [Issue #7](https://github.com/tomohiroJin/devcontainer-java/issues/7) で追跡します。担当は Repository メンテナー、次回確認期限は **2026-11-12**。週次 CI で再検査し、修正版公開時に再ビルド・再監査してください。脆弱性が存在しないとは断定しません。

## 環境上の条件

この管理環境では Java は HTTPS_PROXY を自動使用しないため、検証コマンドにのみ Java のプロキシシステムプロパティを渡し、セッション CA を一時 trust store に取り込みました。コンテナ実行時は Docker が設定した到達可能なプロキシと read-only trust store mount を使用。資格情報・プロキシ設定・セッション証明書は Repository やイメージに保存していません。

GitHub Actions の実行と VS Code GUI（補完、デバッグ、Testing ビュー、保存時整形）はこの CLI セッションでは未確認です。README の手動確認手順を用いて確認してください。環境の公開、リモート push、Issue のクローズは行っていません。
