# Java 開発用 DevContainer

コンテナで即座に Java 開発環境をセットアップ。最小限の設定で素早く開発開始。

> ⚠️ **このブランチ (`feat/clean-architecture-stack`) は写経専用です。`main` にはマージしません。**
> `main` は汎用の Java コンテナを保ち、このブランチだけ Spring Boot + Lombok + H2 + Flyway
> などを追加して『手を動かしてわかるクリーンアーキテクチャ』(Tom Hombergs / buckpal) の
> 写経に使います。スタックの詳細は [クリーンアーキテクチャ学習スタック](#クリーンアーキテクチャ学習スタック) を参照。

## クイックスタート

### 1. コンテナで開く

VS Code で DevContainer を起動:
- 左下の `><` アイコン → `Reopen in Container`
- または Command Palette (`Cmd+Shift+P`) → `Dev Containers: Reopen in Container`

### 2. 環境確認

```bash
java -version    # Java 25 (LTS)
gradle -v        # Gradle 9.6.1
locale           # ja_JP.UTF-8
date             # Asia/Tokyo
```

### 3. ビルド・テスト

```bash
gradle build  # ビルド
gradle test   # テスト実行
```

### 4. アプリを起動

```bash
gradle bootRun   # http://localhost:8080 で起動、Flyway が起動時にスキーマを適用
```

## クリーンアーキテクチャ学習スタック

『手を動かしてわかるクリーンアーキテクチャ』の buckpal を写経するための構成です。

### 含まれるもの

| 分類 | 内容 |
|------|------|
| フレームワーク | Spring Boot 3.5.16 (Web / Data JPA / Validation) |
| 定型コード削減 | Lombok 1.18.46 (Spring Boot BOM 管理) |
| ローカル DB | H2 2.3.x（ファイルモード、`h2-console` から SQL 実行可） |
| DB マイグレーション | Flyway（`src/main/resources/db/migration/V*.sql`） |
| アーキテクチャ検査 | ArchUnit（`ArchitectureTest` が層の依存方向を強制） |
| スタック動作確認 | `SpringContextTest` / `LombokTest`（[動作確認テスト](#スタックの動作確認テスト)） |
| ビルド品質 | Spotless (google-java-format) / Checkstyle / JaCoCo |

### パッケージ構成（ヘキサゴナル）

```
src/main/java/com/example/buckpal/
  BuckpalApplication.java
  account/
    domain/                      … エンティティ / 値オブジェクト（純粋な Java）
    application/
      port/in/                   … 入力ポート（ユースケース IF）
      port/out/                  … 出力ポート（永続化などの抽象）
      service/                   … ユースケース実装
    adapter/
      in/web/                    … REST コントローラ
      out/persistence/           … JPA エンティティ / リポジトリ / マッパー
  common/                        … @UseCase などの共通注釈
```

各層のパッケージには `package-info.java` で責務と依存ルールを記載しています。空の層は
写経で埋めていきます。依存方向を破ると `gradle test`（ArchUnit）が失敗します。

### スタックの動作確認テスト

写経を始める前に「土台が動いているか」を確認できるテストを用意しています。**ここが赤くなったら、
写経したコードではなく環境側（依存関係・設定ファイル・マイグレーション）を疑ってください。**

```
src/test/java/com/example/buckpal/
  SpringContextTest.java   … Spring Boot スタックの動作確認
  LombokTest.java          … Lombok の動作確認
  ArchitectureTest.java    … 層の依存方向の検査（ArchUnit）
src/test/resources/
  application-test.yml     … テスト用のプロファイル設定（インメモリ H2）
```

| テストクラス | 確認していること |
|------|------|
| `SpringContextTest` | `@SpringBootApplication` の起動 / `application.yml` の読み込み / `spring-boot-starter-web` の自動設定（`DispatcherServlet`）/ H2 への接続 / Flyway が V1 を適用済み / `account`・`activity` テーブルの存在 / Lombok が生成したコンストラクタでの DI |
| `LombokTest` | `@Getter` `@Setter` `@RequiredArgsConstructor` / `@Value`（不変・equals・hashCode・toString）/ `@Builder`（`@Builder.Default` 含む）/ `@Slf4j` |
| `ArchitectureTest` | ヘキサゴナルの依存方向（ドメインが Spring / JPA / アダプタに依存していないか等） |

```bash
gradle test                               # 全部（21 テスト）
gradle test --tests '*SpringContextTest'  # Spring まわりだけ
gradle test --tests '*LombokTest'         # Lombok まわりだけ
# 結果レポート: build/reports/tests/test/index.html
```

#### Lombok は「コンパイルが通ること」が第一の検証

Lombok はコンパイル時にコードを生成するため、注釈処理（`annotationProcessor 'org.projectlombok:lombok'`）が
効いていなければ getter やコンストラクタが「存在しない」となり、`LombokTest` は**実行以前にコンパイルで失敗**します。
各テストメソッドはその先の、生成されたコードの**挙動**（equals の結果、builder の既定値など）を確認しています。

> `@Value` は Lombok（`lombok.Value`）と Spring（`org.springframework.beans.factory.annotation.Value`）で
> 名前が衝突します。import を取り違えると意味がまったく変わるので注意してください。

#### テスト用の DB 設定

テストでは `@ActiveProfiles("test")` により `src/test/resources/application-test.yml` が
`src/main/resources/application.yml` の上に重なり（プロファイル固有ファイルが後勝ち）、DB が
インメモリ H2（`jdbc:h2:mem:buckpal-test`）に切り替わります。そのため:

- 開発用のファイル DB `./data/buckpal` をテストが汚さない
- 毎回まっさらなスキーマに対して Flyway が流れる（マイグレーションの検証になる）

### H2 で SQL を試す

アプリ起動後、ブラウザで h2-console を開きます。

1. `gradle bootRun` でアプリを起動
2. ブラウザで <http://localhost:8080/h2-console> を開く
3. 接続情報を入力して Connect:
   - **JDBC URL**: `jdbc:h2:file:./data/buckpal`
   - **User Name**: `sa` / **Password**: （空）
4. `SELECT * FROM account;` や `SELECT * FROM flyway_schema_history;` を実行

> DB はファイルモード（`./data/buckpal`）なので再起動してもデータが残ります。`data/` は
> `.gitignore` 済みです。`AUTO_SERVER=TRUE` によりアプリ起動中でも別接続から SQL を打てます。

### ビルドとレポート

```bash
gradle build            # Spotless / Checkstyle / ArchUnit / JaCoCo を含む
gradle spotlessApply    # 整形を自動修正
gradle test             # テスト（動作確認テスト / ArchUnit 含む）
# カバレッジ: build/reports/jacoco/test/html/index.html
```

## 環境仕様

### 基本設定

| 項目 | 値 |
|------|-----|
| ベースイメージ | Ubuntu 24.04 LTS |
| Java | OpenJDK 25 (LTS) |
| Gradle | 9.6.1 |
| パッケージマネージャー | SDKMAN! |
| ユーザー | vscode (非root) |
| ロケール | ja_JP.UTF-8 |
| タイムゾーン | Asia/Tokyo |

Java 25 は現行の最新 LTS です（次期 LTS は Java 29 / 2027 年予定）。
Gradle は `.devcontainer/Dockerfile`（コンテナ内の SDKMAN）と
`gradle/wrapper/gradle-wrapper.properties`（ラッパー）の両方でバージョンを固定しており、
イメージを再ビルドしても同じ環境が再現されます。更新時は両方を揃えて変更してください。

### 環境変数（自動設定）

```bash
JAVA_HOME=/home/vscode/.sdkman/candidates/java/current
GRADLE_HOME=/home/vscode/.sdkman/candidates/gradle/current
LANG=ja_JP.UTF-8
TZ=Asia/Tokyo
```

### インストール済みツール

- **バージョン管理**: git
- **エディタ**: vim, nano
- **ユーティリティ**: curl, wget, jq, tree, htop, less
- **ビルドツール**: build-essential

### VS Code 拡張機能

- **Extension Pack for Java** (`vscjava.vscode-java-pack`)
  - Language Support for Java (Red Hat)
  - Debugger for Java
  - Test Runner for Java
  - Project Manager for Java
  - Maven for Java
  - Gradle for Java
- **XML** (`redhat.vscode-xml`) - XML / POM / Checkstyle 設定の編集支援
- **EditorConfig** (`EditorConfig.EditorConfig`) - エディタ設定の共通化
- **Markdown All in One** (`yzhang.markdown-all-in-one`)
- **Markdown Lint** (`DavidAnson.vscode-markdownlint`)
- **Code Spell Checker** (`streetsidesoftware.code-spell-checker`)
- **CheckStyle** (`shengchen.vscode-checkstyle`)
- **Git Graph** (`mhutchie.git-graph`)
- **GitLens** (`eamodio.gitlens`) - 行単位の履歴 / blame
- **Error Lens** (`usernamehw.errorlens`) - 診断をコード行に直接表示
- **Todo Tree** (`Gruntfuggly.todo-tree`) - TODO / FIXME の一覧表示
- **YAML** (`redhat.vscode-yaml`) - `application.yml` や CI 設定の補完
- **REST Client** (`humao.rest-client`) - `.http` ファイルから API を叩いて動作確認
- **Live Share** (`MS-vsliveshare.vsliveshare`) - ペアプロ / モブプロ

> Lombok 用の拡張機能は不要です。Language Support for Java (Red Hat) が
> Lombok を標準でサポートします（設定 `java.jdt.ls.lombokSupport.enabled`、既定で有効）。

### Live Share を使う

コンテナ内から共同編集セッションを開始できます。

1. 初回のみサインインが必要です
   Command Palette (`Cmd+Shift+P`) → `Live Share: Sign In` →
   GitHub または Microsoft アカウントでブラウザ認証
2. Command Palette → `Live Share: Start Collaboration Session`
3. クリップボードにコピーされた招待 URL を相手に共有

参加者の接続は都度承認を求める設定 (`liveshare.guestApprovalRequired`) にしてあります。
参加者に Web アプリを見せたい場合は `Live Share: Share Server` でポートを共有してください。

## カスタマイズ

### 拡張機能を追加

#### 方法1: VS Code UI から（簡単）

1. 拡張機能アイコンをクリック
2. 拡張機能を検索
3. `Install in Dev Container` をクリック

#### 方法2: devcontainer.json に追加（永続化）

`.devcontainer/devcontainer.json` の `customizations.vscode.extensions` 配列に追加:

```json
"customizations": {
  "vscode": {
    "extensions": [
      "vscjava.vscode-java-pack",
      // 追加したい拡張機能の ID を記述
      "vmware.vscode-spring-boot"
    ]
  }
}
```

コンテナを再構築:

```bash
# Command Palette (Cmd+Shift+P)
Dev Containers: Rebuild Container
```

### Java バージョンを変更

`.devcontainer/Dockerfile` を編集:

```dockerfile
# Java 21 に変更する例
RUN bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && \
  sdk install java 21-open && \
  sdk install gradle 9.6.1 && \
  sdk default java 21-open"
```

コンテナを再構築後、`build.gradle` も更新:

```gradle
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
```

### Gradle 設定を変更

`gradle.properties` を作成:

```properties
org.gradle.daemon=true
org.gradle.parallel=true
org.gradle.caching=true
```

### 起動時コマンドをカスタマイズ

`.devcontainer/devcontainer.json` を編集:

```json
"postCreateCommand": "bash -c 'sudo chown -R vscode:vscode /home/vscode/.gradle && gradle build'"
```

## トラブルシューティング

### Java 25 + Gradle 互換性エラー

**症状:**

```
Unsupported class file major version 69
```

**原因:** Gradle バージョンが Java 25 に対応していない

**解決方法:**

```bash
# Gradle バージョン確認
gradle --version

# gradle/wrapper/gradle-wrapper.properties を確認
cat gradle/wrapper/gradle-wrapper.properties

# 対応バージョン未満の場合、手動で更新
gradle wrapper --gradle-version 9.6.1

# クリーンビルド
gradle clean build
```

### ロケール/タイムゾーン確認

```bash
locale
date
timedatectl
```

### Gradle キャッシュをクリア

```bash
rm -rf ~/.gradle/caches/
gradle clean build --refresh-dependencies
```

### コンテナを完全に再構築

```bash
# Command Palette (Cmd+Shift+P)
Dev Containers: Rebuild Container

# またはキャッシュなしで再構築
Dev Containers: Rebuild Without Cache
```

### Spring / Lombok のクラスがエディタ上だけ赤くなる

**症状:**

```
SpringBootApplication cannot be resolved to a type Java(16777218)
```

`gradle build` と `gradle test` は成功するのに、VS Code のエディタ上でだけ import が解決できない。

**原因:** `build.gradle` に依存を追加した後、Java 言語サーバー（Red Hat の Language Support for Java）が
プロジェクトを再インポートしておらず、クラスパスが古いまま残っている。

**解決方法:** Command Palette (`Cmd+Shift+P`) から順に試します。

```
Java: Reload Projects                        # まずこれ
Java: Clean Java Language Server Workspace   # 直らなければ（クラスパスを作り直して再起動）
```

`.vscode/settings.json` に以下を設定済みなので、通常は `build.gradle` を保存した時点で
自動的に再インポートされます（既定値の `interactive` では通知を見逃すと取り残されます）。

```json
"java.configuration.updateBuildConfiguration": "automatic"
```

なお、`gradle test` が通るかどうかがビルドの正否です。**エディタの赤線だけで判断しないでください。**

### Java Language Server を再起動

```bash
# Command Palette (Cmd+Shift+P)
Java: Clean Java Language Server Workspace
```

その後、VS Code をリロード。

### Git 設定

初回セットアップ時:

```bash
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"
```

## ライセンス

MIT License - 詳細は [LICENSE](LICENSE) を参照
