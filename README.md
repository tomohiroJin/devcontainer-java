# devcontainer-java

このリポジトリは、Visual Studio CodeのDev Containers機能を活用してJavaアプリケーション開発環境を簡単に構築するためのテンプレートです。開発環境のセットアップを自動化し、効率的な開発を支援します。

## 特徴

- **Java 開発キット (JDK)**: 最新のOpenJDKを使用したJava開発環境を提供します。
- **ビルドツール**: Gradleが事前にインストールされており、プロジェクトのビルドや依存関係管理が簡単に行えます。
- **VS Code 拡張機能**: Java開発に必要な拡張機能（コード補完やデバッグツール）が事前に設定されています。

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

`.devcontainer/devcontainer.json` ファイルを編集することで、開発環境をカスタマイズできます。  
例えば、Javaのバージョンを変更したり、追加のツールをインストールする設定を追加することが可能です。

## ライセンス

このプロジェクトはMITライセンスの下で提供されています。詳細については[LICENSE](LICENSE)ファイルをご覧ください。
