# devcontainer-java

このリポジトリは、Visual Studio CodeのDev Containers機能を活用してJavaアプリケーション開発環境を簡単に構築するためのテンプレートです。開発環境のセットアップを自動化し、効率的な開発を支援します。

## 特徴

- **Java 開発キット (JDK)**: 最新のOpenJDKを使用したJava開発環境を提供します。
- **ビルドツール**: Gradleが事前にインストールされており、プロジェクトのビルドや依存関係管理が簡単に行えます。
- **VS Code 拡張機能**: Java開発に必要な拡張機能（コード補完やデバッグツール）が事前に設定されています。

## 開発環境の詳細

この devcontainer は以下の環境を提供します:

- **OS**: Ubuntu 24.04 LTS
- **Java**: OpenJDK 25 (SDKMAN! 経由)
- **Gradle**: 最新版 (SDKMAN! 経由、8.11.1以降)
- **実行ユーザー**: 非rootユーザー `vscode` (UID 1000)
- **タイムゾーン**: Asia/Tokyo (JST)
- **ロケール**: ja_JP.UTF-8 (日本語 UTF-8)
- **開発ツール**: vim, nano, less, wget, curl, jq, tree, htop など

## VS Code 拡張機能

この devcontainer には、Java 開発に必要な拡張機能が事前設定されています。

### プリインストール済み拡張機能

#### Java 開発 (必須) - 5個
- **Language Support for Java** (Red Hat) - IntelliSense、リファクタリング、コードナビゲーション
- **Debugger for Java** - VS Code 内で Java アプリケーションをデバッグ
- **Test Runner for Java** - JUnit テストの実行とデバッグ
- **Java Dependency Viewer** - プロジェクトの依存関係を表示・管理
- **Gradle for Java** - Gradle ビルドツールとの連携

#### ドキュメント (必須) - 2個
- **Markdown All in One** - README やドキュメントファイルの編集
- **Markdown Lint** - Markdown の品質基準を適用

#### コード品質 (必須) - 1個
- **Code Spell Checker** - コード、コメント、文字列内のタイポを検出

#### 追加ツール (推奨) - 2個
- **CheckStyle** - Java コードスタイル基準を適用
- **Git Graph** - Git 履歴とブランチを可視化

### オプション拡張機能の手動インストール

以下の拡張機能は、必要に応じて個別にインストールできます。

#### 方法1: VS Code UI から拡張機能をインストール

1. **コンテナ内で VS Code を開いた状態で**、左側の拡張機能アイコンをクリック
2. 検索バーに拡張機能名を入力 (例: "Spring Boot")
3. 該当する拡張機能を見つけて「Install」ボタンをクリック
4. インストール完了後、VS Code をリロード (必要に応じて)

#### 方法2: devcontainer.json に追加して永続化

拡張機能をプロジェクトに永続的に追加する場合:

1. `.devcontainer/devcontainer.json` をエディタで開く
2. `"extensions"` 配列に拡張機能 ID を追加:

```json
"extensions": [
  // ... 既存の拡張機能 ...

  // 追加したい拡張機能
  "vmware.vscode-spring-boot",
  "gabrielbb.vscode-lombok"
]
```

3. ファイルを保存
4. コンテナを再ビルド:
   - **Command Palette** を開く: `Cmd+Shift+P` (Mac) / `Ctrl+Shift+P` (Windows/Linux)
   - `Dev Containers: Rebuild Container` を選択
   - コンテナが再起動するまで待つ (数分かかる場合があります)

#### フレームワークサポート (必要時にインストール)

**Spring Boot 拡張機能** - Spring Boot プロジェクト用
```
拡張機能 ID:
vmware.vscode-spring-boot
vscjava.vscode-spring-boot-dashboard
```

**Lombok サポート** - Lombok アノテーションを使用するプロジェクト用
```
拡張機能 ID:
gabrielbb.vscode-lombok
```

**Maven for Java** - Maven ベースのプロジェクト用 (このプロジェクトは Gradle を使用)
```
拡張機能 ID:
vscjava.vscode-maven
```

#### コラボレーション・ユーティリティ

**Live Share** - リアルタイム共同編集
```
拡張機能 ID:
ms-vsliveshare.vsliveshare
```

**Change Case** - テキストケース変換 (camelCase, snake_case など)
```
拡張機能 ID:
wmaurer.change-case
```

**Markdown Preview Enhanced** - 図表付き高度な Markdown プレビュー
```
拡張機能 ID:
shd101wyy.markdown-preview-enhanced
```

**Excel Viewer** - CSV や Excel ファイルの表示
```
拡張機能 ID:
GrapeCity.gc-excelviewer
```

### 拡張機能の方針

この devcontainer は **最小限で焦点を絞った** アプローチに従っています:
- **必須の拡張機能のみをプリインストール** - Java 開発に直接必要なツールのみ
- **フレームワーク固有の拡張機能はオプション** - Spring Boot や Lombok は使用時に追加
- **ユーザーの好みに依存するツールは個別に追加可能** - Live Share や Excel viewer など
- **コンテナを軽量に保ち、起動時間を高速化** - 20個 → 10個に削減

### 初回セットアップ

コンテナを初めて開いた後:

1. **Git 設定** (必要に応じて):
   ```bash
   git config --global user.name "Your Name"
   git config --global user.email "your.email@example.com"
   ```

2. **環境確認**:
   ```bash
   java -version    # Java 25 が表示される
   gradle --version # Gradle 8.11.1+ が表示される
   date            # JST タイムゾーンが表示される
   locale          # ja_JP.UTF-8 が表示される
   ```

3. **プロジェクトのビルド**:
   ```bash
   ./gradlew clean build
   ```

4. **テストの実行**:
   ```bash
   ./gradlew test
   ```

## はじめに

以下の手順で、この開発環境を利用できます。

1. **リポジトリをクローンする**:

   ```bash
   git clone https://github.com/tomohiroJin/devcontainer-java.git
   ```

   - プロジェクトディレクトリに移動します:

   ```bash
   cd devcontainer-java
   ```

2. **VS Codeで開く**:
   - Visual Studio Codeに[Dev Containers拡張機能](https://code.visualstudio.com/docs/devcontainers/containers)をインストールしてください。
   - このリポジトリをVS Codeで開きます。
   - 「コンテナで再オープン」を選択すると、必要な開発環境が自動的にセットアップされます。

3. **プロジェクトのビルドと実行**:
   - ターミナルでGradleを使用してビルドを実行します。

     ```bash
     # Gradleを使用する場合
     gradlew build
     ```

   - Javaアプリケーションを実行する:

     ```bash
     java -jar build/libs/your-app.jar
     ```

## 既知の問題と互換性

### Java 25 と Gradle の互換性

このプロジェクトは **Java 25** を使用しており、クラスファイル形式の互換性 (major version 69) のため **Gradle 9.2.1 以降** が必要です。

#### 問題の症状

Gradle 9.2.1 より前のバージョンでは以下のようなエラーが発生します:

```
BUG! exception in phase 'semantic analysis' in source unit '_BuildScript_'
Unsupported class file major version 69
```

または

```
class file major version 69 is not supported
```

#### 解決方法

##### 1. 現在の Gradle バージョンを確認

コンテナ内のターミナルで以下のコマンドを実行:

```bash
./gradlew --version
```

**期待される出力:**
```
Gradle 9.2.1
```

9.2.1 以降が表示されれば問題ありません。

##### 2. Gradle Wrapper のバージョンを確認

`gradle/wrapper/gradle-wrapper.properties` ファイルを開いて確認:

```bash
cat gradle/wrapper/gradle-wrapper.properties
```

以下の行を確認:
```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-9.2.1-bin.zip
```

##### 3. Gradle Wrapper を手動で更新する方法

もし古いバージョンの Gradle を使用している場合、以下の手順で更新:

```bash
# Gradle 9.2.1 に更新
./gradlew wrapper --gradle-version 9.2.1

# 更新を確認
./gradlew --version
```

##### 4. 完全クリーンビルドを実行

Gradle キャッシュをクリアしてから再ビルド:

```bash
# キャッシュをクリア
rm -rf ~/.gradle/caches/
rm -rf .gradle/

# クリーンビルド
./gradlew clean build --refresh-dependencies
```

### VS Code 拡張機能の互換性

#### SonarLint の削除について

この devcontainer からは **SonarLint 拡張機能が削除されています**。

**理由:** コンテナ環境での Node.js 依存関係の競合によるものです。SonarLint は JS/TS/CSS/JSON の解析に Node.js (>= 20.12.0) を必要としますが、この純粋な Java プロジェクトでは Node.js をインストールしていません。

**代替のコード品質ツール:**

1. **CheckStyle** (含まれています) - Java スタイルチェック
   - 設定ファイル: プロジェクトルートに `checkstyle.xml` を配置
   - 使い方: ファイルを開くと自動的に問題が表示されます

2. **Error Prone** (Gradle に追加可能) - 静的解析

   `build.gradle` に追加:
   ```gradle
   plugins {
       id 'net.ltgt.errorprone' version '3.1.0'
   }

   dependencies {
       errorprone 'com.google.errorprone:error_prone_core:2.23.0'
   }
   ```

3. **SpotBugs** (Gradle に追加可能) - バグ検出

   `build.gradle` に追加:
   ```gradle
   plugins {
       id 'com.github.spotbugs' version '6.0.4'
   }
   ```

### 拡張機能インストール時の注意

#### Oracle Java Extension について

- **競合の可能性**: Red Hat Java 拡張と機能が重複します
- **推奨**: 両方同時にインストールしないこと
- **現在の構成**: Red Hat Java 拡張のみを使用 (より広く利用されている)

#### Live Share について

- **要件**: Microsoft アカウント認証が必要です
- **初回使用時**: サインインを求められます
- **オフライン**: インターネット接続が必要です

#### Spring Boot Extensions について

依存関係に Spring Boot がない場合、警告が表示されることがあります:

```
Spring Boot Dashboard: No Spring Boot project detected
```

**対処法**: 使用していない場合は無視しても安全です。または、拡張機能をアンインストールしてください。

### コンテナ再ビルドの方法

設定ファイルを変更した後は、コンテナを再ビルドする必要があります:

#### 方法1: Command Palette から (推奨)

1. `Cmd+Shift+P` (Mac) / `Ctrl+Shift+P` (Windows/Linux) で Command Palette を開く
2. `Dev Containers: Rebuild Container` を入力して選択
3. コンテナが再ビルドされるまで待つ (初回は数分かかります)

#### 方法2: Docker コマンドで完全再ビルド

キャッシュを含めて完全に再ビルドする場合:

```bash
# VS Code を閉じた状態で、プロジェクトルートで実行
docker compose -f .devcontainer/docker-compose.yml down
docker compose -f .devcontainer/docker-compose.yml build --no-cache
```

その後、VS Code でプロジェクトを開き「コンテナで再オープン」を選択。

### トラブルシューティング

#### 問題: 拡張機能が動作しない

**解決策:**
1. VS Code をリロード: `Cmd+R` (Mac) / `Ctrl+R` (Windows/Linux)
2. コンテナを再起動: `Dev Containers: Rebuild Container`
3. 拡張機能を再インストール

#### 問題: Gradle タスクが表示されない

**解決策:**
```bash
# Java Language Server をリロード
# Command Palette (Cmd+Shift+P) で:
Java: Clean Java Language Server Workspace

# その後、VS Code をリロード
```

#### 問題: ビルドが遅い

**解決策:**
```bash
# Gradle デーモンを使用 (通常は自動的に有効)
./gradlew build --daemon

# 並列ビルドを有効化
./gradlew build --parallel
```

## カスタマイズ

`.devcontainer/` ディレクトリ内のファイルを編集することで、開発環境をカスタマイズできます。

### Dockerfile のカスタマイズ

- **Java バージョンの変更**: `sdk install java <version>` の部分を変更
- **追加パッケージのインストール**: `apt-get install` セクションにパッケージを追加
- **タイムゾーンの変更**: `ENV TZ=` の値を変更 (例: `Asia/Seoul`, `America/New_York`)
- **ロケールの変更**: locale.gen と環境変数 (LANG, LC_ALL) を変更

### devcontainer.json のカスタマイズ

#### VS Code 拡張機能の追加/削除

**手順:**

1. `.devcontainer/devcontainer.json` をエディタで開く
2. `"extensions"` 配列を編集

**注意事項:**
- **Essential extensions** は、コア機能のために残しておく必要があります
- **フレームワーク拡張** (Spring Boot, Lombok) は、必要に応じて追加できます
- **ユーザーの好みに依存する拡張機能** は、VS Code から個別にインストールすることも可能

**編集例:**

```json
"extensions": [
  // ========================================
  // Essential: Java Development
  // ========================================
  "redhat.java",
  "vscjava.vscode-java-debug",
  "vscjava.vscode-java-test",
  "vscjava.vscode-java-dependency",
  "vscjava.vscode-gradle",

  // ========================================
  // Essential: Documentation
  // ========================================
  "yzhang.markdown-all-in-one",
  "DavidAnson.vscode-markdownlint",

  // ========================================
  // Essential: Code Quality
  // ========================================
  "streetsidesoftware.code-spell-checker",

  // ========================================
  // Recommended: Additional Tools
  // ========================================
  "shengchen.vscode-checkstyle",
  "mhutchie.git-graph",

  // ========================================
  // Optional: Add your extensions here
  // ========================================
  "vmware.vscode-spring-boot",          // Spring Boot を使う場合
  "gabrielbb.vscode-lombok"             // Lombok を使う場合
]
```

3. ファイルを保存
4. コンテナを再ビルド: `Dev Containers: Rebuild Container`

#### よく使う拡張機能の追加例

**Spring Boot プロジェクトに変換する場合:**

`.devcontainer/devcontainer.json` に追加:

```json
"extensions": [
  // ... 既存の拡張機能 ...

  // Spring Boot サポート
  "vmware.vscode-spring-boot",
  "vscjava.vscode-spring-boot-dashboard"
]
```

再ビルド後、Spring Boot の機能が利用可能になります。

**Lombok を使用する場合:**

`.devcontainer/devcontainer.json` に追加:

```json
"extensions": [
  // ... 既存の拡張機能 ...

  // Lombok サポート
  "gabrielbb.vscode-lombok"
]
```

さらに、`build.gradle` に Lombok 依存関係を追加:

```gradle
dependencies {
    compileOnly 'org.projectlombok:lombok:latest.release'
    annotationProcessor 'org.projectlombok:lombok:latest.release'
}
```

#### ポートフォワーディング

アプリケーションがポートを使用する場合、`forwardPorts` 配列にポート番号を追加します。

**例: Spring Boot の場合 (デフォルトポート 8080)**

`.devcontainer/devcontainer.json` に追加:

```json
"forwardPorts": [8080]
```

**複数ポートの場合:**

```json
"forwardPorts": [8080, 5005, 3000]
```

#### 起動時コマンド

`postCreateCommand` や `postStartCommand` を編集して、コンテナ起動時に自動実行するコマンドをカスタマイズできます。

**現在の設定:**

```json
"postCreateCommand": "bash -c 'sudo chown -R vscode:vscode /home/vscode/.gradle && source ~/.sdkman/bin/sdkman-init.sh && echo \"=== Environment Setup Verification ===\" && echo && echo \"=== Java Version ===\" && java -version && echo && echo \"=== Timezone ===\" && date && echo && echo \"=== Locale ===\" && locale'"
```

**カスタマイズ例: 依存関係を事前にダウンロード**

```json
"postCreateCommand": "bash -c 'sudo chown -R vscode:vscode /home/vscode/.gradle && source ~/.sdkman/bin/sdkman-init.sh && ./gradlew build --no-daemon'"
```

これにより、コンテナ作成時に自動的にビルドが実行され、依存関係がダウンロードされます。

## ライセンス

このプロジェクトはMITライセンスの下で提供されています。詳細については[LICENSE](LICENSE)ファイルをご覧ください。
