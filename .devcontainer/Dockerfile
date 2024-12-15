# ベースイメージはお好みで
FROM ubuntu:24.10

# 非対話型で apt-get を実行するための環境変数設定
ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get install -y \
  curl \
  zip \
  unzip \
  ca-certificates \
  && rm -rf /var/lib/apt/lists/*

# SDKMAN!のインストール
# インストールスクリプトを取得して実行
RUN curl -s "https://get.sdkman.io" | bash

# SDKMAN! を有効化した上で gradle をインストール
# `source` が必要になるため、bash -cでコマンドを実行
RUN bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk install gradle"

# Gradle のパスを環境変数に追加
ENV PATH=$PATH:/root/.sdkman/candidates/gradle/current/bin

# Java のインストール
RUN apt-get update && apt-get install -y \
    curl \
    zip \
    unzip \
    ca-certificates \
    git \
    openjdk-22-jdk \
    && rm -rf /var/lib/apt/lists/*

# 作業ディレクトリ設定
WORKDIR /workspace

# コンテナ起動時に SDKMAN! を有効化するための設定
# これによりターミナルアクセス時にも gradle が利用可能
RUN echo "source $HOME/.sdkman/bin/sdkman-init.sh" >> /root/.bashrc

# ここで非対話的に Gradle プロジェクトを初期化
# 例えば Java ライブラリプロジェクトを初期化する場合:
RUN gradle init --type java-library --test-framework junit

RUN gradle build

RUN gradle test