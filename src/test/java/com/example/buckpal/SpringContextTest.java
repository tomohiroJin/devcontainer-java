package com.example.buckpal;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Spring Boot スタック (DI / Web / DataSource / Flyway) が動作していることを確認するテスト。
 *
 * <p>写経を始める前の「土台が壊れていないか」を見るためのスモークテスト。ここが赤くなったら、 写経したコードではなく環境側 (依存関係・設定ファイル・マイグレーション) を疑う。
 *
 * <p>{@code @ActiveProfiles("test")} により {@code src/test/resources/application-test.yml} が 効き、DB
 * はインメモリ H2 になる (ファイル DB {@code ./data/buckpal} は汚さない)。
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(SpringContextTest.LombokWiredBean.class)
@DisplayName("Spring Boot スタックの動作確認")
class SpringContextTest {

  @Autowired ApplicationContext context;

  @Autowired Environment environment;

  @Autowired DataSource dataSource;

  @Autowired JdbcTemplate jdbcTemplate;

  @Autowired Flyway flyway;

  @Autowired LombokWiredBean lombokWiredBean;

  @Nested
  @DisplayName("アプリケーションコンテキスト")
  class Context {

    @Test
    @DisplayName("@SpringBootApplication が起動し、コンテキストがロードされる")
    void contextLoads() {
      assertThat(context).isNotNull();
      assertThat(context.getBean(BuckpalApplication.class)).isNotNull();
    }

    @Test
    @DisplayName("application.yml が読み込まれている (spring.application.name)")
    void loadsApplicationYml() {
      assertThat(environment.getProperty("spring.application.name")).isEqualTo("buckpal");
    }

    @Test
    @DisplayName("テストプロファイルが有効で、DB はインメモリ H2 に切り替わっている")
    void usesTestProfile() {
      assertThat(environment.getActiveProfiles()).contains("test");
      assertThat(environment.getProperty("spring.datasource.url")).startsWith("jdbc:h2:mem:");
    }

    @Test
    @DisplayName("spring-boot-starter-web の自動設定が効いている (DispatcherServlet)")
    void autoConfiguresWebLayer() {
      assertThat(context.getBeansOfType(DispatcherServlet.class)).isNotEmpty();
    }
  }

  @Nested
  @DisplayName("DataSource と Flyway")
  class Database {

    @Test
    @DisplayName("H2 に接続できる")
    void connectsToH2() throws SQLException {
      try (Connection connection = dataSource.getConnection()) {
        assertThat(connection.getMetaData().getDatabaseProductName()).isEqualTo("H2");
      }
    }

    @Test
    @DisplayName("Flyway が V1 マイグレーションを適用している")
    void appliesFlywayMigration() {
      MigrationInfo[] applied = flyway.info().applied();

      assertThat(applied).isNotEmpty();
      assertThat(applied[0].getVersion().getVersion()).isEqualTo("1");
      assertThat(applied[0].getState().isApplied()).isTrue();
      assertThat(applied[0].getState().isFailed()).isFalse();
    }

    @Test
    @DisplayName("V1 で作られた account / activity テーブルに問い合わせできる")
    void createsAccountTables() {
      assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM account", Integer.class))
          .isZero();
      assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM activity", Integer.class))
          .isZero();
    }
  }

  @Nested
  @DisplayName("Lombok と Spring の組み合わせ")
  class LombokWithSpring {

    @Test
    @DisplayName("Lombok が生成したコンストラクタで Spring が DI する")
    void injectsViaLombokGeneratedConstructor() {
      // @RequiredArgsConstructor が生成した唯一のコンストラクタを Spring が使うため、
      // @Autowired を書かなくても final フィールドに DataSource が注入される。
      // (buckpal の Service / Adapter で多用するパターン)
      assertThat(lombokWiredBean.getDataSource()).isSameAs(dataSource);
    }
  }

  /**
   * Lombok が生成したコンストラクタ経由で Spring に DI させるための検証用 Bean。
   *
   * <p>{@code @Bean} メソッドではなく {@code @Import} でクラスごと登録しているので、 Spring
   * は「クラスが持つ唯一のコンストラクタ」を探して注入する。つまり Lombok の 注釈処理が動いていなければ、この Bean は生成できない。
   */
  @Getter
  @RequiredArgsConstructor
  static class LombokWiredBean {

    private final DataSource dataSource;
  }
}
