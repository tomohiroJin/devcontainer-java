# Java 開発用 DevContainer

コンテナで即座に Java 開発環境をセットアップ。最小限の設定で素早く開発開始。

## クイックスタート

### 1. コンテナで開く

VS Code で DevContainer を起動:
- 左下の `><` アイコン → `Reopen in Container`
- または Command Palette (`Cmd+Shift+P`) → `Dev Containers: Reopen in Container`

### 2. 環境確認

```bash
java -version    # Java 25 (LTS)
gradle -v        # Gradle 9.4.1
locale           # ja_JP.UTF-8
date             # Asia/Tokyo
```

### 3. ビルド・テスト

```bash
gradle build  # ビルド
gradle test   # テスト実行
```

### 4. サンプルコード確認

```bash
# ソースコード
cat src/main/java/HelloWorld.java

# テストコード
cat src/test/java/HelloWorldTest.java
```

## 環境仕様

### 基本設定

| 項目 | 値 |
|------|-----|
| ベースイメージ | Ubuntu 24.04 LTS |
| Java | OpenJDK 25 (LTS) |
| Gradle | 9.4.1 |
| パッケージマネージャー | SDKMAN! |
| ユーザー | vscode (非root) |
| ロケール | ja_JP.UTF-8 |
| タイムゾーン | Asia/Tokyo |

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
  sdk install gradle && \
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
gradle wrapper --gradle-version 9.4.1

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
