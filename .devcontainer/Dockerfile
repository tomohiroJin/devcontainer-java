# ベースイメージ
FROM ubuntu:24.04

# 非対話型で apt-get を実行するための環境変数設定
ENV DEBIAN_FRONTEND=noninteractive

# ========================================
# Timezone configuration
# ========================================
ENV TZ=Asia/Tokyo
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime \
    && echo $TZ > /etc/timezone

# 必要なパッケージのインストール
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

# ========================================
# Finalize timezone setup
# ========================================
RUN dpkg-reconfigure -f noninteractive tzdata

# ========================================
# Locale Configuration
# ========================================
RUN sed -i '/ja_JP.UTF-8/s/^# //g' /etc/locale.gen \
    && locale-gen ja_JP.UTF-8 \
    && update-locale LANG=ja_JP.UTF-8

ENV LANG=ja_JP.UTF-8
ENV LANGUAGE=ja_JP:ja
ENV LC_ALL=ja_JP.UTF-8

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

# SDKMAN!のインストール
RUN curl -s "https://get.sdkman.io" | bash

# SDKMAN! を使用して Java 25 LTS と Gradle 最新版をインストール
RUN bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && \
  sdk install java 25-open && \
  sdk install gradle && \
  sdk default java 25-open"

# 環境変数の設定
ENV JAVA_HOME=/root/.sdkman/candidates/java/current
ENV GRADLE_HOME=/root/.sdkman/candidates/gradle/current
ENV PATH=$PATH:$JAVA_HOME/bin:$GRADLE_HOME/bin

# 作業ディレクトリ設定
WORKDIR /workspace

# コンテナ起動時に SDKMAN! を有効化するための設定
RUN echo "source $HOME/.sdkman/bin/sdkman-init.sh" >> /root/.bashrc