# PR #8 レビューへの対応

対象: [レビューコメント](https://github.com/tomohiroJin/devcontainer-java/pull/8#issuecomment-6664600445)。外部コメントの主張は独立確認して判断しました。

| 番号 | 判断・対応 | 確認方法・理由 |
| --- | --- | --- |
| 1 | 採用。`scripts/refresh-dependencies.sh` と README にメンテナーの候補生成・独立照合・レビュー・同一PRへのコミット手順を追加 | 通常 CI は厳格な依存検証を継続。更新スクリプトはCIから呼ばず、候補生成後にオフラインbuild・差分表示。Dependabot自身のlockfile対応を前提にしない |
| 2 | 採用。Temurin の公式manifest digestをDockerfileに固定 | `docker buildx imagetools inspect eclipse-temurin:25.0.5_8-jdk-noble` で `sha256:95e350ad1eb738f943e141640f7e3d797e483359a3b33c3864255bfd1ad71f8a` を再確認。apt更新によるイメージの可変性は明記 |
| 3 | 採用。pushをmainに限定、workflow/ref単位のconcurrency追加 | actionlintによる検証。PRで実行し、ブランチへのpushとの二重実行を避ける。2回目の指摘を受けてグループにイベント種別を追加（下記） |
| 4 | 提案する緩和は不採用。未修正版を含むHIGH/CRITICALは引き続き失敗 | 無関係なPRを止める影響をREADMEに明記。ignore-unfixedや期限なし除外を自動導入しない。発生時は影響・修正版・回避策を確認してメンテナーが判断 |
| 5 | 採用。Trivyの対象をOSパッケージとGradle依存へ訂正 | JDKバイナリの監査/無脆弱性を主張しない。Temurin公式セキュリティ情報・タグ/digestを確認しCIのJava版と同期。XML/JSONから実測件数をログ・Job Summary・artifactへ出力 |
| 6 | 採用。vscodeユーザーと実際のpostCreateCommandのsmokeを追加 | rootとは別のvscodeで、read-onlyなソースから一時作業ディレクトリへWrapperをコピー。実際のキャッシュvolumeをmountしsudo/chown/Wrapperを検証。runner UID経路の全7テストも維持。Dev Containersクライアントの全処理・GUI確認とは区別 |
| 7 | 採用。自動導入から外した項目を下記とPR本文に列挙 | 拡張・ツールの任意導入化を明示し、レビュアーが変更範囲を評価できるようにした |
| 8 | 採用。Gradle 9.8.2のwrapperタスクでスクリプト/propertiesを再生成 | 追加されていたfile.encoding引数を除去。Wrapper JARの公式SHA-256は不変。READMEで更新前/更新後Gradleによる2回の再生成、公式JAR/ZIPハッシュの独立照合、CI値の同期を明記 |
| 9 | 採用。対象のないgitignoreコメントを削除 | Wrapper/JARは引き続き追跡。`git diff --check`で確認 |
| 10 | PGPの無条件有効化は見送り | 全依存の署名有無と鍵の独立信頼確認・固定が必要。現在のチェックサムを出所認証とは扱わない点と導入条件をREADMEに明記 |

## 自動導入から外した項目

- 拡張: Live Share と `liveshare.*` 設定、GitLens、Checkstyle、XML、Markdown All in One/markdownlint、Code Spell Checker、Git Graph、Error Lens、Todo Tree、YAML、REST Client。
- OSツール: build-essential、vim、nano、wget、jq、tree、htop、procps、net-tools、iputils-ping、zip、less（`unzip` は維持）。
- エディターの補助設定: editor.rulers と cSpell 辞書。

必要な利用者は任意導入できます。Live Share再導入時には旧設定の参加者承認方針も確認します。Java Pack/EditorConfigとJavaビルド・テストに必要な構成は維持しています。

## 今回の検証

- 公式ベースdigestとWrapper JAR SHA-256を独立再確認。
- 純正Wrapper生成後に、ロック・チェックサム検証付きのJava 25/Gradle 9.8.2オフラインclean buildを実行。
- vscode/sudo/実際のpostCreateCommand/Gradleキャッシュvolumeのsmokeを実行。
- `scripts/summarize-validation.py` は実際のJUnit XML/Trivy JSONから集計し、欠落した出力は「unavailable」と表示。欠落をゼロ件とは表示しない。監査の失敗状態をこの集計で変更しない。
- Bash構文、JSON/YAML/XML、actionlint、差分チェックを実施。

新しいGitHub Actions実行はpush後の確認となります。VS Code GUIとDev Containersクライアントによる完全な起動は未検証です。

検証結果: vscode経路のsmoke成功。純正Wrapperでのロック済みオフラインclean buildは7 tests / 0 failures / 0 errors。更新スクリプトを明示実行した候補生成buildと続くオフラインbuildも成功し、既存のロック/依存チェックサムに差分は発生しませんでした。集計スクリプトは実レポートでGradle依存17件/検出0、OSパッケージ143件/Critical 0・High 0・Medium 76・Low 20を出力しました。欠落出力のunavailable表示も確認しました。

## 2 回目のレビュー（コミット 35da542f）への対応

対象: [再検証コメント](https://github.com/tomohiroJin/devcontainer-java/pull/8#issuecomment-6673020544)。

| 番号 | 判断・対応 | 確認方法・理由 |
| --- | --- | --- |
| 1 | 採用。concurrency の `group` に `github.event_name` を追加し、`cancel-in-progress` は `pull_request` のときのみ true | main push・定期・手動実行が互いを取り消さない。PR の二重実行抑止は維持 |
| 2 | 採用。削除した OS ツールの一覧に `zip` と `less` を追記 | 旧 Dockerfile の `apt-get install` 行と照合 |
| 3 | 採用。Job Summary への追記を ` ```text ` のコードブロックで囲む | 4 行が 1 段落に連結されない |
| 4 | PR 本文の古い記述（push/PR 双方の CI）と重複した「レビュー対応」段落は、本ブランチを取り込む際に PR 本文を更新する | ワークフローの変更内容と整合させる |
| 5 | 参考として README に明記。意図的なダウングレードは `scripts/refresh-dependencies.sh` を通してロックに反映する | ロックは厳格制約のため、`build.gradle` だけ下げても解決結果は変わらない |
| 6 | 採用。`scripts/refresh-dependencies.sh` に実行ビット（100755）を付与 | README の `bash scripts/...` 呼び出しも引き続き有効 |

## 2026-11-19 の再調査で追加した更新

- Gradle 9.8.2 → 9.9.1（Wrapper を 2 段階再生成、CI の JAR 検証値と `verify-env.sh` を同期）。
- Commons Lang 3.21.0 → 3.22.0（ロック・検証メタデータを再生成し、古い項目を削除。チェックサムは Maven Central から直接取得した artifact と独立照合）。
- Temurin タグ再ビルドに伴う Dockerfile の digest 更新（`sha256:8599ea2d…`、JDK は 25.0.5+8 のまま）。
- actions/setup-java v6.3.0、aquasecurity/setup-trivy v0.3.1 へのピン更新。
- 残存検出項目の再監査と `docs/security-findings.tsv` の更新（MEDIUM 87 / LOW 37、修正版あり 0）。次回確認期限 2026-12-03。

詳細は [検証記録](maintenance-validation.md) を参照してください。
