package com.example.buckpal;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;

/**
 * ヘキサゴナル / クリーンアーキテクチャの依存方向を強制するテスト。
 *
 * <p>本書 (『手を動かしてわかるクリーンアーキテクチャ』) の中核テーマ。写経で各層にクラスを 追加していくと、依存方向を破った瞬間にこのテストが赤くなる。まだ空の層に対しても動くよう、
 * 各ルールは {@code allowEmptyShould(true)} を付けている (クラスが増えたら自動的に効き始める)。
 */
@AnalyzeClasses(
    packages = "com.example.buckpal",
    importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

  private static final String DOMAIN = "..account.domain..";
  private static final String APPLICATION = "..account.application..";
  private static final String ADAPTER = "..account.adapter..";
  private static final String ADAPTER_IN = "..account.adapter.in..";
  private static final String ADAPTER_OUT = "..account.adapter.out..";

  /** ドメインはアプリケーション層に依存しない。 */
  @ArchTest
  static final ArchRule domain_does_not_depend_on_application =
      ArchRuleDefinition.noClasses()
          .that()
          .resideInAPackage(DOMAIN)
          .should()
          .dependOnClassesThat()
          .resideInAPackage(APPLICATION)
          .allowEmptyShould(true);

  /** ドメインはアダプタ層に依存しない。 */
  @ArchTest
  static final ArchRule domain_does_not_depend_on_adapter =
      ArchRuleDefinition.noClasses()
          .that()
          .resideInAPackage(DOMAIN)
          .should()
          .dependOnClassesThat()
          .resideInAPackage(ADAPTER)
          .allowEmptyShould(true);

  /** ドメインは Spring / JPA に依存しない (純粋な Java に保つ)。 */
  @ArchTest
  static final ArchRule domain_does_not_depend_on_frameworks =
      ArchRuleDefinition.noClasses()
          .that()
          .resideInAPackage(DOMAIN)
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage("org.springframework..", "jakarta.persistence..")
          .allowEmptyShould(true);

  /** アプリケーション層はアダプタ層に依存しない (依存は port インターフェースまで)。 */
  @ArchTest
  static final ArchRule application_does_not_depend_on_adapter =
      ArchRuleDefinition.noClasses()
          .that()
          .resideInAPackage(APPLICATION)
          .should()
          .dependOnClassesThat()
          .resideInAPackage(ADAPTER)
          .allowEmptyShould(true);

  /** 入力アダプタと出力アダプタは互いに依存しない。 */
  @ArchTest
  static final ArchRule adapters_do_not_depend_on_each_other =
      ArchRuleDefinition.noClasses()
          .that()
          .resideInAPackage(ADAPTER_IN)
          .should()
          .dependOnClassesThat()
          .resideInAPackage(ADAPTER_OUT)
          .allowEmptyShould(true);
}
