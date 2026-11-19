# Issue #7 検証記録

## 2026-11-19 再検証

初回検証（2026-11-05、下記）から 2 週間経過し、残存検出項目の再確認期限 2026-11-12 を過ぎていたため、公式情報を再照合して更新・再監査しました。対象は PR #8 のブランチ `35da542f` に本更新を加えたものです。

- バージョン再照合: Gradle current は **9.9.1**（2026-11-10 公開、配布 ZIP SHA-256 `d8a18a7a841dd5df825d35a685fad6f2d7f12ac0c280e57b67c27b4a520ba228`、Wrapper JAR SHA-256 `621008afc4f5d59357fcf06ffa644dd5aade5e091bdc363742710445f73cdaf8`。いずれも services.gradle.org の versions API と、実ダウンロードの `sha256sum`/再生成 JAR で一致）。Commons Lang は **3.22.0**（2026-11-11 公開）。JUnit は 6.2.0-M1 がマイルストーンのため 6.1.3 を維持。Mockito は 5.24.0 のまま最新。Temurin 25 の最新 GA は引き続き 25.0.5+8。
- Temurin のタグ `25.0.5_8-jdk-noble` は 2026-11-19 に再ビルドされ、manifest digest が `sha256:95e350…` から **`sha256:8599ea2d8f71696f00dcdf46213a4b65fa97ceeaa318671fccba6b0af2727792`**（linux/amd64 manifest `sha256:16b53b71df609d3ce064b73f1d5cd869f34563dacbdfc06f038a6634385e1ab1`）に変わりました。新イメージの `/opt/java/openjdk/release` は `Temurin-25.0.5+8`、OS は Ubuntu 24.04.5 であることを layer 展開で確認し、Dockerfile の digest を更新しました。
- Wrapper は README の手順どおり 9.8.2 で 1 回、9.9.1 で 1 回 `wrapper` タスクを実行して再生成。`gradlew`/`gradlew.bat` に差分はなく、properties と JAR のみ更新。CI の JAR 検証値と `verify-env.sh` の期待バージョンを同期。
- Commons Lang 3.22.0 の jar/pom と新しい親 POM（commons-parent 107、apache 40）のチェックサムを、Gradle とは別に curl で Maven Central から直接取得した artifact の `sha256sum`（jar は Central 公開の `.sha1` とも一致）と照合。不要になった 3.21.0・commons-parent 105・apache 39 の項目は `verification-metadata.xml` から削除し、空の `GRADLE_USER_HOME` からのオンライン build（検証有効）と `--offline clean build` の両方が成功。JUnit 7 tests / 0 failures / 0 errors / 0 skipped。
- Trivy 0.76.2、2026-11-19 13:02 UTC 更新の DB。`trivy fs --include-dev-deps`: `gradle.lockfile` の 17 依存、検出 0。
- OS パッケージ監査: この管理環境には Docker デーモンがないため、新 digest のイメージを layer 展開し、Dockerfile と同じ `apt-get update && apt-get upgrade -y && apt-get install ...` を chroot で適用した rootfs を `trivy rootfs` で検査。143 パッケージ、CRITICAL 0 / HIGH 0 / MEDIUM 87 / LOW 37、修正版が DB に記載された残件 0（ベースイメージ時点で修正版のあった libc6/libc-bin/locales/perl-base の MEDIUM は upgrade で解消）。`docker build` による正式な数値は CI の Job Summary と artifact を正とし、差があればこの記録を更新します。
- GitHub Actions のピン: setup-java v6.2.0 → **v6.3.0**（`d1374f8ac74474200d16ef8ecee22c7ae7a51880`）、setup-trivy v0.2.6 → **v0.3.1**（`81e514348e19b6112ce2a7e3ecbafe19c1e1f567`）。checkout v7.0.1 と upload-artifact v7.0.2 は最新のまま。タグと SHA は `git ls-remote --tags` で照合。
- 2 回目の検証コメントの軽微指摘: concurrency グループをイベント種別で分離し PR のみ cancel-in-progress、`zip`/`less` を削除リストに追記、Job Summary をコードブロック化、`refresh-dependencies.sh` に実行ビットを付与、意図的ダウングレード時の手順を README に明記。
- 未実施: `docker build`/`docker run` の実行、Dev Containers クライアントの起動、VS Code GUI。VS Code Marketplace はこの管理環境から到達できないため再確認しておらず、README の 2026-11-05 の確認結果のままです。

以下は 2026-11-05 の初回検証記録です。

実行日: 2026-11-05。対象: `db3f25b5b5245624d8a79d059ae74dce613a852f`（Mockito テスト追加 PR #6 を含む最新 origin/main）と開発環境更新を統合した本ブランチ。追加されたテストと明示的 Java agent 設定を保持し、統合後に再検証。

## 確認済み

- Adoptium API の現行 Java 25 LTS は Temurin 25.0.5+8、Gradle current API は 9.8.2。Maven Central の release メタデータから Commons Lang 3.21.0 / JUnit 6.1.3 / Mockito 5.24.0 を確認。
- Gradle Wrapper JAR SHA-256 `3b8a25775a69158b5ad2b1d17a80a88dc7b40a352a73eb27d06c612a7ce68e98` を公式 API の wrapperChecksum と照合。
- Gradle 配布 ZIP の公式 SHA-256 `21bd5da2de49b642904077f38c0efdc7778e92b3ed0889392dca61b97e389607` を Wrapper に設定。
- Temurin 25.0.5+8 / Gradle 9.8.2 で `clean build --write-locks --write-verification-metadata sha256` 成功。JUnit: 7 tests（HelloWorld 1件、Mockito Stub/Mock/Spy 6件）、0 failures/errors。メタデータ生成後、通常のロック・チェックサム検証を有効にした `--offline clean build` も成功。
- `docker build -t devcontainer-java:issue-7 .devcontainer` 成功。ベースの manifest digest は `sha256:95e350ad1eb738f943e141640f7e3d797e483359a3b33c3864255bfd1ad71f8a`。生成イメージ ID は `sha256:7cdc8d431e6ab6d151022148000a6026ba6977c3115dc6e52ef0ee0dbc659b4b`。
- 完成イメージ内で `bash scripts/verify-env.sh` 成功。Java 25.0.5、Gradle 9.8.2、ja_JP.UTF-8、Asia/Tokyo の検証と Wrapper による `clean build` がすべて成功（JUnit 7件）。一時 Gradle キャッシュへの配布取得から開始し、ZIP チェックサムと依存チェックサムも検証。
- Trivy **0.76.2**、2026-11-05 取得の DB、`trivy fs --include-dev-deps --scanners vuln --format json ... .`: `gradle.lockfile` の17依存（Mockito と Byte Buddy/Objenesis を含む）、検出0件。
- 同ツールで `trivy image --scanners vuln --format json ... devcontainer-java:issue-7`: Ubuntu 24.04 パッケージ、CRITICAL 0 / HIGH 0 / MEDIUM 76 / LOW 20。修正版が DB に記載された残件は0件。言語依存はコンテナに同梱せずビルド時に解決するため、上記 lockfile 監査で別途カバー。Temurin JDK バイナリ本体はこの結果の対象ではなく、JDK の脆弱性なしを意味しません。
- `mockitoAgent` を含む全 configuration をロックし、agent JAR と追加依存の SHA-256 を記録。Gradle の解決戦略変更エラーを防ぐため、ロックの有効化を agent configuration の解決より先に実行。
- Bash 構文、JSON/YAML 読み込み、`git diff --check` 成功。

## 残件の追跡

MEDIUM/LOW のパッケージ別 CVE・導入版・状態は [security-findings.tsv](security-findings.tsv) に記録。現行の Ubuntu 更新をすべて適用済みで、DB に修正版の記載はありません。開発コンテナを外部公開サーバーとして使用せず、信頼できないコードは実行しない運用とし、既存 [Issue #7](https://github.com/tomohiroJin/devcontainer-java/issues/7) で追跡します。担当は Repository メンテナー、次回確認期限は **2026-12-03**（2026-11-19 の再検証で更新）。週次 CI で再検査し、修正版公開時に再ビルド・再監査してください。脆弱性が存在しないとは断定しません。

## 環境上の条件

この管理環境では Java は HTTPS_PROXY を自動使用しないため、検証コマンドにのみ Java のプロキシシステムプロパティを渡し、セッション CA を一時 trust store に取り込みました。コンテナ実行時は Docker が設定した到達可能なプロキシと read-only trust store mount を使用。資格情報・プロキシ設定・セッション証明書は Repository やイメージに保存していません。

GitHub Actions の実行と VS Code GUI（補完、デバッグ、Testing ビュー、保存時整形）はこの CLI セッションでは未確認です。README の手動確認手順を用いて確認してください。環境の公開、リモート push、Issue のクローズは行っていません。

## GitHub Actions の UID 差異への対応

CI の完成コンテナ検証では、runner の UID/GID とコンテナ既定ユーザーの UID/GID が異なり、Gradle がマウントしたプロジェクトルートへの書き込みを拒否しました。CI の `docker run` をホストと同じ UID/GID で実行し、書き込み可能な一時 `HOME` / `GRADLE_USER_HOME` を指定します。プロジェクトの一部だけを全ユーザー書き込み可能にする処理は除去しました。

UID/GID 1001:1001 所有の一時チェックアウトを用意し、同じ UID/GID のコンテナでワークフローと同じ環境・コマンドを検証しました。セッション CA/proxy は従来同様テストにのみ注入しています。

結果: `scripts/verify-env.sh` 成功、JUnit 7件すべて成功。actionlint と差分チェックも成功。
