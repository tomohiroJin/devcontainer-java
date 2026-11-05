FROM eclipse-temurin:25.0.5_8-jdk-noble

ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update && apt-get upgrade -y && apt-get install -y --no-install-recommends \
    git ca-certificates curl unzip locales tzdata sudo \
    && sed -i '/ja_JP.UTF-8/s/^# //g' /etc/locale.gen \
    && locale-gen ja_JP.UTF-8 \
    && rm -rf /var/lib/apt/lists/*
ENV LANG=ja_JP.UTF-8 LANGUAGE=ja_JP:ja LC_ALL=ja_JP.UTF-8 TZ=Asia/Tokyo
RUN ln -snf /usr/share/zoneinfo/Asia/Tokyo /etc/localtime \
    && echo Asia/Tokyo > /etc/timezone
ARG USER_UID=1000
ARG USER_GID=1000
RUN if id ubuntu >/dev/null 2>&1; then \
      usermod -l vscode -d /home/vscode -m ubuntu && groupmod -n vscode ubuntu; \
    else groupadd --gid "$USER_GID" vscode && useradd --uid "$USER_UID" --gid "$USER_GID" -m -s /bin/bash vscode; fi \
    && echo 'vscode ALL=(root) NOPASSWD:ALL' > /etc/sudoers.d/vscode \
    && chmod 0440 /etc/sudoers.d/vscode
ENV DEBIAN_FRONTEND=
USER vscode
WORKDIR /workspaces
