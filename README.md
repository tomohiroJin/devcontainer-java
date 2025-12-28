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

## ライセンス

このプロジェクトはMITライセンスの下で提供されています。詳細については[LICENSE](LICENSE)ファイルをご覧ください。
