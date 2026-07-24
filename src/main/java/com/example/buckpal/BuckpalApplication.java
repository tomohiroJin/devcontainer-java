package com.example.buckpal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * アプリケーションのエントリポイント。
 *
 * <p>『手を動かしてわかるクリーンアーキテクチャ』(Tom Hombergs) の buckpal を題材にした ヘキサゴナル /
 * クリーンアーキテクチャ写経用のスケルトンです。各層のパッケージは {@code package-info.java} で責務を明示してあり、依存方向は {@code
 * ArchitectureTest} が 強制します。
 */
@SpringBootApplication
public class BuckpalApplication {

  public static void main(String[] args) {
    SpringApplication.run(BuckpalApplication.class, args);
  }
}
