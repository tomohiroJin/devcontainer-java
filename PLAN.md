# Java 25 DevContainer 改善プラン

## 概要

現在の devcontainer 設定を改善し、最新の Java 25 で Gradle が動作する完全な開発環境を構築します。

このドキュメントは段階的な実装ガイドとなっており、各ステップごとにコミット可能な形式になっています。

## 要件

- Java 25 (最新版) を使用
- Gradle でビルド・テストが動作
- 開発ツール (vim, less, wget等) を追加
- タイムゾーンを Asia/Tokyo に設定
- 日本語ロケール (ja_JP.UTF-8) を設定
- セキュリティベストプラクティスに従い非rootユーザーで実行
- VS Code 拡張機能が正常に動作

## 進捗管理

各ステップの実装状況:

- [ ] Step 1: 開発ツールの追加
- [ ] Step 2: タイムゾーン設定の追加
- [ ] Step 3: 日本語ロケール設定の追加
- [ ] Step 4: 非rootユーザーの作成
- [ ] Step 5: SDKMAN! の非rootユーザー化
- [ ] Step 6: devcontainer.json の更新
- [ ] Step 7: 動作確認とテスト
- [ ] Step 8: README.md の更新
- [ ] Step 9: .vscode/settings.json の更新 (オプション)

## 変更対象ファイル

### 主要ファイル
1. `.devcontainer/Dockerfile` - コンテナイメージの定義を全面的に改善
2. `.devcontainer/devcontainer.json` - VS Code設定を更新

### 補助ファイル (オプション)
3. `README.md` - 環境詳細のドキュメント追加
4. `.vscode/settings.json` - Java ランタイム設定の追加

## 段階的実装ガイド

---

### Step 1: 開発ツールの追加

**目的**: 開発に必要な基本ツールをコンテナに追加

**変更ファイル**: `.devcontainer/Dockerfile`

**変更内容**:
現在の apt-get install セクション (8-14行目) を拡張:

```dockerfile
RUN apt-get update && apt-get install -y \
  # Version control
  git \
  # SSL/Certificates
  ca-certificates \
  # Compression
  zip \
  unzip \
  # Network tools
  curl \
  wget \
  net-tools \
  iputils-ping \
  # Text editors
  vim \
  nano \
  # Utilities
  less \
  jq \
  tree \
  htop \
  procps \
  # Build tools
  build-essential \
  # Locale and timezone (for later steps)
  locales \
  tzdata \
  # User management (for later steps)
  sudo \
  && rm -rf /var/lib/apt/lists/*
```

**検証方法**:
```bash
docker build -t devcontainer-java-test .devcontainer/
docker run -it --rm devcontainer-java-test which vim less wget jq tree htop
```

**コミットメッセージ**:
```
feat(devcontainer): add development tools to Dockerfile

- Add text editors: vim, nano
- Add utilities: less, wget, jq, tree, htop, procps
- Add network tools: net-tools, iputils-ping
- Add build-essential for native compilation
- Add locales, tzdata, sudo for future configuration

This provides a complete set of development tools commonly
used in container-based development environments.
```

---

### Step 2: タイムゾーン設定の追加

**目的**: コンテナのタイムゾーンを Asia/Tokyo (JST) に設定

**変更ファイル**: `.devcontainer/Dockerfile`

**変更内容**:
DEBIAN_FRONTEND の設定後、パッケージインストールの前に以下を追加:

```dockerfile
# ========================================
# Timezone configuration
# ========================================
ENV TZ=Asia/Tokyo
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime \
    && echo $TZ > /etc/timezone
```

そして、パッケージインストール後に以下を追加:

```dockerfile
# ========================================
# Finalize timezone setup
# ========================================
RUN dpkg-reconfigure -f noninteractive tzdata
```

**検証方法**:
```bash
docker build -t devcontainer-java-test .devcontainer/
docker run -it --rm devcontainer-java-test bash -c "date && cat /etc/timezone"
```
→ JST タイムゾーンと Asia/Tokyo が表示されることを確認

**コミットメッセージ**:
```
feat(devcontainer): configure timezone to Asia/Tokyo

- Set TZ environment variable to Asia/Tokyo
- Configure /etc/localtime and /etc/timezone
- Run dpkg-reconfigure to finalize timezone setup

This ensures all timestamps in logs and output use JST timezone.
```

---

### Step 3: 日本語ロケール設定の追加

**目的**: 日本語 (ja_JP.UTF-8) ロケールを設定し、日本語文字列を正しく扱えるようにする

**変更ファイル**: `.devcontainer/Dockerfile`

**変更内容**:
タイムゾーン設定の後に以下を追加:

```dockerfile
# ========================================
# Locale Configuration
# ========================================
RUN sed -i '/ja_JP.UTF-8/s/^# //g' /etc/locale.gen \
    && locale-gen ja_JP.UTF-8 \
    && update-locale LANG=ja_JP.UTF-8

ENV LANG=ja_JP.UTF-8
ENV LANGUAGE=ja_JP:ja
ENV LC_ALL=ja_JP.UTF-8
```

**検証方法**:
```bash
docker build -t devcontainer-java-test .devcontainer/
docker run -it --rm devcontainer-java-test bash -c "locale && echo 'テスト' | cat"
```
→ ja_JP.UTF-8 が表示され、日本語文字列が正しく表示されることを確認

**コミットメッセージ**:
```
feat(devcontainer): configure Japanese locale (ja_JP.UTF-8)

- Enable ja_JP.UTF-8 in locale.gen
- Generate Japanese locale
- Set LANG, LANGUAGE, LC_ALL environment variables

This allows proper handling of Japanese characters in filenames,
log messages, and application output.
```

---

### Step 4: 非rootユーザーの作成

**目的**: セキュリティベストプラクティスに従い、非rootユーザー (vscode) を作成

**変更ファイル**: `.devcontainer/Dockerfile`

**変更内容**:
ロケール設定の後、SDKMAN! インストールの前に以下を追加:

```dockerfile
# ========================================
# Create Non-Root User
# ========================================
ARG USERNAME=vscode
ARG USER_UID=1000
ARG USER_GID=$USER_UID

RUN groupadd --gid $USER_GID $USERNAME \
    && useradd --uid $USER_UID --gid $USER_GID -m -s /bin/bash $USERNAME \
    && echo $USERNAME ALL=\(root\) NOPASSWD:ALL > /etc/sudoers.d/$USERNAME \
    && chmod 0440 /etc/sudoers.d/$USERNAME
```

**検証方法**:
```bash
docker build -t devcontainer-java-test .devcontainer/
docker run -it --rm devcontainer-java-test bash -c "id vscode && cat /etc/sudoers.d/vscode"
```
→ vscode ユーザー (UID 1000) が存在し、sudo 権限があることを確認

**コミットメッセージ**:
```
feat(devcontainer): create non-root user 'vscode'

- Create vscode user with UID 1000 and GID 1000
- Grant passwordless sudo access
- Set bash as default shell

This follows security best practices by avoiding root user
execution in the container.
```

---

### Step 5: SDKMAN! の非rootユーザー化

**目的**: SDKMAN! を非rootユーザーでインストールし、環境変数のパスを更新

**変更ファイル**: `.devcontainer/Dockerfile`

**変更内容**:

1. 非rootユーザー作成の後に、USER ディレクティブを追加:

```dockerfile
# ========================================
# Switch to Non-Root User
# ========================================
USER vscode
WORKDIR /home/vscode
```

2. SDKMAN! インストールセクション (現在の16-17行目) はそのまま (既に非root実行になる)

3. SDKMAN! による Java/Gradle インストール (現在の19-22行目) もそのまま

4. 環境変数セクション (現在の25-27行目) を更新:

```dockerfile
# ========================================
# Environment Variables
# ========================================
ENV JAVA_HOME=/home/vscode/.sdkman/candidates/java/current
ENV GRADLE_HOME=/home/vscode/.sdkman/candidates/gradle/current
ENV PATH=$PATH:$JAVA_HOME/bin:$GRADLE_HOME/bin
```

5. bashrc 設定 (現在の30行目) を更新:

```dockerfile
# ========================================
# Shell Configuration
# ========================================
RUN echo "source /home/vscode/.sdkman/bin/sdkman-init.sh" >> /home/vscode/.bashrc
```

6. 作業ディレクトリ設定 (現在の32行目) とクリーンアップを更新:

```dockerfile
# ========================================
# Working Directory
# ========================================
WORKDIR /workspaces

# ========================================
# Cleanup
# ========================================
ENV DEBIAN_FRONTEND=
```

**検証方法**:
```bash
docker build -t devcontainer-java-test .devcontainer/
docker run -it --rm devcontainer-java-test bash -c "whoami && java -version && gradle --version"
```
→ vscode ユーザーとして実行され、Java 25 と Gradle が正しく動作することを確認

**コミットメッセージ**:
```
refactor(devcontainer): run SDKMAN! as non-root user

- Switch to vscode user before SDKMAN! installation
- Update all paths from /root to /home/vscode
- Update JAVA_HOME and GRADLE_HOME environment variables
- Update bashrc path
- Set working directory to /workspaces
- Clean up DEBIAN_FRONTEND

This ensures all Java and Gradle operations run with proper
permissions and follows container security best practices.
```

---

### Step 6: devcontainer.json の更新

**目的**: VS Code の devcontainer 設定を非rootユーザーと新しい機能に対応

**変更ファイル**: `.devcontainer/devcontainer.json`

**変更内容**:

```json
{
  "name": "Gradle with Java DevContainer",
  "build": {
    "dockerfile": "Dockerfile"
  },
  "remoteUser": "vscode",
  "containerEnv": {
    "JAVA_HOME": "/home/vscode/.sdkman/candidates/java/current",
    "GRADLE_HOME": "/home/vscode/.sdkman/candidates/gradle/current",
    "LANG": "ja_JP.UTF-8",
    "TZ": "Asia/Tokyo"
  },
  "customizations": {
    "vscode": {
      "extensions": [
        // Java開発の必須拡張機能
        "redhat.java",
        "vscjava.vscode-java-debug",
        "vscjava.vscode-java-test",
        "vscjava.vscode-java-dependency",
        "vscjava.vscode-maven",
        "vscjava.vscode-gradle",
        "Oracle.oracle-java",

        // Lombokサポート
        "gabrielbb.vscode-lombok",

        // Spring Boot開発(必要に応じて)
        "vmware.vscode-spring-boot",
        "vscjava.vscode-spring-boot-dashboard",

        // コード品質・フォーマット
        "sonarsource.sonarlint-vscode",
        "shengchen.vscode-checkstyle",

        // Git・バージョン管理
        "mhutchie.git-graph",

        // 汎用ツール
        "wmaurer.change-case",
        "streetsidesoftware.code-spell-checker",
        "ms-vsliveshare.vsliveshare",

        // Markdown
        "yzhang.markdown-all-in-one",
        "DavidAnson.vscode-markdownlint",
        "shd101wyy.markdown-preview-enhanced",

        // その他
        "GrapeCity.gc-excelviewer"
      ]
    }
  },
  "mounts": [
    "source=devcontainer-java-gradle-cache,target=/home/vscode/.gradle,type=volume"
  ],
  "forwardPorts": [],
  "postCreateCommand": "bash -c 'source ~/.sdkman/bin/sdkman-init.sh && echo \"=== Environment Setup Verification ===\" && echo && echo \"=== Java Version ===\" && java -version && echo && echo \"=== Gradle Version ===\" && gradle --version && echo && echo \"=== Timezone ===\" && date && echo && echo \"=== Locale ===\" && locale && echo && echo \"=== SDKMAN Version ===\" && sdk version'"
}
```

**主な変更点**:
1. `remoteUser: "vscode"` を追加
2. `containerEnv` で環境変数を明示的に設定
3. `mounts` で Gradle キャッシュをボリュームマウント
4. `postCreateCommand` を強化して包括的な環境確認を実施

**コミットメッセージ**:
```
feat(devcontainer): update devcontainer.json for non-root user

- Set remoteUser to 'vscode'
- Add containerEnv with JAVA_HOME, GRADLE_HOME, LANG, TZ
- Add Gradle cache volume mount for improved build performance
- Enhance postCreateCommand with comprehensive environment verification

This ensures VS Code connects as the non-root user and provides
detailed environment information on container creation.
```

---

### Step 7: 動作確認とテスト

**目的**: すべての変更が正しく動作することを確認

**検証手順**:

#### 7.1 Docker イメージのビルドテスト
```bash
cd /Users/tomohiro/work/private/github/devcontainer-java
docker build -t devcontainer-java-test .devcontainer/
```
→ エラーなくビルドが完了することを確認

#### 7.2 コンテナの起動とユーザー確認
```bash
docker run -it --rm devcontainer-java-test bash -c "whoami && id"
```
→ vscode ユーザー (UID 1000) として実行されることを確認

#### 7.3 Java と Gradle の確認
```bash
docker run -it --rm devcontainer-java-test bash -c "source ~/.sdkman/bin/sdkman-init.sh && java -version && gradle --version"
```
→ Java 25 と Gradle 8.11.1+ が表示されることを確認

#### 7.4 タイムゾーンとロケールの確認
```bash
docker run -it --rm devcontainer-java-test bash -c "date && locale"
```
→ JST タイムゾーンと ja_JP.UTF-8 が表示されることを確認

#### 7.5 開発ツールの確認
```bash
docker run -it --rm devcontainer-java-test bash -c "which vim && which less && which wget && which jq && which tree"
```
→ すべてのツールが利用可能であることを確認

#### 7.6 VS Code での統合テスト
1. VS Code でプロジェクトを開く
2. Command Palette → "Dev Containers: Rebuild and Reopen in Container"
3. コンテナが起動したら、統合ターミナルで以下を確認:
   ```bash
   whoami              # vscode と表示される
   java -version       # Java 25 が表示される
   gradle --version    # Gradle 8.11.1+ が表示される
   date                # JST タイムゾーンが表示される
   locale              # ja_JP.UTF-8 が表示される
   ```

#### 7.7 Gradle ビルドとテストの実行
```bash
./gradlew clean build
./gradlew test
```
→ ビルドとテストが成功することを確認

#### 7.8 VS Code 拡張機能の確認
1. `src/main/java/HelloWorld.java` を開く
2. IntelliSense (コード補完) が動作することを確認
3. `src/test/java/HelloWorldTest.java` を開く
4. テストアイコン (▶︎) が表示され、クリックしてテストが実行できることを確認
5. Gradle サイドバーでタスクが表示されることを確認

**コミットメッセージ** (検証完了後):
```
test(devcontainer): verify all improvements work correctly

- Verified Docker build succeeds
- Verified container runs as vscode user
- Verified Java 25 and Gradle work correctly
- Verified timezone is Asia/Tokyo
- Verified locale is ja_JP.UTF-8
- Verified all development tools are available
- Verified Gradle builds and tests pass
- Verified VS Code extensions function properly

All improvements have been tested and are working as expected.
```

---

### Step 8: README.md の更新

**目的**: 環境の詳細情報と初回セットアップ手順をドキュメント化

**変更ファイル**: `README.md`

**変更内容**:

「特徴」セクションの後に「開発環境の詳細」セクションを追加:

```markdown
## 開発環境の詳細

この devcontainer は以下の環境を提供します:

- **OS**: Ubuntu 24.04 LTS
- **Java**: OpenJDK 25 (SDKMAN! 経由)
- **Gradle**: 最新版 (SDKMAN! 経由、8.11.1以降)
- **実行ユーザー**: 非rootユーザー `vscode` (UID 1000)
- **タイムゾーン**: Asia/Tokyo (JST)
- **ロケール**: ja_JP.UTF-8 (日本語 UTF-8)
- **開発ツール**: vim, nano, less, wget, curl, jq, tree, htop など

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
```

「カスタマイズ」セクションを更新:

```markdown
## カスタマイズ

`.devcontainer/` ディレクトリ内のファイルを編集することで、開発環境をカスタマイズできます。

### Dockerfile のカスタマイズ

- **Java バージョンの変更**: `sdk install java <version>` の部分を変更
- **追加パッケージのインストール**: `apt-get install` セクションにパッケージを追加
- **タイムゾーンの変更**: `ENV TZ=` の値を変更 (例: `Asia/Seoul`, `America/New_York`)
- **ロケールの変更**: locale.gen と環境変数 (LANG, LC_ALL) を変更

### devcontainer.json のカスタマイズ

- **VS Code 拡張機能の追加/削除**: `extensions` 配列を編集
- **ポートフォワーディング**: `forwardPorts` 配列にポート番号を追加
- **起動時コマンド**: `postCreateCommand` や `postStartCommand` を編集
```

**コミットメッセージ**:
```
docs(readme): add detailed environment info and setup guide

- Add "Development Environment Details" section with OS, Java,
  Gradle, user, timezone, locale, and tools information
- Add "Initial Setup" section with Git config, verification,
  build, and test instructions
- Update "Customization" section with Dockerfile and
  devcontainer.json customization examples

This provides users with clear information about the environment
and how to verify and customize it.
```

---

### Step 9: .vscode/settings.json の更新 (オプション)

**目的**: Java ランタイム設定とスペルチェック辞書を追加

**変更ファイル**: `.vscode/settings.json`

**変更内容**:

既存の設定を保持しつつ、以下を追加:

```json
{
  "cSpell.words": [
    "ctxt",
    "devcontainer",
    "gradlew",
    "noninteractive",
    "SDKMAN",
    "vscode"
  ],
  "java.configuration.runtimes": [
    {
      "name": "JavaSE-25",
      "path": "/home/vscode/.sdkman/candidates/java/current"
    }
  ],
  "java.home": "/home/vscode/.sdkman/candidates/java/current"
}
```

**コミットメッセージ**:
```
feat(vscode): add Java runtime configuration

- Add Java 25 runtime configuration
- Add java.home setting
- Update spell checker dictionary with devcontainer-related terms

This improves VS Code Java extension integration and reduces
false positives in spell checking.
```

---

## 参考情報

### 現在の Dockerfile の構造
```dockerfile
# ベースイメージ: Ubuntu 24.04
# 環境変数: DEBIAN_FRONTEND=noninteractive
# パッケージ: curl, zip, unzip, ca-certificates, git
# SDKMAN! インストール (root ユーザーとして)
# Java 25 + Gradle インストール (root/.sdkman)
# 環境変数: JAVA_HOME, GRADLE_HOME, PATH
# bashrc に SDKMAN! 初期化を追加
# WORKDIR: /workspace
```

### 現在の devcontainer.json の構造
```json
{
  "name": "Gradle with Java DevContainer",
  "build": { "dockerfile": "Dockerfile" },
  "customizations": { "vscode": { "extensions": [...] } },
  "forwardPorts": [],
  "postCreateCommand": "gradle --version"
}
```

---

## 想定されるリスクと対策

### リスク 1: パス変更による SDKMAN! の問題
- **リスクレベル**: 中
- **対策**: containerEnv で JAVA_HOME と GRADLE_HOME を明示的に設定

### リスク 2: VS Code 拡張機能の権限問題
- **リスクレベル**: 低
- **対策**: vscode ユーザーは適切な権限を持ち、sudo も使用可能

### リスク 3: Gradle キャッシュの権限問題
- **リスクレベル**: 低
- **対策**: ボリュームマウントで永続化し、適切な所有権を確保

---

## 最終的な成功基準

1. コンテナが正常にビルド・起動する
2. Java 25 と Gradle が正しく動作する
3. タイムゾーンが Asia/Tokyo に設定されている
4. 日本語ロケールが ja_JP.UTF-8 に設定されている
5. すべての開発ツールが使用可能
6. Gradle ビルド・テストが成功する
7. すべての VS Code 拡張機能が正常に動作する
8. コード補完、デバッグ、テスト実行が VS Code で動作する

---

## ロールバック計画

問題が発生した場合:

1. **Git で変更を破棄**:
   ```bash
   git checkout -- .devcontainer/
   git checkout -- .vscode/settings.json
   git checkout -- README.md
   ```

2. **VS Code でコンテナを再ビルド**:
   Command Palette → "Dev Containers: Rebuild Container"

3. **特定のコミットに戻る** (必要に応じて):
   ```bash
   git log --oneline -- .devcontainer/
   git checkout <commit-hash> -- .devcontainer/
   ```

---

## 実装の進め方

1. Step 1から順番に実装
2. 各ステップ完了後にコミット
3. Step 7 で包括的な動作確認を実施
4. 問題があれば該当ステップに戻って修正
5. すべてのステップ完了後、最終確認
