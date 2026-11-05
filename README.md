# Java 開発用 DevContainer

VS Code と Dev Containers 拡張を用意し、このディレクトリを `Dev Containers: Reopen in Container` で開きます。初回はネットワーク接続が必要です。

```bash
./gradlew --version
bash scripts/verify-env.sh
./gradlew test
```

Java は Temurin 25 LTS、Gradle は **同梱の Wrapper** を使います。グローバル Gradle や SDKMAN! のインストールは不要です。`JAVA_HOME=/opt/java/openjdk`、非 root ユーザー `vscode`、ロケール `ja_JP.UTF-8`、タイムゾーン `Asia/Tokyo` です。

## 構成と更新方針

| 対象 | 変更前 | 採用版 | 理由 |
| --- | --- | --- | --- |
| ベース OS | Ubuntu 24.04 | Ubuntu 24.04 noble | LTS の安定性を維持、標準セキュリティ保守は 2029年5月まで |
| JDK | SDKMAN! `25-open` | Eclipse Temurin 25.0.5+8 LTS | 配布元・パッチを明示した公式イメージ。25 の toolchain を維持 |
| Gradle | 9.6.1、Wrapper properties のみ | 9.8.2、完全な Wrapper | 公式 current stable、JDK 25 実行対応、配布 ZIP の SHA-256 検証 |
| Commons Lang | 3.20.0 | 3.21.0 | Maven Central の最新 release |
| JUnit Jupiter/Platform | 6.1.2 | 6.1.3 | Maven Central の最新 release |
| Mockito Core/JUnit integration/Java agent | 5.23.0 | 5.24.0 | 追加された Stub/Mock/Spy テストを維持し Maven Central の最新 release に統一 |

調査日は **2026-11-05**。JDK は Java 26/27 などの短期リリースより既存の LTS 方針を優先します。パッチは定期更新し、次の LTS 移行は別途互換性を検証します。コンテナ OS の修正は `apt-get upgrade` と `docker build --pull` で取り込みます。タグや OS パッケージの内容は更新されるため、ビット単位の再現性ではありません。

- [Temurin 25 公式リリース](https://github.com/adoptium/temurin25-binaries/releases)
- [Adoptium 現行 JDK メタデータ](https://api.adoptium.net/v3/assets/latest/25/hotspot?architecture=x64&image_type=jdk&os=linux)
- [Gradle current stable](https://services.gradle.org/versions/current)、[Java 互換表](https://docs.gradle.org/current/userguide/compatibility.html)
- [Commons Lang リリース情報](https://repo.maven.apache.org/maven2/org/apache/commons/commons-lang3/maven-metadata.xml)
- [Mockito リリース情報](https://repo.maven.apache.org/maven2/org/mockito/mockito-core/maven-metadata.xml)
- [JUnit リリース情報](https://repo.maven.apache.org/maven2/org/junit/jupiter/junit-jupiter/maven-metadata.xml)
- [Ubuntu リリースとサポート期間](https://ubuntu.com/about/release-cycle)

追加された Mockito の Stub/Mock/Spy テストはそのまま保持しています。JDK 25 の動的自己アタッチに依存しないよう、`mockitoAgent` configuration から同じ版の Mockito を `-javaagent` で明示的に接続します。

`gradle.lockfile` で `mockitoAgent` を含む推移的依存も固定し、`gradle/verification-metadata.xml` で取得ファイルのチェックサムを検証します。更新時は以下を実行し、公式情報と差分を照合してください。生成したチェックサムは初回取得内容に対する固定であり、提供元の正当性確認に代わるものではありません。

```bash
./gradlew wrapper --gradle-version NEW_VERSION --distribution-type bin --gradle-distribution-sha256-sum OFFICIAL_SHA256
./gradlew wrapper
./gradlew clean build --write-locks --write-verification-metadata sha256
```

Wrapper JAR の更新では公式 `https://services.gradle.org/distributions/gradle-VERSION-wrapper.jar.sha256` と CI の検証値も合わせます。JDK 更新では Dockerfile と CI の `java-version` を合わせます。

## VS Code 拡張

Dev Container の自動導入と `.vscode/extensions.json` の推奨を一致させています。

- **Extension Pack for Java** (`vscjava.vscode-java-pack`): Microsoft の Java ガイドが推奨する構成。Red Hat Java Language Support、Java Debugger、Test Runner、Project Manager、Maven/Gradle サポートを含みます。パックと個別拡張の重複指定は避けます。
- **EditorConfig** (`EditorConfig.EditorConfig`): エディター規約を共有するための補助拡張。

「デファクトスタンダード」は公式規格ではありません。Java Pack は Microsoft の公式ガイドと現行パック定義を根拠に採用しています。2026-11-05 に Marketplace の公開・検証済み提供元を確認しました（最終更新 2026-10-14）。[Java ガイド](https://code.visualstudio.com/docs/java/java-tutorial)、[パック定義](https://github.com/microsoft/vscode-java-pack/blob/main/package.json)、[Marketplace](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)。

`java.home` を除去し、`java.jdt.ls.java.home`、JavaSE-25 runtime、Gradle 実行 JDK を同じパスに統一しました。整形は `redhat.java` に統一します。Checkstyle はビルドにルールがないため自動導入しません。GitLens、Live Share、Markdown/YAML 支援などは必要な利用者が任意に導入できます。

手動確認: Java プロジェクトの取り込み、補完・定義ジャンプ、ブレークポイント実行、Testing ビューの JUnit 実行、保存時整形を確認してください。GUI 動作は CLI テストでは確認できません。

## セキュリティと CI

GitHub Actions は push/PR/毎週月曜/手動でビルド、Wrapper JAR の検証、コンテナ環境確認、Trivy の言語依存/OS/JDK スキャンを実行します。HIGH/CRITICAL を検出した場合は失敗させ、未修正版を無条件に除外しません。Dependabot は Gradle/Docker/Actions を毎週確認します。Actions はコミット SHA を固定しています。専用の Dev Container Features はありません。

```bash
./gradlew clean build
trivy fs --include-dev-deps --scanners vuln --severity HIGH,CRITICAL --exit-code 1 .
docker build --pull -t devcontainer-java:audit .devcontainer
trivy image --scanners vuln --severity HIGH,CRITICAL --exit-code 1 devcontainer-java:audit
```

監査結果と実行環境の制約は [検証記録](docs/maintenance-validation.md) を参照してください。脆弱性が残る場合は CVE/GHSA、影響、修正版、回避策、担当、期限を追跡します。未公開の機密情報は公開 Issue に記載しません。

MIT License: [LICENSE](LICENSE)
